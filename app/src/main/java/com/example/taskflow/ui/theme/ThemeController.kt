// File: ui/theme/ThemeController.kt
package com.example.taskflow.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun rememberThemeController(): ThemeController {
    val isDark = remember { mutableStateOf(false) }
    return remember { ThemeController(isDark) }
}

class ThemeController(val isDark: MutableState<Boolean>) {
    fun toggle() {
        isDark.value = !isDark.value
    }
}
