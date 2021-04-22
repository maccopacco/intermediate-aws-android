package com.maxdreher.intermediate.fragments

import android.os.Bundle
import androidx.preference.Preference
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.predicate.QueryPredicates
import com.amplifyframework.datastore.generated.model.Update
import com.maxdreher.amphelper.AmpHelper
import com.maxdreher.amphelper.AmpHelperD
import com.maxdreher.extensions.PreferenceFragmentCompatBase
import com.maxdreher.intermediate.R
import java.text.SimpleDateFormat
import java.util.*

class AdminSettings : PreferenceFragmentCompatBase(R.xml.admin_settings) {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState, rootKey)

        findPreference<Preference>("addUpdate")?.setOnPreferenceClickListener {
            AmpHelper<Update>().apply {
                Update.builder()
                    .date(SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))
                    .build().let {
                        Amplify.DataStore.save(it, g, b)
                    }
                afterWait({ toast("Saved") }, { loge("Not saved") })
            }
            true
        }

        findPreference<Preference>("clearUpdates")?.setOnPreferenceClickListener {
            AmpHelperD().apply {
                Amplify.DataStore.delete(Update::class.java, QueryPredicates.all(), g, b)
                afterWait({ toast("Deleted all") }, { loge("Not deleted") })
            }
            true
        }
    }
}