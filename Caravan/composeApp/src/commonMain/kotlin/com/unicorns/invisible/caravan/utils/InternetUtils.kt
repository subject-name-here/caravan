package com.unicorns.invisible.caravan.utils

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText


suspend fun sendRequest(link: String): String {
    val client = HttpClient(CIO)
    val response: HttpResponse = client.get(link)
    return response.bodyAsText()
}

const val crvnUrl = "http://crvnserver.onrender.com"