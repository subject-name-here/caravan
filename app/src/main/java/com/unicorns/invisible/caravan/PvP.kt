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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
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
import com.unicorns.invisible.caravan.save.save
import com.unicorns.invisible.caravan.utils.CheckboxCustom
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getDividerColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.scrollbar
import com.unicorns.invisible.caravan.utils.sendRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import org.chromium.net.CronetEngine


var cronetEngine: CronetEngine? = null
var currentGameId = ""


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
                updateCode(Card(rank, Suit.HEARTS, cardBack, false))
                updateCode(Card(rank, Suit.CLUBS, cardBack, false))
            } else {
                Suit.entries.forEach { suit ->
                    updateCode(Card(rank, suit, cardBack, false))
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


fun isRoomNumberIncorrect(roomNumber: String): Boolean {
    return roomNumber.toIntOrNull() !in (10..22229)
}


@Composable
fun ShowPvP(
    activity: MainActivity,
    selectedDeck: () -> Pair<CardBack, Boolean>,
    showAlertDialog: (String, String) -> Unit,
    goBack: () -> Unit
) {
    var roomNumber by rememberSaveable { mutableStateOf("") }
    var checkedCustomDeck by rememberSaveable { mutableStateOf(true) }
    var checkedPrivate by rememberSaveable { mutableStateOf(false) }
    var isRoomCreated by rememberSaveable { mutableIntStateOf(0) }
    var isCreator by rememberSaveable { mutableStateOf(false) }

    var enemyDeck by rememberSaveable(stateSaver = Saver(
        save = { json.encodeToString(it) },
        restore = { json.decodeFromString<CustomDeck>(it) }
    )) {
        mutableStateOf(CustomDeck())
    }

    fun showFailure(s: String) {
        showAlertDialog("Failure!", s)
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
            showFailure("Response has wrong size!")
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
                    processCard(index, cnt++, Card(rank, Suit.HEARTS, cardBack, false))
                    processCard(index, cnt++, Card(rank, Suit.CLUBS, cardBack, false))
                } else {
                    Suit.entries.forEach { suit ->
                        processCard(index, cnt++, Card(rank, suit, cardBack, false))
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

    fun checkRoomForJoiner() {
        sendRequest("$crvnUrl/crvn/check_room_for_joiner?room=${isRoomCreated}") { result ->
            if (result.getString("body") == "-1") {
                CoroutineScope(Dispatchers.Unconfined).launch {
                    delay(760L)
                    checkRoomForJoiner()
                }
                return@sendRequest
            }
            val response = try {
                json.decodeFromString<List<ULong>>(result.getString("body"))
            } catch (e: Exception) {
                showFailure(result.getString("body"))
                return@sendRequest
            }
            processResponse(response)
        }
    }

    fun createRoom() {
        if (isRoomCreated != 0) {
            return
        }
        if (isRoomNumberIncorrect(roomNumber)) {
            showIncorrectRoomNumber()
            return
        }
        isRoomCreated = roomNumber.toIntOrNull() ?: return
        val deckPair = selectedDeck()
        val deckCodes = customDeckToInts(
            if (checkedCustomDeck) activity.save!!.getCustomDeckCopy() else CustomDeck(
                deckPair.first, deckPair.second
            )
        )
        sendRequest(
            "$crvnUrl/crvn/create?is_custom=${checkedCustomDeck.toPythonBool()}" +
                    "&room=${isRoomCreated}" +
                    "&is_private=${checkedPrivate.toPythonBool()}" +
                    "&cid=${activity.id}" +
                    "&deck0=${deckCodes[0]}" +
                    "&deck1=${deckCodes[1]}" +
                    "&deck2=${deckCodes[2]}" +
                    "&deck3=${deckCodes[3]}" +
                    "&deck4=${deckCodes[4]}" +
                    "&deck5=${deckCodes[5]}"
        ) { result ->
            val response = result.toString()
            if (response.contains("exists")) {
                showFailure("Room exists already!!!")
                return@sendRequest
            }
            isCreator = true
            checkRoomForJoiner()
        }
    }

    fun joinRoom() {
        if (isRoomCreated != 0) {
            return
        }
        if (isRoomNumberIncorrect(roomNumber)) {
            showIncorrectRoomNumber()
            return
        }
        isRoomCreated = roomNumber.toIntOrNull() ?: return
        val deckCodes = customDeckToInts(activity.save!!.getCustomDeckCopy())
        sendRequest(
            "$crvnUrl/crvn/join?room=$isRoomCreated" +
                    "&jid=${activity.id}" +
                    "&back=${selectedDeck().first.ordinal}" +
                    "&deck0=${deckCodes[0]}" +
                    "&deck1=${deckCodes[1]}" +
                    "&deck2=${deckCodes[2]}" +
                    "&deck3=${deckCodes[3]}" +
                    "&deck4=${deckCodes[4]}" +
                    "&deck5=${deckCodes[5]}"
        ) { result ->
            val response = try {
                json.decodeFromString<List<ULong>>(result.getString("body"))
            } catch (e: Exception) {
                showFailure(result.getString("body"))
                return@sendRequest
            }
            isCreator = false
            processResponse(response)
        }
    }

    fun updateAvailableRoom(isCustom: Boolean) {
        if (isRoomCreated != 0) {
            return
        }
        isRoomCreated = 1
        val link = "$crvnUrl/crvn/get_free_room?is_custom=${isCustom.toPythonBool()}"
        val link2 = "$crvnUrl/crvn/get_free_room?is_custom=${(!isCustom).toPythonBool()}"
        sendRequest(link) { result ->
            val res = result.getString("body").toIntOrNull()
            if (res == null) {
                sendRequest(link2) { result2 ->
                    val res2 = result2.getString("body").toIntOrNull()
                    isRoomCreated = 0
                    if (res2 != null) {
                        roomNumber = res2.toString()
                        joinRoom()
                    } else {
                        showFailure("No room to join! Create your own room!")
                    }
                }
            } else {
                isRoomCreated = 0
                roomNumber = res.toString()
                joinRoom()
            }
        }
    }

    if (enemyDeck.size >= MainActivity.MIN_DECK_SIZE) {
        StartPvP(
            activity = activity,
            playerCResources = if (checkedCustomDeck)
                CResources(activity.save!!.getCustomDeckCopy())
            else
                CResources(selectedDeck().first, selectedDeck().second),
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

    val state = rememberLazyListState()
    LazyColumn(
        state = state,
        modifier = Modifier
            .fillMaxSize()
            .background(getBackgroundColor(activity))
            .scrollbar(state,
                knobColor = getKnobColor(activity), trackColor = getTrackColor(activity),
                horizontal = false),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) { item {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.find),
                modifier = Modifier
                    .clickable {
                        updateAvailableRoom(checkedCustomDeck)
                    }
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 8.dp, vertical = 16.dp).background(getTextBackgroundColor(activity)).padding(8.dp),
                textAlign = TextAlign.Center,
                fontFamily = FontFamily(Font(R.font.monofont)),
                fontWeight = FontWeight.ExtraBold,
                style = TextStyle(color = getTextColor(activity), fontSize = 18.sp)
            )
            Row(modifier = Modifier.height(96.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Text(
                    text = when (isRoomCreated) {
                        0 -> stringResource(R.string.create_room)
                        1 -> stringResource(R.string.pool)
                        2 -> stringResource(R.string.failure)
                        4 -> stringResource(R.string.incorrect_room_number)
                        else -> stringResource(R.string.your_room_is, isRoomCreated)
                    },
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily(Font(R.font.monofont)),
                    style = TextStyle(color = getTextColor(activity), fontSize = 14.sp),
                    modifier = Modifier
                        .fillMaxWidth(0.33f)
                        .padding(horizontal = 8.dp)
                        .clickable { createRoom() }.background(getTextBackgroundColor(activity)).padding(4.dp)
                )
                TextField(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    singleLine = true,
                    enabled = isRoomCreated == 0,
                    value = roomNumber,
                    onValueChange = { roomNumber = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = TextStyle(fontSize = 14.sp, color = getTextColor(activity)),
                    label = { Text(text = stringResource(R.string.room_number),
                        fontFamily = FontFamily(Font(R.font.monofont)),
                        style = TextStyle(color = getTextColor(activity), fontSize = 11.sp)) },
                    colors = TextFieldDefaults.colors().copy(
                        cursorColor = getTextColor(activity),
                        focusedContainerColor = getTextBackgroundColor(activity),
                        unfocusedContainerColor = getTextBackgroundColor(activity),
                        disabledContainerColor = getTextBackgroundColor(activity),
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.join),
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.monofont)),
                    textAlign = TextAlign.Center,
                    style = TextStyle(color = getTextColor(activity), fontSize = 14.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .clickable { joinRoom() }.background(getTextBackgroundColor(activity)).padding(4.dp),
                )
            }
            Column(
                Modifier
                    .padding(horizontal = 8.dp)
                    .wrapContentHeight()) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Text(modifier = Modifier.fillMaxWidth(0.7f),
                        fontFamily = FontFamily(Font(R.font.monofont)),
                        text = stringResource(R.string.pve_use_custom_deck), style = TextStyle(
                        color = getTextColor(activity), fontSize = 14.sp
                    ))
                    CheckboxCustom(
                        activity,
                        { checkedCustomDeck },
                        { checkedCustomDeck = !checkedCustomDeck },
                        { isRoomCreated == 0 }
                    )
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Text(modifier = Modifier.fillMaxWidth(0.7f),
                        fontFamily = FontFamily(Font(R.font.monofont)),
                        text = stringResource(R.string.private_room), style = TextStyle(
                        color = getTextColor(activity), fontSize = 14.sp
                    ))
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
        HorizontalDivider(color = getDividerColor(activity))
        Spacer(modifier = Modifier.height(16.dp))
        Column(Modifier.fillMaxSize(0.75f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                fontFamily = FontFamily(Font(R.font.monofont)),
                text = stringResource(R.string.no_stats_here),
                textAlign = TextAlign.Center,
                style = TextStyle(color = getTextColor(activity), fontSize = 20.sp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.pvp_piece),
                fontFamily = FontFamily(Font(R.font.monofont)),
                style = TextStyle(color = getTextColor(activity), fontSize = 13.sp)
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(R.string.menu_back),
            fontFamily = FontFamily(Font(R.font.monofont)),
            style = TextStyle(color = getTextColor(activity), fontSize = 24.sp),
            modifier = Modifier.clickable {
                if (isRoomCreated == 0) {
                    goBack()
                }
            }.background(getTextBackgroundColor(activity)).padding(8.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
    } }
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
                it.initDeck(playerCResources, maxNumOfFaces = 4, initHand = false)
                currentGameId = it.id
            }
        )
    }
    game.also {
        it.onWin = {
            showAlertDialog(activity.getString(R.string.result), activity.getString(R.string.you_win) +
                    winCard(activity, activity.save!!, CardBack.STANDARD, 1, isAlt = true, isCustom = false))
            save(activity, activity.save!!)
        }
        it.onLose = {
            showAlertDialog(activity.getString(R.string.result), activity.getString(R.string.you_lose))
        }
    }
    activity.goBack = goBack

    var pvpUpdater by rememberSaveable { mutableIntStateOf(0) }

    fun pingForMove(sendHandCard: () -> Unit) {
        val link = "$crvnUrl/crvn/get_move?room=$roomNumber" +
                "&is_creators_move=${isCreator.toPythonBool()}"
        sendRequest(link) { result ->
            val body = result.getString("body")
            if (body.contains("Timeout!")) {
                showAlertDialog("Failed to start the game!", body)
                return@sendRequest
            }

            val move = try {
                decodeMove(body)
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Unconfined).launch {
                    delay(760L)
                    pingForMove(sendHandCard)
                }
                return@sendRequest
            }

            if (move.newCardInHandBack in CardBack.entries.indices) {
                val cardReceived = Card(
                    Rank.entries[move.newCardInHandRank],
                    Suit.entries[move.newCardInHandSuit],
                    CardBack.entries[move.newCardInHandBack],
                    isAlt = move.isNewCardAlt
                )
                game.enemyCResources.addCardToHandPvP(cardReceived)

                if (game.playerCResources.hand.size < 8) {
                    sendHandCard()
                    pvpUpdater = 1 - pvpUpdater
                    return@sendRequest
                }
                game.isExchangingCards = false
                game.isPlayerTurn = isCreator
                pvpUpdater = 2
            } else {
                (game.enemy as EnemyPlayer).latestMoveResponse = move
                game.isExchangingCards = false
                game.isPlayerTurn = isCreator
                pvpUpdater = 2

                CoroutineScope(Dispatchers.Default).launch {
                    game.enemy.makeMove(game)
                    game.processFieldAndHand(game.enemyCResources) {}

                    game.isPlayerTurn = true
                    game.checkOnGameOver()
                    pvpUpdater = 2
                }
            }
        }
    }

    fun sendHandCard() {
        val card = game.playerCResources.addToHandR() ?: return
        val link = "$crvnUrl/crvn/move?room=$roomNumber" +
                "&is_creators_move=${isCreator.toPythonBool()}" +
                "&move_code=0" +
                "&new_card_back_in_hand_code=${card.back.ordinal}" +
                "&new_card_rank_in_hand_code=${card.rank.ordinal}" +
                "&new_card_suit_in_hand_code=${card.suit.ordinal}" +
                "&is_alt=${(activity.save?.altDecksChosen?.get(card.back) == true).toPythonBool()}"
        sendRequest(link) { _ ->
            pingForMove(::sendHandCard)
        }
    }

    if (
        game.enemyCResources.hand.isEmpty() && game.playerCResources.hand.isEmpty() &&
        game.enemyCResources.deckSize != 0 && game.playerCResources.deckSize != 0
    ) {
        if (isCreator) {
            sendHandCard()
        } else {
            pingForMove(::sendHandCard)
        }
    }

    if (game.isCorrupted && game.id == currentGameId) {
        goBack()
        activity.goBack = null
        return
    }

    key(pvpUpdater) {
        ShowGamePvP(activity, game, isCreator, roomNumber, showAlertDialog) lambda@{
            if (game.isOver()) {
                goBack()
                activity.goBack = null
                return@lambda
            }
            showAlertDialog(activity.getString(R.string.check_back_to_menu), "")
        }
    }
}
