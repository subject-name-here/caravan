package com.unicorns.invisible.caravan

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun ShowRules(activity: MainActivity, goBack: () -> Unit) {
    val rules = "BUILDING A DECK\n" +
            "Caravan decks are comprised of at least 30 cards from one or more traditional playing card sets. " +
            "The deck may have any number of cards of any type that suits a player's strategy, although it cannot have duplicate cards from the same set. " +
            "For example, a King of Spades from Set A and a King of Spades from the Set B deck is acceptable, " +
            "but more than one King of Spades from Set A would be illegal.\n" +
            "\n" +
            "RULES\n" +
            "Caravan is played with two players building three opposing piles (or \"caravans\") of numbered cards. " +
            "The goal is to outbid your opponent's caravan with the highest value of numbered cards without being too light (under 21) or overburdened (over 26).\n" +
            "\n" +
            "The game begins with each player taking eight cards from their deck and placing either one numerical card or ace on each caravan. " +
            "Players may not discard during this initial round.\n" +
            "\n" +
            "Once both players have started their three caravans, each player may do ONE of the following on their turn:\n" +
            "1. Play one card and draw a new card from his or her deck to their hand.\n" +
            "2. Discard one card from their hand and draw a new card from his or her deck.\n" +
            "3. Disband one of their three caravans by removing all cards from that pile.\n" +
            "\n" +
            "Caravans have a direction, either ascending or descending numerically, and a suit. " +
            "The suit is determined with the first card placed on a caravan, the direction by the second. " +
            "All subsequent cards must continue the numerical direction or match the suit of the previous card. " +
            "Cards of the same numerical value cannot be played in sequence, regardless of suit. " +
            "Face cards can be attached to numeric cards in any caravan and affects them in various ways.\n" +
            "\n" +
            "CARD VALUES\n" +
            "Joker: Played against ace, 2-10. Effects change based on whether it's an ace or a numbered card (see below). " +
            "Multiple jokers may be played on the same card.\n" +
            "\n" +
            "Ace: Value of 1. Jokers played on aces remove all other non-face cards of the ace's suit from the table. " +
            "E.g. a joker played on an Ace of Spades removes all spades (except face cards and that card, specifically) from the table.\n" +
            "\n" +
            "2-10: Listed value. Jokers played on these cards remove all other cards of this value from the table. " +
            "E.g. a joker played on a 4 of Hearts removes all 4s (other than that card, specifically) from the table.\n" +
            "\n" +
            "Jack: Played against ace, 2-10. Removes that card, along with any face cards attached to it.\n" +
            "\n" +
            "Queen: Played against ace, 2-10. Reverses the current direction of the hand and changes the current suit of the hand. " +
            "Multiple queens may be played on the same card.\n" +
            "\n" +
            "King: Played against ace, 2-10. Adds the value of that card again. E.g. a king played on a 9 adds 9 to that hand. " +
            "Multiple kings may be played on the same card for multiplicative effects. E.g. 4+ king = 8. 4 + 2 kings = 16.\n" +
            "\n" +
            "WINNING\n" +
            "A player's caravan is considered sold when the value of its cards is over 20 and under 27. " +
            "The other player may still outbid by increasing the value of their opposing pile while staying within the 21-26 range. " +
            "When each of the three competing caravans has sold, the game is over. " +
            "In the event that one of the three caravan values are tied between players, the game continues until all three caravans have sold. " +
            "The player with two or more sales wins the pot."


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        LazyColumn(Modifier.fillMaxHeight(0.85f)) {
            item {
                Text(
                    text = rules,
                    style = TextStyle(color = Color(activity.getColor(R.color.colorPrimaryDark)), fontSize = 20.sp, textAlign = TextAlign.Center)
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "Back to Menu",
            modifier = Modifier.clickable {
                goBack()
            },
            style = TextStyle(color = Color(activity.getColor(R.color.colorPrimaryDark)), fontSize = 24.sp)
        )
    }
}