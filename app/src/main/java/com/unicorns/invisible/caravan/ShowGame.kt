package com.unicorns.invisible.caravan

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
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
import com.unicorns.invisible.caravan.model.primitives.Card


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
                .fillMaxHeight(0.20f),
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
                .fillMaxHeight(0.45f),
        ) {
            // TODO
        }
        Spacer(modifier = Modifier.height(48.dp))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f)
        ) {
            BoxWithConstraints {
                if (maxWidth < 400.dp) {
                    Column {
                        RowOfCards(cards = game.playerDeck.hand.subList(0, 4), game = game)
                        RowOfCards(cards = game.playerDeck.hand.subList(4, 8), game = game)
                    }
                } else {
                    Row {
                        RowOfCards(cards = game.playerDeck.hand, game = game)
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
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Back to Menu",
                modifier = Modifier
                    .clickable {
                        goBack()
                    }
                    .fillMaxWidth(),
                style = TextStyle(color = Color(activity.getColor(R.color.colorPrimaryDark)), fontSize = 16.sp, textAlign = TextAlign.Center),
            )
        }
    }
}

@Composable
fun RowOfCards(cards: List<Card>, game: Game) {
    Row {
        cards.forEach {
            AsyncImage(
                model = "file:///android_asset/caravan_cards/${getCardName(it)}",
                contentDescription = ""
            )
        }
    }
}