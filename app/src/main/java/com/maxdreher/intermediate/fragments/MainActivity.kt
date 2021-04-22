package com.maxdreher.intermediate.fragments

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
import com.amplifyframework.datastore.AWSDataStorePlugin
import com.google.android.material.navigation.NavigationView
import com.maxdreher.extensions.ActivityBase
import com.maxdreher.intermediate.BuildConfig
import com.maxdreher.intermediate.R

class MainActivity : ActivityBase(R.layout.activity_main) {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val menuItems = mutableListOf<InternalMenuItem>().apply {
        if (BuildConfig.DEBUG) {
//            add(InternalMenuItem("Delete all Users") {
//                AmpHelperD().apply {
//                    Amplify.DataStore.delete(User::class.java, QueryPredicates.all(), g, b)
//                    afterWait({ toast("Delteed :) ") }, { error("Ahh!!! ${it.message}") })
//                }
//            })
        }
//        add(InternalMenuItem("init amp") { AmplifyInitializer.init(this@MainActivity) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(findViewById(R.id.toolbar))

        try {
            Amplify.addPlugin(AWSDataStorePlugin())
            Amplify.addPlugin(AWSApiPlugin())
            Amplify.configure(this)
            toast("Initialized Amplify")
        } catch (error: AmplifyException) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", error)
        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        if (BuildConfig.DEBUG) {
            menuItems.add(InternalMenuItem("Admin settings") {
                navController.navigate(R.id.adminSettings)
            })
        }

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.settingsFragment, R.id.plaidFragment, R.id.SQLFragment
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
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
        val name: String, @DrawableRes val icon: Int = -1,
        val onClick: () -> Unit
    ) {
        val showAsAction =
            if (icon == -1) MenuItem.SHOW_AS_ACTION_NEVER else MenuItem.SHOW_AS_ACTION_IF_ROOM
    }

}