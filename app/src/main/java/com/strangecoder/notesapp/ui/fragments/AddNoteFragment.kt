package com.strangecoder.notesapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import com.strangecoder.notesapp.MainActivity
import com.strangecoder.notesapp.databinding.FragmentAddNoteBinding
import com.strangecoder.notesapp.model.Note
import com.strangecoder.notesapp.ui.MainViewModel
import com.strangecoder.notesapp.utils.Utils

class AddNoteFragment : Fragment() {
    private var _binding: FragmentAddNoteBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel

        binding.saveNoteFab.setOnClickListener {
            val title = binding.etNoteTitle.text.toString()
            val noteDesc = binding.etNoteDesc.text.toString()
            val lastEdited = Utils.getCurrentDateTime().toString()
            if (title.isNotEmpty() or noteDesc.isNotEmpty()) {
                val note = Note(
                    title = title,
                    noteDesc = noteDesc,
                    lastEdited = lastEdited
                )
                viewModel.saveNote(note)
                findNavController().navigate(AddNoteFragmentDirections.actionAddNoteFragmentToNotesListFragment())
            } else {
                Snackbar.make(requireView(), "Empty note can't be added!", Snackbar.LENGTH_SHORT).show()
                findNavController().navigate(AddNoteFragmentDirections.actionAddNoteFragmentToNotesListFragment())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}