package com.unicorns.invisible.caravan

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sebaslogen.resaca.rememberScoped
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.challenge.Challenge
import com.unicorns.invisible.caravan.model.enemy.Enemy
import com.unicorns.invisible.caravan.model.enemy.EnemyHouse
import com.unicorns.invisible.caravan.model.enemy.EnemyRingo
import com.unicorns.invisible.caravan.model.enemy.EnemySunny
import com.unicorns.invisible.caravan.model.enemy.EnemySwank
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Caravan
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.save.saveOnGD
import com.unicorns.invisible.caravan.utils.TextClassic
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableCancel
import com.unicorns.invisible.caravan.utils.clickableOk
import com.unicorns.invisible.caravan.utils.clickableSelect
import com.unicorns.invisible.caravan.utils.frankStopsRadio
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getSelectionColor
import com.unicorns.invisible.caravan.utils.getStrokeColorByStyle
import com.unicorns.invisible.caravan.utils.getTextBackByStyle
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextColorByStyle
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.playCardFlipSound
import com.unicorns.invisible.caravan.utils.playCashSound
import com.unicorns.invisible.caravan.utils.playCloseSound
import com.unicorns.invisible.caravan.utils.playFrankPhrase
import com.unicorns.invisible.caravan.utils.playJokerSounds
import com.unicorns.invisible.caravan.utils.playLoseSound
import com.unicorns.invisible.caravan.utils.playSelectSound
import com.unicorns.invisible.caravan.utils.playVatsReady
import com.unicorns.invisible.caravan.utils.playWinSound
import com.unicorns.invisible.caravan.utils.radioLock
import com.unicorns.invisible.caravan.utils.radioPlayers
import com.unicorns.invisible.caravan.utils.scrollbar
import com.unicorns.invisible.caravan.utils.startLevel11Theme
import com.unicorns.invisible.caravan.utils.startRadio
import com.unicorns.invisible.caravan.utils.stopAmbient
import kotlinx.coroutines.delay
import okio.withLock


enum class Payment {
    FREE,
    ONE_HUNDRED_CAPS,
    TICKET
}


@Composable
fun TowerScreen(
    activity: MainActivity,
    showAlertDialog: (String, String) -> Unit,
    goBack: () -> Unit,
) {
    var payment by remember { mutableStateOf<Payment?>(null) }
    var level by remember { mutableIntStateOf(activity.save?.towerLevel ?: 0) }
    var isGameRigged by remember { mutableStateOf(activity.save?.isGameRigged ?: false) }
    var startFrank by remember { mutableStateOf(false) }
    if (startFrank) {
        ShowFrank(activity) { startFrank = false }
        return
    }

    var showGameLevel1 by remember { mutableStateOf(false) }
    var showGameLevel2 by remember { mutableStateOf(false) }
    var showGameLevel3 by remember { mutableStateOf(false) }
    var showGameLevel4 by remember { mutableStateOf(false) }
    var showGameLevel5 by remember { mutableStateOf(false) }
    var showGameLevel6 by remember { mutableStateOf(false) }
    var showGameLevel7 by remember { mutableStateOf(false) }
    var showGameLevel8 by remember { mutableStateOf(false) }
    var showGameLevel9 by remember { mutableStateOf(false) }
    var showGameLevel10 by remember { mutableStateOf(false) }
    var showGameLevel11 by remember { mutableStateOf(false) }

    var levelMemory by rememberSaveable { mutableIntStateOf(0) }
    @Composable
    fun showTower(enemy: Enemy, goBack: () -> Unit) {
        StartTowerGame(activity, enemy, showAlertDialog, {
            levelMemory = level
            level = 0
            activity.save?.let {
                it.towerLevel = 0
                saveOnGD(activity)
            }
        }, {
            level = levelMemory + 1
            activity.save?.let {
                it.towerLevel = levelMemory + 1
                saveOnGD(activity)
            }
        }, {
            level = 0
            activity.save?.let {
                it.towerLevel = 0
                saveOnGD(activity)
            }
        }, goBack)
    }
    when {
        showGameLevel1 -> {
            showTower(EnemySunny) {
                showGameLevel1 = false
            }
            return
        }
        showGameLevel2 -> {
            showTower(EnemyRingo) {
                showGameLevel2 = false
            }
            return
        }
        showGameLevel3 -> {
            showTower(EnemyRingo) {
                showGameLevel3 = false
            }
            return
        }
        showGameLevel4 -> {
            showTower(EnemyRingo) {
                showGameLevel4 = false
            }
            return
        }
        showGameLevel5 -> {
            showTower(EnemySwank) {
                showGameLevel5 = false
            }
            return
        }
        showGameLevel6 -> {
            showTower(EnemyRingo) {
                showGameLevel6 = false
            }
            return
        }
        showGameLevel7 -> {
            showTower(EnemyRingo) {
                showGameLevel7 = false
            }
            return
        }
        showGameLevel8 -> {
            showTower(EnemyHouse) {
                showGameLevel8 = false
            }
            return
        }
        showGameLevel9 -> {
            showTower(EnemyRingo) {
                showGameLevel9 = false
            }
            return
        }

        showGameLevel10 -> {
            showTower(EnemyRingo) {
                showGameLevel10 = false
            }
            return
        }

        showGameLevel11 -> {
            startLevel11Theme(activity)
            StartTowerGame(activity, EnemyRingo, showAlertDialog, {
                levelMemory = level
                level = 0
                activity.save?.let {
                    it.towerLevel = 0
                    saveOnGD(activity)
                }
            }, {
                level = levelMemory + 1
                activity.save?.let {
                    it.towerLevel = levelMemory + 1
                    it.isEnclaveThemeAvailable = true
                    saveOnGD(activity)
                }
            }, {
                level = 0
                activity.save?.let {
                    it.towerLevel = 0
                    saveOnGD(activity)
                }
            }) {
                showGameLevel11 = false
                frankStopsRadio = false
                isGameRigged = false
                activity.save?.let {
                    it.isGameRigged = false
                    saveOnGD(activity)
                }
                startRadio(activity)
            }
            return
        }
    }


    Column(
        Modifier
            .fillMaxSize()
            .background(getBackgroundColor(activity)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val state2 = rememberLazyListState()
        LazyColumn(
            Modifier
                .fillMaxHeight(0.8f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .scrollbar(
                    state2,
                    knobColor = getKnobColor(activity), trackColor = getTrackColor(activity),
                    horizontal = false,
                ),
            state = state2,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Box(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.align(Alignment.Center),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextFallout(
                            stringResource(R.string.tower),
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            32.sp,
                            Alignment.Center,
                            Modifier,
                            TextAlign.Center
                        )
                    }
                }
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextFallout(
                        "(Custom deck only!)",
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        24.sp,
                        Alignment.Center,
                        Modifier,
                        TextAlign.Center
                    )
                    TextFallout(
                        "(You can change custom deck between games.)",
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        16.sp,
                        Alignment.Center,
                        Modifier,
                        TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    TextFallout(
                        "(Progress is saved between sessions!).",
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        24.sp,
                        Alignment.Center,
                        Modifier,
                        TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))

                if (level == 0) {
                    TextFallout(
                        "Tickets, please!\n(You have tickets: ${activity.save?.tickets ?: 0};\ncaps: ${activity.save?.caps ?: 0}.)",
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        20.sp,
                        Alignment.Center,
                        Modifier,
                        TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
                        TextFallout(
                            "Play for free (NO JACKPOT!)",
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            20.sp,
                            Alignment.Center,
                            Modifier
                                .border(
                                    BorderStroke(
                                        if (payment == Payment.FREE) 3.dp else (-1).dp,
                                        getSelectionColor(activity)
                                    )
                                )
                                .padding(4.dp)
                                .clickable {
                                    if (payment == Payment.FREE) {
                                        payment = null
                                        playCloseSound(activity)
                                    } else {
                                        payment = Payment.FREE
                                        playSelectSound(activity)
                                    }
                                }
                                .background(getTextBackgroundColor(activity))
                                .padding(4.dp),
                            TextAlign.Center
                        )
                        TextFallout(
                            "Pay 1 ticket",
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            20.sp,
                            Alignment.Center,
                            Modifier
                                .border(
                                    BorderStroke(
                                        if (payment == Payment.TICKET) 3.dp else (-1).dp,
                                        getSelectionColor(activity)
                                    )
                                )
                                .padding(4.dp)
                                .clickable {
                                    if (payment == Payment.TICKET) {
                                        payment = null
                                        playCloseSound(activity)
                                    } else {
                                        payment = Payment.TICKET
                                        playSelectSound(activity)
                                    }
                                }
                                .background(getTextBackgroundColor(activity))
                                .padding(4.dp),
                            TextAlign.Center
                        )
                        TextFallout(
                            "Pay 100 caps",
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            20.sp,
                            Alignment.Center,
                            Modifier
                                .border(
                                    BorderStroke(
                                        if (payment == Payment.ONE_HUNDRED_CAPS) 3.dp else (-1).dp,
                                        getSelectionColor(activity)
                                    )
                                )
                                .padding(4.dp)
                                .clickable {
                                    if (payment == Payment.ONE_HUNDRED_CAPS) {
                                        payment = null
                                        playCloseSound(activity)
                                    } else {
                                        payment = Payment.ONE_HUNDRED_CAPS
                                        playSelectSound(activity)
                                    }
                                }
                                .background(getTextBackgroundColor(activity))
                                .padding(4.dp),
                            TextAlign.Center
                        )
                    }

                    Spacer(Modifier.height(32.dp))
                    TextFallout(
                        stringResource(R.string.start),
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        24.sp,
                        Alignment.Center,
                        modifier = Modifier
                            .clickableOk(activity) {
                                if (payment == null) {
                                    showAlertDialog("HEY!", "Select payment method.")
                                    return@clickableOk
                                }

                                when (payment) {
                                    Payment.ONE_HUNDRED_CAPS -> {
                                        if ((activity.save?.caps ?: 0) < 100) {
                                            showAlertDialog("HEY!", "You don't have enough cash, kid.")
                                            return@clickableOk
                                        }
                                    }
                                    Payment.TICKET -> {
                                        if ((activity.save?.tickets ?: 0) < 1) {
                                            showAlertDialog("HEY!", "You don't have a ticket on you.")
                                            return@clickableOk
                                        }
                                    }
                                    else -> {}
                                }

                                level++
                                activity.save?.let {
                                    it.towerLevel++

                                    when (payment) {
                                        Payment.ONE_HUNDRED_CAPS -> {
                                            it.isTowerFree = false
                                            it.caps -= 100
                                        }
                                        Payment.TICKET -> {
                                            it.isTowerFree = false
                                            it.tickets--
                                        }
                                        else -> {
                                            it.isTowerFree = true
                                        }
                                    }

                                    saveOnGD(activity)
                                }
                            }
                            .background(getTextBackgroundColor(activity))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        TextAlign.Center
                    )
                } else {
                    val inBank = if (activity.save?.isTowerFree == false) {
                        when (level) {
                            1 -> 1
                            2 -> 5
                            3 -> 10
                            4 -> 25
                            5 -> 50
                            6 -> 101
                            7 -> 166
                            8 -> 222
                            9 -> 333
                            10 -> 444
                            else -> 555
                        }
                    } else {
                        0
                    }
                    @Composable
                    fun showTowerCard(enemyName: String) {
                        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            TextFallout(
                                "Level: $level",
                                getTextColor(activity),
                                getTextStrokeColor(activity),
                                24.sp,
                                Alignment.Center,
                                Modifier,
                                TextAlign.Center
                            )

                            TextFallout(
                                "Currently in bank: $inBank caps",
                                getTextColor(activity),
                                getTextStrokeColor(activity),
                                24.sp,
                                Alignment.Center,
                                Modifier,
                                TextAlign.Center
                            )
                            TextFallout(
                                "Enemy: $enemyName",
                                getTextColor(activity),
                                getTextStrokeColor(activity),
                                24.sp,
                                Alignment.Center,
                                Modifier,
                                TextAlign.Center
                            )
                        }
                    }
                    when (level) {
                        1 -> {
                            showTowerCard("Sunny Smiles")
                        }
                        2 -> {
                            showTowerCard("Ringo")
                        }
                        3 -> {
                            showTowerCard("Cliff Briscoe")
                        }
                        4 -> {
                            showTowerCard("Yes Man")
                        }
                        5 -> {
                            showTowerCard(stringResource(R.string.pve_enemy_queen))
                        }
                        6 -> {
                            showTowerCard("Ambassador Crocker")
                        }
                        7 -> {
                            showTowerCard("The King")
                        }
                        8 -> {
                            showTowerCard(stringResource(R.string.mr_house))
                        }
                        9 -> {
                            showTowerCard("General Lee Oliver")
                        }
                        10 -> {
                            showTowerCard("Caesar")
                        }
                        11 -> {
                            if (isGameRigged) {
                                showTowerCard("Frank Horrigan")
                            } else {
                                showTowerCard("no enemy")
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                        TextFallout(
                            "Take the cash!",
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            24.sp,
                            Alignment.Center,
                            modifier = Modifier
                                .clickableOk(activity) {
                                    if (isGameRigged) {
                                        isGameRigged = false
                                        frankStopsRadio = false
                                        startRadio(activity)
                                        activity.save?.let {
                                            it.isGameRigged = false
                                            saveOnGD(activity)
                                        }
                                    }
                                    level = 0
                                    payment = null
                                    activity.save?.let {
                                        it.towerLevel = 0
                                        it.caps += inBank
                                        saveOnGD(activity)
                                    }
                                    playCashSound(activity)
                                    showAlertDialog("Congratulations!", "You have earned $inBank caps!")
                                }
                                .background(getTextBackgroundColor(activity))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            TextAlign.Center
                        )
                        if (!(level == 11 && !isGameRigged)) {
                            TextFallout(
                                "En garde!",
                                getTextColor(activity),
                                getTextStrokeColor(activity),
                                24.sp,
                                Alignment.Center,
                                modifier = Modifier
                                    .clickableOk(activity) {
                                        when (level) {
                                            1 -> { showGameLevel1 = true }
                                            2 -> { showGameLevel2 = true }
                                            3 -> { showGameLevel3 = true }
                                            4 -> { showGameLevel4 = true }
                                            5 -> { showGameLevel5 = true }
                                            6 -> { showGameLevel6 = true }
                                            7 -> { showGameLevel7 = true }
                                            8 -> { showGameLevel8 = true }
                                            9 -> { showGameLevel9 = true }
                                            10 -> { showGameLevel10 = true }
                                            11 -> { showGameLevel11 = true }
                                            else -> {}
                                        }
                                    }
                                    .background(getTextBackgroundColor(activity))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                TextAlign.Center
                            )
                        } else {
                            TextClassic(
                                "Rig the game",
                                getTextColorByStyle(activity, Style.PIP_BOY),
                                getStrokeColorByStyle(activity, Style.PIP_BOY),
                                18.sp,
                                Alignment.Center,
                                modifier = Modifier
                                    .clickableSelect(activity) {
                                        isGameRigged = true
                                        activity.save?.let {
                                            it.isGameRigged = true
                                            startFrank = true
                                            saveOnGD(activity)
                                        }
                                    }
                                    .background(getTextBackByStyle(activity, Style.PIP_BOY))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
        TextFallout(
            stringResource(R.string.menu_back),
            getTextColor(activity),
            getTextStrokeColor(activity),
            24.sp,
            Alignment.Center,
            modifier = Modifier
                .clickableCancel(activity) { if (!isGameRigged) goBack() }
                .background(getTextBackgroundColor(activity))
                .padding(8.dp),
            TextAlign.Center
        )
    }
}

@Composable
fun StartTowerGame(
    activity: MainActivity,
    enemy: Enemy,
    showAlertDialog: (String, String) -> Unit,
    onStart: () -> Unit,
    onWin: () -> Unit,
    onLose: () -> Unit,
    goBack: () -> Unit,
) {
    val playerCResources = CResources(activity.save?.getCustomDeckCopy() ?: CustomDeck(CardBack.STANDARD, false))
    val game = rememberScoped {
        Game(
            playerCResources,
            enemy
        ).also {
            onStart()
            it.startGame()
        }
    }

    game.also {
        it.onWin = {
            activity.processChallengesGameOver(it)
            playWinSound(activity)
            onWin()
            showAlertDialog(activity.getString(R.string.result), activity.getString(R.string.you_win))
        }
        it.onLose = {
            playLoseSound(activity)
            onLose()
            showAlertDialog(
                activity.getString(R.string.result),
                activity.getString(R.string.you_lose)
            )
        }
        it.jokerPlayedSound = { playJokerSounds(activity) }
    }

    activity.goBack = { stopAmbient(); goBack() }

    var selectedCard by remember { mutableStateOf<Int?>(null) }
    var selectedCaravan by remember { mutableIntStateOf(-1) }

    var caravansKey by rememberSaveable { mutableStateOf(true) }
    var enemyHandKey by rememberSaveable { mutableIntStateOf(0) }

    val animationSpeed = activity.animationSpeed.value ?: AnimationSpeed.NORMAL

    game.enemyCResources.onDropCardFromHand = { enemyHandKey = -1 }

    fun onCardClicked(index: Int) {
        if (game.isOver()) {
            return
        }
        selectedCard = if (index == selectedCard || index !in game.playerCResources.hand.indices) {
            null
        } else {
            index
        }
        if (selectedCard == null) {
            playCloseSound(activity)
        } else {
            playSelectSound(activity)
        }
        selectedCaravan = -1
    }

    fun onCaravanClicked(index: Int) {
        if (game.isOver()) {
            return
        }
        selectedCaravan = index
        if (selectedCaravan == -1) {
            playCloseSound(activity)
        } else {
            playSelectSound(activity)
        }
        selectedCard = null
        caravansKey = !caravansKey
    }

    fun updateCaravans() {
        caravansKey = !caravansKey
    }

    fun updateEnemyHand() {
        enemyHandKey = when (enemyHandKey) {
            -2 -> 0
            -1 -> -2
            else -> (1 - enemyHandKey).coerceIn(0, 1)
        }
    }

    fun resetSelected() {
        selectedCaravan = -1
        selectedCard = null
    }

    fun dropCardFromHand() {
        val selectedCardNN = selectedCard ?: return
        playVatsReady(activity)
        game.playerCResources.removeFromHand(selectedCardNN)
        resetSelected()
        game.afterPlayerMove(
            { updateEnemyHand(); updateCaravans() },
            animationSpeed
        )
    }

    fun dropCaravan() {
        val selectedCaravanNN = selectedCaravan
        if (selectedCaravanNN == -1) return
        playVatsReady(activity)
        activity.processChallengesMove(Challenge.Move(moveCode = 1), game)
        game.playerCaravans[selectedCaravanNN].dropCaravan()
        resetSelected()
        game.afterPlayerMove(
            { updateEnemyHand(); updateCaravans() },
            animationSpeed
        )
    }

    fun addCardToCaravan(caravan: Caravan, position: Int, isEnemy: Boolean) {
        fun onCaravanCardInserted() {
            resetSelected()
            game.afterPlayerMove(
                { updateEnemyHand(); updateCaravans() },
                animationSpeed
            )
        }

        val cardIndex = selectedCard
        val card = cardIndex?.let { game.playerCResources.hand[cardIndex] }
        if (card != null && game.isPlayerTurn && !game.isOver() && !(game.isInitStage() && card.isFace())) {
            if (card.isFace()) {
                if (position in caravan.cards.indices && caravan.cards[position].canAddModifier(card)) {
                    playCardFlipSound(activity)
                    if (card.rank == Rank.JOKER) {
                        playJokerSounds(activity)
                    }
                    activity.processChallengesMove(
                        Challenge.Move(
                        moveCode = 4,
                        handCard = card
                    ), game)
                    caravan.cards[position].addModifier(
                        game.playerCResources.removeFromHand(
                            cardIndex
                        )
                    )
                    onCaravanCardInserted()
                }
            } else {
                if (position == caravan.cards.size && !isEnemy) {
                    if (caravan.canPutCardOnTop(card)) {
                        playCardFlipSound(activity)
                        activity.processChallengesMove(
                            Challenge.Move(
                            moveCode = 3,
                            handCard = card
                        ), game)
                        caravan.putCardOnTop(game.playerCResources.removeFromHand(cardIndex))
                        onCaravanCardInserted()
                    }
                }
            }
        }
    }

    ShowGameRaw(
        activity,
        false,
        game,
        {
            if (game.isOver()) {
                activity.goBack?.invoke()
                activity.goBack = null
                return@ShowGameRaw
            }

            showAlertDialog(activity.getString(R.string.check_back_to_menu), "Tower progress will be lost!")
        },
        animationSpeed,
        { "" },
        { "" },
        {},
        ::onCardClicked,
        selectedCard,
        getSelectedCaravan = { selectedCaravan },
        setSelectedCaravan = ::onCaravanClicked,
        { a1, _, a3, a4 -> addCardToCaravan(a1, a3, a4) },
        ::dropCardFromHand,
        ::dropCaravan,
        enemyHandKey
    )
}

@Composable
fun ShowFrank(activity: MainActivity, goBack: () -> Unit) {
    LaunchedEffect(Unit) {
        radioLock.withLock {
            radioPlayers.forEach {
                it.stop()
                radioPlayers.remove(it)
                it.release()
            }
            frankStopsRadio = true
        }
        playFrankPhrase(activity, R.raw.frank_on_welcome)
        delay(15000L)
        goBack()
    }
    Box(Modifier.fillMaxSize().background(Color.Black)) {

    }
}