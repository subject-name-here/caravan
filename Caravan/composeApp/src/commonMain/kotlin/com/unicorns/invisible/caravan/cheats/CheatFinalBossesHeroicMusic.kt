package com.unicorns.invisible.caravan.cheats

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.easter_egg
import caravan.composeapp.generated.resources.supreme_leader_and_tower_boss_have_default_music
import caravan.composeapp.generated.resources.supreme_leader_and_tower_boss_have_new_music
import com.unicorns.invisible.caravan.save
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.playYesBeep
import org.jetbrains.compose.resources.getString


data object CheatFinalBossesHeroicMusic : Cheat {
    override fun getCode() = 845

    override suspend fun onEnter(showAlertDialog: (String, String) -> Unit) {
        save.isHeroic = !save.isHeroic
        saveData()
        playYesBeep()

        if (save.isHeroic) {
            showAlertDialog(
                getString(Res.string.easter_egg),
                getString(Res.string.supreme_leader_and_tower_boss_have_new_music)
            )
        } else {
            showAlertDialog(
                getString(Res.string.easter_egg),
                getString(Res.string.supreme_leader_and_tower_boss_have_default_music)
            )
        }
    }
}