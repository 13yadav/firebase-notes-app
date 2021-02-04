package com.strangecoder.notesapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.toObject
import com.strangecoder.notesapp.R
import com.strangecoder.notesapp.databinding.FragmentNotesListBinding
import com.strangecoder.notesapp.model.Note
import com.strangecoder.notesapp.ui.adapters.Interaction
import com.strangecoder.notesapp.ui.adapters.NotesListAdapter
import com.strangecoder.notesapp.utils.FirebaseConfig

class NotesListFragment : Fragment(), Interaction {
    private var _binding: FragmentNotesListBinding? = null
    private val binding get() = _binding!!

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
        Log.d("rrLOG", "User ID: ${FirebaseConfig.getUID()}")
        FirebaseConfig.firestoreCollectionRef.collection(FirebaseConfig.getUID())
            .addSnapshotListener { querySnapshot, error ->
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