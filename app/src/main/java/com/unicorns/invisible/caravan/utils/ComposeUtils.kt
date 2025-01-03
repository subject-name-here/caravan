package com.unicorns.invisible.caravan.utils

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
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
import com.sebaslogen.resaca.rememberScoped
import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.isHorror
import com.unicorns.invisible.caravan.model.getCardName
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.save
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive


@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }


@Composable
fun ShowCard(activity: MainActivity, card: Card, modifier: Modifier, toModify: Boolean = true) {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(activity)
            .size(183, 256)
            .data("file:///android_asset/caravan_cards/${getCardName(card)}")
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
            .data("file:///android_asset/caravan_cards_back/${
                if (card.isAlt) card.back.getCardBackAltAsset() else card.back.getCardBackAsset()
            }")
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
    thickness: Dp = 8.pxToDp(),
    knobCornerRadius: Dp = 2.dp,
    trackCornerRadius: Dp = 4.dp,
    knobColor: Color,
    trackColor: Color,
    padding: Dp = 0.dp,
    visibleAlpha: Float = 1f,
    hiddenAlpha: Float = 0.75f,
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
fun SwitchCustomUsualBackground(
    activity: MainActivity,
    checked: () -> Boolean,
    onCheckedChange: (Boolean) -> Unit
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
    TextCustom(text, Font(R.font.monofont), textColor, strokeColor, textSize, contentAlignment, modifier, textAlign)
}

@Composable
private fun getHorrorString(): String {
    var cnt by rememberScoped { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (isActive) {
            delay(666L)
            cnt++
        }
    }

    return key(cnt) {
        listOf(
            "",
            stringResource(R.string.none),
            stringResource(R.string.close),
            stringResource(R.string.failure),
            stringResource(R.string.joker_name),
            stringResource(R.string.you_lose),
            stringResource(R.string.discard),
            stringResource(R.string.can_t_act),
            stringResource(R.string.check_back_to_menu_body),
            stringResource(R.string.transaction_failed),
            stringResource(R.string.you_don_t_have_a_ticket_on_you),
            stringResource(R.string.finish),
            stringResource(R.string.ch_end),
        ).random()
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
    val textRedacted = if (save.sixtyNineActive)
        text.replace("TOPS", "Bottoms", ignoreCase = true)
    else if (isHorror.value == true)
        getHorrorString()
    else
        text
    Box(modifier, contentAlignment = contentAlignment) {
        Text(
            text = textRedacted, color = textColor,
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
    val textRedacted = if (isHorror.value == true)
        AnnotatedString(getHorrorString())
    else
        text
    val strokeWidth = getStrokeWidth(textSize)
    Box(modifier, contentAlignment = contentAlignment) {
        Text(
            text = textRedacted, color = textColor,
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
            text = textRedacted, color = strokeColor,
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
fun TextClassic(
    text: String,
    textColor: Color,
    strokeColor: Color,
    textSize: TextUnit,
    contentAlignment: Alignment,
    modifier: Modifier,
    textAlign: TextAlign,
) {
    TextCustom(text, Font(R.font.classic), textColor, strokeColor, textSize, contentAlignment, modifier, textAlign)
}

@Composable
fun TextCustom(
    text: String,
    font: Font,
    textColor: Color,
    strokeColor: Color,
    textSize: TextUnit,
    contentAlignment: Alignment,
    modifier: Modifier,
    textAlign: TextAlign,
) {
    val strokeWidth = getStrokeWidth(textSize)
    val textRedacted = if (save.sixtyNineActive)
        text.replace("TOPS", "Bottoms", ignoreCase = true)
    else if (isHorror.value == true)
        getHorrorString()
    else
        text
    Box(modifier, contentAlignment = contentAlignment) {
        Text(
            text = textRedacted, color = textColor,
            fontFamily = FontFamily(font),
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
            text = textRedacted, color = strokeColor,
            fontFamily = FontFamily(font),
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
                Alignment.CenterStart,
                Modifier
                    .background(getTextBackgroundColor(activity))
                    .clickableCancel(activity) {
                        goBack()
                    }
                    .padding(8.dp),
                TextAlign.Start
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
                    Alignment.Center,
                    Modifier
                        .align(Alignment.Center)
                        .background(getBackgroundColor(activity))
                        .padding(8.dp),
                    TextAlign.Center
                )
            }
        }
    }) { innerPadding ->
        Box(Modifier.padding(innerPadding).background(getBackgroundColor(activity)).padding(horizontal = 12.dp - 4.pxToDp())) {
            // TODO: maybe include lazy column here??
            mainBlock()
        }
    }
}