package com.unicorns.invisible.caravan

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
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

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Column(Modifier.fillMaxHeight(0.9f).weight(1f)) {
            CardBack.entries.filter { back ->
                activity.save?.let {
                    it.availableDecks[back]
                } ?: false
            }.forEach { back ->
                val state = rememberLazyListState()
                LazyRow(
                    Modifier
                        .padding(4.dp)
                        .weight(1f).scrollbar(state, horizontal = true), state = state) {
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
            text = "Back to Menu",
            modifier = Modifier.clickable {
                activity.save?.let {
                    save(activity, activity.save!!)
                }
                goBack()
            },
            style = TextStyle(color = Color(activity.getColor(R.color.colorPrimaryDark)), fontSize = 24.sp)
        )
    }
}