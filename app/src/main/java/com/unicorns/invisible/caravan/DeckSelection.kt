package com.unicorns.invisible.caravan

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage


@Composable
fun DeckSelection(activity: MainActivity) {
    Column {
        Row {
            AsyncImage(
                model = "file:///android_asset/caravan_cards_back/FNV_Caravan_card_back_-_standard.webp",
                contentDescription = "",
            )
            AsyncImage(
                model = "file:///android_asset/caravan_cards_back/FNV_Caravan_card_back_-_Tops.webp",
                contentDescription = "",
            )
            AsyncImage(
                model = "file:///android_asset/caravan_cards_back/FNV_Caravan_card_back_-_Lucky_38.webp",
                contentDescription = "",
            )
        }
        Row {
            AsyncImage(
                model = "file:///android_asset/caravan_cards_back/FNV_Caravan_card_back_-_Ultra-Luxe.webp",
                contentDescription = "",
            )
            AsyncImage(
                model = "file:///android_asset/caravan_cards_back/FNV_Caravan_card_back_-_Gomorrah.webp",
                contentDescription = "",
            )
            AsyncImage(
                model = "file:///android_asset/caravan_cards_back/FNV_Caravan_card_back_-_Sierra_Madre.webp",
                contentDescription = "",
            )
        }
        Text(
            text = "Back to Menu",
            modifier = Modifier.clickable {
                activity.deckSelection.value = false
            }
        )
    }
}