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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.trading.Trader
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.MenuItemOpen
import com.unicorns.invisible.caravan.utils.ShowCard
import com.unicorns.invisible.caravan.utils.ShowCardBack
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getDividerColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getSelectionColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.playCashSound
import com.unicorns.invisible.caravan.utils.playNoBeep
import com.unicorns.invisible.caravan.utils.playSelectSound
import com.unicorns.invisible.caravan.utils.scrollbar


@Composable
fun CardToBuy(activity: MainActivity, card: Card, price: Int, update: () -> Unit) {
    if (card.back.deckName == null) {
        return
    }
    val suit = if (card.rank == Rank.JOKER)
        (card.suit.ordinal + 1).toString()
    else
        stringResource(card.suit.nameId)
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(2f).wrapContentHeight()) {
            Row(Modifier.fillMaxWidth().wrapContentHeight().padding(4.dp), horizontalArrangement = Arrangement.Center) {
                ShowCard(activity, card, Modifier)
                Spacer(Modifier.width(4.dp))
                ShowCardBack(activity, card, Modifier)
            }

            TextFallout(
                "${stringResource(card.rank.nameId)} $suit\n" +
                        "(${stringResource(card.back.deckName)}${if (card.isAlt) " ALT!" else ""})",
                getTextColor(activity),
                getTextStrokeColor(activity),
                16.sp,
                Modifier.fillMaxWidth().wrapContentHeight().padding(4.dp),
                textAlignment = TextAlign.Center
            )
        }

        TextFallout(
            stringResource(R.string.buy_for_caps, price),
            getTextColor(activity),
            getTextStrokeColor(activity),
            16.sp,
            Modifier
                .weight(1f)
                .padding(4.dp)
                .background(getTextBackgroundColor(activity))
                .clickable {
                    if (save.capsInHand < price) {
                        playNoBeep(activity)
                    } else {
                        save.capsInHand -= price
                        save.addCard(card)
                        playCashSound(activity)
                        saveData(activity)
                        update()
                    }
                },
            boxAlignment = Alignment.Center
        )
    }
}

@Composable
fun ShowTraders(activity: MainActivity, goBack: () -> Unit) {
    MenuItemOpen(activity, stringResource(R.string.market), "<-", goBack) {
        Spacer(Modifier.height(8.dp))

        var selectedTab by rememberSaveable { mutableIntStateOf(0) }
        var update by remember { mutableStateOf(false) }
        val state = rememberLazyListState()
        key(update) {
            Column(
                Modifier,
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextFallout(
                    stringResource(R.string.your_caps_in_hand, save.capsInHand),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    16.sp,
                    Modifier.padding(4.dp),
                )

                TabRow(
                    selectedTab, Modifier.fillMaxWidth(),
                    containerColor = getBackgroundColor(activity),
                    indicator = { tabPositions ->
                        if (selectedTab < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = getSelectionColor(activity)
                            )
                        }
                    },
                    divider = {
                        HorizontalDivider(color = getDividerColor(activity))
                    }
                ) {
                    @Composable
                    fun TraderTab(tabNumber: Int, trader: Trader) {
                        Tab(selectedTab == tabNumber, { selectedTab = tabNumber; playSelectSound(activity) },
                            selectedContentColor = getSelectionColor(activity),
                            unselectedContentColor = getTextBackgroundColor(activity)
                        ) {
                            TextFallout(
                                trader.getSymbol(),
                                getTextColor(activity),
                                getTextStrokeColor(activity),
                                12.sp,
                                Modifier.padding(4.dp),
                            )
                        }
                    }
                    save.traders.forEachIndexed { index, t ->
                        TraderTab(index, t)
                    }
                }

                val selectedTrader = save.traders[selectedTab]
                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .background(getBackgroundColor(activity))
                        .scrollbar(
                            state,
                            horizontal = false,
                            knobColor = getKnobColor(activity),
                            trackColor = getTrackColor(activity),
                        ), state = state
                ) {
                    item {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {
                            if (selectedTrader.isOpen()) {
                                val cards = selectedTrader.getCards()
                                    .filter { !save.isCardAvailableAlready(it) }

                                TextFallout(
                                    if (cards.isEmpty()) {
                                        stringResource(selectedTrader.getEmptyStoreMessage())
                                    } else {
                                        stringResource(selectedTrader.getWelcomeMessage())
                                    },
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    18.sp,
                                    Modifier.fillMaxWidth().padding(4.dp),
                                )
                                cards.forEach {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    HorizontalDivider(thickness = 1.dp, color = getDividerColor(activity))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    CardToBuy(activity, it, it.getPriceOfCard()) {
                                        update = !update
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                HorizontalDivider(thickness = 1.dp, color = getDividerColor(activity))
                                Spacer(modifier = Modifier.height(4.dp))
                            } else {
                                TextFallout(
                                    selectedTrader.openingCondition(activity),
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    18.sp,
                                    Modifier.fillMaxWidth().padding(4.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}