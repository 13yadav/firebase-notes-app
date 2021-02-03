package com.strangecoder.notesapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.strangecoder.notesapp.R
import com.strangecoder.notesapp.Utils
import com.strangecoder.notesapp.databinding.FragmentAddNoteBinding
import com.strangecoder.notesapp.model.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AddNoteFragment : Fragment() {
    private var _binding: FragmentAddNoteBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var uid: String
    private val firestoreCollectionRef = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        uid = auth.currentUser?.uid.toString()
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
            firestoreCollectionRef.collection(uid).add(note).await()
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