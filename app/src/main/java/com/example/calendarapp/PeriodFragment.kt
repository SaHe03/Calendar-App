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
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner

class PeriodFragment : Fragment(R.layout.period_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Obtain references to views declared in that XML file
        val dateText = view.findViewById<TextView>(R.id.idTVDate)
        val calendarView = view.findViewById<CalendarView>(R.id.calendarView)
        val periodPanelSummary = view.findViewById<LinearLayout>(R.id.periodSummaryPanel)
        val periodSummaryText = view.findViewById<TextView>(R.id.periodSummaryText)
        val periodAddNewPanel= view.findViewById<LinearLayout>(R.id.periodAddNewPanel)
        val addperiodButton = view.findViewById<Button>(R.id.addPeriodButton)
        val closeperiodButton = view.findViewById<Button>(R.id.closePeriodButton)
        val periodConfirmButton = view.findViewById<Button>(R.id.addPeriodLogButton)
        val periodFlowInput = view.findViewById<Spinner>(R.id.flowSpinner)
        val periodNotesInput = view.findViewById<EditText>(R.id.periodNotesInput)
        val flowPrompter = view.findViewById<TextView>(R.id.flowPrompter)

        val flowLabels = spinnerPeriodFlows.map { status ->
            getString(status.labelRes())
        }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            flowLabels
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        periodFlowInput.adapter = adapter

        var selectedDateKey: String? = null
        // get the start up inital date
        addperiodButton.setOnClickListener {
            periodPanelSummary.visibility = View.GONE
            periodAddNewPanel.visibility = View.VISIBLE
        }
        closeperiodButton.setOnClickListener {
            periodAddNewPanel.visibility = View.GONE
            periodPanelSummary.visibility = View.VISIBLE
        }

        periodConfirmButton.setOnClickListener {
            val dateKey = selectedDateKey ?: return@setOnClickListener
            var noteInput = periodNotesInput.text.toString().trim()
            val selectedPosition = periodFlowInput.selectedItemPosition
            if (selectedPosition !in spinnerPeriodFlows.indices) {
                flowPrompter.error = "Pick a flow"
                return@setOnClickListener
            }
            val flowInput: PeriodFlowStatus = spinnerPeriodFlows[selectedPosition]
            val existingPeriod = periods.find {
                it.dateKey == dateKey
            }
            if(noteInput == ""){
                noteInput = "No notes"
            }
            if (existingPeriod != null) {
                existingPeriod.flow = flowInput
                existingPeriod.notes = noteInput
            } else {
                periods.add(PeriodLog(dateKey, flowInput, noteInput))
            }
            periodSummaryText.text = buildPeriodSummary(dateKey)
            calendarView.notifyDateChanged(LocalDate.parse(dateKey))
            periodNotesInput.text.clear()
            periodAddNewPanel.visibility = View.GONE
            periodPanelSummary.visibility = View.VISIBLE
        }

        fun showDate(date: LocalDate) {
            val dateKey = date.toString()
            selectedDateKey = dateKey
            dateText.text = date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
            periodSummaryText.text = buildPeriodSummary(dateKey)
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
                    container.view.findViewById<TextView>(R.id.calendarDayText).text = day.date.dayOfMonth.toString()
                    val dayText = container.view.findViewById<TextView>( R.id.calendarDayText )
                    dayText.text = day.date.dayOfMonth.toString()

                    if (day.date.toString() == selectedDateKey) { dayText.setBackgroundResource(
                        R.drawable.date_selector )
                    }
                    else {
                        dayText.setBackgroundResource(0)
                    }
                    val periodIndicator = container.view.findViewById<View>(R.id.periodIndicator)
                    val periodLogSpecificDate = periods.find(){
                            periodLog -> periodLog.dateKey == dateKey
                    }
                    if (periodLogSpecificDate != null){
                        container.view.setBackgroundResource(periodLogSpecificDate.flow.backgroundRes())
                        periodIndicator.visibility = View.VISIBLE
                    } else {
                        periodIndicator.visibility = View.GONE
                    }
                    /*
                    val displayedStatus = when {
                        periodLogSpecificDate != null -> {periodLogSpecificDate.flow }
                        day.date in expectedPeriodDates -> { PeriodFlowStatus.EXPECTING }
                        else -> {null}
                    }
                     */
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
    private fun buildPeriodSummary(dateKey: String): String {
        val dailyPeriods = periods.filter { it.dateKey == dateKey }
        if (dailyPeriods.isEmpty()) {
            return "No information for this date"
        }
        return "Periods\n" + dailyPeriods.joinToString("\n") {
            periodLog -> val flowLabel = getString(periodLog.flow.labelRes())
                    "\t$flowLabel: ${periodLog.notes}"
        }
    }
    //val expectedPeriodDates = calculateExpectedPeriodDates(periods)
    //private fun calculateExpectedPeriodDates(periodLogs: List<PeriodLog>): Set<LocalDate>{}

}


