package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.utils.ShowCardBack
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableCancel
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getStrokeColorByStyle
import com.unicorns.invisible.caravan.utils.getTextBackByStyle
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextColorByStyle
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.scrollbar


@Composable
fun StockMarket(
    activity: MainActivity,
    goBack: () -> Unit
) {
    val mainState = rememberLazyListState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(getBackgroundColor(activity))
            .padding(horizontal = 16.dp)
    ) {

        LazyColumn(
            Modifier
                .fillMaxHeight(0.9f)
                .fillMaxWidth()
                .scrollbar(
                    mainState,
                    horizontal = false,
                    knobColor = getKnobColor(activity),
                    trackColor = getTrackColor(activity)
                ),
            mainState
        ) {
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    TextFallout(
                        stringResource(R.string.card_prices),
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        24.sp,
                        Alignment.Center,
                        Modifier.fillMaxWidth(),
                        TextAlign.Center
                    )
                    TextFallout(
                        stringResource(R.string.updates_every_midnight),
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        16.sp,
                        Alignment.Center,
                        Modifier.fillMaxWidth(),
                        TextAlign.Center
                    )
                    listOf(
                        CardBack.ULTRA_LUXE to false,
                        CardBack.GOMORRAH to false,
                        CardBack.TOPS to false,
                        CardBack.LUCKY_38 to false,
                        CardBack.VAULT_21 to false,
                        CardBack.TOPS to true,
                        CardBack.GOMORRAH to true,
                        CardBack.ULTRA_LUXE to true,
                        CardBack.LUCKY_38 to true,
                        CardBack.VAULT_21 to true,
                        CardBack.STANDARD to true,
                    ).forEach { (back, isAlt) ->
                        if (back == CardBack.STANDARD && !isAlt) {
                            return@forEach
                        }
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ShowCardBack(
                                activity,
                                Card(Rank.ACE, Suit.CLUBS, back, isAlt),
                                Modifier.weight(0.25f),
                            )
                            val style = activity.styleId
                            TextFallout(
                                activity.save!!.getCardPrice(
                                    Card(
                                        Rank.ACE,
                                        Suit.CLUBS,
                                        back,
                                        isAlt
                                    )
                                ).toString() + stringResource(
                                    R.string.cost_caps
                                ),
                                getTextColorByStyle(activity, style),
                                getStrokeColorByStyle(activity, style),
                                18.sp,
                                Alignment.Center,
                                Modifier
                                    .weight(0.5f)
                                    .padding(end = 4.dp)
                                    .background(getTextBackByStyle(activity, style))
                                    .padding(4.dp),
                                TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        TextFallout(
            stringResource(R.string.menu_back),
            getTextColor(activity),
            getTextStrokeColor(activity),
            24.sp,
            Alignment.Center,
            Modifier
                .background(getTextBackgroundColor(activity))
                .clickableCancel(activity) {
                    goBack()
                }
                .padding(8.dp),
            TextAlign.Center
        )
    }
}