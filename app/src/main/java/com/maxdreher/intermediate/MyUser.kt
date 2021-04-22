package com.maxdreher.intermediate

import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.UserData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.maxdreher.extensions.IGoogleBaseBase

/**
 * Object to store current [User] and [UserData] to avoid excessive queries against
 * [IGoogleBaseBase.account] and [GoogleSignInAccount.getId]
 */
object MyUser {
    var user: User? = null
    var data: UserData? = null

    /**
     * Set [user] and [data] to null
     */
    fun setNull() {
        user = null
        data = null
    }
}
