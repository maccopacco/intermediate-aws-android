package com.maxdreher.intermediate.util

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FbEmulators {
    fun setup() {
        val s = "10.0.2.2"
//        FirebaseAuth.getInstance().useEmulator(s, 9099);
        Firebase.firestore.useEmulator(s, 8080)
    }
}
