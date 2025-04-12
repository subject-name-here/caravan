package com.unicorns.invisible.caravan.save

import android.widget.Toast
import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.activity
import com.unicorns.invisible.caravan.isSaveLoaded
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CardNumber
import com.unicorns.invisible.caravan.model.primitives.RankFace
import com.unicorns.invisible.caravan.model.primitives.RankNumber
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.save
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import okio.ByteString.Companion.toByteString
import org.json.JSONObject
import java.io.File
import java.nio.charset.StandardCharsets


val json = Json {
    allowStructuredMapKeys = true
    ignoreUnknownKeys = true
}

private fun getOldLocalSaveFile(activity: MainActivity): ByteArray? {
    val file = activity.filesDir.resolve("saveMkII") // TODO
    return if (file.exists()) file.readBytes() else null
}

private fun getLocalSaveFile(activity: MainActivity): File {
    return activity.filesDir.resolve("saveMk3").apply { if (!exists()) createNewFile() }
}

actual fun saveData() {
    val act = activity
    if (isSaveLoaded != true || act == null) {
        return
    }

    val text = json.encodeToString(save)
    try {
        val localSave = getLocalSaveFile(act)
        localSave.bufferedWriter().use {
            it.write(text)
        }
    } catch (_: Exception) {
        MainScope().launch {
            Toast.makeText(
                act,
                "Failed to save locally :(",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val bytes = text.toByteArray(StandardCharsets.UTF_8)
    CoroutineScope(Dispatchers.IO).launch { activity?.uploadDataToDrive(bytes) }
}

fun failToLoad(message: String) {
    MainScope().launch {
        Toast.makeText(
            activity ?: return@launch,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}

actual suspend fun loadGDSave(): Save? {
    return try {
        val data = activity?.fetchDataFromDrive()?.toString(StandardCharsets.UTF_8)
        if (data != null && data != "" && data != "null") {
            json.decodeFromString<Save>(data)
        } else {
            throw Exception()
        }
    } catch (_: Exception) {
        failToLoad("Failed to load from GD :(")
        activity?.fetchOldSave()?.let { oldSaveToNewSave(it) }
    }
}

actual suspend fun loadLocalSave(): Save? {
    val act = activity
    if (act == null) return null
    val saveFile = getLocalSaveFile(act)
    return try {
        saveFile.bufferedReader().use {
            val text = it.readText()
            json.decodeFromString<Save>(text)
        }
    } catch (_: Exception) {
        failToLoad("Failed to load local save :(")
        getOldLocalSaveFile(act)?.let { oldSaveToNewSave(it) }
    }
}

private fun oldSaveToNewSave(oldFile: ByteArray): Save? {
    return try {
        val save = Save("")
        val old = oldFile.toByteString().utf8()
        val json = JSONObject(old)
        val cards = json.getJSONArray("availableCards")
        repeat(cards.length()) {
            val card = cards.getJSONObject(it)
            val rank = card.getString("rank")
            val suit = card.getString("suit")
            val back = card.getString("back")
            val alt = card.getBoolean("isAlt")
            val suitWP = Suit.valueOf(suit)
            val backWP = when (back to alt) {
                "STANDARD" to false -> CardBack.STANDARD_RARE
                "LUCKY_38" to false -> CardBack.LUCKY_38
                "LUCKY_38" to true -> CardBack.LUCKY_38_SPECIAL
                "VAULT_21" to false -> CardBack.VAULT_21_DAY
                "VAULT_21" to true -> CardBack.VAULT_21_NIGHT
                "TOPS" to false -> CardBack.TOPS
                "TOPS" to true -> CardBack.TOPS_RED
                "ULTRA_LUXE" to false -> CardBack.ULTRA_LUXE
                "ULTRA_LUXE" to true -> CardBack.ULTRA_LUXE_CRIME
                "GOMORRAH" to false -> CardBack.GOMORRAH
                "GOMORRAH" to true -> CardBack.GOMORRAH_DARK
                "SIERRA_MADRE" to false -> CardBack.SIERRA_MADRE_DIRTY
                "SIERRA_MADRE" to true -> CardBack.SIERRA_MADRE_CLEAN
                "MADNESS" to false -> CardBack.MADNESS
                "ENCLAVE" to false -> CardBack.ENCLAVE
                "CHINESE" to false -> CardBack.CHINESE
                else -> CardBack.STANDARD_UNCOMMON
            }
            val cardWP = when (rank) {
                "ACE" -> CardNumber(RankNumber.ACE, suitWP, backWP)
                "TWO" -> CardNumber(RankNumber.TWO, suitWP, backWP)
                "THREE" -> CardNumber(RankNumber.THREE, suitWP, backWP)
                "FOUR" -> CardNumber(RankNumber.FOUR, suitWP, backWP)
                "FIVE" -> CardNumber(RankNumber.FIVE, suitWP, backWP)
                "SIX" -> CardNumber(RankNumber.SIX, suitWP, backWP)
                "SEVEN" -> CardNumber(RankNumber.SEVEN, suitWP, backWP)
                "EIGHT" -> CardNumber(RankNumber.EIGHT, suitWP, backWP)
                "NINE" -> CardNumber(RankNumber.NINE, suitWP, backWP)
                "TEN" -> CardNumber(RankNumber.TEN, suitWP, backWP)
                "JACK" -> CardFaceSuited(RankFace.JACK, suitWP, backWP)
                "QUEEN" -> CardFaceSuited(RankFace.QUEEN, suitWP, backWP)
                "KING" -> CardFaceSuited(RankFace.KING, suitWP, backWP)
                "JOKER" -> CardJoker(if (suitWP == Suit.HEARTS) CardJoker.Number.ONE else CardJoker.Number.TWO, backWP)
                else -> CardNumber(RankNumber.ACE, suitWP, backWP)
            }
            save.addCard(cardWP)
        }
        val caps = json.getInt("capsInHand")
        val tickets = json.getInt("tickets")
        save.capsInHand += caps / 10
        save.tickets += tickets / 5
        save
    } catch (_: Exception) {
        null
    }
}