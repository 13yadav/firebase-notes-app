package com.strangecoder.notesapp.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.transition.TransitionManager
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialFadeThrough
import com.strangecoder.notesapp.MainActivity
import com.strangecoder.notesapp.R
import com.strangecoder.notesapp.databinding.FragmentNoteDetailBinding
import com.strangecoder.notesapp.model.Note
import com.strangecoder.notesapp.ui.MainViewModel
import com.strangecoder.notesapp.utils.Utils
import com.strangecoder.notesapp.utils.Utils.hide
import com.strangecoder.notesapp.utils.Utils.show


class NoteDetailFragment : Fragment() {
    private var _binding: FragmentNoteDetailBinding? = null
    private val binding get() = _binding!!
    private var menu: Menu? = null

    private lateinit var viewModel: MainViewModel
    private lateinit var noteItem: Note

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(resources.getColor(R.color.primary_color_dark))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteDetailBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel

        val args: NoteDetailFragmentArgs by navArgs()
        noteItem = args.noteItem

        noteItem.apply {
            binding.noteTitle.text = title
            binding.noteDesc.text = noteDesc
            binding.lastEditText.text = lastEdited
        }
        binding.updateNoteFab.setOnClickListener {
            showOverflowMenu(true)
            viewModel.updateNote(noteItem, getNewNoteMap())
            findNavController().navigate(NoteDetailFragmentDirections.actionNoteDetailFragmentToNotesListFragment())
        }
    }

    private fun enableEditing(note: Note) {
        binding.apply {
            etNoteTitle.apply {
                show()
                setText(note.title)
            }
            etNoteDesc.apply {
                show()
                setText(note.noteDesc)
            }
            noteTitle.hide()
            noteDesc.hide()
            lastEditText.hide()
            updateNoteFab.visibility = View.VISIBLE
        }
    }

    private fun getNewNoteMap(): Map<String, Any> {
        val title = binding.etNoteTitle.text.toString()
        val noteDesc = binding.etNoteDesc.text.toString()
        val lastEdited = Utils.getCurrentDateTime().toString()
        val map = mutableMapOf<String, Any>()
        if (title.isNotEmpty())
            map["title"] = title
        if (noteDesc.isNotEmpty())
            map["noteDesc"] = noteDesc
        map["lastEdited"] = lastEdited
        return map
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        this.menu = menu
        showOverflowMenu(true)
        inflater.inflate(R.menu.menu_detail_fragment, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionDelete -> {
                viewModel.deleteNote(noteItem)
                findNavController().navigate(NoteDetailFragmentDirections.actionNoteDetailFragmentToNotesListFragment())
                true
            }
            R.id.actionEdit -> {
                showOverflowMenu(false)
                val fadeThrough = MaterialFadeThrough()
                TransitionManager.beginDelayedTransition(binding.root, fadeThrough)
                enableEditing(noteItem)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showOverflowMenu(showMenu: Boolean) {
        if (menu == null) return
        menu!!.setGroupVisible(R.id.groupDetailMenu, showMenu)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}