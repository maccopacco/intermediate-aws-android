package com.maxdreher.intermediate.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.amplifyframework.datastore.generated.model.Bank
import com.amplifyframework.datastore.generated.model.TransactionWrapper
import com.maxdreher.Util
import com.maxdreher.extensions.FragmentBase
import com.maxdreher.intermediate.MyUser
import com.maxdreher.intermediate.R
import com.maxdreher.intermediate.getCombinedName
import com.maxdreher.intermediate.util.Margin
import com.maxdreher.query
import com.maxdreher.table.TableEntry
import com.maxdreher.table.TableHelper
import de.codecrafters.tableview.SortableTableView


class PlaidFragment : FragmentBase(R.layout.fragment_plaid), IPlaidBase {


    private lateinit var table: SortableTableView<TransactionWrapper>

    private lateinit var entries: List<TableEntry<TransactionWrapper>>

    override val activity: Activity
        get() = requireActivity()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super<IPlaidBase>.onActivityResult(requestCode, resultCode, data)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            table = findViewById(R.id.plaid_table)

            entries = TableEntry.from(
                Margin.get(context),
                mapOf(
                    "Cleared" to { if (it.transaction.pending) "✘" else "✓" },
//                    "Category" to { it.transaction.category?.joinToString(",") ?: "" },
                    "Date" to { it.transaction.date },
//                    "Name Real" to { it.transaction.name },
                    "Name" to { it.getCombinedName() ?: "" },
                    "Amount" to { it.transaction.amount.toString() },
                )
            )

            Util.buttonToListener(
                this, mapOf(
                    R.id.trigger_plaid to { update() },
                    R.id.trigger_link to {
//                        triggerLink()
                    }
                )
            )
        }
    }

    override fun onUserDataFound(bank: Bank) {
        super.onUserDataFound(bank)
        update()
    }


    private fun update() {
        log("Updating")
        MyUser.data?.let { data ->
            TransactionWrapper::class.query(
                TransactionWrapper.USER_DATA.eq(data.id),
                { updateTable(it) }, { alert("Whoops", "No transactions found :(") }
            )
        } ?: toast("No data!")
    }

    private fun updateTable(list: List<TransactionWrapper>) {
        TableHelper.updateTable(requireContext(), table, list, entries)
    }
}