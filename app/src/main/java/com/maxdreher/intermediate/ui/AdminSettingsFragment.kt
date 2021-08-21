package com.maxdreher.intermediate.ui

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.maxdreher.extensions.PreferenceFragmentCompatBase
import com.maxdreher.intermediate.R
import com.maxdreher.intermediate.keys.Keys
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
class AdminSettingsFragment : PreferenceFragmentCompatBase(R.xml.admin_settings), IPlaidBase {

    override val activity: ComponentActivity? = getActivity()
    override val resultCaller: ActivityResultCaller = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super<PreferenceFragmentCompatBase>.onCreate(savedInstanceState)
        super<IPlaidBase>.onCreate(savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState, rootKey)
        goto()
        shows()
        adds()
    }

    private fun goto() {
        findPreference("gotoSettings") {
            findNavController().navigate(R.id.settingsFragment)
        }
    }

    private fun adds() {
        findPreference("addMyself") {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId == null) {
                error("Null user id?")
                return@findPreference
            }
            for (me in Keys.MY_ACCESS_TOKENS) {
                me.run {
                    onPublicTokenExchangeSuccess(
                        userId,
                        institutionID = ins,
                        token = id,
                        populateData = false
                    )
                }
            }
            GlobalScope.launch {
                delay(5000)
                populateBankDatasForUser(userId)
            }
        }
    }

    private fun shows() {
        findPreference("showLogos") {
            Firebase.firestore.collection("banks")
                .get().addOnSuccessListener { res ->
                    val ll = LinearLayout(getContext())
                    res.documents
                        .map { it["logo"] as String }
                        .forEach { logo ->
                            val iv = ImageView(getContext())
                            val decode = Base64.decode(logo, Base64.DEFAULT)
                            iv.setImageBitmap(
                                BitmapFactory.decodeByteArray(
                                    decode,
                                    0,
                                    decode.size
                                )
                            )
                            ll.addView(iv)
                        }
                    alertBuilder("Logos")
                        .setView(ll)
                        .show()

                }.addOnFailureListener {
                    error("Bad")
                }
        }
    }
}