package com.example.taskflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.taskflow.model.Task
import com.example.taskflow.ui.components.TaskCard
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    tasks: List<Task>,
    loading: Boolean,
    error: String?,
    onNavigate: (String) -> Unit,
    onToggleComplete: (String) -> Unit,
    onRefresh: () -> Unit
) {
    // Самый надёжный способ — обычный MutableState
    val dateFilterState = remember { mutableStateOf<DateFilter>(DateFilter.Today) }
    val dateFilter = dateFilterState.value

    // Фильтрация — без smart-cast, без ошибок
    val filteredTasks = remember(tasks, dateFilter) {
        val now = Date().time
        val weekAhead = now + 7 * 24 * 60 * 60 * 1000L

        tasks.filter { task ->
            val deadlineTime = task.deadline?.time ?: return@filter false

            when (dateFilter) {
                DateFilter.Today ->
                    isSameDay(Date(deadlineTime), Date())

                DateFilter.Tomorrow ->
                    isSameDay(Date(deadlineTime), Date(now + 86_400_000L))

                DateFilter.Next7Days ->
                    deadlineTime in now..weekAhead

                is DateFilter.Custom -> {
                    // as? — безопасное приведение, всегда работает
                    val custom = dateFilter as? DateFilter.Custom
                    custom != null && deadlineTime in custom.from..custom.to
                }
            }
        }.sortedBy { it.deadline }
    }

    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        state = pullToRefreshState,
        isRefreshing = loading,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                Column {
                    CenterAlignedTopAppBar(
                        title = { Text("Задачи") },
                    )

                    DateFilterTabs(
                        selectedFilter = dateFilter,
                        onFilterSelected = { dateFilterState.value = it },
                        onCustomRange = { from, to ->
                            dateFilterState.value = DateFilter.Custom(from, to)
                        }
                    )
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { onNavigate("create-task") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = "Добавить задачу")
                }
            },
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (loading && tasks.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (error != null) {
                    Text(
                        text = "Ошибка: $error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (filteredTasks.isEmpty()) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Outlined.Add,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = when (dateFilter) {
                                DateFilter.Today -> "Нет задач на сегодня"
                                DateFilter.Tomorrow -> "Нет задач на завтра"
                                DateFilter.Next7Days -> "Нет задач на ближайшие 7 дней"
                                is DateFilter.Custom -> "Нет задач в выбранном периоде"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        item {
                            Text(
                                text = when (dateFilter) {
                                    DateFilter.Today -> "Сегодня"
                                    DateFilter.Tomorrow -> "Завтра"
                                    DateFilter.Next7Days -> "Ближайшие 7 дней"
                                    is DateFilter.Custom -> "Выбранный период"
                                },
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(filteredTasks) { task ->
                            TaskCard(
                                task = task,
                                onToggleComplete = onToggleComplete,
                                onClick = { onNavigate("task-detail/${task.id}") }
                            )
                        }
                    }
                }
            }
        }

        PullToRefreshDefaults.Indicator(
            state = pullToRefreshState,
            isRefreshing = loading,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

sealed class DateFilter {
    object Today : DateFilter()
    object Tomorrow : DateFilter()
    object Next7Days : DateFilter()
    data class Custom(val from: Long, val to: Long) : DateFilter()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateFilterTabs(
    selectedFilter: DateFilter,
    onFilterSelected: (DateFilter) -> Unit,
    onCustomRange: (Long, Long) -> Unit
) {
    var showDateRangePicker by remember { mutableStateOf(false) }

    val tabs = listOf(
        "Сегодня" to DateFilter.Today,
        "Завтра" to DateFilter.Tomorrow,
        "7 дней" to DateFilter.Next7Days,
        "Период…" to null as DateFilter?
    )

    ScrollableTabRow(
        selectedTabIndex = when (selectedFilter) {
            DateFilter.Today -> 0
            DateFilter.Tomorrow -> 1
            DateFilter.Next7Days -> 2
            is DateFilter.Custom -> 3
        },
        edgePadding = 0.dp,
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
    ) {
        tabs.forEachIndexed { index, (label, filter) ->
            Tab(
                selected = if (filter == null) selectedFilter is DateFilter.Custom else selectedFilter == filter,
                onClick = {
                    if (filter != null) onFilterSelected(filter) else showDateRangePicker = true
                },
                text = { Text(label) }
            )
        }
    }

    if (showDateRangePicker) {
        AlertDialog(
            onDismissRequest = { showDateRangePicker = false },
            title = { Text("Выберите период") },
            text = {
                com.example.taskflow.ui.components.DateRangePicker(
                    onSelect = { from, to ->
                        val fromMillis = from.atStartOfDay()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toInstant().toEpochMilli()
                        val toMillis = to.atStartOfDay().plusDays(1)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toInstant().toEpochMilli()
                        onCustomRange(fromMillis, toMillis)
                        showDateRangePicker = false
                    },
                    onClose = { showDateRangePicker = false }
                )
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}

private fun isSameDay(d1: Date, d2: Date): Boolean {
    val c1 = Calendar.getInstance().apply { time = d1 }
    val c2 = Calendar.getInstance().apply { time = d2 }
    return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
            c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
}