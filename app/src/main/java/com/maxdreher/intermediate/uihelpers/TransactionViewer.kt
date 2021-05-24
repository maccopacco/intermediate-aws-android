package com.maxdreher.intermediate.uihelpers

import android.widget.ListView
import com.amplifyframework.datastore.generated.model.Transaction
import com.maxdreher.extensions.IContextBase

class TransactionViewer(
    val listView: ListView,
    val cb: IContextBase
) {
    private val adapter: DayOfTransactionAdapter

    init {
        if (listView.adapter != null) {
            throw ImproperTransactionViewerException("Adapter already configured for ListView!")
        }
        listView.adapter = DayOfTransactionAdapter(cb).also {
            adapter = it
        }
    }

    fun clear() {
        adapter.clear()
    }

    fun add(transactions: List<Transaction>) {
        val groupedByDate =
            transactions.groupByTo(HashMap<String, MutableList<Transaction>>()) { it.date }
                .map {
                    it
                }.toList()
                .sortedByDescending { it.key }
                .onEach {
                    it.value.sortBy { it.exactTime ?: "" }
                }.toList()
        adapter.addAll(groupedByDate)
    }
}