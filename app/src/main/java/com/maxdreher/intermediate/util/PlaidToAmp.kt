package com.maxdreher.intermediate.util

import com.amplifyframework.core.model.Model
import com.amplifyframework.datastore.generated.model.*
import com.maxdreher.Util
import com.maxdreher.intermediate.toAmpDate
import com.plaid.client.response.TransactionsGetResponse
import java.text.SimpleDateFormat

/**
 * Helper functions to convert [TransactionsGetResponse.Transaction] from Plaid API to
 * [List] of [Model]s for save later
 */
object PlaidToAmp {
    private val plaidDateFormat = SimpleDateFormat("yyyy-MM-dd")

    fun convertDate(text: String): String {
        return plaidDateFormat.parse(text).toAmpDate()
    }

    fun convert(
        inputTransaction: TransactionsGetResponse.Transaction,
        userData: UserData
    ): List<Model> {
        with(inputTransaction) {
            return mutableListOf<Model>().apply {
                TransactionWrapper.builder()
                    .userData(userData)
                    .transaction(Transaction.Builder()
                        .amount(amount)
                        .accountId(accountId)
                        .isoCurrencyCode(isoCurrencyCode)
                        .unofficialCurrencyCode(unofficialCurrencyCode)
                        .category(category)
                        .categoryId(categoryId)
                        .date(convertDate(date))
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
                        .build().also {
                            add(it)
                            add(convert(location, it))
                            add(convert(paymentMeta, it))
                        })
                    .plaidId(transactionId)
                    .build().also {
                        add(it)
                    }
            }
        }
    }

    private fun convert(
        inputLocation: TransactionsGetResponse.Transaction.Location,
        transaction: Transaction
    ): Location {
        with(inputLocation) {
            return Location.Builder()
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
        }
    }

    private fun convert(
        inputPaymentMeta: TransactionsGetResponse.Transaction.PaymentMeta,
        transaction: Transaction
    ): PaymentMeta {
        with(inputPaymentMeta) {
            return PaymentMeta.Builder()
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
        }
    }
}