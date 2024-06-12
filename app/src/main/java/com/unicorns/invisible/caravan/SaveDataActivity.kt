package com.unicorns.invisible.caravan

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.games.*
import com.google.android.gms.games.SnapshotsClient.DataOrConflict
import com.google.android.gms.games.snapshot.Snapshot
import com.google.android.gms.games.snapshot.SnapshotMetadataChange
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


abstract class SaveDataActivity : AppCompatActivity() {
    fun uploadDataToDrive(data: ByteArray): Boolean {
        if (snapshotsClient == null) return false
        val conflictResolutionPolicy = SnapshotsClient.RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED
        snapshotsClient!!.open(SAVE_FILE_NAME, true, conflictResolutionPolicy)
            .continueWith { task ->
                val snapshot = task.result.data
                snapshot!!.snapshotContents.writeBytes(data)
                val metadataChange = SnapshotMetadataChange.Builder().build()
                snapshotsClient!!.commitAndClose(snapshot, metadataChange)
                null
            }
        return true
    }

    suspend fun fetchDataFromDrive(): ByteArray? {
        if (snapshotsClient == null) return null
        val conflictResolutionPolicy = SnapshotsClient.RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED
        return snapshotsClient!!.open(SAVE_FILE_NAME, true, conflictResolutionPolicy)
            .addOnFailureListener { e -> Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show() }
            .continueWith(object : Continuation<DataOrConflict<Snapshot?>?, ByteArray?> {
                override fun then(task: Task<DataOrConflict<Snapshot?>?>): ByteArray? {
                    val snapshot = task.result?.data ?: return null
                    return try {
                        snapshot.snapshotContents.readFully()
                    } catch (e: IOException) {
                        null
                    }
                }
            }).await()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PlayGamesSdk.initialize(this)
        signInLoud()
    }

    var snapshotsClient: SnapshotsClient? = null
        private set
    private fun signInLoud() {
        val gamesSignInClient = PlayGames.getGamesSignInClient(this)
        gamesSignInClient.isAuthenticated()
            .addOnCompleteListener { isAuthenticatedTask: Task<AuthenticationResult> ->
                val isAuthenticated = (isAuthenticatedTask.isSuccessful && isAuthenticatedTask.result.isAuthenticated)
                if (isAuthenticated) {
                    Toast.makeText(this, "KEKEK", Toast.LENGTH_SHORT).show()
                } else {
                    PlayGames.getGamesSignInClient(this).signIn().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            snapshotsClient = PlayGames.getSnapshotsClient(this)
                            Toast.makeText(this, "BOOBEEBOO", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

    }

    companion object {
        const val SAVE_FILE_NAME = "saveFile"
    }
}