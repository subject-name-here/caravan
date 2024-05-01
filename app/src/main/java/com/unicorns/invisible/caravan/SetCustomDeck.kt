package com.unicorns.invisible.caravan

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.getCardName
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.save.save
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
            save(activity, activity.save!!)
        }
    }
    fun isAvailable(card: Card): Boolean {
        return activity.save?.availableCards?.let { cards ->
            cards.any { it.rank == card.rank && it.suit == card.suit && it.back == card.back }
        } ?: false
    }

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top) {
        var updater by remember { mutableStateOf(false) }
        key(updater) {
            ShowCharacteristics(activity)
        }
        Column(Modifier.fillMaxHeight(0.9f)) {
            CardBack.entries.forEach { back ->
                val state = rememberLazyListState()
                LazyRow(
                    Modifier
                        .padding(4.dp)
                        .weight(1f)
                        .scrollbar(state, horizontal = true), state = state) lambda@ {
                    if (activity.save?.availableDecks?.get(back) != true) {
                        return@lambda
                    }
                    items(CustomDeck(back).toList().sortedWith { o1, o2 ->
                        if (o1.rank != o2.rank) {
                            o2.rank.value - o1.rank.value
                        } else {
                            o1.suit.ordinal - o2.suit.ordinal
                        }
                    }) { card ->
                        var isSelected by remember { mutableStateOf(isInCustomDeck(card)) }
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
                                        color = Color(activity.getColor(R.color.colorAccent))
                                    )
                                    .padding(4.dp)
                                    .alpha(if (isSelected) 1f else 0.5f)
                            )
                        } else {
                            AsyncImage(
                                model = "file:///android_asset/caravan_cards_back/${card.back.getCardBackAsset()}",
                                contentDescription = "",
                                Modifier
                                    .padding(4.dp)
                                    .alpha(0.33f)
                            )
                        }
                    }
                }
            }
        }
        Text(
            text = stringResource(id = R.string.menu_back),
            modifier = Modifier
                .clickable {
                    activity.save?.let {
                        save(activity, activity.save!!)
                    }
                    goBack()
                }
                .fillMaxHeight()
                .wrapContentHeight(),
            textAlign = TextAlign.Center,
            style = TextStyle(color = Color(activity.getColor(R.color.colorPrimaryDark)), fontSize = 24.sp)
        )
    }
}

@Composable
fun ShowCharacteristics(activity: MainActivity) {
    Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
        val deckSize = activity.save?.customDeck?.size ?: -1
        val deckSizeMin = MainActivity.MIN_DECK_SIZE
        val color1 = if (deckSize < deckSizeMin) Color.Red else Color(activity.getColor(R.color.colorPrimaryDark))
        Text(text = stringResource(R.string.custom_deck_size, deckSize, deckSizeMin),
            Modifier.fillMaxWidth(0.5f),
            textAlign = TextAlign.Center,
            style = TextStyle(color = color1, fontSize = 12.sp))

        val nonFaces = activity.save?.customDeck?.count { !it.isFace() } ?: -1
        val nonFacesMin = MainActivity.MIN_NUM_OF_NUMBERS
        val color2 = if (nonFaces < nonFacesMin) Color.Red else Color(activity.getColor(R.color.colorPrimaryDark))
        Text(text = stringResource(R.string.custom_deck_non_faces, nonFaces, nonFacesMin),
            Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyle(color = color2, fontSize = 12.sp))
    }
}