package com.maxdreher.intermediate.util

import android.net.Uri
import com.amplifyframework.core.model.Model
import com.amplifyframework.datastore.generated.model.*
import com.maxdreher.Util
import com.maxdreher.Util.Date.toAmplifyDate
import com.maxdreher.Util.Date.toAmplifyDateTime
import com.maxdreher.Util.Date.toDateTime
import com.maxdreher.Util.Date.toSimpleDate
import com.maxdreher.extensions.IContextBase
import org.json.JSONObject
import java.lang.Integer.min
import java.util.*

object ParseSimpleToAmp {

    /**
     * @return [List] of [List] of [Model]s to be saved. Each inner [List] represents
     * the nested models (like [Location]s in a [Transaction] and so on)
     */
    fun convert(
        uri: Uri?,
        cb: IContextBase,
        userData: UserData,
        account: Account,
        maxAmount: Int? = null
    ): List<List<Model>>? {
        uri?.let {
            val text: String?
            try {
                text = readFile(uri, cb)
            } catch (e: java.lang.Exception) {
                cb.error("Could not read file:\n${e.message}")
                return null
            }
            return try {
                convertJSONToAmp(text, userData, account, maxAmount)
            } catch (e: Exception) {
                cb.error("Could not parse file\n${e.message}")
                e.printStackTrace()
                null
            }
        } ?: cb.error("Null URI! (somehow, not a valid file)")
        return null
    }

    private fun convertJSONToAmp(
        text: String,
        userData: UserData,
        account: Account,
        maxAmount: Int?
    ): List<List<Model>> {
        val importBatch = userData.maxImportBatch + 1
        val importDate = Date().toAmplifyDateTime()
        val importSource = "Simple_JSON"

        val json = JSONObject(text)
        val transactions = json.getJSONArray("transactions")

        val size = transactions.length()
        val length = maxAmount?.let {
            min(it, size)
        } ?: size

        return (0 until length).map {
            val rawT = transactions.getJSONObject(it)
            try {
                return@map mutableListOf<Model>().apply {
                    val t = Transaction.Builder()
                        .userData(userData)
                        .account(account.id)
                        //amount
                        .run {
                            val a = rawT.getJSONObject("amounts").getDouble("amount") / 10000
                            val sign = if (rawT.getString("bookkeeping_type") == "debit") 1 else -1
                            amount(a * sign)
                        }
                        //date
                        .apply {
                            val date = Date(
                                rawT.getJSONObject("times")
                                    .getLong("when_recorded")
                            )
                            date(date.toAmplifyDate())
                            exactTime(date.toAmplifyDateTime())
                        }
                        .importBatch(importBatch)
                        .importSource(importSource)
                        .importDate(importDate)
                        .pending(
                            false
                            /** rawT.getBoolean("is_hold")*/
                        )
                        .name(rawT.getString("raw_description"))
                        //memo & description
                        .apply {
                            if (rawT.has("memo")) {
                                memo(rawT.getString("memo"))
                            }
                            //Discrepancy between Simple / Plaid naming conventions.
                            //The actual 'description' in Plaid doesn't appear to be used
                            if (rawT.has("description")) {
                                overrideName(rawT.getString("description"))
                            }
                        }
                        //categories
                        .apply {
                            val categories = rawT.getJSONArray("categories")
                            if (categories.length() > 0) {
                                val cat = categories.getJSONObject(0)
                                category(listOf(cat.getString("name")))
                                categoryFolder(cat.getString("folder"))
                            }
                        }
                        .build().also { self -> add(self) }

                    if (rawT.has("geo")) {
                        val geo = rawT.getJSONObject("geo")
                        Location.Builder()
                            .transaction(t)
                            .city(geo.getString("city"))
                            .apply {
                                if (geo.has("country")) {
                                    country(geo.getString("country"))
                                }
                                if (geo.has("lat")) {
                                    lat(geo.getDouble("lat"))
                                }
                                if (geo.has("lon")) {
                                    lon(geo.getDouble("lon"))
                                }
                            }
                            .postalCode(geo.getString("zip"))
                            .build().also { self -> add(self) }
                    }
                }
            } catch (e: java.lang.Exception) {
                throw Exception("On line $rawT\n${e.message}", e.cause)
            }
        }
    }

    /**
     * Opens [uri] and @return one big [String]
     */
    private fun readFile(uri: Uri, cb: IContextBase): String {
        val sb = StringBuilder()
        cb.getContext()!!.contentResolver!!.openInputStream(uri)?.use { inputStream ->
            Scanner(inputStream).use {
                while (it.hasNextLine()) {
                    sb.append(it.nextLine()).append("\n")
                }
            }
        } ?: throw Exception("Could not open file")
        return sb.toString()
    }
}