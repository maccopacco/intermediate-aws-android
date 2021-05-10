package com.maxdreher.intermediate

import com.amplifyframework.datastore.generated.model.*
import com.maxdreher.Util
import com.maxdreher.extensions.IContextBase
import com.maxdreher.intermediate.ui.IPlaidBase
import com.maxdreher.intermediate.util.PlaidToAmp
import com.plaid.client.response.TransactionsGetResponse
import java.text.SimpleDateFormat
import java.util.*

/**
 * A file to store all extension functions
 */
fun Location.isEmpty(): Boolean {
    val values = listOf(address, city, lat, lon, postalCode).map { it?.toString() ?: "" }
    return !values.any { it != "" }
}

fun TransactionWrapper.getCombinedName(): String? {
    return this.overrideName ?: this.transaction.name
}

fun TransactionWrapper.matches(
    plaid: TransactionsGetResponse.Transaction
): Boolean {
    return plaidId == plaid.transactionId
}

fun TransactionWrapper.basicData(): String {
    return "${this.plaidId} ${this.transaction.date} ${this.transaction.amount} ${this.getCombinedName()}"
}

fun TransactionsGetResponse.Transaction.basicData(): String {
    return "${this.transactionId} ${this.date} ${this.amount} ${this.name} ${this.merchantName}"
}

private fun ampFormat(): SimpleDateFormat {
    return Util.simpleDateFormat
}

fun Date.toAmpDate(): String {
    return ampFormat().format(this)
}

fun String.toAmpDate(): Date? {
    return try {
        ampFormat().parse(this)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}