package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.save.saveOnGD
import com.unicorns.invisible.caravan.utils.getAccentColor
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getDividerColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.scrollbar


@Composable
fun DeckSelection(
    activity: MainActivity,
    getSelectedBack: () -> Pair<CardBack, Boolean>,
    setSelectedBack: (CardBack, Boolean) -> Unit,
    goBack: () -> Unit,
) {
    fun getModifier(cardBack: CardBack, isAlt: Boolean): Modifier {
        activity.save?.let { save ->
            val checker = if (isAlt) save.availableDecksAlt else save.availableDecks
            if (checker[cardBack] == true) {
                val (backSelected, isAltSelected) = getSelectedBack()
                return if (backSelected == cardBack && isAltSelected == isAlt) {
                    Modifier.border(width = 4.dp, color = getAccentColor(activity))
                } else {
                    Modifier
                }.clickable {
                    setSelectedBack(cardBack, isAlt)
                    save.selectedDeck = cardBack to isAlt
                    saveOnGD(activity)
                }
            }
        }
        return Modifier.alpha(0.5f)
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
                Text(
                    text = stringResource(R.string.deck_select),
                    fontFamily = FontFamily(Font(R.font.monofont)),
                    style = TextStyle(color = getTextColor(activity), fontSize = 24.sp)
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
                Text(
                    text = stringResource(R.string.deck_select_about),
                    fontFamily = FontFamily(Font(R.font.monofont)),
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center,
                    style = TextStyle(color = getTextColor(activity), fontSize = 16.sp)
                )

                HorizontalDivider(color = getDividerColor(activity))
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = stringResource(R.string.deck_custom),
                    modifier = Modifier.clickable {
                        setCustomDeck = true
                    }.background(getTextBackgroundColor(activity)).padding(8.dp),
                    fontFamily = FontFamily(Font(R.font.monofont)),
                    style = TextStyle(color = getTextColor(activity), fontSize = 20.sp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.deck_custom_about),
                    Modifier.padding(12.dp),
                    fontFamily = FontFamily(Font(R.font.monofont)),
                    textAlign = TextAlign.Center,
                    style = TextStyle(color = getTextColor(activity), fontSize = 14.sp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = getDividerColor(activity))
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = stringResource(R.string.menu_back),
                    modifier = Modifier.clickable {
                        goBack()
                    }.background(getTextBackgroundColor(activity)).padding(8.dp),
                    fontFamily = FontFamily(Font(R.font.monofont)),
                    style = TextStyle(color = getTextColor(activity), fontSize = 24.sp)
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}