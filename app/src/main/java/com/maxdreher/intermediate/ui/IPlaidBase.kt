package com.maxdreher.intermediate.ui

import android.content.DialogInterface
import android.content.Intent
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.EditText
import com.amplifyframework.core.model.Model
import com.amplifyframework.core.model.query.predicate.QueryPredicate
import com.amplifyframework.datastore.generated.model.*
import com.google.android.gms.tasks.Task
import com.maxdreher.Util
import com.maxdreher.intermediate.MyUser
import com.maxdreher.intermediate.keys.Keys
import com.maxdreher.intermediate.matches
import com.maxdreher.intermediate.basicData
import com.maxdreher.intermediate.toAmpDate
import com.maxdreher.intermediate.util.PlaidToAmp
import com.maxdreher.intermediate.util.plaidcallbacks.PlaidCallback
import com.maxdreher.query
import com.maxdreher.save
import com.plaid.client.PlaidClient
import com.plaid.client.request.InstitutionsGetByIdRequest
import com.plaid.client.request.ItemPublicTokenExchangeRequest
import com.plaid.client.request.LinkTokenCreateRequest
import com.plaid.client.request.TransactionsGetRequest
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
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

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
                .lastTouchedTime(Util.getSaneDate())
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
                        } ?: toast("No Plaid institution found... something's wrong", error = true)
                    } else {
                        toast(
                            "Response to get Plaid Institution unsuccessful\n$response",
                            error = true
                        )
                    }
                }, { toast("Plaid callback failed\n${it.message}", error = true) }))
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
            TransactionWrapper::class.query(
                TransactionWrapper.USER_DATA.eq(data.id),
                { list ->
                    val opt: String? = userData.oldestPendingTime
                    val ob: Int? = userData.maxImportBatch
                    val nopt: String? = list.filter { it.transaction.pending }
                        .minWithOrNull { a, b -> a.transaction.date.compareTo(b.transaction.date) }?.transaction?.date
                    val nob: Int? =
                        list.filter { it.importBatch != null }.maxWithOrNull { a, b ->
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
                                oldestPendingTime(nopt)
                            } else this
                        }.run {
                            if (updateBatch) {
                                maxImportBatch(nob)
                            } else this
                        }.build().save(this@IPlaidBase) {
                            log("Saved ${UserData::class.simpleName} from $src")
                            findBankAndUserData()
                        }
                    } else {
                        log("Found nothing new")
                    }
                },
                { error("Could not get TransactionWrappers");it.printStackTrace() }
            )
        }
    }

    fun updateItems(userData: UserData? = MyUser.data) {
        call(object {})
        userData?.let {
            val oldestDate: Date? = userData.oldestPendingTime.let {
                if (it.isNullOrEmpty()) Date(0) else it.toAmpDate()
            }
            GlobalScope.launch {
                var response: Response<TransactionsGetResponse>? = null
                var transactionWrappers: List<TransactionWrapper>? = null
                awaitAll(
                    async {
                        log("Getting Plaid response")
                        response = plaidClient.service().transactionsGet(
                            TransactionsGetRequest(
                                userData.bank.plaidAccessToken,
                                oldestDate,
                                Date(Date().time + TimeUnit.DAYS.toMillis(3))
                            )
                        ).execute()
                    },
                    async {
                        log("Getting transactions")
                        Transaction::class.query(
                            Transaction.DATE.ge(
                                Util.simpleDateFormat.format(
                                    oldestDate
                                )
                            ),
                            { transactions ->
                                log("Got ${Transaction::class.simpleName}s")
                                if (transactions.isEmpty()) {
                                    transactionWrappers = listOf()
                                    return@query
                                }
                                val predicate: QueryPredicate =
                                    transactions.map { TransactionWrapper.TRANSACTION.eq(it.id) as QueryPredicate }
                                        .reduce { acc, value ->
                                            acc.or(value)
                                        }
                                        .and(TransactionWrapper.USER_DATA.eq(userData.id))

                                TransactionWrapper::class.query(
                                    predicate,
                                    {
                                        log("Got ${TransactionWrapper::class.simpleName}")
                                        transactionWrappers = it
                                    },
                                    {
                                        loge("Could not get transaction wrappers: ${it.message}")
                                        it.printStackTrace()
                                    })
                            }, {
                                loge("Could not get transactions: ${it.message}")
                                it.printStackTrace()
                            })
                    })

                response?.let { response ->
                    if (response.isSuccessful) {
                        val plaidTrans = response.body()!!.transactions
                        log("Plaid transactions")
                        plaidTrans.forEach { log(it.basicData()) }
                        log("Wrappers")
                        transactionWrappers?.forEach { log(it.basicData()) }
                        val count = AtomicInteger(0)
                        plaidTrans?.forEach { plaid ->
                            updateTransactionFromPlaid(
                                transactionWrappers?.find { it.matches(plaid) },
                                plaid,
                                userData,
                                count
                            )
                        }
                    }
                }
            }
        } ?: loge("No user data")
    }

    private fun updateTransactionFromPlaid(
        t: TransactionWrapper?,
        plaid: TransactionsGetResponse.Transaction,
        userData: UserData,
        saveCount: AtomicInteger
    ) {
        if (t == null) {
            loge("New transaction found for ${plaid.basicData()}")
            val list: List<Model> = PlaidToAmp.convert(plaid, userData)
            GlobalScope.launch {
                Util.saveModels(this@IPlaidBase, list, saveCount.getAndIncrement())
            }
        } else {
            val print = { res: String, error: Boolean ->
                log("Match found for ${plaid.basicData()}, $res", error)
            }
            if (t.transaction.pending != plaid.pending) {
                print.invoke("Update required, pending status now ${plaid.pending}", true)
                t.transaction.copyOfBuilder().pending(plaid.pending)
                    .build().save()
            } else {
                print.invoke("Update not required", false)
            }
        }
    }
}
