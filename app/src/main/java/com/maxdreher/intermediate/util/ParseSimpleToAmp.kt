package com.maxdreher.intermediate.util

import android.net.Uri
import com.amplifyframework.core.model.Model
import com.amplifyframework.datastore.generated.model.*
import com.maxdreher.Util
import com.maxdreher.extensions.IContextBase
import org.json.JSONObject
import java.util.*

object ParseSimpleToAmp {
    /**
     * @return [List] of [List] of [Model]s to be saved. Each inner [List] represents
     * the nested models (like [Transaction]s in a [TransactionWrapper] and so on)
     */
    fun convert(
        uri: Uri?,
        cb: IContextBase,
        userData: UserData,
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
                convertJSONToAmp(text, userData)
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
        userData: UserData
    ): List<List<Model>> {
        val importBatch = userData.maxImportBatch + 1
        val importDate = Util.getSaneDate()
        val importSource = "Simple_JSON"

        val json = JSONObject(text)
        val transactions = json.getJSONArray("transactions")

        return (0 until transactions.length()).map {
            val rawT = transactions.getJSONObject(it)
            try {
                return@map mutableListOf<Model>().apply {
                    val t = Transaction.Builder()
                        .apply {
                            val a = rawT.getJSONObject("amounts").getDouble("amount") / 10000
                            val sign = if (rawT.getString("bookkeeping_type") == "debit") 1 else -1
                            amount(a * sign)
                        }
                        .apply {
                            val date = Date(
                                rawT.getJSONObject("times")
                                    .getLong("when_recorded")
                            )
                            date(Util.simpleDateFormat.format(date))
                            exactTime(Util.saneDateFormat.format(date))
                        }
                        .pending(false)
//                        .pending(rawT.getBoolean("is_hold"))
                        .name(rawT.getString("raw_description"))
                        .apply {
                            val categories = rawT.getJSONArray("categories")
                            if (categories.length() > 0) {
                                val cat = categories.getJSONObject(0)
                                category(listOf(cat.getString("name")))
                                categoryFolder(cat.getString("folder"))
                            }
                        }
                        .build().also {
                            add(it)
                        }
                    val w = TransactionWrapper.builder()
                        .userData(userData)
                        .transaction(t)
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
                        .importBatch(importBatch)
                        .importSource(importSource)
                        .importDate(importDate)
                        .build().also { add(it) }
                    if (rawT.has("geo")) {
                        val geo = rawT.getJSONObject("geo")
                        val loc = Location.Builder()
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
                            .build().also { add(it) }
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