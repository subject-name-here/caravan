package com.unicorns.invisible.caravan.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.FixedScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.composables.core.ScrollArea
import com.composables.core.Thumb
import com.composables.core.VerticalScrollbar
import com.composables.core.rememberScrollAreaState
import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.CardBack.GOMORRAH_DARK
import com.unicorns.invisible.caravan.model.CardBack.STANDARD_LEGENDARY
import com.unicorns.invisible.caravan.model.CardBack.STANDARD_MYTHIC
import com.unicorns.invisible.caravan.model.CardBack.STANDARD_RARE
import com.unicorns.invisible.caravan.model.CardBack.STANDARD_UNCOMMON
import com.unicorns.invisible.caravan.model.CardBack.TOPS_RED
import com.unicorns.invisible.caravan.model.CardBack.ULTRA_LUXE_CRIME
import com.unicorns.invisible.caravan.model.getCardName
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CardAtomic
import com.unicorns.invisible.caravan.model.primitives.CardFBomb
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CardNumber
import com.unicorns.invisible.caravan.model.primitives.CardNumberWW
import com.unicorns.invisible.caravan.model.primitives.CardWildWasteland
import com.unicorns.invisible.caravan.model.primitives.CardWithPrice
import com.unicorns.invisible.caravan.model.primitives.RankFace
import com.unicorns.invisible.caravan.model.primitives.RankNumber
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlin.random.Random


@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }
@Composable
fun Dp.dpToSp() = with(LocalDensity.current) { this@dpToSp.toSp() }
@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }



fun getFilter(back: CardBack): ColorFilter {
    return when (back) {
        TOPS_RED -> ColorFilter.colorMatrix(ColorMatrix().apply {
            timesAssign(
                ColorMatrix(
                    floatArrayOf(
                        0.9f, 2f, 0f, 0f, 0f,
                        0.3f, 2f, 0f, 0f, 0f,
                        0.15f, 2f, 0.1f, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
            )
            timesAssign(
                ColorMatrix(
                    floatArrayOf(
                        1f, 0f, 0f, 0f, 0f,
                        0f, 1f, -1f, 0f, 0f,
                        0f, 0f, 1f, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
            )
            timesAssign(
                ColorMatrix(
                    floatArrayOf(
                        1f, 0f, 0f, 0f, 0f,
                        0f, -1f, 0f, 0f, 255f,
                        0f, 0f, -1f, 0f, 255f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
            )
            timesAssign(
                ColorMatrix(
                    floatArrayOf(
                        0f, 0f, 1f, 0f, 0f,
                        0f, 1f, 0f, 0f, 0f,
                        1f, 0f, 0f, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
            )
        })

        ULTRA_LUXE_CRIME -> ColorFilter.colorMatrix(ColorMatrix().apply {
            timesAssign(
                ColorMatrix(
                    floatArrayOf(
                        1f, 0f, 0f, 0f, 0f,
                        0f, 1f, 0f, 0f, 0f,
                        0f, 0f, 16f, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
            )
            timesAssign(
                ColorMatrix(
                    floatArrayOf(
                        -1f, 0f, 0f, 0f, 255f,
                        0f, -1f, 0f, 0f, 255f,
                        0f, 0f, -1f, 0f, 255f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
            )
            timesAssign(
                ColorMatrix(
                    floatArrayOf(
                        0f, 0f, 2f, 0f, 0f,
                        0f, 2f, 0f, 0f, 0f,
                        2f, 0f, 0f, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
            )
        })

        GOMORRAH_DARK -> ColorFilter.colorMatrix(ColorMatrix().apply {
            timesAssign(
                ColorMatrix(
                    floatArrayOf(
                        0f, 1f, 0f, 0f, 0f,
                        0.5f, 0.5f, 0f, 0f, 0f,
                        1f, 0f, 0f, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
            )
            timesAssign(
                ColorMatrix(
                    floatArrayOf(
                        -1f, 0f, 0f, 0f, 255f,
                        0f, -1f, 0f, 0f, 255f,
                        0f, 0f, -1f, 0f, 255f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
            )
            timesAssign(
                ColorMatrix(
                    floatArrayOf(
                        2f, 0f, 0f, 0f, 0f,
                        0f, 2f, 0f, 0f, 0f,
                        0f, 0f, 2f, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
            )
        })

        STANDARD_UNCOMMON -> getBackFilter(back)
        STANDARD_RARE -> getBackFilter(back)
        STANDARD_MYTHIC -> getBackFilter(back)
        STANDARD_LEGENDARY -> getBackFilter(back)
        else -> ColorFilter.colorMatrix(ColorMatrix())
    }
}


fun getBackFilter(back: CardBack): ColorFilter {
    return when (back) {
        STANDARD_UNCOMMON -> {
            ColorFilter.colorMatrix(ColorMatrix(floatArrayOf(
                0f, 1f, 0f, 0f, 0f,
                1f, 0f, 0f, 0f, 0f,
                0f, 0f, 1f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )))
        }
        STANDARD_RARE -> {
            ColorFilter.colorMatrix(ColorMatrix(floatArrayOf(
                0f, 0f, 1f, 0f, 0f,
                0f, 1f, 0f, 0f, 0f,
                1f, 0f, 0f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )))
        }
        STANDARD_MYTHIC -> {
            ColorFilter.colorMatrix(
                ColorMatrix(
                    floatArrayOf(
                        1f, 0f, 0f, 0f, 0f,
                        0f, 1f, 0f, 0f, 0f,
                        1f, 0f, 0f, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
            )
        }
        STANDARD_LEGENDARY -> {
            ColorFilter.colorMatrix(ColorMatrix(floatArrayOf(
                1f, 0f, 0f, 0f, 0f,
                1f, 0f, 0f, 0f, 0f,
                0f, 0f, 1f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )))
        }
        else -> ColorFilter.colorMatrix(ColorMatrix())
    }
}


@Composable
fun ShowCard(activity: MainActivity, card: Card, modifier: Modifier, scale: Float = 1f) {
    Box(Modifier.wrapContentSize()) {
        AsyncImage(
            model = ImageRequest.Builder(activity)
                .data("file:///android_asset/caravan_cards/${getCardName(card)}")
                .size(183, 256)
                .decoderFactory(SvgDecoder.Factory())
                .build(),
            contentDescription = "",
            modifier.clip(RoundedCornerShape(5)),
            colorFilter = if (card is CardWithPrice) {
                getFilter(card.getBack())
            } else {
                ColorFilter.colorMatrix(ColorMatrix())
            },
            contentScale = FixedScale(scale),
            alignment = BiasAlignment(-1f, -1f)
        )
        if (card is CardWithPrice && card.getBack() == CardBack.NCR) {
            Box(Modifier.size(183.pxToDp() * scale, 256.pxToDp() * scale).clipToBounds()) {
                @Composable
                fun SuitToStamp(suit: Suit, extra: Int) {
                    val rand = Random(22229 + extra + suit.ordinal)
                    when (suit) {
                        Suit.HEARTS -> {
                            val rotation = rand.nextDouble(-5.0, 5.0)
                            AsyncImage(
                                model = ImageRequest.Builder(activity)
                                    .data(R.drawable.ncr_stamp_date)
                                    .decoderFactory(SvgDecoder.Factory())
                                    .build(),
                                contentDescription = "",
                                modifier = Modifier.scale(0.75f).offset { IntOffset(0, rand.nextInt(90, 150)) * scale }.rotate(rotation.toFloat()),
                            )
                        }
                        Suit.CLUBS -> {
                            val rotation = rand.nextDouble(-40.0, -30.0)
                            AsyncImage(
                                model = ImageRequest.Builder(activity)
                                    .data(R.drawable.ncr_stamp_ncr)
                                    .decoderFactory(SvgDecoder.Factory())
                                    .build(),
                                contentDescription = "",
                                Modifier.offset { IntOffset(90, 0) * scale }.rotate(rotation.toFloat()),
                            )
                        }
                        Suit.SPADES -> {
                            val rotation = rand.nextDouble(-10.0, 10.0)
                            val offset = IntOffset(rand.nextInt(-5, 70).toInt(), rand.nextInt(-10, 170).toInt()) * scale
                            AsyncImage(
                                model = ImageRequest.Builder(activity)
                                    .data(R.drawable.ncr_stamp_usage_apprvd)
                                    .decoderFactory(SvgDecoder.Factory())
                                    .build(),
                                contentDescription = "",
                                Modifier.offset { offset }.rotate(rotation.toFloat()),
                            )
                        }
                        Suit.DIAMONDS -> {
                            val rotation1 = rand.nextDouble(-10.0, 10.0)
                            val rotation2 = rand.nextDouble(-2.5, 2.5)
                            Box(Modifier.scale(0.75f).offset { IntOffset(0, 150) * scale }.rotate(rotation1.toFloat())) {
                                AsyncImage(
                                    model = ImageRequest.Builder(activity)
                                        .data(R.drawable.ncr_stamp_validated_by_qm)
                                        .decoderFactory(SvgDecoder.Factory())
                                        .build(),
                                    contentDescription = "",
                                )
                                AsyncImage(
                                    model = ImageRequest.Builder(activity)
                                        .data(R.drawable.ncr_signature)
                                        .decoderFactory(SvgDecoder.Factory())
                                        .build(),
                                    contentDescription = "",
                                    Modifier.scale(0.5f).offset { IntOffset(115, 25) * scale }.rotate(rotation2.toFloat()),
                                )
                            }
                        }
                    }
                }

                when (card) {
                    is CardFaceSuited -> {
                        SuitToStamp(card.suit, card.rank.value * 113)
                        if (card.rank == RankFace.JACK && card.suit == Suit.HEARTS) {
                            SuitToStamp(Suit.CLUBS, 257)
                        } else if (card.rank == RankFace.JACK && card.suit == Suit.CLUBS) {
                            val rand = Random(22229 + 147)
                            val rotation = rand.nextDouble(-4.0, 4.0)
                            val offset = IntOffset(0, 120) * scale
                            AsyncImage(
                                model = ImageRequest.Builder(activity)
                                    .data(R.drawable.ncr_stamp_warning)
                                    .decoderFactory(SvgDecoder.Factory())
                                    .build(),
                                contentDescription = "",
                                Modifier.offset { offset }.rotate(rotation.toFloat()),
                            )
                        }
                    }
                    is CardJoker -> {
                        if (card.number == CardJoker.Number.ONE) {
                            Box(Modifier.offset { IntOffset(0, 50) * scale }.rotate(4f)) {
                                AsyncImage(
                                    model = ImageRequest.Builder(activity)
                                        .data(R.drawable.moove_along_1)
                                        .decoderFactory(SvgDecoder.Factory())
                                        .build(),
                                    contentDescription = "",
                                    Modifier.scale(0.75f)
                                )
                                AsyncImage(
                                    model = ImageRequest.Builder(activity)
                                        .data(R.drawable.moove_along_2)
                                        .decoderFactory(SvgDecoder.Factory())
                                        .build(),
                                    contentDescription = "",
                                    Modifier.scale(0.8f).offset { IntOffset(50, 100) * scale }.rotate(5f),
                                )
                            }
                        } else {
                            SuitToStamp(Suit.DIAMONDS, card.rank.value * 155)
                            SuitToStamp(Suit.HEARTS, card.rank.value * 175)
                            SuitToStamp(Suit.SPADES, card.rank.value * 195)
                            SuitToStamp(Suit.CLUBS, card.rank.value * 215)
                        }
                    }
                    is CardNumber -> {
                        if (card.rank == RankNumber.SIX && card.suit == Suit.SPADES) {
                            val rand = Random(22229 + 214)
                            val rotation = rand.nextDouble(-4.0, 4.0)
                            val offset = IntOffset(0, 120) * scale
                            AsyncImage(
                                model = ImageRequest.Builder(activity)
                                    .data(R.drawable.ncr_stamp_usage_denied)
                                    .decoderFactory(SvgDecoder.Factory())
                                    .build(),
                                contentDescription = "",
                                Modifier.offset { offset }.rotate(rotation.toFloat()),
                            )
                        } else {
                            SuitToStamp(card.suit, card.rank.value * 131)
                            if (card.rank == RankNumber.FOUR && card.suit == Suit.SPADES) {
                                SuitToStamp(card.suit, card.rank.value * 134)
                                SuitToStamp(card.suit, card.rank.value * 138)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShowCardBack(activity: MainActivity, card: Card, modifier: Modifier, scale: Float = 1f) {
    val asset = when (card) {
        is CardWithPrice -> card.getBack().nameIdWithBackFileName.second
        is CardNumberWW -> "ww_back.webp"
        is CardAtomic -> "nuclear_back.webp"
        is CardFBomb -> "ccp_alt_back.webp"
        is CardWildWasteland -> "ww_back.webp"
    }
    AsyncImage(
        model = ImageRequest.Builder(activity)
            .data("file:///android_asset/caravan_cards_back/$asset")
            .size(183, 256)
            .decoderFactory(SvgDecoder.Factory())
            .build(),
        contentDescription = "",
        modifier.clip(RoundedCornerShape(5)),
        contentScale = FixedScale(scale),
        colorFilter = if (card is CardWithPrice) {
            getBackFilter(card.getBack())
        } else {
            ColorFilter.colorMatrix(ColorMatrix())
        },
        alignment = BiasAlignment(-1f, -1f)
    )
}


@Composable
fun CheckboxCustom(
    activity: MainActivity,
    checked: () -> Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: () -> Boolean
) {
    Checkbox(
        checked = checked(), onCheckedChange = onCheckedChange, colors = CheckboxColors(
            checkedCheckmarkColor = getCheckBoxBorderColor(activity),
            uncheckedCheckmarkColor = Color.Transparent,
            checkedBoxColor = getCheckBoxFillColor(activity),
            uncheckedBoxColor = Color.Transparent,
            disabledCheckedBoxColor = Color.Red,
            disabledUncheckedBoxColor = Color.Red,
            disabledIndeterminateBoxColor = Color.Red,
            checkedBorderColor = getCheckBoxBorderColor(activity),
            uncheckedBorderColor = getCheckBoxBorderColor(activity),
            disabledBorderColor = Color.Red,
            disabledUncheckedBorderColor = Color.Red,
            disabledIndeterminateBorderColor = Color.Red,
        ), enabled = enabled()
    )
}

@Composable
fun SliderCustom(
    activity: MainActivity,
    getValue: () -> Float,
    setValue: (Float) -> Unit,
    onValueChangedFinished: () -> Unit = {}
) {
    Slider(
        getValue(), onValueChange = { setValue(it) }, colors = SliderColors(
            thumbColor = getSliderThumbColor(activity),
            activeTrackColor = getSliderTrackColor(activity),
            activeTickColor = getSliderTrackColor(activity),
            inactiveTickColor = getSliderTrackColor(activity),
            inactiveTrackColor = getSliderTrackColor(activity),
            disabledThumbColor = Color.Gray,
            disabledActiveTrackColor = Color.Gray,
            disabledActiveTickColor = Color.Gray,
            disabledInactiveTickColor = Color.Gray,
            disabledInactiveTrackColor = Color.Gray,
        ), onValueChangeFinished = onValueChangedFinished
    )
}

@Composable
fun SwitchCustomUsualBackground(
    activity: MainActivity,
    checked: () -> Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: () -> Boolean = { true }
) {
    Switch(
        checked = checked(), onCheckedChange = onCheckedChange, colors = SwitchColors(
            checkedThumbColor = getTextColor(activity),
            checkedTrackColor = getTextBackgroundColor(activity),
            checkedBorderColor = Color.Transparent,
            checkedIconColor = Color.Transparent,
            uncheckedThumbColor = getTextColor(activity),
            uncheckedTrackColor = getTextBackgroundColor(activity),
            uncheckedBorderColor = Color.Transparent,
            uncheckedIconColor = Color.Transparent,
            disabledCheckedThumbColor = colorResource(R.color.red),
            disabledCheckedTrackColor = colorResource(R.color.white),
            disabledCheckedBorderColor = Color.Transparent,
            disabledCheckedIconColor = Color.Transparent,
            disabledUncheckedThumbColor = colorResource(R.color.red),
            disabledUncheckedTrackColor = colorResource(R.color.white),
            disabledUncheckedBorderColor = Color.Transparent,
            disabledUncheckedIconColor = Color.Transparent,
        ),
        enabled = enabled()
    )
}

// TEXT

private fun getStrokeWidth(textSize: TextUnit): Float {
    return when {
        textSize >= 30.sp -> 4f
        textSize >= 24.sp -> 2f
        textSize >= 18.sp -> 1f
        else -> 0f
    }
}

@Composable
fun TextFallout(
    text: String,
    textColor: Color,
    strokeColor: Color,
    textSize: TextUnit,
    boxModifier: Modifier,
    textAlignment: TextAlign = TextAlign.Center,
    boxAlignment: Alignment = Alignment.Center
) {
    TextCustom(text, Font(R.font.monofont), textColor, strokeColor, textSize, boxModifier, textAlignment, boxAlignment)
}

@Composable
fun TextSymbola(
    text: String,
    textColor: Color,
    textSize: TextUnit,
    boxModifier: Modifier,
    textAlignment: TextAlign = TextAlign.Center,
    boxAlignment: Alignment = Alignment.Center
) {
    Box(boxModifier, contentAlignment = boxAlignment) {
        Text(
            text = text, color = textColor,
            fontFamily = FontFamily(Font(R.font.symbola)),
            style = TextStyle(
                color = textColor,
                fontSize = textSize,
                drawStyle = Fill,
                textAlign = textAlignment
            )
        )
    }
}

@Composable
fun TextFallout(
    text: AnnotatedString,
    textColor: Color,
    strokeColor: Color,
    textSize: TextUnit,
    boxModifier: Modifier,
    textAlignment: TextAlign = TextAlign.Center,
    boxAlignment: Alignment = Alignment.Center
) {
    val textRedacted = text
    val strokeWidth = getStrokeWidth(textSize)
    Box(boxModifier, contentAlignment = boxAlignment) {
        Text(
            text = textRedacted, color = textColor,
            fontFamily = FontFamily(Font(R.font.monofont)),
            style = TextStyle(
                color = textColor,
                fontSize = textSize,
                fontWeight = FontWeight.Light,
                drawStyle = Fill,
                textAlign = textAlignment
            ),
        )
        if (textColor.toArgb() == strokeColor.toArgb()) {
            return@Box
        }
        Text(
            text = textRedacted, color = strokeColor,
            fontFamily = FontFamily(Font(R.font.monofont)),
            style = TextStyle(
                color = strokeColor,
                fontSize = textSize,
                fontWeight = FontWeight.Normal,
                drawStyle = Stroke(width = strokeWidth),
                textAlign = textAlignment
            ),
        )
    }
}


@Composable
fun TextClassic(
    text: String,
    textColor: Color,
    strokeColor: Color,
    textSize: TextUnit,
    boxModifier: Modifier,
    textAlignment: TextAlign = TextAlign.Center,
    boxAlignment: Alignment = Alignment.Center
) {
    TextCustom(text, Font(R.font.classic), textColor, strokeColor, textSize, boxModifier, textAlignment, boxAlignment)
}

@Composable
fun TextCustom(
    text: String,
    font: Font,
    textColor: Color,
    strokeColor: Color,
    textSize: TextUnit,
    boxModifier: Modifier,
    textAlignment: TextAlign = TextAlign.Center,
    boxAlignment: Alignment = Alignment.Center
) {
    val strokeWidth = getStrokeWidth(textSize)
    Box(boxModifier, contentAlignment = boxAlignment) {
        Text(
            text = text, color = textColor,
            fontFamily = FontFamily(font),
            style = TextStyle(
                color = textColor,
                fontSize = textSize,
                fontWeight = FontWeight.Light,
                drawStyle = Fill,
                textAlign = textAlignment
            )
        )
        if (textColor.toArgb() == strokeColor.toArgb()) {
            return@Box
        }
        Text(
            text = text, color = strokeColor,
            fontFamily = FontFamily(font),
            style = TextStyle(
                color = strokeColor,
                fontSize = textSize,
                fontWeight = FontWeight.Normal,
                drawStyle = Stroke(width = strokeWidth),
                textAlign = textAlignment
            )
        )
    }
}

@Composable
fun Modifier.clickableCancel(activity: MainActivity, block: () -> Unit): Modifier {
    return this.clickable { playCloseSound(activity); block() }
}
@Composable
fun Modifier.clickableOk(activity: MainActivity, block: () -> Unit): Modifier {
    return this.clickable { playClickSound(activity); block() }
}
@Composable
fun Modifier.clickableSelect(activity: MainActivity, block: () -> Unit): Modifier {
    return this.clickable { playSelectSound(activity); block() }
}


@Composable
fun MenuItemOpen(
    activity: MainActivity,
    name: String, back: String,
    goBack: () -> Unit,
    mainBlock: @Composable BoxWithConstraintsScope.() -> Unit
) {
    val scrollbarWidth = 4.dp.dpToPx()
    Scaffold(bottomBar = {
        Row(Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(getBackgroundColor(activity))
            .padding(start = 12.dp))
        {
            TextFallout(
                back,
                getTextColor(activity),
                getTextStrokeColor(activity),
                24.sp,
                Modifier
                    .background(getTextBackgroundColor(activity))
                    .clickableCancel(activity) {
                        goBack()
                    }
                    .padding(8.dp)
            )
            Box(
                Modifier.fillMaxWidth()
                    .background(getBackgroundColor(activity))
                    .padding(start = 8.dp, end = 12.dp)
                    .drawBehind {
                        drawPath(
                            Path().apply {
                                moveTo(0f, size.height / 2)
                                lineTo(size.width, size.height / 2)
                                lineTo(size.width, 0f)
                                lineTo(size.width, size.height)
                            },
                            color = getDividerColor(activity),
                            style = Stroke(width = scrollbarWidth),
                        )
                    }
            ) {
                TextFallout(
                    name,
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    24.sp,
                    Modifier
                        .align(Alignment.Center)
                        .background(getBackgroundColor(activity))
                        .padding(8.dp),
                )
            }
        }
    }) { innerPadding ->
        BoxWithConstraints(Modifier
            .padding(innerPadding)
            .background(getBackgroundColor(activity))
            .padding(start = 12.dp, end = 10.dp, top = 4.dp, bottom = 4.dp)
        ) {
            val lazyListState = rememberLazyListState()
            val state = rememberScrollAreaState(lazyListState)
            ScrollArea(state, Modifier.fillMaxSize()) {
                LazyColumn(
                    state = lazyListState, modifier = Modifier.fillMaxSize().padding(end = 8.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    item {
                        mainBlock()
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.TopEnd)
                        .padding(vertical = 4.dp)
                        .fillMaxHeight()
                        .background(getTrackColor(activity))
                        .width(4.dp)
                ) {
                    Thumb(Modifier.background(getKnobColor(activity)))
                }
            }
        }
    }
}