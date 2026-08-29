package com.example.calendarapp

import android.R
import java.time.LocalDate
 // To hold the information of a date of an appointment/event of a single day
    data class Appointment(
        var dateKey: String,
        var title: String,
        var notes: String,
        var time: String,
        var meridiem: String
    ) : CalendarItem
    //val appointments: MutableList<Appointment> = mutableListOf()
val appointments: MutableList<Appointment> = mutableListOf(
    Appointment( "2026-08-17",  "Doctor",  "Teeth",  "1:22", "AM")
)
    // To hold the information of a habit being done, does not include the frequency of a habit nor the progress
    enum class HabitStatus{habitComplete, habitIncomplete, habitEmpty}
    data class HabitCheck(
        var dateKey: String,
        var habitName: String,
        var frequency: Int,
        var currentStatus: HabitStatus,
    ) : CalendarItem
    // To hold the information of a single period day
    val habitSet: MutableList<HabitCheck> = mutableListOf(
        HabitCheck("2026-08-17","Shower", 2,HabitStatus.habitIncomplete)
    )

    enum class PeriodFlowStatus{SPOTTY, NORMAL, HEAVY, FLOODING, EXPECTING, PREGNANT}
    data class PeriodLog(
        var dateKey: String,
        var flow: PeriodFlowStatus,
        var notes: String
    ) : CalendarItem
    // To deal with the multiday event of a period and related functions to calculate a period
    val periods: MutableList<PeriodLog> = mutableListOf(
        PeriodLog("2026-08-17", PeriodFlowStatus.NORMAL,"No notes")
    )

//    data class PeriodCycle(val startDate: LocalDate, val endDate: LocalDate){ }
//function to add to a growing list, remove/add at random

    data class Todo(
        var dateKey: String,
        var title: String,
        var notes: String,
        var isComplete: Boolean = false
    ) : CalendarItem
    val todoList: MutableList<Todo> = mutableListOf(
        Todo("2026-08-17",  "Eat",  "food",  false)
    )

