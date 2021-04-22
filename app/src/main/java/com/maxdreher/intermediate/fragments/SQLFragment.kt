package com.maxdreher.intermediate.fragments

import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.predicate.QueryPredicates
import com.amplifyframework.datastore.generated.model.TestGoal
import com.maxdreher.Util
import com.maxdreher.amphelper.AmpHelper
import com.maxdreher.amphelper.AmpHelperD
import com.maxdreher.amphelper.AmpHelperQ
import com.maxdreher.extensions.FragmentBase
import com.maxdreher.intermediate.R
import com.maxdreher.intermediate.util.Margin
import com.maxdreher.table.TableEntry
import com.maxdreher.table.TableHelper
import de.codecrafters.tableview.SortableTableView

class SQLFragment : FragmentBase(R.layout.fragment_sql) {

    private lateinit var table: SortableTableView<TestGoal>
    private lateinit var entries: List<TableEntry<TestGoal>>

    private val help = AmpHelper<TestGoal>()
    private val helpQ = AmpHelperQ<TestGoal>()
    private val helpD = AmpHelperD()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val margin = Margin.get(context)

        entries = listOf(
            TableEntry(
                "ID", TableEntry.textViewGenerator(
                    { it.id },
                    margin
                )
            ),
            TableEntry(
                "Content",
                TableEntry.textViewGenerator(
                    { it.content }, margin
                )
            )
        )

        table = view.findViewById(R.id.sql_query_result)
        table.addDataLongClickListener { _, data ->
            return@addDataLongClickListener deleteFromTable(data)
        }

        val submitValue = view.findViewById<EditText>(R.id.sql_value_edittext)

        update()

        Util.buttonToListener(
            view,
            mapOf(
                R.id.sql_submit to View.OnClickListener {
                    submit(submitValue)
                },
                R.id.sql_query to View.OnClickListener {
                    update()
                },
                R.id.sql_delete to View.OnClickListener {
                    delete()
                })
        )
    }

    private fun deleteFromTable(data: TestGoal): Boolean {
        Amplify.DataStore.delete(data, help.g, help.b)
        help.afterWait({ toast("Deleted [${data.content}]"); update() },
            { toast("Could not delete [${data.content}]") })
        return true
    }

    private fun submit(submitValue: EditText) {
        val t = submitValue.text.toString()
        if (t == "") {
            toast("Cannot save nothing")
            return
        }
        Amplify.DataStore.save(
            TestGoal.builder().content(t).build(),
            help.g, help.b
        )
        help.afterWait(
            onSuccess = {
                toast("Updated")
                submitValue.setText("")
                update()
            },
            onFail = { toast("Not updated") })
    }

    private fun delete() {
        Amplify.DataStore.delete(TestGoal::class.java, QueryPredicates.all(), helpD.g, helpD.b)
        helpD.afterWait({ toast("Deleted"); update() }, { toast("Not deleted") })
    }

    private fun update() {
        Amplify.DataStore.query(
            TestGoal::class.java,
            helpQ.g, helpQ.b
        )
        helpQ.afterWait({
            TableHelper.updateTable(requireContext(), table, it, entries)
            toast("Updated")
        }, { toast("This is bad") })

    }
}