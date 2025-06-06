package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.buy_chips
import caravan.composeapp.generated.resources.buy_for_caps
import caravan.composeapp.generated.resources.buy_for_chips
import caravan.composeapp.generated.resources.buy_ticket
import caravan.composeapp.generated.resources.empty_string
import caravan.composeapp.generated.resources.market
import caravan.composeapp.generated.resources.your_caps_in_hand
import caravan.composeapp.generated.resources.your_chips_in_hand
import com.unicorns.invisible.caravan.model.Currency
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CardNumber
import com.unicorns.invisible.caravan.model.primitives.CardWithPrice
import com.unicorns.invisible.caravan.model.trading.ChineseTrader
import com.unicorns.invisible.caravan.model.trading.CommonTrader
import com.unicorns.invisible.caravan.model.trading.EnclaveTrader
import com.unicorns.invisible.caravan.model.trading.GomorrahTrader
import com.unicorns.invisible.caravan.model.trading.Lucky38Trader
import com.unicorns.invisible.caravan.model.trading.SierraMadreTrader
import com.unicorns.invisible.caravan.model.trading.TopsTrader
import com.unicorns.invisible.caravan.model.trading.Trader
import com.unicorns.invisible.caravan.model.trading.UltraLuxeTrader
import com.unicorns.invisible.caravan.model.trading.Vault21Trader
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.MenuItemOpenNoScroll
import com.unicorns.invisible.caravan.utils.ShowCard
import com.unicorns.invisible.caravan.utils.ShowCardBack
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.VertScrollbar
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getDividerColor
import com.unicorns.invisible.caravan.utils.getSelectionColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.playCashSound
import com.unicorns.invisible.caravan.utils.playNoBeep
import com.unicorns.invisible.caravan.utils.playSelectSound
import org.jetbrains.compose.resources.stringResource


@Composable
fun CardToBuy(card: CardWithPrice, price: Int, update: () -> Unit) {
    val rankToSuit = when (card) {
        is CardJoker -> stringResource(card.rank.nameId) to card.number.n.toString()
        is CardNumber -> stringResource(card.rank.nameId) to stringResource(card.suit.nameId)
        is CardFaceSuited -> stringResource(card.rank.nameId) to stringResource(card.suit.nameId)
    }
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(2f).wrapContentHeight()) {
            Row(Modifier.fillMaxWidth().wrapContentHeight().padding(4.dp), horizontalArrangement = Arrangement.Center) {
                ShowCard(card, Modifier)
                Spacer(Modifier.width(4.dp))
                ShowCardBack(card, Modifier)
            }

            val backName = card.getBack().nameIdWithBackFileName.first

            TextFallout(
                "${rankToSuit.first} ${rankToSuit.second}\n" + stringResource(backName),
                getTextColor(),
                getTextStrokeColor(),
                16.sp,
                Modifier.fillMaxWidth().wrapContentHeight().padding(4.dp),
                textAlignment = TextAlign.Center
            )
        }

        TextFallout(
            when (card.getBack().currency) {
                Currency.CAPS -> stringResource(Res.string.buy_for_caps, price)
                Currency.SIERRA_MADRE_CHIPS -> stringResource(Res.string.buy_for_chips, price)
                Currency.NOT_FOR_SALE -> stringResource(Res.string.empty_string)
            },
            getTextColor(),
            getTextStrokeColor(),
            16.sp,
            Modifier
                .weight(1f)
                .padding(4.dp)
                .background(getTextBackgroundColor())
                .clickable {
                    val cash = if (card.getBack().currency == Currency.SIERRA_MADRE_CHIPS) {
                        saveGlobal.sierraMadreChips
                    } else {
                        saveGlobal.capsInHand
                    }
                    if (cash < price) {
                        playNoBeep()
                    } else {
                        if (card.getBack().currency == Currency.SIERRA_MADRE_CHIPS) {
                            saveGlobal.sierraMadreChips -= price
                            saveGlobal.chipsWasted += price
                        } else {
                            saveGlobal.capsInHand -= price
                            saveGlobal.capsWasted += price
                        }
                        saveGlobal.addCard(card)
                        playCashSound()
                        saveData()
                        update()
                    }
                },
            boxAlignment = Alignment.Center
        )
    }
}



@Composable
fun TicketToBuy(price: Int, update: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(2f).wrapContentHeight()) {
            TextFallout(
                stringResource(Res.string.buy_ticket),
                getTextColor(),
                getTextStrokeColor(),
                16.sp,
                Modifier.fillMaxWidth().wrapContentHeight().padding(4.dp),
                textAlignment = TextAlign.Center
            )
        }

        TextFallout(
            stringResource(Res.string.buy_for_chips, price),
            getTextColor(),
            getTextStrokeColor(),
            16.sp,
            Modifier
                .weight(1f)
                .padding(4.dp)
                .background(getTextBackgroundColor())
                .clickable {
                    val cash = saveGlobal.sierraMadreChips
                    if (cash < price) {
                        playNoBeep()
                    } else {
                        saveGlobal.sierraMadreChips -= price
                        saveGlobal.chipsWasted += price
                        saveGlobal.tickets++
                        playCashSound()
                        saveData()
                        update()
                    }
                },
            boxAlignment = Alignment.Center
        )
    }
}

@Composable
fun ChipsToBuy(price: Int, update: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(2f).wrapContentHeight()) {
            TextFallout(
                stringResource(Res.string.buy_chips),
                getTextColor(),
                getTextStrokeColor(),
                16.sp,
                Modifier.fillMaxWidth().wrapContentHeight().padding(4.dp),
                textAlignment = TextAlign.Center
            )
        }

        TextFallout(
            stringResource(Res.string.buy_for_caps, price),
            getTextColor(),
            getTextStrokeColor(),
            16.sp,
            Modifier
                .weight(1f)
                .padding(4.dp)
                .background(getTextBackgroundColor())
                .clickable {
                    val cash = saveGlobal.capsInHand
                    if (cash < price) {
                        playNoBeep()
                    } else {
                        saveGlobal.capsInHand -= price
                        saveGlobal.capsWasted += price
                        saveGlobal.sierraMadreChips += 10
                        playCashSound()
                        saveData()
                        update()
                    }
                },
            boxAlignment = Alignment.Center
        )
    }
}


@Composable
fun ShowTraders(goBack: () -> Unit) {
    val traders = listOf<Trader>(
        CommonTrader,
        UltraLuxeTrader,
        TopsTrader,
        GomorrahTrader,
        Lucky38Trader,
        Vault21Trader,
        SierraMadreTrader,
        EnclaveTrader,
        ChineseTrader,
    )

    MenuItemOpenNoScroll(stringResource(Res.string.market), "<-", goBack) {
        Spacer(Modifier.height(8.dp))

        var selectedTab by rememberSaveable { mutableIntStateOf(0) }
        var update by remember { mutableStateOf(false) }
        key(selectedTab) {
            val state = rememberScrollState()

            key(update) {
                Column(
                    Modifier,
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextFallout(
                        stringResource(Res.string.your_caps_in_hand, saveGlobal.capsInHand),
                        getTextColor(),
                        getTextStrokeColor(),
                        16.sp,
                        Modifier.padding(4.dp),
                    )
                    TextFallout(
                        stringResource(Res.string.your_chips_in_hand, saveGlobal.sierraMadreChips),
                        getTextColor(),
                        getTextStrokeColor(),
                        16.sp,
                        Modifier.padding(4.dp),
                    )

                    TabRow(
                        selectedTab, Modifier.fillMaxWidth(),
                        containerColor = getBackgroundColor(),
                        indicator = { tabPositions ->
                            if (selectedTab < tabPositions.size) {
                                TabRowDefaults.SecondaryIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = getSelectionColor()
                                )
                            }
                        },
                        divider = {
                            HorizontalDivider(color = getDividerColor())
                        }
                    ) {
                        @Composable
                        fun TraderTab(tabNumber: Int, trader: Trader) {
                            Tab(selectedTab == tabNumber, { selectedTab = tabNumber; playSelectSound() },
                                selectedContentColor = getSelectionColor(),
                                unselectedContentColor = getTextBackgroundColor()
                            ) {
                                TextFallout(
                                    trader.getSymbol(),
                                    getTextColor(),
                                    getTextStrokeColor(),
                                    12.sp,
                                    Modifier.padding(4.dp),
                                )
                            }
                        }
                        traders.forEachIndexed { index, t ->
                            TraderTab(index, t)
                        }
                    }

                    val selectedTrader = traders[selectedTab]
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        if (selectedTrader.isOpen()) {
                            val cards = selectedTrader.getCards()
                                .filter { !saveGlobal.isCardAvailableAlready(it) }

                            TextFallout(
                                if (cards.isEmpty() && selectedTrader !is CommonTrader) {
                                    stringResource(selectedTrader.getEmptyStoreMessage())
                                } else {
                                    stringResource(selectedTrader.getWelcomeMessage())
                                },
                                getTextColor(),
                                getTextStrokeColor(),
                                18.sp,
                                Modifier.fillMaxWidth().padding(4.dp),
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            HorizontalDivider(thickness = 1.dp, color = getDividerColor())

                            BoxWithConstraints(Modifier.fillMaxSize()) {
                                Column(
                                    modifier = Modifier.fillMaxSize().verticalScroll(state = state).padding(end = 4.dp),
                                    verticalArrangement = Arrangement.Top
                                ) {
                                    cards.forEach {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        CardToBuy(it, it.getPriceOfCard()) {
                                            update = !update
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        HorizontalDivider(thickness = 1.dp, color = getDividerColor())
                                    }

                                    if (selectedTrader is CommonTrader) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        TicketToBuy(50) {
                                            update = !update
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        HorizontalDivider(thickness = 1.dp, color = getDividerColor())
                                        Spacer(modifier = Modifier.height(4.dp))
                                        ChipsToBuy(40) {
                                            update = !update
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        HorizontalDivider(thickness = 1.dp, color = getDividerColor())
                                    }
                                }

                                VertScrollbar(state)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        } else {
                            val cond by produceState("") {
                                value = selectedTrader.openingCondition()
                            }
                            TextFallout(
                                cond,
                                getTextColor(),
                                getTextStrokeColor(),
                                18.sp,
                                Modifier.fillMaxWidth().padding(4.dp),
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}