package com.unicorns.invisible.caravan.multiplayer

import com.unicorns.invisible.caravan.model.primitives.Card
import kotlinx.serialization.Serializable


/**
 * 1 - drop caravan caravan_code
 * 2 - drop card hand_card_number from hand
 * 3 - put card hand_card_number from hand to top of caravan caravan_code
 * 4 - put card hand_card_number from hand on card card_in_caravan_number from caravan caravan_code
 *
 * CaravanCode: 0, 1, 2 - enemy's own caravans; -1, -2 , -3 - player's caravans.
 */


@Serializable
data class MoveResponse(
    val moveCode: Int,
    val caravanCode: Int,
    val handCardNumber: Int,
    val cardInCaravanNumber: Int,
    val newCardBackInHandCode: Int,
)