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
import com.google.android.material.timepicker.TimeFormat
import java.sql.Time

class ToDoFragment : Fragment(R.layout.todo_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var selectedDateKey: String? = null
        // Obtain references to views declared in that XML file
        val dateText = view.findViewById<TextView>(R.id.idTVDate)
        val calendarView = view.findViewById<CalendarView>(R.id.calendarView)
        val todoPanelSummary = view.findViewById<LinearLayout>(R.id.ToDoSummaryPanel)
        val todoSummaryText = view.findViewById<TextView>(R.id.ToDoSummaryText)
        val todoPanelEdit= view.findViewById<LinearLayout>(R.id.toDoEditPanel)
        val addToDoButton = view.findViewById<Button>(R.id.addNewToDoButton)
        val closeToDoButton = view.findViewById<Button>(R.id.closeToDoButton)
        val addToDoConfirmButton = view.findViewById<Button>(R.id.addToDoConfirmButton)
        val todoTitleInput = view.findViewById<EditText>(R.id.toDoTitleInput)
        val todoNotesInput = view.findViewById<EditText>(R.id.toDoNotesInput)

        // get the start up inital date
        addToDoButton.setOnClickListener {
            todoPanelSummary.visibility = View.GONE
            todoPanelEdit.visibility = View.VISIBLE
        }
        closeToDoButton.setOnClickListener {
            todoPanelEdit.visibility = View.GONE
            todoPanelSummary.visibility = View.VISIBLE
        }
        addToDoConfirmButton.setOnClickListener {
            val dateKey = selectedDateKey ?: return@setOnClickListener
            val titleInput = todoTitleInput.text.toString().trim()
            var noteInput = todoNotesInput.text.toString().trim()
            if (titleInput.isBlank()) {
               todoTitleInput.error = "Enter a Todo"
                return@setOnClickListener
            }

            todoSummaryText.text = buildToDoSummary(dateKey)
            calendarView.notifyDateChanged(LocalDate.parse(dateKey))
            todoList.add(Todo(dateKey, titleInput, noteInput, false))
            todoTitleInput.text.clear()
            todoNotesInput.text.clear()
            todoPanelEdit.visibility = View.GONE
            todoPanelSummary.visibility = View.VISIBLE
        }


        fun showDate(date: LocalDate) {
            val dateKey = date.toString()

            selectedDateKey = dateKey
            dateText.text = date.format(
                DateTimeFormatter.ofPattern("MM/dd/yyyy")
            )
            todoSummaryText.text = buildToDoSummary(dateKey)
        }
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
                    val todoIndicator = container.view.findViewById<View>(R.id.todoIndicator)

                    todoIndicator.visibility =
                        if (todoList.any { it.dateKey == dateKey }) {
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
    private fun buildToDoSummary(dateKey: String): String {
        val dailyToDos = todoList.filter { it.dateKey == dateKey }
        val sections = mutableListOf<String>()
        if (dailyToDos.isNotEmpty()) {
            sections.add(
                "To-dos\n" +
                        dailyToDos.joinToString("\n") {
                            "\t${it.title}: ${it.notes} + ${it.isComplete}"
                        })
        }
        return if (sections.isEmpty()) {
            "No information for this date"
        } else {
            sections.joinToString("\n\n")
        }
    }

}


