package com.strangecoder.notesapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Note(
    val title: String = "",
    val noteDesc: String = "",
    val lastEdited: String = ""
) : Parcelable
