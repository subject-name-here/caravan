package com.unicorns.invisible.caravan.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntSize
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.unicorns.invisible.caravan.activity
import org.jetbrains.compose.resources.ExperimentalResourceApi


@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun ShowImageFromPath(
    path: String,
    size: IntSize,
    modifier: Modifier,
    colorFilter: ColorFilter,
    scale: ContentScale,
    alignment: Alignment
) {
    val act = activity ?: return
    AsyncImage(
        model = ImageRequest.Builder(act)
            .data("file:///android_asset/${path.removePrefix("files/")}")
            .size(size.width, size.height)
            .decoderFactory(SvgDecoder.Factory())
            .build(),
        contentDescription = "",
        modifier,
        colorFilter = colorFilter,
        contentScale = scale,
        alignment = alignment
    )
}