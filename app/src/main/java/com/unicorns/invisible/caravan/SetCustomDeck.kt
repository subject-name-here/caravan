package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.CheckboxCustom
import com.unicorns.invisible.caravan.utils.MenuItemOpen
import com.unicorns.invisible.caravan.utils.ShowCard
import com.unicorns.invisible.caravan.utils.ShowCardBack
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableCancel
import com.unicorns.invisible.caravan.utils.clickableSelect
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getDividerColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getSelectionColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.playClickSound
import com.unicorns.invisible.caravan.utils.playCloseSound
import com.unicorns.invisible.caravan.utils.playSelectSound
import com.unicorns.invisible.caravan.utils.scrollbar


@Composable
fun SetCustomDeck(
    activity: MainActivity,
    goBack: () -> Unit,
) {
    fun isInCustomDeck(card: Card): Boolean {
        return save.customDeck.let { deck -> card in deck }
    }

    fun toggleToCustomDeck(card: Card) {
        save.customDeck.let { deck ->
            if (card in deck) {
                deck.removeAll(listOf(card))
            } else {
                deck.add(card)
            }
        }
        saveData(activity)
    }

    fun isAvailable(card: Card): Boolean {
        return save.availableCards
            .any { it.rank == card.rank && it.suit == card.suit && it.back == card.back && it.isAlt == card.isAlt }
    }

    val mainState = rememberLazyListState()
    var updater by remember { mutableStateOf(false) }
    MenuItemOpen(activity, stringResource(R.string.deck_custom), "<-", goBack) {
        Column(Modifier.fillMaxSize().background(getBackgroundColor(activity))) {
            key (updater) {
                ShowCharacteristics(activity)
            }

            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .scrollbar(
                        mainState,
                        horizontal = false,
                        knobColor = getKnobColor(activity),
                        trackColor = getTrackColor(activity)
                    ),
                mainState
            ) {
                item {
                    CardBack.entries.filter { it in save.ownedDecks }.forEach { back ->
                        var check by rememberSaveable { mutableStateOf(save.altDecksChosen[back] == true) }
                        Row(Modifier.fillMaxWidth().padding(4.dp), horizontalArrangement = Arrangement.Start) {
                            Column(
                                Modifier
                                    .padding(horizontal = 8.dp)
                                    .fillMaxWidth(0.33f)
                            ) {
                                TextFallout(
                                    stringResource(back.getDeckName()),
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    12.sp,
                                    Alignment.Center,
                                    Modifier.fillMaxWidth(),
                                    TextAlign.Center
                                )
                                ShowCardBack(
                                    activity,
                                    Card(Rank.ACE, Suit.CLUBS, back, check),
                                    Modifier.align(Alignment.CenterHorizontally),
                                )
                            }
                            Column(
                                Modifier
                                    .padding(horizontal = 8.dp)
                                    .fillMaxWidth(0.5f)
                            ) {
                                TextFallout(
                                    "Select all",
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    18.sp,
                                    Alignment.Center,
                                    Modifier.fillMaxWidth()
                                        .background(getTextBackgroundColor(activity))
                                        .clickableSelect(activity) {
                                            updater = !updater
                                            CustomDeck(back, check).toList()
                                                .filter { isAvailable(it) }
                                                .forEach {
                                                    if (!isInCustomDeck(it)) {
                                                        toggleToCustomDeck(it)
                                                    }
                                                }
                                        },
                                    TextAlign.Center
                                )
                                HorizontalDivider(color = getDividerColor(activity))
                                TextFallout(
                                    "Deselect all",
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    18.sp,
                                    Alignment.Center,
                                    Modifier.fillMaxWidth()
                                        .background(getTextBackgroundColor(activity))
                                        .clickableCancel(activity) {
                                            updater = !updater
                                            CustomDeck(back, check).toList()
                                                .filter { isAvailable(it) }
                                                .forEach {
                                                    if (isInCustomDeck(it)) {
                                                        toggleToCustomDeck(it)
                                                    }
                                                }
                                        },
                                    TextAlign.Center
                                )
                            }
                            Column(
                                Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (back.hasAltPlayable()) {
                                    TextFallout(
                                        text = "ALT!",
                                        getTextColor(activity),
                                        getTextStrokeColor(activity),
                                        12.sp,
                                        Alignment.Center,
                                        modifier = Modifier.wrapContentHeight(),
                                        TextAlign.Center
                                    )
                                    CheckboxCustom(
                                        activity,
                                        { check },
                                        {
                                            check = !check
                                            save.altDecksChosen[back] = check
                                            saveData(activity)
                                            if (check) {
                                                playClickSound(activity)
                                            } else {
                                                playCloseSound(activity)
                                            }
                                            updater = !updater
                                        }
                                    ) { back in save.ownedDecksAlt }
                                }
                            }
                        }
                        val state = rememberLazyListState()
                        key(check) {
                            LazyRow(
                                Modifier
                                    .scrollbar(
                                        state,
                                        knobColor = getKnobColor(activity),
                                        trackColor = getTrackColor(activity),
                                        horizontal = true
                                    )
                                    .padding(horizontal = 4.dp)
                                    .padding(bottom = 4.dp),
                                state = state
                            ) lambda@{
                                items(CustomDeck(back, check).toList().sortedWith { o1, o2 ->
                                    if (o1.rank != o2.rank) {
                                        o2.rank.value - o1.rank.value
                                    } else {
                                        o1.suit.ordinal - o2.suit.ordinal
                                    }
                                }) { card ->
                                    var isSelected by remember {
                                        mutableStateOf(isInCustomDeck(card))
                                    }

                                    if (isAvailable(card)) {
                                        ShowCard(activity, card, Modifier
                                            .clickable {
                                                toggleToCustomDeck(card)
                                                isSelected = !isSelected
                                                if (isSelected) {
                                                    playSelectSound(activity)
                                                } else {
                                                    playCloseSound(activity)
                                                }
                                                updater = !updater
                                            }
                                            .border(
                                                width = (if (isSelected) 3 else 0).dp,
                                                color = getSelectionColor(activity)
                                            )
                                            .padding(4.dp)
                                            .alpha(if (isSelected) 1f else 0.5f))
                                    } else {
                                        ShowCardBack(
                                            activity, card, Modifier
                                                .padding(4.dp)
                                                .alpha(0.33f)
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

@Composable
fun ShowCharacteristics(activity: MainActivity) {
    // TODO: decks used (MAX IS 6!!!)
    Row(
        Modifier
            .padding(6.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val deck = save.getCustomDeckCopy()
        val deckSizeMin = CResources.MIN_DECK_SIZE
        val color1 = if (deck.size < deckSizeMin) Color.Red else getTextColor(activity)
        val color2 = if (deck.size < deckSizeMin) Color.Red else getTextStrokeColor(activity)
        TextFallout(
            text = stringResource(R.string.custom_deck_size, deck.size, deckSizeMin),
            color1,
            color2,
            14.sp,
            Alignment.Center,
            Modifier.fillMaxWidth(0.5f),
            TextAlign.Center
        )
        val nonFaces = deck.count { !it.isFace() }
        val nonFacesMin = CResources.MIN_NUM_OF_NUMBERS
        val color3 = if (nonFaces < nonFacesMin) Color.Red else getTextColor(activity)
        val color4 = if (nonFaces < nonFacesMin) Color.Red else getTextStrokeColor(activity)
        TextFallout(
            text = stringResource(R.string.custom_deck_non_faces, nonFaces, nonFacesMin),
            color3,
            color4,
            14.sp,
            Alignment.Center,
            Modifier.fillMaxWidth(),
            TextAlign.Center,
        )
    }
}