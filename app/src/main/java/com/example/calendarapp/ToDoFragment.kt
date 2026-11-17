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
import com.example.calendarapp.databinding.TodoFragmentBinding
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.MonthDayBinder
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

class ToDoFragment : Fragment() {
    private var _binding: TodoFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CalendarViewModel by activityViewModels()
    private lateinit var itemAdapter: CalendarItemAdapter
    private var state = CalendarUiState()
    private var editingTodo: Todo? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = TodoFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEventList()
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
        binding.todoSummaryList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = itemAdapter
        }
    }

    private fun setupEditor() = with(binding) {
        addNewToDoButton.setOnClickListener { openEditor(null) }
        closeToDoButton.setOnClickListener { closeEditor() }
        addToDoConfirmButton.setOnClickListener {
            val title = toDoTitleInput.text.toString().trim()
            if (title.isBlank()) {
                toDoTitleInput.error = "Enter a to-do"
                return@setOnClickListener
            }
            val previous = editingTodo
            val todo = Todo(
                dateKey = previous?.dateKey ?: state.selectedDateKey,
                title = title,
                notes = toDoNotesInput.text.toString().trim().ifBlank { "No notes" },
                isComplete = previous?.isComplete ?: false,
                id = previous?.id ?: 0,
            )
            if (previous == null) viewModel.add(todo) else viewModel.update(todo)
            closeEditor()
        }
    }

    private fun openEditor(todo: Todo?) = with(binding) {
        editingTodo = todo
        toDoTitleInput.setText(todo?.title.orEmpty())
        toDoNotesInput.setText(todo?.notes.orEmpty())
        addToDoConfirmButton.setText(
            if (todo == null) R.string.add_todo_text else R.string.save_changes,
        )
        todoSummaryPanel.isVisible = false
        toDoAddNewPanel.isVisible = true
    }

    private fun closeEditor() {
        editingTodo = null
        viewModel.clearEditRequest()
        binding.toDoAddNewPanel.isVisible = false
        binding.todoSummaryPanel.isVisible = true
    }

    private fun setupCalendar() = with(binding.calendarView) {
        dayBinder = object : MonthDayBinder<CalendarDayViewContainer> {
            override fun create(view: View) = CalendarDayViewContainer(view)

            override fun bind(container: CalendarDayViewContainer, day: CalendarDay) {
                val dayBinding = container.binding
                val hasTodo = state.items.any {
                    it is Todo && it.dateKey == day.date.toString()
                }
                dayBinding.calendarDayRoot.setBackgroundResource(0)
                dayBinding.calendarDayText.apply {
                    text = day.date.dayOfMonth.toString()
                    setBackgroundResource(
                        if (day.date == state.selectedDate) R.drawable.date_selector else 0,
                    )
                }
                dayBinding.appointmentIndicator.isVisible = false
                dayBinding.periodIndicator.isVisible = false
                dayBinding.habitIndicator.isVisible = false
                dayBinding.todoIndicator.isVisible = hasTodo
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
        itemAdapter.submitList(newState.itemsForSelectedDate().filterIsInstance<Todo>())
        binding.calendarView.notifyCalendarChanged()

        val requestedEdit = newState.editingItem as? Todo
        if (requestedEdit != null && editingTodo?.id != requestedEdit.id) {
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
        binding.todoSummaryList.adapter = null
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        val DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    }
}
