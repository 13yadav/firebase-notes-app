package com.strangecoder.notesapp.ui

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.strangecoder.notesapp.model.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainViewModel(
    application: Application
) : AndroidViewModel(application) {

    val firebaseAuth = Firebase.auth

    private val firestoreCollectionRef = Firebase.firestore

    private fun getUID(): String {
        return firebaseAuth.currentUser?.uid.toString()
    }

    private val _notesList = MutableLiveData<List<Note>>()
    val notesList: LiveData<List<Note>> get() = _notesList

    fun getRealtimeNotes() {
        firestoreCollectionRef
            .collection(getUID())
            .addSnapshotListener { querySnapshot, error ->
                error?.let {
                    Toast.makeText(getApplication(), error.message, Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }
                querySnapshot?.let { collection ->
                    val notesList = mutableListOf<Note>()
                    for (document in collection.documents) {
                        Log.d("rrLOG", document.id)
                        val note = document.toObject<Note>()
                        note?.let {
                            notesList.add(it)
                        }
                    }
                    _notesList.value = notesList
                }
            }
    }

    fun saveNote(note: Note) = CoroutineScope(Dispatchers.IO).launch {
        try {
            firestoreCollectionRef.collection(getUID()).add(note)
                .await()
            withContext(Dispatchers.Main) {
                Toast.makeText(getApplication(), "Note Added Successfully", Toast.LENGTH_LONG)
                    .show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(getApplication(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun updateNote(note: Note, newNoteMap: Map<String, Any>) =
        CoroutineScope(Dispatchers.IO).launch {
            val noteQuery = queryNoteForId(note)
            if (noteQuery.documents.isNotEmpty()) {
                for (document in noteQuery) {
                    try {
                        firestoreCollectionRef.collection(getUID())
                            .document(document.id)
                            .set(newNoteMap, SetOptions.merge()).await()
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(getApplication(), e.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(getApplication(), "No such note exists", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }

    fun deleteNote(note: Note) =
        CoroutineScope(Dispatchers.IO).launch {
            val noteQuery = queryNoteForId(note)
            if (noteQuery.documents.isNotEmpty()) {
                for (document in noteQuery) {
                    try {
                        firestoreCollectionRef.collection(getUID())
                            .document(document.id)
                            .delete().await()
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(getApplication(), e.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(getApplication(), "No such note exists", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }

    private suspend fun queryNoteForId(note: Note): QuerySnapshot {
        return firestoreCollectionRef.collection(getUID())
            .whereEqualTo("title", note.title)
            .whereEqualTo("noteDesc", note.noteDesc)
            .whereEqualTo("lastEdited", note.lastEdited)
            .get()
            .await()
    }

}

class ViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}