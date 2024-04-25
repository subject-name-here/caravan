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
import com.unicorns.invisible.caravan.model.primitives.Caravan
import com.unicorns.invisible.caravan.model.primitives.Deck
import com.unicorns.invisible.caravan.save.save


@Composable
fun Tutorial(activity: MainActivity, goBack: () -> Unit) {
    var tutorialKey by rememberSaveable { mutableIntStateOf(0) }
    var update by remember { mutableStateOf(false) }

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

    val enemy = EnemyTutorial()
    enemy.update = { update = !update }
    val game by rememberSaveable(stateSaver = GameSaver) {
        mutableStateOf(
            Game(
                Deck(CardBack.STANDARD),
                enemy
            ).also { it.startGame(maxNumOfFaces = 4) }
        )
    }
    game.also {
        it.onWin = {
            activity.save?.let { save ->
                save.availableDecks[CardBack.TOPS] = true
                save(activity, save)
            }
            showAlertDialog("Result", "You win!")
        }
        it.onLose = {
            showAlertDialog("Result", "You lose!")
        }
    }

    ShowGame(activity = activity, game = game) { goBack() }
    key(update) {
        when (tutorialKey) {
            0 -> {
                showAlertDialog("Kyle says:", "Howdy! Fine day, ain't it?")
            }
            1 -> {
                showAlertDialog("Kyle says:", "Days like this are the reason why I ain't leaving Mojave, not for anything.")
            }
            2 -> {
                showAlertDialog("Kyle says:", "While we waiting, how about a game of Caravan?")
            }
            3 -> {
                showAlertDialog("Kyle says:", "Some say that Caravan is as old as Mojave.\n" +
                        "Others say it was created in New Reno.")
            }
            4 -> {
                showAlertDialog("Kyle says:", "I think that's all bullshit. Prob'ly some caravan owner made up the rules while waiting on NCR outpost.")
            }
            5 -> {
                showAlertDialog("Kyle says:", "You don't know rules? Ain't a problem. Gonna teach you while we are waiting.")
            }
            6 -> {
                showAlertDialog("Kyle says:", "There are many rules. We play the balanced ones.\n" +
                        "It is not that popular here.")
            }
            7 -> {
                showAlertDialog("Kyle says:", "Usually people of Mojave playing with custom decks.\n" +
                        "I then say 'sticks and stones' and whip out the deck of 6s and 10s and Ks.\n" +
                        "After I beat them, they don't play with me no more. That's why I prefer balanced rules.")
            }
            8 -> {
                showAlertDialog("Kyle says:", "In balanced rules both players start with full deck o' 54. It is more - you guessed it - balanced.")
            }
            9 -> {
                showAlertDialog("Kyle says:", "Each player has three caravans - left, central and right - to sold.\n" +
                        "Caravans in the same column - opposing - are competing with each other.")
            }
            10 -> {
                showAlertDialog("Kyle says:", "The target is to sell two or three of your caravans at a higher sum, or \"bid\", " +
                        "than the opposing caravan. The bid is determined by the cards in caravan.")
            }
            11 -> {
                showAlertDialog("Kyle says:", "A caravan is considered sold when it reaches a bid of 21-26; any bid that is higher or lower is not a sold caravan.")
            }
            12 -> {
                showAlertDialog("Kyle says:", "The game starts like this: we take 8 cards from our decks to our hand.\n" +
                        "Then we have to start the caravans by placing ace or 2-10 card on top of each our caravan.")
            }
            13 -> {
                showAlertDialog("Kyle says:", "To place card on top of a caravan, select it in your hand and then click the brown rectangle with yellow border.")
            }
            14 -> {
                showAlertDialog("Kyle says:", "Now start your caravans.")
            }
            15 -> {
                if (game.playerCaravans.all { it.getValue() > 0 }) {
                    tutorialKey++
                }
            }
            16 -> {
                showAlertDialog("Kyle says:", "Good job.")
            }
            17 -> {
                showAlertDialog("Kyle says:", "You might ask: 'What if all 8 cards are, let's say, Jacks and Kings, and no aces or 2-10?'")
            }
            18 -> {
                showAlertDialog("Kyle says:", "Well, if you play with someone else, you'd have to reshuffle your deck.\n" +
                        "Me though, I never had that. Probably, I am too good in deck shuffling, he-he.")
            }
            19 -> {
                showAlertDialog("Kyle says:", "Now the game begins truly.")
            }
            20 -> {
                showAlertDialog("Kyle says:", "You can do one of actions:")
            }
            21 -> {
                showAlertDialog("Kyle says:", "put ace or 2-10 card on top of one of your caravans;")
            }
            22 -> {
                showAlertDialog("Kyle says:", "use face cards or Joker on one of the cards from any of caravans, mine or yours;")
            }
            23 -> {
                showAlertDialog("Kyle says:", "discard one of the cards in your hand;")
            }
            24 -> {
                showAlertDialog("Kyle says:", "or disband one of your caravans completely.")
            }
            25 -> {
                showAlertDialog("Kyle says:", "Let's start with discarding cards from your hand. It is the easiest one.")
            }
            26 -> {
                showAlertDialog("Kyle says:", "Select any card, and the button 'Discard card' will appear on your right. Press it.")
            }
            27 -> {
                showAlertDialog("Warning!", "If you do anything wrong, tutorial will be soft-locked, and you have to restart. Please, follow the instructions!")
            }
            28 -> {
                if (game.playerDeck.deckSize < 46) {
                    tutorialKey++
                }
            }
            29 -> {
                showAlertDialog("Kyle says:", "Nice. Have you noticed that card was added in your hand? There are always 5 cards in your hand.")
            }
            30 -> {
                showAlertDialog("Kyle says:", "Now let's talk about how to build caravan. You do it with aces or 2-10 cards.")
            }
            31 -> {
                showAlertDialog("Kyle says:", "Each of those cards has a value: 1 for ace, 2 for 2,... 10 for 10.")
            }
            32 -> {
                showAlertDialog("Kyle says:", "However, you cannot just put any card on top. You have to follow one of two rules:")
            }
            33 -> {
                showAlertDialog("Kyle says:", "either the card you want to add has the same suit as the card on top of the caravan")
            }
            34 -> {
                showAlertDialog("Kyle says:", "or its rank follows numerical sequence.")
            }
            35 -> {
                showAlertDialog("Kyle says:", "Numerical sequence is determined by the top two cards of the caravan.\n" +
                        "If the top rank is greater than pre-top card rank, the new card must have value greater than the top.\n" +
                        "If the top rank is less than pre-top card rank, the new card must have value less than the top.")
            }
            36 -> {
                showAlertDialog("Kyle says:", "Did you get it? Now make me a caravan of three cards, where pre-top card has the same suit as the pre-pre-top card,")
            }
            37 -> {
                showAlertDialog("Kyle says:", "and top card has different suit, but follows numerical sequence.")
            }
            38 -> {
                showAlertDialog("Hint:", "You know how to discard cards. If you think there are no cards in your hands to do what Kyle asked you to do, " +
                        "discard any card from your hand.")
            }
            39 -> {
                fun check(caravan: Caravan): Boolean {
                    if (caravan.size != 3) {
                        return false
                    }
                    if (caravan.cards[0].card.suit != caravan.cards[1].card.suit) {
                        return false
                    }
                    if (caravan.cards[1].card.suit == caravan.cards[2].card.suit) {
                        return false
                    }
                    return true
                }
                if (game.playerCaravans.any { check(it) }) {
                    tutorialKey++
                }
            }
            40 -> {
                showAlertDialog("Kyle says:", "Well done. Building caravan is the most difficult part of the game.")
            }
            41 -> {
                showAlertDialog("Kyle says:", "Now let us talk about Jacks, Queens, Kings and Jokers.")
            }
            42 -> {
                showAlertDialog("Kyle says:", "You put them not on top of caravan, but on one of caravan's cards.")
            }
            43 -> {
                showAlertDialog("Kyle says:", "You can even put it on the cards from my caravan.")
            }
            44 -> {
                showAlertDialog("Kyle says:", "First of all: you can put only three cards on a card, unless it is Jack.")
            }
            45 -> {
                showAlertDialog("Kyle says:", "King, if you put it on a card, doubles the total value of that card. " +
                        "If you put two kings, it quadruples the original value of the card.")
            }
            46 -> {
                showAlertDialog("Kyle says:", "Jack just removes the card from caravan, with all its face cards.")
            }
            47 -> {
                showAlertDialog("Kyle says:", "Queen reverses the current numerical direction of the caravan, and changes the suit of the caravan to that of the Queen.")
            }
            48 -> {
                showAlertDialog("Kyle says:", "Finally, Joker. It is like Jack, but way more powerful.")
            }
            49 -> {
                showAlertDialog("Kyle says:", "If you put Joker onto ace of some suit, " +
                        "all other number cards from both player's caravans that share the same suit of the ace are removed.")
            }
            50 -> {
                showAlertDialog("Kyle says:", "If you put Joker onto 2-10 card, " +
                        "all other number cards of the same rank from both player's caravans are removed.")
            }
            51 -> {
                showAlertDialog("Kyle says:", "The card you have put Joker onto is safe for now. However, if any other Joker is played, that card can be removed.")
            }
            52 -> {
                showAlertDialog("Kyle says:", "Hard, I know. But it is the hardest part - remembering what face cards do.")
            }
            53 -> {
                showAlertDialog("Kyle says:", "Now add any face card or Joker to any card from any of the caravans.")
            }
            54 -> {
                if ((game.playerCaravans.toMutableList() + game.enemyCaravans.toMutableList()).any { it.cards.any { it.modifiers.size > 0 } }) {
                    tutorialKey++
                }
            }
            55 -> {
                showAlertDialog("Kyle says:", "Good, good.")
            }
            56 -> {
                showAlertDialog("Kyle says:", "Finally, you can disband caravans. You should do it, if its value is too big to handle with Jacks and Jokers.")
            }
            57 -> {
                showAlertDialog("Kyle says:", "To disband caravan, tap its value. To your right the button will appear. Press it, and the caravan is gone.")
            }
            58 -> {
                showAlertDialog("Kyle says:", "Disband all your caravans.")
            }
            59 -> {
                if (game.playerCaravans.all { it.getValue() == 0 }) {
                    tutorialKey++
                }
            }
            60 -> {
                showAlertDialog("Kyle says:", "Very well.")
            }
            61 -> {
                showAlertDialog("Kyle says:", "The game is over when in each of opposing caravan pairs one of the caravan is sold at the higher bid.")
            }
            62 -> {
                showAlertDialog("Kyle says:", "If two caravans are sold with the same bid, the game continues until one of caravans has higher bid, but no more than 26.")
            }
            63 -> {
                showAlertDialog("Kyle says:", "That's it. I am not going to tell all my tricks, you should figure the game out yourself.")
            }
            64 -> {
                showAlertDialog("Kyle says:", "The last thing: take this Tops deck. I don't use it anymore.")
            }
            65 -> {
                showAlertDialog("Kyle says:", "See ya round.")
            }
            else -> {
                activity.save?.let { save ->
                    save.availableDecks[CardBack.TOPS] = true
                    save(activity, save)
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