package com.maxdreher.intermediate.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.Model
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.UserData
import com.maxdreher.MIME_TYPE
import com.maxdreher.amphelper.AmpHelper
import com.maxdreher.amphelper.AmpHelperD
import com.maxdreher.extensions.IGoogleBaseBase
import com.maxdreher.extensions.PreferenceFragmentCompatBase
import com.maxdreher.intermediate.MyUser
import com.maxdreher.intermediate.ParseSimpleToAmp
import com.maxdreher.intermediate.R
import com.maxdreher.intermediate.RequestCode.SIMPLE_FILE_CODE
import com.maxdreher.intermediate.util.IGoogleBase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class SettingsFragment : PreferenceFragmentCompatBase(R.xml.preferences), IGoogleBase {

    override val fragment: Fragment = this

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState, rootKey)

        findPreference<Preference>("googleAccountSignin")?.setOnPreferenceClickListener {
            if (IGoogleBaseBase.account != null) {
                signout()?.addOnSuccessListener { signin() }
            } else {
                signin()
            }
            true
        }

        findPreference<Preference>("googleAccountSignout")?.setOnPreferenceClickListener {
            signout()
            true
        }

        findPreference<Preference>("importSimple")?.setOnPreferenceClickListener {
            checkSignedIn { _, _ ->
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    type = MIME_TYPE.CSV.value
                    addCategory(Intent.CATEGORY_OPENABLE)
                }

                startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    SIMPLE_FILE_CODE
                )
            }
            true
        }

        findPreference<Preference>("intermediateDelete")?.setOnPreferenceClickListener {
            checkSignedIn { user, _ ->
                AmpHelperD().apply {
                    Amplify.DataStore.delete(
                        UserData::class.java, UserData.USER.eq(user.id), g, b
                    )
                    afterWait(
                        { toast("User data deleted");signout() },
                        { error("User data could not be deleted") })
                }
            }
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super<IGoogleBase>.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SIMPLE_FILE_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    saveSimpleData(data)
                }
            }
        }
    }

    /**
     * Saves data from Simple from file picker [Intent] which stores the files [Uri] in
     * [Intent.getData]
     */
    private fun saveSimpleData(data: Intent?) {
        checkSignedIn { _, userData ->
            ParseSimpleToAmp.convert(data?.data, this, userData)?.let { doubleModelList ->
                doubleModelList.take(30).forEachIndexed { index, list ->
                    GlobalScope.launch {
                        saveModels(list, index)
                    }
                }
            }
        }
    }

    /**
     * Recursively save [Model]s
     */
    private fun saveModels(list: List<Model>, batch: Int) {
        if (list.isNotEmpty()) {
            val model = list[0]
            AmpHelper<Model>().apply {
                Amplify.DataStore.save(model, g, b)
                afterWait(
                    {
                        log("[$batch] Saved ${model.modelName}, ${list.size - 1} left")
                        saveModels(
                            list.drop(1),
                            batch
                        ) //:(, recursion, best way I could think to do it
                    }, { loge("Error with ${model.modelName}, ${model.id}") })
            }
        }
    }

    /**
     * Helper function to first check for [MyUser.user] and [MyUser.data]
     */
    private fun checkSignedIn(onSignedIn: (User, UserData) -> Unit) {
        MyUser.run {
            user?.let { user ->
                data?.let { data ->
                    onSignedIn.invoke(user, data)
                }
            }
        } ?: error("Not signed in")
    }

}