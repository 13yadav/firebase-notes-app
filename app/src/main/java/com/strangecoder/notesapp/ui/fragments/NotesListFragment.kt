package com.strangecoder.notesapp.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialElevationScale
import com.google.firebase.auth.FirebaseAuth
import com.strangecoder.notesapp.MainActivity
import com.strangecoder.notesapp.R
import com.strangecoder.notesapp.databinding.FragmentNotesListBinding
import com.strangecoder.notesapp.model.Note
import com.strangecoder.notesapp.ui.MainViewModel
import com.strangecoder.notesapp.ui.adapters.Interaction
import com.strangecoder.notesapp.ui.adapters.NotesListAdapter

class NotesListFragment : Fragment(), Interaction {
    private var _binding: FragmentNotesListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: NotesListAdapter

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

        viewModel = (activity as MainActivity).viewModel

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        binding.addNewNoteFab.setOnClickListener {
            val addNoteTransitionName = getString(R.string.add_note_element)
            val extras = FragmentNavigatorExtras(it to addNoteTransitionName)
            val directions = NotesListFragmentDirections.actionNotesListFragmentToAddNoteFragment()
            findNavController().navigate(directions, extras)
        }
        adapter = NotesListAdapter(this)
        viewModel.getRealtimeNotes()
        viewModel.notesList.observe(viewLifecycleOwner, {
            binding.notesList.adapter = adapter
            adapter.submitList(it)
        })
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

    override fun onItemClicked(view: View, note: Note) {
        val noteCardDetailTransitionName = getString(R.string.note_detail_transition_name)
        val extras = FragmentNavigatorExtras(view to noteCardDetailTransitionName)
        val directions =
            NotesListFragmentDirections.actionNotesListFragmentToNoteDetailFragment(note)
        exitTransition = MaterialElevationScale(false).apply {
            duration = 300L
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = 300L
        }
        findNavController().navigate(directions, extras)
    }
}