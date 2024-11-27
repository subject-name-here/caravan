package com.unicorns.invisible.caravan.save

import android.widget.Toast
import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.Style
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.save
import com.unicorns.invisible.caravan.snapshotsClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONObject
import java.io.File
import java.nio.charset.StandardCharsets


val json = Json {
    allowStructuredMapKeys = true
    ignoreUnknownKeys = true
}


private fun getLocalSaveFile(activity: MainActivity): File {
    return activity.filesDir.resolve("saveMkII").apply { if (!exists()) createNewFile() }
}

fun saveData(activity: MainActivity) {
    val localSave = getLocalSaveFile(activity)
    try {
        val text = json.encodeToString(save)
        localSave.bufferedWriter().use {
            it.write(text)
        }
    } catch (_: Exception) {
        MainScope().launch {
            Toast.makeText(
                activity,
                "Failed to save locally :(",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    saveOnGD(activity)
}

private fun saveOnGD(activity: MainActivity) {
    if (snapshotsClient == null)
        return

    val bytes = json.encodeToString(save).toByteArray(StandardCharsets.UTF_8)
    CoroutineScope(Dispatchers.IO).launch { activity.uploadDataToDrive(bytes) }
}


suspend fun loadSave(activity: MainActivity): Save? {
    try {
        return loadLocalSave(activity) ?: run {
            val data = activity.fetchDataFromDrive()?.toString(StandardCharsets.UTF_8)
            if (data != null && data != "" && data != "null") {
                json.decodeFromString<Save>(data)
            } else {
                null
            }
        }
    } catch (_: Exception) {
        MainScope().launch {
            Toast.makeText(
                activity,
                "Failed to load save!!",
                Toast.LENGTH_SHORT
            ).show()
        }
        return null
    }
}

private fun loadLocalSave(activity: MainActivity): Save? {
    val saveFile = getLocalSaveFile(activity)
    return try {
        saveFile.bufferedReader().use {
            val text = it.readText()
            json.decodeFromString<Save>(text)
        }
    } catch (_: Exception) {
        null
    }
}

fun processOldSave(activity: MainActivity) {
    CoroutineScope(Dispatchers.IO).launch {
        val oldSaveString = activity.fetchOldSaveFromDrive()?.toString(StandardCharsets.UTF_8) ?: return@launch
        val jsonObject = JSONObject(oldSaveString)
        val cards = jsonObject.getJSONArray("availableCards")
        val cardsLength = cards.length()
        val availableCards = save.availableCards
        availableCards.clear()
        repeat(cardsLength) {
            val cardJSONObject = cards.getJSONObject(it)
            val rank = Rank.valueOf(cardJSONObject.getString("rank"))
            val suit = Suit.valueOf(cardJSONObject.getString("suit"))
            val backString = cardJSONObject.getString("back")
            val back = when (backString) {
                "STANDARD" -> CardBack.STANDARD
                "TOPS" -> CardBack.TOPS
                "ULTRA_LUXE" -> CardBack.ULTRA_LUXE
                "GOMORRAH" -> CardBack.GOMORRAH
                "LUCKY_38" -> CardBack.LUCKY_38
                "VAULT_21" -> CardBack.VAULT_21
                else -> CardBack.MADNESS
            }
            val isAlt = cardJSONObject.optBoolean("isAlt", false)
            if (back == CardBack.MADNESS) {
                if (isAlt) {
                    availableCards.add(Card(rank, suit, back, false))
                }
            } else {
                availableCards.add(Card(rank, suit, back, isAlt))
            }
        }
        val ownedStyles = jsonObject.getJSONArray("ownedStyles")
        repeat(ownedStyles.length()) {
            val style = ownedStyles.getString(it)
            try {
                save.ownedStyles.add(Style.valueOf(style))
            } catch (_: Exception) {}
        }
        val caps = jsonObject.getInt("caps")
        save.capsInHand += caps
        val tickets = jsonObject.getInt("tickets")
        save.tickets += tickets
    }
}