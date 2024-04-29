package com.unicorns.invisible.caravan

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.save.save
import com.unicorns.invisible.caravan.utils.scrollbar


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
                return Modifier
                    .clickable {
                        setSelectedBack(cardBack)
                        save.selectedDeck = cardBack
                        save(activity, save)
                    }
                    .border(
                        width = (if (getSelectedBack() == cardBack) 4 else 0).dp,
                        color = Color(activity.getColor(R.color.colorAccent))
                    )
            }
        }
        return Modifier.alpha(0.5f)
    }

    var setCustomDeck by rememberSaveable { mutableStateOf(false) }
    if (setCustomDeck) {
        SetCustomDeck(activity = activity) {
            setCustomDeck = false
        }
        return
    }

    val state = rememberLazyListState()
    LazyColumn(
        modifier = Modifier.scrollbar(state, horizontal = false).fillMaxSize(),
        state = state,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Select the Deck",
                style = TextStyle(color = Color(activity.getColor(R.color.colorPrimaryDark)), fontSize = 24.sp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row {
                AsyncImage(
                    model = "file:///android_asset/caravan_cards_back/FNV_Caravan_card_back_-_standard.webp",
                    contentDescription = "",
                    modifier = getModifier(CardBack.STANDARD)
                )
                Spacer(modifier = Modifier.width(12.dp))
                AsyncImage(
                    model = "file:///android_asset/caravan_cards_back/FNV_Caravan_card_back_-_Tops.webp",
                    contentDescription = "",
                    modifier = getModifier(CardBack.TOPS)
                )
                Spacer(modifier = Modifier.width(12.dp))
                AsyncImage(
                    model = "file:///android_asset/caravan_cards_back/FNV_Caravan_card_back_-_Lucky_38.webp",
                    contentDescription = "",
                    modifier = getModifier(CardBack.LUCKY_38)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row {
                AsyncImage(
                    model = "file:///android_asset/caravan_cards_back/FNV_Caravan_card_back_-_Ultra-Luxe.webp",
                    contentDescription = "",
                    modifier = getModifier(CardBack.ULTRA_LUXE)
                )
                Spacer(modifier = Modifier.width(12.dp))
                AsyncImage(
                    model = "file:///android_asset/caravan_cards_back/FNV_Caravan_card_back_-_Gomorrah.webp",
                    contentDescription = "",
                    modifier = getModifier(CardBack.GOMORRAH)
                )
                Spacer(modifier = Modifier.width(12.dp))
                AsyncImage(
                    model = "file:///android_asset/caravan_cards_back/FNV_Caravan_card_back_-_Sierra_Madre.webp",
                    contentDescription = "",
                    modifier = getModifier(CardBack.SIERRA_MADRE)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Win new decks by defeating enemies in balanced game (w/o using custom deck).",
                Modifier.padding(12.dp),
                textAlign = TextAlign.Center,
                style = TextStyle(color = Color(activity.getColor(R.color.colorPrimaryDark)), fontSize = 16.sp)
            )

            HorizontalDivider()
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Set Custom Deck",
                modifier = Modifier.clickable {
                    setCustomDeck = true
                },
                style = TextStyle(color = Color(activity.getColor(R.color.colorPrimaryDark)), fontSize = 20.sp)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Win cards for custom deck by fighting enemies in PvE:" +
                        "\n1 or 2 cards when using custom deck," +
                        "\n5 cards when playing balanced game." +
                        "\nThe chance of winning _new_ card, however, is not 100%.",
                Modifier.padding(12.dp),
                textAlign = TextAlign.Center,
                style = TextStyle(color = Color(activity.getColor(R.color.colorPrimary)), fontSize = 14.sp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Back to Menu",
                modifier = Modifier.clickable {
                    goBack()
                },
                style = TextStyle(color = Color(activity.getColor(R.color.colorPrimaryDark)), fontSize = 24.sp)
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}