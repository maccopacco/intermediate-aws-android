package com.maxdreher.intermediate

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.maxdreher.extensions.IGoogleBaseBase

/**
 * Object to store current [User] and [UserData] to avoid excessive queries against
 * [IGoogleBaseBase.account] and [GoogleSignInAccount.getId]
 */
object MyUser {
    var importLimit: Int? = null
}
