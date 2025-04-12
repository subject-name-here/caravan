package com.unicorns.invisible.caravan

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.games.AchievementsClient
import com.google.android.gms.games.PlayGames
import com.google.android.gms.games.PlayGamesSdk
import com.google.android.gms.games.SnapshotsClient
import com.google.android.gms.games.snapshot.Snapshot
import com.google.android.gms.games.snapshot.SnapshotMetadataChange
import com.google.android.gms.tasks.Task
import com.unicorns.invisible.caravan.save.Save
import com.unicorns.invisible.caravan.save.loadGDSave
import com.unicorns.invisible.caravan.save.loadLocalSave
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.startRadio
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


private var snapshotsClient: SnapshotsClient? = null

abstract class SaveDataActivity : ComponentActivity() {
    suspend fun uploadDataToDrive(data: ByteArray): Boolean {
        val snapshot = getSnapshot(SAVE_FILE_NAME)
        return try {
            snapshot!!.snapshotContents.writeBytes(data)
            val builder = SnapshotMetadataChange.Builder()
            val metadataChange = builder.build()
            val result = snapshotsClient!!.commitAndClose(snapshot, metadataChange)
            result.isSuccessful
        } catch (_: Exception) {
            false
        }
    }

    var achievementsClient: AchievementsClient? = null
    private val contracts = ActivityResultContracts.StartActivityForResult()
    private val startForResult = registerForActivityResult(contracts) {}
    fun openAchievements(task: Task<Intent>) {
        task.addOnSuccessListener {
            startForResult.launch(it)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        achievementsClient = PlayGames.getAchievementsClient(this)

        if (snapshotsClient == null) {
            PlayGamesSdk.initialize(this)
            signInLoud()
        }
    }

    private fun onSnapshotClientInitialized(isInited: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            if (save.playerId == null) {
                val playerId = if (isInited) getPlayerId() else ""

                val localSave = loadLocalSave()
                if (localSave != null && (localSave.playerId == playerId || playerId == "")) {
                    save = localSave
                } else {
                    val loadedSave = loadGDSave()
                    if (loadedSave != null) {
                        save = loadedSave
                    } else {
                        save = Save(playerId)
                        saveData()
                    }
                }

                if (save.playerId == "" && playerId != "") {
                    save.playerId = playerId
                    saveData()
                }

                isSaveLoaded = true
                startRadio()
            }
        }
    }

    private fun signInLoud() {
        val gamesSignInClient = PlayGames.getGamesSignInClient(this)
        gamesSignInClient.isAuthenticated()
            .addOnCompleteListener { task ->
                val isAuthenticated = task.isSuccessful && task.result.isAuthenticated
                if (isAuthenticated) {
                    snapshotsClient = PlayGames.getSnapshotsClient(this)
                    onSnapshotClientInitialized(true)
                } else {
                    gamesSignInClient.signIn().addOnCompleteListener { task ->
                        if (task.isSuccessful && task.result.isAuthenticated) {
                            snapshotsClient = PlayGames.getSnapshotsClient(this)
                            onSnapshotClientInitialized(true)
                        } else {
                            MainScope().launch {
                                Toast.makeText(
                                    this@SaveDataActivity,
                                    "Failed to auth in Google Play Services.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            onSnapshotClientInitialized(false)
                        }
                    }
                }
            }
    }

    suspend fun getPlayerId(): String {
        return try {
            PlayGames.getPlayersClient(this).currentPlayerId.await()
        } catch (_: Exception) {
            ""
        }
    }

    suspend fun fetchDataFromDrive(): ByteArray? {
        return fetchFileFromDrive(SAVE_FILE_NAME)
    }
    private suspend fun fetchFileFromDrive(fileName: String): ByteArray? {
        val snapshot = getSnapshot(fileName)
        return try {
            snapshot!!.snapshotContents.readFully()
        } catch (_: Exception) {
            null
        }
    }

    private suspend fun getSnapshot(fileName: String): Snapshot? {
        if (snapshotsClient == null) return null
        val conflictResolutionPolicy = SnapshotsClient.RESOLUTION_POLICY_LONGEST_PLAYTIME
        return snapshotsClient!!.open(fileName, true, conflictResolutionPolicy)
            .continueWith { task ->
                if (task.exception != null) {
                    return@continueWith null
                }
                if (task.result.isConflict) {
                    return@continueWith null
                }
                task.result.data
            }.await()
    }

    suspend fun fetchOldSave() = fetchFileFromDrive(OLD_SAVE_FILE_NAME)

    companion object {
        const val SAVE_FILE_NAME = "saveFileMk3"
        const val OLD_SAVE_FILE_NAME = "saveFileMkII"
    }
}