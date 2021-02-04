package com.strangecoder.notesapp.utils

import android.view.View
import android.widget.EditText
import android.widget.TextView
import java.util.*

object Utils {

    fun getRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    fun TextView.hide() {
        visibility = View.GONE
    }

    fun TextView.show() {
        visibility = View.VISIBLE
    }

    fun EditText.hide() {
        visibility = View.GONE
    }

    fun EditText.show() {
        visibility = View.VISIBLE
    }
}