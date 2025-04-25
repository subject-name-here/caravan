package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.menu_pvp
import caravan.composeapp.generated.resources.monofont
import caravan.composeapp.generated.resources.room_number
import caravan.composeapp.generated.resources.use_custom_deck
import caravan.composeapp.generated.resources.use_wild_wasteland_cards
import com.sebaslogen.resaca.rememberScoped
import com.unicorns.invisible.caravan.utils.CheckboxCustom
import com.unicorns.invisible.caravan.utils.MenuItemOpen
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getDividerColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.playClickSound
import com.unicorns.invisible.caravan.utils.playCloseSound
import com.unicorns.invisible.caravan.utils.playSelectSound
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.stringResource


fun isRoomNumberIncorrect(roomNumber: String): Boolean {
    return roomNumber.toIntOrNull() !in (10..22229) && roomNumber != "" && roomNumber != "0"
}

val client = HttpClient(CIO) {
    install(WebSockets)
    install(createClientPlugin("fix") {
        on(Send) { request ->
            request.headers.remove("Accept-Charset")
            this.proceed(request)
        }
    })
}

const val crvnUrl = "ws://caravaneer2.onrender.com"


@Composable
fun ShowPvP(
    showAlertDialog: (String, String, (() -> Unit)?) -> Unit,
    goBack: () -> Unit
) {
    var roomNumber by rememberScoped { mutableStateOf("") }
    var isLoading by rememberScoped { mutableStateOf(false) }
    var checkedCustomDeck by rememberScoped { mutableStateOf(true) }
    var checkedWild by rememberScoped { mutableStateOf(false) }
    var context by rememberScoped { mutableStateOf<DefaultClientWebSocketSession?>(null) }


    if (context != null) {
        PvPGame(context!!, { p1, p2 -> showAlertDialog(p1, p2, null) }) {
            context = null
        }
        return
    }

    suspend fun createRoom(): String {
        isLoading = true
        val isQueue = roomNumber == "0" || roomNumber == ""
        var closeReasonOutside = ""
        client.webSocket(
            crvnUrl + if (isQueue) {
                "/game/room/queue"
            } else {
                "/game/room/join/$roomNumber"
            },
        ) {
            try {
                val message = incoming.receive()
                if (message is Frame.Text && message.readText() == "LET THE GAME BEGIN") {
                    context = this
                } else {
                    closeReasonOutside = message.data.decodeToString()
                }
                isLoading = false
            } catch (e: Exception) {
                closeReasonOutside = e.message ?: "UNKNOWN FAILUR"
                isLoading = false
            }
        }

        return closeReasonOutside
    }

    MenuItemOpen(stringResource(Res.string.menu_pvp), "<-", Alignment.TopCenter, {
        if (!isLoading) {
            goBack()
        }
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(getBackgroundColor()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(0.5f),
                singleLine = true,
                enabled = !isLoading,
                value = roomNumber,
                onValueChange = { roomNumber = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = getTextColor(),
                    fontFamily = FontFamily(Font(Res.font.monofont))
                ),
                label = {
                    TextFallout(
                        text = stringResource(Res.string.room_number),
                        getTextColor(),
                        getTextStrokeColor(),
                        11.sp,
                        Modifier,
                    )
                },
                colors = TextFieldDefaults.colors().copy(
                    cursorColor = getTextColor(),
                    focusedContainerColor = getTextBackgroundColor(),
                    unfocusedContainerColor = getTextBackgroundColor(),
                    disabledContainerColor = getBackgroundColor(),
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextFallout(
                text = "JOIN OR CREATE",
                getTextColor(),
                getTextStrokeColor(),
                16.sp,
                Modifier
                    .fillMaxWidth(0.5f)
                    .padding(horizontal = 8.dp)
                    .clickable {
                        if (isRoomNumberIncorrect(roomNumber)) {
                            playCloseSound()
                            return@clickable
                        }
                        playSelectSound()
                        CoroutineScope(Dispatchers.Default).launch {
                            val result = createRoom()
                            if (result != "") {
                                showAlertDialog("ROOM IS OVER", result, null)
                            }
                        }
                    }
                    .background(getTextBackgroundColor())
                    .padding(4.dp),
            )

            Column(
                Modifier
                    .padding(horizontal = 8.dp)
                    .wrapContentHeight()
            ) {
                @Composable
                fun CheckRow(
                    name: String,
                    getChecked: () -> Boolean,
                    toggleChecked: () -> Unit
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextFallout(
                            name,
                            getTextColor(),
                            getTextStrokeColor(),
                            14.sp,
                            Modifier.fillMaxWidth(0.5f),
                            textAlignment = TextAlign.Start
                        )
                        CheckboxCustom(
                            getChecked,
                            {
                                toggleChecked()
                                if (getChecked()) {
                                    playClickSound()
                                } else {
                                    playCloseSound()
                                }
                            },
                            { !isLoading }
                        )
                    }
                }

                CheckRow(
                    stringResource(Res.string.use_custom_deck),
                    { checkedCustomDeck },
                    { checkedCustomDeck = !checkedCustomDeck }
                )
                CheckRow(
                    stringResource(Res.string.use_wild_wasteland_cards),
                    { checkedWild },
                    { checkedWild = !checkedWild }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = getDividerColor())
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextFallout(
                    "TODO",
                    getTextColor(),
                    getTextStrokeColor(),
                    13.sp,
                    Modifier.padding(horizontal = 16.dp),
                    textAlignment = TextAlign.Start
                )
            }
        }
    }
}

@Composable
fun PvPGame(
    session: DefaultClientWebSocketSession,
    showAlertDialog: (String, String) -> Unit,
    goBack: () -> Unit,
) {
    var isCreator by rememberScoped { mutableStateOf<Int?>(null) }
    LaunchedEffect(Unit) {
        showAlertDialog("PRE-RECEIVED", "")
        val message = session.incoming.receive()
        isCreator = when {
            message is Frame.Text && message.readText() == "BEGIN THE END." -> {
                showAlertDialog("RECEIVED", "MESSAGE 1")
                1
            }
            message is Frame.Text && message.readText() == "WAIT." -> {
                showAlertDialog("RECEIVED", "MESSAGE 0")
                0
            }
            else -> {
                showAlertDialog("RECEIVED", "MESSAGE -1")
                -1
            }
        }
    }

    if (isCreator != null) {
        if (isCreator == -1) {
            goBack()
            return
        }

        if (isCreator == 0) {
            LaunchedEffect(Unit) {
                session.outgoing.send(Frame.Text("OVER"))
                goBack()
            }
        }
    }
}