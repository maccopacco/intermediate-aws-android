package com.maxdreher.intermediate.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.amplifyframework.core.model.query.Page
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.core.model.temporal.Temporal
import com.amplifyframework.datastore.generated.model.Bank
import com.amplifyframework.datastore.generated.model.Transaction
import com.maxdreher.AmpGson
import com.maxdreher.Util.Date.toAmplifyDate
import com.maxdreher.Util.Date.unit
import com.maxdreher.Util.buttonToListener
import com.maxdreher.Util.get
import com.maxdreher.extensions.FragmentBase
import com.maxdreher.intermediate.ExtensionFunctions.Models.basicData
import com.maxdreher.intermediate.MyUser
import com.maxdreher.intermediate.R
import com.maxdreher.intermediate.uihelpers.TransactionViewer
import com.maxdreher.query
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.TimeUnit.*


class PlaidFragment : FragmentBase(R.layout.fragment_plaid), IPlaidBase {

    companion object {
        private var data: Bundle? = null
    }

    override val activity: Activity?
        get() = getActivity()

    private var viewer: TransactionViewer? = null

    private var oldestSaveDate: Temporal.Date =
        future()

    private fun future() = (3 unit DAYS).fromNow().toAmplifyDate()

    private val sort = listOf(
        Transaction.DATE.descending(),
        Transaction.EXACT_TIME.descending()
    ).toTypedArray()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super<IPlaidBase>.onActivityResult(requestCode, resultCode, data)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewer?.saveToBundle(outState)
    }

    override fun onStop() {
        super.onStop()
        data = viewer?.let { view ->
            Bundle().also { bundle ->
                view.saveToBundle(bundle)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        data = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            viewer = TransactionViewer(
                listView = findViewById(R.id.plaid_view_listview),
                cb = this@PlaidFragment,
                getBatchOfTransactions = this@PlaidFragment::getTransactions,
                onClick = { transaction ->
                    findNavController().navigate(
                        R.id.transactionFragment,
                        bundleOf(
                            "Transaction" to AmpGson.serialize(true)
                                .toJson(transaction)
                        )
                    )
                }
            ).apply {
                savedInstanceState?.let {
                    loadFromBundle(it)
                } ?: data?.let {
                    loadFromBundle(it)
                }
            }

            buttonToListener(
                R.id.add_items to {
                    update()
                },
                R.id.remove_items to {
                    clear()
                }
            )
        }
    }


    private suspend fun getTransactions(page: Int): List<Transaction>? {
        call(object {})

        return timeSuspend(getCallerSafe(object {})) {
            MyUser.data?.let { data ->
                log("Oldest save date: $oldestSaveDate")
                Transaction::class.query(
                    Where.matches(
                        Transaction.USER_DATA.eq(data.id)
                            .and(
                                Transaction.DATE.lt(oldestSaveDate)
                            )
                    ).sorted(
                        *sort
                    ).paginated(
                        Page.firstPage()
                            .withLimit(TransactionViewer.DEFAULT_BATCH_SIZE)
                    )
                ).getOr {
                    error("Could not get transactions\n${it.get()}")
                }?.let { transactions ->
                    if (transactions.isEmpty()) {
                        log("No more transactions, not gonna double query")
                        return@let transactions
                    } else {
                        log("${transactions.size} transactions found\n${transactions.joinToString("\n") { it.basicData() }}")
                    }
                    val comparator: (o1: Transaction, o2: Transaction) -> Int =
                        { t1, t2 -> t1.date.compareTo(t2.date) }
                    val min = transactions.minWithOrNull(comparator)!!.date
                    val max = transactions.maxWithOrNull(comparator)!!.date
                    log("Min: $min, Max: $max")

                    Transaction::class.query(
                        Where.matches(
                            Transaction.USER_DATA.eq(data.id)
                                .and(Transaction.DATE.between(min, max))
                        )
                    ).getOr {
                        loge("Could not double query: ${it.get()}")
                        TODO()
                    }?.also {
                        oldestSaveDate = min
                    }
                }
            }
        }
    }

    private fun clear() {
        call(object {})
        viewer?.clear()
        oldestSaveDate = future()
    }

    private fun update() {
        call(object {})
        clear()
        viewer?.loadNextBatch()
    }

    override fun onUserDataFound(bank: Bank) {
        super.onUserDataFound(bank)
        update()
    }
}