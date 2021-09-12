package com.maxdreher.intermediate

data class Bank(
    var userId: String? = null,
    var institutionID: String? = null,
    var logo: String? = null,
    var name: String? = null,
    var populated: Boolean? = null,
    var token: String? = null,
) {
    override fun toString(): String {
        return "{name=$name}, {institutionID=$institutionID}, {populated=$populated}"
    }

    enum class Fields(val value: String) {
        USER_ID("userId"),
        INSTITUTION_ID("institutionID"),
        LOGO("logo"),
        NAME("name"),
        POPULATED("populated"),
        TOKEN("token")
    }
}