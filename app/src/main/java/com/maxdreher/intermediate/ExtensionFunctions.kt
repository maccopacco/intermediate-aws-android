package com.maxdreher.intermediate

import com.amplifyframework.datastore.generated.model.Location

/**
 * A file to store all extension functions
 */

fun Location.isEmpty(): Boolean {
    val values = listOf(address, city, lat, lon, postalCode).map { it?.toString() ?: "" }
    return !values.any { it != "" }
}