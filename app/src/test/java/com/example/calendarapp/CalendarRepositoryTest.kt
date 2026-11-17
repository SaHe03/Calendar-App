package com.example.calendarapp

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CalendarRepositoryTest {

    @Test
    fun addUpdateAndDeleteUseAStableId() = runBlocking {
        val repository = InMemoryCalendarRepository()
        repository.add(
            Appointment(
                dateKey = "2026-11-16",
                title = "Planning",
                notes = "Initial",
                time = "9:00",
                meridiem = "AM",
            ),
        )

        val added = repository.items.first()
            .filterIsInstance<Appointment>()
            .first { it.title == "Planning" }
        assertTrue(added.id > 0)

        repository.update(added.copy(notes = "Updated"))
        val updated = repository.items.first().first { it.id == added.id }
        assertTrue(updated is Appointment)
        assertEquals("Updated", (updated as Appointment).notes)

        repository.delete(added.id)
        assertFalse(repository.items.first().any { it.id == added.id })
    }
}
