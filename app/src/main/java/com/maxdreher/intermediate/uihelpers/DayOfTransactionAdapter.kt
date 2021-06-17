package com.maxdreher.intermediate.uihelpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.get
import androidx.core.view.size
import com.amplifyframework.core.model.temporal.Temporal
import com.amplifyframework.datastore.generated.model.Transaction
import com.maxdreher.ArrayAdapterBase
import com.maxdreher.extensions.IContextBase
import com.maxdreher.intermediate.*
import com.maxdreher.intermediate.ExtensionFunctions.Date.toView
import kotlinx.coroutines.*

class DayOfTransactionAdapter(
    private val cb: IContextBase,
    private val loadAnotherBatch: () -> Unit,
    private val onTransactionClick: (Transaction) -> Unit
) :
    ArrayAdapterBase<Map.Entry<Temporal.Date, MutableList<Transaction>>?>(
        cb.getContext()!!,
        R.layout.day_of_transactions
    ), IContextBase {

    var showLoadMore = true
        set(value) {
            if (field != value) {
                if (value) addNull()
                else remove(null)
                field = value
            }
        }

    override fun add(`object`: Map.Entry<Temporal.Date, MutableList<Transaction>>?) {
        call(object {})
        throw UnsupportedOperationException("Cannot add only one item")
    }

    override fun addAll(collection: Collection<Map.Entry<Temporal.Date, MutableList<Transaction>>?>) {
        call(object {})
        super.addAll(collection)
        if (showLoadMore) {
            addNull()
        }
        notifyDataSetChanged()
    }

    override fun addAll(vararg items: Map.Entry<Temporal.Date, MutableList<Transaction>>?) {
        call(object {})
        addAll(items.toCollection(mutableListOf()))
    }

    private fun addNull() {
        call(object {})
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                remove(null)
                super.add(null)
            }
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return super.getView(position, convertView, parent).apply {
            isClickable = false

            val item: Map.Entry<Temporal.Date, List<Transaction>>? = getItem(position)
            val date = findViewById<RelativeLayout>(R.id.date)
            val linearLayout = findViewById<LinearLayout>(R.id.transactions)
            val loadMore = findViewById<TextView>(R.id.load_more)

            fun setVis(visibility: Int) {
                listOf(date, linearLayout).forEach { it.visibility = visibility }
            }

            if (item == null) {
                setVis(View.GONE)
                loadMore.visibility = View.VISIBLE
                setupListener(loadMore)
                return@apply
            } else {
                loadMore.visibility = View.GONE
                setVis(View.VISIBLE)
            }

            val dateValue = item.key.toDate().toView()
            TransactionLoader.loadDate(date, dateValue)

            item.value.let { transactions ->
                for ((i, t) in transactions.withIndex()) {
                    val transactionView: View =
                        if (i < linearLayout.size) {
                            linearLayout[i]
                        } else {
                            LayoutInflater.from(context)
                                .inflate(R.layout.transaction, parent, false)
                                .also {
                                    linearLayout.addView(it)
                                }
                        }
                    TransactionLoader.loadTransaction(transactionView, t)
                    transactionView.setOnClickListener {
                        onTransactionClick(t)
                    }
                }
            }
        }
    }

    private fun setupListener(textView: TextView) {
        textView.setOnClickListener {
            loadAnotherBatch()
        }
    }
}