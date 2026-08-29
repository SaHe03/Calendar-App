package com.example.calendarapp

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.core.DayPosition
import java.time.YearMonth
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import android.app.DatePickerDialog
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.timepicker.TimeFormat
import java.sql.Time

class HabitFragment : Fragment(R.layout.habit_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var selectedDateKey: String? = null
        // Obtain references to views declared in that XML file
        val dateText = view.findViewById<TextView>(R.id.idTVDate)
        val calendarView = view.findViewById<CalendarView>(R.id.calendarView)
        val habitSummaryPanel = view.findViewById<LinearLayout>(R.id.habitSummaryPanel)
        val habitSummary = view.findViewById<RecyclerView>(R.id.habitSummaryText)
        val habitAddNewPanel= view.findViewById<LinearLayout>(R.id.habitAddNewPanel)
        val addNewHabitButton = view.findViewById<Button>(R.id.addNewHabitButton)
        val closeHabitButton = view.findViewById<Button>(R.id.closeHabitButton)
        val confirmHabitButton = view.findViewById<Button>(R.id.confirmHabitButton)
        val habitTitleInput = view.findViewById<EditText>(R.id.habitTitleInput)
        val habitFrequencyInput = view.findViewById<EditText>(R.id.habitFrequencyInput)
        habitSummary.layoutManager = LinearLayoutManager(requireContext())

        addNewHabitButton.setOnClickListener {
            habitSummaryPanel.visibility = View.GONE
            habitEditPanel.visibility = View.VISIBLE
        }
        closeHabitButton.setOnClickListener {
            habitEditPanel.visibility = View.GONE
            habitSummaryPanel.visibility = View.VISIBLE
        }

        confirmHabitButton.setOnClickListener {
            val dateKey = selectedDateKey ?: return@setOnClickListener
            val titleInput = habitTitleInput.text.toString().trim()
            var freqInput = habitFrequencyInput.text.toString().trim().toInt()
            if (titleInput.isBlank()) {
                habitTitleInput.error = "Enter a Habit name"
                return@setOnClickListener
            }
            if(freqInput < 1){
                freqInput = 1
            }
            habitSummary.adapter = CalendarItemAdapter(List("Placeholder item"))
            habitSummary.adapter = CalendarItemAdapter(habitsForDate(dateKey))
            calendarView.notifyDateChanged(LocalDate.parse(dateKey))
            habitSet.add(HabitCheck(dateKey, titleInput, freqInput.toInt(), HabitStatus.habitIncomplete))
            habitTitleInput.text.clear()
            habitFrequencyInput.text.clear()
            habitEditPanel.visibility = View.GONE
            habitSummaryPanel.visibility = View.VISIBLE
        }


        fun showDate(date: LocalDate) {
            val dateKey = date.toString()

            selectedDateKey = dateKey
            dateText.text = date.format(
                DateTimeFormatter.ofPattern("MM/dd/yyyy")
            )
            habitSummary.adapter = CalendarItemAdapter(
                arrayOf("Placeholder item")
            )        }
        dateText.setOnClickListener {
            val today = LocalDate.now()
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                val pickedDate = LocalDate.of( year, month + 1, dayOfMonth)
                showDate(pickedDate)
                calendarView.smoothScrollToDate(pickedDate) },
                today.year, today.monthValue - 1, today.dayOfMonth).show()
        }
        showDate(LocalDate.now())
        calendarView.dayBinder =
            object : MonthDayBinder<ViewContainer> {
                override fun create(view: View) = ViewContainer(view)
                override fun bind(container: ViewContainer, day: CalendarDay){
                    val dateKey = day.date.toString()
                    container.view.findViewById<TextView>(R.id.calendarDayText)
                        .text = day.date.dayOfMonth.toString()
                    val dayText = container.view.findViewById<TextView>( R.id.calendarDayText )
                    dayText.text = day.date.dayOfMonth.toString()

                    if (day.date.toString() == selectedDateKey) { dayText.setBackgroundResource(
                        R.drawable.date_selector ) }
                    else {
                        dayText.setBackgroundResource(0)
                    }
                    val habitIndicator = container.view.findViewById<View>(R.id.habitIndicator)

                    habitIndicator.visibility =
                        if (habitSet.any { it.dateKey == dateKey }) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                    container.view.setOnClickListener {
                        val previousDateKey = selectedDateKey
                        showDate(day.date)
                        previousDateKey?.let {
                            calendarView.notifyDateChanged(
                                LocalDate.parse(it) )
                        }
                        calendarView.notifyDateChanged(day.date)

                        if (day.position != DayPosition.MonthDate) {
                            calendarView.smoothScrollToMonth( YearMonth.from(day.date) )
                        }
                    }
                    // THis is handling clicks on day
                    container.view.setOnClickListener {
                        val previousDateKey = selectedDateKey

                        showDate(day.date)

                        previousDateKey?.let {
                            calendarView.notifyDateChanged(
                                LocalDate.parse(it)
                            )
                        }

                        calendarView.notifyDateChanged(day.date)

                        if (day.position != DayPosition.MonthDate) {
                            calendarView.smoothScrollToMonth(
                                YearMonth.from(day.date)
                            )
                        }
                    }
                }
            }
        val currentMonth = YearMonth.now()
        calendarView.setup(
            currentMonth.minusMonths(12),
            currentMonth.plusMonths(12),
            firstDayOfWeekFromLocale()
        )

        calendarView.scrollToMonth(currentMonth)
    }
    private fun habitsForDate(dateKey: String): List<HabitCheck> {
        val dailyHabit = habitSet.filter { it.dateKey == dateKey }
        return dailyHabit
    }

}


