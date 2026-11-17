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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calendarapp.databinding.AppointmentFragmentBinding
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.MonthDayBinder
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

class AppointmentFragment : Fragment() {
    private var _binding: AppointmentFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CalendarViewModel by activityViewModels()
    private lateinit var itemAdapter: CalendarItemAdapter
    private var state = CalendarUiState()
    private var editingAppointment: Appointment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = AppointmentFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEventList()
        setupCalendar()
        setupEditor()
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
        binding.apptSummaryList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = itemAdapter
        }
    }

    private fun setupEditor() = with(binding) {
        addApptButton.setOnClickListener { openEditor(null) }
        closeApptButton.setOnClickListener { closeEditor() }
        addApptConfirmButton.setOnClickListener {
            val title = appointmentTitleInput.text.toString().trim()
            val time = appointmentTimeInput.text.toString().trim()
            if (title.isBlank() || time.isBlank()) {
                appointmentTitleInput.error = "Enter an appointment title and time"
                return@setOnClickListener
            }
            val previous = editingAppointment
            val appointment = Appointment(
                dateKey = previous?.dateKey ?: state.selectedDateKey,
                title = title,
                notes = appointmentNotesInput.text.toString().trim().ifBlank { "No notes" },
                time = time,
                meridiem = meridiemSpinner.selectedItem.toString().trim(),
                id = previous?.id ?: 0,
            )
            if (previous == null) viewModel.add(appointment) else viewModel.update(appointment)
            closeEditor()
        }
    }

    private fun openEditor(appointment: Appointment?) = with(binding) {
        editingAppointment = appointment
        appointmentTitleInput.setText(appointment?.title.orEmpty())
        appointmentNotesInput.setText(appointment?.notes.orEmpty())
        appointmentTimeInput.setText(appointment?.time.orEmpty())
        val meridiemIndex = resources.getStringArray(R.array.meridiem_Spinner_Options)
            .indexOfFirst { it.trim() == appointment?.meridiem }
        if (meridiemIndex >= 0) meridiemSpinner.setSelection(meridiemIndex)
        addApptConfirmButton.setText(
            if (appointment == null) R.string.add_appointment_text else R.string.save_changes,
        )
        apptSummaryPanel.isVisible = false
        appointmentAddNewPanel.isVisible = true
    }

    private fun closeEditor() {
        editingAppointment = null
        viewModel.clearEditRequest()
        binding.appointmentAddNewPanel.isVisible = false
        binding.apptSummaryPanel.isVisible = true
    }

    private fun setupCalendar() = with(binding.calendarView) {
        dayBinder = object : MonthDayBinder<CalendarDayViewContainer> {
            override fun create(view: View) = CalendarDayViewContainer(view)

            override fun bind(container: CalendarDayViewContainer, day: CalendarDay) {
                val dayBinding = container.binding
                val hasAppointment = state.items.any {
                    it is Appointment && it.dateKey == day.date.toString()
                }
                dayBinding.calendarDayRoot.setBackgroundResource(0)
                dayBinding.calendarDayText.apply {
                    text = day.date.dayOfMonth.toString()
                    setBackgroundResource(
                        if (day.date == state.selectedDate) R.drawable.date_selector else 0,
                    )
                }
                dayBinding.appointmentIndicator.isVisible = hasAppointment
                dayBinding.periodIndicator.isVisible = false
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
        itemAdapter.submitList(newState.itemsForSelectedDate().filterIsInstance<Appointment>())
        binding.calendarView.notifyCalendarChanged()

        val requestedEdit = newState.editingItem as? Appointment
        if (requestedEdit != null && editingAppointment?.id != requestedEdit.id) {
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
        binding.apptSummaryList.adapter = null
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        val DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    }
}
