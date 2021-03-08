package com.maxdreher.intermediate

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.plaid.client.PlaidClient
import com.plaid.client.request.AccountsBalanceGetRequest
import com.plaid.client.response.AccountsGetResponse
import retrofit2.Response


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class PlaidFragment : Fragment(R.layout.fragment_plaid) {

//    private val plaidClient = ApiClient(
//        mapOf(
//            "clientId" to "summ",
//            "secret" to "summ else",
//            "plaidVersion" to "2020-09-14"
//        )
//    ).apply { setPlaidAdapter(ApiClient.Development) }.createService(PlaidApi::class.java)

    var plaidClient = PlaidClient.newBuilder()
        .clientIdAndSecret("nah", "nope")
        .developmentBaseUrl()
        .build()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            val result = findViewById<TextView>(R.id.plaid_result)
            findViewById<Button>(R.id.back).setOnClickListener {
                findNavController().navigate(R.id.action_plaidFragment_to_homeFragment)
            }
            findViewById<Button>(R.id.trigger_plaid).setOnClickListener {
//                val accessToken =
//                    AccountsBalanceGetRequest().accessToken("bad_boy_dont_post_the_dang_keys")
//                val response: Response<AccountsGetResponse> =
//                    plaidClient.accountsBalanceGet(accessToken).execute()
//
//                result.text = if (response.isSuccessful) response.body().toString() else "Error?"
            }
        }
    }
}