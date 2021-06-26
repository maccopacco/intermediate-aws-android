package com.maxdreher.intermediate.ui

import android.os.Bundle
import android.view.View
import com.amplifyframework.datastore.generated.model.Transaction
import com.maxdreher.AmpGson
import com.maxdreher.Util.Date.toDateTime
import com.maxdreher.Util.Date.toSimpleDate
import com.maxdreher.extensions.FragmentBase
import com.maxdreher.intermediate.R
import com.maxdreher.intermediate.databinding.TransactionFragmentBinding
import com.maxdreher.intermediate.uihelpers.TransactionLoader

class TransactionFragment :
    FragmentBase<TransactionFragmentBinding>(TransactionFragmentBinding::class.java) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString("Transaction")?.let { string ->

            val t = AmpGson.deserialize().fromJson(string, Transaction::class.java)
            alert(
                "Debug",
                "Raw: ${t.date}" +
                        "\nMy: ${t.date.toDate().toDateTime()}"
            )
            TransactionLoader.loadTransaction(view, t)
            TransactionLoader.loadDate(view.findViewById(R.id.date), t)
            TransactionLoader.wrapTitle(view)

        } ?: etoast("No transaction found, what did you do...")
    }
}