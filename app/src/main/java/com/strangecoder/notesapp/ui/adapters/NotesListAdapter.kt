package com.strangecoder.notesapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.strangecoder.notesapp.databinding.ListItemNotesBinding
import com.strangecoder.notesapp.model.Note

class NotesListAdapter(
    private val interaction: Interaction
) :
    ListAdapter<Note, NotesListAdapter.NoteViewHolder>(NoteItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ListItemNotesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NoteViewHolder(binding, interaction)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder
     */
    inner class NoteViewHolder(
        private val binding: ListItemNotesBinding,
        private val interaction: Interaction
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note) {
            binding.note = note
            itemView.setOnClickListener {
                interaction.onItemClicked(it, note)
            }
        }
    }
}

interface Interaction {
    fun onItemClicked(view: View, note: Note)
}

class NoteItemDiffCallback : DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem.title == newItem.title && oldItem.noteDesc == newItem.noteDesc
    }

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem == newItem
    }
}