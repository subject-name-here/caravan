package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.heads
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
import com.unicorns.invisible.caravan.model.enemy.EnemyPvENoBank
import com.unicorns.invisible.caravan.model.enemy.EnemyPvEWithBank
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.playFanfares
import com.unicorns.invisible.caravan.utils.playNoBeep
import com.unicorns.invisible.caravan.utils.playYesBeep
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.random.Random


enum class Style(
    val styleNameId: StringResource,
    val conditionToOpenId: StringResource,
    val progress: () -> Pair<Int, Int>
) {
    DESERT(Res.string.style_desert, Res.string.style_desert_condition, { save.maxStrike to 6 }),
    ALASKA_FRONTIER(Res.string.style_alaska, Res.string.style_alaska_condition, { save.maxStrike to 12 }),
    PIP_BOY(Res.string.style_pip_boy, Res.string.null_condition, { 0 to 0 }),
    PIP_GIRL(Res.string.style_pip_girl, Res.string.style_pip_girl_condition, { save.maxBetWon to 969 }),
    OLD_WORLD(Res.string.style_old_world, Res.string.style_old_world_condition, { save.capsWasted + save.chipsWasted to 1000 }),
    NEW_WORLD(Res.string.style_new_world, Res.string.style_new_world_condition, { save.capsWasted + save.chipsWasted to 10000 }),
    SIERRA_MADRE(Res.string.style_sierra_madre, Res.string.style_sierra_madre_condition, { save.challengesCompleted to 150 }),
    MADRE_ROJA(Res.string.style_madre_roja, Res.string.style_madre_roja_condition, { save.challengesCompleted to 1500 }),
    VAULT_21(Res.string.style_vault_21, Res.string.style_vault_21_condition, { save.winsWithBet to 1000 }),
    VAULT_22(Res.string.style_vault_22, Res.string.style_vault_22_condition, { save.winsWithBet to 100 }),
    BLACK(Res.string.style_black, Res.string.style_black_condition, { save.pvpWins to 10 }),
    ENCLAVE(Res.string.style_enclave, Res.string.style_enclave_condition, { (if (save.towerBeaten) 1 else 0) to 1 }),
    NCR(Res.string.style_ncr, Res.string.style_ncr_condition, {
        save.enemiesGroups2.flatten().count { (when (it) {
            is EnemyPvENoBank -> it.winsBlitz
            is EnemyPvEWithBank -> it.winsBlitzBet
        }) > 0 } to save.enemiesGroups2.flatten().size
    }),
    LEGION(Res.string.style_legion, Res.string.style_legion_condition, {
        save.enemiesGroups2.flatten().count { (when (it) {
            is EnemyPvENoBank -> it.winsBlitz + it.wins
            is EnemyPvEWithBank -> it.winsBlitzBet + it.winsBet
        }) >= 3 } to save.enemiesGroups2.flatten().size
    });
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


// TODO: check everything!!!
@Composable
fun BoxWithConstraintsScope.StylePicture(
    style: Style,
    width: Int,
    height: Int
) {
    val prefix = "file:///android_asset/menu_items/"
    val rand by rememberScoped { mutableStateOf(Random(Random.nextInt())) }
    when (style) {
        OLD_WORLD -> {

        }

        VAULT_22 -> {

        }

        SIERRA_MADRE -> {

        }

        MADRE_ROJA -> {

        }

        DESERT -> {

        }

        ALASKA_FRONTIER -> {

        }

        NEW_WORLD -> {

        }

        PIP_BOY -> {
            Row(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterEnd)
                    .padding(top = 48.dp, bottom = 48.dp, end = 8.dp)
            ) {
                Box(Modifier.weight(1f))
                Column(
                    Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val numberOfRounds = 10
                    var cnt by rememberSaveable { mutableIntStateOf(0) }
                    if (cnt == numberOfRounds) {
                        LaunchedEffect(Unit) {
                            playFanfares()
                            save.capsInHand += 777
                            saveData()
                            delay(25000L)
                            cnt = 0
                        }

                    }
                    var side by rememberSaveable { mutableStateOf(Random.nextBoolean()) }

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
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
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
        }

        PIP_GIRL -> {
            // Do not extract those.
            val phrases = listOf(
                "6-28-69\nRemember.",
                "Storms come and go,\nBut you\'re still standing.",
                "War never changes.\n\nBut people do, through the roads they walk.",
                "...about\n2.8 times 10^7\npeople...",
                "Billie pondered: \"What's Pip-Boy?\""
            )
        }

        VAULT_21 -> {

        }

        ENCLAVE -> {

        }

        Style.BLACK -> {}
        NCR -> {
            // TODO
        }
        LEGION -> {
            // TODO
        }
    }
}
