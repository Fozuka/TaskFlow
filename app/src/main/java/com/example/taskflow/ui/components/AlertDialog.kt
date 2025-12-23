// File: ui/components/AlertDialog.kt
package com.example.taskflow.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun AlertDialog(
    onDismissRequest: () -> Unit,
    title: String? = null,
    text: String? = null,
    confirmText: String = "OK",
    dismissText: String? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit = onDismissRequest
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                if (title != null) {
                    Text(
                        text = title,
                        style = androidx.compose.material3.MaterialTheme.typography.titleLarge
                    )
                }
            },
            text = {
                if (text != null) {
                    Column {
                        Text(
                            text = text,
                            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    }
                ) {
                    Text(confirmText)
                }
            },
            dismissButton = {
                if (dismissText != null) {
                    TextButton(onClick = onDismiss) {
                        Text(dismissText)
                    }
                }
            }
        )
    }
}
