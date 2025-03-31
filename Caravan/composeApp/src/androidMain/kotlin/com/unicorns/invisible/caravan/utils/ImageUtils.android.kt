package com.unicorns.invisible.caravan.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntSize
import caravan.composeapp.generated.resources.Res
import com.unicorns.invisible.caravan.activity
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
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
    // TODO: caching??
    var bytes by remember { mutableStateOf(ByteArray(0)) }
    LaunchedEffect(path) { bytes = Res.readBytes(path) }
    AsyncImage(
        model = ImageRequest.Builder(act)
            .data(bytes)
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