package com.maxdreher.intermediate.util.plaidcallbacks

import com.plaid.client.response.BaseResponse
import retrofit2.Response

class PlaidCallback<ReturnType : BaseResponse>(
    onResponse: (Response<ReturnType>) -> Unit,
    onFailure: (Throwable) -> Unit
) :
    PlaidCallbackFull<ReturnType>({ _, r ->
        onResponse.invoke(r)
    }, { _, t ->
        onFailure.invoke(t)
    }) {

}