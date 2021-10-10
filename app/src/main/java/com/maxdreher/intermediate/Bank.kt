package com.maxdreher.intermediate

data class Bank(
    var institutionID: String? = null,
    var logo: String? = null,
    var name: String? = null,
) {
    override fun toString(): String {
        return "{name=$name}, {institutionID=$institutionID}"
    }

    enum class Fields(val value: String) {
        INSTITUTION_ID("institutionID"),
        LOGO("logo"),
        NAME("name"),
    }
}