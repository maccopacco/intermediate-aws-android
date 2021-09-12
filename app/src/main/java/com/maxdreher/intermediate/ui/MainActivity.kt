package com.maxdreher.intermediate.ui

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import androidx.annotation.DrawableRes
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.maxdreher.BuildConfig
import com.maxdreher.extensions.ActivityBase
import com.maxdreher.intermediate.MyUser.getUserUid
import com.maxdreher.intermediate.R
import com.maxdreher.intermediate.databinding.ActivityMainBinding
import com.maxdreher.intermediate.util.FbEmulators

class MainActivity :
    ActivityBase<ActivityMainBinding>(ActivityMainBinding::class.java),
    IPlaidBase {

    override val activity: ComponentActivity = this
    override val resultCaller: ActivityResultCaller = this

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val menuItems = mutableListOf<InternalMenuItem>()

    private infix fun String.whenClicked(onClick: () -> Unit) {
        InternalMenuItem(this, InternalMenuItem.emptyIcon, onClick).also {
            menuItems.add(it)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super<ActivityBase>.onCreate(savedInstanceState)
        super<IPlaidBase>.onCreate(savedInstanceState)

        setSupportActionBar(findViewById(R.id.toolbar))

        Firebase.firestore.clearPersistence()
            .addOnFailureListener {
                toast("Could not clear Firestore persistence")
            }.addOnSuccessListener {
                log("Firestore persistence cleared")
            }

        FbEmulators.setup()
        FirebaseApp.initializeApp(applicationContext)


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment)

        if (BuildConfig.DEBUG) {
            "Admin settings" whenClicked {
                log("Going to admit settings")
                navController.navigate(R.id.adminSettings)
            }
            "Trigger Link" whenClicked {
                triggerLink()
            }

            "Trigger Signin" whenClicked {
                signIn()
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
}