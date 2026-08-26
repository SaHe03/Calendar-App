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

    class MainOverviewFragment : Fragment(R.layout.main_overview) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var selectedDateKey: String? = null
        // Obtain references to views declared in that XML file
        val dateText = view.findViewById<TextView>(R.id.idTVDate)
        val calendarView = view.findViewById<CalendarView>(R.id.calendarView)
        val dailySummaryText = view.findViewById<TextView>(R.id.dailySummaryText)
        // get the start up inital date
        fun showDate(date: LocalDate) {
            val dateKey = date.toString()

            selectedDateKey = dateKey
            dateText.text = date.format(
                DateTimeFormatter.ofPattern("MM/dd/yyyy")
            )
            dailySummaryText.text =
                buildDailySummary(dateKey)
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
                    val periodIndicator=container.view.findViewById<View>(R.id.periodIndicator)
                    val habitIndicator=container.view.findViewById<View>(R.id.habitIndicator)
                    val todoIndicator = container.view.findViewById<View>(R.id.todoIndicator)

                    appointmentIndicator.visibility =
                        if (appointments.any { it.dateKey == dateKey }) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }

                    periodIndicator.visibility =
                        if (periods.any { it.dateKey == dateKey }) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }

                    habitIndicator.visibility =
                        if (habits.any { it.dateKey == dateKey }) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
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
        private fun buildDailySummary(dateKey: String): String {
            val dailyAppointments = appointments.filter { it.dateKey == dateKey }
            val dailyHabits = habits.filter { it.dateKey == dateKey }
            val dailyPeriodLogs = periods.filter { it.dateKey == dateKey }
            val sections = mutableListOf<String>()
            if (dailyAppointments.isNotEmpty()) {
                sections.add(
                    "Appointments\n" +
                            dailyAppointments.joinToString("\n") {
                                "\t${it.title}: ${it.time} ${it.meridiem}\n\t\t- ${it.notes}"
                            })
            }
            // need to move into check text view
            if (dailyHabits.isNotEmpty()) {
                sections.add(
                    "Habits\n" +
                            dailyHabits.joinToString("\n") {
                                "\t${it.habitName}"
                            })
            }
            // would want to affect the calendar's view
            if (dailyPeriodLogs.isNotEmpty()) {
                sections.add(
                    "Period\n" +
                            dailyPeriodLogs.joinToString("\n") {
                                val flowLabel = getString(it.flow.labelRes())
                                "\t$flowLabel: ${it.notes}"
                            })
            }
            return if (sections.isEmpty()) {
                "No information for this date"
            } else {
                sections.joinToString("\n\n")
            }
        }
    }

