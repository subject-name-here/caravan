package com.unicorns.invisible.caravan.utils

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.getCardName
import com.unicorns.invisible.caravan.model.primitives.Card


@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }


@Composable
fun ShowCard(activity: MainActivity, card: Card, modifier: Modifier, toModify: Boolean = true) {
    val cardName = getCardName(card, card.isAlt)
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(activity)
            .size(183, 256)
            .data("file:///android_asset/caravan_cards/$cardName")
            .decoderFactory(SvgDecoder.Factory())
            .build()
    )
    Image(
        painter = painter,
        contentDescription = "",
        modifier = if (toModify) modifier.clip(RoundedCornerShape(5)) else modifier,
        colorFilter = card.back.getFilter(card.isAlt)
    )
}

@Composable
fun ShowCardBack(activity: MainActivity, card: Card, modifier: Modifier) {
    val painter2 = rememberAsyncImagePainter(
        ImageRequest.Builder(activity)
            .size(183, 256)
            .data("file:///android_asset/caravan_cards_back/${if (card.isAlt) card.back.getCardBackAltAsset() else card.back.getCardBackAsset()}")
            .decoderFactory(SvgDecoder.Factory())
            .build()
    )
    Image(
        painter = painter2,
        contentDescription = "",
        modifier.clip(RoundedCornerShape(5)),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun Modifier.scrollbar(
    state: LazyListState,
    horizontal: Boolean,
    alignEnd: Boolean = true,
    thickness: Dp = 4.dp,
    knobCornerRadius: Dp = 4.dp,
    trackCornerRadius: Dp = 2.dp,
    knobColor: Color,
    trackColor: Color,
    padding: Dp = 0.dp,
    visibleAlpha: Float = 1f,
    hiddenAlpha: Float = 0.8f,
    fadeInAnimationDurationMs: Int = 150,
    fadeOutAnimationDurationMs: Int = 500,
    fadeOutAnimationDelayMs: Int = 1000,
): Modifier {
    val targetAlpha =
        if (state.isScrollInProgress) {
            visibleAlpha
        } else {
            hiddenAlpha
        }
    val animationDurationMs =
        if (state.isScrollInProgress) {
            fadeInAnimationDurationMs
        } else {
            fadeOutAnimationDurationMs
        }
    val animationDelayMs =
        if (state.isScrollInProgress) {
            0
        } else {
            fadeOutAnimationDelayMs
        }

    val alpha by
    animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(delayMillis = animationDelayMs, durationMillis = animationDurationMs)
    )

    return drawWithContent {
        drawContent()

        state.layoutInfo.visibleItemsInfo.firstOrNull()?.let { firstVisibleItem ->
            if (state.isScrollInProgress || alpha > 0f) {
                val viewportSize =
                    if (horizontal) {
                        size.width
                    } else {
                        size.height
                    } - padding.toPx() * 2

                val firstItemSize = firstVisibleItem.size
                val estimatedFullListSize = firstItemSize * state.layoutInfo.totalItemsCount

                if (viewportSize > estimatedFullListSize) {
                    return@drawWithContent
                }

                val viewportOffsetInFullListSpace =
                    state.firstVisibleItemIndex * firstItemSize + state.firstVisibleItemScrollOffset

                // Where we should render the knob in our composable.
                val knobPosition =
                    (viewportSize / estimatedFullListSize) * viewportOffsetInFullListSpace + padding.toPx()
                // How large should the knob be.
                val knobSize = (viewportSize / estimatedFullListSize) * viewportSize

                // Draw the track
                drawRoundRect(
                    color = trackColor,
                    topLeft =
                    when {
                        // When the scrollbar is horizontal and aligned to the bottom:
                        horizontal && alignEnd -> Offset(
                            padding.toPx(),
                            size.height - thickness.toPx()
                        )
                        // When the scrollbar is horizontal and aligned to the top:
                        horizontal && !alignEnd -> Offset(padding.toPx(), 0f)
                        // When the scrollbar is vertical and aligned to the end:
                        alignEnd -> Offset(size.width - thickness.toPx(), padding.toPx())
                        // When the scrollbar is vertical and aligned to the start:
                        else -> Offset(0f, padding.toPx())
                    },
                    size =
                    if (horizontal) {
                        Size(size.width - padding.toPx() * 2, thickness.toPx())
                    } else {
                        Size(thickness.toPx(), size.height - padding.toPx() * 2)
                    },
                    alpha = alpha,
                    cornerRadius = CornerRadius(
                        x = trackCornerRadius.toPx(),
                        y = trackCornerRadius.toPx()
                    ),
                )

                // Draw the knob
                drawRoundRect(
                    color = knobColor,
                    topLeft =
                    when {
                        // When the scrollbar is horizontal and aligned to the bottom:
                        horizontal && alignEnd -> Offset(
                            knobPosition,
                            size.height - thickness.toPx()
                        )
                        // When the scrollbar is horizontal and aligned to the top:
                        horizontal && !alignEnd -> Offset(knobPosition, 0f)
                        // When the scrollbar is vertical and aligned to the end:
                        alignEnd -> Offset(size.width - thickness.toPx(), knobPosition)
                        // When the scrollbar is vertical and aligned to the start:
                        else -> Offset(0f, knobPosition)
                    },
                    size =
                    if (horizontal) {
                        Size(knobSize, thickness.toPx())
                    } else {
                        Size(thickness.toPx(), knobSize)
                    },
                    alpha = alpha,
                    cornerRadius = CornerRadius(
                        x = knobCornerRadius.toPx(),
                        y = knobCornerRadius.toPx()
                    ),
                )
            }
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

// Both these Slider and Switch have colors chosen out of the assumption that the background color is Selection color

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
fun SwitchCustom(
    activity: MainActivity,
    checked: () -> Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Switch(
        checked = checked(), onCheckedChange = onCheckedChange, colors = SwitchColors(
            checkedThumbColor = getSwitchThumbColor(activity),
            checkedTrackColor = getSwitchTrackColor(activity),
            checkedBorderColor = Color.Transparent,
            checkedIconColor = Color.Transparent,
            uncheckedThumbColor = getSwitchThumbColor(activity),
            uncheckedTrackColor = getSwitchTrackColor(activity),
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
        )
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
    contentAlignment: Alignment,
    modifier: Modifier,
    textAlign: TextAlign,
) {
    val strokeWidth = getStrokeWidth(textSize)
    Box(modifier, contentAlignment = contentAlignment) {
        Text(
            text = text, color = textColor,
            fontFamily = FontFamily(Font(R.font.monofont)),
            style = TextStyle(
                color = textColor,
                fontSize = textSize,
                fontWeight = FontWeight.Light,
                drawStyle = Fill
            ),
            textAlign = textAlign
        )
        if (textColor.toArgb() == strokeColor.toArgb()) {
            return@Box
        }
        Text(
            text = text, color = strokeColor,
            fontFamily = FontFamily(Font(R.font.monofont)),
            style = TextStyle(
                color = strokeColor,
                fontSize = textSize,
                fontWeight = FontWeight.Normal,
                drawStyle = Stroke(width = strokeWidth)
            ),
            textAlign = textAlign
        )
    }
}

@Composable
fun TextSymbola(
    text: String,
    textColor: Color,
    textSize: TextUnit,
    contentAlignment: Alignment,
    modifier: Modifier,
    textAlign: TextAlign,
) {
    Box(modifier, contentAlignment = contentAlignment) {
        Text(
            text = text, color = textColor,
            fontFamily = FontFamily(Font(R.font.symbola)),
            style = TextStyle(
                color = textColor,
                fontSize = textSize,
                drawStyle = Fill
            ),
            textAlign = textAlign
        )
    }
}

@Composable
fun TextFallout(
    text: AnnotatedString,
    textColor: Color,
    strokeColor: Color,
    textSize: TextUnit,
    contentAlignment: Alignment,
    modifier: Modifier,
    textAlign: TextAlign,
) {
    val strokeWidth = getStrokeWidth(textSize)
    Box(modifier, contentAlignment = contentAlignment) {
        Text(
            text = text, color = textColor,
            fontFamily = FontFamily(Font(R.font.monofont)),
            style = TextStyle(
                color = textColor,
                fontSize = textSize,
                fontWeight = FontWeight.Light,
                drawStyle = Fill
            ),
            textAlign = textAlign
        )
        if (textColor.toArgb() == strokeColor.toArgb()) {
            return@Box
        }
        Text(
            text = text, color = strokeColor,
            fontFamily = FontFamily(Font(R.font.monofont)),
            style = TextStyle(
                color = strokeColor,
                fontSize = textSize,
                fontWeight = FontWeight.Normal,
                drawStyle = Stroke(width = strokeWidth)
            ),
            textAlign = textAlign
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