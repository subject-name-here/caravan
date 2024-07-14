package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.enemy.EnemyEasy
import com.unicorns.invisible.caravan.model.enemy.EnemyFinalBoss
import com.unicorns.invisible.caravan.model.enemy.EnemyMedium
import com.unicorns.invisible.caravan.model.enemy.EnemyPriestess
import com.unicorns.invisible.caravan.model.enemy.EnemySnuffles
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableCancel
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.getTrackColor
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

    fun getPlayerDeck(): CResources {
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

    if (showGameSnuffles) {
        StartGame(
            activity = activity,
            playerCResources = getPlayerDeck(),
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
            playerCResources = getPlayerDeck(),
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
            playerCResources = getPlayerDeck(),
            isCustom = true,
            enemy = EnemyFinalBoss().apply { playAlarm = { playNoCardAlarm(activity) } },
            showAlertDialog = showAlertDialog
        ) {
            showGameFinalBoss = false
        }
        return
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(getBackgroundColor(activity)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val state = rememberLazyListState()
        Row(
            modifier = Modifier
                .height(48.dp)
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextFallout(
                stringResource(R.string.custom_deck_only),
                getTextColor(activity),
                getTextStrokeColor(activity),
                28.sp,
                Alignment.Center,
                Modifier.fillMaxWidth(0.7f),
                TextAlign.Center
            )
        }
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
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
                    stringResource(R.string.pve_select_enemy),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    22.sp,
                    Alignment.Center,
                    Modifier,
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
                Spacer(modifier = Modifier.height(10.dp))
                OpponentItem(stringResource(R.string.final_boss)) { playVatsEnter(activity); showGameFinalBoss = true }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        TextFallout(
            stringResource(R.string.menu_back),
            getTextColor(activity),
            getTextStrokeColor(activity),
            24.sp,
            Alignment.Center,
            modifier = Modifier
                .clickableCancel(activity) { goBack() }
                .background(getTextBackgroundColor(activity))
                .padding(8.dp),
            TextAlign.Center
        )
    }
}