package com.unicorns.invisible.caravan

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun DeckSelection(activity: MainActivity) {
    Column {
        Row {

        }
        Text(
            text = "Back to Menu",
            modifier = Modifier.clickable {
                activity.deckSelection.value = false
            }
        )
    }
}