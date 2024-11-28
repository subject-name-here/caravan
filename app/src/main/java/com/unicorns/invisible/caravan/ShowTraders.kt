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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.trading.Trader
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.MenuItemOpen
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableOk
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getDividerColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getSelectionColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.scrollbar


@Composable
fun ShowTraders(activity: MainActivity, goBack: () -> Unit) {
    MenuItemOpen(activity, "Traders", "<-", goBack) {
        val state = rememberLazyListState()
        LazyColumn(
            Modifier
                .fillMaxSize()
                .background(getBackgroundColor(activity))
                .scrollbar(
                    state,
                    horizontal = false,
                    knobColor = getKnobColor(activity),
                    trackColor = getTrackColor(activity),
                    padding = 4.dp
                ), state = state
        ) {
            item {
                Spacer(Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
                    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
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
                        fun TraderTab(trader: Trader, tabNumber: Int) {
                            Tab(selectedTab == tabNumber, { selectedTab = tabNumber },
                                selectedContentColor = getSelectionColor(activity),
                                unselectedContentColor = getTextBackgroundColor(activity)
                            ) {
                                TextFallout(
                                    stringResource(trader.getName()),
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    12.sp,
                                    Alignment.Center,
                                    Modifier.padding(4.dp),
                                    TextAlign.Center
                                )
                            }
                        }
                        save.traders.forEachIndexed { index, t ->
                            TraderTab(t, index)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    val selectedTrader = save.traders[selectedTab]

                    @Composable
                    fun CardToBuy(card: Card, price: Int) {
                        Row(Modifier.fillMaxWidth()) {
                            TextFallout(
                                stringResource(card.rank.nameId) + " " +
                                        stringResource(card.suit.nameId) + "\n(" +
                                        stringResource(card.back.getDeckName()) +
                                        if (card.isAlt) " ALT!)" else ")",
                                getTextColor(activity),
                                getTextStrokeColor(activity),
                                16.sp,
                                Alignment.Center,
                                Modifier.weight(1f).padding(4.dp),
                                TextAlign.Center
                            )
                            if (save.availableCards.none { c ->
                                c.rank == card.rank && c.suit == card.suit && c.back == card.back && c.isAlt == card.isAlt
                            }) {
                                TextFallout(
                                    "Buy for $price caps",
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    16.sp,
                                    Alignment.Center,
                                    Modifier.weight(1f)
                                        .padding(4.dp)
                                        .background(getTextBackgroundColor(activity))
                                        .clickableOk(activity) {
                                            // TODO: check if enough money
                                            save.capsInHand -= price
                                            save.availableCards.add(card)
                                            save.onCardBuying(activity)
                                            saveData(activity)
                                        },
                                    TextAlign.Center
                                )
                            } else {
                                TextFallout(
                                    "You already have this card!",
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    14.sp,
                                    Alignment.Center,
                                    Modifier.weight(1f).padding(4.dp),
                                    TextAlign.Center
                                )
                            }
                        }
                    }
                    if (selectedTrader.isOpen()) {
                        selectedTrader.getCards().forEach {
                            Spacer(modifier = Modifier.height(4.dp))
                            CardToBuy(it.first, it.second)
                        }
                    } else {
                        TextFallout(
                            stringResource(selectedTrader.openingCondition()),
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            18.sp,
                            Alignment.Center,
                            Modifier.fillMaxWidth().padding(4.dp),
                            TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}