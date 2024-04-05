package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.getCardName
import com.unicorns.invisible.caravan.model.primitives.Caravan
import com.unicorns.invisible.caravan.model.primitives.Card


@Composable
fun ShowGame(activity: MainActivity, game: Game, goBack: () -> Unit) {
    var selectedCard by remember { mutableStateOf<Card?>(null) }
    val selectedCardColor = Color(activity.getColor(R.color.colorAccent))

    fun onCardClicked(index: Int) {
        selectedCard = if (game.playerDeck.hand[index] == selectedCard) {
            null
        } else {
            game.playerDeck.hand[index]
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(activity.getColor(R.color.colorPrimaryDark)))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            BoxWithConstraints(modifier = Modifier
                .fillMaxWidth(0.75f)
                .fillMaxHeight(),
            ) {
                if (maxWidth < 400.dp) {
                    val handSize = game.enemyDeck.hand.size
                    Column {
                        RowOfEnemyCards(minOf(4, handSize), game.enemyDeck.back)
                        if (handSize - 4 > 0) {
                            RowOfEnemyCards(handSize - 4, game.enemyDeck.back)
                        }
                    }
                } else {
                    Row {
                        RowOfEnemyCards(game.enemyDeck.hand.size, game.enemyDeck.back)
                    }
                }
            }
            Column(modifier = Modifier.fillMaxHeight()) {
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.45f),
        ) {
            Spacer(modifier = Modifier.weight(0.03f))
            Column(modifier = Modifier
                .weight(0.25f)
                .fillMaxHeight()) {
                CaravanOnField(activity, game.enemyCaravans[0], true)
                HorizontalDivider()
                CaravanOnField(activity, game.playerCaravans[0], false)
            }
            Column(modifier = Modifier
                .weight(0.25f)
                .fillMaxHeight()) {
                CaravanOnField(activity, game.enemyCaravans[1], true)
                HorizontalDivider()
                CaravanOnField(activity, game.playerCaravans[1], false)
            }
            Column(modifier = Modifier
                .weight(0.25f)
                .fillMaxHeight()) {
                CaravanOnField(activity, game.enemyCaravans[2], true)
                HorizontalDivider()
                CaravanOnField(activity, game.playerCaravans[2], false)
            }
            Column(modifier = Modifier
                .weight(0.22f)
                .fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                Text(text = "OK", textAlign = TextAlign.Center, color = Color(activity.getColor(R.color.colorAccent)), modifier = Modifier.fillMaxWidth())
            }
        }
        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f)
        ) {
            BoxWithConstraints(modifier = Modifier
                .fillMaxWidth(0.75f)
                .fillMaxHeight(),
                contentAlignment = Alignment.BottomCenter
            ) {
                if (maxWidth < 400.dp) {
                    Column {
                        RowOfCards(cards = game.playerDeck.hand.subList(0, 4), 0, selectedCard, selectedCardColor, ::onCardClicked)
                        RowOfCards(cards = game.playerDeck.hand.subList(4, 8), 4, selectedCard, selectedCardColor, ::onCardClicked)
                    }
                } else {
                    Row {
                        RowOfCards(cards = game.playerDeck.hand, 0, selectedCard, selectedCardColor, ::onCardClicked)
                    }
                }
            }
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = game.playerDeck.deckSize.toString(),
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(color = Color(activity.getColor(R.color.colorAccent)), fontSize = 16.sp, textAlign = TextAlign.Center)
                )
                AsyncImage(
                    model = "file:///android_asset/caravan_cards_back/${game.playerDeck.back.getCardBackName()}",
                    contentDescription = "",
                    modifier = Modifier.fillMaxWidth(),
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
                style = TextStyle(color = Color(activity.getColor(R.color.colorAccent)), fontSize = 16.sp, textAlign = TextAlign.Center),
            )
        }
    }
}

@Composable
fun RowOfCards(cards: List<Card>, offset: Int = 0, selectedCard: Card?, selectedCardColor: Color, onClick: (Int) -> Unit) {
    Row {
        cards.forEachIndexed { index, it ->
            val modifier = if (it == selectedCard) {
                Modifier.border(
                    width = 4.dp,
                    color = selectedCardColor
                )
            } else {
                Modifier
            }
                .clickable {
                    onClick(offset + index)
                }
                .padding(4.dp)
                .weight(1f, fill = false)
            AsyncImage(
                model = "file:///android_asset/caravan_cards/${getCardName(it)}",
                contentDescription = "",
                modifier
            )
        }
    }
}

@Composable
fun RowOfEnemyCards(numOfCards: Int, back: CardBack) {
    if (numOfCards <= 0) {
        return
    }
    Row {
        repeat(numOfCards) {
            AsyncImage(
                model = "file:///android_asset/caravan_cards_back/${back.getCardBackName()}",
                contentDescription = ""
            )
        }
    }
}

@Composable
fun CaravanOnField(activity: MainActivity, caravan: Caravan, isEnemy: Boolean) {
    if (isEnemy) {
        LazyColumn(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxHeight(0.5f).fillMaxWidth()
        ) {
            if (!caravan.isFull()) {
                item {
                    Box(modifier = Modifier
                        .fillParentMaxWidth()
                        .fillParentMaxHeight(0.25f)
                        .background(Color(activity.getColor(R.color.colorPrimary)))
                        .border(4.dp, Color(activity.getColor(R.color.colorAccent)))
                    ) {}
                }
            }
            items(caravan.cards.reversed()) {
                AsyncImage(
                    model = "file:///android_asset/caravan_cards/${getCardName(it.card)}",
                    contentDescription = "",
                )
            }
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxHeight().fillMaxWidth()
        ) {
            items(caravan.cards) {
                AsyncImage(
                    model = "file:///android_asset/caravan_cards/${getCardName(it.card)}",
                    contentDescription = "",
                )
            }
            if (!caravan.isFull()) {
                item {
                    Box(modifier = Modifier
                        .fillParentMaxWidth()
                        .fillParentMaxHeight(0.25f)
                        .background(Color(activity.getColor(R.color.colorPrimary)))
                        .border(4.dp, Color(activity.getColor(R.color.colorAccent)))
                    ) {}
                }
            }
        }
    }
}