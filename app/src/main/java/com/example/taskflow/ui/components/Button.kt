// File: ui/components/Button.kt
package com.example.taskflow.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class ButtonVariant {
    Default, Destructive, Outline, Secondary, Ghost, Link
}

enum class ButtonSize {
    Default, Small, Large, Icon
}

@Composable
fun CustomButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Default,
    size: ButtonSize = ButtonSize.Default,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    // Определяем цвета и границу для Material 3
    val containerColor = when (variant) {
        ButtonVariant.Default -> Color(0xFF6200EE)
        ButtonVariant.Destructive -> Color(0xFFB00020)
        ButtonVariant.Outline -> Color.Transparent
        ButtonVariant.Secondary -> Color(0xFFE0E0E0)
        ButtonVariant.Ghost -> Color.Transparent
        ButtonVariant.Link -> Color.Transparent
    }

    val contentColor = when (variant) {
        ButtonVariant.Default, ButtonVariant.Destructive -> Color.White
        ButtonVariant.Outline, ButtonVariant.Secondary -> MaterialTheme.colorScheme.onSurface
        ButtonVariant.Ghost, ButtonVariant.Link -> MaterialTheme.colorScheme.onSurface
    }

    val border = when (variant) {
        ButtonVariant.Outline -> BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        else -> null
    }

    // Размеры и отступы
    val height = when (size) {
        ButtonSize.Default -> 36.dp
        ButtonSize.Small -> 32.dp
        ButtonSize.Large -> 40.dp
        ButtonSize.Icon -> 36.dp
    }

    val padding = when (size) {
        ButtonSize.Default -> PaddingValues(horizontal = 16.dp)
        ButtonSize.Small -> PaddingValues(horizontal = 12.dp)
        ButtonSize.Large -> PaddingValues(horizontal = 24.dp)
        ButtonSize.Icon -> PaddingValues(0.dp)
    }

    // Используем Material 3 Button
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.5f),
            disabledContentColor = contentColor.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(6.dp),
        border = border,
        elevation = null,
        modifier = modifier
            .height(height)
            .padding(padding)
    ) {
        content()
    }
}