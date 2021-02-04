package com.strangecoder.notesapp.utils

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirebaseConfig {

    val firebaseAuth = Firebase.auth

    val firestoreCollectionRef = Firebase.firestore

    fun getUID(): String {
        return firebaseAuth.currentUser?.uid.toString()
    }
}