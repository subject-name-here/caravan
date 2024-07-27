package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.enemy.EnemyFinalBoss
import com.unicorns.invisible.caravan.model.enemy.EnemyPriestess
import com.unicorns.invisible.caravan.model.enemy.EnemySnuffles
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.utils.MenuItemOpen
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableCancel
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getDialogBackground
import com.unicorns.invisible.caravan.utils.getDialogTextColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.nextSong
import com.unicorns.invisible.caravan.utils.playNoCardAlarm
import com.unicorns.invisible.caravan.utils.playVatsEnter
import com.unicorns.invisible.caravan.utils.scrollbar


@Composable
fun ShowWildWasteland(
    activity: MainActivity,
    showAlertDialog: (String, String) -> Unit,
    goBack: () -> Unit
) {
    var showGameSnuffles by rememberSaveable { mutableStateOf(false) }
    var showGamePriestess by rememberSaveable { mutableStateOf(false) }
    var showGameFinalBoss by rememberSaveable { mutableStateOf(false) }
    var showGameFinalBossWild by rememberSaveable { mutableStateOf(false) }
    var dialogText by rememberSaveable { mutableStateOf("") }

    if (dialogText.isNotBlank()) {
        AlertDialog(
            modifier = Modifier.border(width = 4.dp, color = getTextColor(activity)),
            onDismissRequest = {},
            confirmButton = {
                TextFallout(
                    "OK",
                    getDialogTextColor(activity),
                    getDialogTextColor(activity),
                    14.sp,
                    Alignment.CenterEnd,
                    Modifier.clickableCancel(activity) { dialogText = "" },
                    TextAlign.End
                )
            },
            title = {
                TextFallout(
                    "Supreme Leader says:",
                    getDialogTextColor(activity),
                    getDialogTextColor(activity),
                    24.sp,
                    Alignment.CenterEnd,
                    Modifier,
                    TextAlign.End
                )
            },
            text = {
                TextFallout(
                    dialogText,
                    getDialogTextColor(activity),
                    getDialogTextColor(activity),
                    18.sp,
                    Alignment.CenterStart,
                    Modifier,
                    TextAlign.Start
                )
            },
            containerColor = getDialogBackground(activity),
            textContentColor = getDialogTextColor(activity),
            shape = RectangleShape,
        )
    }

    fun getPlayerDeck(isFinalBoss: Boolean, isFinalBossWild: Boolean = false): CResources {
        if (isFinalBoss) {
            val deck = CustomDeck()
            if (isFinalBossWild) {
                CardBack.playableBacks.forEach { back ->
                    Rank.entries.forEach { rank ->
                        if (rank == Rank.JOKER) {
                            deck.add(Card(rank, Suit.HEARTS, back, true))
                            deck.add(Card(rank, Suit.CLUBS, back, true))
                        } else {
                            Suit.entries.forEach { suit ->
                                deck.add(Card(rank, suit, back, true))
                            }
                        }
                    }
                }

                deck.apply {
                    add(Card(Rank.ACE, Suit.HEARTS, CardBack.WILD_WASTELAND, true))
                    add(Card(Rank.ACE, Suit.CLUBS, CardBack.WILD_WASTELAND, true))
                    add(Card(Rank.ACE, Suit.DIAMONDS, CardBack.WILD_WASTELAND, true))
                    add(Card(Rank.KING, Suit.HEARTS, CardBack.WILD_WASTELAND, false))
                    add(Card(Rank.KING, Suit.CLUBS, CardBack.WILD_WASTELAND, false))
                    add(Card(Rank.KING, Suit.DIAMONDS, CardBack.WILD_WASTELAND, false))
                    add(Card(Rank.KING, Suit.SPADES, CardBack.WILD_WASTELAND, false))
                    add(Card(Rank.JACK, Suit.HEARTS, CardBack.WILD_WASTELAND, false))
                    add(Card(Rank.QUEEN, Suit.HEARTS, CardBack.WILD_WASTELAND, false))
                }
            } else {
                deck.add(Card(Rank.KING, Suit.HEARTS, CardBack.DECK_13, false))
                deck.add(Card(Rank.KING, Suit.CLUBS, CardBack.DECK_13, false))
                deck.add(Card(Rank.KING, Suit.DIAMONDS, CardBack.DECK_13, false))
                deck.add(Card(Rank.KING, Suit.SPADES, CardBack.DECK_13, false))
                deck.add(Card(Rank.KING, Suit.HEARTS, CardBack.STANDARD, false))
                deck.add(Card(Rank.KING, Suit.CLUBS, CardBack.STANDARD, false))
                deck.add(Card(Rank.KING, Suit.DIAMONDS, CardBack.STANDARD, false))
                deck.add(Card(Rank.KING, Suit.SPADES, CardBack.STANDARD, false))
                deck.add(Card(Rank.KING, Suit.HEARTS, CardBack.ULTRA_LUXE, false))
                deck.add(Card(Rank.KING, Suit.CLUBS, CardBack.ULTRA_LUXE, false))
                deck.add(Card(Rank.KING, Suit.DIAMONDS, CardBack.ULTRA_LUXE, false))
                deck.add(Card(Rank.KING, Suit.SPADES, CardBack.ULTRA_LUXE, false))
                deck.add(Card(Rank.KING, Suit.CLUBS, CardBack.TOPS, true))
                deck.add(Card(Rank.KING, Suit.DIAMONDS, CardBack.GOMORRAH, true))
                deck.add(Card(Rank.KING, Suit.SPADES, CardBack.LUCKY_38, true))

                deck.add(Card(Rank.TEN, Suit.HEARTS, CardBack.DECK_13, false))
                deck.add(Card(Rank.TEN, Suit.DIAMONDS, CardBack.DECK_13, false))
                deck.add(Card(Rank.TEN, Suit.CLUBS, CardBack.DECK_13, false))
                deck.add(Card(Rank.TEN, Suit.SPADES, CardBack.DECK_13, false))

                deck.add(Card(Rank.SIX, Suit.HEARTS, CardBack.DECK_13, false))
                deck.add(Card(Rank.SIX, Suit.DIAMONDS, CardBack.DECK_13, false))
                deck.add(Card(Rank.SIX, Suit.CLUBS, CardBack.DECK_13, false))
                deck.add(Card(Rank.SIX, Suit.SPADES, CardBack.DECK_13, false))

                deck.add(Card(Rank.FIVE, Suit.HEARTS, CardBack.DECK_13, false))
                deck.add(Card(Rank.FIVE, Suit.DIAMONDS, CardBack.DECK_13, false))
                deck.add(Card(Rank.FIVE, Suit.CLUBS, CardBack.DECK_13, false))
                deck.add(Card(Rank.FIVE, Suit.SPADES, CardBack.DECK_13, false))

                deck.add(Card(Rank.ACE, Suit.HEARTS, CardBack.DECK_13, false))
                deck.add(Card(Rank.ACE, Suit.DIAMONDS, CardBack.DECK_13, false))
                deck.add(Card(Rank.ACE, Suit.CLUBS, CardBack.DECK_13, false))
                deck.add(Card(Rank.ACE, Suit.HEARTS, CardBack.WILD_WASTELAND, true))
            }
            return CResources(deck)
        } else {
            val deck = activity.save?.getCustomDeckCopy() ?: CustomDeck(CardBack.STANDARD, false)
            deck.apply {
                add(Card(Rank.ACE, Suit.HEARTS, CardBack.WILD_WASTELAND, true))
                add(Card(Rank.ACE, Suit.CLUBS, CardBack.WILD_WASTELAND, true))
                add(Card(Rank.ACE, Suit.DIAMONDS, CardBack.WILD_WASTELAND, true))
                add(Card(Rank.KING, Suit.HEARTS, CardBack.WILD_WASTELAND, false))
                add(Card(Rank.KING, Suit.CLUBS, CardBack.WILD_WASTELAND, false))
                add(Card(Rank.KING, Suit.DIAMONDS, CardBack.WILD_WASTELAND, false))
                add(Card(Rank.KING, Suit.SPADES, CardBack.WILD_WASTELAND, false))
                add(Card(Rank.JACK, Suit.HEARTS, CardBack.WILD_WASTELAND, false))
                add(Card(Rank.QUEEN, Suit.HEARTS, CardBack.WILD_WASTELAND, false))
            }
            return CResources(deck)
        }
    }

    if (showGameSnuffles) {
        StartGame(
            activity = activity,
            playerCResources = getPlayerDeck(isFinalBoss = false),
            isCustom = true,
            enemy = EnemySnuffles,
            showAlertDialog = showAlertDialog
        ) {
            showGameSnuffles = false
        }
        return
    } else if (showGamePriestess) {
        StartGame(
            activity = activity,
            playerCResources = getPlayerDeck(isFinalBoss = false),
            isCustom = true,
            enemy = EnemyPriestess,
            showAlertDialog = showAlertDialog
        ) {
            showGamePriestess = false
        }
        return
    } else if (showGameFinalBoss) {
        StartGame(
            activity = activity,
            playerCResources = getPlayerDeck(isFinalBoss = true),
            isCustom = true,
            enemy = EnemyFinalBoss().apply {
                playAlarm = {
                    repeat(3) {
                        playNoCardAlarm(activity)
                    }
                }
                sayThing = { dialogText = it }
            },
            showAlertDialog = showAlertDialog
        ) {
            nextSong(activity)
            isSoundEffectsReduced = false
            showGameFinalBoss = false
        }
        return
    } else if (showGameFinalBossWild) {
        StartGame(
            activity = activity,
            playerCResources = getPlayerDeck(isFinalBoss = true, isFinalBossWild = true),
            isCustom = true,
            enemy = EnemyFinalBoss().apply {
                playAlarm = {
                    repeat(3) {
                        playNoCardAlarm(activity)
                    }
                }
                sayThing = { dialogText = it }
            },
            showAlertDialog = showAlertDialog
        ) {
            nextSong(activity)
            isSoundEffectsReduced = false
            showGameFinalBossWild = false
        }
        return
    }

    MenuItemOpen(activity, stringResource(R.string.menu_wild_wastealnd), "<-", goBack) {
        val state = rememberLazyListState()
        LazyColumn(
            Modifier
                .fillMaxSize()
                .background(getBackgroundColor(activity))
                .scrollbar(
                    state,
                    knobColor = getKnobColor(activity), trackColor = getTrackColor(activity),
                    horizontal = false,
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            state = state
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                TextFallout(
                    stringResource(R.string.custom_deck_only),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    28.sp,
                    Alignment.Center,
                    Modifier.fillMaxWidth(0.7f),
                    TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))

                @Composable
                fun OpponentItem(name: String, onClick: () -> Unit) {
                    TextFallout(
                        name,
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        16.sp,
                        Alignment.Center,
                        Modifier
                            .clickable { onClick() }
                            .background(getTextBackgroundColor(activity))
                            .padding(4.dp),
                        TextAlign.Center
                    )
                }

                OpponentItem(stringResource(R.string.snuffles)) { playVatsEnter(activity); showGameSnuffles = true }
                Spacer(modifier = Modifier.height(10.dp))
                OpponentItem(stringResource(R.string.priest)) { playVatsEnter(activity); showGamePriestess = true }
                if ((activity.save?.storyChaptersProgress ?: 0) >= 9) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OpponentItem(stringResource(R.string.final_boss) + " BETA") { playVatsEnter(activity); showGameFinalBoss = true }
                    Spacer(modifier = Modifier.height(10.dp))
                    OpponentItem(stringResource(R.string.final_boss) + " BETA WILD") { playVatsEnter(activity); showGameFinalBossWild = true }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}