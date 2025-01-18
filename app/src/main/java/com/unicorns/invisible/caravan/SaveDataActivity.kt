package com.unicorns.invisible.caravan

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.games.AchievementsClient
import com.google.android.gms.games.PlayGames
import com.google.android.gms.games.PlayGamesSdk
import com.google.android.gms.games.SnapshotsClient
import com.google.android.gms.games.snapshot.Snapshot
import com.google.android.gms.games.snapshot.SnapshotMetadata.PLAYED_TIME_UNKNOWN
import com.google.android.gms.games.snapshot.SnapshotMetadataChange
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date


private var snapshotsClient: SnapshotsClient? = null

abstract class SaveDataActivity : AppCompatActivity() {
    suspend fun uploadDataToDrive(data: ByteArray): Boolean {
        val snapshot = getSnapshot(SAVE_FILE_NAME)
        return try {
            snapshot!!.snapshotContents.writeBytes(data)
            val builder = SnapshotMetadataChange.Builder()
            val time = getPlayedTime()
            if (time != null) {
                builder.setPlayedTimeMillis(time)
                save.lastSaveTime = Date().time
            }
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

    protected abstract fun onSnapshotClientInitialized()
    private fun signInLoud() {
        val gamesSignInClient = PlayGames.getGamesSignInClient(this)
        gamesSignInClient.isAuthenticated()
            .addOnCompleteListener { task ->
                val isAuthenticated = task.isSuccessful && task.result.isAuthenticated
                if (isAuthenticated) {
                    snapshotsClient = PlayGames.getSnapshotsClient(this)
                    onSnapshotClientInitialized()
                } else {
                    gamesSignInClient.signIn().addOnCompleteListener { task ->
                        if (task.isSuccessful && task.result.isAuthenticated) {
                            snapshotsClient = PlayGames.getSnapshotsClient(this)
                        } else {
                            MainScope().launch {
                                Toast.makeText(
                                    this@SaveDataActivity,
                                    "Failed to auth in Google Play Services.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        onSnapshotClientInitialized()
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
    suspend fun fetchOldSaveFromDrive(): ByteArray? {
        return fetchFileFromDrive(OLD_SAVE_FILE_NAME)
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
            .addOnFailureListener { e ->
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
            }
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

    var lastPlayedTimeCache = 0L
    suspend fun getPlayedTime(): Long? {
        val snapshot = getSnapshot(SAVE_FILE_NAME)
        return try {
            val lastSaveTime = save.lastSaveTime
            val lastPlayedTime = snapshot!!.metadata.playedTime
            if (lastPlayedTime == PLAYED_TIME_UNKNOWN) {
                0L
            } else {
                lastPlayedTime + (Date().time - lastSaveTime)
            }.also { lastPlayedTimeCache = it }
        } catch (_: Exception) {
            null
        }
    }

    companion object {
        const val SAVE_FILE_NAME = "saveFileMkII"
        const val OLD_SAVE_FILE_NAME = "saveFile"
    }
}