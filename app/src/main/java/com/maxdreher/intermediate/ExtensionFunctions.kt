package com.maxdreher.intermediate

import com.amplifyframework.datastore.generated.model.*
import com.maxdreher.Util
import com.maxdreher.extensions.IContextBase
import com.maxdreher.intermediate.util.IAccount
import com.plaid.client.response.TransactionsGetResponse
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/**
 * A file to store all extension functions
 */

object ExtensionFunctions {

    fun IContextBase.defaultMargin(): Int {
        return Util.getDefaultMargin(getContext(), R.dimen.default_margin)
    }

    object Models {
        fun PaymentMeta.isEmpty(): Boolean {
            return lacking(
                payee,
                payer,
                paymentMethod,
                paymentProcessor,
                ppdId,
                reason,
                referenceNumber
            )
        }

        fun Location.isEmpty(): Boolean {
            return lacking(address, city, lat, lon, postalCode)
        }

        private fun lacking(vararg items: Any?): Boolean {
            val values = items.map { it?.toString() ?: "" }
            return values.all { it == "" }
        }

        fun Transaction.getCombinedName(): String {
            return overrideName ?: name
        }

        fun Account.getCombinedName(): String {
            return overrideName ?: name
        }

        fun Transaction.amountAsString(): String {
            return "$%4.2f".format(abs(amount))
        }

        fun UserData.getLastDate(): java.util.Date {
            return oldestPendingDate?.toDate() ?: Date(0)
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

        fun Account.toIAccount(): IAccount {
            return IAccount.from(name, plaidId)
        }

        fun com.plaid.client.response.Account.toIAccount(): IAccount {
            return IAccount.from(officialName, accountId)
        }
    }

    object Date {
        private val timeFormat = SimpleDateFormat("h:mm a")
        private val viewFormat = SimpleDateFormat("MMMM d")


        fun java.util.Date.toView(): String {
            return viewFormat.format(this)
        }

        fun java.util.Date.toTime(): String {
            return timeFormat.format(this)
        }
    }
}