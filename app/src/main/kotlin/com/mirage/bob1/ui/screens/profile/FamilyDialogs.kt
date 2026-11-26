package com.mirage.bob1.ui.screens.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mirage.bob1.ui.components.ConfirmDialog
import com.mirage.bob1.ui.screens.register.INVITATION_CODE_LENGTH
import dev.kindling.core.components.ui.KInput
import dev.kindling.core.components.ui.button.KButton
import dev.kindling.core.components.ui.button.KButtonVariant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Create / join / leave a family, and the dialog that shows a freshly created invitation code. */
@Composable
fun FamilyDialogs(state: ProfileState, actions: ProfileActions) {
    when (state.familyDialog) {
        FamilyDialog.Create -> InputDialog(
            title = "Créer ma famille", confirmText = "Créer", busy = state.familyBusy,
            onConfirm = actions.confirmFamilyDialog, onDismiss = actions.closeFamilyDialog,
        ) {
            KInput(state.familyName, actions.onFamilyName, placeholder = "Nom de la famille (facultatif)",
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { actions.confirmFamilyDialog() }))
            Hint("Sans nom, on utilise votre nom de famille. Vous pourrez ensuite inviter un autre parent avec un code.")
        }

        FamilyDialog.Join -> InputDialog(
            title = "Rejoindre une famille", confirmText = "Rejoindre", busy = state.familyBusy, enabled = state.joinCodeValid,
            onConfirm = actions.confirmFamilyDialog, onDismiss = actions.closeFamilyDialog,
        ) {
            KInput(state.joinCode, actions.onJoinCode, placeholder = "Code d'invitation ($INVITATION_CODE_LENGTH caractères)",
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { actions.confirmFamilyDialog() }))
            Hint("Demandez le code à un parent de la famille : Profil → Inviter un parent. Il est valable 7 jours.")
        }

        FamilyDialog.Leave -> ConfirmDialog(
            title = "Quitter la famille ?",
            message = if (state.lastMember) "Vous êtes le dernier membre : la famille sera supprimée, avec ses confirmations de présence."
            else "Vous ne verrez plus les présences de cette famille. Vos inscriptions et vos points vous restent, et un parent pourra vous inviter à nouveau avec un code.",
            confirmText = "Quitter", onConfirm = actions.confirmFamilyDialog, onDismiss = actions.closeFamilyDialog,
        )

        null -> Unit
    }

    state.invitation?.let { inv ->
        val until = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRANCE).withZone(ZoneId.systemDefault()).format(inv.expiresAt)
        ConfirmDialog(
            title = "Code d'invitation",
            message = "${inv.code}\n\nValable jusqu'au $until. Chaque code ne sert qu'une fois.",
            confirmText = "Partager", dismissText = "Fermer",
            onConfirm = actions.shareInvitation, onDismiss = actions.dismissInvitation,
        )
    }
}

@Composable
private fun InputDialog(
    title: String,
    confirmText: String,
    busy: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = { Column(verticalArrangement = Arrangement.spacedBy(10.dp)) { content() } },
        confirmButton = { KButton(text = confirmText, onClick = onConfirm, enabled = enabled && !busy, isLoading = busy) },
        dismissButton = { KButton(text = "Annuler", onClick = onDismiss, variant = KButtonVariant.Ghost) },
    )
}

@Composable
private fun Hint(text: String) = Text(text, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
