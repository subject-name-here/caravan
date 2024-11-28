package com.unicorns.invisible.caravan.utils

import com.unicorns.invisible.caravan.multiplayer.MyUrlRequestCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.chromium.net.CronetEngine
import org.json.JSONObject
import java.util.concurrent.Executors


fun sendRequest(link: String, onFinish: (JSONObject) -> Unit) {
    val cronetEngine = cronetEngine ?: return

    CoroutineScope(Dispatchers.IO).launch {
        val requestBuilder = cronetEngine.newUrlRequestBuilder(
            link,
            object : MyUrlRequestCallback(object : OnFinishRequest<JSONObject> {
                override fun onFinishRequest(result: JSONObject) {
                    onFinish(result)
                }
            }) {},
            Executors.newSingleThreadExecutor()
        )

        val request = requestBuilder.build()
        request.start()
    }
}

const val crvnUrl = "http://crvnserver.onrender.com"
var cronetEngine: CronetEngine? = null