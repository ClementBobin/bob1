package com.mirage.bob1.ui.core.components.ui.calendar

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.mirage.bob1.data.dto.OfficialRole
import com.mirage.bob1.ui.screens.calendar.ConfirmAction

@Composable
fun ConfirmRoleDialog(
    role: OfficialRole,
    action: ConfirmAction,
    onYes: () -> Unit,
    onNo: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onNo,
        title = { Text("Confirmer") },
        text  = {
            val actionLabel = if (action == ConfirmAction.SUBSCRIBE) "vous inscrire comme" else "vous désinscrire de"
            Text("Voulez-vous $actionLabel ${role.displayName()} ?")
        },
        confirmButton = { TextButton(onClick = onYes) { Text("Oui") } },
        dismissButton = { TextButton(onClick = onNo)  { Text("Non") } },
    )
}