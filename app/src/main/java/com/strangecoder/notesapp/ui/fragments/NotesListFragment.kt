package com.strangecoder.notesapp.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialSharedAxis
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
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
        postponeEnterTransition()
        view.doOnPreDraw {
            startPostponedEnterTransition()
        }

        viewModel = (activity as MainActivity).viewModel

        binding.addNewNoteFab.setOnClickListener {
            val directions = NotesListFragmentDirections.actionNotesListFragmentToAddNoteFragment()
            exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
            reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
            findNavController().navigate(directions)
        }
        adapter = NotesListAdapter(this)
        viewModel.getRealtimeNotes()
        viewModel.notesList.observe(viewLifecycleOwner) {
            binding.notesList.adapter = adapter
            adapter.submitList(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_list_fragment, menu)
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
        exitTransition = MaterialElevationScale(false)
        reenterTransition = MaterialElevationScale(true)
        findNavController().navigate(directions, extras)
    }
}