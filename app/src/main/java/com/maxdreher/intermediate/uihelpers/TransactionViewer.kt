//package com.maxdreher.intermediate.uihelpers
//
//import android.content.Context
//import android.os.Bundle
//import android.widget.ListView
//import com.maxdreher.AmpGson
//import com.maxdreher.extensions.IContextBase
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.sync.Mutex
//import kotlinx.coroutines.sync.withLock
//import kotlinx.coroutines.withContext
//
//class TransactionViewer(
//    val listView: ListView,
//    private val cb: IContextBase,
//    private val getBatchOfTransactions: suspend (Int) -> List<Transaction>?,
//    onClick: (Transaction) -> Unit,
//) : IContextBase {
//
//    companion object {
//        const val DEFAULT_BATCH_SIZE = 10
//        private val cname = TransactionViewer::class.java.simpleName
//        val BUNDLE_ITEMS = "${cname}_ITEMS"
//        val BUNDLE_BATCH_COUNT = "${cname}_BATCH_COUNT"
//    }
//
//    private val adapter: DayOfTransactionAdapter
//
//    private var amountOfBatches = 0
//
//    private val mutex = Mutex()
//
//    init {
//        call(object {})
//        if (listView.adapter != null) {
//            throw ImproperTransactionViewerException("Adapter already configured for ListView!")
//        }
//
//        listView.adapter = DayOfTransactionAdapter(cb, ::loadNextBatch, onClick).also {
//            adapter = it
//        }
//    }
//
//    fun loadNextBatch() {
//        call(object {})
//
//        GlobalScope.launch {
//            mutex.withLock {
//                val nextBatch = getBatchOfTransactions(amountOfBatches)
//                if (nextBatch != null) {
//                    val hasItems = nextBatch.isNotEmpty()
//                    withContext(Dispatchers.Main) {
//                        adapter.showLoadMore = hasItems
//                        if (hasItems) {
//                            amountOfBatches++
//                            addBatch(nextBatch)
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    fun clear() {
//        call(object {})
//        adapter.clear()
//        amountOfBatches = 0
//    }
//
//    private fun addBatch(transactions: List<Transaction>) {
//        call(object {})
//
//        val existingItems = adapter.getItems().flatten()
//        val list = (transactions + existingItems).distinctBy { it.id }
//        val newGroups = list.toGroups()
//
//        adapter.clear()
//        adapter.addAll(newGroups)
//    }
//
//    private fun MutableList<Map.Entry<Temporal.Date, MutableList<Transaction>>?>.flatten(): List<Transaction> {
//        return map { it?.value ?: listOf() }.flatten()
//    }
//
//    private fun List<Transaction>.toGroups(): List<Map.Entry<Temporal.Date, MutableList<Transaction>>?> {
//        call(object {})
//        return groupByTo(mutableMapOf()) { t -> t.date }
//            .map { it }
//            .toList()
//            .sortedByDescending { pair -> pair.key }
//            .onEach { pair ->
//                pair.value.sortWith { t1, t2 -> t1.exactTime?.compareTo(t2?.exactTime) ?: 0 }
//            }
//    }
//
//    fun saveToBundle(outState: Bundle) {
//        outState.run {
//            putString(BUNDLE_ITEMS, itemJson)
//            putInt(BUNDLE_BATCH_COUNT, amountOfBatches)
//        }
//    }
//
//    fun loadFromBundle(savedInstanceState: Bundle) {
//        savedInstanceState.run {
//            val itemsString = getString(BUNDLE_ITEMS, null) ?: return
//            val batchInt = getInt(BUNDLE_BATCH_COUNT, -1).also {
//                if (it == -1) return
//            }
//
//            val items = AmpGson.deserialize().fromJson(
//                itemsString,
//                ListOfTransactions::class.java
//            ).list
//
//            amountOfBatches = batchInt
//            addBatch(items)
//        }
//    }
//
//    private val itemJson: String
//        get() = AmpGson.serialize()
//            .toJson(
//                ListOfTransactions(
//                    adapter.getItems().flatten().toMutableList()
//                )
//            )
//
//    override fun getContext(): Context? {
//        return cb.getContext()
//    }
//}