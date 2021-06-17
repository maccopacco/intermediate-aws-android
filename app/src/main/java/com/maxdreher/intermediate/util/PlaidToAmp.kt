package com.maxdreher.intermediate.util

import com.amplifyframework.core.model.Model
import com.amplifyframework.core.model.temporal.Temporal
import com.amplifyframework.datastore.generated.model.*
import com.maxdreher.Util.Date.toAmplifyDate
import com.maxdreher.extensions.IContextBase
import com.maxdreher.intermediate.ExtensionFunctions.Models.isEmpty
import com.plaid.client.response.TransactionsGetResponse
import java.text.SimpleDateFormat

/**
 * Helper functions to convert [TransactionsGetResponse.Transaction] from Plaid API to
 * [List] of [Model]s for save later
 */
object PlaidToAmp {
    private val plaidDateFormat
        get() = SimpleDateFormat("yyyy-MM-dd")

    private fun convertDate(text: String): Temporal.Date {
        //plaid is already in UTC
        return plaidDateFormat.parse(text).toAmplifyDate(offset = 0)
    }

    fun convert(
        inputTransaction: TransactionsGetResponse.Transaction,
        account: Account,
        userData: UserData,
    ): List<Model> {
        with(inputTransaction) {
            return mutableListOf<Model>().apply {
                Transaction.builder()
                    .userData(userData)
                    .account(account.plaidId)
                    .amount(amount)
                    .plaidId(transactionId)
                    .isoCurrencyCode(isoCurrencyCode)
                    .unofficialCurrencyCode(unofficialCurrencyCode)
                    .category(category)
                    .categoryId(categoryId)
                    .date(convertDate(date))
//                    .exactTime(convertTime)
                    .merchantName(merchantName)
                    .name(name)
                    .originalDescription(originalDescription)
                    .pending(pending)
                    .pendingTransactionId(pendingTransactionId)
                    .transactionId(transactionId)
                    .transactionType(transactionType)
                    .accountOwner(accountOwner)
                    .authorizedDate(authorizedDate)
                    .transactionCode(transactionCode)
                    .paymentChannel(paymentChannel)
                    .build().also { trans ->
                        add(trans)
                        convert(location, trans)?.also { l -> add(l) }
                        convert(paymentMeta, trans)?.also { p -> add(p) }
                    }
            }
        }
    }

    private fun convert(
        inputLocation: TransactionsGetResponse.Transaction.Location,
        transaction: Transaction
    ): Location? {
        with(inputLocation) {
            val l = Location.Builder()
                .transaction(transaction)
                .address(address)
                .city(city)
                .lat(lat)
                .lon(lon)
                .region(region)
                .storeNumber(storeNumber)
                .postalCode(postalCode)
                .country(country)
                .build()
            return if (l.isEmpty()) null else l
        }
    }

    private fun convert(
        inputPaymentMeta: TransactionsGetResponse.Transaction.PaymentMeta,
        transaction: Transaction
    ): PaymentMeta? {
        with(inputPaymentMeta) {
            val p = PaymentMeta.Builder()
                .transaction(transaction)
                .byOrderOf(byOrderOf)
                .payee(payee)
                .payer(payer)
                .paymentMethod(paymentMethod)
                .paymentProcessor(paymentProcessor)
                .ppdId(ppdId)
                .reason(reason)
                .referenceNumber(referenceNumber)
                .build()
            return if (p.isEmpty()) null else p
        }
    }
}