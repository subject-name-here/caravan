package com.unicorns.invisible.caravan

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.Deck
import com.unicorns.invisible.caravan.save.Save
import com.unicorns.invisible.caravan.save.loadSave


class MainActivity : AppCompatActivity() {
    var deckSelection = mutableStateOf(false)
    var showAbout = mutableStateOf(false)
    var showGame = mutableStateOf(false)
    var selectedDeck = mutableStateOf(CardBack.STANDARD)

    var save: Save? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        save = (loadSave(this) ?: Save()).apply {
            this@MainActivity.selectedDeck.value = selectedDeck
        }

        setContent {
            when {
                deckSelection.value -> {
                    DeckSelection(this)
                }
                showAbout.value -> {
                    ShowAbout(activity = this)
                }
                showGame.value -> {
                    ShowGame(activity = this, Game(Deck(selectedDeck.value), Deck(CardBack.entries.random())).also { it.startGame() })
                }
                else -> {
                    MainMenu()
                }
            }
        }
    }

    @Composable
    fun MainMenu() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                text = "PvEasy",
                modifier = Modifier.clickable {
                    showGame.value = true
                },
                style = TextStyle(color = Color(getColor(R.color.colorPrimaryDark)), fontSize = 20.sp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "PvMedium",
                modifier = Modifier.clickable {

                },
                style = TextStyle(color = Color(getColor(R.color.colorPrimaryDark)), fontSize = 20.sp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "PvAI",
                modifier = Modifier.clickable {

                },
                style = TextStyle(color = Color(getColor(R.color.colorPrimaryDark)), fontSize = 20.sp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Select Deck",
                modifier = Modifier.clickable {
                    deckSelection.value = true
                },
                style = TextStyle(color = Color(getColor(R.color.colorPrimaryDark)), fontSize = 20.sp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "About",
                modifier = Modifier.clickable {
                    showAbout.value = true
                },
                style = TextStyle(color = Color(getColor(R.color.colorPrimaryDark)), fontSize = 20.sp)
            )
        }
    }
}