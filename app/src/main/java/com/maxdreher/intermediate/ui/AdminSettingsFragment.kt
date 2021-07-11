package com.maxdreher.intermediate.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import com.maxdreher.extensions.PreferenceFragmentCompatBase
import com.maxdreher.intermediate.R

class AdminSettingsFragment : PreferenceFragmentCompatBase(R.xml.admin_settings), IPlaidBase {

    override val activity: ComponentActivity? = getActivity()
    override val resultCaller: ActivityResultCaller = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super<PreferenceFragmentCompatBase>.onCreate(savedInstanceState)
        super<IPlaidBase>.onCreate(savedInstanceState)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super<IPlaidBase>.onActivityResult(requestCode, resultCode, data)
//    }
//
//    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
//        super.onCreatePreferences(savedInstanceState, rootKey)
//        goto()
//        settings()
//        adds()
//        shows()
//        deletes()
//        test()
//    }
//
//    private fun goto() {
//        findPreference("gotoSettings") {
//            findNavController().navigate(R.id.settingsFragment)
//        }
//    }
//
//    private fun settings() {
//        (findPreference("settingImportLimit") as SeekBarPreference).apply {
//            val setVal = { newValue: Int ->
//                if (newValue == 51) {
//                    MyUser.importLimit = null
//                } else {
//                    MyUser.importLimit = newValue
//                }
//                toast("Import limit set to ${MyUser.importLimit}")
//            }
//            setOnPreferenceChangeListener { _, newValue: Any ->
//                if (newValue is Int) {
//                    setVal.invoke(newValue)
//                }
//                true
//            }
//            setVal.invoke(value)
//        }
//    }
//
//    private fun test() {
//        findPreference("test1") {
//            getDataFromPlaid()
//        }
//        findPreference("test2") {
//            Transaction::class.query(
//                Where.matchesAll().sorted(Transaction.DATE.descending())
//                    .paginated(Page.firstPage().withLimit(5)),
//                {
//                    alert("Item dates", it.joinToString("\n") { it.date.toString() })
//                },
//                { error("Could not get!!!!!!!!\n${it.get()}") })
////            updateBalances()
//        }
//        findPreference("test3") {
//            MyUser.data?.let { data ->
//                val oldestDate = data.getLastDate()
//                plaidClient.service().transactionsGet(
//                    TransactionsGetRequest(
//                        data.bank.plaidAccessToken,
//                        oldestDate,
//                        (3 unit DAYS).fromNow()
//                    )
//                ).enqueue(PlaidCallback({
//                    toast("Total of ${it.body()?.totalTransactions} from Plaid")
//                }, {
//                    error("Could not do plaid\n${it.get()}")
//                }))
//            }
//        }
//    }
//
//    private fun adds() {
//        findPreference("addMyself") {
//            for (me in Keys.MY_ACCESS_TOKENS) {
//                me.run {
//                    onPublicTokenExchangeSuccess(MyUser.user, ins, id)
//                }
//            }
//            GlobalScope.launch {
//                delay(5000)
//                findBankAndUserData(MyUser.user)
//            }
//        }
//
//        findPreference("setDate") {
//            MyUser.data?.let { data ->
//                val datePicker = DatePicker(context)
//                val set = { date: Date? ->
//                    data.copyOfBuilder()
//                        .oldestPendingDate(
//                            date?.toAmplifyDate()
//                        ).build().save({ toast("Should be saved"); findBankAndUserData() }, {
//                            alert("Could not set date", it.get() ?: "Unknown cause")
//                        })
//                }
//                alertBuilder("Pick a date")
//                    .setView(datePicker)
//                    .setPositiveButton("Submit") { _: DialogInterface, _: Int ->
//                        set.invoke(datePicker.getDate())
//                    }
//                    .setNegativeButton("Cancel", null)
//                    .setNeutralButton("Set null") { _, _ ->
//                        set.invoke(null)
//                    }
//                    .show()
//            } ?: toast("No data")
//        }
//        findPreference("forceUpdate") {
////            Amplify.DataStore.
//        }
//    }
//
//    private fun deletes() {
//        findPreference("deleteAllUsers") {
//            User::class.deleteAll(
//                { toast("Should be gone");signout() },
//                { error("It ain't gone\n${it.get()}") })
//        }
//
//        findPreference("deleteMyBanks") {
//            MyUser.user?.let {
//                Bank::class.delete(
//                    Bank.USER.eq(it.id),
//                    { toast("Should be gone") },
//                    { ex -> error("Not gone\n${ex.get()}") })
//            } ?: toast("Uhh, not signed in")
//        }
//        findPreference("deleteAll") {
//            Amplify.DataStore.clear({
//                GlobalScope.launch {
//                    withContext(Dispatchers.Main) {
//                        toast("Should be deleted")
//                    }
//                }
//            }, {
//                error("Could not clear datastore ${it.get()}")
//            })
//        }
//
//        findPreference("deleteTransactions") {
//            Transaction::class.deleteAll(
//                {
//                    GlobalScope.launch {
//                        withContext(Dispatchers.Main) {
//                            toast("Bye transactions")
//                        }
//                    }
//                },
//                { ex ->
//                    GlobalScope.launch {
//                        withContext(Dispatchers.Main) {
//                            error("Transactions not gone\n${ex.get()}")
//                        }
//                    }
//                })
//        }
//
//        findPreference("deleteUser") {
//            User::class.query(Where.matchesAll(), { users ->
//                GlobalScope.launch {
//                    val usersWithBanks = users.map { user ->
//                        GlobalScope.async {
//                            user to (Bank::class.query(
//                                Bank.USER.eq(user.id)
//                            ).getOr { error("Could not get a bank info ${it.get()}") }?.size ?: -1)
//                        }
//                    }.awaitAll()
//                    withContext(Dispatchers.Main) {
//                        deleteUsersDialog(usersWithBanks)
//                    }
//                }
//            }, {})
//        }
//
//        findPreference("deleteDatas") {
//            UserData::class.deleteAll(
//                { toast("Deleted datas") },
//                { error("Transactions not gone\n${it.get()}") })
//        }
//    }
//
//    private fun deleteUsersDialog(usersWithBanks: List<Pair<User, Int>>) {
//        alertBuilder("Delete users").apply {
//            val t = SortableTableView<Pair<User, Int>>(context).apply {
//                addDataClickListener { _, clickedData ->
//                    clickedData.first.delete(
//                        { toast("Deleted") },
//                        { error("Nah couldn't delete ${it.get()}") })
//                }
//            }
//            setView(t)
//            TableHelper.updateTable(
//                context, t, usersWithBanks, TableEntry.from(
//                    defaultMargin(),
//                    mapOf("User" to { it.first.originalEmail },
//                        "ID" to { it.first.googleId },
//                        "Count" to { it.second.toString() }
//                    )
//                )
//            )
//            show()
//        }
//    }
//
//
//    private fun shows() {
//        findPreference("institutionSearch") {
//            val et = EditText(context).apply { gravity = Gravity.CENTER }
//            alertBuilder("Search Institution", "Enter name of institution to search for")
//                .setView(et)
//                .setPositiveButton("Search") { _, _ ->
//                    plaidClient.service()
//                        .institutionsSearch(
//                            InstitutionsSearchRequest(
//                                et.text.toString(),
//                                listOf("US")
//                            ).withIncludeOptionalMetadata(true)
//                        )
//                        .enqueue(PlaidCallback<InstitutionsSearchResponse>({ response ->
//                            response.body()?.institutions?.let { insts ->
//                                if (insts.isEmpty()) {
//                                    return@let
//                                }
//                                val table = SortableTableView<Institution>(context).apply {
//                                    addDataLongClickListener { _, clickedData ->
//                                        (getSystemService(
//                                            requireContext(),
//                                            ClipboardManager::class.java
//                                        ) as ClipboardManager).setPrimaryClip(
//                                            ClipData.newPlainText(
//                                                "URL",
//                                                clickedData.url
//                                            )
//                                        )
//
//                                        true
//                                    }
//                                }
//                                alertBuilder("Items").setView(table)
//                                    .create().apply {
//                                        setOnShowListener {
//                                            TableHelper.updateTable(
//                                                context, table, insts,
//                                                TableEntry.from(defaultMargin(), mapOf(
//                                                    "Name" to { it.name },
//                                                    "ID" to { it.institutionId },
//                                                    "URL" to { it.url }
//                                                ))
//                                            )
//                                        }
//                                        show()
//                                    }
//                            }
//                        }, {
//                            alert("Debug", "Couldn't search stutions")
//                        }))
//                }.show()
//        }
//
//        findPreference("showInstitution") {
//            plaidClient.service().accountsGet(AccountsGetRequest(Keys.MY_ACCESS_TOKENS[0].id))
//                .enqueue(
//                    PlaidCallback({
//                        alert("Debug", "Here he is: ${it.body()?.item?.institutionId}")
//                    }, { toast("Extra no counts") })
//                )
//        }
//
//        findPreference("showLogo") {
//            MyUser.bank?.institutionLogo?.let { logo ->
//                alertBuilder("Here it is").apply {
//                    val decode = Base64.decode(logo, Base64.DEFAULT)
//                    val bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.size)
//                    setView(ImageView(context).apply {
//                        setImageBitmap(bitmap)
//                    })
//                }.show()
//            } ?: toast("No logo idiot!")
//        }
//
//        findPreference("showTransactionCount") {
//            Transaction::class.query(Where.matchesAll(), {
//                alert("Success", "Transactions: ${it.size}")
//            }, {
//                alert("Error", it.message ?: "Idk")
//            })
//        }
//
//        findPreference("showAccounts") {
//            MyUser.data?.let { data ->
//                plaidClient.service().accountsGet(AccountsGetRequest(data.bank.plaidAccessToken))
//                    .enqueue(
//                        PlaidCallback({ response ->
//                            if (response.isSuccessful) {
//                                response.body()?.accounts?.joinToString("\n") { account ->
//                                    "[${account.name}] [${account.officialName}]"
//                                }?.also {
//                                    alert("Accounts for ${data.bank.institutionName}", it)
//                                } ?: toast("Bad response seems")
//                            }
//                        }, {
//                            error("Nah summ wrong: \n${it.get()}")
//                        })
//                    )
//            }
//        }
//
//        findPreference("showTransactionDates") {
//            Transaction::class.query(
//                Where.matchesAll().paginated(
//                    Page.firstPage().withLimit(5)
//                ), {
//                    alert(
//                        "Transaction dates",
//                        it.joinToString("\n") { "${it.date}\n${it.date.toDate().toSimpleDate()}" })
//                }, {
//                    error("Could not load\n${it.get()}")
//                }
//            )
//        }
//    }
}