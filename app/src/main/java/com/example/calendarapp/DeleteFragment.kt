package com.example.calendarapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.calendarapp.databinding.DeleteFragmentBinding

/**
 * Kept as a legacy draft. Row deletion is now handled directly through the
 * adapter callback and CalendarViewModel.
 */
class DeleteFragment : Fragment() {
    private var _binding: DeleteFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DeleteFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeDeletionButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.deleteEventConfirmButton.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                "delete_confirmation",
                Bundle().apply { putBoolean("confirmed", true) },
            )
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
