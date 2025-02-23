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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sebaslogen.resaca.rememberScoped
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.Rank
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
import com.unicorns.invisible.caravan.utils.scrollbar


@Composable
fun DeckSelection(
    activity: MainActivity,
    goBack: () -> Unit,
) {
    var selectedDeck by rememberScoped { mutableStateOf(save.selectedDeck) }
    @Composable
    fun getModifier(cardBack: CardBack, isAlt: Boolean): Modifier {
        if (!isAlt && cardBack !in save.ownedDecks || isAlt && cardBack !in save.ownedDecksAlt) {
            return Modifier.padding(4.dp).alpha(0.33f)
        }

        val (backSelected, isAltSelected) = selectedDeck
        return if (backSelected == cardBack && isAltSelected == isAlt) {
            Modifier.border(width = 3.dp, color = getSelectionColor(activity))
        } else {
            Modifier
        }
            .padding(4.dp)
            .clickableSelect(activity) {
                selectedDeck = cardBack to isAlt
                save.selectedDeck = cardBack to isAlt
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
        val state = rememberLazyListState()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(getBackgroundColor(activity))
                .scrollbar(
                    state,
                    knobColor = getKnobColor(activity),
                    trackColor = getTrackColor(activity),
                    horizontal = false
                ),
            state = state,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            item {
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

                @Composable
                fun showDeckBack(back: CardBack, isAlt: Boolean) {
                    ShowCardBack(activity, Card(Rank.ACE, Suit.HEARTS, back, isAlt), getModifier(back, isAlt))
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    Modifier,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Column(Modifier, verticalArrangement = Arrangement.Center) {
                        showDeckBack(CardBack.STANDARD, false)
                        showDeckBack(CardBack.TOPS, false)
                        showDeckBack(CardBack.GOMORRAH, false)
                        showDeckBack(CardBack.ULTRA_LUXE, false)
                        showDeckBack(CardBack.LUCKY_38, false)
                        showDeckBack(CardBack.VAULT_21, false)
                        showDeckBack(CardBack.SIERRA_MADRE, false)
                    }
                    Spacer(Modifier.width(8.dp))
                    Column(Modifier, verticalArrangement = Arrangement.Center) {
                        showDeckBack(CardBack.STANDARD, true)
                        showDeckBack(CardBack.TOPS, true)
                        showDeckBack(CardBack.GOMORRAH, true)
                        showDeckBack(CardBack.ULTRA_LUXE, true)
                        showDeckBack(CardBack.LUCKY_38, true)
                        showDeckBack(CardBack.VAULT_21, true)
                        showDeckBack(CardBack.SIERRA_MADRE, true)
                    }
                    Spacer(Modifier.width(8.dp))
                    Column(Modifier, verticalArrangement = Arrangement.Center) {
                        showDeckBack(CardBack.MADNESS, false)
                        showDeckBack(CardBack.NCR, false)
                        showDeckBack(CardBack.LEGION, false)
                        showDeckBack(CardBack.VIKING, false)
                        showDeckBack(CardBack.ENCLAVE, false)
                        showDeckBack(CardBack.CHINESE, false)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}