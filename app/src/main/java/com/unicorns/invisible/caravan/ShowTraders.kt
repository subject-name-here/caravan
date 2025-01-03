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
import com.unicorns.invisible.caravan.model.trading.Lucky38Trader
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.MenuItemOpen
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
import com.unicorns.invisible.caravan.utils.playPimpBoySound
import com.unicorns.invisible.caravan.utils.playSelectSound
import com.unicorns.invisible.caravan.utils.scrollbar


@Composable
fun ShowTraders(activity: MainActivity, goBack: () -> Unit) {
    MenuItemOpen(activity, stringResource(R.string.market), "<-", goBack) {
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
                ), state = state
        ) {
            item {
                Spacer(Modifier.height(8.dp))

                var selectedTab by rememberSaveable { mutableIntStateOf(0) }
                var update by remember { mutableStateOf(false) }
                key(update) {
                    Column(Modifier.padding(horizontal = 4.dp), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            TextFallout(
                                stringResource(R.string.your_barter_stat, save.barterStat),
                                getTextColor(activity),
                                getTextStrokeColor(activity),
                                16.sp,
                                Alignment.Center,
                                Modifier.padding(4.dp),
                                TextAlign.Center
                            )

                            TextFallout(
                                stringResource(R.string.your_caps_in_hand, save.capsInHand),
                                getTextColor(activity),
                                getTextStrokeColor(activity),
                                16.sp,
                                Alignment.Center,
                                Modifier.padding(4.dp),
                                TextAlign.Center
                            )
                        }

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
                            fun TraderTab(tabNumber: Int) {
                                Tab(selectedTab == tabNumber, { selectedTab = tabNumber; playSelectSound(activity) },
                                    selectedContentColor = getSelectionColor(activity),
                                    unselectedContentColor = getTextBackgroundColor(activity)
                                ) {
                                    TextFallout(
                                        when (tabNumber) {
                                            0 -> "UL"
                                            1 -> "T"
                                            2 -> "G"
                                            3 -> "38"
                                            4 -> "21"
                                            5 -> "SM"
                                            6 -> "E"
                                            7 -> "•"
                                            else -> "?"
                                        },
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
                                TraderTab(index)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        val selectedTrader = save.traders[selectedTab]

                        @Composable
                        fun CardToBuy(card: Card, price: Int) {
                            val suit = if (card.rank != Rank.JOKER)
                                stringResource(card.suit.nameId)
                            else
                                (card.suit.ordinal + 1).toString()
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                TextFallout(
                                    stringResource(card.rank.nameId) + " " + suit +
                                            "\n(" + stringResource(card.back.getDeckName()) +
                                            if (card.isAlt) " ALT!)" else ")",
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    16.sp,
                                    Alignment.Center,
                                    Modifier
                                        .weight(1f)
                                        .padding(4.dp),
                                    TextAlign.Center
                                )
                                TextFallout(
                                    stringResource(R.string.buy_for_caps, price),
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    16.sp,
                                    Alignment.Center,
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
                                                save.onCardBuying(activity)
                                                playCashSound(activity)
                                                saveData(activity)
                                                update = !update
                                            }
                                        },
                                    TextAlign.Center
                                )
                            }
                        }
                        if (selectedTrader.isOpen()) {
                            Column(
                                Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Top
                            ) {
                                TextFallout(
                                    stringResource(
                                        if (selectedTrader is Lucky38Trader)
                                            R.string.hi_our_name_is
                                        else
                                            R.string.hi_my_name_is,
                                        stringResource(selectedTrader.getName())
                                    ),
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    18.sp,
                                    Alignment.Center,
                                    Modifier.padding(4.dp),
                                    TextAlign.Center
                                )

                                selectedTrader.getStyles().filter { it !in save.ownedStyles }.forEach {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        TextFallout(
                                            stringResource(
                                                R.string.style_buy,
                                                stringResource(it.styleNameId)
                                            ),
                                            getTextColor(activity),
                                            getTextStrokeColor(activity),
                                            16.sp,
                                            Alignment.Center,
                                            Modifier
                                                .weight(1f)
                                                .padding(4.dp),
                                            TextAlign.Center
                                        )
                                        TextFallout(
                                            stringResource(R.string.buy_for_caps, it.price),
                                            getTextColor(activity),
                                            getTextStrokeColor(activity),
                                            16.sp,
                                            Alignment.Center,
                                            Modifier
                                                .weight(1f)
                                                .padding(4.dp)
                                                .background(getTextBackgroundColor(activity))
                                                .clickable {
                                                    if (save.capsInHand < it.price) {
                                                        playNoBeep(activity)
                                                    } else {
                                                        save.capsInHand -= it.price
                                                        save.ownedStyles.add(it)
                                                        playPimpBoySound(activity)
                                                        saveData(activity)
                                                        update = !update
                                                    }
                                                },
                                            TextAlign.Center
                                        )
                                    }
                                }

                                val cards = selectedTrader.getCards()
                                    .filter { !save.isCardAvailableAlready(it.first) }
                                if (cards.isEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    TextFallout(
                                        "Sorry, no new cards!!",
                                        getTextColor(activity),
                                        getTextStrokeColor(activity),
                                        18.sp,
                                        Alignment.Center,
                                        Modifier.padding(4.dp),
                                        TextAlign.Center
                                    )

                                    // TODO: INVESTMENT!!!!????
                                } else {
                                    cards.forEach {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        CardToBuy(it.first, it.second)
                                    }
                                }
                            }
                        } else {
                            TextFallout(
                                selectedTrader.openingCondition(activity),
                                getTextColor(activity),
                                getTextStrokeColor(activity),
                                18.sp,
                                Alignment.Center,
                                Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}