package com.strangecoder.notesapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialContainerTransform
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

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = 300L
            setAllContainerColors(resources.getColor(R.color.primary_color_dark))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel

        val args: NoteDetailFragmentArgs by navArgs()
        val noteItem = args.noteItem

        noteItem.apply {
            binding.noteTitle.text = title
            binding.noteDesc.text = noteDesc
            binding.lastEditText.text = lastEdited
        }

        binding.noteTitle.setOnClickListener {
            enableEditing(noteItem)
        }
        binding.noteDesc.setOnClickListener {
            enableEditing(noteItem)
        }
        binding.updateNoteFab.setOnClickListener {
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}