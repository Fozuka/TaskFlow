// File: ui/components/CustomSwitch.kt
package com.example.taskflow.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CustomSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = Color(0xFF6200EE),
            uncheckedThumbColor = Color.White,
            uncheckedTrackColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        ),
        modifier = modifier.size(width = 48.dp, height = 20.dp)
    )
}
