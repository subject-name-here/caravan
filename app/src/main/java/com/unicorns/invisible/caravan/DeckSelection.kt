package com.unicorns.invisible.caravan

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.save.save


@Composable
fun DeckSelection(
    activity: MainActivity,
    getSelectedBack: () -> CardBack,
    setSelectedBack: (CardBack) -> Unit,
    goBack: () -> Unit,
) {
    fun getModifier(cardBack: CardBack): Modifier {
        activity.save?.let { save ->
            if (save.availableDecks[cardBack] == true) {
                return Modifier.clickable {
                    setSelectedBack(cardBack)
                    save.selectedDeck = cardBack
                    save(activity, save)
                }.border(
                    width = (if (getSelectedBack() == cardBack) 4 else 0).dp,
                    color = Color(activity.getColor(R.color.colorAccent))
                )
            }
        }
        return Modifier.alpha(0.5f)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
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
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "Back to Menu",
            modifier = Modifier.clickable {
                goBack()
            },
            style = TextStyle(color = Color(activity.getColor(R.color.colorPrimaryDark)), fontSize = 24.sp)
        )
    }
}