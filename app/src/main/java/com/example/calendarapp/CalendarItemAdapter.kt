package com.example.calendarapp

import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility

class CalendarItemAdapter(private val dataSet: List<CalendarItem>,
                          private val onDeleteRequested: (CalendarItem) -> Unit,
                          private val onEditRequested: (CalendarItem) -> Unit):
    RecyclerView.Adapter<CalendarItemAdapter.ViewHolder>(){

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            // Define click listener for the ViewHolder's View
            val itemTextName: TextView = view.findViewById(R.id.itemTextName)
            val itemTextNotes: TextView = view.findViewById(R.id.itemTextNotes)
            val itemCheckBox: TextView = view.findViewById(R.id.itemCheckBox)
            val itemEditButton: TextView = view.findViewById(R.id.itemEditButton)
            val itemDeleteButton: TextView = view.findViewById(R.id.itemDeleteButton)
        }
        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            // Create a new view, which defines the UI of the list item
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.calendar_item, viewGroup, false)
            return ViewHolder(view)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            val event = dataSet[position]
            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            when (event) {
                is Appointment -> {
                    viewHolder.itemTextName.text = event.title
                    viewHolder.itemTextNotes.text = event.notes
                }
                is PeriodLog -> {
                    viewHolder.itemTextName.text = event.flow.toString()
                    viewHolder.itemTextNotes.text = event.notes
                }
                is HabitCheck -> {
                    viewHolder.itemTextName.text = event.habitName
                    viewHolder.itemCheckBox.visibility = View.VISIBLE
                    if (event.frequency > 1){
                        viewHolder.itemTextNotes.text = "Every " + event.frequency + " days"
                    } else {
                        viewHolder.itemTextNotes.text = "Every single days"
                    }
                    viewHolder.itemCheckBox.isActivated = event.currentStatus == HabitStatus.habitComplete
                }
                is Todo -> {
                    viewHolder.itemTextName.text = event.title
                    viewHolder.itemTextNotes.text = event.notes
                    viewHolder.itemCheckBox.visibility = View.VISIBLE
                    viewHolder.itemCheckBox.isActivated = event.isComplete == true
                }
            }
            viewHolder.itemTextName.setOnClickListener {
                viewHolder.itemEditButton.visibility = View.VISIBLE
                viewHolder.itemDeleteButton.visibility = View.VISIBLE

            }
            viewHolder.itemEditButton.setOnClickListener {
                viewHolder.itemEditButton.visibility = View.GONE
                viewHolder.itemDeleteButton.visibility = View.GONE
                viewHolder.itemEditButton.setOnClickListener {
                    onEditRequested(event)
                }
            }
            viewHolder.itemDeleteButton.setOnClickListener {
                viewHolder.itemEditButton.visibility = View.GONE
                viewHolder.itemDeleteButton.visibility = View.GONE
                viewHolder.itemDeleteButton.setOnClickListener {
                    onDeleteRequested(event)
                }
            }


        }
        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = dataSet.size
}