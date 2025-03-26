package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sebaslogen.resaca.rememberScoped
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.CardNumber
import com.unicorns.invisible.caravan.model.primitives.RankNumber
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.MenuItemOpen
import com.unicorns.invisible.caravan.utils.ShowCardBack
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableOk
import com.unicorns.invisible.caravan.utils.clickableSelect
import com.unicorns.invisible.caravan.utils.getDividerColor
import com.unicorns.invisible.caravan.utils.getSelectionColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.pxToDp


@Composable
fun DeckSelection(
    activity: MainActivity,
    goBack: () -> Unit,
) {
    var selectedDeck by rememberScoped { mutableStateOf(save.selectedDeck) }
    @Composable
    fun getModifier(cardBack: CardBack): Modifier {
        if (cardBack !in save.availableDecks) {
            return Modifier.padding(4.dp).alpha(0.33f)
        }

        val backSelected = selectedDeck
        return if (backSelected == cardBack) {
            Modifier.border(width = 3.dp, color = getSelectionColor(activity))
        } else {
            Modifier
        }
            .padding(4.dp)
            .clickableSelect(activity) {
                selectedDeck = cardBack
                save.selectedDeck = cardBack
                saveData(activity)
            }
            .clip(RoundedCornerShape(6f))
    }

    var setCustomDeck by rememberSaveable { mutableStateOf(false) }
    if (setCustomDeck) {
        SetCustomDeck(activity) { setCustomDeck = false }
        return
    }

    MenuItemOpen(activity, stringResource(R.string.menu_deck), "<-", goBack) {
        val cardsInRow = (maxWidth / (183.pxToDp() + 8.dp)).toInt()
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(16.dp))
            TextFallout(
                stringResource(R.string.deck_custom),
                getTextColor(activity),
                getTextStrokeColor(activity),
                20.sp,
                Modifier
                    .background(getTextBackgroundColor(activity))
                    .clickableOk(activity) {
                        setCustomDeck = true
                    }
                    .padding(8.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = getDividerColor(activity))
            Spacer(modifier = Modifier.height(16.dp))

            TextFallout(
                stringResource(R.string.deck_select),
                getTextColor(activity),
                getTextStrokeColor(activity),
                24.sp,
                Modifier,
            )

            Spacer(modifier = Modifier.height(16.dp))

            @Composable
            fun showDeckBack(back: CardBack) {
                ShowCardBack(
                    activity,
                    CardNumber(RankNumber.ACE, Suit.SPADES, back),
                    getModifier(back)
                )
            }

            CardBack.entries.chunked(cardsInRow).forEach { chunk ->
                Row(
                    Modifier.fillMaxWidth().wrapContentHeight(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    chunk.forEach { back ->
                        showDeckBack(back)
                    }
                }
            }
        }
    }
}