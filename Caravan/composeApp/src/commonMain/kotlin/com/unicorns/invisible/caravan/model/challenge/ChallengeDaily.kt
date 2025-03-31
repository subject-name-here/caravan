package com.unicorns.invisible.caravan.model.challenge

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.claim_caps
import caravan.composeapp.generated.resources.claim_tickets
import com.unicorns.invisible.caravan.save
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.getString


@Serializable
sealed interface ChallengeDaily : Challenge {
    override suspend fun reward(): List<Pair<String, () -> Unit>> {
        val caps = 30
        val tickets = 2
        return listOf(
            getString(Res.string.claim_caps, caps.toString()) to {
                save.capsInHand += caps
            },
            getString(Res.string.claim_tickets, tickets.toString()) to {
                save.tickets += tickets
            }
        )
    }
}