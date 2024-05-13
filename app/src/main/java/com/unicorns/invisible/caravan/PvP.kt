package com.unicorns.invisible.caravan

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.GameSaver
import com.unicorns.invisible.caravan.model.enemy.EnemyPlayer
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.multiplayer.decodeMove
import com.unicorns.invisible.caravan.save.json
import com.unicorns.invisible.caravan.utils.CheckboxCustom
import com.unicorns.invisible.caravan.utils.sendRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import org.chromium.net.CronetEngine


var cronetEngine: CronetEngine? = null


fun customDeckToInts(customDeck: CustomDeck): List<ULong> {
    val result = ArrayList<ULong>()

    CardBack.entries.forEachIndexed { _, cardBack ->
        var code = 0UL
        var cnt = 0
        fun updateCode(card: Card) {
            if (customDeck.contains(card)) {
                code = code or (1UL shl cnt)
            }
            cnt++
        }
        Rank.entries.forEach { rank ->
            if (rank == Rank.JOKER) {
                updateCode(Card(rank, Suit.HEARTS, cardBack))
                updateCode(Card(rank, Suit.CLUBS, cardBack))
            } else {
                Suit.entries.forEach { suit ->
                    updateCode(Card(rank, suit, cardBack))
                }
            }
        }

        result.add(code)
    }
    return result
}

fun Boolean.toPythonBool(): String {
    return this.toString().replaceFirstChar { it.uppercase() }
}


fun isRoomNumberCorrect(roomNumber: String): Boolean {
    return roomNumber.toIntOrNull() !in (10..22229)
}


@Composable
fun ShowPvP(
    activity: MainActivity,
    selectedDeck: () -> CardBack,
    showAlertDialog: (String, String) -> Unit,
    goBack: () -> Unit
) {
    if (cronetEngine == null) {
        val myBuilder = CronetEngine.Builder(activity)
        cronetEngine = myBuilder.build()
    }

    var roomNumber by rememberSaveable { mutableStateOf("") }
    var checkedCustomDeck by rememberSaveable { mutableStateOf(false) }
    var checkedPrivate by rememberSaveable { mutableStateOf(false) }
    var isRoomCreated by rememberSaveable { mutableIntStateOf(0) }
    var isCreator by rememberSaveable { mutableStateOf(false) }

    var enemyDeck by rememberSaveable(stateSaver = Saver(
        save = { json.encodeToString(it) },
        restore = { json.decodeFromString<CustomDeck>(it) }
    )) {
        mutableStateOf(CustomDeck())
    }

    fun showFailure() {
        isRoomCreated = 2
        CoroutineScope(Dispatchers.Unconfined).launch {
            delay(3800L)
            isRoomCreated = 0
        }
    }
    fun showIncorrectRoomNumber() {
        isRoomCreated = 4
        CoroutineScope(Dispatchers.Unconfined).launch {
            delay(3800L)
            isRoomCreated = 0
        }
    }

    fun processResponse(response: List<ULong>) {
        if (response.size != CardBack.entries.size) {
            showFailure()
            return
        }

        val cardsList = mutableListOf<Card>()
        fun processCard(cardBackIndex: Int, index: Int, card: Card) {
            val n = response[cardBackIndex]
            if ((n shr index) and 1UL == 1UL) {
                cardsList.add(card)
            }
        }

        CardBack.entries.forEachIndexed { index, cardBack ->
            var cnt = 0
            Rank.entries.forEach { rank ->
                if (rank == Rank.JOKER) {
                    processCard(index, cnt++, Card(rank, Suit.HEARTS, cardBack))
                    processCard(index, cnt++, Card(rank, Suit.CLUBS, cardBack))
                } else {
                    Suit.entries.forEach { suit ->
                        processCard(index, cnt++, Card(rank, suit, cardBack))
                    }
                }
            }
        }

        if (!isCreator) {
            checkedCustomDeck = response[0] > (1UL shl 54)
        }

        enemyDeck = CustomDeck().also { customDeck ->
            cardsList.forEach { customDeck.add(it) }
        }
    }

    var isFreeRoomRequested = false
    fun updateAvailableRoom(isCustom: Boolean) {
        if (isFreeRoomRequested) {
            return
        }
        isFreeRoomRequested = true
        val link = "http://subjectn4mehere.pythonanywhere.com/crvn/get_free_room?is_custom=${isCustom.toPythonBool()}"
        sendRequest(link) { result ->
            val res = result.getString("body").toIntOrNull()
            roomNumber = res?.toString() ?: ""
            isFreeRoomRequested = false
        }
    }

    if (enemyDeck.size >= MainActivity.MIN_DECK_SIZE) {
        StartPvP(
            activity = activity,
            playerCResources = if (checkedCustomDeck) CResources(activity.save!!.getCustomDeckCopy()) else CResources(selectedDeck()),
            enemyStartDeck = run {
                val deck = CustomDeck()
                repeat(enemyDeck.size) {
                    deck.add(enemyDeck[it])
                }
                deck
            },
            roomNumber = isRoomCreated,
            isCreator = isCreator,
            showAlertDialog = showAlertDialog
        ) {
            enemyDeck = CustomDeck()
            isRoomCreated = 0
        }
        return
    }

    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.height(100.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Text(
                    text = when (isRoomCreated) {
                        0 -> "Create Room"
                        2 -> "Failure!"
                        4 -> "Incorrect room number!"
                        else -> "Your Room is $isRoomCreated. Awaiting the Opponent....."
                    },
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    style = TextStyle(color = Color(activity.getColor(R.color.colorPrimary)), fontSize = 16.sp),
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .clickable {
                            if (isRoomCreated != 0 || isRoomNumberCorrect(roomNumber)) {
                                showIncorrectRoomNumber()
                                return@clickable
                            }
                            isRoomCreated = roomNumber.toIntOrNull() ?: return@clickable
                            val deckCodes = customDeckToInts(
                                if (checkedCustomDeck) activity.save!!.getCustomDeckCopy() else CustomDeck(
                                    selectedDeck()
                                )
                            )
                            sendRequest(
                                "http://subjectn4mehere.pythonanywhere.com/crvn/create?is_custom=${checkedCustomDeck.toPythonBool()}" +
                                    "&room=${isRoomCreated}" +
                                    "&is_private=${checkedPrivate.toPythonBool()}" +
                                    "&deck0=${deckCodes[0]}" +
                                    "&deck1=${deckCodes[1]}" +
                                    "&deck2=${deckCodes[2]}" +
                                    "&deck3=${deckCodes[3]}" +
                                    "&deck4=${deckCodes[4]}" +
                                    "&deck5=${deckCodes[5]}"
                            ) { result ->
                                val response = try {
                                    json.decodeFromString<List<ULong>>(
                                        result.getString("body")
                                    )
                                } catch (e: Exception) {
                                    showFailure()
                                    return@sendRequest
                                }
                                isCreator = true
                                processResponse(response)
                            }
                        }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(modifier = Modifier.fillMaxHeight(0.5f).fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(modifier = Modifier.fillMaxWidth(0.7f), text = stringResource(R.string.pve_use_custom_deck))
                        CheckboxCustom(
                            activity,
                            { checkedCustomDeck },
                            {
                                checkedCustomDeck = !checkedCustomDeck
                                updateAvailableRoom(checkedCustomDeck)
                            },
                            { isRoomCreated == 0 }
                        )
                    }
                    Row(modifier = Modifier.fillMaxSize().padding(8.dp),
                        horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(modifier = Modifier.fillMaxWidth(0.7f), text = "Private room")
                        CheckboxCustom(
                            activity,
                            { checkedPrivate },
                            { checkedPrivate = !checkedPrivate },
                            { isRoomCreated == 0 }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.height(100.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                TextField(
                    singleLine = true,
                    enabled = isRoomCreated == 0,
                    value = roomNumber,
                    onValueChange = { roomNumber = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text(text = "Join Room:", style = TextStyle(color = Color(activity.getColor(R.color.colorPrimary)), fontSize = 16.sp)) },
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Join!!!",
                    modifier = Modifier.clickable {
                        if (isRoomCreated != 0 || isRoomNumberCorrect(roomNumber)) {
                            showIncorrectRoomNumber()
                            return@clickable
                        }
                        isRoomCreated = roomNumber.toIntOrNull() ?: return@clickable
                        val deckCodes = customDeckToInts(activity.save!!.getCustomDeckCopy())
                        sendRequest(
                            "http://subjectn4mehere.pythonanywhere.com/crvn/join?room=$isRoomCreated" +
                                    "&back=${selectedDeck().ordinal}" +
                                    "&deck0=${deckCodes[0]}" +
                                    "&deck1=${deckCodes[1]}" +
                                    "&deck2=${deckCodes[2]}" +
                                    "&deck3=${deckCodes[3]}" +
                                    "&deck4=${deckCodes[4]}" +
                                    "&deck5=${deckCodes[5]}"
                        ) { result ->
                            val response = try {
                                json.decodeFromString<List<ULong>>(
                                    result.getString("body")
                                )
                            } catch (e: Exception) {
                                showFailure()
                                return@sendRequest
                            }
                            isCreator = false
                            processResponse(response)
                        }
                    },
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(color = Color(activity.getColor(R.color.colorPrimary)), fontSize = 16.sp)
                )
            }
        }
        Spacer(modifier = Modifier.height(28.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))
        Column(Modifier.fillMaxSize(0.75f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "No stats here: the game's for fun, not for numbers!",
                textAlign = TextAlign.Center,
                style = TextStyle(color = Color(activity.getColor(R.color.colorPrimaryDark)), fontSize = 20.sp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Room number is from 10 to 22229.\n" +
                        "Time limit for awaiting opponent is 38 seconds, then room is destroyed.\n" +
                        "\n" +
                        "Pls no cheats thx\n" +
                        "\n" +
                        "Good luck!",
                style = TextStyle(color = Color(activity.getColor(R.color.colorPrimary)), fontSize = 12.sp)
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(R.string.menu_back),
            style = TextStyle(color = Color(activity.getColor(R.color.colorPrimaryDark)), fontSize = 24.sp),
            modifier = Modifier.clickable {
                if (isRoomCreated != 0) {
                    return@clickable
                }
                goBack()
            }
        )
    }
}

@Composable
fun StartPvP(
    activity: MainActivity,
    playerCResources: CResources,
    enemyStartDeck: CustomDeck,
    isCreator: Boolean,
    roomNumber: Int,
    showAlertDialog: (String, String) -> Unit,
    goBack: () -> Unit,
) {
    val game by rememberSaveable(stateSaver = GameSaver) {
        mutableStateOf(
            Game(
                playerCResources,
                EnemyPlayer(enemyStartDeck)
            ).also {
                it.isPlayerTurn = false
                it.isExchangingCards = true
                it.playerCResources.shuffleDeck()
                var tmpHand = it.playerCResources.getTopHand()
                while (tmpHand.count { card -> card.isFace() } > 4) {
                    it.playerCResources.shuffleDeck()
                    tmpHand = it.playerCResources.getTopHand()
                }
            }
        )
    }
    game.also {
        it.onWin = {
            showAlertDialog(activity.getString(R.string.result), activity.getString(R.string.you_win))
        }
        it.onLose = {
            showAlertDialog(activity.getString(R.string.result), activity.getString(R.string.you_lose))
        }
    }

    var pvpUpdater by rememberSaveable { mutableStateOf(false) }

    fun sendHandCard() {
        val card = game.playerCResources.addToHandR() ?: return
        val link = "http://subjectn4mehere.pythonanywhere.com/crvn/move?room=$roomNumber" +
                "&is_creators_move=${isCreator.toPythonBool()}" +
                "&is_util=False" +
                "&move_code=0" +
                "&new_card_back_in_hand_code=${card.back.ordinal}" +
                "&new_card_rank_in_hand_code=${card.rank.ordinal}" +
                "&new_card_suit_in_hand_code=${card.suit.ordinal}"
        sendRequest(link) { result ->
            val move = try {
                decodeMove(result.getString("body"))
            } catch (e: Exception) {
                goBack()
                return@sendRequest
            }

            if (game.enemyCResources.hand.size < 8) {
                val cardReceived = Card(
                    Rank.entries[move.newCardInHandRank],
                    Suit.entries[move.newCardInHandSuit],
                    CardBack.entries[move.newCardInHandBack],
                )
                game.enemyCResources.addCardToHandPvP(cardReceived)
            }
            if (game.playerCResources.hand.size < 8) {
                pvpUpdater = !pvpUpdater
                sendHandCard()
                return@sendRequest
            }
            game.isExchangingCards = false
            game.isPlayerTurn = isCreator
            if (!game.isPlayerTurn) {
                (game.enemy as EnemyPlayer).latestMoveResponse = move
                CoroutineScope(Dispatchers.Default).launch {
                    game.enemy.makeMove(game)
                    game.processFieldAndHand(game.enemyCResources) {}

                    game.isPlayerTurn = true
                    game.checkOnGameOver()
                }
            }
            pvpUpdater = !pvpUpdater
        }
    }

    if (
        game.enemyCResources.hand.isEmpty() && game.playerCResources.hand.isEmpty() &&
        game.enemyCResources.deckSize != 0 && game.playerCResources.deckSize != 0
    ) {
        if (!isCreator) {
            sendRequest("http://subjectn4mehere.pythonanywhere.com/crvn/move?room=$roomNumber" +
                    "&is_creators_move=False&is_util=True") { result ->
                val move = try {
                    decodeMove(result.getString("body"))
                } catch (e: Exception) {
                    goBack()
                    return@sendRequest
                }
                val card = Card(
                    Rank.entries[move.newCardInHandRank],
                    Suit.entries[move.newCardInHandSuit],
                    CardBack.entries[move.newCardInHandBack],
                )
                game.enemyCResources.addCardToHandPvP(card)
                pvpUpdater = !pvpUpdater
                sendHandCard()
            }
        } else {
            sendHandCard()
        }
    }

    if (game.isCorrupted) {
        goBack()
    }

    key(pvpUpdater) {
        ShowGamePvP(activity, game, isCreator, roomNumber, showAlertDialog) { goBack() }
    }
}
