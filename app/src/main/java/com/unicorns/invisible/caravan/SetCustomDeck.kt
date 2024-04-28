package com.unicorns.invisible.caravan

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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


@Composable
fun SetCustomDeck(
    activity: MainActivity,
    goBack: () -> Unit,
) {
    // TODO: horizontal orientation!!!

    fun isInCustomDeck(card: Card): Boolean {
        return activity.save?.customDeck?.let { deck ->
            deck.any { it.suit == card.suit && it.back == card.back && it.rank == card.rank }
        } ?: false
    }
    fun toggleToCustomDeck(card: Card) {
        activity.save?.customDeck?.let { deck ->
            val cardInDeck = deck.find { it.suit == card.suit && it.back == card.back && it.rank == card.rank }
            if (cardInDeck == null) {
                deck.add(card)
            } else {
                deck.remove(cardInDeck)
            }
            save(activity, activity.save!!)
        }
    }

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Column(Modifier.fillMaxHeight(0.9f)) {
            CardBack.entries.filter { back ->
                activity.save?.let {
                    it.availableDecks[back]
                } ?: false
            }.forEach { back ->
                if (back == CardBack.SIERRA_MADRE) {
                    return@forEach
                }
                LazyRow(Modifier.padding(4.dp)) {
                    items(CustomDeck(back).toList().sortedWith { o1, o2 ->
                        if (o1.rank != o2.rank) {
                            o2.rank.value - o1.rank.value
                        } else {
                            o1.suit.ordinal - o2.suit.ordinal
                        }
                    }) { card ->
                        var isSelected by remember { mutableStateOf(isInCustomDeck(card)) }
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