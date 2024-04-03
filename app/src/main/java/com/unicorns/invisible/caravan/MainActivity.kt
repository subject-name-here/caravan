package com.unicorns.invisible.caravan

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
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
        Column {
            Text(
                text = "PvAI",
                modifier = Modifier.clickable {

                }
            )
            Text(
                text = "PvE",
                modifier = Modifier.clickable {

                }
            )
            Text(
                text = "PvP",
                modifier = Modifier.clickable {

                }
            )
            Text(
                text = "Deck",
                modifier = Modifier.clickable {
                    deckSelection.value = true
                }
            )
        }
    }
}