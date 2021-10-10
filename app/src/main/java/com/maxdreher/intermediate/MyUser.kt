package com.maxdreher.intermediate

import com.google.firebase.auth.FirebaseAuth

object MyUser {
    var importLimit: Int? = null

    fun getUserUid() = FirebaseAuth.getInstance().currentUser?.uid

    var selectedBank: Bank? = null
}
