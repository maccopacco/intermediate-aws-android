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
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.maxdreher.Util
import com.maxdreher.Util.get
import com.maxdreher.extensions.IContextBase
import com.maxdreher.intermediate.Bank
import com.maxdreher.intermediate.R
import com.maxdreher.intermediate.UserInfo
import com.maxdreher.intermediate.keys.Keys
import com.maxdreher.intermediate.util.plaidcallbacks.PlaidCallback
import com.plaid.client.PlaidClient
import com.plaid.client.request.*
import com.plaid.client.response.InstitutionsGetByIdResponse
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
        startLoading(user)
    }

    fun startLoading(user: FirebaseUser) {

    }

    fun signIn() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            latestLauncher?.launch(signInIntent)
        }
    }

    fun signOut() {
        AuthUI.getInstance().signOut(getContext()!!)
            .addOnSuccessListener {
                toast("Signed out")
                updateUI()
            }
            .addOnFailureListener { toast("Not signed out") }
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
            val bank = Bank(
                institutionID = institutionID,
            )
            Firebase.firestore.collection("banks").document(institutionID)
                .set(bank, SetOptions.merge())
                .addOnSuccessListener {
                    log("Public token exchange success")
                    if (populateData) {
                        populateBankDatasForUser()
                    }
                }.addOnFailureListener {
                    tokenButNoUser(token)
                }
        } ?: tokenButNoUser(token)


    }

    private class DocBankInst(
        val doc: DocumentSnapshot,
        val bank: Bank,
        val resp: Response<InstitutionsGetByIdResponse>
    )

    fun populateBankDatasForUser() {
        call(object {})
        Firebase.firestore.collection("banks")
            .whereNotEqualTo(Bank.Fields.NAME.value, null)
            .get()
            .addOnSuccessListener { result ->
                GlobalScope.launch {
                    val responses = result.documents.map { doc ->
                        return@map GlobalScope.async {
                            val bank = doc.toObject(Bank::class.java)!!
                            DocBankInst(
                                doc, bank, plaidClient.service()
                                    .institutionsGetById(
                                        InstitutionsGetByIdRequest(
                                            bank.institutionID,
                                            listOf("US")
                                        ).withIncludeOptionalMetadata(true)
                                    )
                                    .execute()
                            )
                        }
                    }.awaitAll()
                    log("Got ${responses.size} institutes, ${responses.count { it.resp.isSuccessful }} valid responses")
                    val batch = Firebase.firestore.batch()

                    for (combined in responses.filter { it.resp.isSuccessful }) {
                        val ref = combined.doc.reference
                        val oldInstitute = combined.bank
                        val newInstitute =
                            combined.resp.body()?.institution

                        if (newInstitute == null) {
                            loge("Institute for document ${combined.bank.institutionID} is null")
                            return@launch
                        }
                        oldInstitute.run {
                            logo = newInstitute.logo
                            name = newInstitute.name
                        }

                        batch.set(
                            ref,
                            oldInstitute
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
