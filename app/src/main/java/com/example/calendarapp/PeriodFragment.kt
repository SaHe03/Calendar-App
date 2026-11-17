package com.example.calendarapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calendarapp.databinding.PeriodFragmentBinding
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.MonthDayBinder
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

class PeriodFragment : Fragment() {
    private var _binding: PeriodFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CalendarViewModel by activityViewModels()
    private lateinit var itemAdapter: CalendarItemAdapter
    private var state = CalendarUiState()
    private var editingPeriod: PeriodLog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = PeriodFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEventList()
        setupFlowSpinner()
        setupEditor()
        setupCalendar()
        binding.idTVDate.setOnClickListener { showDatePicker() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect(::render)
            }
        }
    }

    private fun setupEventList() {
        itemAdapter = CalendarItemAdapter(
            onDeleteRequested = viewModel::delete,
            onEditRequested = viewModel::requestEdit,
            onCompletionChanged = viewModel::toggleComplete,
        )
        binding.periodSummaryList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = itemAdapter
        }
    }

    private fun setupFlowSpinner() {
        val labels = spinnerPeriodFlows.map { getString(it.labelRes()) }
        binding.flowSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            labels,
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    private fun setupEditor() = with(binding) {
        addPeriodButton.setOnClickListener {
            val existing = state.itemsForSelectedDate().filterIsInstance<PeriodLog>().firstOrNull()
            openEditor(existing)
        }
        closePeriodButton.setOnClickListener { closeEditor() }
        addPeriodLogButton.setOnClickListener {
            val selectedPosition = flowSpinner.selectedItemPosition
            if (selectedPosition !in spinnerPeriodFlows.indices) {
                flowPrompter.error = "Pick a flow"
                return@setOnClickListener
            }
            val previous = editingPeriod
            val period = PeriodLog(
                dateKey = previous?.dateKey ?: state.selectedDateKey,
                flow = spinnerPeriodFlows[selectedPosition],
                notes = periodNotesInput.text.toString().trim().ifBlank { "No notes" },
                id = previous?.id ?: 0,
            )
            if (previous == null) viewModel.add(period) else viewModel.update(period)
            closeEditor()
        }
    }

    private fun openEditor(period: PeriodLog?) = with(binding) {
        editingPeriod = period
        periodNotesInput.setText(period?.notes.orEmpty())
        val flowIndex = spinnerPeriodFlows.indexOf(period?.flow)
        if (flowIndex >= 0) flowSpinner.setSelection(flowIndex)
        addPeriodLogButton.setText(
            if (period == null) R.string.add_period_text else R.string.save_changes,
        )
        periodSummaryPanel.isVisible = false
        periodAddNewPanel.isVisible = true
    }

    private fun closeEditor() {
        editingPeriod = null
        viewModel.clearEditRequest()
        binding.periodAddNewPanel.isVisible = false
        binding.periodSummaryPanel.isVisible = true
    }

    private fun setupCalendar() = with(binding.calendarView) {
        dayBinder = object : MonthDayBinder<CalendarDayViewContainer> {
            override fun create(view: View) = CalendarDayViewContainer(view)

            override fun bind(container: CalendarDayViewContainer, day: CalendarDay) {
                val dayBinding = container.binding
                val period = state.items.filterIsInstance<PeriodLog>()
                    .firstOrNull { it.dateKey == day.date.toString() }
                dayBinding.calendarDayRoot.setBackgroundResource(period?.flow?.backgroundRes() ?: 0)
                dayBinding.calendarDayText.apply {
                    text = day.date.dayOfMonth.toString()
                    setBackgroundResource(
                        if (day.date == state.selectedDate) R.drawable.date_selector else 0,
                    )
                }
                dayBinding.appointmentIndicator.isVisible = false
                dayBinding.periodIndicator.isVisible = period != null
                dayBinding.habitIndicator.isVisible = false
                dayBinding.todoIndicator.isVisible = false
                dayBinding.calendarDayRoot.setOnClickListener {
                    viewModel.selectDate(day.date)
                    if (day.position != DayPosition.MonthDate) {
                        smoothScrollToMonth(YearMonth.from(day.date))
                    }
                }
            }
        }
        val currentMonth = YearMonth.now()
        setup(
            currentMonth.minusMonths(12),
            currentMonth.plusMonths(12),
            firstDayOfWeekFromLocale(),
        )
        scrollToMonth(currentMonth)
    }

    private fun render(newState: CalendarUiState) {
        state = newState
        binding.idTVDate.text = newState.selectedDate.format(DATE_FORMAT)
        itemAdapter.submitList(newState.itemsForSelectedDate().filterIsInstance<PeriodLog>())
        binding.calendarView.notifyCalendarChanged()

        val requestedEdit = newState.editingItem as? PeriodLog
        if (requestedEdit != null && editingPeriod?.id != requestedEdit.id) {
            openEditor(requestedEdit)
        }
    }

    private fun showDatePicker() {
        val selectedDate = state.selectedDate
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val date = LocalDate.of(year, month + 1, dayOfMonth)
                viewModel.selectDate(date)
                binding.calendarView.smoothScrollToDate(date)
            },
            selectedDate.year,
            selectedDate.monthValue - 1,
            selectedDate.dayOfMonth,
        ).show()
    }

    override fun onDestroyView() {
        binding.periodSummaryList.adapter = null
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        val DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    }
}
