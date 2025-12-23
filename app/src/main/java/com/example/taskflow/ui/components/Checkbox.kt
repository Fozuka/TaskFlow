// File: ui/components/Checkbox.kt
package com.example.taskflow.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CustomCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        colors = CheckboxDefaults.colors(
            checkedColor = Color(0xFF6200EE),
            checkmarkColor = Color.White,
            uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        ),
        modifier = modifier.size(16.dp)
    )
}