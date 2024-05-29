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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.getCardName
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.save.save
import com.unicorns.invisible.caravan.utils.CheckboxCustom
import com.unicorns.invisible.caravan.utils.getAccentColor
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTrackColor
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
        save(activity, activity.save!!)

    }
    fun isAvailable(card: Card): Boolean {
        return activity.save?.availableCards?.let { cards ->
            cards.any { it.rank == card.rank && it.suit == card.suit && it.back == card.back && it.isAlt == card.isAlt }
        } ?: false
    }

    Column(Modifier.fillMaxSize().background(getBackgroundColor(activity)),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top) {
        var updater by remember { mutableStateOf(false) }
        key(updater) {
            ShowCharacteristics(activity)
        }

        Text(
            text = "Tap card back to open cards",
            fontFamily = FontFamily(Font(R.font.monofont)),
            modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(8.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(color = getTextColor(activity), fontSize = 24.sp)
        )

        val mainState = rememberLazyListState()
        LazyColumn(
            Modifier.fillMaxHeight(0.9f).fillMaxWidth()
            .scrollbar(mainState, horizontal = false, knobColor = getKnobColor(activity), trackColor = getTrackColor(activity)),
            mainState
        ) {
            item {
                CardBack.entries.forEach { back ->
                    var rowTabShow by remember { mutableStateOf(false) }
                    var check by rememberSaveable { mutableStateOf(activity.save?.altDecksChosen?.get(back) ?: false) }
                    Row(Modifier.padding(4.dp), horizontalArrangement = Arrangement.Start) {
                        Column(Modifier.padding(horizontal = 8.dp).fillMaxWidth(0.33f)) {
                            Text(
                                text = stringResource(
                                    if (back == CardBack.STANDARD && check)
                                        back.getSierraMadreDeckName()
                                    else
                                        back.getDeckName()
                                ),
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                fontFamily = FontFamily(Font(R.font.monofont)),
                                style = TextStyle(color = getTextColor(activity), fontSize = 12.sp)
                            )
                            AsyncImage(
                                model = "file:///android_asset/caravan_cards_back/${if (check) back.getCardBackAltAsset() else back.getCardBackAsset()}",
                                contentDescription = "",
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                                    .clip(RoundedCornerShape(6f))
                                    .clickable { rowTabShow = !rowTabShow },
                                contentScale = ContentScale.Fit
                            )
                        }
                        Text(
                            text = "Get cards from:\n" + back.getOwners().joinToString("\n") { activity.getString(it) },
                            modifier = Modifier.fillMaxWidth(0.66f).align(Alignment.CenterVertically),
                            fontFamily = FontFamily(Font(R.font.monofont)),
                            style = TextStyle(color = getTextColor(activity), fontSize = 12.sp)
                        )
                        Column(Modifier.fillMaxSize().padding(vertical = 8.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "ALT!",
                                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                                fontFamily = FontFamily(Font(R.font.monofont)),
                                textAlign = TextAlign.Center,
                                style = TextStyle(color = getTextColor(activity), fontSize = 12.sp)
                            )
                            CheckboxCustom(
                                activity,
                                { check },
                                {
                                    activity.save!!.altDecksChosen[back] = !check
                                    save(activity, activity.save!!)
                                    check = !check
                                    updater = !updater
                                }
                            ) { activity.save!!.availableDecksAlt[back] == true }
                        }
                    }
                    if (rowTabShow) {
                        val state = rememberLazyListState()
                        key (check) {
                            LazyRow(
                                Modifier
                                    .weight(1f)
                                    .scrollbar(state, knobColor = getKnobColor(activity), trackColor = getTrackColor(activity), horizontal = true), state = state) lambda@ {
                                items(CustomDeck(back, check).toList().sortedWith { o1, o2 ->
                                    if (o1.rank != o2.rank) {
                                        o2.rank.value - o1.rank.value
                                    } else {
                                        o1.suit.ordinal - o2.suit.ordinal
                                    }
                                }) { card ->
                                    var isSelected by remember { mutableStateOf(
                                        isInCustomDeck(card)
                                    ) }
                                    if (isAvailable(card)) {
                                        AsyncImage(
                                            model = "file:///android_asset/caravan_cards/${getCardName(card)}",
                                            contentDescription = "",
                                            Modifier
                                                .clickable {
                                                    toggleToCustomDeck(card)
                                                    isSelected = !isSelected
                                                    updater = !updater
                                                }
                                                .border(
                                                    width = (if (isSelected) 4 else 0).dp,
                                                    color = getAccentColor(activity)
                                                )
                                                .padding(4.dp)
                                                .alpha(if (isSelected) 1f else 0.5f)
                                                .background(if (isSelected) getAccentColor(activity) else Color.Transparent)
                                                .clip(RoundedCornerShape(6f))
                                        )
                                    } else {
                                        AsyncImage(
                                            model = "file:///android_asset/caravan_cards_back/${card.back.getCardBackAssetSplit(activity)}",
                                            contentDescription = "",
                                            Modifier
                                                .padding(4.dp)
                                                .alpha(0.33f)
                                                .clip(RoundedCornerShape(6f))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Text(
            text = stringResource(id = R.string.menu_back),
            fontFamily = FontFamily(Font(R.font.monofont)),
            modifier = Modifier
                .clickable {
                    activity.save?.let {
                        save(activity, activity.save!!)
                    }
                    goBack()
                }
                .fillMaxHeight()
                .wrapContentHeight().background(getTextBackgroundColor(activity)).padding(8.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(color = getTextColor(activity), fontSize = 24.sp)
        )
    }
}

@Composable
fun ShowCharacteristics(activity: MainActivity) {
    Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
        val deck = activity.save?.getCustomDeckCopy() ?: CustomDeck()
        val deckSizeMin = MainActivity.MIN_DECK_SIZE
        val color1 = if (deck.size < deckSizeMin) Color.Red else getTextColor(activity)
        Text(text = stringResource(R.string.custom_deck_size, deck.size, deckSizeMin),
            Modifier.fillMaxWidth(0.5f),
            textAlign = TextAlign.Center,
            fontFamily = FontFamily(Font(R.font.monofont)),
            style = TextStyle(color = color1, fontSize = 12.sp))

        val nonFaces = deck.count { !it.isFace() }
        val nonFacesMin = MainActivity.MIN_NUM_OF_NUMBERS
        val color2 = if (nonFaces < nonFacesMin) Color.Red else getTextColor(activity)
        Text(text = stringResource(R.string.custom_deck_non_faces, nonFaces, nonFacesMin),
            Modifier.fillMaxWidth(),
            fontFamily = FontFamily(Font(R.font.monofont)),
            textAlign = TextAlign.Center,
            style = TextStyle(color = color2, fontSize = 12.sp))
    }
}