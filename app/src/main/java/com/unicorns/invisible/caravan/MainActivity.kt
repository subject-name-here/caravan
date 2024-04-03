package com.unicorns.invisible.caravan

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unicorns.invisible.caravan.model.CardBack


class MainActivity : AppCompatActivity() {
    var deckSelection = mutableStateOf(false)
    var selectedDeck = CardBack.STANDARD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            if (deckSelection.value) {
                DeckSelection(this)
                return@setContent
            }
            MainMenu()
        }
    }

    @Composable
    fun MainMenu() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Text(
                text = "PvAI",
                modifier = Modifier.clickable {

                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "PvE",
                modifier = Modifier.clickable {

                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "PvP",
                modifier = Modifier.clickable {

                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Deck",
                modifier = Modifier.clickable {
                    deckSelection.value = true
                }
            )
        }
    }
}