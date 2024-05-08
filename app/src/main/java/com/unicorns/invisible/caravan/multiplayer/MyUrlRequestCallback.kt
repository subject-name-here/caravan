package com.unicorns.invisible.caravan.multiplayer


import org.chromium.net.CronetException
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import org.json.JSONException
import org.json.JSONObject
import java.nio.ByteBuffer


//You should create a MyUrlRequestCallback.OnFinishRequest() and
//override onFinishRequest.
//We will send JSON String response to this interface and you can then
//perform actions on the UI or otherwise based on the result.

//All MyUrlRequestCallback functions send response to this.delegate
//which provides it to the interface onFinishRequest which you use in
//your activity or fragment.

abstract class MyUrlRequestCallback (var delegate: OnFinishRequest<JSONObject>) : UrlRequest.Callback() {
    private var redirectionCounter = 5

    override fun onRedirectReceived(
        request: UrlRequest,
        info: UrlResponseInfo,
        newLocationUrl: String
    ) {
        if (redirectionCounter-- <= 0) {
            request.cancel()
        } else {
            request.followRedirect()
        }
    }

    @Throws(Exception::class)
    override fun onResponseStarted(request: UrlRequest, info: UrlResponseInfo) {
        val byteBuffer = ByteBuffer.allocateDirect(102400)
        request.read(byteBuffer)
    }

    @Throws(Exception::class)
    override fun onReadCompleted(
        request: UrlRequest,
        info: UrlResponseInfo,
        byteBuffer: ByteBuffer
    ) {
        request.read(byteBuffer)

        val bytes: ByteArray
        if (byteBuffer.hasArray()) {
            bytes = byteBuffer.array()
        } else {
            bytes = ByteArray(byteBuffer.remaining())
            byteBuffer.get(bytes)
        }

        var responseBodyString = String(bytes)

        //Properly format the response String
        responseBodyString = responseBodyString.trim { it <= ' ' }
            .replace("(\r\n|\n\r|\r|\n|\r0|\n0)".toRegex(), "")
        if (responseBodyString.endsWith("0")) {
            responseBodyString = responseBodyString.substring(0, responseBodyString.length - 1)
        }

        val results = JSONObject()
        try {
            results.put("body", responseBodyString)
            results.put("statusCode", info.httpStatusCode)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        //Send to OnFinishRequest which we will override in activity to read results gotten.
        delegate.onFinishRequest(results)
    }


    override fun onSucceeded(request: UrlRequest, info: UrlResponseInfo) {}

    override fun onFailed(request: UrlRequest, info: UrlResponseInfo, error: CronetException) {
        val results = JSONObject()
        results.put("body", "")
        results.put("statusCode", info.httpStatusCode)
        delegate.onFinishRequest(results)
    }


    interface OnFinishRequest<JSONObject> {
        fun onFinishRequest(result: JSONObject)
    }
}