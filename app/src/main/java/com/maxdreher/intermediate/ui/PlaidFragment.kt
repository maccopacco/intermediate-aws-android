package com.maxdreher.intermediate.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ListView
import com.amplifyframework.core.model.query.Page
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.datastore.generated.model.Bank
import com.amplifyframework.datastore.generated.model.Transaction
import com.maxdreher.Util
import com.maxdreher.extensions.FragmentBase
import com.maxdreher.intermediate.MyUser
import com.maxdreher.intermediate.R
import com.maxdreher.query


class PlaidFragment : FragmentBase(R.layout.fragment_plaid), IPlaidBase {


    override val activity: Activity?
        get() = getActivity()

    private var listView: ListView? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super<IPlaidBase>.onActivityResult(requestCode, resultCode, data)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            listView = findViewById<ListView>(R.id.plaid_view_listview)?.apply {
                adapter = DayOfTransactionAdapter(this@PlaidFragment)
            }

            Util.buttonToListener(
                this, mapOf(
                    R.id.add_items to {
                        update()
                    },
                    R.id.remove_items to {
                        clear()
                    }
                )
            )
        }
    }

    private fun clear() {
        call(object {})
        (listView?.adapter as DayOfTransactionAdapter).apply {
            clear()
        }
    }

    private fun update() {
        call(object {})
        clear()
        (listView?.adapter as DayOfTransactionAdapter).apply {
            MyUser.data?.let { data ->
                Transaction::class.query(
                    Where.matches(
                        Transaction.USER_DATA.eq(data.id)
                    ).paginated(Page.firstPage().withLimit(20)),
                    { wrappers ->
                        log("Got wrappers")
                        val groupedByDate =
                            wrappers.groupByTo(HashMap<String, MutableList<Transaction>>()) { it.date }
                                .map {
                                    it
                                }.toList()
                                .sortedByDescending { it.key }
                                .onEach {
                                    it.value.sortBy { it.exactTime ?: "" }
                                }.toList()
                        log("Adding items")
                        addAll(groupedByDate)
                    }, {
                        loge("Could not get wrappers on update: ${it.message}")
                        it.printStackTrace()
                    })
            } ?: toast("Not signed in")
        }
    }

    override fun onUserDataFound(bank: Bank) {
        super.onUserDataFound(bank)
        update()
    }
}