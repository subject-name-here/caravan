package com.unicorns.invisible.caravan.cheats

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.save
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.playYesBeep

data object CheatFinalBossesHeroicMusic : Cheat {
    override fun getCode() = 845

    override fun onEnter(
        activity: MainActivity,
        showAlertDialog: (String, String) -> Unit
    ) {
        save.isHeroic = !save.isHeroic
        saveData(activity)
        playYesBeep(activity)

        if (save.isHeroic) {
            showAlertDialog(
                activity.getString(R.string.easter_egg),
                activity.getString(R.string.supreme_leader_and_tower_boss_have_new_music)
            )
        } else {
            showAlertDialog(
                activity.getString(R.string.easter_egg),
                activity.getString(R.string.supreme_leader_and_tower_boss_have_default_music)
            )
        }
    }
}