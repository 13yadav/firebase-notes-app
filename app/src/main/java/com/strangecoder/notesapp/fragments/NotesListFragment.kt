package com.strangecoder.notesapp.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.strangecoder.notesapp.R
import com.strangecoder.notesapp.adapters.Interaction
import com.strangecoder.notesapp.adapters.NotesListAdapter
import com.strangecoder.notesapp.databinding.FragmentNotesListBinding
import com.strangecoder.notesapp.model.Note

class NotesListFragment : Fragment(), Interaction {
    private var _binding: FragmentNotesListBinding? = null
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
        _binding = FragmentNotesListBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addNewNoteFab.setOnClickListener {
            findNavController().navigate(NotesListFragmentDirections.actionNotesListFragmentToAddNoteFragment())
        }
        getRealtimeNotes()
    }

    private fun getRealtimeNotes() {
        firestoreCollectionRef.collection(uid).addSnapshotListener { querySnapshot, error ->
            error?.let {
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            querySnapshot?.let { collection ->
                val notesList = mutableListOf<Note>()
                for (document in collection.documents) {
                    val note = document.toObject<Note>()
                    note?.let {
                        notesList.add(it)
                    }
                }
                initListAdapter(notesList)
            }
        }
    }

    private fun initListAdapter(notesList: MutableList<Note>) {
        val adapter = NotesListAdapter(this)
        binding.notesList.adapter = adapter
        adapter.submitList(notesList)
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            findNavController().navigate(NotesListFragmentDirections.actionNotesListFragmentToLoginFragment())
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionLogout -> {
                FirebaseAuth.getInstance().signOut()
                findNavController().navigate(NotesListFragmentDirections.actionNotesListFragmentToLoginFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onItemClicked(position: Int, note: Note) {
        findNavController().navigate(
            NotesListFragmentDirections.actionNotesListFragmentToNoteDetailFragment(note)
        )
    }
}