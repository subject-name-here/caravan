package com.unicorns.invisible.caravan.model.challenge

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.claim_caps
import caravan.composeapp.generated.resources.claim_tickets
import com.unicorns.invisible.caravan.saveGlobal
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.getString


@Serializable
sealed interface ChallengeInfinite : Challenge {
    override fun getXp(): Int = 25

    override suspend fun reward(): List<Pair<String, () -> Unit>> {
        val caps = 15
        val tickets = 1
        return listOf(
            getString(Res.string.claim_caps, caps.toString()) to {
                saveGlobal.capsInHand += caps
            },
            getString(Res.string.claim_tickets, tickets.toString()) to {
                saveGlobal.tickets += tickets
            }
        )
    }

    fun restartChallenge()
}