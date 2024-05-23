package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.save.save
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
    getSelectedBack: () -> CardBack,
    setSelectedBack: (CardBack) -> Unit,
    goBack: () -> Unit,
) {
    fun getModifier(cardBack: CardBack): Modifier {
        activity.save?.let { save ->
            if (save.availableDecks[cardBack] == true) {
                return if (getSelectedBack() == cardBack) {
                    Modifier.border(width = 4.dp, color = getAccentColor(activity))
                } else {
                    Modifier
                }.clickable {
                    setSelectedBack(cardBack)
                    save.selectedDeck = cardBack
                    save(activity, save)
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

    val state = rememberLazyListState()
    LazyColumn(
        modifier = Modifier
            .scrollbar(state, knobColor = getKnobColor(activity), trackColor = getTrackColor(activity), horizontal = false)
            .fillMaxSize().background(getBackgroundColor(activity)),
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
                    model = "file:///android_asset/caravan_cards_back/FNV_Caravan_card_back_-_standard.webp",
                    contentDescription = "",
                    modifier = getModifier(CardBack.STANDARD).clip(RoundedCornerShape(6f))
                )
                Spacer(modifier = Modifier.width(12.dp))
                AsyncImage(
                    model = "file:///android_asset/caravan_cards_back/FNV_Caravan_card_back_-_Tops.webp",
                    contentDescription = "",
                    modifier = getModifier(CardBack.TOPS).clip(RoundedCornerShape(6f))
                )
                Spacer(modifier = Modifier.width(12.dp))
                AsyncImage(
                    model = "file:///android_asset/caravan_cards_back/FNV_Caravan_card_back_-_Lucky_38.webp",
                    contentDescription = "",
                    modifier = getModifier(CardBack.LUCKY_38).clip(RoundedCornerShape(6f))
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row {
                AsyncImage(
                    model = "file:///android_asset/caravan_cards_back/FNV_Caravan_card_back_-_Ultra-Luxe.webp",
                    contentDescription = "",
                    modifier = getModifier(CardBack.ULTRA_LUXE).clip(RoundedCornerShape(6f))
                )
                Spacer(modifier = Modifier.width(12.dp))
                AsyncImage(
                    model = "file:///android_asset/caravan_cards_back/FNV_Caravan_card_back_-_Gomorrah.webp",
                    contentDescription = "",
                    modifier = getModifier(CardBack.GOMORRAH).clip(RoundedCornerShape(6f))
                )
                Spacer(modifier = Modifier.width(12.dp))
                AsyncImage(
                    model = "file:///android_asset/caravan_cards_back/FNV_Caravan_card_back_-_Sierra_Madre.webp",
                    contentDescription = "",
                    modifier = getModifier(CardBack.SIERRA_MADRE).clip(RoundedCornerShape(6f))
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

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