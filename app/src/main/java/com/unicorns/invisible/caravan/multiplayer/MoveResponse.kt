package com.unicorns.invisible.caravan.multiplayer

import kotlinx.serialization.Serializable
import org.json.JSONObject


/**
 * 0 - only take card from deck (for the game's start)
 * 1 - drop caravan caravan_code
 * 2 - drop card hand_card_number from hand
 * 3 - put card hand_card_number from hand to top of caravan caravan_code
 * 4 - put card hand_card_number from hand on card card_in_caravan_number from caravan caravan_code
 *
 * CaravanCode: 0, 1, 2 - enemy's own caravans; -1, -2 , -3 - player's caravans.
 */


@Serializable
data class MoveResponse(
    val moveCode: Int = 0,
    val caravanCode: Int = 0,
    val handCardNumber: Int = 0,
    val cardInCaravanNumber: Int = 0,
    var newCardInHandRank: Int = 0,
    var newCardInHandSuit: Int = 0,
    var newCardInHandBack: Int = 0,
    var symbolNumber: Int = 0
)

fun decodeMove(s: String): MoveResponse {
    val objS = s.dropLast(1).drop(1)
    val obj = JSONObject(objS)
    val fields = obj.getJSONObject("fields")
    return MoveResponse(
        moveCode = fields.getInt("move_code"),
        caravanCode = fields.getInt("caravan_code"),
        handCardNumber = fields.getInt("hand_card_number"),
        cardInCaravanNumber = fields.getInt("card_in_caravan_number"),
        newCardInHandBack = fields.getInt("new_card_back_in_hand_code"),
        newCardInHandSuit = fields.getInt("new_card_suit_in_hand_code"),
        newCardInHandRank = fields.getInt("new_card_rank_in_hand_code"),
        symbolNumber = fields.optInt("symbol", 0),
    )
}