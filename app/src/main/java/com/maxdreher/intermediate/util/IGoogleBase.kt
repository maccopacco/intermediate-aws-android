package com.maxdreher.intermediate.util

import android.app.Activity
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.predicate.QueryPredicates
import com.amplifyframework.datastore.generated.model.Update
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.UserData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.navigation.NavigationView
import com.maxdreher.Util
import com.maxdreher.amphelper.AmpHelper
import com.maxdreher.amphelper.AmpHelperQ
import com.maxdreher.extensions.IGoogleBaseBase
import com.maxdreher.intermediate.MyUser
import com.maxdreher.intermediate.R
import com.maxdreher.intermediate.RequestCode
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

    override fun onSignoutFail(exception: Exception) {
        super.onSignoutFail(exception)
    }

    /**
     * Update account data ([MyUser], meaning potentially create new users), and update ui ([setAccount])
     */
    private fun setAccount(account: GoogleSignInAccount? = null) {
        account?.let {
            AmpHelperQ<User>().apply {
                Amplify.DataStore.query(User::class.java, User.GOOGLE_ID.eq(account.id), g, b)
                afterWait(
                    {
                        when (it.size) {
                            0 -> {
                                toast("No user found")
                                createUser(account)
                            }
                            1 -> {
                                MyUser.user = it[0]
                                updateUI(account)
                                checkAndCreateUserData(MyUser.user)
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
                    },
                    {
                        error("Cannot get ${User::class.java.simpleName}s from Amplify\n${it.message}")
                        it.printStackTrace()
                    })
            }
        } ?: run { MyUser.setNull(); updateUI() }
    }

    /**
     * Check state of [Update] (make sure we're online),
     *  then call [rawCreateUserData]
     */
    private fun createUser(account: GoogleSignInAccount) {
        AmpHelperQ<Update>().apply {
            Amplify.DataStore.query(Update::class.java, QueryPredicates.all(), g, b)
            afterWait(
                {
                    if (it.isNotEmpty()) {
                        rawCreateUser(account)
                    } else {
                        error(
                            "It doesn't appear that this account has synced to the " +
                                    "internet yet\n\nA new user cannot be created until we can " +
                                    "confirm it doesn't exist yet"
                        )
                    }
                },
                { error("Could not find ${Update::class.java.simpleName} instance, cannot make new User") })
        }
    }

    /**
     * Create new [User] from [account]
     */
    private fun rawCreateUser(account: GoogleSignInAccount) {
        User.builder()
            .googleId(account.id)
            .originalEmail(account.email)
            .build().let {
                MyUser.user = it
                AmpHelper<User>().apply {
                    Amplify.DataStore.save(it, g, b)
                    afterWait(
                        {
                            toast("New user saved")
                            updateUI(account)
                        },
                        { ex -> error("New user not saved!\n${ex.message}") })
                }
                rawCreateUserData(it)
            }
    }

    /**
     * Check quantity of [UserData] in [user] and error, create, or load appropriately
     */
    private fun checkAndCreateUserData(user: User?) {
        user?.let {
            AmpHelperQ<UserData>().apply {
                Amplify.DataStore.query(UserData::class.java, UserData.USER.eq(user.id), g, b)
                afterWait({
                    when {
                        it.isEmpty() -> {
                            rawCreateUserData(user)
                        }
                        it.size == 1 -> {
                            MyUser.data = it[0]
                            log("1 ${UserData::class.java.simpleName} found for ${User::class.java.simpleName}")
                        }
                        else -> {
                            error("Too many ${UserData::class.java.simpleName}s found for user!")
                            MyUser.setNull()
                        }
                    }
                }, { error("Could not query ${UserData::class.java.simpleName}") })
            }
        } ?: run { MyUser.setNull() }
    }

    /**
     * Create and save new [UserData] for
     * @param user
     */
    private fun rawCreateUserData(user: User?) {
        AmpHelper<UserData>().apply {
            Amplify.DataStore.save(UserData.builder().user(user).build(), g, b)
            afterWait(
                {
                    MyUser.data = it
                    log("User data saved")
                },
                {
                    error("Could not save user data\n${it.message}")
                    it.printStackTrace()
                    MyUser.setNull()
                    signout()
                })
        }
    }

    /**
     * [setName], [setEmail], and [setImage] with
     * @param account [GoogleSignInAccount]
     */
    private fun updateUI(account: GoogleSignInAccount? = null) {
        getHeader().let { view ->
            setEmail(account?.email, view)
            setName(account?.displayName, view)
            GlobalScope.launch {
                val drawable = account?.photoUrl?.let { uri ->
                    Util.urlToDrawable(uri.toString())
                }
                withContext(Dispatchers.Main) {
                    setImage(drawable, view)
                }
            }
        }
    }

    /**
     * @param name to update name [TextView] with
     */
    private fun setName(name: String?, headerView: View? = getHeader()) {
        headerView?.findViewById<TextView>(R.id.nav_header_name)?.text = name ?: ""
    }

    /**
     * @param email to update email [TextView] with
     */
    private fun setEmail(email: String?, headerView: View? = getHeader()) {
        headerView?.findViewById<TextView>(R.id.nav_header_email)?.text = email ?: ""
    }

    /**
     * @param image to update profile picture view with
     */
    private fun setImage(image: Drawable?, headerView: View? = getHeader()) {
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
        return fragment.activity?.findViewById<NavigationView>(R.id.nav_view)?.getHeaderView(0)
    }
}