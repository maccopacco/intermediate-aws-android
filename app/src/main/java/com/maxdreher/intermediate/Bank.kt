package com.maxdreher.intermediate

import com.google.gson.Gson

class Bank(
    var institutionID: String?,
    var logo: String?,
    var name: String?,
    var populated: Boolean?,
    var token: String?,
    var user: String?
) {
    override fun toString(): String {
        return "id: $institutionID" +
//                "\nlogo: $logo" +
                "\nname: $name" +
                "\npopuylated: $populated"
//                "\ntoken: $token" +
//                "\nuser: $user"
    }

    constructor() : this(
        null, null, null,
        null, null, null
    )
}