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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
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
import com.unicorns.invisible.caravan.multiplayer.MyUrlRequestCallback
import com.unicorns.invisible.caravan.multiplayer.decodeMove
import com.unicorns.invisible.caravan.save.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import org.chromium.net.CronetEngine
import org.chromium.net.UrlRequest
import org.json.JSONObject
import java.util.concurrent.Executors


// TODO: android:usesCleartextTraffic="true"
var cronetEngine: CronetEngine? = null


fun customDeckToInts(customDeck: CustomDeck): List<ULong> {
    val result = ArrayList<ULong>()

    CardBack.entries.forEachIndexed { _, cardBack ->
        var code = 0UL

        var cnt = 0
        Rank.entries.forEach { rank ->
            if (rank == Rank.JOKER) {
                if (customDeck.contains(Card(rank, Suit.HEARTS, cardBack))) {
                    code = code or (1UL shl cnt)
                }
                cnt++
                if (customDeck.contains(Card(rank, Suit.CLUBS, cardBack))) {
                    code = code or (1UL shl cnt)
                }
                cnt++
            } else {
                Suit.entries.forEach { suit ->
                    if (customDeck.contains(Card(rank, suit, cardBack))) {
                        code = code or (1UL shl cnt)
                    }
                    cnt++
                }
            }
        }

        result.add(code)
    }
    return result
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

    fun processResponse(response: List<ULong>) {
        if (response.size != CardBack.entries.size) {
            isRoomCreated = 2
            CoroutineScope(Dispatchers.Unconfined).launch {
                delay(3800L)
                isRoomCreated = 0
            }
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

        enemyDeck = CustomDeck().also { customDeck ->
            cardsList.forEach { customDeck.add(it) }
        }
    }

    var isFreeRoomRequested = false
    fun updateAvailableRoom(isCustom: Boolean) {
        if (isFreeRoomRequested) {
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            val requestBuilder = cronetEngine?.newUrlRequestBuilder(
                "http://192.168.1.191:8000/crvn/get_free_room?is_custom=${isCustom.toString().replaceFirstChar { it.uppercase() }}",
                object : MyUrlRequestCallback(object : OnFinishRequest<JSONObject> {
                    override fun onFinishRequest(result: JSONObject) {
                        val res = result.getString("body").toIntOrNull()
                        roomNumber = res?.toString() ?: ""
                        isFreeRoomRequested = false
                    }
                }) {},
                Executors.newSingleThreadExecutor()
            ) ?: return@launch

            isFreeRoomRequested = true
            val request = requestBuilder.build()
            request.start()
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
                        else -> "Your Room is $isRoomCreated. Awaiting the Opponent....."
                    },
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    style = TextStyle(color = Color(activity.getColor(R.color.colorPrimary)), fontSize = 16.sp),
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .clickable {
                            val roomNumberQ = roomNumber.toIntOrNull()
                            if (isRoomCreated != 0 || roomNumberQ in listOf(null, 0, 1, 2)) {
                                return@clickable
                            }
                            isRoomCreated = roomNumberQ ?: return@clickable
                            val cronetEngine = cronetEngine ?: return@clickable
                            val deckCodes = customDeckToInts(
                                if (checkedCustomDeck) activity.save!!.getCustomDeckCopy() else CustomDeck(
                                    selectedDeck()
                                )
                            )
                            CoroutineScope(Dispatchers.IO).launch {
                                val requestBuilder = cronetEngine.newUrlRequestBuilder(
                                    "http://192.168.1.191:8000/crvn/create?is_custom=${
                                        checkedCustomDeck
                                            .toString()
                                            .replaceFirstChar { it.uppercase() }
                                    }" +
                                            "&room=${
                                                isRoomCreated
                                                    .toString()
                                                    .replaceFirstChar { it.uppercase() }
                                            }" +
                                            "&is_private=${
                                                checkedPrivate
                                                    .toString()
                                                    .replaceFirstChar { it.uppercase() }
                                            }" +
                                            "&deck0=${deckCodes[0]}" +
                                            "&deck1=${deckCodes[1]}" +
                                            "&deck2=${deckCodes[2]}" +
                                            "&deck3=${deckCodes[3]}" +
                                            "&deck4=${deckCodes[4]}" +
                                            "&deck5=${deckCodes[5]}",
                                    object :
                                        MyUrlRequestCallback(object : OnFinishRequest<JSONObject> {
                                            override fun onFinishRequest(result: JSONObject) {
                                                val response = try {
                                                    json.decodeFromString<List<ULong>>(
                                                        result.getString("body")
                                                    )
                                                } catch (e: Exception) {
                                                    isRoomCreated = 2
                                                    CoroutineScope(Dispatchers.Unconfined).launch {
                                                        delay(3800L)
                                                        isRoomCreated = 0
                                                    }
                                                    return
                                                }
                                                isCreator = true
                                                processResponse(response)
                                            }
                                        }) {},
                                    Executors.newSingleThreadExecutor()
                                )

                                val request: UrlRequest = requestBuilder.build()
                                request.start()
                            }
                        }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(modifier = Modifier
                        .fillMaxHeight(0.5f)
                        .padding(8.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(modifier = Modifier.fillMaxWidth(0.7f), text = stringResource(R.string.pve_use_custom_deck))
                        Checkbox(checked = checkedCustomDeck, onCheckedChange = {
                            checkedCustomDeck = !checkedCustomDeck
                            updateAvailableRoom(checkedCustomDeck)
                        }, colors = CheckboxColors(
                            checkedCheckmarkColor = Color(activity.getColor(R.color.colorPrimaryDark)),
                            uncheckedCheckmarkColor = Color.Transparent,
                            checkedBoxColor = Color(activity.getColor(R.color.colorPrimary)),
                            uncheckedBoxColor = Color.Transparent,
                            disabledCheckedBoxColor = Color.Red,
                            disabledUncheckedBoxColor = Color.Red,
                            disabledIndeterminateBoxColor = Color.Red,
                            checkedBorderColor = Color(activity.getColor(R.color.colorPrimaryDark)),
                            uncheckedBorderColor = Color(activity.getColor(R.color.colorPrimaryDark)),
                            disabledBorderColor = Color.Red,
                            disabledUncheckedBorderColor = Color.Red,
                            disabledIndeterminateBorderColor = Color.Red,
                        ), enabled = isRoomCreated == 0)
                    }
                    Row(modifier = Modifier
                        .padding(8.dp)
                        .fillMaxSize(),
                        horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(modifier = Modifier.fillMaxWidth(0.7f), text = "Private room")

                        Checkbox(checked = checkedPrivate, onCheckedChange = {
                            checkedPrivate = !checkedPrivate
                        }, colors = CheckboxColors(
                            checkedCheckmarkColor = Color(activity.getColor(R.color.colorPrimaryDark)),
                            uncheckedCheckmarkColor = Color.Transparent,
                            checkedBoxColor = Color(activity.getColor(R.color.colorPrimary)),
                            uncheckedBoxColor = Color.Transparent,
                            disabledCheckedBoxColor = Color.Red,
                            disabledUncheckedBoxColor = Color.Red,
                            disabledIndeterminateBoxColor = Color.Red,
                            checkedBorderColor = Color(activity.getColor(R.color.colorPrimaryDark)),
                            uncheckedBorderColor = Color(activity.getColor(R.color.colorPrimaryDark)),
                            disabledBorderColor = Color.Red,
                            disabledUncheckedBorderColor = Color.Red,
                            disabledIndeterminateBorderColor = Color.Red,
                        ), enabled = isRoomCreated == 0)
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
                        if (isRoomCreated != 0 || roomNumber.toIntOrNull() in listOf(0, 1, 2, null)) {
                            return@clickable
                        }
                        isRoomCreated = roomNumber.toIntOrNull() ?: return@clickable
                        val cronetEngine = cronetEngine ?: return@clickable
                        val deckCodes = customDeckToInts(activity.save!!.getCustomDeckCopy())
                        CoroutineScope(Dispatchers.IO).launch {
                            val requestBuilder = cronetEngine.newUrlRequestBuilder(
                                "http://192.168.1.191:8000/crvn/join?room=$isRoomCreated" +
                                        "&back=${selectedDeck().ordinal}" +
                                        "&deck0=${deckCodes[0]}" +
                                        "&deck1=${deckCodes[1]}" +
                                        "&deck2=${deckCodes[2]}" +
                                        "&deck3=${deckCodes[3]}" +
                                        "&deck4=${deckCodes[4]}" +
                                        "&deck5=${deckCodes[5]}",
                                object : MyUrlRequestCallback(object : OnFinishRequest<JSONObject> {
                                    override fun onFinishRequest(result: JSONObject) {
                                        val response = try {
                                            json.decodeFromString<List<ULong>>(
                                                result.getString("body")
                                            )
                                        } catch (e: Exception) {
                                            isRoomCreated = 2
                                            CoroutineScope(Dispatchers.Unconfined).launch {
                                                delay(3800L)
                                                isRoomCreated = 0
                                            }
                                            return
                                        }
                                        isCreator = false
                                        processResponse(response)
                                    }
                                }) {},
                                Executors.newSingleThreadExecutor()
                            )

                            val request: UrlRequest = requestBuilder.build()
                            request.start()
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
                text = "So, you wanna play with people. Two ways:\n" +
                        "1) you join Room. Two ways of getting room number: either from someone (then enter the number to 'Join Room' field) " +
                        "or use a number provided by server (if there are public rooms without opponent, the room number will be written in 'Join Room' field).\n" +
                        "Select whether you wanna use custom deck or not by checking the box above.\n" +
                        "Finally, press 'Join!!!'. If you didn't wait for too long, the game should start.\n" +
                        "2) You create room. Select whether to use custom deck or not, select privacy (private room number is never provided by server to other people). " +
                        "Finally, press 'Create Room', then wait.\n" +
                        "\n" +
                        "Room number is from 10 to 22229.\n" +
                        "Time limit for awaiting opponent is 38 seconds, then room is destroyed.\n" +
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
                EnemyPlayer(
                    enemyStartDeck
                )
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
        val requestBuilder = cronetEngine?.newUrlRequestBuilder(
            "http://192.168.1.191:8000/crvn/move?room=$roomNumber" +
                    "&is_creators_move=${isCreator.toString().replaceFirstChar { it.uppercase() }}" +
                    "&is_util=False" +
                    "&move_code=0" +
                    "&new_card_back_in_hand_code=${card.back.ordinal}" +
                    "&new_card_rank_in_hand_code=${card.rank.ordinal}" +
                    "&new_card_suit_in_hand_code=${card.suit.ordinal}"
            ,
            object : MyUrlRequestCallback(object : OnFinishRequest<JSONObject> {
                override fun onFinishRequest(result: JSONObject) {
                    val move = decodeMove(result.getString("body"))
                    if (game.playerCResources.hand.size < 8) {
                        val cardReceived = Card(
                            Rank.entries[move.newCardInHandRank],
                            Suit.entries[move.newCardInHandSuit],
                            CardBack.entries[move.newCardInHandBack],
                        )
                        game.enemyCResources.addCardToHandPvP(cardReceived)
                        pvpUpdater = !pvpUpdater
                        sendHandCard()
                        return
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
                    } else {
                        val cardReceived = Card(
                            Rank.entries[move.newCardInHandRank],
                            Suit.entries[move.newCardInHandSuit],
                            CardBack.entries[move.newCardInHandBack],
                        )
                        game.enemyCResources.addCardToHandPvP(cardReceived)
                    }
                    pvpUpdater = !pvpUpdater
                }
            }) {},
            Executors.newSingleThreadExecutor()
        ) ?: return
        val request = requestBuilder.build()
        request.start()
    }

    if (
        game.enemyCResources.hand.isEmpty() && game.playerCResources.hand.isEmpty() &&
        game.enemyCResources.deckSize != 0 && game.playerCResources.deckSize != 0
    ) {
        if (!isCreator) {
            val requestBuilder = cronetEngine?.newUrlRequestBuilder(
                "http://192.168.1.191:8000/crvn/move?room=$roomNumber" +
                        "&is_creators_move=False" +
                        "&is_util=True"
                ,
                object : MyUrlRequestCallback(object : OnFinishRequest<JSONObject> {
                    override fun onFinishRequest(result: JSONObject) {
                        val move = decodeMove(result.getString("body"))
                        val card = Card(
                            Rank.entries[move.newCardInHandRank],
                            Suit.entries[move.newCardInHandSuit],
                            CardBack.entries[move.newCardInHandBack],
                        )
                        game.enemyCResources.addCardToHandPvP(card)
                        sendHandCard()
                    }
                }) {},
                Executors.newSingleThreadExecutor()
            ) ?: return
            val request = requestBuilder.build()
            request.start()
        } else {
            sendHandCard()
        }
    }

    key(pvpUpdater) {
        ShowGamePvP(activity, game, isCreator, roomNumber) { goBack() }
    }
}
