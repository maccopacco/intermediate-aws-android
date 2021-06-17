package com.maxdreher.intermediate.uihelpers

import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.amplifyframework.datastore.generated.model.Transaction
import com.maxdreher.intermediate.ExtensionFunctions.Date.toTime
import com.maxdreher.intermediate.ExtensionFunctions.Date.toView
import com.maxdreher.intermediate.ExtensionFunctions.Models.amountAsString
import com.maxdreher.intermediate.ExtensionFunctions.Models.getCombinedName
import com.maxdreher.intermediate.R

object TransactionLoader {
    fun loadDate(view: RelativeLayout, transaction: Transaction) {
        loadDate(
            view,
            transaction.date?.toDate()?.toView(),
            transaction.exactTime?.toDate()?.toTime(),
        )
    }

    fun loadDate(view: RelativeLayout, date: String?, time: String? = null) {
        view.apply {
            findViewById<TextView>(R.id.date_value)?.text = date ?: "Unknown date"

            findViewById<TextView>(R.id.time_value)?.apply {
                time?.let {
                    text = time
                    visibility = View.VISIBLE
                } ?: kotlin.run {
                    visibility = View.GONE
                }
            }
        }
    }

    fun loadTransaction(
        v: View,
        t: Transaction
    ) {
        v.apply {
            findViewById<LinearLayout>(R.id.name_ll)?.findViewById<TextView>(R.id.name)?.apply {
                text = t.getCombinedName()
            }

            findViewById<TextView>(R.id.amount).apply {
                text = t.amountAsString()
                val color =
                    if (t.amount <= 0) R.color.transaction_credit
                    else R.color.transaction_debit
                setTextColor(context.resources.getColor(color))
            }
            findViewById<LinearLayout>(R.id.category_ll)?.findViewById<TextView>(R.id.category)
                ?.apply {
                    text = t.categoryFolder.let { folder ->
                        if (folder?.isNotEmpty() == true)
                            folder
                        else
                            t.category?.joinToString(",") ?: ""
                    }
                }

            findViewById<LinearLayout>(R.id.spent_from_ll)
                ?.findViewById<LinearLayout>(R.id.spent_from_value_ll)
                ?.findViewById<TextView>(R.id.spent_from_value)?.apply {
                    text = "Okay"
                }
            findViewById<TextView>(R.id.pending)?.apply {
                visibility =
                    if (t.pending) View.VISIBLE else View.INVISIBLE
            }
        }
    }

    fun wrapTitle(v: View) {
        v.findViewById<LinearLayout>(R.id.name_ll)?.findViewById<TextView>(R.id.name)
            ?.apply {
                maxLines = Integer.MAX_VALUE
            }
    }
}