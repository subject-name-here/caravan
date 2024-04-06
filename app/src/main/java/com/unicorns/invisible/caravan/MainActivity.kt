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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.GameSaver
import com.unicorns.invisible.caravan.model.enemy.Enemy
import com.unicorns.invisible.caravan.model.enemy.EnemyEasy
import com.unicorns.invisible.caravan.model.enemy.EnemyMedium
import com.unicorns.invisible.caravan.model.primitives.Deck
import com.unicorns.invisible.caravan.save.Save
import com.unicorns.invisible.caravan.save.loadSave
import com.unicorns.invisible.caravan.save.save
import com.unicorns.invisible.caravan.toast.showToast
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Suppress("MoveLambdaOutsideParentheses")
class MainActivity : AppCompatActivity() {
    var save: Save? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        save = loadSave(this) ?: Save()

        setContent {
            var deckSelection by rememberSaveable { mutableStateOf(false) }
            var showAbout by rememberSaveable { mutableStateOf(false) }
            var showGameEasy by rememberSaveable { mutableStateOf(false) }
            var showGameMedium by rememberSaveable { mutableStateOf(false) }
            var selectedDeck by rememberSaveable { mutableStateOf(save?.selectedDeck ?: CardBack.STANDARD) }

            when {
                deckSelection -> {
                    DeckSelection(
                        this,
                        { selectedDeck },
                        { selectedDeck = it },
                        { deckSelection = false }
                    )
                }
                showAbout -> {
                    ShowAbout(activity = this, { showAbout = false })
                }
                showGameEasy -> {
                    StartGame(selectedDeck = selectedDeck, enemyCardBack = CardBack.ULTRA_LUXE, enemy = EnemyEasy) {
                        showGameEasy = false
                    }
                }
                showGameMedium -> {
                    StartGame(selectedDeck = selectedDeck, enemyCardBack = CardBack.GOMORRAH, enemy = EnemyMedium) {
                        showGameMedium = false
                    }
                }
                else -> {
                    MainMenu(
                        { deckSelection = true },
                        { showAbout = true },
                        { showGameEasy = true },
                        { showGameMedium = true }
                    )
                }
            }
        }
    }

    @Composable
    fun StartGame(selectedDeck: CardBack, enemyCardBack: CardBack, enemy: Enemy, goBack: () -> Unit) {
        val game by rememberSaveable(stateSaver = GameSaver) {
            mutableStateOf(Game(
                Deck(selectedDeck),
                Deck(enemyCardBack),
                enemy
            ).also {
                // TODO: win/lose message
                it.onWin = {
                    MainScope().launch {
                        delay(2000L)
                        save?.let { save ->
                            save.availableDecks[enemyCardBack] = true
                            save(this@MainActivity, save)
                        }
                        showToast(this@MainActivity, "You win!")
                        goBack()
                    }
                }
                it.onLose = {
                    MainScope().launch {
                        delay(2000L)
                        showToast(this@MainActivity, "You lose!")
                        goBack()
                    }
                }
                it.startGame()
            })
        }
        ShowGame(activity = this, game, goBack)
    }

    @Composable
    fun MainMenu(
        showDeckSelection: () -> Unit,
        showAbout: () -> Unit,
        showGame: () -> Unit,
        showGameMedium: () -> Unit,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "PvE1",
                modifier = Modifier.clickable {
                    showGame()
                },
                style = TextStyle(color = Color(getColor(R.color.colorPrimaryDark)), fontSize = 20.sp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "PvE2",
                modifier = Modifier.clickable {
                    showGameMedium()
                },
                style = TextStyle(color = Color(getColor(R.color.colorPrimaryDark)), fontSize = 20.sp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "PvAI",
                modifier = Modifier.clickable {
                    // TODO
                },
                style = TextStyle(color = Color(getColor(R.color.colorPrimaryDark)), fontSize = 20.sp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Select Deck",
                modifier = Modifier.clickable {
                    showDeckSelection()
                },
                style = TextStyle(color = Color(getColor(R.color.colorPrimaryDark)), fontSize = 20.sp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "About",
                modifier = Modifier.clickable {
                    showAbout()
                },
                style = TextStyle(color = Color(getColor(R.color.colorPrimaryDark)), fontSize = 20.sp)
            )
        }
    }
}