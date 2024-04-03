package com.unicorns.invisible.caravan

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.unicorns.invisible.caravan.model.CardBack


@Composable
fun DeckSelection(activity: MainActivity) {
    var selectedDeck by remember { mutableStateOf(activity.selectedDeck) }

    fun getModifier(cardBack: CardBack): Modifier {
        return Modifier.clickable {
            activity.selectedDeck = cardBack
            selectedDeck = cardBack
        }.border(width = (if (selectedDeck == cardBack) 4 else 0).dp, color = Color(activity.getColor(R.color.colorAccent)))
    }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Select the Deck",
            style = TextStyle(color = Color(activity.getColor(R.color.colorPrimaryDark)), fontSize = 24.sp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            AsyncImage(
                model = "file:///android_asset/caravan_cards_back/FNV_Caravan_card_back_-_standard.webp",
                contentDescription = "",
                modifier = getModifier(CardBack.STANDARD)
            )
            Spacer(modifier = Modifier.width(8.dp))
            AsyncImage(
                model = "file:///android_asset/caravan_cards_back/FNV_Caravan_card_back_-_Tops.webp",
                contentDescription = "",
                modifier = getModifier(CardBack.TOPS)
            )
            Spacer(modifier = Modifier.width(8.dp))
            AsyncImage(
                model = "file:///android_asset/caravan_cards_back/FNV_Caravan_card_back_-_Lucky_38.webp",
                contentDescription = "",
                modifier = getModifier(CardBack.LUCKY_38)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            AsyncImage(
                model = "file:///android_asset/caravan_cards_back/FNV_Caravan_card_back_-_Ultra-Luxe.webp",
                contentDescription = "",
                modifier = getModifier(CardBack.ULTRA_LUXE)
            )
            Spacer(modifier = Modifier.width(8.dp))
            AsyncImage(
                model = "file:///android_asset/caravan_cards_back/FNV_Caravan_card_back_-_Gomorrah.webp",
                contentDescription = "",
                modifier = getModifier(CardBack.GOMORRAH)
            )
            Spacer(modifier = Modifier.width(8.dp))
            AsyncImage(
                model = "file:///android_asset/caravan_cards_back/FNV_Caravan_card_back_-_Sierra_Madre.webp",
                contentDescription = "",
                modifier = getModifier(CardBack.SIERRA_MADRE)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Back to Menu",
            modifier = Modifier.clickable {
                activity.deckSelection.value = false
            },
            style = TextStyle(color = Color(activity.getColor(R.color.colorPrimaryDark)), fontSize = 20.sp)
        )
    }
}