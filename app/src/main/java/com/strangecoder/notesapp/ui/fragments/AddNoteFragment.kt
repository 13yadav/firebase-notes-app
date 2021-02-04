package com.strangecoder.notesapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.strangecoder.notesapp.databinding.FragmentAddNoteBinding
import com.strangecoder.notesapp.model.Note
import com.strangecoder.notesapp.utils.FirebaseConfig
import com.strangecoder.notesapp.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AddNoteFragment : Fragment() {
    private var _binding: FragmentAddNoteBinding? = null
    private val binding get() = _binding!!

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
                saveNote(note)
                findNavController().navigate(AddNoteFragmentDirections.actionAddNoteFragmentToNotesListFragment())
            } else {
                Toast.makeText(requireContext(), "Fields can't be empty!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun saveNote(note: Note) = CoroutineScope(Dispatchers.IO).launch {
        try {
            FirebaseConfig.firestoreCollectionRef.collection(FirebaseConfig.getUID()).add(note).await()
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Note Added Successfully", Toast.LENGTH_LONG)
                    .show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}