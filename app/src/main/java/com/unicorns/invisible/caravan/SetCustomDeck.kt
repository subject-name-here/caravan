package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.save.saveOnGD
import com.unicorns.invisible.caravan.utils.CheckboxCustom
import com.unicorns.invisible.caravan.utils.ShowCard
import com.unicorns.invisible.caravan.utils.ShowCardBack
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableCancel
import com.unicorns.invisible.caravan.utils.getBackgroundColor
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
        return activity.save?.customDeck?.let { deck ->
            card in deck
        } ?: false
    }

    fun toggleToCustomDeck(card: Card) {
        activity.save?.customDeck?.let { deck ->
            if (card in deck) {
                deck.remove(card)
            } else {
                deck.add(card)
            }
        }
        saveOnGD(activity)

    }

    fun isAvailable(card: Card): Boolean {
        return activity.save?.availableCards?.let { cards ->
            cards.any { it.rank == card.rank && it.suit == card.suit && it.back == card.back && it.isAlt == card.isAlt }
        } ?: false
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(getBackgroundColor(activity)),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top
    ) {
        var updater by remember { mutableStateOf(false) }
        ShowCharacteristics(activity, updater)

        TextFallout(
            stringResource(R.string.tap_card_back_to_open_cards),
            getTextColor(activity),
            getTextStrokeColor(activity),
            24.sp,
            Alignment.Center,
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(8.dp),
            TextAlign.Center
        )

        val mainState = rememberLazyListState()
        LazyColumn(
            Modifier
                .fillMaxHeight(0.9f)
                .fillMaxWidth()
                .scrollbar(
                    mainState,
                    horizontal = false,
                    knobColor = getKnobColor(activity),
                    trackColor = getTrackColor(activity)
                ),
            mainState
        ) {
            item {
                CardBack.entries.forEach { back ->
                    var rowTabShow by remember { mutableStateOf(false) }
                    var check by rememberSaveable {
                        mutableStateOf(
                            activity.save?.altDecksChosen?.get(
                                back
                            ) ?: false
                        )
                    }
                    Row(Modifier.padding(4.dp), horizontalArrangement = Arrangement.Start) {
                        Column(
                            Modifier
                                .padding(horizontal = 8.dp)
                                .fillMaxWidth(0.33f)
                        ) {
                            TextFallout(
                                stringResource(
                                    if (back == CardBack.STANDARD && check)
                                        back.getSierraMadreDeckName()
                                    else
                                        back.getDeckName()
                                ),
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
                                Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .clickable {
                                        rowTabShow = !rowTabShow
                                        if (rowTabShow) {
                                            playClickSound(activity)
                                        } else {
                                            playCloseSound(activity)
                                        }
                                    },
                            )
                        }
                        val owners = back.getOwners()
                        val owner = if (check) owners[1] else owners[0]
                        TextFallout(
                            stringResource(R.string.get_cards_from) + activity.getString(owner),
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            12.sp,
                            Alignment.CenterStart,
                            Modifier
                                .fillMaxWidth(0.66f)
                                .align(Alignment.CenterVertically),
                            TextAlign.Center
                        )
                        Column(
                            Modifier
                                .fillMaxSize()
                                .padding(vertical = 8.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            TextFallout(
                                text = "ALT!",
                                getTextColor(activity),
                                getTextStrokeColor(activity),
                                12.sp,
                                Alignment.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                TextAlign.Center
                            )
                            CheckboxCustom(
                                activity,
                                { check },
                                {
                                    activity.save!!.altDecksChosen[back] = !check
                                    saveOnGD(activity)
                                    check = !check
                                    if (check) {
                                        playClickSound(activity)
                                    } else {
                                        playCloseSound(activity)
                                    }
                                    updater = !updater
                                }
                            ) { activity.save!!.availableDecksAlt[back] == true }
                        }
                    }
                    if (rowTabShow) {
                        val state = rememberLazyListState()
                        key(check) {
                            LazyRow(
                                Modifier
                                    .weight(1f)
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
                                        mutableStateOf(
                                            isInCustomDeck(card)
                                        )
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
        TextFallout(
            text = stringResource(id = R.string.menu_back),
            getTextColor(activity),
            getTextStrokeColor(activity),
            24.sp,
            Alignment.Center,
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentHeight()
                .clickableCancel(activity) {
                    activity.save?.let {
                        saveOnGD(activity)
                    }
                    goBack()
                }
                .background(getTextBackgroundColor(activity))
                .padding(8.dp),
            TextAlign.Center
        )
    }
}

@Composable
fun ShowCharacteristics(activity: MainActivity, updater: Boolean) {
    key(updater) {
        Row(
            Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val deck = activity.save?.getCustomDeckCopy() ?: CustomDeck()
            val deckSizeMin = MainActivity.MIN_DECK_SIZE
            val color1 = if (deck.size < deckSizeMin) Color.Red else getTextColor(activity)
            TextFallout(
                text = stringResource(R.string.custom_deck_size, deck.size, deckSizeMin),
                color1,
                getTextStrokeColor(activity),
                12.sp,
                Alignment.Center,
                Modifier.fillMaxWidth(0.5f),
                TextAlign.Center
            )
            val nonFaces = deck.count { !it.isFace() }
            val nonFacesMin = MainActivity.MIN_NUM_OF_NUMBERS
            val color2 = if (nonFaces < nonFacesMin) Color.Red else getTextColor(activity)
            TextFallout(
                text = stringResource(R.string.custom_deck_non_faces, nonFaces, nonFacesMin),
                color2,
                getTextStrokeColor(activity),
                12.sp,
                Alignment.Center,
                Modifier.fillMaxWidth(),
                TextAlign.Center,
            )
        }
    }
}