package com.maxdreher.intermediate.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.DrawableRes
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.Page
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.datastore.AWSDataStorePlugin
import com.amplifyframework.datastore.DataStoreChannelEventName
import com.amplifyframework.datastore.events.NetworkStatusEvent
import com.amplifyframework.datastore.generated.model.Bank
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.hub.HubChannel
import com.google.android.material.navigation.NavigationView
import com.maxdreher.extensions.ActivityBase
import com.maxdreher.intermediate.BuildConfig
import com.maxdreher.intermediate.MyUser
import com.maxdreher.intermediate.R

class MainActivity : ActivityBase(R.layout.activity_main), IPlaidBase {

    override val activity: Activity = this

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val menuItems = mutableListOf<InternalMenuItem>()

    private infix fun String.whenClicked(onClick: () -> Unit) {
        InternalMenuItem(this, InternalMenuItem.emptyIcon, onClick).also {
            menuItems.add(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(findViewById(R.id.toolbar))

        setupAmplify()

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        if (BuildConfig.DEBUG) {
            "Admin settings" whenClicked {
                log("Going to admit settings")
                navController.navigate(R.id.adminSettings)
            }
            "Trigger Link" whenClicked {
                triggerLink()
            }
        }

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.settingsFragment, R.id.plaidFragment
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun setupAmplify() {
        call(object {})
        try {
            Amplify.addPlugin(AWSDataStorePlugin())
            Amplify.addPlugin(AWSApiPlugin())
            Amplify.configure(this)
            toast("Initialized Amplify")

            onNetwork()
            startDatastore()
        } catch (error: AmplifyException) {
            error("Could not initialize Amplify\n${error.message}")
            error.printStackTrace()
        }
    }

    private fun onNetwork() {
        call(object {})
        val ev = DataStoreChannelEventName.NETWORK_STATUS.toString()
        Amplify.Hub.subscribe(
            HubChannel.DATASTORE,
            { it.name == ev },
            {
                log("$ev: $it")
                it.data.apply {
                    if (this is NetworkStatusEvent) {
                        log("Network status updated: $active")
                        MyUser.setOnline(active)
                    }
                }
            })
    }

    private fun startDatastore() {
        call(object {})
        Amplify.DataStore.start(
            {
                log("DataStore started")
                signin()
            }, {
                loge("Could not start DataStore ${it.message}")
                it.printStackTrace()
            })
    }

    /**
     * Create [MenuItem] from [InternalMenuItem]s stored in [menuItems]
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuItems.forEachIndexed { index, mi ->
            menu.add(0, index, index, mi.name).let {
                if (mi.icon != -1) {
                    it.setIcon(mi.icon)
                }
                it.setShowAsAction(mi.showAsAction)
            }
        }
        return true
    }

    /**
     * Call [InternalMenuItem.onClick] if [item] in [menuItems]
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId in 0 until menuItems.size) {
            menuItems[item.itemId].onClick.invoke()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    /**
     * Class with [name], [onClick] function, to be mapped into [menuItems]
     */
    private class InternalMenuItem(
        val name: String, @DrawableRes val icon: Int = emptyIcon,
        val onClick: () -> Unit
    ) {
        companion object {
            const val emptyIcon = -1
        }

        val showAsAction =
            if (icon == -1) MenuItem.SHOW_AS_ACTION_NEVER else MenuItem.SHOW_AS_ACTION_IF_ROOM

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super<ActivityBase>.onActivityResult(requestCode, resultCode, data)
        super<IPlaidBase>.onActivityResult(requestCode, resultCode, data)
    }

    override fun onUserDataFound(bank: Bank) {
        super.onUserDataFound(bank)
        val frag = supportFragmentManager.fragments.let { list ->
            for (frag in list) {
                frag.childFragmentManager.fragments.find { it is IPlaidBase }?.also {
                    return@let it
                }
            }
        }

        frag.let {
            if (it is IPlaidBase) {
                it.onUserDataFound(bank)
            }
        }
    }
}