package com.maxdreher.intermediate

import com.amplifyframework.datastore.DataStoreException
import com.amplifyframework.datastore.generated.model.*
import com.maxdreher.Util
import com.maxdreher.intermediate.util.IAccount
import com.plaid.client.response.Account
import com.plaid.client.response.TransactionsGetResponse
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/**
 * A file to store all extension functions
 */


fun Location.isEmpty(): Boolean {
    val values = listOf(address, city, lat, lon, postalCode).map { it?.toString() ?: "" }
    return !values.any { it != "" }
}

fun Transaction.getCombinedName(): String {
    return this.overrideName ?: this.name
}

fun Transaction.amountAsString(): String {
    return "$%4.2f".format(abs(amount))
}

fun UserData.getLastDate(): Date? {
    return oldestPendingTime.let { date ->
        if (date == null || date.isEmpty()) {
            Date(0)
        } else {
            date.toAmpDate()
        }
    }
}

fun Transaction.matches(
    plaid: TransactionsGetResponse.Transaction
): Boolean {
    return plaidId == plaid.transactionId
}

fun Transaction.basicData(): String {
    return "${this.plaidId} ${this.date} ${this.amount} ${this.getCombinedName()}"
}

fun TransactionsGetResponse.Transaction.basicData(): String {
    return "${this.transactionId} ${this.date} ${this.amount} ${this.name} ${this.merchantName}"
}

fun com.amplifyframework.datastore.generated.model.Account.toIAccount(): IAccount {
    return IAccount.from(name, plaidId)
}

fun com.plaid.client.response.Account.toIAccount(): IAccount {
    return IAccount.from(officialName, accountId)
}

fun DataStoreException.get(): String? {
    printStackTrace()
    return this.message
}

private val ampFormat = Util.simpleDateFormat
private val viewFormat = SimpleDateFormat("MMMM d")

fun Date.toAmpDate(): String {
    return ampFormat.format(this)
}

fun String.toAmpDate(): Date? {
    return try {
        ampFormat.parse(this)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun Date.toView(): String {
    return viewFormat.format(this)
}