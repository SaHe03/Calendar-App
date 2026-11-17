package com.example.calendarapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calendarapp.databinding.MainOverviewBinding
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.MonthDayBinder
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

class MainOverviewFragment : Fragment() {
    private var _binding: MainOverviewBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CalendarViewModel by activityViewModels()
    private lateinit var itemAdapter: CalendarItemAdapter
    private var state = CalendarUiState()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = MainOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEventList()
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
            onEditRequested = { item ->
                viewModel.requestEdit(item)
                val destination = when (item) {
                    is Appointment -> R.id.nav_appointments
                    is PeriodLog -> R.id.nav_periods
                    is HabitCheck -> R.id.nav_habits
                    is Todo -> R.id.nav_todo
                }
                findNavController().navigate(destination)
            },
            onCompletionChanged = viewModel::toggleComplete,
        )
        binding.dailySummaryList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = itemAdapter
        }
    }

    private fun setupCalendar() = with(binding.calendarView) {
        dayBinder = object : MonthDayBinder<CalendarDayViewContainer> {
            override fun create(view: View) = CalendarDayViewContainer(view)

            override fun bind(container: CalendarDayViewContainer, day: CalendarDay) {
                val dayBinding = container.binding
                val itemsForDate = state.items.filter { it.dateKey == day.date.toString() }

                dayBinding.calendarDayRoot.setBackgroundResource(0)
                dayBinding.calendarDayText.apply {
                    text = day.date.dayOfMonth.toString()
                    setBackgroundResource(
                        if (day.date == state.selectedDate) R.drawable.date_selector else 0,
                    )
                }
                dayBinding.appointmentIndicator.isVisible = itemsForDate.any { it is Appointment }
                dayBinding.periodIndicator.isVisible = itemsForDate.any { it is PeriodLog }
                dayBinding.habitIndicator.isVisible = itemsForDate.any { it is HabitCheck }
                dayBinding.todoIndicator.isVisible = itemsForDate.any { it is Todo }

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
        itemAdapter.submitList(newState.itemsForSelectedDate())
        binding.calendarView.notifyCalendarChanged()
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
        binding.dailySummaryList.adapter = null
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        val DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    }
}
