package com.unicorns.invisible.caravan

import androidx.compose.foundation.clickable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.GameSaver
import com.unicorns.invisible.caravan.model.enemy.EnemyTutorial
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.save.save

@Composable
fun Tutorial(activity: MainActivity, goBack: () -> Unit) {
    var tutorialKey by rememberSaveable { mutableIntStateOf(0) }

    var showAlertDialog by rememberSaveable { mutableStateOf(false) }
    var alertDialogHeader by rememberSaveable { mutableStateOf("") }
    var alertDialogMessage by rememberSaveable { mutableStateOf("") }

    fun showAlertDialog(header: String, message: String) {
        showAlertDialog = true
        alertDialogHeader = header
        alertDialogMessage = message
    }
    fun hideAlertDialog() {
        showAlertDialog = false
        tutorialKey++
    }

    val enemy = EnemyTutorial
    val game by rememberSaveable(stateSaver = GameSaver) {
        mutableStateOf(
            Game(
                CResources(CustomDeck().apply {
                    add(Card(Rank.JACK, Suit.HEARTS, CardBack.STANDARD))
                    add(Card(Rank.QUEEN, Suit.CLUBS, CardBack.STANDARD))
                    add(Card(Rank.KING, Suit.DIAMONDS, CardBack.STANDARD))
                    add(Card(Rank.JOKER, Suit.HEARTS, CardBack.STANDARD))
                    add(Card(Rank.TWO, Suit.HEARTS, CardBack.STANDARD))
                    add(Card(Rank.TWO, Suit.DIAMONDS, CardBack.STANDARD))
                    add(Card(Rank.TWO, Suit.CLUBS, CardBack.STANDARD))
                    add(Card(Rank.THREE, Suit.HEARTS, CardBack.STANDARD))
                    add(Card(Rank.FOUR, Suit.DIAMONDS, CardBack.STANDARD))
                    add(Card(Rank.ACE, Suit.DIAMONDS, CardBack.STANDARD))
                }),
                enemy
            ).also { it.startGame(maxNumOfFaces = 4) }
        )
    }

    var updater by remember { mutableStateOf(false) }
    game.enemyCResources.onRemoveFromHand = {
        updater = !updater
    }

    ShowGame(activity = activity, game = game) { goBack() }

    key(updater) {
        when (tutorialKey) {
            0 -> {
                showAlertDialog("Kyle says:", "Howdy! Fine hot day for a Sarsaparilla, ain't it?\n" +
                        "Days like this are the reason why I ain't leaving Mojave, not for anything.")
            }
            1 -> {
                showAlertDialog("Kyle says:", "While we waiting, how about a game of Caravan?")
            }
            2 -> {
                showAlertDialog("Kyle says:", "You don't know rules? Ain't a problem. Gonna teach you while we are waiting.")
            }
            3 -> {
                showAlertDialog("Kyle says:", "There are many rules, but they mostly differ on how you get deck.")
            }
            4 -> {
                showAlertDialog("Kyle says:", "Your deck now has only ten cards. That is only for teaching you. In the field your deck is bigger.")
            }
            5 -> {
                showAlertDialog("Kyle says:", "Usually people of Mojave play with custom decks.\n" +
                        "I then say 'sticks and stones' and whip out the deck of 6s and 10s and Ks.\n" +
                        "After I beat them, they don't play with me no more. That's why I prefer balanced rules.")
            }
            6 -> {
                showAlertDialog("Kyle says:", "In balanced rules both players start with full deck o' 54. It is more - you guessed it - balanced.")
            }
            7 -> {
                showAlertDialog("Kyle says:", "Each player has three caravans - left, central and right - to sell.\n" +
                        "Caravans in the same column - opposing - are competing with each other.")
            }
            8 -> {
                showAlertDialog("Kyle says:", "The game starts like this: we take 8 cards from our decks to our hand.\n" +
                        "Then we have to start the caravans by placing ace or 2-10 card on top of each our caravan.")
            }
            9 -> {
                showAlertDialog("Kyle says:", "To place card on top of a caravan, select it in your hand and then click the brown rectangle with yellow border.")
            }
            10 -> {
                showAlertDialog("Kyle says:", "Now start your caravans.")
            }
            11 -> {
                if (game.playerCaravans.all { it.getValue() > 0 }) {
                    tutorialKey++
                }
            }
            12 -> {
                showAlertDialog("Kyle says:", "Good job.")
            }
            13 -> {
                showAlertDialog("Kyle says:", "You might ask: 'What if all 8 cards are, let's say, Jacks and Kings, and no aces or 2-10?'")
            }
            14 -> {
                showAlertDialog("Kyle says:", "Well, if you play with someone else, you'd have to reshuffle your deck.\n" +
                        "Me though, I never had that.")
            }
            15 -> {
                showAlertDialog("Kyle says:", "Sometimes there is a rule that you can discard the card during the opening phase.\n" +
                        "If you ask me, it seems like a huge advantage, so we ain't.")
            }
            16 -> {
                showAlertDialog("Kyle says:", "Now the game begins truly. You have choice of four.")
            }
            17 -> {
                showAlertDialog("Kyle says:", "Let's start with discarding cards from your hand. It is the easiest one.")
            }
            18 -> {
                showAlertDialog("Kyle says:", "Select any card, and the button 'Discard card' will appear on your right. When you press it, the card is gone.")
            }
            19 -> {
                showAlertDialog("Kyle says:", "Now, discard any face card. Preferably Queen or Joker, because King and Jack are valuable.")
            }
            20 -> {
                showAlertDialog("Warning!", "You can do, actually, whatever you want. You can discard non-face card or put card onto caravan.\n" +
                        "However, it is like travelling to Vegas through north - may break the game, difficult and not worth it ;)")
            }
            21 -> {
                if (game.playerCResources.deckSize < 2) {
                    tutorialKey++
                }
            }
            22 -> {
                showAlertDialog("Kyle says:", "Nice. Have you noticed that card was added in your hand? " +
                        "There are always 5 cards in your hand, unless your deck is empty.")
            }
            23 -> {
                showAlertDialog("Kyle says:", "Now let's talk about how to build caravan. You do it with aces or 2-10 cards.")
            }
            24 -> {
                showAlertDialog("Kyle says:", "Each of those cards has a value: 1 for ace, 2 for 2,... 10 for 10.")
            }
            25 -> {
                showAlertDialog("Kyle says:", "However, you cannot just put any card on top. You cannot put card of the same rank as the top card.")
            }
            26 -> {
                showAlertDialog("Kyle says:", "Also, you have to follow one of two rules:")
            }
            27 -> {
                showAlertDialog("Kyle says:", "either the card you want to add has the same suit as the card on top of the caravan, " +
                        "or its rank follows numerical sequence.")
            }
            28 -> {
                showAlertDialog("Kyle says:", "Numerical sequence is determined by the top two cards of the caravan.\n" +
                        "If the top rank is greater than pre-top card rank, the new card must have value greater than the top.\n" +
                        "If the top rank is less than pre-top card rank, the new card must have value less than the top.\n\n" +
                        "In raider's terms: if it's bigger - go even bigger, if it's smaller - go even smaller.")
            }
            29 -> {
                showAlertDialog("Kyle says:", "Now put all non-face cards onto caravans. Try to build a long one. Show me how you understood the rules.")
            }
            30 -> {
                showAlertDialog("Hint!", "You can discard cards, Kyle won't notice.")
            }
            31 -> {
                if (game.playerCResources.hand.all { it.isFace() }) {
                    tutorialKey++
                }
            }
            32 -> {
                showAlertDialog("Kyle says:", "Well done. Building caravan is the most difficult part of the game.")
            }
            33 -> {
                showAlertDialog("Kyle says:", "You can also disband caravans. You should do it, if its value is too big to handle with Jacks and Jokers.")
            }
            34 -> {
                showAlertDialog("Kyle says:", "To disband caravan, tap its value. To your right the button will appear. Press it, and the caravan is gone.")
            }
            35 -> {
                showAlertDialog("Kyle says:", "Disband all your caravans.")
            }
            36 -> {
                if (game.playerCaravans.all { it.getValue() == 0 }) {
                    tutorialKey++
                }
            }
            37 -> {
                showAlertDialog("Kyle says:", "Now let us talk about Jacks, Queens, Kings and Jokers.")
            }
            38 -> {
                showAlertDialog("Kyle says:", "You put them not on top of caravan, but on one of caravan's cards. Hell, even on the cards from my caravan.")
            }
            39 -> {
                showAlertDialog("Kyle says:", "First of all: you can put only three cards on a card, unless it is Jack.")
            }
            40 -> {
                showAlertDialog("Kyle says:", "King, if you put it on a card, doubles the total value of that card.")
            }
            41 -> {
                showAlertDialog("Kyle says:", "Jack just removes the card from caravan, with all its face cards.")
            }
            42 -> {
                showAlertDialog("Kyle says:", "Queen reverses the current numerical direction of the caravan, and changes the suit of the caravan to that of the Queen.")
            }
            43 -> {
                showAlertDialog("Kyle says:", "Finally, Joker. It is like Jack, but way more powerful.")
            }
            44 -> {
                showAlertDialog("Kyle says:", "If you put Joker onto ace of some suit, " +
                        "all other number cards from both player's caravans that share the same suit of the ace are removed.")
            }
            45 -> {
                showAlertDialog("Kyle says:", "If you put Joker onto 2-10 card, " +
                        "all other number cards of the same rank from both player's caravans are removed.")
            }
            46 -> {
                showAlertDialog("Kyle says:", "The card you have put Joker onto is safe for now. However, if any other Joker is played, that card can be removed.")
            }
            47 -> {
                showAlertDialog("Kyle says:", "Hard, I know. But it is the hardest part - remembering what face cards do.")
            }
            48 -> {
                showAlertDialog("Kyle says:", "Now get rid of all cards from your hand. Use face cards on my cards.")
            }
            49 -> {
                if (game.playerCResources.hand.isEmpty()) {
                    tutorialKey++
                }
            }
            50 -> {
                showAlertDialog("Kyle says:", "Congratulations! You have lost!")
            }
            51 -> {
                showAlertDialog("Kyle says:", "You have no cards to build caravan with. It means I win.")
            }
            52 -> {
                showAlertDialog("Kyle says:", "However, it is rare for the game of Caravan to end like this.")
            }
            53 -> {
                showAlertDialog("Kyle says:", "The game's target is to sell two or three of your caravans at a higher sum, or \"bid\", " +
                        "than the opposing caravan. The bid is determined by the cards in caravan.")
            }
            54 -> {
                showAlertDialog("Kyle says:", "A caravan is considered sold when its bid within 21-26 range; any bid that is higher or lower is not a sold caravan.")
            }
            55 -> {
                showAlertDialog("Kyle says:", "Usually, the game is over when in each of opposing caravan pairs one of the caravan is sold at the higher bid.")
            }
            56 -> {
                showAlertDialog("Kyle says:", "If two caravans are sold with the same bid, the game continues until one of caravans has higher bid, but no more than 26.")
            }
            57 -> {
                showAlertDialog("Kyle says:", "That's it. I am not going to tell all my tricks, you should figure the game out yourself.")
            }
            58 -> {
                showAlertDialog("Kyle says:", "Oh, and you can have this deck. It's from the Tops casino. Won it in blackjack.")
            }
            59 -> {
                showAlertDialog("Kyle says:", "Know what they say? Casino may be called Tops, but when you leave it, you're at the rock Bottoms. " +
                        "The deck is all I managed to win that day.")
            }
            60 -> {
                showAlertDialog("Kyle says:", "See ya round.")
            }
            else -> {
                activity.save?.let { save ->
                    if (save.availableDecks[CardBack.TOPS] != true) {
                        save.availableDecks[CardBack.TOPS] = true
                        save(activity, save)
                    }
                    if (save.availableCards.none { card -> card.back == CardBack.TOPS }) {
                        save.availableCards.addAll(CustomDeck(CardBack.TOPS).toList())
                        save(activity, save)
                    }
                }
                goBack()
            }
        }
    }

    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = { hideAlertDialog() },
            confirmButton = { Text(text = "OK", modifier = Modifier.clickable { hideAlertDialog() }) },
            title = { Text(text = alertDialogHeader) },
            text = { Text(text = alertDialogMessage) },
        )
    }
}