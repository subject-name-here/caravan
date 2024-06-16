package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.save.saveOnGD
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableCancel
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
    getSelectedBack: () -> Pair<CardBack, Boolean>,
    setSelectedBack: (CardBack, Boolean) -> Unit,
    goBack: () -> Unit,
) {
    @Composable
    fun getModifier(cardBack: CardBack, isAlt: Boolean): Modifier {
        activity.save?.let { save ->
            val checker = if (isAlt) save.availableDecksAlt else save.availableDecks
            if (checker[cardBack] == true) {
                val (backSelected, isAltSelected) = getSelectedBack()
                return if (backSelected == cardBack && isAltSelected == isAlt) {
                    Modifier.border(width = 3.dp, color = getSelectionColor(activity))
                } else {
                    Modifier
                }.padding(4.dp).clickableSelect(activity) {
                    setSelectedBack(cardBack, isAlt)
                    save.selectedDeck = cardBack to isAlt
                    saveOnGD(activity)
                }
            }
        }
        return Modifier.padding(4.dp).alpha(0.5f)
    }

    var setCustomDeck by rememberSaveable { mutableStateOf(false) }
    if (setCustomDeck) {
        SetCustomDeck(activity = activity) {
            setCustomDeck = false
        }
        return
    }

    Column(Modifier.fillMaxSize().background(getBackgroundColor(activity))) {
        val state = rememberLazyListState()
        LazyColumn(
            modifier = Modifier
                .scrollbar(state, knobColor = getKnobColor(activity), trackColor = getTrackColor(activity), horizontal = false)
                .fillMaxWidth().fillMaxHeight(0.5f),
            state = state,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                TextFallout(
                    stringResource(R.string.deck_select),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    24.sp,
                    Alignment.Center,
                    Modifier,
                    TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row {
                    AsyncImage(
                        model = "file:///android_asset/caravan_cards_back/" + CardBack.STANDARD.getCardBackAsset(),
                        contentDescription = "",
                        modifier = getModifier(CardBack.STANDARD, false).clip(RoundedCornerShape(6f))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    AsyncImage(
                        model = "file:///android_asset/caravan_cards_back/" + CardBack.TOPS.getCardBackAsset(),
                        contentDescription = "",
                        modifier = getModifier(CardBack.TOPS, false).clip(RoundedCornerShape(6f))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    AsyncImage(
                        model = "file:///android_asset/caravan_cards_back/" + CardBack.LUCKY_38.getCardBackAsset(),
                        contentDescription = "",
                        modifier = getModifier(CardBack.LUCKY_38, false).clip(RoundedCornerShape(6f))
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row {
                    AsyncImage(
                        model = "file:///android_asset/caravan_cards_back/" + CardBack.ULTRA_LUXE.getCardBackAsset(),
                        contentDescription = "",
                        modifier = getModifier(CardBack.ULTRA_LUXE, false).clip(RoundedCornerShape(6f))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    AsyncImage(
                        model = "file:///android_asset/caravan_cards_back/" + CardBack.GOMORRAH.getCardBackAsset(),
                        contentDescription = "",
                        modifier = getModifier(CardBack.GOMORRAH, false).clip(RoundedCornerShape(6f))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    AsyncImage(
                        model = "file:///android_asset/caravan_cards_back/" + CardBack.VAULT_21.getCardBackAsset(),
                        contentDescription = "",
                        modifier = getModifier(CardBack.VAULT_21, false).clip(RoundedCornerShape(6f))
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row {
                    AsyncImage(
                        model = "file:///android_asset/caravan_cards_back/" + CardBack.ULTRA_LUXE.getCardBackAltAsset(),
                        contentDescription = "",
                        modifier = getModifier(CardBack.ULTRA_LUXE, true).clip(RoundedCornerShape(6f))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    AsyncImage(
                        model = "file:///android_asset/caravan_cards_back/" + CardBack.GOMORRAH.getCardBackAltAsset(),
                        contentDescription = "",
                        modifier = getModifier(CardBack.GOMORRAH, true).clip(RoundedCornerShape(6f))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    AsyncImage(
                        model = "file:///android_asset/caravan_cards_back/" + CardBack.VAULT_21.getCardBackAltAsset(),
                        contentDescription = "",
                        modifier = getModifier(CardBack.VAULT_21, true).clip(RoundedCornerShape(6f))
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row {
                    AsyncImage(
                        model = "file:///android_asset/caravan_cards_back/" + CardBack.STANDARD.getCardBackAltAsset(),
                        contentDescription = "",
                        modifier = getModifier(CardBack.STANDARD, true).clip(RoundedCornerShape(6f))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    AsyncImage(
                        model = "file:///android_asset/caravan_cards_back/" + CardBack.TOPS.getCardBackAltAsset(),
                        contentDescription = "",
                        modifier = getModifier(CardBack.TOPS, true).clip(RoundedCornerShape(6f))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    AsyncImage(
                        model = "file:///android_asset/caravan_cards_back/" + CardBack.LUCKY_38.getCardBackAltAsset(),
                        contentDescription = "",
                        modifier = getModifier(CardBack.LUCKY_38, true).clip(RoundedCornerShape(6f))
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        val state2 = rememberLazyListState()
        LazyColumn(
            modifier = Modifier
                .scrollbar(state2, knobColor = getKnobColor(activity), trackColor = getTrackColor(activity), horizontal = false)
                .fillMaxSize(),
            state = state2,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            item {
                TextFallout(
                    stringResource(R.string.deck_select_about),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    16.sp,
                    Alignment.Center,
                    Modifier.padding(12.dp),
                    textAlign = TextAlign.Start
                )

                HorizontalDivider(color = getDividerColor(activity))
                Spacer(modifier = Modifier.height(20.dp))
                TextFallout(
                    stringResource(R.string.deck_custom),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    20.sp,
                    Alignment.Center,
                    Modifier.background(getTextBackgroundColor(activity)).clickableOk(activity) {
                        setCustomDeck = true
                    }.padding(8.dp),
                    TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                TextFallout(
                    stringResource(R.string.deck_custom_about),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    14.sp,
                    Alignment.Center,
                    Modifier.padding(12.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = getDividerColor(activity))
                Spacer(modifier = Modifier.height(24.dp))
                TextFallout(
                    stringResource(R.string.menu_back),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    24.sp,
                    Alignment.Center,
                    Modifier.background(getTextBackgroundColor(activity)).clickableCancel(activity) {
                        goBack()
                    }.padding(8.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}