package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.player_level
import caravan.composeapp.generated.resources.pve_caps_bet
import caravan.composeapp.generated.resources.pve_caps_wasted
import caravan.composeapp.generated.resources.pve_caps_won
import caravan.composeapp.generated.resources.pve_challenges_completed
import caravan.composeapp.generated.resources.pve_chips_wasted
import caravan.composeapp.generated.resources.pve_finished_to_started
import caravan.composeapp.generated.resources.pve_games_finished
import caravan.composeapp.generated.resources.pve_games_started
import caravan.composeapp.generated.resources.pve_games_started_pvp
import caravan.composeapp.generated.resources.pve_games_won
import caravan.composeapp.generated.resources.pve_games_won_pvp
import caravan.composeapp.generated.resources.pve_games_won_with_bet
import caravan.composeapp.generated.resources.pve_max_strike
import caravan.composeapp.generated.resources.pve_more_stats
import caravan.composeapp.generated.resources.pve_percentiles
import caravan.composeapp.generated.resources.pve_stats
import caravan.composeapp.generated.resources.pve_w_to_finished
import caravan.composeapp.generated.resources.pve_w_to_l
import caravan.composeapp.generated.resources.pve_w_to_started
import caravan.composeapp.generated.resources.xp_left
import com.unicorns.invisible.caravan.utils.MenuItemOpen
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.toString
import org.jetbrains.compose.resources.stringResource


@Composable
fun ShowStats(goBack: () -> Unit) {
    MenuItemOpen(stringResource(Res.string.pve_stats), "<-", Alignment.TopCenter, goBack) {
        val started = save.gamesStarted
        val finished = save.gamesFinished
        val won = save.wins
        val loss = finished - won
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(getBackgroundColor()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            @Composable
            fun StatsItem(text: String) {
                TextFallout(
                    text,
                    getTextColor(),
                    getTextStrokeColor(),
                    16.sp,
                    Modifier,
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            TextFallout(
                stringResource(Res.string.player_level, save.lvl),
                getTextColor(),
                getTextStrokeColor(),
                24.sp,
                Modifier,
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextFallout(
                stringResource(Res.string.xp_left, save.needXpToNextLevel() - save.xp),
                getTextColor(),
                getTextStrokeColor(),
                16.sp,
                Modifier,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Spacer(modifier = Modifier.height(12.dp))
            TextFallout(
                stringResource(Res.string.pve_stats),
                getTextColor(),
                getTextStrokeColor(),
                20.sp,
                Modifier,
            )
            Spacer(modifier = Modifier.height(12.dp))
            StatsItem(text = stringResource(Res.string.pve_games_started, started))
            Spacer(modifier = Modifier.height(8.dp))
            StatsItem(text = stringResource(Res.string.pve_games_finished, finished))
            Spacer(modifier = Modifier.height(8.dp))
            StatsItem(text = stringResource(Res.string.pve_games_won, won))
            Spacer(modifier = Modifier.height(8.dp))
            StatsItem(text = stringResource(Res.string.pve_games_won_with_bet, save.winsWithBet))
            Spacer(modifier = Modifier.height(8.dp))
            StatsItem(text = stringResource(Res.string.pve_games_started_pvp, save.pvpGames))
            Spacer(modifier = Modifier.height(8.dp))
            StatsItem(text = stringResource(Res.string.pve_games_won_pvp, save.pvpWins))
            Spacer(modifier = Modifier.height(8.dp))
            StatsItem(text = stringResource(Res.string.pve_caps_bet, save.capsBet))
            Spacer(modifier = Modifier.height(8.dp))
            StatsItem(text = stringResource(Res.string.pve_caps_won, save.capsWon))
            Spacer(modifier = Modifier.height(12.dp))
            TextFallout(
                stringResource(Res.string.pve_percentiles),
                getTextColor(),
                getTextStrokeColor(),
                20.sp,
                Modifier,
            )
            Spacer(modifier = Modifier.height(12.dp))
            StatsItem(
                text = stringResource(
                    Res.string.pve_w_to_l,
                    if (loss == 0) "-" else (won.toDouble() / loss).toString(3)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            StatsItem(
                text = stringResource(
                    Res.string.pve_w_to_finished,
                    if (finished == 0) "-" else ((won.toDouble() / finished) * 100).toString(2)
                ),
            )
            Spacer(modifier = Modifier.height(8.dp))
            StatsItem(
                text = stringResource(
                    Res.string.pve_w_to_started,
                    if (started == 0) "-" else (won.toDouble() / started * 100.0).toString(2)
                ),
            )
            Spacer(modifier = Modifier.height(8.dp))
            StatsItem(
                text = stringResource(
                    Res.string.pve_finished_to_started,
                    if (started == 0) "-" else (finished.toDouble() / started * 100.0).toString(1)
                ),
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextFallout(
                stringResource(Res.string.pve_more_stats),
                getTextColor(),
                getTextStrokeColor(),
                20.sp,
                Modifier,
            )
            Spacer(modifier = Modifier.height(12.dp))
            StatsItem(text = stringResource(Res.string.pve_caps_wasted, save.capsWasted))
            Spacer(modifier = Modifier.height(8.dp))
            StatsItem(text = stringResource(Res.string.pve_chips_wasted, save.chipsWasted))
            Spacer(modifier = Modifier.height(8.dp))
            StatsItem(text = stringResource(Res.string.pve_challenges_completed, save.challengesCompleted))
            Spacer(modifier = Modifier.height(8.dp))
            StatsItem(text = stringResource(Res.string.pve_max_strike, save.maxStrike))
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}