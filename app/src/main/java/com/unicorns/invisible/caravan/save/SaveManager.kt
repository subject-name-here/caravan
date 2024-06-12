package com.unicorns.invisible.caravan.save

import android.util.Log
import com.unicorns.invisible.caravan.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File


val json = Json {
    allowStructuredMapKeys = true
    ignoreUnknownKeys = true
}

fun loadLocalSave(activity: MainActivity): Save? {
    val saveFile = getSaveFile(activity)
    return try {
        saveFile.bufferedReader().use {
            val text = it.readText()
            json.decodeFromString<Save>(text)
        }
    } catch (e: Exception) {
        null
    }
}

fun getSaveFile(activity: MainActivity): File {
    return activity.filesDir.resolve("save")
}

fun saveOnGD(activity: MainActivity) {
    CoroutineScope(Dispatchers.Default).launch {
        if (activity.snapshotsClient == null) return@launch
        val bytes = json.encodeToString(activity.save!!).toByteArray()
        activity.uploadDataToDrive(bytes)
    }
}
fun loadFromGD(activity: MainActivity) {
    runBlocking {
        if (activity.snapshotsClient != null) {
            try {
                val data = activity.fetchDataFromDrive()
                Log.i("Ulysses", data.contentToString())
                activity.save = json.decodeFromString<Save>(data.contentToString())
            } catch (e: Exception) {
                Log.i("GoogleDriveLoad", e.message.toString())
            }
        }
    }
}