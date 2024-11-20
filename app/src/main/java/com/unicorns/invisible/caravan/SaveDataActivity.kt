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
import com.google.android.gms.games.snapshot.SnapshotMetadataChange
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.IOException

var snapshotsClient: SnapshotsClient? = null
    private set

abstract class SaveDataActivity : AppCompatActivity() {
    suspend fun uploadDataToDrive(data: ByteArray): Boolean {
        if (snapshotsClient == null) return false
        val conflictResolutionPolicy = SnapshotsClient.RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED
        val res = snapshotsClient!!.open(SAVE_FILE_NAME, true, conflictResolutionPolicy)
            .addOnFailureListener { e ->
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
            }
            .continueWith { task ->
                if (task.exception != null) {
                    return@continueWith false
                }
                if (task.result.isConflict) {
                    return@continueWith false
                }
                val snapshot = task.result.data
                snapshot!!.snapshotContents.writeBytes(data)
                val metadataChange = SnapshotMetadataChange.Builder().build()
                val result = snapshotsClient!!.commitAndClose(snapshot, metadataChange)
                result.isSuccessful
            }.await()
        return res
    }

    suspend fun fetchDataFromDrive(): ByteArray? {
        if (snapshotsClient == null) return null
        val conflictResolutionPolicy = SnapshotsClient.RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED
        return snapshotsClient!!.open(SAVE_FILE_NAME, true, conflictResolutionPolicy)
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
                val snapshot = task.result.data
                return@continueWith try {
                    snapshot!!.snapshotContents.readFully()
                } catch (e: IOException) {
                    null
                }
            }.await()
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
            .addOnCompleteListener { isAuthenticatedTask ->
                val isAuthenticated =
                    (isAuthenticatedTask.isSuccessful && isAuthenticatedTask.result.isAuthenticated)
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
                                    "Failed to auth in Google Play Services",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        onSnapshotClientInitialized()
                    }
                }
            }
    }

    companion object {
        const val SAVE_FILE_NAME = "saveFileMkII"
        const val OLD_SAVE_FILE_NAME = "saveFile"
    }
}