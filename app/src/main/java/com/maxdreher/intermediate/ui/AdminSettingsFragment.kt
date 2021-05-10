package com.maxdreher.intermediate.ui

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.Gravity
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.fragment.findNavController
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.*
import com.maxdreher.*
import com.maxdreher.extensions.PreferenceFragmentCompatBase
import com.maxdreher.intermediate.*
import com.maxdreher.intermediate.R
import com.maxdreher.intermediate.keys.Keys
import com.maxdreher.intermediate.ui.IPlaidBase.Companion.plaidClient
import com.maxdreher.intermediate.util.Margin
import com.maxdreher.intermediate.util.plaidcallbacks.PlaidCallback
import com.maxdreher.table.TableEntry
import com.maxdreher.table.TableHelper
import com.plaid.client.request.AccountsGetRequest
import com.plaid.client.request.InstitutionsSearchRequest
import com.plaid.client.response.Institution
import com.plaid.client.response.InstitutionsSearchResponse
import de.codecrafters.tableview.SortableTableView
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class AdminSettings : PreferenceFragmentCompatBase(R.xml.admin_settings), IPlaidBase {

    override val activity: Activity
        get() = requireActivity()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super<IPlaidBase>.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState, rootKey)
        goto()
        adds()
        shows()
        deletes()
        test()
    }

    private fun goto() {
        findPreference("gotoSettings") {
            findNavController().navigate(R.id.settingsFragment)
        }
    }

    private fun test() {
        val count = AtomicInteger(0)
        findPreference("test1") {
            MyUser.data?.let { data ->
                Transaction.builder()
                    .amount(1.0)
                    .pending(true)
                    .date(
                        Date(
                            Date().time - TimeUnit.DAYS.toMillis(
                                count.getAndIncrement().toLong()
                            )
                        ).toAmpDate()
                    )
                    .originalDescription("desc")
                    .build().save(this) {
                        TransactionWrapper.Builder()
                            .userData(data)
                            .transaction(it)
                            .overrideName("Better desc")
                            .memo("memo")
                            .build()
                            .save(this)
                    }

            } ?: toast("No user data")
        }
        findPreference("test2") {
            updateLastItem()
        }
        findPreference("test3") {
            updateItems()
        }
    }

    private fun adds() {
        findPreference("addMyself") {
            for (me in Keys.MY_ACCESS_TOKENS) {
                me.run {
                    onPublicTokenExchangeSuccess(MyUser.user, ins, id)
                }
            }
            GlobalScope.launch {
                delay(5000)
                findBankAndUserData(MyUser.user)
            }
        }

        findPreference("setDate") {
            MyUser.data?.let { data ->
                val datePicker = DatePicker(context)
                val set = { date: Date? ->
                    data.copyOfBuilder()
                        .oldestPendingTime(
                            date?.toAmpDate()
                        ).build().save({ toast("Should be saved"); findBankAndUserData() })
                }
                alertBuilder("Pick a date")
                    .setView(datePicker)
                    .setPositiveButton("Submit") { dialogInterface: DialogInterface, i: Int ->
                        set.invoke(datePicker.getDate())
                    }
                    .setNegativeButton("Cancel", null)
                    .setNeutralButton("Set null") { d, i ->
                        set.invoke(null)
                    }
                    .show()
            } ?: toast("No data")
        }
    }

    private fun deletes() {
        findPreference("deleteAllUsers") {
            User::class.deleteAll(
                { toast("Should be gone");signout() },
                { error("It ain't gone\n${it.message}"); it.printStackTrace() })
        }

        findPreference("deleteMyBanks") {
            MyUser.user?.let {
                Bank::class.delete(
                    Bank.USER.eq(it.id),
                    { toast("Should be gone") },
                    { error("Not gone\n${it.message}") })

            } ?: toast("Uhh, not signed in")
        }
        findPreference("deleteAll") {
            Amplify.DataStore.clear({
                GlobalScope.launch {
                    withContext(Dispatchers.Main) {
                        toast("Should be deleted")
                    }
                }
            }, {
                error("Could not clear datastore ${it.message}")
                it.printStackTrace()
            })
        }

        findPreference("deleteTransactions") {
            TransactionWrapper::class.deleteAll(
                {
                    toast("Wrappers gone")
                    Transaction::class.deleteAll(
                        { toast("Bye transactions") },
                        { ex -> error("Transactions not gone\n${ex.message}");ex.printStackTrace() })
                },
                { error("Wrappers not gone\n${it.message}");it.printStackTrace() })
        }
    }


    private fun shows() {
        findPreference("institutionSearch") {
            val et = EditText(context).apply { gravity = Gravity.CENTER }
            alertBuilder("Search Institution", "Enter name of institution to search for")
                .setView(et)
                .setPositiveButton("Search") { _, _ ->
                    plaidClient.service()
                        .institutionsSearch(
                            InstitutionsSearchRequest(
                                et.text.toString(),
                                listOf("US")
                            ).withIncludeOptionalMetadata(true)
                        )
                        .enqueue(PlaidCallback<InstitutionsSearchResponse>({ response ->
                            response.body()?.institutions?.let { insts ->
                                if (insts.isEmpty()) {
                                    return@let
                                }
                                val table = SortableTableView<Institution>(context).apply {
                                    addDataLongClickListener { rowIndex, clickedData ->
                                        (getSystemService(
                                            requireContext(),
                                            ClipboardManager::class.java
                                        ) as ClipboardManager).setPrimaryClip(
                                            ClipData.newPlainText(
                                                "URL",
                                                clickedData.url
                                            )
                                        )

                                        true
                                    }
                                }
                                alertBuilder("Items").setView(table)
                                    .create().apply {
                                        setOnShowListener {
                                            TableHelper.updateTable(
                                                context, table, insts,
                                                TableEntry.from(Margin.get(context), mapOf(
                                                    "Name" to { it.name },
                                                    "ID" to { it.institutionId },
                                                    "URL" to { it.url }
                                                ))
                                            )
                                        }
                                        show()
                                    }
                            }
                        }, {
                            alert("Debug", "Couldn't search stutions")
                        }))
                }.show()
        }

        findPreference("showInstitution") {
            plaidClient.service().accountsGet(AccountsGetRequest(Keys.MY_ACCESS_TOKENS[0].id))
                .enqueue(
                    PlaidCallback({
                        alert("Debug", "Here he is: ${it.body()?.item?.institutionId}")
                    }, { toast("Extra no counts") })
                )
        }

        findPreference("showLogo") {
            MyUser.bank?.institutionLogo?.let { logo ->
                alertBuilder("Here it is").apply {
                    val decode = Base64.decode(logo, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.size)
                    setView(ImageView(context).apply {
                        setImageBitmap(bitmap)
                    })
                }.show()
            } ?: toast("No logo idiot!")
        }

    }

}