package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.FixedScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.heads
import caravan.composeapp.generated.resources.help
import caravan.composeapp.generated.resources.null_condition
import caravan.composeapp.generated.resources.pip_boy_main_screen
import caravan.composeapp.generated.resources.style_alaska
import caravan.composeapp.generated.resources.style_alaska_condition
import caravan.composeapp.generated.resources.style_black
import caravan.composeapp.generated.resources.style_black_condition
import caravan.composeapp.generated.resources.style_desert
import caravan.composeapp.generated.resources.style_desert_condition
import caravan.composeapp.generated.resources.style_enclave
import caravan.composeapp.generated.resources.style_enclave_condition
import caravan.composeapp.generated.resources.style_legion
import caravan.composeapp.generated.resources.style_legion_condition
import caravan.composeapp.generated.resources.style_madre_roja
import caravan.composeapp.generated.resources.style_madre_roja_condition
import caravan.composeapp.generated.resources.style_ncr
import caravan.composeapp.generated.resources.style_ncr_condition
import caravan.composeapp.generated.resources.style_new_world
import caravan.composeapp.generated.resources.style_new_world_condition
import caravan.composeapp.generated.resources.style_old_world
import caravan.composeapp.generated.resources.style_old_world_condition
import caravan.composeapp.generated.resources.style_pip_boy
import caravan.composeapp.generated.resources.style_pip_girl
import caravan.composeapp.generated.resources.style_pip_girl_condition
import caravan.composeapp.generated.resources.style_sierra_madre
import caravan.composeapp.generated.resources.style_sierra_madre_condition
import caravan.composeapp.generated.resources.style_vault_21
import caravan.composeapp.generated.resources.style_vault_21_condition
import caravan.composeapp.generated.resources.style_vault_22
import caravan.composeapp.generated.resources.style_vault_22_condition
import caravan.composeapp.generated.resources.table_wood
import caravan.composeapp.generated.resources.tails
import com.sebaslogen.resaca.rememberScoped
import com.unicorns.invisible.caravan.Style.ALASKA_FRONTIER
import com.unicorns.invisible.caravan.Style.DESERT
import com.unicorns.invisible.caravan.Style.ENCLAVE
import com.unicorns.invisible.caravan.Style.LEGION
import com.unicorns.invisible.caravan.Style.MADRE_ROJA
import com.unicorns.invisible.caravan.Style.NCR
import com.unicorns.invisible.caravan.Style.NEW_WORLD
import com.unicorns.invisible.caravan.Style.OLD_WORLD
import com.unicorns.invisible.caravan.Style.PIP_BOY
import com.unicorns.invisible.caravan.Style.PIP_GIRL
import com.unicorns.invisible.caravan.Style.SIERRA_MADRE
import com.unicorns.invisible.caravan.Style.VAULT_21
import com.unicorns.invisible.caravan.Style.VAULT_22
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.ShowImageFromPath
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.dpToPx
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.playFanfares
import com.unicorns.invisible.caravan.utils.playNoBeep
import com.unicorns.invisible.caravan.utils.playSporePlantSound
import com.unicorns.invisible.caravan.utils.playYesBeep
import com.unicorns.invisible.caravan.utils.pxToDp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.min
import kotlin.random.Random


enum class Style(
    val styleNameId: StringResource,
    val conditionToOpenId: StringResource,
    val progress: () -> Pair<Int, Int>
) {
    DESERT(Res.string.style_desert, Res.string.style_desert_condition, { saveGlobal.maxStrike to 7 }),
    ALASKA_FRONTIER(Res.string.style_alaska, Res.string.style_alaska_condition, { saveGlobal.maxStrike to 14 }),
    PIP_BOY(Res.string.style_pip_boy, Res.string.null_condition, { 0 to 0 }),
    PIP_GIRL(Res.string.style_pip_girl, Res.string.style_pip_girl_condition, { saveGlobal.lvl to 6 }),
    OLD_WORLD(Res.string.style_old_world, Res.string.style_old_world_condition, { saveGlobal.capsWasted + saveGlobal.chipsWasted to 7500 }),
    NEW_WORLD(Res.string.style_new_world, Res.string.style_new_world_condition, { saveGlobal.capsWasted + saveGlobal.chipsWasted to 20000 }),
    SIERRA_MADRE(Res.string.style_sierra_madre, Res.string.style_sierra_madre_condition, { saveGlobal.challengesCompleted to 100 }),
    MADRE_ROJA(Res.string.style_madre_roja, Res.string.style_madre_roja_condition, { saveGlobal.challengesCompleted to 500 }),
    VAULT_21(Res.string.style_vault_21, Res.string.style_vault_21_condition, { saveGlobal.winsWithBet to 600 }),
    VAULT_22(Res.string.style_vault_22, Res.string.style_vault_22_condition, { saveGlobal.winsWithBet to 200 }),
    BLACK(Res.string.style_black, Res.string.style_black_condition, { saveGlobal.pvpWins to 10 }),
    ENCLAVE(Res.string.style_enclave, Res.string.style_enclave_condition, { (if (saveGlobal.towerBeatenN) 1 else 0) to 1 }),
    NCR(Res.string.style_ncr, Res.string.style_ncr_condition, { saveGlobal.availableDecks.size to 12 }),
    LEGION(Res.string.style_legion, Res.string.style_legion_condition, { saveGlobal.availableCardsSize() to 333 });
}


@Composable
fun Modifier.getTableBackground(): Modifier {
    return paint(painterResource(Res.drawable.table_wood), contentScale = ContentScale.Crop)
}

fun getStyleCities(style: Style): List<String> {
    return when (style) {
        ALASKA_FRONTIER -> listOf("ALPINE", "KOTZEBUE", "NEWTOK", "AKUTAN", "ANCHORAGE", "SCAGWAY")
        OLD_WORLD, SIERRA_MADRE -> listOf("L.A.", "REDDING", "BLACK ROCK", "SAN DIEGO", "RENO", "ED. AFB")
        MADRE_ROJA -> listOf("DOG", "DOMINO", "ELIJAH", "GOD", "CHRISTINE", "COURIER")
        VAULT_21 -> listOf("LONG 15", "PRIMM", "NOVAK", "188", "NEW VEGAS", "HOOVER DAM")
        VAULT_22 -> listOf("VAULT DOOR", "OXYGEN RECYCLING", "COMMON AREAS", "ENTRANCE HALL", "FOOD PRODUCTION", "PEST CONTROL")
        ENCLAVE -> listOf("NAVARRO", "AUSTIN", "WASHINGTON", "SEATTLE", "CHICAGO", "NEW YORK")
        Style.BLACK -> listOf("YOU WILL BE", "DOING GREAT", "FEELING GOOD", "DYING IN PAIN", "IS UNLIKELY AND", "IS JUST AN ILLUSION")
        LEGION -> listOf("THE FORT", "FLAGSTAFF", "DOG CITY", "PHOENIX", "TWO SUN", "MALPAIS")
        else -> listOf("BONEYARD", "REDDING", "VAULT CITY", "DAYGLOW", "NEW RENO", "THE HUB")
    }
}


// TODO 3.0: check everything!!!
@Composable
fun BoxWithConstraintsScope.StylePicture(
    style: Style,
    showAlertDialog: (String, String) -> Unit,
    screenWidth: Int,
    screenHeight: Int
) {
    val rand by rememberScoped { mutableStateOf(Random(Random.nextInt())) }
    Row(
        Modifier
            .fillMaxWidth()
            .align(Alignment.CenterEnd)) {
        Box(Modifier.weight(1f))
        Box(
            Modifier
                .weight(1f)
                .padding(top = 32.dp, end = 20.dp, bottom = 48.dp)) {
                when (style) {
                    OLD_WORLD -> {

                    }

                    VAULT_22 -> {
                        // TODO: fix
                        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                            BoxWithConstraints(Modifier.size(600.pxToDp(), 666.pxToDp()).offset(y = 60.pxToDp()), contentAlignment = Alignment.BottomCenter) {
                                val w = maxWidth.dpToPx()
                                !saveGlobal.v22PrizeWon
                                if (true) {
                                    Box(Modifier.wrapContentSize()) {
                                        var v22scaleDiff by rememberScoped { mutableFloatStateOf(0f) }
                                        var scaleDiffTrue by rememberScoped { mutableFloatStateOf(0f) }
                                        val clicksTotal = 200f
                                        val scaleMaxFactor = min(1f, w / 600f)
                                        val scale = scaleMaxFactor * (clicksTotal - scaleDiffTrue / 2) / clicksTotal
                                        ShowImageFromPath(
                                            path = "menu_items/vault_22/spore_plant.webp",
                                            IntSize(600, 666),
                                            Modifier
                                                .clickable {
                                                    if (v22scaleDiff.toInt() % 50 == 5) {
                                                        playSporePlantSound()
                                                        scaleDiffTrue = v22scaleDiff
                                                    }
                                                    if (v22scaleDiff >= clicksTotal) {
                                                        playSporePlantSound()
                                                        showAlertDialog("Knockout!", "You have won 22 caps!")
                                                        saveGlobal.capsInHand += 22
                                                        saveGlobal.v22PrizeWon = true
                                                        saveData()
                                                    }
                                                    v22scaleDiff++
                                                },
                                            ColorFilter.colorMatrix(ColorMatrix()),
                                            alignment = BiasAlignment(0f, 1f),
                                            scale = FixedScale(scale)
                                        )
                                    }
                                }
                            }

                            Box(Modifier.wrapContentSize()) {
                                ShowImageFromPath(
                                    path = "menu_items/vault_22/v22sign.webp",
                                    IntSize(512, 356),
                                    Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter),
                                    ColorFilter.colorMatrix(ColorMatrix()),
                                    alignment = Alignment.TopCenter,
                                    scale = ContentScale.Inside
                                )
                                val mossUp = listOf(
                                    "vault_22/v22moss2.webp" to IntSize(136, 76),
                                    "vault_22/v22moss4.webp" to IntSize(139, 139),
                                    "vault_22/v22moss5.webp" to IntSize(141, 73),
                                    "vault_22/v22moss3.webp" to IntSize(187, 151),
                                )

                                var currentWidth = 0
                                Row(
                                    Modifier
                                        .wrapContentSize()
                                        .offset((-5).dp, 0.dp),
                                    horizontalArrangement = Arrangement.Start,
                                ) {
                                    while (currentWidth < screenWidth) {
                                        val curMossUp = mossUp.random(rand)
                                        ShowImageFromPath(
                                            "menu_items/" + curMossUp.first,
                                            curMossUp.second,
                                            Modifier
                                                .rotate(if (curMossUp.first == "vault_22/v22moss3.webp") 90f else 0f)
                                                .offset {
                                                    if (curMossUp.first == "vault_22/v22moss3.webp")
                                                        IntOffset(-20, 0)
                                                    else
                                                        IntOffset(0, -5)
                                                },
                                            colorFilter = ColorFilter.colorMatrix(ColorMatrix()),
                                            scale = ContentScale.Inside,
                                            alignment = Alignment.TopStart
                                        )
                                        currentWidth += curMossUp.second.width
                                    }
                                }
                            }
                        }
                    }

                    SIERRA_MADRE -> {

                    }

                    MADRE_ROJA -> {

                    }

                    DESERT -> {
                        Column {
                            ShowImageFromPath(
                                path = "menu_items/desert/nv_graffiti_02.webp",
                                IntSize(512, 512),
                                Modifier,
                                ColorFilter.colorMatrix(ColorMatrix()),
                                alignment = Alignment.CenterEnd,
                                scale = ContentScale.Fit
                            )
                            val extras = listOf(
                                "ligas.webp" to IntSize(293, 107),
                                "raders_ahead.webp" to IntSize(289, 105),
                                "stop_whining.webp" to IntSize(499, 100)
                            )
                            if (rand.nextBoolean()) {
                                val extra = extras.random()
                                ShowImageFromPath(
                                    path = "menu_items/desert/" + extra.first,
                                    IntSize(512, 512),
                                    Modifier
                                        .fillMaxWidth()
                                        .rotate(-20f + rand.nextFloat() * 40f)
                                        .offset(x = ((-4..4).random(rand).dp)),
                                    ColorFilter.tint(Color.Black),
                                    alignment = Alignment.Center,
                                    scale = ContentScale.Fit
                                )
                            }
                        }
                    }

                    ALASKA_FRONTIER -> {

                    }

                    NEW_WORLD -> {

                    }

                    PIP_BOY -> {
                        val numberOfRounds = 10
                        var cnt by rememberSaveable { mutableIntStateOf(0) }
                        if (cnt == numberOfRounds) {
                            LaunchedEffect(Unit) {
                                playFanfares()
                                if (!saveGlobal.pipBoyPrizeWon) {
                                    showAlertDialog("Jackpot!", "You have won 777 caps!")
                                    saveGlobal.capsInHand += 777
                                    saveGlobal.pipBoyPrizeWon = true
                                    saveData()
                                }
                                delay(25000L)
                                cnt = 0
                            }
                        }
                        var side by rememberSaveable { mutableStateOf(Random.nextBoolean()) }

                        Column(
                            Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            TextFallout(
                                stringResource(Res.string.pip_boy_main_screen),
                                getTextColor(),
                                getTextStrokeColor(),
                                12.sp,
                                Modifier.fillMaxWidth(),
                            )
                            Spacer(Modifier.height(4.dp))
                            TextFallout(
                                "$cnt / $numberOfRounds",
                                getTextColor(),
                                getTextStrokeColor(),
                                14.sp,
                                Modifier.fillMaxWidth(),
                            )
                            Spacer(Modifier.height(4.dp))
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                TextFallout(
                                    stringResource(Res.string.heads),
                                    getTextColor(),
                                    getTextStrokeColor(),
                                    16.sp,
                                    Modifier
                                        .background(getTextBackgroundColor())
                                        .padding(4.dp)
                                        .clickable {
                                            if (cnt == numberOfRounds) return@clickable
                                            if (side) {
                                                cnt++
                                                playYesBeep()
                                            } else {
                                                cnt = 0
                                                playNoBeep()
                                            }
                                            side = Random.nextBoolean()
                                        },
                                )
                                TextFallout(
                                    stringResource(Res.string.tails),
                                    getTextColor(),
                                    getTextStrokeColor(),
                                    16.sp,
                                    Modifier
                                        .background(getTextBackgroundColor())
                                        .padding(4.dp)
                                        .clickable {
                                            if (cnt == numberOfRounds) return@clickable
                                            if (!side) {
                                                cnt++
                                                playYesBeep()
                                            } else {
                                                cnt = 0
                                                playNoBeep()
                                            }
                                            side = Random.nextBoolean()
                                        },
                                )
                            }
                        }
                    }

                    PIP_GIRL -> {
                        // Do not extract those.
                        val phrases = listOf(
                            "6-28-69\nRemember.",
                            "Storms come and go,\nBut you\'re still standing.",
                            "War never changes.\n\nBut people do, through the roads they walk.",
                            "...about\n2.8 times 10^7\npeople...",
                            "Billie pondered: \"What's Pip-Boy?\"",
                        )
                        Column(
                            Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            BoxWithConstraints(Modifier.wrapContentSize()) {
                                val numberOfRounds = 10
                                var cnt by rememberSaveable { mutableIntStateOf(0) }
                                if (cnt == numberOfRounds) {
                                    LaunchedEffect(Unit) {
                                        playFanfares()
                                        if (!saveGlobal.pipGirlPrizeWon) {
                                            showAlertDialog("Jackpot!", "You have won 100 caps!")
                                            saveGlobal.capsInHand += 100
                                            saveGlobal.pipGirlPrizeWon = true
                                            saveData()
                                        }
                                        delay(25000L)
                                        cnt = 0
                                    }
                                }
                                var side by rememberSaveable { mutableIntStateOf((0..2).random()) }
                                var isButtonPressed by rememberSaveable { mutableIntStateOf(-1) }
                                Column(
                                    Modifier.wrapContentSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    TextFallout(
                                        "Device #62869: CharisMeter",
                                        getTextColor(),
                                        getTextStrokeColor(),
                                        12.sp,
                                        Modifier.fillMaxWidth(),
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    TextFallout(
                                        "$cnt / $numberOfRounds",
                                        getTextColor(),
                                        getTextStrokeColor(),
                                        14.sp,
                                        Modifier.fillMaxWidth(),
                                    )
                                    Spacer(Modifier.height(4.dp))

                                    BoxWithConstraints(Modifier.fillMaxWidth().padding(4.dp)) {
                                        LaunchedEffect(Unit) {
                                            while (isActive) {
                                                if (isButtonPressed != -1) {
                                                    if (isButtonPressed == 2 && side == 0 || side - isButtonPressed == 1) {
                                                        cnt = 0
                                                        playNoBeep()
                                                    } else if (isButtonPressed != side) {
                                                        cnt++
                                                        playYesBeep()
                                                    }
                                                    delay(500L)
                                                    side = (0..2).random()
                                                    isButtonPressed = -1
                                                }
                                                delay(150L)
                                                side = (0..2).random()
                                                delay(150L)
                                            }
                                        }
                                        TextFallout(
                                            when (side) {
                                                0 -> "Flirt"
                                                1 -> "Roast"
                                                else -> "Joke"
                                            },
                                            getTextColor(),
                                            getTextStrokeColor(),
                                            16.sp,
                                            Modifier.align(Alignment.Center),
                                        )
                                    }

                                    fun onSidePressed(thisSide: Int) {
                                        if (cnt < numberOfRounds) {
                                            isButtonPressed = thisSide
                                        }
                                    }
                                    @Composable
                                    fun Button(name: String, num: Int) {
                                        TextFallout(
                                            name,
                                            getTextColor(),
                                            getTextStrokeColor(),
                                            16.sp,
                                            Modifier
                                                .background(getTextBackgroundColor())
                                                .padding(4.dp)
                                                .clickable {
                                                    onSidePressed(num)
                                                },
                                        )
                                    }
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        Button("Flirt", 0)
                                        Button("Roast", 1)
                                    }
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        Button("Joke", 2)
                                    }
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            BoxWithConstraints(Modifier, contentAlignment = Alignment.BottomCenter) {
                                val text = phrases.random(rand)
                                Text(
                                    text = text,
                                    textAlign = TextAlign.Center,
                                    color = Color.Black,
                                    fontFamily = FontFamily(Font(Res.font.help)),
                                    modifier = Modifier
                                        .rotate(-20f + rand.nextFloat() * 40f)
                                        .offset(
                                            x = ((-5..5).random(rand).dp),
                                            y = ((-5..5).random(rand).dp)
                                        ),
                                    fontSize = 12.sp,
                                )
                            }
                        }
                    }

                    VAULT_21 -> {

                    }

                    ENCLAVE -> {

                    }

                    Style.BLACK -> {}
                    NCR -> {

                    }

                    LEGION -> {

                    }
                }
        }
    }
}
