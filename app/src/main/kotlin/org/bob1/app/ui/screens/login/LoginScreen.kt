package org.bob1.app.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.kindling.compose.KScreen
import dev.kindling.core.components.ui.KInput
import dev.kindling.core.components.ui.button.KButton
import dev.kindling.core.components.ui.button.KButtonVariant
import org.bob1.app.ui.core.components.Bob1Logo
import org.bob1.app.ui.core.navigation.rememberUiEventHandler

@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel = hiltViewModel()) {
    val handle = rememberUiEventHandler()
    KScreen(viewModel = viewModel, navController = navController, onEvent = { _, _, event -> handle(event) }) { state, vm ->
        Column(
            Modifier.fillMaxSize().systemBarsPadding().imePadding().verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Bob1Logo(72.dp)
            Text("BOB1", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
            Text("Le club, ensemble", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))
            KInput(
                value = state.email, onValueChange = vm::onEmail, placeholder = "Adresse e-mail",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            )
            KInput(
                value = state.password, onValueChange = vm::onPassword, placeholder = "Mot de passe", isPassword = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { vm.submit() }),
            )
            KButton(text = "Se connecter", onClick = vm::submit, modifier = Modifier.fillMaxWidth(), enabled = state.canSubmit, isLoading = state.loading)
            KButton(text = "Créer un compte", onClick = vm::openRegister, modifier = Modifier.fillMaxWidth(), variant = KButtonVariant.Ghost)
        }
    }
}
