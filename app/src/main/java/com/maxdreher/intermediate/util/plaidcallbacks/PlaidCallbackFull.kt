package com.maxdreher.intermediate.util.plaidcallbacks

import com.plaid.client.response.BaseResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class PlaidCallbackFull<ReturnType : BaseResponse>
    (
    private val onResponse: (Call<ReturnType>, Response<ReturnType>) -> Unit,
    private val onFailure: (Call<ReturnType>, Throwable) -> Unit
) :
    Callback<ReturnType> {

    override fun onResponse(call: Call<ReturnType>, response: Response<ReturnType>) {
        onResponse.invoke(call, response)
    }

    override fun onFailure(call: Call<ReturnType>, t: Throwable) {
        onFailure.invoke(call, t)
    }
}