package com.maxdreher.intermediate

import com.amplifyframework.datastore.generated.model.Bank
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.UserData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.maxdreher.extensions.IGoogleBaseBase
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Object to store current [User] and [UserData] to avoid excessive queries against
 * [IGoogleBaseBase.account] and [GoogleSignInAccount.getId]
 */
object MyUser {
    private val everOnline = AtomicBoolean(false)
    private val online = AtomicBoolean(false)

    var allowOfflineSignin = false
    var user: User? = null
    var bank: Bank? = null
    var data: UserData? = null

    /**
     * Set [user] and [data] to null
     */
    fun setNull() {
        user = null
        bank = null
        data = null
    }

    /**
     * @param online whether Amplify is now online
     * @return whether user is newly online for the first time
     */
    fun setOnline(online: Boolean): Boolean {
        val onlineBefore = everOnline.get()
        if (online) {
            everOnline.set(true)
        }
        return !onlineBefore && online
    }

    fun wasEverOnline(): Boolean {
        return everOnline.get()
    }

    fun isOnline(): Boolean {
        return online.get()
    }

}
