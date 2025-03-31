package com.unicorns.invisible.caravan.utils

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntSize
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource


expect @Composable fun ShowImageFromPath(
    path: String,
    size: IntSize,
    modifier: Modifier,
    colorFilter: ColorFilter,
    scale: ContentScale,
    alignment: Alignment
)

@Composable
fun ShowImageFromDrawable(
    drawable: DrawableResource,
    modifier: Modifier,
) {
    Image(
        painterResource(drawable),
        contentDescription = "",
        modifier,
    )
}