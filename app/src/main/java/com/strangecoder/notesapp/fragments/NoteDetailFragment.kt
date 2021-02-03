package com.strangecoder.notesapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.strangecoder.notesapp.databinding.FragmentNoteDetailBinding

class NoteDetailFragment : Fragment() {
    private var _binding: FragmentNoteDetailBinding? = null
    private val binding get() = _binding!!

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
        val args: NoteDetailFragmentArgs by navArgs()
        args.noteItem.apply {
            binding.etNoteTitle.text = title
            binding.etNoteDesc.text = noteDesc
            binding.lastEditText.text = lastEdited
        }

        binding.editNoteFab.setOnClickListener {
            findNavController().navigate(NoteDetailFragmentDirections.actionNoteDetailFragmentToAddNoteFragment())
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}