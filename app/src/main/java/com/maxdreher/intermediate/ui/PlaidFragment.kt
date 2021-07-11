package com.maxdreher.intermediate.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import com.maxdreher.extensions.FragmentBase
import com.maxdreher.intermediate.databinding.FragmentPlaidBinding


class PlaidFragment :
    FragmentBase<FragmentPlaidBinding>(FragmentPlaidBinding::class.java),
    IPlaidBase {
    //
//    companion object {
//        private var data: Bundle? = null
//    }
//
    override val activity: ComponentActivity? = getActivity()
    override val resultCaller: ActivityResultCaller = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super<FragmentBase>.onCreate(savedInstanceState)
        super<IPlaidBase>.onCreate(savedInstanceState)
    }
//
//    private var viewer: TransactionViewer? = null
//
////    private var oldestSaveDate: Temporal.Date =
////        future()
////
////    private fun future() = (3 unit DAYS).fromNow().toAmplifyDate()
//
////    private val sort = listOf(
////        Transaction.DATE.descending(),
////        Transaction.EXACT_TIME.descending()
////    ).toTypedArray()
//
////    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
////        super<IPlaidBase>.onActivityResult(requestCode, resultCode, data)
////    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        viewer?.saveToBundle(outState)
//    }
//
//    override fun onStop() {
//        super.onStop()
//        data = viewer?.let { view ->
//            Bundle().also { bundle ->
//                view.saveToBundle(bundle)
//            }
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        data = null
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//
//        view.apply {
//            viewer = TransactionViewer(
//                listView = findViewById(R.id.plaid_view_listview),
//                cb = this@PlaidFragment,
//                getBatchOfTransactions = this@PlaidFragment::getTransactions,
//                onClick = { transaction ->
//                    findNavController().navigate(
//                        R.id.transactionFragment,
//                        bundleOf(
//                            "Transaction" to AmpGson.serialize(true)
//                                .toJson(transaction)
//                        )
//                    )
//                }
//            ).apply {
//                savedInstanceState?.let {
//                    loadFromBundle(it)
//                } ?: data?.let {
//                    loadFromBundle(it)
//                }
//            }
//            binding.addItems.setOnClickListener {
//                update()
//            }
//
//            binding.removeItems.setOnClickListener {
//                clear()
//            }
//        }
//    }
//
//
////    private suspend fun getTransactions(page: Int): List<Transaction>? {
////        call(object {})
////
////        return timeSuspend(getCallerSafe(object {})) {
////            MyUser.data?.let { data ->
////                log("Oldest save date: $oldestSaveDate")
////                Transaction::class.query(
////                    Where.matches(
////                        Transaction.USER_DATA.eq(data.id)
////                            .and(
////                                Transaction.DATE.lt(oldestSaveDate)
////                            )
////                    ).sorted(
////                        *sort
////                    ).paginated(
////                        Page.firstPage()
////                            .withLimit(TransactionViewer.DEFAULT_BATCH_SIZE)
////                    )
////                ).getOr {
////                    error("Could not get transactions\n${it.get()}")
////                }?.let { transactions ->
////                    if (transactions.isEmpty()) {
////                        log("No more transactions, not gonna double query")
////                        return@let transactions
////                    } else {
////                        log("${transactions.size} transactions found\n${transactions.joinToString("\n") { it.basicData() }}")
////                    }
////                    val comparator: (o1: Transaction, o2: Transaction) -> Int =
////                        { t1, t2 -> t1.date.compareTo(t2.date) }
////                    val min = transactions.minWithOrNull(comparator)!!.date
////                    val max = transactions.maxWithOrNull(comparator)!!.date
////                    log("Min: $min, Max: $max")
////
////                    Transaction::class.query(
////                        Where.matches(
////                            Transaction.USER_DATA.eq(data.id)
////                                .and(Transaction.DATE.between(min, max))
////                        )
////                    ).getOr {
////                        loge("Could not double query: ${it.get()}")
////                        TODO()
////                    }?.also {
////                        oldestSaveDate = min
////                    }
////                }
////            }
////        }
////    }
//
//    private fun clear() {
//        call(object {})
//        viewer?.clear()
//        oldestSaveDate = future()
//    }
//
//    private fun update() {
//        call(object {})
//        clear()
//        viewer?.loadNextBatch()
//    }
//
//    override fun onUserDataFound(bank: Bank) {
//        super.onUserDataFound(bank)
//        update()
//    }
}