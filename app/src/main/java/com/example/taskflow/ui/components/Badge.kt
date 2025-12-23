// File: ui/components/Badge.kt
package com.example.taskflow.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class BadgeVariant {
    Default, Secondary, Destructive, Outline
}

@Composable
fun Badge(
    text: String,
    variant: BadgeVariant = BadgeVariant.Default,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (variant) {
        BadgeVariant.Default -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        BadgeVariant.Secondary -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
        BadgeVariant.Destructive -> Color(0xFFFCEBEB) // светло-красный
        BadgeVariant.Outline -> Color.Transparent
    }

    val contentColor = when (variant) {
        BadgeVariant.Default -> MaterialTheme.colorScheme.primary
        BadgeVariant.Secondary -> MaterialTheme.colorScheme.secondary
        BadgeVariant.Destructive -> Color(0xFFB00020) // тёмно-красный
        BadgeVariant.Outline -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    }

    val borderWidth = when (variant) {
        BadgeVariant.Outline -> 1.dp
        else -> 0.dp
    }

    val borderColor = when (variant) {
        BadgeVariant.Outline -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        else -> Color.Transparent
    }

    Surface(
        modifier = modifier.padding(end = 8.dp),
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        border = if (borderWidth > 0.dp) androidx.compose.foundation.BorderStroke(borderWidth, borderColor) else null
    ) {
        Text(
            text = text,
            color = contentColor,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
