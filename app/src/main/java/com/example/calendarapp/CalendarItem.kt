package com.example.calendarapp

/**
 * A record that can be placed on a calendar day.
 *
 * The stable [id] lets storage update or delete the correct row even when the
 * RecyclerView has been sorted, filtered, or recreated.
 */
sealed interface CalendarItem {
    val id: Long
    val dateKey: String
}
