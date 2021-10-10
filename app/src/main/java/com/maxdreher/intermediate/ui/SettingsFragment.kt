package com.maxdreher.intermediate.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import androidx.preference.ListPreference
import com.maxdreher.extensions.PreferenceFragmentCompatBase
import com.maxdreher.intermediate.R


class SettingsFragment : PreferenceFragmentCompatBase(R.xml.preferences), IPlaidBase {

    override val activity: ComponentActivity? = getActivity()
    override val resultCaller: ActivityResultCaller = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super<PreferenceFragmentCompatBase>.onCreate(savedInstanceState)
        super<IPlaidBase>.onCreate(savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState, rootKey)
        google()
    }

    private fun app() {
        call(object {})
    }

    private fun google() {
        call(object {})
        findPreference("googleAccountSignin") { signIn() }
        findPreference("googleAccountSignout") { signOut() }
    }

}