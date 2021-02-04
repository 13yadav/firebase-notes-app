package com.strangecoder.notesapp.utils

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
}