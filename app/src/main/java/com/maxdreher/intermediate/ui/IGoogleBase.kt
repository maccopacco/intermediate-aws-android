package com.maxdreher.intermediate.ui

import android.app.Activity
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Base64
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.amplifyframework.core.model.query.Page
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.datastore.generated.model.Bank
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.UserData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.navigation.NavigationView
import com.maxdreher.Util
import com.maxdreher.Util.get
import com.maxdreher.extensions.IGoogleBaseBase
import com.maxdreher.intermediate.*
import com.maxdreher.intermediate.keys.Keys
import com.maxdreher.query
import com.maxdreher.save
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


interface IGoogleBase : IGoogleBaseBase {

    override val GOOGLE_REQUEST_CODE: Int
        get() = RequestCode.GOOGLE_SIGNIN_CODE

    override fun onSigninSuccess(account: GoogleSignInAccount) {
        super.onSigninSuccess(account)
        setAccount(account)
    }

    override fun onSigninFail(exception: Exception) {
        super.onSigninFail(exception)
        setAccount()
    }

    override fun onSignoutSuccess() {
        super.onSignoutSuccess()
        setAccount()
    }

    /**
     * Update account data ([MyUser], meaning potentially create new users), and update ui ([setAccount])
     */
    private fun setAccount(account: GoogleSignInAccount? = null) {
        call(object {})
        account?.let {
            User::class.query(User.GOOGLE_ID.eq(account.id),
                { users ->
                    when (users.size) {
                        0 -> {
                            toast("No user found")
                            createUser(account)
                        }
                        1 -> {
                            log("One user found")
                            MyUser.user = users[0]
                            updateUI(account)
                            findBankAndUserData(MyUser.user)
                        }
                        else -> {
                            loge("Account with too many users: ${account.id}")
                            updateUI()
                            error(
                                "Too many users for '${account.email}'" +
                                        "\n(this actually works off account ID but go off queen)"
                            )
                        }
                    }
                }, {
                    error("Cannot get ${User::class.java.simpleName}s from Amplify\n${it.get()}")
                })
        } ?: run { MyUser.setNull(); loge("Account null") }
    }

    /**
     * Check state of [Update] (make sure we're online),
     *  then call [rawCreateUserData]
     */
    private fun createUser(account: GoogleSignInAccount) {
        call(object {})
        if (MyUser.wasEverOnline() || (MyUser.allowOfflineSignin && account.id == Keys.MY_GOOGLE_ID)) {
            log("Update found, creating user")
            rawCreateUser(account)
        } else {
            error(
                "It doesn't appear that this account has synced to the " +
                        "internet yet\n\nA new user cannot be created until we can " +
                        "confirm it doesn't exist yet"
            )
        }
    }


    /**
     * Create new [User] from [account]
     */
    private fun rawCreateUser(account: GoogleSignInAccount) {
        call(object {})
        User.builder()
            .googleId(account.id)
            .originalEmail(account.email)
            .build().save({
                MyUser.user = it
                toast("New user saved")
                updateUI(account)
                findBankAndUserData(it)
            }, { ex ->
                error("New user not saved!\n${ex.message}")
            })
    }

    /**
     * Check quantity of [UserData] in [user]'s [User.banks] and error, create, or load appropriately
     */
    fun findBankAndUserData(user: User? = MyUser.user) {
        call(object {})
        user?.let {
            Bank::class.query(Where.matches(Bank.USER.eq(user.id))
                .sorted(Bank.LAST_TOUCHED_TIME.descending())
                .paginated(Page.firstResult()),
                {
                    if (it.isNotEmpty()) {
                        log("Bank found")
                        val newBank = it[0]
//                        val isNewBank = MyUser.bank?.equals(newBank) == true
                        MyUser.bank = newBank.also { primaryBank ->
                            UserData::class.query(
                                UserData.BANK.eq(primaryBank.id),
                                { userData ->
                                    log("Got bank, checking user data")
                                    checkUserData(primaryBank, userData)
                                }, { ex ->
                                    error(
                                        ("Could not query ${UserData::class.java.simpleName} for " +
                                                "${Bank::class.java.simpleName}\n${ex.get()}")
                                    )
                                })
                        }
                    } else {
                        log("No banks found")
                        onNoBankFound()
                    }
                },
                { alert("Alert", "There seems to be no banks found for User") })
        } ?: run { MyUser.setNull(); error("User null, cannot find bank and user data"); signout() }
    }

    fun onNoBankFound()

    fun onUserDataFound(bank: Bank) {
        call(object {})
        setBankImage(bank)
    }

    fun setBankImage(bank: Bank? = MyUser.bank, headerView: View? = getHeader()) {
        call(object {})
        headerView?.findViewById<ImageView>(R.id.nav_header_bank_logo)?.let { iv ->
            bank?.run {
                (if (institutionLogo.isNullOrEmpty()) null
                else institutionLogo)?.let { logo ->
                    val decode = Base64.decode(logo, Base64.DEFAULT)
                    iv.setImageBitmap(BitmapFactory.decodeByteArray(decode, 0, decode.size))
                } ?: iv.setImageResource(R.drawable.no_bank_image_foreground)
            } ?: iv.setImageResource(R.drawable.no_bank_found_foreground)
        }
    }


    /**
     * Check quantity of [datas] before creating a new [UserData]
     */
    private fun checkUserData(primaryBank: Bank, datas: List<UserData>) {
        call(object {})
        when {
            datas.isEmpty() -> {
                log("User data is empty")
                rawCreateUserData(primaryBank)
            }
            datas.size == 1 -> {
                log("1 ${UserData::class.java.simpleName} found for ${User::class.java.simpleName}")
                MyUser.data = datas[0]
                onUserDataFound(primaryBank)
            }
            else -> {
                error("Too many ${UserData::class.java.simpleName}s found for ${Bank::class.java.simpleName}")
                MyUser.setNull()
                signout()
            }
        }
    }


    /**
     * Create and save new [UserData] for
     * @param bank
     */
    private fun rawCreateUserData(bank: Bank) {
        call(object {})
        UserData.builder().bank(bank).maxImportBatch(0).build().save({
            MyUser.data = it
            onUserDataFound(bank)
            log("New ${UserData::class.java.simpleName} saved")
        }, {
            error("Could not save new ${UserData::class.java.simpleName}\n${it.get()}")
            MyUser.setNull()
            signout()
        })
    }

    /**
     * [setName], [setEmail], and [setImage] with
     * @param account [GoogleSignInAccount]
     */
    private fun updateUI(account: GoogleSignInAccount? = null) {
        call(object {})
        val v = getHeader()
        setEmail(account?.email, v)
        setName(account?.displayName, v)
        GlobalScope.launch {
            log("Getting drawable")
            val drawable = account?.photoUrl?.let { uri ->
                Util.urlToDrawable(uri.toString())
            }
            withContext(Dispatchers.Main) {
                setImage(drawable, v)
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
    private fun setImage(image: Drawable?, headerView: View? = getHeader()) {
        call(object {})
        headerView?.findViewById<ImageView>(R.id.nav_header_image)?.apply {
            if (image == null) {
                setImageResource(R.drawable.pog)
            } else {
                setImageDrawable(image)
            }
        }
    }

    /**
     * Get [View] of header from source [Activity]
     * @return [View]
     */
    private fun getHeader(): View? {
        call(object {})
        return activity?.findViewById<NavigationView>(R.id.nav_view)?.getHeaderView(0)
    }

    fun notSignedIn() {
        toast("Not signed in")
    }
}