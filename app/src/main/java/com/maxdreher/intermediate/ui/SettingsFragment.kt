package com.maxdreher.intermediate.ui

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.ListView
import android.widget.ScrollView
import android.widget.TextView
import androidx.preference.ListPreference
import com.amplifyframework.core.model.Model
import com.amplifyframework.core.model.query.Page
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.core.model.query.predicate.QueryPredicate
import com.amplifyframework.datastore.generated.model.*
import com.maxdreher.*
import com.maxdreher.extensions.IGoogleBaseBase
import com.maxdreher.extensions.PreferenceFragmentCompatBase
import com.maxdreher.intermediate.*
import com.maxdreher.intermediate.R
import com.maxdreher.intermediate.ui.IPlaidBase.Companion.plaidClient
import com.maxdreher.intermediate.uihelpers.AlertEditText
import com.maxdreher.intermediate.uihelpers.TransactionViewer
import com.maxdreher.intermediate.util.ParseSimpleToAmp
import com.maxdreher.table.TableEntry
import com.maxdreher.table.TableHelper
import com.plaid.client.request.AuthGetRequest
import de.codecrafters.tableview.SortableTableView
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference


class SettingsFragment : PreferenceFragmentCompatBase(R.xml.preferences), IPlaidBase {

    override val activity: Activity?
        get() = getActivity()

    private var listPreference: ListPreference? = null

    private var accountToSaveTo: Account? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState, rootKey)
        google()
        app()
        import()
    }

    override fun onUserDataFound(bank: Bank) {
        super.onUserDataFound(bank)
        call(object {})
        updateBanks()
    }

    private fun updateBanks() {
        call(object {})
        listPreference?.apply {
            Bank::class.query(Where.matches(Bank.USER.eq(MyUser.user?.id))
                .sorted(Bank.INSTITUTION_NAME.descending()),
                { list ->
                    if (list.isEmpty()) {
                        updateBankFailure()
                    } else {
                        val badBanks = list.filter { it.institutionName == null }
                        if (badBanks.isNotEmpty()) {
                            updateBankFailure()
                            getBankNamesAndIDs(badBanks)
                        } else {
                            isEnabled = true
                            entryValues = list.map { it.id }.toTypedArray()
                            entries = list.map { bank ->
                                bank.institutionName
                            }.toTypedArray()
                        }
                    }
                }, { updateBankFailure() })
        }
    }

    private fun getBankNamesAndIDs(list: List<Bank>) {
        call(object {})
        for (bank in list.dropLast(1)) {
            populateInstitutionLogo(bank)
        }
        //populate all logos, but attempt to update banks when the file is done
        populateInstitutionLogo(list.last()) { updateBanks() }
    }

    private fun updateBankFailure() {
        call(object {})
        listPreference?.apply {
            setEntryValues(R.array.chooseBankValues)
            setEntries(R.array.chooseBankText)
            isEnabled = false
        }
    }

    private fun app() {
        call(object {})
        listPreference = (findPreference("appChooseBank") as ListPreference).apply {
            updateBanks()
            setOnPreferenceChangeListener { _, newValue ->
                onNewBankSelected(newValue)
                true
            }
        }
        updateBanks()

        findPreference("appShowAccountNumbers") {
            MyUser.user?.let { user ->
                Bank::class.query(Bank.USER.eq(user.id), { banks ->
                    GlobalScope.launch {
                        val message = banks.joinToString("\n") { bank ->
                            val result = plaidClient.service()
                                .authGet(AuthGetRequest(bank.plaidAccessToken)).execute()
                            "${bank.institutionName}:\n${
                                result.body()?.numbers?.ach?.joinToString("\n") {
                                    "\taccount: ${it.account}\n" +
                                            "\trouting: ${it.routing}\n" +
                                            it.wireRouting.let { routing ->
                                                if (routing != null) "\twire: $routing"
                                                else ""
                                            }
                                } ?: "No account numbers found\n"
                            }"
                        }
                        withContext(Dispatchers.Main) {
                            alertBuilder("Account numbers (ACH #s)")
                                .setView(
                                    ScrollView(context).apply {
                                        addView(TextView(context).also {
                                            it.text = message
                                            it.setMargin(
                                                wrapHorizontal = false,
                                                wrapVertical = false,
                                                defaultMargin()
                                            )
                                        })
                                    })
                                .show()
                        }
                    }
                }, {
                    error("Could not find banks for you\n{${it.message}}")
                    it.printStackTrace()
                })
            } ?: notSignedIn()
        }

        findPreference("appDelete") {
            MyUser.user?.let { user ->
                Bank::class.query(Bank.USER.eq(user.id), { banks ->
                    UserData::class.delete(UserData.BANK.inList(banks),
                        { toast("User data deleted");signout() },
                        { error("User data could not be deleted") })
                }, { error("Could not get ${Bank::class.java.simpleName}s for user") })
            } ?: badSignin()
        }

        findPreference("appEditAccounts") {
            editAccounts()
        }

        findPreference("appMoveTransactions") {
            moveTransactions()
        }
    }

    private fun editAccounts() {
        MyUser.data?.let { data ->
            Account::class.query(Account.USER_DATA.eq(data.id), { accounts ->
                if (accounts.isNotEmpty()) {
                    val c = context ?: return@query
                    val table = SortableTableView<Account>(c).apply {
                        setMargin(true, false, defaultMargin())
                    }
                    TableHelper.updateTable(
                        c, table, accounts,
                        TableEntry.from(
                            defaultMargin(), mapOf(
                                "Original Account Name" to { it.name },
                                "Override name" to { it.overrideName ?: "" },
                            )
                        )
                    )
                    val dialog = alertBuilder("Choose account to edit (hold to delete)")
                        .setView(table)
                        .setPositiveButton("Cancel", null)
                        .setNeutralButton("Create account") { _, _ -> onCreateAccount(data) }
                        .show()
                    table.addDataClickListener { _, acc ->
                        onEditAccount(acc)
                        dialog.dismiss()
                    }
                    table.addDataLongClickListener { _, acc ->
                        if (acc.plaidId != null) {
                            acc.delete({ toast("Account deleted") }, {
                                error("Account could not be deleted\n${it.message}")
                                it.printStackTrace()
                            })
                            dialog.dismiss()
                        } else {
                            toast("Can't delete an account that you didn't create")
                        }
                        true
                    }
                } else {
                    onCreateAccount(data)
                }
            }, {
                etoast("Could not get accounts :\\")
                it.printStackTrace()
            })
        } ?: notSignedIn()
    }

    private fun onCreateAccount(userData: UserData, onSuccess: (() -> Unit)? = null) {
        val editText = AlertEditText(this@SettingsFragment)
        val ref = AtomicReference<Dialog>()
        editText.onAction(EditorInfo.IME_ACTION_DONE) { _, _, _ -> ref.get()?.dismiss(); true }
        alertBuilder("Create account")
            .setView(editText)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Create") { _, _ ->
                val accountName = editText.get()
                if (accountName.isEmpty()) {
                    etoast("You can't create an account with no name")
                    return@setPositiveButton
                }
                Account.builder()
                    .plaidId("")
                    .name(accountName)
                    .userData(userData)
                    .build().save({
                        toast("New account created")
                        onSuccess?.invoke()
                    }, {
                        error("New account could not be created\n${it.message}")
                        it.printStackTrace()
                    })
            }.show().also {
                ref.set(it)
            }
    }

    private fun onEditAccount(account: Account) {
        val ref = AtomicReference<Dialog>()
        val editText = AlertEditText(this@SettingsFragment, hint = account.getCombinedName())

        val onSub = { force: Boolean ->
            val newName = if (force) null else editText.get()
            if (newName?.isEmpty() == true) {
                etoast("Cannot change name to empty, that's silly")
            } else {
                account.copyOfBuilder().overrideName(newName).build().save(
                    {
                        toast("Account name updated")
                    }, {
                        etoast("Name not updated, an internal error occurred")
                        it.printStackTrace()
                    })
            }
            ref.get()?.dismiss()
        }

        editText.onAction(EditorInfo.IME_ACTION_DONE) { _, _, _ -> onSub(false);true }

        val overrideName = account.overrideName.let {
            if (it.isNullOrEmpty()) {
                ""
            } else "(${it})"
        }
        alertBuilder("Edit account name for ${account.name} ${overrideName}")
            .setView(editText)
            .setNegativeButton("Cancel", null)
            .setNeutralButton("Clear") { _, _ -> onSub(true) }
            .setPositiveButton("Change") { _, _ -> onSub(false) }
            .show().also {
                ref.set(it)
            }
        editText.requestFocus()
    }

    private fun moveTransactions() {
        MyUser.data?.let { data ->
            MyUser.user?.let { user ->
                Transaction::class.query(
                    Transaction.USER_DATA.eq(data.id).and(
                        Transaction.IMPORT_SOURCE.ne("")
                    ), { transactions ->
                        if (transactions.isEmpty()) {
                            toast("No imported batches to move :\\")
                            return@query
                        }
                        val groups: Map<List<String>, List<Transaction>> = transactions.groupBy {
                            listOf(
                                it.importSource,
                                it.importDate,
                                it.importBatch.toString()
                            )
                        }.map { entry ->
                            val newkey =
                                listOf(*entry.key.toTypedArray(), entry.value.size.toString())
                            entry.value
                            Pair(newkey, entry.value)
                        }.toMap()
                        val ref = AtomicReference<Dialog>()
                        alertBuilder("Select batch to move (hold to view)")
                            .setView(SortableTableView<List<String>>(context).also { table ->
                                TableHelper.updateTable(
                                    requireContext(),
                                    table,
                                    groups.map { it.key }.toList(),
                                    TableEntry.from(
                                        defaultMargin(),
                                        mapOf("Source" to { it[0] },
                                            "Date" to {
                                                it[1].let { date ->
                                                    date.toSaneDate()?.toSimpleDate() ?: date
                                                }
                                            },
                                            "Batch" to { it[2] },
                                            "Count" to { it[3] })
                                    )
                                )
                                table.addDataClickListener { _, clickedData: List<String> ->
                                    choseAccountToMoveTo(user, groups[clickedData]!!)
                                    ref.get()?.dismiss()
                                }
                                table.addDataLongClickListener { _, clickedData ->
                                    alertBuilder("Transactions")
                                        .setView(
                                            TransactionViewer(
                                                ListView(context),
                                                this@SettingsFragment
                                            ).apply {
                                                add(groups[clickedData]!!)
                                            }.listView
                                        )
                                        .show()
                                    true
                                }
                            }).show().also { ref.set(it) }
                    },
                    {
                        etoast("Could not get transactions right now... sorry about that")
                        it.printStackTrace()
                    })
            } ?: notSignedIn()
        }
    }

    private fun choseAccountToMoveTo(user: User, transactions: List<Transaction>) {
        Bank::class.query(Bank.USER.eq(user.id), { banks ->
            GlobalScope.launch {
                val datas = UserData::class.query(
                    UserData.BANK.inList(banks)
                ).getOr {
                    etoast("Could not get data for banks")
                    it.printStackTrace()
                } ?: return@launch
                val accounts =
                    Account::class.query(Account.USER_DATA.inList(datas)).getOr {
                        etoast("Could not get accounts from data")
                        it.printStackTrace()
                    } ?: return@launch

                withContext(Dispatchers.Main) {
                    val ref = AtomicReference<Dialog>()
                    alertBuilder("Select account to move batch to")
                        .setView(SortableTableView<Account>(context).also { table ->
                            table.addDataClickListener { _, acc ->
                                moveTransactionsTo(acc, transactions)
                                ref.get()?.dismiss()
                            }
                            TableHelper.updateTable(
                                requireContext(), table, accounts,
                                TableEntry.from(
                                    defaultMargin(), mapOf(
                                        "Bank" to { it.userData.bank.institutionName ?: "" },
                                        "Name" to { it.getCombinedName() }
                                    )
                                )
                            )
                        })
                        .show().also { ref.set(it) }
                }
            }
        },
            {
                error("Could not get banks\n${it.message}")
                it.printStackTrace()
            })
    }

    private fun moveTransactionsTo(
        account: Account,
        transactions: List<Transaction>
    ) {
        val data = account.userData
        GlobalScope.launch {
            val saves = transactions.map {
                async {
                    it.copyOfBuilder()
                        .userData(data)
                        .account(account.id)
                        .build().saveSuspend()
                }
            }.awaitAll()
            val errors = saves.filter { it.exception != null }
            withContext(Dispatchers.Main) {
                if (errors.isEmpty()) {
                    toast("All transactions moved")
                } else {
                    error("Some transactions (${errors.size}) could not be moved")
                    errors.forEach { it.exception?.printStackTrace() }
                }
            }
        }
    }

    private fun onNewBankSelected(newValue: Any?) {
        call(object {})
        Bank::class.query(Where.matches(Bank.ID.eq(newValue)).paginated(Page.firstResult()),
            {
                if (it.isEmpty()) {
                    error("No bank returned? How'd you do that?")
                    updateBankFailure()
                } else {
                    it[0].copyOfBuilder()
                        .lastTouchedTime(Util.getSaneDate())
                        .build()
                        .save(this@SettingsFragment) {
                            findBankAndUserData()
                            setBankImage()
                            toast("${it.institutionName} selected")
                        }
                }
            },
            {
                updateBankFailure()
                error("Bank could not be found\n${it.message}")
                it.printStackTrace()
            })
    }

    private fun import() {
        call(object {})
        findPreference("importSimpleJSON") {
            startSimpleImport()
        }
    }


    private fun google() {
        call(object {})
        findPreference("googleAccountSignin") {
            if (IGoogleBaseBase.account != null) {
                signout()?.addOnSuccessListener { signin() }
            } else {
                signin()
            }
        }
        findPreference("googleAccountSignout") { signout() }
    }

    private fun startSimpleImport() {
        call(object {})
        MyUser.data?.let { data ->
            Account::class.query(Account.USER_DATA.eq(data.id), { accounts ->
                if (accounts.isEmpty()) {
                    onCreateAccount(data, onSuccess = this::startSimpleImport)
                } else {
                    val c: Context = context ?: return@query
                    val ref = AtomicReference<Dialog>()
                    alertBuilder("Choose account from ${accounts[0].userData.bank.institutionName ?: "current bank"}")
                        .setView(SortableTableView<Account>(c).also { table ->
                            TableHelper.updateTable(
                                c,
                                table,
                                accounts,
                                TableEntry.from(
                                    defaultMargin(),
                                    mapOf(
                                        "Name" to { it.getCombinedName() }
                                    )
                                )
                            )
                            table.addDataClickListener { _, acc ->
                                ref.get()?.dismiss()
                                accountToSaveTo = acc
                                openFile(MIME_TYPE.JSON, RequestCode.SIMPLE_FILE_CODE_JSON)
                            }
                        }).show().also { ref.set(it) }
                }
            }, {
                etoast("Could not get accounts :\\")
                it.printStackTrace()
            })
        } ?: notSignedIn()
    }

    private fun openFile(mimeType: MIME_TYPE, code: Int) {
        call(object {})
        if (MyUser.user == null) {
            return
        }
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = mimeType.value
            addCategory(Intent.CATEGORY_OPENABLE)
        }

        startActivityForResult(
            Intent.createChooser(intent, "Select a File to Upload"),
            code
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super<IPlaidBase>.onActivityResult(requestCode, resultCode, data)
        call(object {})
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RequestCode.SIMPLE_FILE_CODE_JSON -> saveSimpleData(data)
            }
        }
    }

    /**
     * Saves data from Simple from file picker [Intent] which stores the files [Uri] in
     * [Intent.getData]
     */
    private fun saveSimpleData(data: Intent?) {
        call(object {})
        accountToSaveTo?.let { account ->
            GlobalScope.launch {
                withContext(Dispatchers.Default) {
                    MyUser.data?.let { userData ->
                        val parseResult = ParseSimpleToAmp.convert(
                            data?.data,
                            this@SettingsFragment,
                            userData,
                            account,
                            MyUser.importLimit
                        )
                        if (parseResult == null) {
                            toast("Bad data from Simple")
                            return@let
                        }
                        GlobalScope.launch {
                            saveSimpleResults(parseResult)
                        }
                    } ?: badSignin()
                }
            }
        } ?: error(
            "The account you wanted to save to is gone, how'd you get here? " +
                    "\nLeave and don't come back"
        )
    }

    private fun saveSimpleResults(parseResult: List<List<Model>>) {
        call(object {})
        val firstError = AtomicBoolean(true)
        val errorConsumer: (Model, Exception, Int) -> Unit = { model, ex, _ ->
            if (firstError.get()) {
                alert("Errors found in Simple Import", "Will have to send log files to Max")
                firstError.set(false)
            }
            loge("Could not save ${model.modelName}. ID: ${model.id}")
            ex.printStackTrace()
        }
        GlobalScope.launch {
            runBlocking {
                parseResult.forEachIndexed { index, list ->
                    launch {
                        Util.saveModels(this@SettingsFragment, list, index, errorConsumer)
                    }
                }
            }
            updateLastItem()
        }
    }


    private fun badSignin() {
        call(object {})
        error("Not signed in")
    }

}