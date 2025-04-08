package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import caravan.composeapp.generated.resources.create_room
import caravan.composeapp.generated.resources.failed_to_start_the_game
import caravan.composeapp.generated.resources.failure
import caravan.composeapp.generated.resources.incorrect_room_number
import caravan.composeapp.generated.resources.join
import caravan.composeapp.generated.resources.menu_pvp
import caravan.composeapp.generated.resources.monofont
import caravan.composeapp.generated.resources.private_room
import caravan.composeapp.generated.resources.pvp_piece
import caravan.composeapp.generated.resources.result
import caravan.composeapp.generated.resources.room_number
import caravan.composeapp.generated.resources.use_custom_deck
import caravan.composeapp.generated.resources.use_wild_wasteland_cards
import caravan.composeapp.generated.resources.you_lose
import caravan.composeapp.generated.resources.you_win
import caravan.composeapp.generated.resources.your_room_is
import com.sebaslogen.resaca.rememberScoped
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.EnemyPlayer
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.CheckboxCustom
import com.unicorns.invisible.caravan.utils.MenuItemOpen
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableOk
import com.unicorns.invisible.caravan.utils.crvnUrl
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getDividerColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.playClickSound
import com.unicorns.invisible.caravan.utils.playCloseSound
import com.unicorns.invisible.caravan.utils.playLoseSound
import com.unicorns.invisible.caravan.utils.playWinSound
import com.unicorns.invisible.caravan.utils.sendRequest
import com.unicorns.invisible.caravan.utils.stopAmbient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


// TODO: update mp (number of decks!)
//fun customDeckToInts(customDeck: CustomDeck): List<ULong> {
//    val result = ArrayList<ULong>()
//
//    CardBack.entries.forEachIndexed { _, cardBack ->
//        var code = 0UL
//        fun updateCode(card: Card) {
//            val cnt = card.rank.ordinal * Suit.entries.size + card.suit.ordinal
//            code = code or (1UL shl cnt)
//        }
//        var isAlt = false
//        customDeck.toList().forEach {
//            if (it.back == cardBack) {
//                updateCode(it)
//                isAlt = isAlt || it.isAlt
//            }
//        }
//
//        if (isAlt) {
//            code = code or (1UL shl 60)
//        }
//        result.add(code)
//    }
//    return result
//}

fun Boolean.toPythonBool(): String {
    return this.toString().replaceFirstChar { it.uppercase() }
}

fun isRoomNumberIncorrect(roomNumber: String): Boolean {
    return roomNumber.toIntOrNull() !in (10..22229)
}


@OptIn(ExperimentalUuidApi::class)
val id = Uuid.random().toHexString()

@Composable
fun ShowPvP(
    showAlertDialog: (String, String, (() -> Unit)?) -> Unit,
    goBack: () -> Unit
) {
    goBack()
    return


    var roomNumber by rememberSaveable { mutableStateOf("") }
    var checkedCustomDeck by rememberSaveable { mutableStateOf(true) }
    var checkedWild by rememberSaveable { mutableStateOf(false) }
    var checkedPrivateRoom by rememberSaveable { mutableStateOf(true) }
    var isRoomCreated by rememberSaveable { mutableIntStateOf(0) }
    var isCreator by rememberSaveable { mutableStateOf(false) }

    var enemyDeck by rememberScoped { mutableStateOf(CustomDeck()) }

    suspend fun showFailure(s: String) {
        showAlertDialog(getString(Res.string.failure), s, null)
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

    fun processResponse(response: String) {
        val cardsList = mutableListOf<Card>()
        enemyDeck = CustomDeck().also { customDeck ->
            cardsList.forEach { customDeck.add(it) }
        }
    }

    suspend fun checkRoomForJoiner() {
        val result = sendRequest("${crvnUrl}/crvn/check_room_for_joiner?room=${isRoomCreated}")
        if (result == "-1") {
            CoroutineScope(Dispatchers.Unconfined).launch {
                delay(1900L)
                checkRoomForJoiner()
            }
            return
        }
        processResponse(result)
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
        val deckPair = save.selectedDeck
//        val deckCodes = customDeckToInts(
//            if (checkedCustomDeck)
//                save.getCustomDeckCopy()
//            else
//                CustomDeck(deckPair.first, deckPair.second)
//        )
//        sendRequest(
//            "${crvnUrl}/crvn/create?is_custom=${checkedCustomDeck.toPythonBool()}" +
//                    "&room=${isRoomCreated}" +
//                    "&is_private=${checkedPrivateRoom.toPythonBool()}" +
//                    "&is_new=True" +
//                    "&is_wild=${checkedWild.toPythonBool()}" +
//                    "&cid=$id" +
//                    "&deck0=${deckCodes[0]}" +
//                    "&deck1=${deckCodes[1]}" +
//                    "&deck2=${deckCodes[2]}" +
//                    "&deck3=${deckCodes[3]}" +
//                    "&deck4=${deckCodes[4]}" +
//                    "&deck5=${deckCodes[5]}" +
//                    "&deck6=${deckCodes[6]}" +
//                    "&deck7=${deckCodes[7]}" +
//                    "&deck8=${deckCodes[8]}" +
//                    "&deck9=${deckCodes[9]}" +
//                    "&deck10=${deckCodes[10]}"
//        ) { result ->
//            val response = result.toString()
//            if (response.contains("exists")) {
//                showFailure(getString(Res.string.room_exists_already))
//                return@sendRequest
//            }
//            isCreator = true
//            checkRoomForJoiner()
//        }
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
//        val deckCodes = customDeckToInts(save.getCustomDeckCopy())
//        sendRequest(
//            "${crvnUrl}/crvn/join?room=$isRoomCreated" +
//                    "&jid=$id" +
//                    "&back=${save.selectedDeck.first.ordinal}" +
//                    "&is_alt=${save.selectedDeck.second.toPythonBool()}" +
//                    "&deck0=${deckCodes[0]}" +
//                    "&deck1=${deckCodes[1]}" +
//                    "&deck2=${deckCodes[2]}" +
//                    "&deck3=${deckCodes[3]}" +
//                    "&deck4=${deckCodes[4]}" +
//                    "&deck5=${deckCodes[5]}" +
//                    "&deck6=${deckCodes[6]}" +
//                    "&deck7=${deckCodes[7]}" +
//                    "&deck8=${deckCodes[8]}" +
//                    "&deck9=${deckCodes[9]}" +
//                    "&deck10=${deckCodes[10]}"
//        ) { result ->
//            val response = try {
//                json.decodeFromString<List<ULong>>(result.getString("body"))
//            } catch (_: Exception) {
//                showFailure(result.getString("body"))
//                return@sendRequest
//            }
//            isCreator = false
//            processResponse(response)
//        }
    }
//
//    if (enemyDeck.size >= 10) {
//        fun makeDeckWild(deck: CustomDeck) {
//            deck.apply {
//                add(Card(Rank.ACE, Suit.HEARTS, CardBack.ENCLAVE, true))
//                add(Card(Rank.ACE, Suit.CLUBS, CardBack.ENCLAVE, true))
//                add(Card(Rank.ACE, Suit.DIAMONDS, CardBack.ENCLAVE, true))
//                add(Card(Rank.KING, Suit.HEARTS, CardBack.MADNESS, true))
//                add(Card(Rank.KING, Suit.CLUBS, CardBack.MADNESS, true))
//                add(Card(Rank.KING, Suit.DIAMONDS, CardBack.MADNESS, true))
//                add(Card(Rank.KING, Suit.SPADES, CardBack.MADNESS, true))
//                add(Card(Rank.JACK, Suit.HEARTS, CardBack.MADNESS, true))
//                add(Card(Rank.QUEEN, Suit.HEARTS, CardBack.MADNESS, true))
//            }
//        }
//
//        StartPvP(
//            activity = activity,
//            playerCResources = run {
//                val deck = if (checkedCustomDeck)
//                    save.getCustomDeckCopy()
//                else
//                    CustomDeck(save.selectedDeck.first, save.selectedDeck.second)
//
//                if (checkedWild) {
//                    makeDeckWild(deck)
//                }
//
//                CResources(deck)
//            },
//            enemyStartDeck = run {
//                val deck = CustomDeck()
//                repeat(enemyDeck.size) {
//                    deck.add(enemyDeck[it])
//                }
//                if (checkedWild) {
//                    makeDeckWild(deck)
//                }
//                deck
//            },
//            roomNumber = isRoomCreated,
//            isCreator = isCreator,
//            showAlertDialog = showAlertDialog
//        ) {
//            enemyDeck = CustomDeck()
//            isRoomCreated = 0
//        }
//        return
//    }

    MenuItemOpen(stringResource(Res.string.menu_pvp), "<-", Alignment.TopCenter, {
        if (isRoomCreated == 0) {
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
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.height(96.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextFallout(
                        text = when (isRoomCreated) {
                            0 -> stringResource(Res.string.create_room)
                            2 -> stringResource(Res.string.failure)
                            4 -> stringResource(Res.string.incorrect_room_number)
                            else -> stringResource(Res.string.your_room_is, isRoomCreated)
                        },
                        getTextColor(),
                        getTextStrokeColor(),
                        14.sp,
                        Modifier
                            .fillMaxWidth(0.33f)
                            .padding(horizontal = 8.dp)
                            .clickableOk { createRoom() }
                            .background(getTextBackgroundColor())
                            .padding(4.dp),
                        textAlignment = TextAlign.Start
                    )
                    TextField(
                        modifier = Modifier.fillMaxWidth(0.5f),
                        singleLine = true,
                        enabled = isRoomCreated == 0,
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
                    TextFallout(
                        text = stringResource(Res.string.join),
                        getTextColor(),
                        getTextStrokeColor(),
                        14.sp,
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .clickableOk { joinRoom() }
                            .background(getTextBackgroundColor())
                            .padding(4.dp),
                        textAlignment = TextAlign.Start
                    )
                }
                Column(
                    Modifier
                        .padding(horizontal = 8.dp)
                        .wrapContentHeight()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextFallout(
                            stringResource(Res.string.use_custom_deck),
                            getTextColor(),
                            getTextStrokeColor(),
                            14.sp,
                            Modifier.fillMaxWidth(0.5f),
                            textAlignment = TextAlign.Start
                        )
                        CheckboxCustom(
                            { checkedCustomDeck },
                            {
                                checkedCustomDeck = !checkedCustomDeck
                                if (checkedCustomDeck) {
                                    playClickSound()
                                } else {
                                    playCloseSound()
                                }
                            },
                            { isRoomCreated == 0 }
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextFallout(
                            stringResource(Res.string.private_room),
                            getTextColor(),
                            getTextStrokeColor(),
                            14.sp,
                            Modifier.fillMaxWidth(0.5f),
                            textAlignment = TextAlign.Start
                        )
                        CheckboxCustom(
                            { checkedPrivateRoom },
                            {
                                checkedPrivateRoom = !checkedPrivateRoom
                                if (checkedPrivateRoom) {
                                    playClickSound()
                                } else {
                                    playCloseSound()
                                }
                            },
                            { isRoomCreated == 0 }
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextFallout(
                            stringResource(Res.string.use_wild_wasteland_cards),
                            getTextColor(),
                            getTextStrokeColor(),
                            14.sp,
                            Modifier.fillMaxWidth(0.5f),
                            textAlignment = TextAlign.Start
                        )
                        CheckboxCustom(
                            { checkedWild },
                            {
                                checkedWild = !checkedWild
                                if (checkedWild) {
                                    playClickSound()
                                } else {
                                    playCloseSound()
                                }
                            },
                            { isRoomCreated == 0 }
                        )
                    }
                }
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
                    stringResource(Res.string.pvp_piece),
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

var currentGameId = ""
@Composable
fun StartPvP(
    playerCResources: CResources,
    enemyStartDeck: CustomDeck,
    isCreator: Boolean,
    roomNumber: Int,
    showAlertDialog: (String, String, (() -> Unit)?) -> Unit,
    game: Game = rememberScoped {
        Game(
            playerCResources,
            EnemyPlayer(enemyStartDeck)
        ).also {
            it.isPlayerTurn = false
            playerCResources.shuffleDeck()
            currentGameId = it.id
        }
    },
    goBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    game.also {
        it.onWin = {
            processChallengesGameOver(it)
            playWinSound()
            scope.launch {
                showAlertDialog(
                    getString(Res.string.result),
                    getString(Res.string.you_win) + winCard(CardBack.STANDARD_RARE, false),
                    null
                )
            }
            saveData()
        }
        it.onLose = {
            playLoseSound()
            scope.launch {
                showAlertDialog(
                    getString(Res.string.result),
                    getString(Res.string.you_lose),
                    null
                )
            }
        }
    }
    val quit = {
        stopAmbient()
        goBack()
    }

    var caravansKey by rememberSaveable { mutableIntStateOf(0) }
    var enemyHandKey by rememberSaveable { mutableIntStateOf(0) }
    var playerHandKey by rememberSaveable { mutableIntStateOf(0) }
    fun updateCaravans() {
        caravansKey++
    }
    fun updateEnemyHand() {
        enemyHandKey++
    }
    fun updatePlayerHand() {
        playerHandKey++
    }

    val scopew = rememberCoroutineScope()
    fun pingForMove(sendHandCard: () -> Unit) {
        scope.launch {
            val link = "${crvnUrl}/crvn/get_move?room=$roomNumber" +
                    "&is_creators_move=${isCreator.toPythonBool()}"
            val result = sendRequest(link)
            if (result.contains("Timeout!")) {
                showAlertDialog(getString(Res.string.failed_to_start_the_game), result, null)
                return@launch
            }
        }
//
//            val move = try {
//                decodeMove(body)
//            } catch (_: Exception) {
//                CoroutineScope(Dispatchers.Unconfined).launch {
//                    delay(1900L)
//                    pingForMove(sendHandCard)
//                }
//                return@sendRequest
//            }
//
//            if (move.newCardInHandBack in CardBack.entries.indices) {
//                val cardReceived = Card(
//                    Rank.entries[move.newCardInHandRank],
//                    Suit.entries[move.newCardInHandSuit],
//                    CardBack.entries[move.newCardInHandBack],
//                    isAlt = move.isNewCardAlt
//                )
//                game.enemyCResources.addCardToHandDirect(cardReceived)
//                updateEnemyHand()
//
//                if (game.playerCResources.hand.size < 8) {
//                    sendHandCard()
//                    return@sendRequest
//                }
//                game.isPlayerTurn = isCreator
//                game.canPlayerMove = isCreator
//                updateCaravans()
//            } else {
//                (game.enemy as EnemyPlayer).latestMoveResponse = move
//                game.isPlayerTurn = isCreator
//
//                CoroutineScope(Dispatchers.Unconfined).launch {
//                    // game.enemy.makeMove(game)
//                    updateEnemyHand()
//                    game.processField(AnimationSpeed.NONE)
//
//                    updateCaravans()
//
//                    game.isPlayerTurn = true
//                    game.checkOnGameOver()
//                }
//            }
    }

    fun sendHandCard() {
        game.playerCResources.addToHand()
        val card = game.playerCResources.hand.last()
        updatePlayerHand()
//
//        val link = "${crvnUrl}/crvn/move?room=$roomNumber" +
//                "&is_creators_move=${isCreator.toPythonBool()}" +
//                "&move_code=0" +
//                "&new_card_back_in_hand_code=${card.back.ordinal}" +
//                "&new_card_rank_in_hand_code=${card.rank.ordinal}" +
//                "&new_card_suit_in_hand_code=${card.suit.ordinal}" +
//                "&is_alt=${card.isAlt.toPythonBool()}"
//        sendRequest(link) { _ ->
//            pingForMove(::sendHandCard)
//        }
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
        quit()
        return
    }

//    ShowGamePvP(
//        activity,
//        game,
//        isCreator,
//        roomNumber,
//        showAlertDialog,
//        ::updateEnemyHand,
//        ::updateCaravans,
//        ::updatePlayerHand
//    ) lambda@{
//        if (game.isOver()) {
//            quit()
//        } else {
//            showAlertDialog(
//                getString(Res.string.check_back_to_menu),
//                "",
//                quit
//            )
//        }
//    }
}