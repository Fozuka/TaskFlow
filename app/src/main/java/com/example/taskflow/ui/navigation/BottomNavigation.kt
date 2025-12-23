// File: ui/navigation/BottomNavigation.kt
package com.example.taskflow.ui.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.RowScope


@Composable
fun BottomNavigation(
    currentScreen: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp
    ) {
        NavigationItem(
            id = "dashboard",
            label = "Главная",
            icon = Icons.Default.Home,
            selected = currentScreen == "dashboard",
            onClick = { onNavigate("dashboard") }
        )
        NavigationItem(
            id = "analytics",
            label = "Аналитика",
            icon = Icons.Default.PieChart,
            selected = currentScreen == "analytics",
            onClick = { onNavigate("analytics") }
        )
        NavigationItem(
            id = "settings",
            label = "Настройки",
            icon = Icons.Default.Settings,
            selected = currentScreen == "settings",
            onClick = { onNavigate("settings") }
        )
    }
}

@Composable
private fun RowScope.NavigationItem(
    id: String,
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp)
            )
        },
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
        },
        selected = selected,
        onClick = onClick,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        )
    )
}
