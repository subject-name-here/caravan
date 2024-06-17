package com.unicorns.invisible.caravan

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.unicorns.invisible.caravan.Style.ALASKA_FRONTIER
import com.unicorns.invisible.caravan.Style.DESERT
import com.unicorns.invisible.caravan.Style.MADRE_ROJA
import com.unicorns.invisible.caravan.Style.NEW_WORLD
import com.unicorns.invisible.caravan.Style.OLD_WORLD
import com.unicorns.invisible.caravan.Style.PIP_BOY
import com.unicorns.invisible.caravan.Style.PIP_GIRL
import com.unicorns.invisible.caravan.Style.SIERRA_MADRE
import com.unicorns.invisible.caravan.Style.VAULT_21
import com.unicorns.invisible.caravan.Style.VAULT_22
import com.unicorns.invisible.caravan.utils.dpToPx
import com.unicorns.invisible.caravan.utils.pxToDp
import kotlin.random.Random


enum class Style(val styleName: String, val price: Int) {
    DESERT("Desert", 0),
    ALASKA_FRONTIER("Frontier of Anchorage", 500),
    PIP_BOY("Pip-boy", 0),
    PIP_GIRL("Pip-Girl", 2500),
    OLD_WORLD("Old World", 2000),
    NEW_WORLD("New World", 3000),
    SIERRA_MADRE("Sierra Madre", 2000),
    MADRE_ROJA("Madre Roja", 3000),
    VAULT_21("Vault 21", 2100),
    VAULT_22("Vault 22", 2200);
}


@Composable
fun Modifier.getTableBackground(style: Style): Modifier {
    return paint(
        painterResource(id = when (style) {
            DESERT -> R.drawable.table_wood
            ALASKA_FRONTIER -> R.drawable.table_black
            PIP_BOY -> R.drawable.table_blue
            PIP_GIRL -> R.drawable.table_black // Future pink-ish
            OLD_WORLD -> R.drawable.table_amber
            NEW_WORLD -> R.drawable.table_wood
            SIERRA_MADRE -> R.drawable.table_brown
            MADRE_ROJA -> R.drawable.table_brown
            VAULT_21 -> R.drawable.table_blue
            VAULT_22 -> R.drawable.table_green
        }),
        contentScale = ContentScale.Crop, // TODO!
        colorFilter = if (style != PIP_GIRL) {
            ColorFilter.colorMatrix(ColorMatrix())
        } else {
            ColorFilter.colorMatrix(ColorMatrix().apply {
                timesAssign(
                    ColorMatrix(
                        floatArrayOf(
                            2f, 0f, 0f, 0f, 0f,
                            0f, 1.75f, 0f, 0f, 0f,
                            0f, 0f, 1.75f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
            })
        }
    )
}

fun getStyleCities(style: Style): List<String> {
    return when (style) {
        ALASKA_FRONTIER -> listOf("ALPINE", "KOTZEBUE", "NEWTOK", "AKUTAN", "ANCHORAGE", "SCAGWAY")
        OLD_WORLD, SIERRA_MADRE -> listOf("L.A.", "REDDING", "BLACK ROCK", "SAN DIEGO", "RENO", "ED. AFB")
        MADRE_ROJA -> listOf("YOU", "CAN", "NEVER", "LEAVE", "SIERRA", "MADRE")
        VAULT_21, -> listOf("LONG 15", "PRIMM", "NOVAK", "188", "NEW VEGAS", "HOOVER DAM")
        VAULT_22 -> listOf("GRRRRR", "BRRRR", "VRRR", "MRRRRRR", "HRRRR", "DRRR")
        else -> listOf("BONEYARD", "REDDING", "VAULT CITY", "DAYGLOW", "NEW RENO", "THE HUB")
    }
}

@Composable
fun BoxWithConstraintsScope.StylePicture(activity: MainActivity, style: Style, key: Int, width: Int, height: Int) {
    val rand = Random(key)
    when (style) {
        OLD_WORLD -> {
            val prefix = "file:///android_asset/menu_items/"
            val painterToColorFilter = listOf(
                rememberAsyncImagePainter(
                    ImageRequest.Builder(activity)
                        .size(256, 256)
                        .data(prefix + "old_world/nuka-coladecal.png")
                        .decoderFactory(SvgDecoder.Factory())
                        .build()
                ) to null,
                rememberAsyncImagePainter(
                    ImageRequest.Builder(activity)
                        .size(256, 256)
                        .data(prefix + "old_world/holyhandgrenades.png")
                        .decoderFactory(SvgDecoder.Factory())
                        .build(),
                ) to ColorFilter.tint(color = Color.Black)
            ).random(Random(key))

            Image(
                painter = painterToColorFilter.first,
                contentDescription = "",
                Modifier.align(Alignment.Center)
                    .rotate(-30f + rand.nextFloat() * 60f)
                    .offset {
                        IntOffset(
                            (0..(width / 4)).random(rand),
                            (-height / 4 + (0..height / 2).random(rand))
                        )
                    },
                colorFilter = painterToColorFilter.second
            )
        }
        else -> {}
    }
}