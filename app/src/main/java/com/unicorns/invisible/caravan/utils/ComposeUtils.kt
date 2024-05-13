package com.unicorns.invisible.caravan.utils

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R

@Composable
fun Modifier.scrollbar(
    state: LazyListState,
    horizontal: Boolean,
    alignEnd: Boolean = true,
    thickness: Dp = 4.dp,
    knobCornerRadius: Dp = 4.dp,
    trackCornerRadius: Dp = 2.dp,
    knobColor: Color = Color.Black,
    trackColor: Color = Color.White,
    padding: Dp = 0.dp,
    visibleAlpha: Float = 1f,
    hiddenAlpha: Float = 0.25f,
    fadeInAnimationDurationMs: Int = 150,
    fadeOutAnimationDurationMs: Int = 500,
    fadeOutAnimationDelayMs: Int = 1000,
): Modifier {
    check(thickness > 0.dp) { "Thickness must be a positive integer." }
    check(knobCornerRadius >= 0.dp) { "Knob corner radius must be greater than or equal to 0." }
    check(trackCornerRadius >= 0.dp) { "Track corner radius must be greater than or equal to 0." }
    check(hiddenAlpha <= visibleAlpha) { "Hidden alpha cannot be greater than visible alpha." }
    check(fadeInAnimationDurationMs >= 0) {
        "Fade in animation duration must be greater than or equal to 0."
    }
    check(fadeOutAnimationDurationMs >= 0) {
        "Fade out animation duration must be greater than or equal to 0."
    }
    check(fadeOutAnimationDelayMs >= 0) {
        "Fade out animation delay must be greater than or equal to 0."
    }

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
                        horizontal && alignEnd -> Offset(padding.toPx(), size.height - thickness.toPx())
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
                    cornerRadius = CornerRadius(x = trackCornerRadius.toPx(), y = trackCornerRadius.toPx()),
                )

                // Draw the knob
                drawRoundRect(
                    color = knobColor,
                    topLeft =
                    when {
                        // When the scrollbar is horizontal and aligned to the bottom:
                        horizontal && alignEnd -> Offset(knobPosition, size.height - thickness.toPx())
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
                    cornerRadius = CornerRadius(x = knobCornerRadius.toPx(), y = knobCornerRadius.toPx()),
                )
            }
        }
    }
}

@Composable
fun Modifier.caravanScrollbar(
    state: LazyListState,
    hasNewCardPlaceholder: Boolean = false,
    alignEnd: Boolean = true,
    thickness: Dp = 4.dp,
    knobCornerRadius: Dp = 4.dp,
    trackCornerRadius: Dp = 2.dp,
    knobColor: Color = Color.Black,
    trackColor: Color = Color.White,
    padding: Dp = 0.dp,
    visibleAlpha: Float = 1f,
    hiddenAlpha: Float = 0.4f,
    fadeInAnimationDurationMs: Int = 150,
    fadeOutAnimationDurationMs: Int = 500,
    fadeOutAnimationDelayMs: Int = 1000,
): Modifier {
    check(thickness > 0.dp) { "Thickness must be a positive integer." }
    check(knobCornerRadius >= 0.dp) { "Knob corner radius must be greater than or equal to 0." }
    check(trackCornerRadius >= 0.dp) { "Track corner radius must be greater than or equal to 0." }
    check(hiddenAlpha <= visibleAlpha) { "Hidden alpha cannot be greater than visible alpha." }
    check(fadeInAnimationDurationMs >= 0) {
        "Fade in animation duration must be greater than or equal to 0."
    }
    check(fadeOutAnimationDurationMs >= 0) {
        "Fade out animation duration must be greater than or equal to 0."
    }
    check(fadeOutAnimationDelayMs >= 0) {
        "Fade out animation delay must be greater than or equal to 0."
    }

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

        if (state.layoutInfo.totalItemsCount <= 1) {
            return@drawWithContent
        }

        if (state.isScrollInProgress || alpha > 0f) {
            val viewportSize = size.height - padding.toPx() * 2

            var totalSize = if (hasNewCardPlaceholder) 20.dp.toPx().toInt() else 0
            val totalCards = state.layoutInfo.totalItemsCount - (if (hasNewCardPlaceholder) 1 else 0)

            val cardSize = state.layoutInfo.visibleItemsInfo.maxOf { it.size }
            totalSize += cardSize + cardSize / 3 * (totalCards - 1)

            val viewportOffsetInFullListSpace =
                state.firstVisibleItemIndex * state.layoutInfo.visibleItemsInfo.first().size +
                        state.firstVisibleItemScrollOffset

            // Where we should render the knob in our composable.
            val knobPosition = (viewportSize / totalSize) * viewportOffsetInFullListSpace + padding.toPx()
            // How large should the knob be.
            val knobSize = viewportSize * viewportSize / totalSize

            if (viewportSize > totalSize) {
                return@drawWithContent
            }

            // Draw the track
            drawRoundRect(
                color = trackColor,
                topLeft =
                when {
                    // When the scrollbar is vertical and aligned to the end:
                    alignEnd -> Offset(size.width - thickness.toPx(), padding.toPx())
                    // When the scrollbar is vertical and aligned to the start:
                    else -> Offset(0f, padding.toPx())
                },
                size = Size(thickness.toPx(), size.height - padding.toPx() * 2),
                alpha = alpha,
                cornerRadius = CornerRadius(x = trackCornerRadius.toPx(), y = trackCornerRadius.toPx()),
            )

            // Draw the knob
            drawRoundRect(
                color = knobColor,
                topLeft =
                when {
                    // When the scrollbar is vertical and aligned to the end:
                    alignEnd -> Offset(size.width - thickness.toPx(), knobPosition)
                    // When the scrollbar is vertical and aligned to the start:
                    else -> Offset(0f, knobPosition)
                },
                size = Size(thickness.toPx(), knobSize),
                alpha = alpha,
                cornerRadius = CornerRadius(x = knobCornerRadius.toPx(), y = knobCornerRadius.toPx()),
            )
        }
    }
}


@Composable
fun CheckboxCustom(activity: MainActivity, checked: () -> Boolean, onCheckedChange: (Boolean) -> Unit, enabled: () -> Boolean) {
    Checkbox(checked = checked(), onCheckedChange = onCheckedChange, colors = CheckboxColors(
        checkedCheckmarkColor = Color(activity.getColor(R.color.colorPrimaryDark)),
        uncheckedCheckmarkColor = Color.Transparent,
        checkedBoxColor = Color(activity.getColor(R.color.colorPrimary)),
        uncheckedBoxColor = Color.Transparent,
        disabledCheckedBoxColor = Color.Red,
        disabledUncheckedBoxColor = Color.Red,
        disabledIndeterminateBoxColor = Color.Red,
        checkedBorderColor = Color(activity.getColor(R.color.colorPrimaryDark)),
        uncheckedBorderColor = Color(activity.getColor(R.color.colorPrimaryDark)),
        disabledBorderColor = Color.Red,
        disabledUncheckedBorderColor = Color.Red,
        disabledIndeterminateBorderColor = Color.Red,
    ), enabled = enabled())
}