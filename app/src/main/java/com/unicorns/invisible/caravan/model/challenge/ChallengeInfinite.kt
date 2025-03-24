package com.unicorns.invisible.caravan.model.challenge

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.save
import kotlinx.serialization.Serializable


@Serializable
sealed interface ChallengeInfinite : Challenge {
    override fun reward(activity: MainActivity): List<Pair<String, () -> Unit>> {
        val caps = 15
        val tickets = 1
        return listOf(
            activity.getString(R.string.claim_caps, caps.toString()) to {
                save.capsInHand += caps
            },
            activity.getString(R.string.claim_tickets, tickets.toString()) to {
                save.tickets += tickets
            }
        )
    }

    fun restartChallenge()
}