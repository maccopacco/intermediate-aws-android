package com.maxdreher.intermediate.ui

import android.content.DialogInterface
import android.content.Intent
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.EditText
import com.amplifyframework.core.model.Model
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.core.model.temporal.Temporal
import com.amplifyframework.datastore.DataStoreException
import com.amplifyframework.datastore.generated.model.*
import com.google.android.gms.tasks.Task
import com.maxdreher.*
import com.maxdreher.Util.Date.toAmplifyDate
import com.maxdreher.Util.Date.toAmplifyDateTime
import com.maxdreher.Util.Date.unit
import com.maxdreher.Util.get
import com.maxdreher.amphelper.suspense.Suspend
import com.maxdreher.intermediate.*
import com.maxdreher.intermediate.ExtensionFunctions.Models.basicData
import com.maxdreher.intermediate.ExtensionFunctions.Models.getLastDate
import com.maxdreher.intermediate.ExtensionFunctions.Models.toIAccount
import com.maxdreher.intermediate.keys.Keys
import com.maxdreher.intermediate.util.IAccount
import com.maxdreher.intermediate.util.PlaidToAmp
import com.maxdreher.intermediate.util.plaidcallbacks.PlaidCallback
import com.plaid.client.PlaidClient
import com.plaid.client.request.*
import com.plaid.client.response.ItemPublicTokenExchangeResponse
import com.plaid.client.response.LinkTokenCreateResponse
import com.plaid.client.response.TransactionsGetResponse
import com.plaid.link.Plaid
import com.plaid.link.configuration.LinkTokenConfiguration
import com.plaid.link.result.LinkExit
import com.plaid.link.result.LinkResultHandler
import com.plaid.link.result.LinkSuccess
import kotlinx.coroutines.*
import retrofit2.Response
import java.util.*
import java.util.concurrent.TimeUnit.DAYS
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis

interface IPlaidBase : IGoogleBase {

    companion object {
        var plaidClient: PlaidClient = PlaidClient.newBuilder()
            .clientIdAndSecret(Keys.PLAID_CLIENT_ID, Keys.PLAID_SECRET)
            .developmentBaseUrl()
            .build()
        var inProgress = false
    }

    override fun signout(): Task<Void>? {
        if (!inProgress) {
            return super.signout()
        }
        return null
    }

    private val linkResultHandler: LinkResultHandler
        get() = LinkResultHandler({ onLinkSuccess(it) }, { onLinkFailure(it) })

    val linkTokenCreateRequest: LinkTokenCreateRequest?
        get() = run {
            call(object {})
            MyUser.user?.let { user ->
                LinkTokenCreateRequest(
                    LinkTokenCreateRequest.User(user.id),
                    "Intermediate",
                    listOf("transactions"),
                    listOf("US"),
                    "en"
                ).withAndroidPackageName("com.maxdreher.intermediate")
            }
        }

    override fun onNoBankFound() {
        triggerLink()
    }

    fun triggerLink() {
        call(object {})
        MyUser.user?.let {
            log("Creating plaid link for ${it.googleId} (${it.originalEmail})")
            inProgress = true


            plaidClient.service().linkTokenCreate(
                linkTokenCreateRequest
            ).enqueue(PlaidCallback<LinkTokenCreateResponse>({ response ->
                if (response.isSuccessful) {
                    val outterToken = response.body()?.linkToken
                    log("Token create request successful ($outterToken), starting Plaid")

                    activity?.let { activity ->
                        Plaid.create(
                            activity.application,
                            LinkTokenConfiguration.Builder().apply {
                                token = outterToken
                            }.build()
                        ).open(activity)
                    } ?: toast("Cannot trigger Link from this location, no activity")

                } else {
                    onLinkTokenCreateBadResponse(response)
                }
            }, { onLinkTokenCreateFailure(it) }))

        } ?: error("No user found!")
    }

    fun onLinkTokenCreateBadResponse(response: Response<LinkTokenCreateResponse>) {
        call(object {})
        error("Could not generate Link Token\n\n$response")
        inProgress = false
    }

    fun onLinkTokenCreateFailure(t: Throwable) {
        call(object {})
        error("Call to generate Link Token failed\n\n${t.message}")
        inProgress = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        call(object {})
        if (!linkResultHandler.onActivityResult(requestCode, resultCode, data)) {
            log("linkResultHandler did not handle...")
        } else {
            log("linkResultHandler did handle results")
        }
    }

    private fun onLinkSuccess(linkSuccess: LinkSuccess) {
        call(object {})
        log("Link success public token: ${linkSuccess.publicToken}")
        plaidClient.service()
            .itemPublicTokenExchange(ItemPublicTokenExchangeRequest(linkSuccess.publicToken))
            .enqueue(PlaidCallback<ItemPublicTokenExchangeResponse>({ response ->
                if (response.isSuccessful) {
                    onPublicTokenExchangeSuccess(
                        MyUser.user,
                        linkSuccess.metadata.institution!!.id,
                        response.body()?.accessToken
                    )
                } else {
                    onPublicTokenExchangeBadResponse(response)
                }
            }, { onPublicTokenExchangeFailure(it) }))
    }

    fun onLinkFailure(linkExit: LinkExit) {
        call(object {})
        error("Link failure: ${linkExit.error?.toString()}")
        inProgress = false
    }

    fun onPublicTokenExchangeFailure(t: Throwable) {
        call(object {})
        error("Link token public exchange failure\n${t.message}")
        inProgress = false
    }


    fun onPublicTokenExchangeBadResponse(response: Response<ItemPublicTokenExchangeResponse>) {
        call(object {})
        error("Link token public exchange bad response\n$response")
        inProgress = false
    }

    fun onPublicTokenExchangeSuccess(user: User?, institutionID: String, token: String?) {
        call(object {})
        user?.let {
            Bank.builder()
                .user(user)
                .plaidAccessToken(token)
                .institutionId(institutionID)
                .lastTouchedTime(Date().toAmplifyDateTime())
                .build().save(this) {
                    log("Bank created/saved")
                    MyUser.bank = it
                    populateInstitutionLogo(it)
                }
        } ?: tokenButNoUser(token)
    }

    fun populateInstitutionLogo(bank: Bank?, onSave: ((Bank) -> Unit)? = null) {
        call(object {})
        bank?.let {
            plaidClient.service()
                .institutionsGetById(
                    InstitutionsGetByIdRequest(
                        bank.institutionId,
                        listOf("US")
                    ).withIncludeOptionalMetadata(true)
                )
                .enqueue(PlaidCallback({ response ->
                    log("Got response")
                    if (response.isSuccessful) {
                        log("Response successful")
                        response.body()?.institution?.let { inst ->
                            if (!inst.logo.equals(bank.institutionLogo)
                                || !inst.name.equals(bank.institutionName)
                            ) {
                                bank.copyOfBuilder()
                                    .institutionLogo(inst.logo)
                                    .institutionName(inst.name)
                                    .build().save(this) {
                                        MyUser.bank = it
                                        toast("${it.institutionName} logo found")
                                        onSave?.invoke(it)
                                    }
                            } else {
                                log("Logo/Name already present")
                            }
                        } ?: etoast("No Plaid institution found... something's wrong")
                    } else {
                        etoast("Response to get Plaid Institution unsuccessful\n$response")
                    }
                }, { ex -> etoast("Plaid callback failed\n${ex.get()}") }))
        } ?: loge("No bank found")
    }

    override fun setBankImage(bank: Bank?, headerView: View?) {
        super.setBankImage(bank, headerView)
        if (bank?.institutionLogo.isNullOrEmpty()) {
            populateInstitutionLogo(bank) {
                setBankImage(it)
            }
        }
    }


    private fun tokenButNoUser(token: String?) {
        call(object {})
        val textEdit = EditText(getContext()).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            gravity = Gravity.CENTER
            hint = "Enter the specified value"
        }
        val rand = Util.getRandInt(1000, 9999)
        alertBuilder(
            "DO NOT CLOSE THIS POPUP",
            "I don't know how you did this, but theres an ID made for you but " +
                    "it couldn't be saved to your user." +
                    "\nWe've got a limited number of these IDs (100)," +
                    " so please let send a screenshot so Max fix this manually." +
                    "\nID is:\n${token}" +
                    "\nTo clear this screen, enter\n$rand"
        ).run {
            setPositiveButton("Close window", null)
            setView(textEdit)
            create()
        }.apply {
            setOnShowListener { dialog ->
                getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                    if (textEdit.text.toString() == rand.toString()) {
                        dialog.dismiss()
                        inProgress = false
                    } else {
                        textEdit.text.clear()
                        alert(
                            "Wrong number",
                            "Sorry, can't let you leave. Really need that number"
                        )
                    }
                }
            }
            show()
        }
    }

    fun updateLastItem(userData: UserData? = MyUser.data) {
        val src = call(object {})
        userData?.let { data ->
            Transaction::class.query(
                Transaction.USER_DATA.eq(data.id),
                { list: List<Transaction> ->
                    val opt: Temporal.Date? = userData.oldestPendingDate
                    val ob: Int? = userData.maxImportBatch
                    val nopt: Temporal.Date? = list.filter { it.pending }
                        .minWithOrNull { a, b -> a.date.compareTo(b.date) }?.date

                    val nob: Int? = list.filter { it.importBatch != null }.maxWithOrNull { a, b ->
                        a.importBatch.compareTo(b.importBatch)
                    }?.importBatch

                    val updateMinPendingTime = (opt == null || (nopt != null && nopt < opt)).also {
                        log("Found new pending time: $it")
                    }
                    val updateBatch = (ob == null || (nob != null && nob > ob)).also {
                        log("Found new batch: $it")
                    }
                    if (updateBatch || updateMinPendingTime) {
                        data.copyOfBuilder().run {
                            if (updateMinPendingTime) {
                                oldestPendingDate(nopt)
                            } else this
                        }.run {
                            if (updateBatch) {
                                maxImportBatch(nob)
                            } else this
                        }.build().save({
                            log("Saved ${UserData::class.simpleName} from $src")
                            findBankAndUserData()
                        }, {
                            loge("Could not save new UserData! ${it.get()}")
                        })
                    } else {
                        log("Found nothing new")
                    }
                },
                { error("Could not get Transactions ${it.get()}") }
            )
        }
    }

    fun getDataFromPlaid(userData: UserData? = MyUser.data) {
        call(object {})
        if (userData == null) {
            loge("No user data")
            return
        }
        GlobalScope.launch {

            val accountsJob = async {
                timeSuspend("get accounts") {
                    getAccounts(userData)
                }
            }

            //Gotta pay for dis one
//            val refreshJob = async {
//                timeSuspend("Plaid Refresh") {
//                    val plaidAccessToken = userData.bank.plaidAccessToken
//                    log("Refreshing for $plaidAccessToken")
//                    plaidClient.service()
//                        .transactionsRefresh(TransactionsRefreshRequest(plaidAccessToken))
//                        .execute()
//                }
//            }

            val accounts = accountsJob.await()
            if (accounts == null) {
                loge("Could not get accounts")
                return@launch
            } else {
                log("Got accounts")
            }
//            val refresh = refreshJob.await()
//            log("Refresh successful: ${refresh.isSuccessful} ${refresh.errorBody()?.string() ?: ""}")

            launch {
                timeSuspend("update transactions") {
                    updateTransactions(userData, accounts)
                }
            }
            launch {
                updateBalances(userData, accounts)
            }
        }
    }

    suspend fun getAccounts(userData: UserData): List<Account>? {
        call(object {})
        return Account::class.query(Account.USER_DATA.eq(userData.id))
            .getOr {
                loge("Could not get accounts\n${it.get()}")
            }
    }

    fun updateBalances() {
        call(object {})
        MyUser.data?.let { data ->
            Account::class.query(Account.USER_DATA.eq(data.id), {
                updateBalances(data, it)
            }, {
                log("Could not update balances: ${it.get()}")
            })
        } ?: toast("No user data?")
    }


    private fun updateBalances(userData: UserData, myAccounts: List<Account>) {
        call(object {})
        val time = Date().toAmplifyDateTime()
        plaidClient.service()
            .accountsBalanceGet(AccountsBalanceGetRequest(userData.bank.plaidAccessToken))
            .enqueue(PlaidCallback({ response ->
                if (response.isSuccessful) {
                    val plaidAccounts = response.body()?.accounts
                    if (plaidAccounts == null) {
                        loge("Plaid accounts null")
                        return@PlaidCallback
                    }
                    for (myAccount in myAccounts) {
                        plaidAccounts.find { myAccount.plaidId == it.accountId }?.apply {
                            log("Plaid account matched for ${myAccount.toIAccount().basicData()}")
                            val bal = Balance.builder().account(myAccount)
                                .availableBalance(balances.available)
                                .currentBalance(balances.current)
                                .time(time).build()

                            bal.save({ log("Balance ${it.account.name} saved") }, {
                                loge("Balance for ${bal.account.name} not saved ${it.get()}")
                            })
                        }
                    }
                } else {
                    loge("Response not successful\n${response.message()}")
                }
            }, {
                loge("No response from Plaid\n${it.get()}")
            }))
    }

    private suspend fun updateTransactions(userData: UserData, accounts: List<Account>) {
        call(object {})
        val oldestDate: Date = userData.getLastDate()
        val responseJob = GlobalScope.async {
            log("Getting Plaid response")
            time("get Plaid response") {
                plaidClient.service().transactionsGet(
                    TransactionsGetRequest(
                        userData.bank.plaidAccessToken,
                        oldestDate,
                        (3 unit DAYS).fromNow()
                    )
                ).execute()
            }
        }

        val transactionJob: Deferred<List<Transaction>?> = GlobalScope.async {
            timeSuspend("get transactions") {
                Transaction::class.query(
                    Transaction.USER_DATA.eq(userData.id)
                        .and(Transaction.DATE.ge(oldestDate.toAmplifyDate()))
                ).getOr {
                    loge("Could not get transactions: ${it.get()}")
                }
            }
        }
        val response = responseJob.await()
        val transactions = transactionJob.await()

        if (!response.isSuccessful) {
            loge("Plaid response not successful ${response.errorBody()?.toString()}")
        } else if (transactions == null) {
            loge("Could not get transactions")
        } else {
            log("Responses for update transactions looks good")
            val plaidTrans = response.body()!!.transactions
            val plaidAccounts = response.body()!!.accounts

            log("Plaid transactions\n" +
                    plaidTrans.joinToString("\n") {
                        it.basicData()
                    })
            log("Transactions\n" +
                    transactions.joinToString("\n") {
                        it.basicData()
                    })

            val partition = plaidTrans.partition { plaidT ->
                accounts.any { acc -> acc.plaidId == plaidT.accountId }
            }

            val transactionsWithAccounts = partition.first
            val transactionsNoAccount = partition.second

            log("${transactionsWithAccounts.size} transactions with account already")
            val assignValidAccounts = GlobalScope.async {
                timeSuspend("assign transactions") {
                    assign(transactionsWithAccounts, transactions, userData, accounts)
                }
            }

            log("${transactionsNoAccount.size} transactions with no account")
            val badAccountIDs = transactionsNoAccount.map { it.accountId }
            val accountsToCreate = plaidAccounts.filter { p -> badAccountIDs.contains(p.accountId) }
            log("${accountsToCreate.size} accounts to create")

            val thingsToWaitFor = mutableListOf(assignValidAccounts)
            if (accountsToCreate.isNotEmpty()) {
                val newAccounts =
                    timeSuspend("create new accounts") {
                        createAccounts(
                            userData,
                            accountsToCreate.map { it.toIAccount() })
                    } ?: return

                thingsToWaitFor.addAll(
                    listOf(GlobalScope.async {
                        updateBalances(userData, newAccounts)
                    }, GlobalScope.async {
                        assign(transactionsNoAccount, transactions, userData, newAccounts)
                    })
                )
            }
            thingsToWaitFor.awaitAll()
        }
    }

    private suspend fun assign(
        plaidTransactions: List<TransactionsGetResponse.Transaction>,
        transactions: List<Transaction>,
        userData: UserData,
        accounts: List<Account>,
    ) {
        call(object {})
        val count = AtomicInteger(0)
        plaidTransactions.map { plaidT ->
            GlobalScope.async {
                updateTransactionFromPlaid(
                    transactions.find { it.plaidId == plaidT.transactionId },
                    plaidT,
                    userData,
                    accounts.find { plaidT.accountId == it.plaidId }!!,
                    count
                )
            }
        }.awaitAll()
    }

    suspend fun createAccounts(
        userData: UserData,
        accounts: List<IAccount>,
    ): List<Account>? {
        call(object {})
        val saveResults: List<Suspend<Account>> = accounts.map { account ->
            GlobalScope.async {
                log("Saving account ${account.basicData()}")
                Account.Builder()
                    .plaidId(account.accountId)
                    .name(account.name)
                    .userData(userData)
                    .build().saveSuspend()
            }
        }.awaitAll()
        log("Got response from accounts save")
        val badSaves = saveResults.filter { it.exception != null }
        if (badSaves.isNotEmpty()) {
            log(
                "Bad save results (${badSaves.size})\n${
                    badSaves
                        .mapIndexed
                        { num, it -> num.toString() + (it.exception?.message ?: "No response") }
                        .joinToString(",")
                }"
            )
            badSaves.forEach { it.exception?.printStackTrace() }
            return null
        }
        log("Results good")
        return saveResults.map { it.result!! }
    }

    private fun updateTransactionFromPlaid(
        t: Transaction?,
        plaid: TransactionsGetResponse.Transaction,
        userData: UserData,
        account: Account,
        saveCount: AtomicInteger
    ) {
        call(object {})
        if (t == null) {
            loge("New transaction fond for ${plaid.basicData()}")
            val list: List<Model> = PlaidToAmp.convert(plaid, account, userData)
            GlobalScope.launch {
                saveModels(this@IPlaidBase, list, saveCount.getAndIncrement())
            }
        } else {
            val print = { res: String, error: Boolean ->
                log("Match found for ${plaid.basicData()}, $res", error)
            }
            if (t.pending != plaid.pending) {
                print.invoke("Update required, pending status now ${plaid.pending}", true)
                val newT = t.copyOfBuilder().pending(plaid.pending)
                    .build()
                newT.save({}, {
                    loge("Could not update transaction ${newT.id}\n${it.get()}")
                })
            } else {
                print.invoke("Update not required", false)
            }
        }
    }
}
