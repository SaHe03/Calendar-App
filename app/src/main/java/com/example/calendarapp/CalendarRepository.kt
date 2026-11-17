package com.example.calendarapp

import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * The UI depends on this boundary instead of a concrete storage technology.
 * A future Room repository can implement the same contract.
 */
interface CalendarRepository {
    val items: Flow<List<CalendarItem>>

    suspend fun add(item: CalendarItem)
    suspend fun update(item: CalendarItem)
    suspend fun delete(itemId: Long)
}

class InMemoryCalendarRepository : CalendarRepository {
    private val nextId = AtomicLong(5)
    private val storedItems = MutableStateFlow<List<CalendarItem>>(
        listOf(
            Appointment("2026-08-17", "Doctor", "Teeth", "1:22", "AM", id = 1),
            HabitCheck("2026-08-17", "Shower", 2, HabitStatus.INCOMPLETE, id = 2),
            PeriodLog("2026-08-17", PeriodFlowStatus.NORMAL, "No notes", id = 3),
            Todo("2026-08-17", "Eat", "food", id = 4),
        ),
    )

    override val items: Flow<List<CalendarItem>> = storedItems.asStateFlow()

    override suspend fun add(item: CalendarItem) {
        val storedItem = if (item.id == 0L) item.withId(nextId.getAndIncrement()) else item
        storedItems.update { current -> current + storedItem }
    }

    override suspend fun update(item: CalendarItem) {
        storedItems.update { current ->
            current.map { stored -> if (stored.id == item.id) item else stored }
        }
    }

    override suspend fun delete(itemId: Long) {
        storedItems.update { current -> current.filterNot { it.id == itemId } }
    }
}

object CalendarRepositoryProvider {
    val repository: CalendarRepository = InMemoryCalendarRepository()
}

private fun CalendarItem.withId(id: Long): CalendarItem = when (this) {
    is Appointment -> copy(id = id)
    is HabitCheck -> copy(id = id)
    is PeriodLog -> copy(id = id)
    is Todo -> copy(id = id)
}
