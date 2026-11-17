package com.example.calendarapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.calendarapp.databinding.CalendarItemBinding

class CalendarItemAdapter(
    private val onDeleteRequested: (CalendarItem) -> Unit,
    private val onEditRequested: (CalendarItem) -> Unit,
    private val onCompletionChanged: (CalendarItem, Boolean) -> Unit,
) : ListAdapter<CalendarItem, CalendarItemAdapter.ViewHolder>(ItemDiff) {

    private var expandedItemId: Long? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CalendarItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: CalendarItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CalendarItem) = with(binding) {
            itemCheckBox.setOnCheckedChangeListener(null)
            itemCheckBox.visibility = View.GONE
            itemCheckBox.isChecked = false

            when (item) {
                is Appointment -> {
                    itemTextName.text = "${item.title} · ${item.time} ${item.meridiem}"
                    itemTextNotes.text = item.notes
                }
                is PeriodLog -> {
                    itemTextName.text = item.flow.name.lowercase().replaceFirstChar { it.uppercase() }
                    itemTextNotes.text = item.notes
                }
                is HabitCheck -> {
                    itemTextName.text = item.habitName
                    itemTextNotes.text = if (item.frequency == 1) {
                        "Every day"
                    } else {
                        "Every ${item.frequency} days"
                    }
                    itemCheckBox.visibility = View.VISIBLE
                    itemCheckBox.isChecked = item.currentStatus == HabitStatus.COMPLETE
                }
                is Todo -> {
                    itemTextName.text = item.title
                    itemTextNotes.text = item.notes
                    itemCheckBox.visibility = View.VISIBLE
                    itemCheckBox.isChecked = item.isComplete
                }
            }

            val expanded = item.id == expandedItemId
            itemEditButton.visibility = if (expanded) View.VISIBLE else View.GONE
            itemDeleteButton.visibility = if (expanded) View.VISIBLE else View.GONE

            root.setOnClickListener {
                val previousExpandedId = expandedItemId
                expandedItemId = if (expandedItemId == item.id) null else item.id
                previousExpandedId?.let { notifyItemChangedById(it) }
                notifyItemChangedById(item.id)
            }
            itemEditButton.setOnClickListener {
                expandedItemId = null
                onEditRequested(item)
            }
            itemDeleteButton.setOnClickListener {
                expandedItemId = null
                onDeleteRequested(item)
            }
            itemCheckBox.setOnCheckedChangeListener { _, isChecked ->
                onCompletionChanged(item, isChecked)
            }
        }
    }

    private fun notifyItemChangedById(id: Long) {
        val index = currentList.indexOfFirst { it.id == id }
        if (index >= 0) notifyItemChanged(index)
    }

    private object ItemDiff : DiffUtil.ItemCallback<CalendarItem>() {
        override fun areItemsTheSame(oldItem: CalendarItem, newItem: CalendarItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: CalendarItem, newItem: CalendarItem): Boolean =
            oldItem == newItem
    }
}
