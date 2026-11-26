package com.mirage.bob1.ui.screens.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mirage.bob1.ui.components.Bob1TopBar
import com.mirage.bob1.ui.components.PasswordChecklist
import com.mirage.bob1.ui.core.navigation.rememberUiEventHandler
import dev.kindling.compose.KScreen
import dev.kindling.core.components.ui.KInput
import dev.kindling.core.components.ui.button.KButton
import dev.kindling.core.components.ui.button.KButtonVariant

@Composable
fun RegisterScreen(navController: NavController, viewModel: RegisterViewModel = hiltViewModel()) {
    val handle = rememberUiEventHandler()
    KScreen(
        viewModel = viewModel, navController = navController,
        onEvent = { _, _, e -> handle(e) },
    ) { state, vm ->
        RegisterContent(
            state = state,
            actions = RegisterActions(
                onCode = vm::onCode,
                onEmail = vm::onEmail,
                onFamilyName = vm::onFamilyName,
                onFirstName = vm::onFirstName,
                onLastName = vm::onLastName,
                onPassword = vm::onPassword,
                setParent = vm::setParent,
                setFamilyMode = vm::setFamilyMode,
                submit = vm::submit,
                back = { navController.popBackStack() },
            ),
        )
    }
}

@Composable
fun RegisterContent(state: RegisterState, actions: RegisterActions) {
    Column(Modifier.fillMaxSize().imePadding()) {
        Bob1TopBar(title = "Créer un compte", onBack = { actions.back() })
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).navigationBarsPadding().padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KInput(state.firstName, actions.onFirstName, Modifier.weight(1f), placeholder = "Prénom",
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next))
                KInput(state.lastName, actions.onLastName, Modifier.weight(1f), placeholder = "Nom",
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next))
            }
            KInput(state.email, actions.onEmail, placeholder = "Adresse e-mail",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next))
            KInput(state.password, actions.onPassword, placeholder = "Mot de passe (12 caractères min.)", isPassword = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next))
            PasswordChecklist(state.passwordRules)

            // The whole row toggles, so a tap on the text works as well as on the switch.
            Row(
                Modifier.fillMaxWidth().toggleable(value = state.isParent, role = Role.Switch, onValueChange = actions.setParent).padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Je suis parent", fontWeight = FontWeight.Bold)
                    Text(
                        if (state.isParent) "Présences, covoiturages, buvette : tout se fait au nom de votre famille."
                        else "Vous vous inscrivez comme membre du staff (arbitre, table de marque…). Vous pourrez rejoindre une famille avec un code.",
                        fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(checked = state.isParent, onCheckedChange = null)
            }

            Text("Votre famille", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
            state.familyModes.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    row.forEach { mode ->
                        KButton(familyModeLabel(mode), { actions.setFamilyMode(mode) }, Modifier.weight(1f),
                            variant = if (state.familyMode == mode) KButtonVariant.Default else KButtonVariant.Outline)
                    }
                }
            }
            when (state.familyMode) {
                FamilyMode.Join -> {
                    KInput(state.code, actions.onCode, placeholder = "Code d'invitation (8 caractères)", isError = state.code.isNotBlank() && !state.codeValid,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { actions.submit() }))
                    Text("Demandez le code à un parent de la famille : Profil → Inviter un parent. Il est valable 7 jours.",
                        fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                FamilyMode.Create -> {
                    KInput(state.familyName, actions.onFamilyName, placeholder = "Nom de la famille (facultatif)",
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { actions.submit() }))
                    Text("Sans nom, on utilise votre nom de famille.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                FamilyMode.Skip ->
                    Text("Vous pourrez créer ou rejoindre une famille plus tard, depuis votre profil.",
                        fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            state.hint?.let { Text(it, fontSize = 13.sp, color = MaterialTheme.colorScheme.error) }
            KButton("Créer mon compte", actions.submit, Modifier.fillMaxWidth().padding(top = 4.dp), enabled = state.canSubmit, isLoading = state.loading)
        }
    }
}

private fun familyModeLabel(mode: FamilyMode) = when (mode) {
    FamilyMode.Create -> "Nouvelle famille"
    FamilyMode.Join -> "J'ai un code"
    FamilyMode.Skip -> "Passer"
}
