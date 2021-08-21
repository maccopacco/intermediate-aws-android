package com.maxdreher.intermediate.ui

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.maxdreher.Util
import com.maxdreher.Util.get
import com.maxdreher.extensions.IContextBase
import com.maxdreher.intermediate.R
import com.maxdreher.intermediate.keys.Keys
import com.maxdreher.intermediate.util.plaidcallbacks.PlaidCallback
import com.plaid.client.PlaidClient
import com.plaid.client.request.*
import com.plaid.client.response.ItemPublicTokenExchangeResponse
import com.plaid.client.response.LinkTokenCreateResponse
import com.plaid.link.Plaid
import com.plaid.link.configuration.LinkTokenConfiguration
import com.plaid.link.result.LinkExit
import com.plaid.link.result.LinkResultHandler
import com.plaid.link.result.LinkSuccess
import kotlinx.coroutines.*
import retrofit2.Response

interface IPlaidBase : IContextBase {

    val activity: Activity?

    val resultCaller: ActivityResultCaller?

    companion object {
        var plaidClient: PlaidClient = PlaidClient.newBuilder()
            .clientIdAndSecret(Keys.PLAID_CLIENT_ID, Keys.PLAID_SECRET)
            .developmentBaseUrl()
            .build()
        var inProgress = false

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAlwaysShowSignInMethodScreen(true)
            .setAvailableProviders(
                arrayListOf(
                    AuthUI.IdpConfig.EmailBuilder()
                        .setRequireName(true).build(),
                    AuthUI.IdpConfig.GoogleBuilder()
                        .build(),
                    AuthUI.IdpConfig.PhoneBuilder().build(),
                )
            )
            .build()

        private var latestLauncher: ActivityResultLauncher<Intent>? = null

        fun set(
            resultCaller: ActivityResultCaller?,
            onSignIn: (FirebaseAuthUIAuthenticationResult) -> Unit
        ) {
            latestLauncher = resultCaller?.registerForActivityResult(
                FirebaseAuthUIActivityResultContract()
            ) { onSignIn.invoke(it) }
        }
    }

    fun onCreate(savedInstanceState: Bundle?) {
        set(resultCaller, this::onSignIn)
    }


    fun onSignIn(firebaseAuthUIAuthenticationResult: FirebaseAuthUIAuthenticationResult) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            toast("User not found")
            return
        } else {
            toast("User found")
        }

        updateUI(user)
    }

    fun signIn() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            AuthUI.getInstance().signOut(getContext()!!)
                .addOnSuccessListener {
                    toast("Signed out")
                    updateUI()
                }
                .addOnFailureListener { toast("Not signed out") }
        } else {
            latestLauncher?.launch(signInIntent)
        }
    }

    private val linkResultHandler: LinkResultHandler
        get() = LinkResultHandler({ onLinkSuccess(it) }, { onLinkFailure(it) })

    val linkTokenCreateRequest: LinkTokenCreateRequest?
        get() = run {
            call(object {})
            FirebaseAuth.getInstance().currentUser?.let { user ->
                LinkTokenCreateRequest(
                    LinkTokenCreateRequest.User(user.uid),
                    "Intermediate",
                    listOf("transactions"),
                    listOf("US"),
                    "en"
                ).withAndroidPackageName("com.maxdreher.intermediate")
            }
        }

    fun triggerLink() {
        call(object {})
        FirebaseAuth.getInstance().currentUser?.let { user ->
            log("Creating Plaid link for ${user.uid} ${user.displayName}")
            inProgress = true

            plaidClient.service().linkTokenCreate(
                linkTokenCreateRequest
            ).enqueue(PlaidCallback<LinkTokenCreateResponse>({ response ->
                if (response.isSuccessful) {
                    val outerToken = response.body()?.linkToken
                    log("Token create request successful ($outerToken), starting Plaid")
                    activity?.let { activity ->
                        Plaid.create(
                            activity.application,
                            LinkTokenConfiguration.Builder().apply {
                                token = outerToken
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

    private fun onLinkSuccess(linkSuccess: LinkSuccess) {
        call(object {})
        log("Link success public token: ${linkSuccess.publicToken}")
        plaidClient.service()
            .itemPublicTokenExchange(ItemPublicTokenExchangeRequest(linkSuccess.publicToken))
            .enqueue(PlaidCallback<ItemPublicTokenExchangeResponse>({ response ->
                if (response.isSuccessful) {
                    onPublicTokenExchangeSuccess(
                        FirebaseAuth.getInstance().currentUser?.uid,
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

    fun onPublicTokenExchangeSuccess(
        userId: String?,
        institutionID: String,
        token: String?,
        populateData: Boolean = true
    ) {
        call(object {})
        userId?.let {
            Firebase.firestore.collection("banks").add(
                mapOf(
                    "user" to userId,
                    "institutionID" to institutionID,
                    "token" to token,
                    "populated" to false,
                )
            ).addOnSuccessListener {
                log("Public token exchange success")
                if (populateData) {
                    populateBankDatasForUser(userId)
                }
            }.addOnFailureListener {
                tokenButNoUser(token)
            }
        } ?: tokenButNoUser(token)
    }

    fun populateBankDatasForUser(userId: String) {
        call(object {})
        Firebase.firestore.collection("banks")
            .whereEqualTo("user", userId)
            .whereNotEqualTo("populated", true)
            .get()
            .addOnSuccessListener { result ->
                log("Unpopulated banks for user $userId = ${result.documents.size}")
                GlobalScope.launch {
                    val institutes = result.documents.map { doc ->
                        return@map GlobalScope.async {
                            val id = doc["institutionID"] as String
                            doc to plaidClient.service()
                                .institutionsGetById(
                                    InstitutionsGetByIdRequest(
                                        id,
                                        listOf("US")
                                    ).withIncludeOptionalMetadata(true)
                                )
                                .execute()
                        }
                    }.awaitAll()
                    log("Got ${institutes.size} institutes")

                    val batch = Firebase.firestore.batch()
                    for (doc_and_institute in institutes.filter { it.second.isSuccessful }) {
                        val ref = doc_and_institute.first.reference
                        val institute =
                            doc_and_institute.second.body()?.institution
                        if (institute == null) {
                            loge("Institute for document ${doc_and_institute.first.id} is null")
                            return@launch
                        }
                        batch.update(
                            ref,
                            mutableMapOf(
                                "name" to institute.name,
                                "logo" to institute.logo,
                                "populated" to true,
                            ) as Map<String, Any>
                        )
                    }
                    batch.commit().addOnSuccessListener {
                        toast("Institutes updated")
                    }.addOnFailureListener { ex ->
                        error("Could not update institutions ${ex.get()}")
                    }
                }
            }.addOnFailureListener {
                toast("Couldn't get tokens")
                loge(it.get() ?: "No message")
            }
    }

    //    fun populateInstitutionLogo(bank: Bank?, onSave: ((Bank) -> Unit)? = null) {
//        call(object {})
//        bank?.let {
//            plaidClient.service()
//                .institutionsGetById(
//                    InstitutionsGetByIdRequest(
//                        bank.institutionId,
//                        listOf("US")
//                    ).withIncludeOptionalMetadata(true)
//                )
//                .enqueue(PlaidCallback({ response ->
//                    log("Got response")
//                    if (response.isSuccessful) {
//                        log("Response successful")
//                        response.body()?.institution?.let { inst ->
//                            if (!inst.logo.equals(bank.institutionLogo)
//                                || !inst.name.equals(bank.institutionName)
//                            ) {
//                                bank.copyOfBuilder()
//                                    .institutionLogo(inst.logo)
//                                    .institutionName(inst.name)
//                                    .build().save(this) {
//                                        MyUser.bank = it
//                                        toast("${it.institutionName} logo found")
//                                        onSave?.invoke(it)
//                                    }
//                            } else {
//                                log("Logo/Name already present")
//                            }
//                        } ?: etoast("No Plaid institution found... something's wrong")
//                    } else {
//                        etoast("Response to get Plaid Institution unsuccessful\n$response")
//                    }
//                }, { ex -> etoast("Plaid callback failed\n${ex.get()}") }))
//        } ?: loge("No bank found")
//    }
//
//    override fun setBankImage(bank: Bank?, headerView: View?) {
//        super.setBankImage(bank, headerView)
//        if (bank?.institutionLogo.isNullOrEmpty()) {
//            populateInstitutionLogo(bank) {
//                setBankImage(it)
//            }
//        }
//    }
//
//
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
//
//    fun updateLastItem(userData: UserData? = MyUser.data) {
//        val src = call(object {})
//        userData?.let { data ->
//            Transaction::class.query(
//                Transaction.USER_DATA.eq(data.id),
//                { list: List<Transaction> ->
//                    val opt: Temporal.Date? = userData.oldestPendingDate
//                    val ob: Int? = userData.maxImportBatch
//                    val nopt: Temporal.Date? = list.filter { it.pending }
//                        .minWithOrNull { a, b -> a.date.compareTo(b.date) }?.date
//
//                    val nob: Int? = list.filter { it.importBatch != null }.maxWithOrNull { a, b ->
//                        a.importBatch.compareTo(b.importBatch)
//                    }?.importBatch
//
//                    val updateMinPendingTime = (opt == null || (nopt != null && nopt < opt)).also {
//                        log("Found new pending time: $it")
//                    }
//                    val updateBatch = (ob == null || (nob != null && nob > ob)).also {
//                        log("Found new batch: $it")
//                    }
//                    if (updateBatch || updateMinPendingTime) {
//                        data.copyOfBuilder().run {
//                            if (updateMinPendingTime) {
//                                oldestPendingDate(nopt)
//                            } else this
//                        }.run {
//                            if (updateBatch) {
//                                maxImportBatch(nob)
//                            } else this
//                        }.build().save({
//                            log("Saved ${UserData::class.simpleName} from $src")
//                            findBankAndUserData()
//                        }, {
//                            loge("Could not save new UserData! ${it.get()}")
//                        })
//                    } else {
//                        log("Found nothing new")
//                    }
//                },
//                { error("Could not get Transactions ${it.get()}") }
//            )
//        }
//    }
//
//    fun getDataFromPlaid(userData: UserData? = MyUser.data) {
//        call(object {})
//        if (userData == null) {
//            loge("No user data")
//            return
//        }
//        GlobalScope.launch {
//
//            val accountsJob = async {
//                timeSuspend("get accounts") {
//                    getAccounts(userData)
//                }
//            }
//
//            //Gotta pay for dis one
////            val refreshJob = async {
////                timeSuspend("Plaid Refresh") {
////                    val plaidAccessToken = userData.bank.plaidAccessToken
////                    log("Refreshing for $plaidAccessToken")
////                    plaidClient.service()
////                        .transactionsRefresh(TransactionsRefreshRequest(plaidAccessToken))
////                        .execute()
////                }
////            }
//
//            val accounts = accountsJob.await()
//            if (accounts == null) {
//                loge("Could not get accounts")
//                return@launch
//            } else {
//                log("Got accounts")
//            }
////            val refresh = refreshJob.await()
////            log("Refresh successful: ${refresh.isSuccessful} ${refresh.errorBody()?.string() ?: ""}")
//
//            launch {
//                timeSuspend("update transactions") {
//                    updateTransactions(userData, accounts)
//                }
//            }
//            launch {
//                updateBalances(userData, accounts)
//            }
//        }
//    }
//
//    suspend fun getAccounts(userData: UserData): List<Account>? {
//        call(object {})
//        return Account::class.query(Account.USER_DATA.eq(userData.id))
//            .getOr {
//                loge("Could not get accounts\n${it.get()}")
//            }
//    }
//
//    fun updateBalances() {
//        call(object {})
//        MyUser.data?.let { data ->
//            Account::class.query(Account.USER_DATA.eq(data.id), {
//                updateBalances(data, it)
//            }, {
//                log("Could not update balances: ${it.get()}")
//            })
//        } ?: toast("No user data?")
//    }
//
//
//    private fun updateBalances(userData: UserData, myAccounts: List<Account>) {
//        call(object {})
//        val time = Date().toAmplifyDateTime()
//        plaidClient.service()
//            .accountsBalanceGet(AccountsBalanceGetRequest(userData.bank.plaidAccessToken))
//            .enqueue(PlaidCallback({ response ->
//                if (response.isSuccessful) {
//                    val plaidAccounts = response.body()?.accounts
//                    if (plaidAccounts == null) {
//                        loge("Plaid accounts null")
//                        return@PlaidCallback
//                    }
//                    for (myAccount in myAccounts) {
//                        plaidAccounts.find { myAccount.plaidId == it.accountId }?.apply {
//                            log("Plaid account matched for ${myAccount.toIAccount().basicData()}")
//                            val bal = Balance.builder().account(myAccount)
//                                .availableBalance(balances.available)
//                                .currentBalance(balances.current)
//                                .time(time).build()
//
//                            bal.save({ log("Balance ${it.account.name} saved") }, {
//                                loge("Balance for ${bal.account.name} not saved ${it.get()}")
//                            })
//                        }
//                    }
//                } else {
//                    loge("Response not successful\n${response.message()}")
//                }
//            }, {
//                loge("No response from Plaid\n${it.get()}")
//            }))
//    }
//
//    private suspend fun updateTransactions(userData: UserData, accounts: List<Account>) {
//        call(object {})
//        val oldestDate: Date = userData.getLastDate()
//        val responseJob = GlobalScope.async {
//            log("Getting Plaid response")
//            time("get Plaid response") {
//                plaidClient.service().transactionsGet(
//                    TransactionsGetRequest(
//                        userData.bank.plaidAccessToken,
//                        oldestDate,
//                        (3 unit DAYS).fromNow()
//                    )
//                ).execute()
//            }
//        }
//
//        val transactionJob: Deferred<List<Transaction>?> = GlobalScope.async {
//            timeSuspend("get transactions") {
//                Transaction::class.query(
//                    Transaction.USER_DATA.eq(userData.id)
//                        .and(Transaction.DATE.ge(oldestDate.toAmplifyDate()))
//                ).getOr {
//                    loge("Could not get transactions: ${it.get()}")
//                }
//            }
//        }
//        val response = responseJob.await()
//        val transactions = transactionJob.await()
//
//        if (!response.isSuccessful) {
//            loge("Plaid response not successful ${response.errorBody()?.toString()}")
//        } else if (transactions == null) {
//            loge("Could not get transactions")
//        } else {
//            log("Responses for update transactions looks good")
//            val plaidTrans = response.body()!!.transactions
//            val plaidAccounts = response.body()!!.accounts
//
//            log("Plaid transactions\n" +
//                    plaidTrans.joinToString("\n") {
//                        it.basicData()
//                    })
//            log("Transactions\n" +
//                    transactions.joinToString("\n") {
//                        it.basicData()
//                    })
//
//            val partition = plaidTrans.partition { plaidT ->
//                accounts.any { acc -> acc.plaidId == plaidT.accountId }
//            }
//
//            val transactionsWithAccounts = partition.first
//            val transactionsNoAccount = partition.second
//
//            log("${transactionsWithAccounts.size} transactions with account already")
//            val assignValidAccounts = GlobalScope.async {
//                timeSuspend("assign transactions") {
//                    assign(transactionsWithAccounts, transactions, userData, accounts)
//                }
//            }
//
//            log("${transactionsNoAccount.size} transactions with no account")
//            val badAccountIDs = transactionsNoAccount.map { it.accountId }
//            val accountsToCreate = plaidAccounts.filter { p -> badAccountIDs.contains(p.accountId) }
//            log("${accountsToCreate.size} accounts to create")
//
//            val thingsToWaitFor = mutableListOf(assignValidAccounts)
//            if (accountsToCreate.isNotEmpty()) {
//                val newAccounts =
//                    timeSuspend("create new accounts") {
//                        createAccounts(
//                            userData,
//                            accountsToCreate.map { it.toIAccount() })
//                    } ?: return
//
//                thingsToWaitFor.addAll(
//                    listOf(GlobalScope.async {
//                        updateBalances(userData, newAccounts)
//                    }, GlobalScope.async {
//                        assign(transactionsNoAccount, transactions, userData, newAccounts)
//                    })
//                )
//            }
//            thingsToWaitFor.awaitAll()
//        }
//    }
//
//    private suspend fun assign(
//        plaidTransactions: List<TransactionsGetResponse.Transaction>,
//        transactions: List<Transaction>,
//        userData: UserData,
//        accounts: List<Account>,
//    ) {
//        call(object {})
//        val count = AtomicInteger(0)
//        plaidTransactions.map { plaidT ->
//            GlobalScope.async {
//                updateTransactionFromPlaid(
//                    transactions.find { it.plaidId == plaidT.transactionId },
//                    plaidT,
//                    userData,
//                    accounts.find { plaidT.accountId == it.plaidId }!!,
//                    count
//                )
//            }
//        }.awaitAll()
//    }
//
//    suspend fun createAccounts(
//        userData: UserData,
//        accounts: List<IAccount>,
//    ): List<Account>? {
//        call(object {})
//        val saveResults: List<Suspend<Account>> = accounts.map { account ->
//            GlobalScope.async {
//                log("Saving account ${account.basicData()}")
//                Account.Builder()
//                    .plaidId(account.accountId)
//                    .name(account.name)
//                    .userData(userData)
//                    .build().saveSuspend()
//            }
//        }.awaitAll()
//        log("Got response from accounts save")
//        val badSaves = saveResults.filter { it.exception != null }
//        if (badSaves.isNotEmpty()) {
//            log(
//                "Bad save results (${badSaves.size})\n${
//                    badSaves
//                        .mapIndexed
//                        { num, it -> num.toString() + (it.exception?.message ?: "No response") }
//                        .joinToString(",")
//                }"
//            )
//            badSaves.forEach { it.exception?.printStackTrace() }
//            return null
//        }
//        log("Results good")
//        return saveResults.map { it.result!! }
//    }
//
//    private fun updateTransactionFromPlaid(
//        t: Transaction?,
//        plaid: TransactionsGetResponse.Transaction,
//        userData: UserData,
//        account: Account,
//        saveCount: AtomicInteger
//    ) {
//        call(object {})
//        if (t == null) {
//            loge("New transaction fond for ${plaid.basicData()}")
//            val list: List<Model> = PlaidToAmp.convert(plaid, account, userData)
//            GlobalScope.launch {
//                saveModels(this@IPlaidBase, list, saveCount.getAndIncrement())
//            }
//        } else {
//            val print = { res: String, error: Boolean ->
//                log("Match found for ${plaid.basicData()}, $res", error)
//            }
//            if (t.pending != plaid.pending) {
//                print.invoke("Update required, pending status now ${plaid.pending}", true)
//                val newT = t.copyOfBuilder().pending(plaid.pending)
//                    .build()
//                newT.save({}, {
//                    loge("Could not update transaction ${newT.id}\n${it.get()}")
//                })
//            } else {
//                print.invoke("Update not required", false)
//            }
//        }
//    }


    /**
     * [setName], [setEmail], and [setImage] with
     * @param account [FirebaseUser]
     */
    private fun updateUI(account: FirebaseUser? = null) {
        call(object {})
        val view = getHeader()
        setEmail(account?.email, view)
        setName(account?.displayName, view)
        GlobalScope.launch {
            if (account != null) {
                val drawable = account.photoUrl?.let { uri ->
                    Util.urlToDrawable(uri.toString())
                }
                withContext(Dispatchers.Main) {
                    setImage(Image(drawable), view)
                }
            } else {
                withContext(Dispatchers.Main) {
                    setImage(Image(R.mipmap.ic_launcher_round), view)
                }
            }
        }

    }

    /**
     * @param name to update name [TextView] with
     */
    private fun setName(name: String?, headerView: View? = getHeader()) {
        call(object {})
        headerView?.findViewById<TextView>(R.id.nav_header_name)?.text = name ?: ""
    }

    /**
     * @param email to update email [TextView] with
     */
    private fun setEmail(email: String?, headerView: View? = getHeader()) {
        call(object {})
        headerView?.findViewById<TextView>(R.id.nav_header_email)?.text = email ?: ""
    }

    /**
     * @param image to update profile picture view with
     */
    private fun setImage(image: Image, headerView: View? = getHeader()) {
        call(object {})
        headerView?.findViewById<ImageView>(R.id.nav_header_image)?.apply {
            image.apply {
                when {
                    imageResource != null -> {
                        setImageResource(imageResource)
                    }
                    drawable != null -> {
                        setImageDrawable(drawable)
                    }
                    else -> {
                        setImageResource(R.drawable.pog)
                    }
                }
            }
        }
    }

    class Image(val drawable: Drawable?, val imageResource: Int? = null) {
        constructor(imageResource: Int?) : this(null, imageResource)
    }

    /**
     * Get [View] of header from source [Activity]
     * @return [View]
     */
    private fun getHeader(): View? {
        call(object {})
        return activity?.findViewById<NavigationView>(R.id.nav_view)?.getHeaderView(0)
    }
}
