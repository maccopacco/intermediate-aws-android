package com.maxdreher.intermediate

import com.amplifyframework.datastore.generated.model.Location
import com.amplifyframework.datastore.generated.model.PaymentMeta
import com.amplifyframework.datastore.generated.model.Transaction
import com.plaid.client.response.TransactionsGetResponse

class PlaidToAmp {
    companion object {
        fun convert(inputTransaction: TransactionsGetResponse.Transaction): Transaction {
            with(inputTransaction) {
                return Transaction.Builder()
                    .amount(amount)
                    .accountId(accountId)
                    .isoCurrencyCode(isoCurrencyCode)
                    .unofficialCurrencyCode(unofficialCurrencyCode)
                    .category(category)
                    .categoryId(categoryId)
                    .date(date)
                    .location(convert(location))
                    .merchantName(merchantName)
                    .name(name)
                    .originalDescription(originalDescription)
                    .paymentMeta(convert(paymentMeta))
                    .pending(pending)
                    .pendingTransactionId(pendingTransactionId)
                    .transactionId(transactionId)
                    .transactionType(transactionType)
                    .accountOwner(accountOwner)
                    .authorizedDate(authorizedDate)
                    .transactionCode(transactionCode)
                    .paymentChannel(paymentChannel).build()
            }
        }

        fun convert(inputLocation: TransactionsGetResponse.Transaction.Location): Location {
            with(inputLocation) {
                return Location.Builder()
                    .address(address)
                    .city(city)
                    .lat(lat)
                    .lon(lon)
                    .region(region)
                    .storeNumber(storeNumber)
                    .postalCode(postalCode)
                    .country(country).build()
            }
        }

        fun convert(inputPaymentMeta: TransactionsGetResponse.Transaction.PaymentMeta): PaymentMeta {
            with(inputPaymentMeta) {
                return PaymentMeta.Builder()
                    .byOrderOf(byOrderOf)
                    .payee(payee)
                    .payer(payer)
                    .paymentMethod(paymentMethod)
                    .paymentProcessor(paymentProcessor)
                    .ppdId(ppdId)
                    .reason(reason)
                    .referenceNumber(referenceNumber).build()
            }
        }
    }
}