package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.composables.core.ScrollArea
import com.composables.core.VerticalScrollbar
import com.sebaslogen.resaca.rememberScoped
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CardNumber
import com.unicorns.invisible.caravan.model.primitives.RankNumber
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.MenuItemOpen
import com.unicorns.invisible.caravan.utils.ShowCardBack
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableOk
import com.unicorns.invisible.caravan.utils.clickableSelect
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getDividerColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getSelectionColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.pxToDp
import com.unicorns.invisible.caravan.utils.scrollbar


@Composable
fun DeckSelection(
    activity: MainActivity,
    goBack: () -> Unit,
) {
    var selectedDeck by rememberScoped { mutableStateOf(save.selectedDeck) }
    @Composable
    fun getModifier(cardBack: CardBack): Modifier {
        if (cardBack to number !in save.availableDecks) {
            return Modifier.padding(4.dp).alpha(0.33f)
        }

        val (backSelected, numberSelected) = selectedDeck
        return if (backSelected == cardBack && numberSelected == number) {
            Modifier.border(width = 3.dp, color = getSelectionColor(activity))
        } else {
            Modifier
        }
            .padding(4.dp)
            .clickableSelect(activity) {
                selectedDeck = cardBack to number
                save.selectedDeck = cardBack to number
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

            CardBack.entries.forEach { back ->
                showDeckBack(back)
            }
        }
    }
}