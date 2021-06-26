package com.maxdreher.intermediate.util

import com.plaid.client.PlaidApiService
import com.plaid.client.request.TransactionsGetRequest
import com.plaid.client.response.TransactionsGetResponse
import retrofit2.Response

object PlaidPaginator {

    suspend fun PlaidApiService.withPagination(
        accessToken: String,
        startDate: Date,
        endDate: Date
    ) {
        withPagination(TransactionsGetRequest(accessToken, startDate, endDate))
    }

    suspend fun PlaidApiService.withPagination(transactionsGetRequest: TransactionsGetRequest): Response<TransactionsGetResponse> {
        return transactionsGet(transactionsGetRequest).execute()
    }
}