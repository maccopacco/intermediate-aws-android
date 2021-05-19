package com.maxdreher.intermediate.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.ListPreference
import com.amplifyframework.core.model.Model
import com.amplifyframework.core.model.query.Page
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.core.model.query.predicate.QueryPredicate
import com.amplifyframework.datastore.generated.model.*
import com.maxdreher.*
import com.maxdreher.extensions.IGoogleBaseBase
import com.maxdreher.extensions.PreferenceFragmentCompatBase
import com.maxdreher.intermediate.*
import com.maxdreher.intermediate.R
import com.maxdreher.intermediate.ui.IPlaidBase.Companion.plaidClient
import com.maxdreher.intermediate.util.ParseSimpleToAmp
import com.plaid.client.request.AuthGetRequest
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean


class SettingsFragment : PreferenceFragmentCompatBase(R.xml.preferences), IPlaidBase {

    override val activity: Activity?
        get() = getActivity()

    private var listPreference: ListPreference? = null

    private var accountToSaveTo: Account? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState, rootKey)

        google()
        app()
        import()
    }

    override fun onUserDataFound(bank: Bank) {
        super.onUserDataFound(bank)
        call(object {})
        updateBanks()
    }

    private fun updateBanks() {
        call(object {})
        listPreference?.apply {
            Bank::class.query(Where.matches(Bank.USER.eq(MyUser.user?.id))
                .sorted(Bank.INSTITUTION_NAME.descending()),
                { list ->
                    if (list.isEmpty()) {
                        updateBankFailure()
                    } else {
                        val badBanks = list.filter { it.institutionName == null }
                        if (badBanks.isNotEmpty()) {
                            updateBankFailure()
                            getBankNamesAndIDs(badBanks)
                        } else {
                            isEnabled = true
                            entryValues = list.map { it.id }.toTypedArray()
                            entries = list.map { bank ->
                                bank.institutionName
                            }.toTypedArray()
                        }
                    }
                }, { updateBankFailure() })
        }
    }

    private fun getBankNamesAndIDs(list: List<Bank>) {
        call(object {})
        for (bank in list.dropLast(1)) {
            populateInstitutionLogo(bank)
        }
        //populate all logos, but attempt to update banks when the file is done
        populateInstitutionLogo(list.last()) { updateBanks() }
    }

    private fun updateBankFailure() {
        call(object {})
        listPreference?.apply {
            setEntryValues(R.array.chooseBankValues)
            setEntries(R.array.chooseBankText)
            isEnabled = false
        }
    }

    private fun app() {
        call(object {})
        listPreference = (findPreference("appChooseBank") as ListPreference).apply {
            updateBanks()
            setOnPreferenceChangeListener { _, newValue ->
                onNewBankSelected(newValue)
                true
            }
        }
        updateBanks()

        findPreference("appShowAccountNumbers") {
            MyUser.user?.let { user ->
                Bank::class.query(Bank.USER.eq(user.id), { banks ->
                    GlobalScope.launch {
                        banks.joinToString("\n") { bank ->
                            val result = plaidClient.service()
                                .authGet(AuthGetRequest(bank.plaidAccessToken)).execute()
                            "${bank.institutionName}:\n${
                                result.body()?.numbers?.ach?.joinToString("\n") {
                                    "\taccount: ${it.account}\n" +
                                            "\trouting: ${it.routing}\n" +
                                            it.wireRouting.let { routing ->
                                                if (routing != null) "\twire: $routing"
                                                else ""
                                            }
                                } ?: "No account numbers found\n"
                            }"
                        }.also { message ->
                            withContext(Dispatchers.Main) {
                                alert("Account numbers (ACH #s)", message)
                            }
                        }
                    }
                }, {
                    error("Could not find banks for you\n{${it.message}}")
                    it.printStackTrace()
                })
            } ?: toast("Not signed in")
        }

        findPreference("appDelete")
        {
            MyUser.user?.let { user ->
                Bank::class.query(Bank.USER.eq(user.id), { banks ->
                    val predicate = banks.map { UserData.BANK.eq(it.id) as QueryPredicate }
                        .reduce { acc, value ->
                            acc.or(value)
                        }

                    UserData::class.delete(predicate,
                        { toast("User data deleted");signout() },
                        { error("User data could not be deleted") })
                }, { error("Could not get ${Bank::class.java.simpleName}s for user") })
            } ?: badSignin()
        }
    }

    private fun onNewBankSelected(newValue: Any?) {
        call(object {})
        Bank::class.query(Where.matches(Bank.ID.eq(newValue)).paginated(Page.firstResult()),
            {
                if (it.isEmpty()) {
                    error("No bank returned? How'd you do that?")
                    updateBankFailure()
                } else {
                    it[0].copyOfBuilder()
                        .lastTouchedTime(Util.getSaneDate())
                        .build()
                        .save(this@SettingsFragment) {
                            findBankAndUserData()
                            setBankImage()
                            toast("${it.institutionName} selected")
                        }
                }
            },
            {
                updateBankFailure()
                error("Bank could not be found\n${it.message}")
                it.printStackTrace()
            })
    }

    private fun import() {
        call(object {})
        findPreference("importSimpleJSON") {
            startSimpleImport()
        }
    }


    private fun google() {
        call(object {})
        findPreference("googleAccountSignin") {
            if (IGoogleBaseBase.account != null) {
                signout()?.addOnSuccessListener { signin() }
            } else {
                signin()
            }
        }
        findPreference("googleAccountSignout") { signout() }
    }

    private fun startSimpleImport() {
        call(object {})
        //account chooser, then
        openFile(MIME_TYPE.CSV, RequestCode.SIMPLE_FILE_CODE_JSON)
    }

    private fun openFile(mimeType: MIME_TYPE, code: Int) {
        call(object {})
        if (MyUser.user == null) {
            return
        }
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = mimeType.value
            addCategory(Intent.CATEGORY_OPENABLE)
        }

        startActivityForResult(
            Intent.createChooser(intent, "Select a File to Upload"),
            code
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super<IPlaidBase>.onActivityResult(requestCode, resultCode, data)
        call(object {})
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RequestCode.SIMPLE_FILE_CODE_JSON -> saveSimpleData(data)
            }
        }
    }

    /**
     * Saves data from Simple from file picker [Intent] which stores the files [Uri] in
     * [Intent.getData]
     */
    private fun saveSimpleData(data: Intent?) {
        call(object {})
        accountToSaveTo?.let { account ->
            GlobalScope.launch {
                withContext(Dispatchers.Default) {
                    MyUser.data?.let { userData ->
                        val parseResult = ParseSimpleToAmp.convert(
                            data?.data,
                            this@SettingsFragment,
                            userData,
                            account,
                            15
                        )
                        if (parseResult == null) {
                            toast("Bad data from Simple")
                            return@let
                        }
                        GlobalScope.launch {
                            saveSimpleResults(parseResult)
                        }
                    } ?: badSignin()
                }
            }
        } ?: error(
            "The account you wanted to save to is gone, how'd you get here? " +
                    "\nLeave and don't come back"
        )
    }

    private fun saveSimpleResults(parseResult: List<List<Model>>) {
        call(object {})
        val firstError = AtomicBoolean(true)
        val errorConsumer: (Model, Exception, Int) -> Unit = { model, ex, _ ->
            if (firstError.get()) {
                alert("Errors found in Simple Import", "Will have to send log files to Max")
                firstError.set(false)
            }
            loge("Could not save ${model.modelName}. ID: ${model.id}")
            ex.printStackTrace()
        }
        GlobalScope.launch {
            runBlocking {
                parseResult.forEachIndexed { index, list ->
                    launch {
                        Util.saveModels(this@SettingsFragment, list, index, errorConsumer)
                    }
                }
            }
            updateLastItem()
        }
    }


    private fun badSignin() {
        call(object {})
        error("Not signed in")
    }

}