package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.core.ScrollArea
import com.composables.core.rememberScrollAreaState
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CardNumber
import com.unicorns.invisible.caravan.model.primitives.CardWithPrice
import com.unicorns.invisible.caravan.model.primitives.CollectibleDeck
import com.unicorns.invisible.caravan.model.primitives.RankNumber
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.MenuItemOpen
import com.unicorns.invisible.caravan.utils.ShowCard
import com.unicorns.invisible.caravan.utils.ShowCardBack
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableSelect
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getDividerColor
import com.unicorns.invisible.caravan.utils.getSelectionColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.playCloseSound
import com.unicorns.invisible.caravan.utils.playSelectSound


@Composable
fun SetCustomDeck(
    activity: MainActivity,
    goBack: () -> Unit,
) {
    fun isInCustomDeck(card: CardWithPrice) = card in save.getCurrentCustomDeck()
    fun isAvailable(card: CardWithPrice) = save.isCardAvailableAlready(card)

    fun toggleToCustomDeck(card: CardWithPrice) {
        save.getCurrentCustomDeck().let { deck ->
            if (card in deck) {
                deck.remove(card)
            } else {
                deck.add(card)
            }
        }
        saveData(activity)
    }

    val mainState = rememberLazyListState()
    var selectedDeck by rememberSaveable { mutableIntStateOf(save.activeCustomDeck) }
    var updateCharacteristics by remember { mutableStateOf(false) }
    var updateAll by remember { mutableStateOf(false) }

    fun getSelectedDeckIndex() = selectedDeck - 1
    fun selectDeck(deck: Int) {
        selectedDeck = deck
        save.activeCustomDeck = deck
        updateCharacteristics = !updateCharacteristics
        updateAll = !updateAll
        saveData(activity)
    }

    MenuItemOpen(activity, stringResource(R.string.deck_custom), "<-", goBack) {
        Column(Modifier
            .fillMaxSize()
            .background(getBackgroundColor(activity))) {
            TabRow(
                getSelectedDeckIndex(), Modifier.fillMaxWidth(),
                containerColor = getBackgroundColor(activity),
                indicator = { tabPositions ->
                    if (getSelectedDeckIndex() < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[getSelectedDeckIndex()]),
                            color = getSelectionColor(activity)
                        )
                    }
                },
                divider = {
                    HorizontalDivider(color = getDividerColor(activity))
                }
            ) {
                @Composable
                fun CustomDeckTab(d: Int) {
                    Tab(
                        selectedDeck == d, { playSelectSound(activity); selectDeck(d) },
                        selectedContentColor = getSelectionColor(activity),
                        unselectedContentColor = getTextBackgroundColor(activity)
                    ) {
                        TextFallout(
                            stringResource(when (d) {
                                1 -> R.string.custom_deck_1
                                2 -> R.string.custom_deck_2
                                3 -> R.string.custom_deck_3
                                4 -> R.string.custom_deck_4
                                else -> R.string.empty_string
                            }),
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            16.sp,
                            Modifier.padding(4.dp),
                        )
                    }
                }
                CustomDeckTab(1)
                CustomDeckTab(2)
                CustomDeckTab(3)
                CustomDeckTab(4)
            }

            key (updateCharacteristics) {
                ShowCharacteristics(activity)
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = getDividerColor(activity))
            Spacer(modifier = Modifier.height(12.dp))

            Column(Modifier.fillMaxSize()) {
                CardBack.entries.forEach { back ->
                    var updateInfo by remember { mutableStateOf(false) }
                    var updateCards by remember { mutableStateOf(false) }
                    key(updateAll, updateInfo) {
                        @Composable
                        fun Button(text: String, action: (CardWithPrice) -> Unit) {
                            TextFallout(
                                text,
                                getTextColor(activity),
                                getTextStrokeColor(activity),
                                18.sp,
                                Modifier
                                    .fillMaxWidth()
                                    .background(getTextBackgroundColor(activity))
                                    .clickableSelect(activity) {
                                        CollectibleDeck(back).toList()
                                            .filter { isAvailable(it) }
                                            .forEach(action)
                                        updateCharacteristics = !updateCharacteristics
                                        updateCards = !updateCards
                                    }
                                    .padding(vertical = 4.dp),
                                boxAlignment = Alignment.Center
                            )
                        }

                        val lazyRowState = rememberLazyListState()
                        val state = rememberScrollAreaState(lazyRowState)
                        ScrollArea(state, Modifier.fillMaxSize()) {
                            LazyRow(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically,
                                state = lazyRowState
                            ) {
                                item {
                                    Column(Modifier.padding(horizontal = 8.dp)) {
                                        val name = stringResource(back.nameIdWithBackFileName.first)
                                        TextFallout(
                                            name.replace(" (", "\n("),
                                            getTextColor(activity),
                                            getTextStrokeColor(activity),
                                            14.sp,
                                            Modifier.fillMaxWidth(),
                                            boxAlignment = Alignment.Center
                                        )
                                        ShowCardBack(
                                            activity,
                                            CardNumber(RankNumber.ACE, Suit.CLUBS, back),
                                            Modifier.align(Alignment.CenterHorizontally),
                                        )

                                        Button(activity.getString(R.string.select_all)) {
                                            if (!isInCustomDeck(it)) { toggleToCustomDeck(it) }
                                        }
                                        HorizontalDivider(color = getDividerColor(activity))
                                        Button(activity.getString(R.string.deselect_all)) {
                                            if (isInCustomDeck(it)) { toggleToCustomDeck(it) }
                                        }

                                        val state2 = rememberLazyListState()
                                        key(updateAll, updateCards) {
                                            CardsColumn(
                                                activity,
                                                back,
                                                state2,
                                                {
                                                    updateCharacteristics = !updateCharacteristics
                                                    updateCards = !updateCards
                                                },
                                                ::isInCustomDeck,
                                                ::isAvailable,
                                                ::toggleToCustomDeck
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CardsColumn(
    activity: MainActivity,
    back: CardBack,
    state2: LazyListState,
    updateKeys: () -> Unit,
    isInCustomDeck: (CardWithPrice) -> Boolean,
    isAvailable: (CardWithPrice) -> Boolean,
    toggleToCustomDeck: (CardWithPrice) -> Unit,
) {
    LazyColumn(Modifier, state = state2) lambda@{
        items(CollectibleDeck(back).toList().sortedWith { o1, o2 ->
            when (o1) {
                is CardNumber -> {
                    if (o2 !is CardNumber) {
                        1
                    } else {
                        if (o1.rank != o2.rank) {
                            o2.rank.value - o1.rank.value
                        } else {
                            o1.suit.ordinal - o2.suit.ordinal
                        }
                    }
                }
                is CardFaceSuited -> {
                    when (o2) {
                        is CardJoker -> {
                            1
                        }
                        is CardFaceSuited -> {
                            if (o1.rank != o2.rank) {
                                o2.rank.value - o1.rank.value
                            } else {
                                o1.suit.ordinal - o2.suit.ordinal
                            }
                        }
                        is CardNumber -> {
                            -1
                        }
                    }
                }
                is CardJoker -> {
                    if (o2 is CardJoker) {
                        o2.number.ordinal - o1.number.ordinal
                    } else {
                        -1
                    }
                }
            }
        }) { card ->
            var isSelected by remember { mutableStateOf(isInCustomDeck(card)) }
            if (isAvailable(card)) {
                ShowCard(activity, card as Card, Modifier
                    .clickable {
                        toggleToCustomDeck(card)
                        isSelected = !isSelected
                        if (isSelected) {
                            playSelectSound(activity)
                        } else {
                            playCloseSound(activity)
                        }
                        updateKeys()
                    }
                    .border(
                        width = (if (isSelected) 3 else 0).dp,
                        color = getSelectionColor(activity)
                    )
                    .padding(4.dp)
                    .alpha(if (isSelected) 1f else 0.55f))
            } else {
                ShowCardBack(activity, card as Card, Modifier
                    .padding(4.dp)
                    .alpha(0.33f))
            }
        }
    }
}

@Composable
fun ShowCharacteristics(activity: MainActivity) {
    Column(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val deck = save.getCurrentDeckCopy()
        Row(
            Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val deckSizeMin = CResources.MIN_DECK_SIZE
            val color1 = if (deck.size < deckSizeMin) Color.Red else getTextColor(activity)
            val color2 = if (deck.size < deckSizeMin) Color.Red else getTextStrokeColor(activity)
            TextFallout(
                text = stringResource(R.string.custom_deck_size, deck.size, deckSizeMin),
                color1,
                color2,
                14.sp,
                Modifier.fillMaxWidth(0.5f),
            )
            val nonFaces = deck.toList().count { it is CardNumber }
            val nonFacesMin = CResources.MIN_NUM_OF_NUMBERS
            val color3 = if (nonFaces < nonFacesMin) Color.Red else getTextColor(activity)
            val color4 = if (nonFaces < nonFacesMin) Color.Red else getTextStrokeColor(activity)
            TextFallout(
                text = stringResource(R.string.custom_deck_non_faces, nonFaces, nonFacesMin),
                color3,
                color4,
                14.sp,
                Modifier.fillMaxWidth(),
            )
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            val decksUsed = deck.toList().distinctBy { it.getBack() }.size
            val decksUsedMax = CResources.MAX_NUMBER_OF_DECKS
            val color1 = if (decksUsed > decksUsedMax) Color.Red else getTextColor(activity)
            val color2 = if (decksUsed > decksUsedMax) Color.Red else getTextStrokeColor(activity)
            TextFallout(
                text = stringResource(R.string.custom_deck_num_of_decks, decksUsed, decksUsedMax),
                color1,
                color2,
                14.sp,
                Modifier.fillMaxWidth(0.5f),
            )
        }
    }
}