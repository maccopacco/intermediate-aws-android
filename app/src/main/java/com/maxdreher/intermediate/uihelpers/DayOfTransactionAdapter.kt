package com.maxdreher.intermediate.uihelpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.get
import androidx.core.view.size
import com.amplifyframework.datastore.generated.model.Transaction
import com.maxdreher.ArrayAdapterBase
import com.maxdreher.extensions.IContextBase
import com.maxdreher.intermediate.*

class DayOfTransactionAdapter(private val cb: IContextBase) :
    ArrayAdapterBase<Map.Entry<String, List<Transaction>>>(
        cb.getContext()!!,
        R.layout.day_of_transactions
    ) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return super.getView(position, convertView, parent).apply {
            isClickable = false

            val item = getItem(position)
            findViewById<TextView>(R.id.day_of_transaction_date).text =
                item?.key?.toAmpDate()?.toView()

            val linearLayout = findViewById<LinearLayout>(R.id.transactions)

            item?.value?.let { transactions ->
                for ((i, transaction) in transactions.withIndex()) {
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
                    updateTransaction(cb, transactionView, transaction)
                }
            }
        }
    }

    private fun updateTransaction(
        cb: IContextBase,
        v: View,
        t: Transaction
    ) {
        v.apply {
            setOnClickListener {
                cb.toast("Clicked ${t.getCombinedName()}")
            }
            findViewById<TextView>(R.id.name).apply {
                text = t.getCombinedName()
            }
            findViewById<TextView>(R.id.amount).apply {
                text = t.amountAsString()
                val color =
                    if (t.amount <= 0) R.color.transaction_credit
                    else R.color.transaction_debit
                setTextColor(context.resources.getColor(color))
            }
            findViewById<TextView>(R.id.category).apply {
                text = t.categoryFolder.let { folder ->
                    if (folder?.isNotEmpty() == true)
                        folder
                    else
                        t.category?.joinToString(",") ?: ""
                }
            }
            findViewById<TextView>(R.id.spent_from).apply {
//                text = t
            }
            findViewById<TextView>(R.id.pending).apply {
                visibility =
                    if (t.pending) View.VISIBLE else View.INVISIBLE
            }
        }
    }
}