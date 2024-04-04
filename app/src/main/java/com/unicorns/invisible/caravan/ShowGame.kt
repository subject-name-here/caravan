package com.unicorns.invisible.caravan

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.getCardName


@Composable
fun ShowGame(activity: MainActivity, game: Game, goBack: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.25f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val handSize = game.enemyDeck.hand.size
            repeat(handSize) { numOfCard ->
                AsyncImage(
                    model = "file:///android_asset/caravan_cards_back/${game.enemyDeck.back.getCardBackName()}",
                    contentDescription = "",
                    modifier = Modifier.fillMaxWidth(1f / (handSize - numOfCard + 3))
                )
            }
            Spacer(modifier = Modifier.width(24.dp))
            Column {
                Text(
                    text = game.enemyDeck.deckSize.toString(),
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(color = Color(activity.getColor(R.color.colorAccent)), fontSize = 16.sp, textAlign = TextAlign.Center)
                )
                AsyncImage(
                    model = "file:///android_asset/caravan_cards_back/${game.enemyDeck.back.getCardBackName()}",
                    contentDescription = "",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        Spacer(modifier = Modifier.height(48.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f),
        ) {
            // TODO
        }
        Spacer(modifier = Modifier.height(48.dp))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.75f)
        ) {
            Column {
                Row(Modifier.weight(0.5f)) {
                    val firstFour = game.playerDeck.hand.subList(0, 4)
                    firstFour.forEach {
                        AsyncImage(
                            model = "file:///android_asset/caravan_cards/${getCardName(it)}",
                            contentDescription = ""
                        )
                    }
                }
                Row(Modifier.weight(0.5f)) {
                    val lastFour = game.playerDeck.hand.subList(4, 8)
                    lastFour.forEach {
                        AsyncImage(
                            model = "file:///android_asset/caravan_cards/${getCardName(it)}",
                            contentDescription = ""
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(24.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = game.playerDeck.deckSize.toString(),
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(color = Color(activity.getColor(R.color.colorAccent)), fontSize = 16.sp, textAlign = TextAlign.Center)
                )
                AsyncImage(
                    model = "file:///android_asset/caravan_cards_back/${game.playerDeck.back.getCardBackName()}",
                    contentDescription = "",
                    modifier = Modifier.weight(1f, fill = false),
                )
            }
        }
        Spacer(modifier = Modifier.width(48.dp))
        Text(
            text = "Back to Menu",
            modifier = Modifier
                .clickable {
                    goBack()
                }
                .fillMaxSize(),
            style = TextStyle(color = Color(activity.getColor(R.color.colorPrimaryDark)), fontSize = 16.sp, textAlign = TextAlign.Center)
        )
    }

}