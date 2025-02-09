package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.MenuItemOpen
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
                    Alignment.Center,
                    Modifier
                        .background(getTextBackgroundColor(activity))
                        .clickableOk(activity) {
                            setCustomDeck = true
                        }
                        .padding(8.dp),
                    TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = getDividerColor(activity))
                Spacer(modifier = Modifier.height(16.dp))

                TextFallout(
                    stringResource(R.string.deck_select),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    24.sp,
                    Alignment.Center,
                    Modifier,
                    TextAlign.Center
                )

                @Composable
                fun showDeckBackRow(back: CardBack) {
                    if (back.hasAltPlayable()) {
                        Row {
                            AsyncImage(
                                model = "file:///android_asset/caravan_cards_back/" + back.getCardBackAsset(),
                                contentDescription = "",
                                modifier = getModifier(back, false).clip(RoundedCornerShape(6f))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            AsyncImage(
                                model = "file:///android_asset/caravan_cards_back/" + back.getCardBackAltAsset(),
                                contentDescription = "",
                                modifier = getModifier(back, true).clip(RoundedCornerShape(6f))
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    } else {
                        AsyncImage(
                            model = "file:///android_asset/caravan_cards_back/" + back.getCardBackAsset(),
                            contentDescription = "",
                            modifier = getModifier(back, false).clip(RoundedCornerShape(6f))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                showDeckBackRow(CardBack.STANDARD)
                showDeckBackRow(CardBack.TOPS)
                showDeckBackRow(CardBack.ULTRA_LUXE)
                showDeckBackRow(CardBack.GOMORRAH)
                showDeckBackRow(CardBack.LUCKY_38)
                showDeckBackRow(CardBack.VAULT_21)
                showDeckBackRow(CardBack.SIERRA_MADRE)
                Row {
                    showDeckBackRow(CardBack.ENCLAVE)
                    Spacer(modifier = Modifier.width(8.dp))
                    showDeckBackRow(CardBack.CHINESE)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    showDeckBackRow(CardBack.MADNESS)
                    Spacer(modifier = Modifier.width(8.dp))
                    showDeckBackRow(CardBack.VIKING)
                }
            }
        }
    }
}