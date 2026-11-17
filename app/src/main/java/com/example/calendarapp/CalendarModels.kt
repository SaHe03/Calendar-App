package com.example.calendarapp

data class Appointment(
    override val dateKey: String,
    val title: String,
    val notes: String,
    val time: String,
    val meridiem: String,
    override val id: Long = 0,
) : CalendarItem

enum class HabitStatus { COMPLETE, INCOMPLETE }

data class HabitCheck(
    override val dateKey: String,
    val habitName: String,
    val frequency: Int,
    val currentStatus: HabitStatus,
    override val id: Long = 0,
) : CalendarItem

enum class PeriodFlowStatus { SPOTTY, NORMAL, HEAVY, FLOODING, EXPECTING, PREGNANT }

data class PeriodLog(
    override val dateKey: String,
    val flow: PeriodFlowStatus,
    val notes: String,
    override val id: Long = 0,
) : CalendarItem

data class Todo(
    override val dateKey: String,
    val title: String,
    val notes: String,
    val isComplete: Boolean = false,
    override val id: Long = 0,
) : CalendarItem

