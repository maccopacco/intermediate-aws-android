package com.maxdreher.intermediate.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.amplifyframework.core.model.query.Page
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.datastore.generated.model.Bank
import com.amplifyframework.datastore.generated.model.Transaction
import com.maxdreher.Util.Companion.buttonToListener
import com.maxdreher.extensions.FragmentBase
import com.maxdreher.intermediate.MyUser
import com.maxdreher.intermediate.R
import com.maxdreher.intermediate.uihelpers.TransactionViewer
import com.maxdreher.query


class PlaidFragment : FragmentBase(R.layout.fragment_plaid), IPlaidBase {


    override val activity: Activity?
        get() = getActivity()

    private var viewer: TransactionViewer? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super<IPlaidBase>.onActivityResult(requestCode, resultCode, data)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            viewer = TransactionViewer(findViewById(R.id.plaid_view_listview), this@PlaidFragment)

            buttonToListener(
                mapOf(
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
        viewer?.clear()
    }

    private fun update() {
        call(object {})
        clear()
        viewer?.let { viewer ->
            MyUser.data?.let { data ->
                Transaction::class.query(
                    Where.matches(
                        Transaction.USER_DATA.eq(data.id)
                    ).paginated(Page.firstPage().withLimit(20)),
                    {
                        viewer.add(it)
                    }, {
                        loge("Could not get transactions on update: ${it.message}")
                        it.printStackTrace()
                    })
            } ?: notSignedIn()
        } ?: toast("Viewer could not be found")
    }

    override fun onUserDataFound(bank: Bank) {
        super.onUserDataFound(bank)
        update()
    }
}