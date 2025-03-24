package com.unicorns.invisible.caravan.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.CardBack.GOMORRAH
import com.unicorns.invisible.caravan.model.CardBack.STANDARD
import com.unicorns.invisible.caravan.model.CardBack.TOPS
import com.unicorns.invisible.caravan.model.CardBack.ULTRA_LUXE
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
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlin.random.Random
import kotlin.random.nextInt


@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }
@Composable
fun Dp.dpToSp() = with(LocalDensity.current) { this@dpToSp.toSp() }
@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }



fun getFilter(back: CardBack, backNumber: Int): ColorFilter {
    if (backNumber == 0) {
        return ColorFilter.colorMatrix(ColorMatrix())
    }
    return when (back) {
        STANDARD -> getBackFilter(back, backNumber)

        TOPS -> ColorFilter.colorMatrix(ColorMatrix().apply {
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

        ULTRA_LUXE -> ColorFilter.colorMatrix(ColorMatrix().apply {
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

        GOMORRAH -> ColorFilter.colorMatrix(ColorMatrix().apply {
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

        else -> ColorFilter.colorMatrix(ColorMatrix())
    }
}


fun getBackFilter(back: CardBack, backNumber: Int): ColorFilter {
    return when (back) {
        STANDARD ->
            when (backNumber) {
                1 -> {
                    ColorFilter.colorMatrix(ColorMatrix(floatArrayOf(
                        0f, 1f, 0f, 0f, 0f,
                        1f, 0f, 0f, 0f, 0f,
                        0f, 0f, 1f, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f
                    )))
                }
                2 -> {
                    ColorFilter.colorMatrix(ColorMatrix(floatArrayOf(
                        0f, 0f, 1f, 0f, 0f,
                        0f, 1f, 0f, 0f, 0f,
                        1f, 0f, 0f, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f
                    )))
                }
                3 -> {
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
                4 -> {
                    ColorFilter.colorMatrix(ColorMatrix(floatArrayOf(
                        1f, 0f, 0f, 0f, 0f,
                        1f, 0f, 0f, 0f, 0f,
                        0f, 0f, 1f, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f
                    )))
                }
                else -> {
                    ColorFilter.colorMatrix(ColorMatrix())
                }
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
                getFilter(card.getBack(), card.getBackNumber())
            } else {
                ColorFilter.colorMatrix(ColorMatrix())
            },
            contentScale = FixedScale(scale),
            alignment = BiasAlignment(-1f, -1f)
        )
        if (card is CardWithPrice && card.getBack() == CardBack.FNV_FACTION && card.getBackNumber() == 0) {
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
                                modifier = Modifier.scale(0.75f).offset { IntOffset(0, 120) * scale }.rotate(rotation.toFloat()),
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
                        Suit.DIAMONDS -> {
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
                        Suit.SPADES -> {
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
                        val rand = Random(22229 + card.rank.value * 113 + card.suit.ordinal)
                        SuitToStamp(card.suit, card.rank.value * 133)
                    }
                    is CardJoker -> {
                        val rand = Random(22229 + card.rank.value * 117 + card.number.n)
                        // TODO
                    }
                    is CardNumber -> {
                        SuitToStamp(card.suit, card.rank.value * 131)
                    }
                }
            }
        }
    }
}

@Composable
fun ShowCardBack(activity: MainActivity, card: Card, modifier: Modifier, scale: Float = 1f) {
    val asset = when (card) {
        is CardWithPrice -> card.getBack().nameIdWithBackFileName[card.getBackNumber()].second
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
            getBackFilter(card.getBack(), card.getBackNumber())
        } else {
            ColorFilter.colorMatrix(ColorMatrix())
        },
        alignment = BiasAlignment(-1f, -1f)
    )
}


// TODO: doesn't work everywhere from the colors pov
@Composable
fun Modifier.scrollbar(
    state: LazyListState,
    horizontal: Boolean = false,
    alignEnd: Boolean = true,
    thickness: Int = 8,
    knobColor: Color,
    trackColor: Color,
): Modifier {
    return drawWithContent {
        drawContent()

        state.layoutInfo.visibleItemsInfo.firstOrNull()?.let { firstVisibleItem ->
            val viewportSize = if (horizontal) {
                size.width
            } else {
                size.height
            }

            val firstItemSize = firstVisibleItem.size
            val estimatedFullListSize = firstItemSize * state.layoutInfo.totalItemsCount - 1

            if (viewportSize > estimatedFullListSize) {
                return@drawWithContent
            }

            val viewportOffsetInFullListSpace =
                state.firstVisibleItemIndex * firstItemSize + state.firstVisibleItemScrollOffset

            // Where we should render the knob in our composable.
            val knobPosition = (viewportSize / estimatedFullListSize) * viewportOffsetInFullListSpace
            // How large should the knob be.
            val knobSize = (viewportSize / estimatedFullListSize) * viewportSize

            // Draw the track
            drawRoundRect(
                color = trackColor,
                topLeft = when {
                    // When the scrollbar is horizontal and aligned to the bottom:
                    horizontal && alignEnd -> Offset(0f, size.height - thickness)
                    // When the scrollbar is horizontal and aligned to the top:
                    horizontal && !alignEnd -> Offset(0f, 0f)
                    // When the scrollbar is vertical and aligned to the end:
                    alignEnd -> Offset(size.width - thickness, 0f)
                    // When the scrollbar is vertical and aligned to the start:
                    else -> Offset(0f, 0f)
                },
                size = if (horizontal) {
                    Size(size.width, thickness.toFloat())
                } else {
                    Size(thickness.toFloat(), size.height)
                },
            )

            // Draw the knob
            drawRoundRect(
                color = knobColor,
                topLeft =
                    when {
                        // When the scrollbar is horizontal and aligned to the bottom:
                        horizontal && alignEnd -> Offset(
                            knobPosition,
                            size.height - thickness * 3 / 4
                        )
                        // When the scrollbar is horizontal and aligned to the top:
                        horizontal && !alignEnd -> Offset(knobPosition, thickness / 4f)
                        // When the scrollbar is vertical and aligned to the end:
                        alignEnd -> Offset(size.width - thickness * 3 / 4, knobPosition)
                        // When the scrollbar is vertical and aligned to the start:
                        else -> Offset(thickness / 4f, knobPosition)
                    },
                size = if (horizontal) {
                    Size(knobSize, thickness.toFloat() / 2)
                } else {
                    Size(thickness.toFloat() / 2, knobSize)
                },
            )
        }
    }
}

@Composable
fun Modifier.scrollbar(
    state: LazyGridState,
    horizontal: Boolean = false,
    alignEnd: Boolean = true,
    thickness: Int = 8,
    knobColor: Color,
    trackColor: Color,
): Modifier {
    return drawWithContent {
        drawContent()

        state.layoutInfo.visibleItemsInfo.firstOrNull()?.let { firstVisibleItem ->
            val viewportSize = if (horizontal) {
                size.width
            } else {
                size.height
            }

            val firstItemSize = if (horizontal) firstVisibleItem.size.width else firstVisibleItem.size.height
            val estimatedFullListSize = firstItemSize * state.layoutInfo.totalItemsCount - 1

            if (viewportSize > estimatedFullListSize) {
                return@drawWithContent
            }

            val viewportOffsetInFullListSpace =
                state.firstVisibleItemIndex * firstItemSize + state.firstVisibleItemScrollOffset

            // Where we should render the knob in our composable.
            val knobPosition = (viewportSize / estimatedFullListSize) * viewportOffsetInFullListSpace
            // How large should the knob be.
            val knobSize = (viewportSize / estimatedFullListSize) * viewportSize

            // Draw the track
            drawRoundRect(
                color = trackColor,
                topLeft = when {
                    // When the scrollbar is horizontal and aligned to the bottom:
                    horizontal && alignEnd -> Offset(0f, size.height - thickness)
                    // When the scrollbar is horizontal and aligned to the top:
                    horizontal && !alignEnd -> Offset(0f, 0f)
                    // When the scrollbar is vertical and aligned to the end:
                    alignEnd -> Offset(size.width - thickness, 0f)
                    // When the scrollbar is vertical and aligned to the start:
                    else -> Offset(0f, 0f)
                },
                size = if (horizontal) {
                    Size(size.width, thickness.toFloat())
                } else {
                    Size(thickness.toFloat(), size.height)
                },
            )

            // Draw the knob
            drawRoundRect(
                color = knobColor,
                topLeft =
                    when {
                        // When the scrollbar is horizontal and aligned to the bottom:
                        horizontal && alignEnd -> Offset(
                            knobPosition,
                            size.height - thickness * 3 / 4
                        )
                        // When the scrollbar is horizontal and aligned to the top:
                        horizontal && !alignEnd -> Offset(knobPosition, thickness / 4f)
                        // When the scrollbar is vertical and aligned to the end:
                        alignEnd -> Offset(size.width - thickness * 3 / 4, knobPosition)
                        // When the scrollbar is vertical and aligned to the start:
                        else -> Offset(thickness / 4f, knobPosition)
                    },
                size = if (horizontal) {
                    Size(knobSize, thickness.toFloat() / 2)
                } else {
                    Size(thickness.toFloat() / 2, knobSize)
                },
            )
        }
    }
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
    mainBlock: @Composable () -> Unit
) {
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
                            style = Stroke(width = 8f),
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
        Box(Modifier
            .padding(innerPadding)
            .background(getBackgroundColor(activity))
            .padding(horizontal = 12.dp - 4.pxToDp(), vertical = 4.dp)
        ) {
            mainBlock()
        }
    }
}