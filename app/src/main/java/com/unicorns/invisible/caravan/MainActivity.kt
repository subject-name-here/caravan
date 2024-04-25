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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.save.Save
import com.unicorns.invisible.caravan.save.loadSave
import com.unicorns.invisible.caravan.save.save


@Suppress("MoveLambdaOutsideParentheses")
class MainActivity : AppCompatActivity() {
    var save: Save? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        save = loadSave(this) ?: run {
            save(this, Save())
            loadSave(this)!!
        }

        setContent {
            var deckSelection by rememberSaveable { mutableStateOf(false) }
            var showAbout by rememberSaveable { mutableStateOf(false) }
            var showGameStats by rememberSaveable { mutableStateOf(false) }
            var showTutorial by rememberSaveable { mutableStateOf(false) }
            var selectedDeck by rememberSaveable { mutableStateOf(save?.selectedDeck ?: CardBack.STANDARD) }

            var showAlertDialog by remember { mutableStateOf(false) }
            var alertDialogHeader by remember { mutableStateOf("") }
            var alertDialogMessage by remember { mutableStateOf("") }
            fun showAlertDialog(header: String, message: String) {
                showAlertDialog = true
                alertDialogHeader = header
                alertDialogMessage = message
            }
            fun hideAlertDialog() {
                showAlertDialog = false
            }

            if (showAlertDialog) {
                AlertDialog(
                    onDismissRequest = { hideAlertDialog() },
                    confirmButton = { Text(text = "OK", modifier = Modifier.clickable { hideAlertDialog() }) },
                    title = { Text(text = alertDialogHeader) },
                    text = { Text(text = alertDialogMessage) },
                )
            }

            when {
                showTutorial -> {
                    Tutorial(activity = this) { showTutorial = false }
                }
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
                showGameStats -> {
                    ShowPvE(activity = this, selectedDeck = { selectedDeck }, ::showAlertDialog) {
                        showGameStats = false
                    }
                }
                else -> {
                    MainMenu(
                        { deckSelection = true },
                        { showAbout = true },
                        { showGameStats = true },
                        { showTutorial = true }
                    )
                }
            }
        }
    }

    @Composable
    fun MainMenu(
        showDeckSelection: () -> Unit,
        showAbout: () -> Unit,
        showPvE: () -> Unit,
        showTutorial: () -> Unit,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // TODO: just rules!!!
            Text(
                text = "Tutorial",
                modifier = Modifier.clickable {
                    showTutorial()
                },
                style = TextStyle(color = Color(getColor(R.color.colorPrimaryDark)), fontSize = 20.sp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "PvE",
                modifier = Modifier.clickable {
                    showPvE()
                },
                style = TextStyle(color = Color(getColor(R.color.colorPrimaryDark)), fontSize = 20.sp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Select Deck",
                modifier = Modifier.clickable {
                    showDeckSelection()
                },
                style = TextStyle(color = Color(getColor(R.color.colorPrimaryDark)), fontSize = 20.sp)
            )
            Spacer(modifier = Modifier.height(32.dp))
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