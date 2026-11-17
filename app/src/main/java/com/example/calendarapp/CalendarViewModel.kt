package com.example.calendarapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CalendarUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val items: List<CalendarItem> = emptyList(),
    val editingItem: CalendarItem? = null,
) {
    val selectedDateKey: String get() = selectedDate.toString()

    fun itemsForSelectedDate(): List<CalendarItem> =
        items.filter { it.dateKey == selectedDateKey }
}

class CalendarViewModel : ViewModel() {
    private val repository = CalendarRepositoryProvider.repository
    private val selectedDate = MutableStateFlow(LocalDate.now())
    private val editingItem = MutableStateFlow<CalendarItem?>(null)

    val uiState: StateFlow<CalendarUiState> = combine(
        repository.items,
        selectedDate,
        editingItem,
    ) { items, date, itemBeingEdited ->
        CalendarUiState(date, items, itemBeingEdited)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CalendarUiState(),
    )

    fun selectDate(date: LocalDate) {
        selectedDate.value = date
    }

    fun requestEdit(item: CalendarItem) {
        selectedDate.value = LocalDate.parse(item.dateKey)
        editingItem.value = item
    }

    fun clearEditRequest() {
        editingItem.value = null
    }

    fun add(item: CalendarItem) {
        viewModelScope.launch { repository.add(item) }
    }

    fun update(item: CalendarItem) {
        viewModelScope.launch { repository.update(item) }
    }

    fun delete(item: CalendarItem) {
        if (editingItem.value?.id == item.id) clearEditRequest()
        viewModelScope.launch { repository.delete(item.id) }
    }

    fun toggleComplete(item: CalendarItem, isComplete: Boolean) {
        when (item) {
            is HabitCheck -> update(
                item.copy(
                    currentStatus = if (isComplete) HabitStatus.COMPLETE else HabitStatus.INCOMPLETE,
                ),
            )
            is Todo -> update(item.copy(isComplete = isComplete))
            else -> Unit
        }
    }
}
