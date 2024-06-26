package com.unicorns.invisible.caravan.utils

import android.content.Context
import android.util.Log
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.common.util.concurrent.ListenableFuture
import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.crvnUrl
import com.unicorns.invisible.caravan.isQPinging
import com.unicorns.invisible.caravan.processQPingingResponse
import com.unicorns.invisible.caravan.save.json
import com.unicorns.invisible.caravan.toPythonBool
import com.unicorns.invisible.caravan.userId
import java.util.concurrent.TimeUnit


class QPingingWorker(val appContext: Context, workerParams: WorkerParameters) : ListenableWorker(appContext, workerParams) {
    private val isCustom = workerParams.inputData.getBoolean("isCustom", false)
    override fun startWork(): ListenableFuture<Result> {
        return CallbackToFutureAdapter.getFuture { completer ->
            if (isQPinging.value != true) {
                completer.set(Result.failure())
                return@getFuture Unit
            }
            sendRequest("$crvnUrl/crvn/q_ping?vid=${userId}&is_custom=${isCustom.toPythonBool()}") { result ->
                if (result.getString("body") == "0") {
                    enqueue(appContext, isCustom)
                    completer.set(Result.failure())
                    return@sendRequest
                }
                val response = try {
                    json.decodeFromString<Int>(result.getString("body"))
                } catch (e: Exception) {
                    enqueue(appContext, isCustom)
                    completer.set(Result.failure())
                    return@sendRequest
                }

                if (response in (10..22229)) {
                    processQPingingResponse(response)
                    enqueue(appContext, isCustom)
                    completer.set(Result.success())
                } else {
                    enqueue(appContext, isCustom)
                    completer.set(Result.failure())
                }
            }
        }
    }

    companion object {
        fun enqueue(activity: Context, isCustom: Boolean) {
            val request = OneTimeWorkRequestBuilder<QPingingWorker>()
                .setInputData(Data.Builder().putAll(mapOf("isCustom" to isCustom)).build())
                .setInitialDelay(9500, TimeUnit.MILLISECONDS)
                .build()
            WorkManager.getInstance(activity).enqueue(request)
        }
    }
}