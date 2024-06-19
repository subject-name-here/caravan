package com.unicorns.invisible.caravan

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.FixedScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import coil.size.Scale
import coil.size.Size
import coil.size.pxOrElse
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
import com.unicorns.invisible.caravan.save.saveOnGD
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.dpToPx
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.playFanfares
import com.unicorns.invisible.caravan.utils.playNoBeep
import com.unicorns.invisible.caravan.utils.playYesBeep
import com.unicorns.invisible.caravan.utils.pxToDp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.min
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
        colorFilter = if (style == PIP_GIRL) {
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
        } else if (style == ALASKA_FRONTIER) {
            ColorFilter.colorMatrix(ColorMatrix().apply {
                timesAssign(
                    ColorMatrix(
                        floatArrayOf(
                            3f, 0f, 0f, 0f, 0f,
                            0f, 3f, 0f, 0f, 0f,
                            0f, 0f, 3f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
            })
        } else {
            ColorFilter.colorMatrix(ColorMatrix())
        }
    )
}

fun getStyleCities(style: Style): List<String> {
    return when (style) {
        ALASKA_FRONTIER -> listOf("ALPINE", "KOTZEBUE", "NEWTOK", "AKUTAN", "ANCHORAGE", "SCAGWAY")
        OLD_WORLD, SIERRA_MADRE -> listOf("L.A.", "REDDING", "BLACK ROCK", "SAN DIEGO", "RENO", "ED. AFB")
        MADRE_ROJA -> listOf("YOU", "CAN", "NEVER", "LEAVE", "SIERRA", "MADRE")
        VAULT_21, -> listOf("LONG 15", "PRIMM", "NOVAK", "188", "NEW VEGAS", "HOOVER DAM")
        VAULT_22 -> listOf("GRRRRR", "BRRRR", "VRRR", "MRR", "HRRRRRRR", "DRRRRRR")
        else -> listOf("BONEYARD", "REDDING", "VAULT CITY", "DAYGLOW", "NEW RENO", "THE HUB")
    }
}

@Composable
fun BoxWithConstraintsScope.StylePicture(activity: MainActivity, style: Style, key: Int, width: Int, height: Int) {
    // TODO: jumping pictures!!! put size everywhere!!
    val prefix = "file:///android_asset/menu_items/"
    val rand = Random(key)
    when (style) {
        OLD_WORLD -> {
            val flagPainter = listOf(
                rememberAsyncImagePainter(
                    ImageRequest.Builder(activity)
                        .data(prefix + "old_world/china.png")
                        .build()
                    ),
                rememberAsyncImagePainter(
                    ImageRequest.Builder(activity)
                        .data(prefix + "old_world/usa.png")
                        .build()
                ),
                rememberAsyncImagePainter(
                    ImageRequest.Builder(activity)
                        .data(prefix + "old_world/nevada_flag.png")
                        .build()
                )
            ).random(rand)
            val painter3 = rememberAsyncImagePainter(
                ImageRequest.Builder(activity)
                    .size(512, 256)
                    .data(prefix + listOf(
                        "old_world/repconn_poster1.jpg",
                        "old_world/repconn_poster2.jpg",
                        "old_world/repconnposter03.jpg",
                    ).random(rand))
                    .build()
            )


            Row(Modifier.fillMaxWidth().align(Alignment.BottomEnd)) {
                Image(
                    painter = painter3,
                    contentDescription = "",
                    Modifier.fillMaxWidth().padding(end = 8.dp, bottom = 52.dp),
                    alignment = Alignment.BottomEnd,
                    contentScale = ContentScale.Inside
                )
            }
            Row(Modifier.fillMaxWidth().align(Alignment.TopEnd)) {
                Image(
                    painter = flagPainter,
                    contentDescription = "",
                    modifier = Modifier.fillMaxWidth().padding(top = 48.dp, end = 8.dp),
                    contentScale = ContentScale.Inside,
                    alignment = Alignment.TopEnd
                )
            }

            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(activity)
                        .size(238, 207)
                        .data(prefix + "old_world/decal1.png")
                        .decoderFactory(SvgDecoder.Factory())
                        .build()
                ),
                contentDescription = "",
                Modifier.align(Alignment.Center)
                    .rotate(-30f + rand.nextFloat() * 60f)
                    .offset {
                        // TODO: animation!
                        IntOffset(
                            (0..(width / 4)).random(rand),
                            (-height / 4 + (0..height / 2).random(rand))
                        )
                    }.padding(vertical = 48.dp),
            )
        }
        VAULT_22 -> {
            val painter = rememberAsyncImagePainter(
                ImageRequest.Builder(activity)
                    .size(512, 356)
                    .data(prefix + "vault_22/v22sign.png")
                    .build()
            )
            Row(Modifier.fillMaxWidth().align(Alignment.BottomEnd)) {
                Image(
                    painter = painter,
                    contentDescription = "",
                    Modifier.fillMaxWidth().padding(end = 8.dp, bottom = 48.dp),
                    alignment = Alignment.BottomEnd,
                    contentScale = ContentScale.Inside
                )
            }

            val mossUp = listOf(
                "vault_22/v22moss2.png" to Size(136, 76),
                "vault_22/v22moss4.png" to Size(139, 139),
                "vault_22/v22moss5.png" to Size(141, 73),
                "vault_22/v22moss3.png" to Size(187, 151),
            )

            var currentWidth = 0
            Row(Modifier.fillMaxWidth().offset((-5).dp, 0.dp), horizontalArrangement = Arrangement.Start) {
                while (currentWidth < width) {
                    val curMossUp = mossUp.random(rand)
                    val painter2 = rememberAsyncImagePainter(
                        ImageRequest.Builder(activity)
                            .size(curMossUp.second)
                            .data(prefix + curMossUp.first)
                            .build()
                    )
                    Image(
                        painter = painter2,
                        contentDescription = "",
                        modifier = Modifier
                            .rotate(if (curMossUp.first == "vault_22/v22moss3.png") 90f else 0f)
                            .offset {
                                if (curMossUp.first == "vault_22/v22moss3.png")
                                    IntOffset(-20, 0)
                                else
                                    IntOffset(0, -5)
                            },
                        contentScale = ContentScale.Inside,
                        alignment = Alignment.TopStart
                    )
                    currentWidth += curMossUp.second.width.pxOrElse { 0 }
                }
            }
        }
        SIERRA_MADRE -> {
            val painter = rememberAsyncImagePainter(
                ImageRequest.Builder(activity)
                    .size(512, 512)
                    .data(prefix + "sierra_madre/mosaic.png")
                    .build()
            )
            val painterLogo = rememberAsyncImagePainter(
                ImageRequest.Builder(activity)
                    .size(512, 512)
                    .data(prefix + listOf("sierra_madre/logo1.png", "sierra_madre/logo2.png").random(rand))
                    .build()
            )
            Row(Modifier.fillMaxWidth().align(Alignment.CenterEnd)) {
                Box(Modifier.weight(1f))
                Column(
                    Modifier.weight(1f).padding(top = 48.dp, end = 8.dp, bottom = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterLogo,
                        contentDescription = "",
                        Modifier,
                        alignment = Alignment.CenterEnd,
                        contentScale = ContentScale.Inside
                    )
                    Image(
                        painter = painter,
                        contentDescription = "",
                        Modifier,
                        alignment = Alignment.CenterEnd,
                        contentScale = ContentScale.Inside
                    )
                }
            }

            val painterFrame = rememberAsyncImagePainter(
                ImageRequest.Builder(activity)
                    .data(prefix + "sierra_madre/frame.png")
                    .build()
            )
            Box(
                Modifier.fillMaxHeight().offset { IntOffset(12.dp.roundToPx() + 8, -(12.dp.roundToPx() + 8)) },
                contentAlignment = Alignment.BottomStart
            ) {
                Image(
                    painter = painterFrame,
                    contentDescription = "",
                    Modifier.height(36.dp).width(36.dp),
                    alignment = Alignment.BottomStart,
                    contentScale = ContentScale.None
                )
            }

            val painterPostcard1 = rememberAsyncImagePainter(
                ImageRequest.Builder(activity)
                    .data(prefix + "sierra_madre/postcard1.png")
                    .build()
            )
            val painterPostcard2 = rememberAsyncImagePainter(
                ImageRequest.Builder(activity)
                    .data(prefix + "sierra_madre/postcard2.png")
                    .build()
            )
            val painter44 = listOf(painterPostcard1, painterPostcard2).random(rand)

            Image(
                painter = painter44,
                contentDescription = "",
                Modifier.rotate(-40f + rand.nextFloat() * 80f),
                alignment = Alignment.TopStart,
                contentScale = ContentScale.None
            )
        }
        MADRE_ROJA -> {
            val painterDrip = rememberAsyncImagePainter(
                ImageRequest.Builder(activity)
                    .data(prefix + "madre_roja/drip.png")
                    .build()
            )
            Image(
                painter = painterDrip,
                contentDescription = "",
                Modifier.padding(start = 18.dp),
                alignment = Alignment.TopStart,
                contentScale = ContentScale.None
            )

            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(activity)
                        .data(prefix + "madre_roja/graffiti_${(1..2).random(rand)}.png")
                        .build()
                ),
                contentDescription = "",
                modifier = Modifier.fillMaxWidth().padding(end = 18.dp, top = 18.dp),
                contentScale = ContentScale.Inside,
                alignment = Alignment.TopEnd,
            )

            Row(Modifier.fillMaxWidth().align(Alignment.CenterEnd)) {
                Box(Modifier.weight(1f))
                Column(
                    Modifier.weight(1f).padding(top = 48.dp, end = 8.dp, bottom = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(Modifier.weight(1f)) {  }
                    Box(Modifier.weight(1f)) {  }
                    val graffiti = if (rand.nextBoolean()) {
                        "madre_roja/let_go.png"
                    } else {
                        listOf(
                            "madre_roja/madre_0.png",
                            "madre_roja/madre_1.png",
                            "madre_roja/madre_2.png",
                            "madre_roja/madre_3.png",
                            "madre_roja/madre_4.png",
                            "madre_roja/madre_5.png",
                            "madre_roja/madre_6.png",
                            "madre_roja/madre_7.png",
                            "madre_roja/madre_8.png",
                            "madre_roja/madre_9.png",
                            "madre_roja/madre_10.png",
                            "madre_roja/madre_11.png",
                        ).random(rand)
                    }
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(activity)
                                .data(prefix + graffiti)
                                .build()
                        ),
                        contentDescription = "",
                        Modifier.weight(2f).scale(2f),
                        alignment = Alignment.CenterEnd,
                        contentScale = ContentScale.Inside
                    )
                    Box(Modifier.weight(1f)) {  }
                    Box(Modifier.weight(1f)) {  }
                }
            }
        }
        DESERT -> {
            val painterMain = rememberAsyncImagePainter(
                ImageRequest.Builder(activity)
                    .size(512, 512)
                    .data(prefix + "desert/nv_graffiti_02.png")
                    .build(),
            )

            val graffitiPainter = listOf(
                rememberAsyncImagePainter(
                    ImageRequest.Builder(activity)
                        .size(293, 107)
                        .data(prefix + "desert/ligas.png")
                        .build(),
                ),
                rememberAsyncImagePainter(
                    ImageRequest.Builder(activity)
                        .size(289, 105)
                        .data(prefix + "desert/raders_ahead.png")
                        .build(),
                ),
                rememberAsyncImagePainter(
                    ImageRequest.Builder(activity)
                        .size(499, 100)
                        .data(prefix + "desert/stop_whining.png")
                        .build(),
                ),
            ).random(rand)
            Row(Modifier.fillMaxWidth().align(Alignment.CenterEnd)) {
                Box(Modifier.weight(1f))
                Box(Modifier.weight(1f).padding(top = 48.dp, end = 8.dp, bottom = 48.dp)) {
                    Column {
                        Image(
                            painter = painterMain,
                            contentDescription = "",
                            Modifier,
                            alignment = Alignment.CenterEnd,
                            contentScale = ContentScale.Fit
                        )
                        if (rand.nextBoolean()) {
                            Image(
                                painter = graffitiPainter,
                                contentDescription = "",
                                Modifier.fillMaxWidth().rotate(-20f + rand.nextFloat() * 40f).offset(x = ((-4..4).random(rand).dp)),
                                alignment = Alignment.Center,
                                contentScale = ContentScale.Fit,
                                colorFilter = ColorFilter.tint(Color.Black)
                            )
                        }
                    }
                }
            }
        }
        ALASKA_FRONTIER -> {
            if (rand.nextBoolean()) {
                val painter = listOf(
                    rememberAsyncImagePainter(
                        ImageRequest.Builder(activity)
                            .size(339, 512)
                            .data(prefix + "alaska/america_prop_1.png")
                            .build(),
                    ),
                    rememberAsyncImagePainter(
                        ImageRequest.Builder(activity)
                            .size(512, 512)
                            .data(prefix + "alaska/america_prop_2.png")
                            .build(),
                    )
                ).random(rand)

                Row(Modifier.fillMaxWidth().align(Alignment.CenterEnd)) {
                    Box(Modifier.weight(1f))
                    Column(
                        Modifier.weight(1f).padding(top = 48.dp, end = 8.dp, bottom = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painter,
                            contentDescription = "",
                            Modifier,
                            alignment = Alignment.CenterEnd,
                            contentScale = ContentScale.Inside
                        )
                    }
                }

                val painterFrame = rememberAsyncImagePainter(
                    ImageRequest.Builder(activity)
                        .data(prefix + "alaska/autodoc.png")
                        .build()
                )
                Box(
                    Modifier.fillMaxHeight().offset { IntOffset(12.dp.roundToPx() + 8, -(12.dp.roundToPx() + 8)) },
                    contentAlignment = Alignment.BottomStart
                ) {
                    Image(
                        painter = painterFrame,
                        contentDescription = "",
                        Modifier.height(36.dp).width(36.dp),
                        alignment = Alignment.BottomStart,
                        contentScale = ContentScale.None
                    )
                }
            } else {
                val painter = listOf(
                    rememberAsyncImagePainter(
                        ImageRequest.Builder(activity)
                            .size(412, 512)
                            .data(prefix + "alaska/china_prop_1.png")
                            .build(),
                    ),
                    rememberAsyncImagePainter(
                        ImageRequest.Builder(activity)
                            .size(409, 512)
                            .data(prefix + "alaska/china_prop_2.png")
                            .build(),
                    )
                ).random(rand)

                Row(Modifier.fillMaxWidth().align(Alignment.CenterEnd)) {
                    Box(Modifier.weight(1f))
                    Column(
                        Modifier.weight(1f).padding(top = 48.dp, end = 8.dp, bottom = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painter,
                            contentDescription = "",
                            Modifier,
                            alignment = Alignment.CenterEnd,
                            contentScale = ContentScale.Inside
                        )
                    }
                }

                val painterFrame = rememberAsyncImagePainter(
                    ImageRequest.Builder(activity)
                        .data(prefix + "alaska/dlcanchredstar.jpg")
                        .build()
                )
                Box(
                    Modifier.fillMaxHeight().offset { IntOffset(12.dp.roundToPx() + 8, -(12.dp.roundToPx() + 8)) },
                    contentAlignment = Alignment.BottomStart
                ) {
                    Image(
                        painter = painterFrame,
                        contentDescription = "",
                        Modifier.height(36.dp).width(36.dp),
                        alignment = Alignment.BottomStart,
                        contentScale = ContentScale.None
                    )
                }
            }
        }
        NEW_WORLD -> {
            val painter4 = rememberAsyncImagePainter(
                ImageRequest.Builder(activity)
                    .size(327, 75)
                    .data(prefix + "new_world/ghoul_and_loving_it.png")
                    .build()
            )
            val painter5_1 = rememberAsyncImagePainter(
                ImageRequest.Builder(activity)
                    .size(128, 128)
                    .data(prefix + "new_world/nvgraffitisierra03.png")
                    .build()
            )
            val painter5_2 = rememberAsyncImagePainter(
                ImageRequest.Builder(activity)
                    .size(256, 128)
                    .data(prefix + "new_world/nvgraffitisierra07.png")
                    .build()
            )
            val painter5_3 = rememberAsyncImagePainter(
                ImageRequest.Builder(activity)
                    .size(256, 128)
                    .data(prefix + "new_world/wwromanes.png")
                    .build()
            )

            Row(Modifier.fillMaxWidth()) {
                Image(
                    painter = painter4,
                    contentDescription = "",
                    modifier = Modifier.weight(1f).padding(top = 8.dp, start = 18.dp),
                    contentScale = ContentScale.Inside,
                    alignment = Alignment.TopStart,
                    colorFilter = ColorFilter.tint(Color.White)
                )
                Image(
                    painter = listOf(painter5_1, painter5_2, painter5_3).random(rand),
                    contentDescription = "",
                    modifier = Modifier.weight(1f).padding(top = 2.dp, start = 8.dp, end = 8.dp),
                    contentScale = ContentScale.Inside,
                    alignment = Alignment.TopCenter,
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }

            if (rand.nextBoolean()) {
                val objPainter = rememberAsyncImagePainter(
                    ImageRequest.Builder(activity)
                        .size(235, 501)
                        .data(prefix + "new_world/legion1.png")
                        .build()
                )
                val painter3 = rememberAsyncImagePainter(
                    ImageRequest.Builder(activity)
                        .data(prefix + "new_world/graffiti_cool.png")
                        .build()
                )

                Row(Modifier.fillMaxWidth().align(Alignment.CenterEnd).padding(top = 48.dp, end = 8.dp)) {
                    Box(Modifier.weight(3f))
                    Box(Modifier.weight(1f)) {
                        val totalHeight = 501 + 185
                        val newHeight = min(totalHeight * 2, height)
                        val scale = newHeight.toFloat() / totalHeight
                        Image(
                            painter = painter3,
                            contentDescription = "",
                            modifier = Modifier.scale(scale).offset { IntOffset(0, (-50 * scale).toInt()) },
                            contentScale = ContentScale.None
                        )
                        Image(
                            painter = objPainter,
                            contentDescription = "",
                            Modifier.scale(scale),
                            contentScale = ContentScale.None
                        )
                    }
                }
            } else {
                val painter6 = rememberAsyncImagePainter(
                    ImageRequest.Builder(activity)
                        .data(prefix + "new_world/graffiti_coolest.png")
                        .build()
                )

                Row(Modifier.fillMaxWidth().align(Alignment.CenterEnd)) {
                    Box(Modifier.weight(3f))
                    Box(Modifier.weight(1f).padding(top = 48.dp, bottom = 48.dp, end = 8.dp)) {
                        val totalHeight1 = 676
                        val newHeight1 = min(totalHeight1 * 2, height - (96.dp).dpToPx().toInt())
                        val scale = newHeight1.toFloat() / totalHeight1
                        Image(
                            painter = painter6,
                            contentDescription = "",
                            Modifier.scale(scale)
                        )
                    }
                }
            }
        }
        PIP_BOY -> {
            Row(Modifier.fillMaxWidth().align(Alignment.CenterEnd).padding(top = 48.dp, bottom = 48.dp, end = 8.dp)) {
                Box(Modifier.weight(1f))
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    val numberOfRounds = 20
                    var cnt by rememberSaveable { mutableIntStateOf(0) }
                    if (cnt == numberOfRounds) {
                        LaunchedEffect(Unit) {
                            playFanfares(activity)
                            activity.save?.let {
                                it.caps += 22229
                                saveOnGD(activity)
                            }
                            delay(25000L)
                            cnt = 0
                        }

                    }
                    var side by rememberSaveable { mutableStateOf(Random.nextBoolean()) }

                    TextFallout(
                        "And this is the device #50724. " +
                                "It was our first attempt to measure someone\'s Luck. " +
                                "All you have to do to win is to pass 20 rounds of guessing game: Heads or Tails? " +
                                "Choose right - counter adds 1. Choose wrong - counter goes to zero. " +
                                "Counter gets to 20 - get a prize. " +
                                "Not monetary, of course, just a huge pile of bottlecaps we received from Sunset Sarsaparilla HQ. " +
                                "Still have no idea why we've got it.",
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        12.sp,
                        Alignment.TopCenter,
                        Modifier.fillMaxWidth(),
                        TextAlign.Center,
                    )
                    Spacer(Modifier.height(12.dp))
                    TextFallout(
                        "$cnt / $numberOfRounds",
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        14.sp,
                        Alignment.TopCenter,
                        Modifier.fillMaxWidth(),
                        TextAlign.Center,
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        TextFallout(
                            "HEADS",
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            16.sp,
                            Alignment.Center,
                            Modifier.background(getTextBackgroundColor(activity)).padding(4.dp).clickable {
                                if (cnt == numberOfRounds) return@clickable
                                if (side) {
                                    cnt++
                                    playYesBeep(activity)
                                } else {
                                    cnt = 0
                                    playNoBeep(activity)
                                }
                                side = Random.nextBoolean()
                            },
                            TextAlign.Center,
                        )
                        TextFallout(
                            "TAILS",
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            16.sp,
                            Alignment.Center,
                            Modifier.background(getTextBackgroundColor(activity)).padding(4.dp).clickable {
                                if (cnt == numberOfRounds) return@clickable
                                if (!side) {
                                    cnt++
                                    playYesBeep(activity)
                                } else {
                                    cnt = 0
                                    playNoBeep(activity)
                                }
                                side = Random.nextBoolean()
                            },
                            TextAlign.Center,
                        )
                    }
                }
            }
        }
        PIP_GIRL -> {
            val phrases = listOf(
                "I have no sleep tonight;\nTransmission starts again.\nBut telling you what\'s right\nIs crying in the rain.",
                "6-28-69\nRemember.",
                "Storms come and go,\nBut you\'re still standing.",
                "War never changes.\n\nBut people do, through the roads they walk.",
                "...about\n2.8 times 10^7\npeople...",
                "Billie pondered: \"What's Pip-Boy?\""
            )
            val painters = listOf(
                rememberAsyncImagePainter(
                    ImageRequest.Builder(activity)
                        .data(prefix + "pip_girl/vault_girl1.png")
                        .build()
                ),
                rememberAsyncImagePainter(
                    ImageRequest.Builder(activity)
                        .data(prefix + "pip_girl/vault_girl2.png")
                        .build()
                ),
            )
            if ((1..8).random(rand) in (1..6)) {
                val text = phrases.random(rand)
                Row(Modifier.fillMaxWidth().align(Alignment.CenterEnd).padding(top = 48.dp, bottom = 48.dp, end = 8.dp)) {
                    Box(Modifier.weight(1f))
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(
                            text =  text,
                            textAlign = TextAlign.Center,
                            color = Color.Black,
                            fontFamily = FontFamily(Font(R.font.help)),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .rotate(-20f + rand.nextFloat() * 40f)
                                .offset(x = ((-5..5).random(rand).dp), y = ((-5..5).random(rand).dp)),
                            fontSize = 12.sp,
                        )
                    }
                }
            } else {
                Row(Modifier.fillMaxWidth().align(Alignment.CenterEnd).padding(top = 48.dp, bottom = 48.dp, end = 8.dp)) {
                    Box(Modifier.weight(1f))
                    Box(Modifier.weight(1f)) {
                        Image(
                            painter = painters.random(rand),
                            contentDescription = "",
                            Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit,
                            alignment = Alignment.Center
                        )
                    }
                }
            }
        }
        VAULT_21 -> {
            val totalHeight = 768
            val newHeight = min(totalHeight * 2, height - (96.dp).dpToPx().toInt())
            val scale1 = newHeight.toFloat() / totalHeight
            val totalWidth = 768
            val newWidth = min(totalWidth * 2, width / 2)
            val scale2 = newWidth.toFloat() / totalWidth
            val scale = minOf(scale1, scale2)
            Box(Modifier.fillMaxSize().padding(vertical = 48.dp), Alignment.CenterEnd) {
                val painter1 = rememberAsyncImagePainter(
                    ImageRequest.Builder(activity)
                        .size(256, 256)
                        .data(prefix + "vault_21/load_roulette_wheel.png")
                        .build()
                )
                val painter0 = rememberAsyncImagePainter(
                    ImageRequest.Builder(activity)
                        .size(16, 256)
                        .data(prefix + "vault_21/load_roulette_ball.png")
                        .build()
                )
                val painter2 = rememberAsyncImagePainter(
                    ImageRequest.Builder(activity)
                        .size(768, 768)
                        .data(prefix + "vault_21/load_bars.png")
                        .build()
                )
                val wheelRotation = remember { mutableFloatStateOf(0f) }
                val ballRotation = remember { mutableFloatStateOf(0f) }
                LaunchedEffect(Unit) {
                    while (isActive) {
                        wheelRotation.floatValue += 5f
                        delay(activity.animationTickLength.value!! / 5)
                    }
                }
                LaunchedEffect(Unit) {
                    while (isActive) {
                        ballRotation.floatValue -= 4f
                        delay(activity.animationTickLength.value!! / 5)
                    }
                }

                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
                    Box(Modifier.size(672.pxToDp(), 672.pxToDp()), contentAlignment = Alignment.Center) {
                        Image(
                            painter = painter2,
                            contentDescription = "",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = FixedScale(scale),
                        )
                        Box(Modifier.size(672.pxToDp(), 672.pxToDp()).scale(scale), contentAlignment = Alignment.Center) {
                            Image(
                                painter = painter1,
                                contentDescription = "",
                                modifier = Modifier.rotate(wheelRotation.floatValue),
                            )
                            Image(
                                painter = painter0,
                                contentDescription = "",
                                modifier = Modifier.rotate(ballRotation.floatValue),
                            )
                        }
                    }
                }
            }
        }
    }
}