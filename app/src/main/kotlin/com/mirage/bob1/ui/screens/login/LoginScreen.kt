package com.mirage.bob1.ui.screens.login

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
import com.mirage.bob1.ui.components.Bob1Logo
import com.mirage.bob1.ui.core.navigation.rememberUiEventHandler
import dev.kindling.compose.KScreen
import dev.kindling.core.components.ui.KInput
import dev.kindling.core.components.ui.button.KButton
import dev.kindling.core.components.ui.button.KButtonVariant

@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel = hiltViewModel()) {
    val handle = rememberUiEventHandler()
    KScreen(viewModel = viewModel, navController = navController, onEvent = { _, _, event -> handle(event) }) { state, vm ->
        LoginContent(
            state = state,
            actions = LoginActions(
                onEmail = vm::onEmail,
                onPassword = vm::onPassword,
                openRegister = vm::openRegister,
                submit = vm::submit,
            ),
        )
    }
}

@Composable
fun LoginContent(state: LoginState, actions: LoginActions) {
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
            value = state.email, onValueChange = actions.onEmail, placeholder = "Adresse e-mail",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
        )
        KInput(
            value = state.password, onValueChange = actions.onPassword, placeholder = "Mot de passe", isPassword = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { actions.submit() }),
        )
        KButton(text = "Se connecter", onClick = actions.submit, modifier = Modifier.fillMaxWidth(), enabled = state.canSubmit, isLoading = state.loading)
        KButton(text = "Créer un compte", onClick = actions.openRegister, modifier = Modifier.fillMaxWidth(), variant = KButtonVariant.Ghost)
    }
}
