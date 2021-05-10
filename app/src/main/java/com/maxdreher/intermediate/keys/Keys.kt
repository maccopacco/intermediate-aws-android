package com.maxdreher.intermediate.keys

import java.lang.Exception

object Keys {
    private val e = Exception("No API Keys")

    val PLAID_CLIENT_ID: String
        get() = throw e
    val PLAID_SECRET: String
        get() = throw e
    val MY_ACCESS_TOKENS: List<InstituteAndID>
        get() = throw e

    class InstituteAndID(val ins: String, val id: String)
}