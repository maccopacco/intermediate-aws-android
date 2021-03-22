package com.maxdreher.intermediate

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.maxdreher.Util
import com.maxdreher.consumers.ConsumeAndSupply
import com.maxdreher.intermediate.keys.Keys
import com.maxdreher.intermediate.util.Margin
import com.maxdreher.table.TableEntry
import com.maxdreher.table.TableHelper
import com.plaid.client.PlaidClient
import com.plaid.client.request.TransactionsGetRequest
import com.plaid.client.response.TransactionsGetResponse.Transaction
import de.codecrafters.tableview.SortableTableView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.TimeUnit


class PlaidFragment : Fragment(R.layout.fragment_plaid) {

    var plaidClient: PlaidClient = PlaidClient.newBuilder()
        .clientIdAndSecret(Keys.PLAID_CLIENT_ID, Keys.PLAID_SECRET)
        .developmentBaseUrl()
        .build()

    private lateinit var table: SortableTableView<Transaction>
    private lateinit var entries: List<TableEntry<Transaction>>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            table = findViewById(R.id.plaid_table)

            Util.buttonToAction(
                this,
                mapOf(R.id.plaid_back to R.id.action_plaidFragment_to_homeFragment)
            )

            entries = mapOf<String, ConsumeAndSupply<Transaction, String>>(
                "Date" to ConsumeAndSupply { it.date },
                "Name" to ConsumeAndSupply { it.name },
                "Amount" to ConsumeAndSupply { it.amount.toString() },
                "Pending" to ConsumeAndSupply { if (it.pending) "✓" else "x" }
            ).map {
                TableEntry(
                    it.key,
                    null,
                    TableEntry.textViewGenerator(
                        it.value,
                        Margin.get(context)
                    )
                )
            }

            findViewById<Button>(R.id.trigger_plaid).setOnClickListener {
                GlobalScope.launch {
                    val now = Date().time
                    val startDate = Date(now - TimeUnit.DAYS.toMillis(15))
                    val endDate = Date(now + TimeUnit.DAYS.toMillis(1))

                    val response = plaidClient.service()
                        .transactionsGet(
                            TransactionsGetRequest(Keys.MY_ACCESS_TOKEN, startDate, endDate)
                        ).execute()

                    withContext(Dispatchers.Main) {
                        response.body()?.transactions?.let {
                            TableHelper.updateTable(context, table, it, entries)
                        }
                    }
                }
            }
        }
    }
}