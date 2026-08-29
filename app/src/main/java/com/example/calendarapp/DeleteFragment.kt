package com.example.calendarapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
// THE ADAPTER DOESN'T HANDLE THE MOVING THIS WITH THE BUTTONS IN HERE, throw it at fragments that know what's being deleted
class DeleteFragment : Fragment(R.layout.delete_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val exitDeleteButton = view.findViewById<Button>(R.id.closeDeletionButton)
        val confirmDeleteButton = view.findViewById<Button>(    R.id.deleteEventConfirmButton)
        exitDeleteButton.setOnClickListener{
            parentFragmentManager.popBackStack()
        }
        confirmDeleteButton.setOnClickListener {
            val result = Bundle().apply {
                putBoolean("confirmed", true)
            }
            parentFragmentManager.setFragmentResult(
                "delete_confirmation",
                result
            )
            parentFragmentManager.popBackStack()
        }
    }

}