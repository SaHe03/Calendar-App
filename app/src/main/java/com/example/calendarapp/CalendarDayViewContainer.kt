package com.example.calendarapp

import android.view.View
import com.example.calendarapp.databinding.CalendarDayLayoutBinding
import com.kizitonwose.calendar.view.ViewContainer

class CalendarDayViewContainer(view: View) : ViewContainer(view) {
    val binding: CalendarDayLayoutBinding = CalendarDayLayoutBinding.bind(view)
}
