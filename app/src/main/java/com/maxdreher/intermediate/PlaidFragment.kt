package com.maxdreher.intermediate

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.maxdreher.intermediate.keys.Keys
import com.plaid.client.PlaidClient
import com.plaid.client.request.TransactionsGetRequest
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.TimeUnit


class PlaidFragment : Fragment(R.layout.fragment_plaid) {

    var plaidClient: PlaidClient = PlaidClient.newBuilder()
        .clientIdAndSecret(Keys.PLAID_CLIENT_ID, Keys.PLAID_SECRET)
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
                GlobalScope.launch {
                    val builder = StringBuilder()

                    val now = Date().time
                    val startDate = Date(now - TimeUnit.DAYS.toMillis(2))
                    val endDate = Date(now + TimeUnit.DAYS.toMillis(1))

                    val response = plaidClient.service()
                        .transactionsGet(
                            TransactionsGetRequest(
                                Keys.MY_ACCESS_TOKEN,
                                startDate,
                                endDate
                            )
                        ).execute()

                    response.body()?.transactions?.let { transactions ->
                        for (transaction in transactions) {
                            transaction.run {
                                builder.let { b ->
                                    b.append(date).append("\n")
                                    b.append(amount).append("\n")
                                }
                            }
                        }
                    } ?: builder.append("No bad transaction")

                    withContext(Dispatchers.Main) {
                        result.text = builder.toString()
                    }
                }
            }
        }
    }
}