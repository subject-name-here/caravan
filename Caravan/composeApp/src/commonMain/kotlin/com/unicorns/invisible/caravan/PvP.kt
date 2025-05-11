package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.check_back_to_menu
import caravan.composeapp.generated.resources.check_back_to_menu_body
import caravan.composeapp.generated.resources.menu_pvp
import caravan.composeapp.generated.resources.monofont
import caravan.composeapp.generated.resources.result
import caravan.composeapp.generated.resources.room_number
import caravan.composeapp.generated.resources.use_custom_deck
import caravan.composeapp.generated.resources.use_wild_wasteland_cards
import caravan.composeapp.generated.resources.you_lose
import caravan.composeapp.generated.resources.you_win
import com.sebaslogen.resaca.rememberScoped
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.EnemyPlayer
import com.unicorns.invisible.caravan.model.enemy.Move
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CardAtomic
import com.unicorns.invisible.caravan.model.primitives.CardWildWasteland
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.WWType
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.CheckboxCustom
import com.unicorns.invisible.caravan.utils.MenuItemOpen
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableCancel
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getDialogBackground
import com.unicorns.invisible.caravan.utils.getDialogTextColor
import com.unicorns.invisible.caravan.utils.getDividerColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.playClickSound
import com.unicorns.invisible.caravan.utils.playCloseSound
import com.unicorns.invisible.caravan.utils.playLoseSound
import com.unicorns.invisible.caravan.utils.playSelectSound
import com.unicorns.invisible.caravan.utils.playWinSoundAlone
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource


fun isRoomNumberIncorrect(roomNumber: String): Boolean {
    return roomNumber.toIntOrNull() !in (10..22229)
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
        val deck by rememberScoped { mutableStateOf(
            if (checkedCustomDeck) {
                CustomDeck().apply { addAll(saveGlobal.getCurrentDeckCopy()) }
            } else {
                CustomDeck(saveGlobal.selectedDeck)
            }.apply {
                if (checkedWild) {
                    add(CardAtomic())
                    add(CardAtomic())
                    WWType.entries.forEach { type ->
                        add(CardWildWasteland(type))
                    }
                }
            }
        ) }
        PvPGame(
            context!!,
            deck,
            { p1, p2 -> showAlertDialog(p1, p2, null) },
            showAlertDialog
        ) {
            CoroutineScope(Dispatchers.Default).launch {
                context = null
            }
        }
        return
    }

    suspend fun createRoom(): String {
        isLoading = true
        var closeReasonOutside = ""
        try {
            client.webSocket(
                "$crvnUrl/game/room/join/$roomNumber/$checkedWild/$checkedCustomDeck",
            ) {
                try {
                    val message = incoming.receive()
                    if (message is Frame.Text && message.readText() == "LET THE GAME BEGIN") {
                        context = this
                        isLoading = false
                        while (context != null) {
                            delay(1000L)
                        }
                    } else {
                        closeReasonOutside = message.data.decodeToString()
                        isLoading = false
                    }
                } catch (e: Exception) {
                    closeReasonOutside = e.message ?: "UNKNOWN FAILUR"
                    isLoading = false
                }
            }
        } catch (e: Exception) {
            closeReasonOutside = e.message ?: ""
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
                        12.sp,
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
                            showAlertDialog("WRONG ROOM NUMBER", "room number is in [10..22229].", null)
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
                    "TODO: Rules",
                    getTextColor(),
                    getTextStrokeColor(),
                    13.sp,
                    Modifier.padding(horizontal = 16.dp),
                    textAlignment = TextAlign.Start
                )
            }
        }
    }

    if (isLoading) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = @Composable {},
            modifier = Modifier.border(width = 4.dp, color = getTextColor()),
            dismissButton = null,
            title = @Composable {
                TextFallout(
                    "REQUEST WAS SENT!",
                    getDialogTextColor(),
                    getDialogTextColor(),
                    24.sp, Modifier,
                    textAlignment = TextAlign.Start
                )
            },
            text = @Composable {
                TextFallout(
                    "PLEASE, STAND BY...",
                    getDialogTextColor(),
                    getDialogTextColor(),
                    16.sp, Modifier,
                    textAlignment = TextAlign.Start
                )
            },
            shape = RectangleShape,
            backgroundColor = getDialogBackground(),
            contentColor = getDialogTextColor(),
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = true
            )
        )
    }
}

@Composable
fun PvPGame(
    session: DefaultClientWebSocketSession,
    deck: CustomDeck,
    showAlertDialog: (String, String) -> Unit,
    showAlertDialogWithCallback: (String, String, () -> Unit) -> Unit,
    goBack: () -> Unit,
) {
    var isCreator by rememberScoped { mutableStateOf<Int?>(null) }
    var enemyDeckSize by rememberScoped { mutableStateOf<Int?>(null) }
    var deckReceived by rememberScoped { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        val message = session.incoming.receive()
        isCreator = when {
            message is Frame.Text && message.readText() == "BEGIN THE END." -> {
                showAlertDialog("RECEIVED", "YOU BEGIN THE GAME.")
                1
            }
            message is Frame.Text && message.readText() == "WAIT." -> {
                showAlertDialog("RECEIVED", "YOU GO SECOND.")
                0
            }
            else -> {
                showAlertDialog("RECEIVED", "MESSAGE -1")
                -1
            }
        }

        suspend fun sendDeckSize() {
            session.outgoing.send(Frame.Text(deck.size.toString()))
        }
        suspend fun receiveDeckSize() {
            val deckSize = session.incoming.receive()
            when {
                deckSize is Frame.Text -> {
                    enemyDeckSize = deckSize.readText().toIntOrNull()
                }
                else -> {
                    showAlertDialog("RECEIVED BAD", "DECK SIZE is bad")
                    goBack()
                }
            }
        }
        if (isCreator == 1) {
            sendDeckSize()
            receiveDeckSize()
        } else {
            receiveDeckSize()
            sendDeckSize()
        }
        deckReceived = true
    }

    val scope = rememberCoroutineScope()
    if (deckReceived) {
        val game by rememberScoped { mutableStateOf(Game(
            CResources(deck).also { it.initResourcesPvP() },
            EnemyPlayer(enemyDeckSize, session),
            isDeckOperatedFromOutside = true
        ).also {
            saveGlobal.pvpGames++
            it.onWin = {
                playWinSoundAlone()
                processChallengesGameOver(it)

                saveGlobal.gamesFinished++
                saveGlobal.wins++
                saveGlobal.pvpWins++

                val back = CardBack.STANDARD_RARE
                scope.launch {
                    val rewardCard = winCard(back, false)
                    showAlertDialog(
                        getString(Res.string.result),
                        getString(Res.string.you_win) + rewardCard,
                    )
                }
                saveData()
            }
            it.onLose = {
                playLoseSound()
                saveGlobal.gamesFinished++
                saveData()

                scope.launch {
                    showAlertDialog(
                        getString(Res.string.result),
                        getString(Res.string.you_lose)
                    )
                }
            }
        }) }

        LaunchedEffect(Unit) {
            suspend fun sendCard() {
                val card = deck.removeFirst()
                game.playerCResources.addCardToHandDirect(card)
                session.outgoing.send(Frame.Text(Json.encodeToString(card)))
            }
            suspend fun receiveCard() {
                val cardRaw = session.incoming.receive() as Frame.Text
                val card = Json.decodeFromString<Card>(cardRaw.readText())
                game.enemyCResources.addCardToHandDirect(card)
            }

            when (isCreator) {
                1 -> {
                    repeat(8) {
                        sendCard()
                        receiveCard()
                    }
                }
                0 -> {
                    repeat(8) {
                        receiveCard()
                        sendCard()
                    }
                    game.enemy.makeMove(game, AnimationSpeed.NONE)
                }
                else -> {
                    goBack()
                    return@LaunchedEffect
                }
            }
            game.enemyCResources.recomposeResources++
            game.canPlayerMove = true
        }

        ShowGame(
            game,
            isBlitz = false,
            isPvP = true,
            onMove = { a1, a2, a3, a4 ->
                val newCard = game.playerCResources.addToHandPvP()
                CoroutineScope(Dispatchers.Default).launch {
                    session.outgoing.send(
                        Frame.Text(
                            Json.encodeToString(Move(
                                a1, a2, a3, a4, Json.encodeToString(newCard)
                            ))
                        )
                    )
                }
            }
        ) {
            if (game.isOver()) {
                goBack()
            } else {
                scope.launch {
                    showAlertDialogWithCallback(
                        getString(Res.string.check_back_to_menu),
                        getString(Res.string.check_back_to_menu_body),
                        goBack
                    )
                }
            }
        }
    } else {
        BoxWithConstraints(Modifier.fillMaxSize().background(getBackgroundColor()), contentAlignment = Alignment.Center) {
            TextFallout(
                text = "LOADING (press to exit)",
                getTextColor(),
                getTextStrokeColor(),
                16.sp,
                Modifier
                    .fillMaxWidth(0.5f)
                    .padding(horizontal = 8.dp)
                    .clickableCancel {
                        goBack()
                    }
                    .background(getTextBackgroundColor())
                    .padding(4.dp),
            )
        }
    }
}