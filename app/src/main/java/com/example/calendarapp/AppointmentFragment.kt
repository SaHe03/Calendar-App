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
import android.widget.Spinner
import com.google.android.material.timepicker.TimeFormat
import java.sql.Time

class AppointmentFragment : Fragment(R.layout.appointment_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var selectedDateKey: String? = null
        // Obtain references to views declared in that XML file
        val dateText = view.findViewById<TextView>(R.id.idTVDate)
        val calendarView = view.findViewById<CalendarView>(R.id.calendarView)
        val apptPanelSummary = view.findViewById<LinearLayout>(R.id.apptSummaryPanel)
        val apptSummaryText = view.findViewById<TextView>(R.id.apptSummaryText)
        val apptPanelAddNew= view.findViewById<LinearLayout>(R.id.appointmentAddNewPanel)
        val addApptButton = view.findViewById<Button>(R.id.addApptButton)
        val closeApptButton = view.findViewById<Button>(R.id.closeApptButton)
        val addApptConfirmButton = view.findViewById<Button>(R.id.addApptConfirmButton)
        val appointmentTitleInput = view.findViewById<EditText>(R.id.appointmentTitleInput)
        val appointmentNotesInput = view.findViewById<EditText>(R.id.appointmentNotesInput)
        val appointmentTimeInput = view.findViewById<EditText>(R.id.appointmentTimeInput)
        val meridiemSpinner = view.findViewById<Spinner>(R.id.meridiem_Spinner)

        // get the start up inital date
        addApptButton.setOnClickListener {
            apptPanelSummary.visibility = View.GONE
            apptPanelAddNew.visibility = View.VISIBLE
        }
        closeApptButton.setOnClickListener {
            apptPanelAddNew.visibility = View.GONE
            apptPanelSummary.visibility = View.VISIBLE
        }
        addApptConfirmButton.setOnClickListener {
            val dateKey = selectedDateKey ?: return@setOnClickListener
            val titleInput = appointmentTitleInput.text.toString().trim()
            var noteInput = appointmentNotesInput.text.toString().trim()
            val timeInput = appointmentTimeInput.text.toString().trim()
            val meridiemInput = meridiemSpinner.selectedItem.toString()
            if (titleInput.isBlank() || timeInput.isBlank()) {
                appointmentTitleInput.error = "Enter an appointment title and time"
                return@setOnClickListener
            }
            if(noteInput == ""){
                noteInput = "No notes"
            }

            appointments.add(Appointment(dateKey, titleInput, noteInput, timeInput, meridiemInput))

            apptSummaryText.text = buildApptSummary(dateKey)
            calendarView.notifyDateChanged(LocalDate.parse(dateKey))
            appointmentTitleInput.text.clear()
            appointmentNotesInput.text.clear()
            appointmentTimeInput.text.clear()
            apptPanelAddNew.visibility = View.GONE
            apptPanelSummary.visibility = View.VISIBLE
        }
        
        fun showDate(date: LocalDate) {
            val dateKey = date.toString()

            selectedDateKey = dateKey
            dateText.text = date.format(
                DateTimeFormatter.ofPattern("MM/dd/yyyy")
            )
            apptSummaryText.text =
                buildApptSummary(dateKey)
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
                    val appointmentIndicator = container.view.findViewById<View>(R.id.appointmentIndicator)

                    appointmentIndicator.visibility =
                        if (appointments.any { it.dateKey == dateKey }) {
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
    private fun buildApptSummary(dateKey: String): String {
        val dailyAppointments = appointments.filter { it.dateKey == dateKey }
        val sections = mutableListOf<String>()
        if (dailyAppointments.isNotEmpty()) {
            sections.add(
                "Appointments\n" +
                        dailyAppointments.joinToString("\n") {
                            "\t${it.title}: ${it.time} ${it.meridiem} \n\t\t- ${it.notes}"
                        })
        }
        return if (sections.isEmpty()) {
            "No information for this date"
        } else {
            sections.joinToString("\n\n")
        }
    }
}
