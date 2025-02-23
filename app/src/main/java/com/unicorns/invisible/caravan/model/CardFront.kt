package com.unicorns.invisible.caravan.model

import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit


fun getCardName(card: Card): String {
    return when (card.back) {
        CardBack.STANDARD -> getStandardName(card)
        CardBack.GOMORRAH -> getGomorrahName(card)
        CardBack.TOPS -> getTopsName(card)
        CardBack.ULTRA_LUXE -> getUltraLuxeName(card)
        CardBack.LUCKY_38 -> if (!card.isAlt) getLucky38Name(card) else getLucky38AltName(card)
        CardBack.VAULT_21 -> if (!card.isAlt) getVault21Name(card) else getVault21AltName(card)
        CardBack.SIERRA_MADRE -> if (!card.isAlt) getStandardName(card) else getSmCleanName(card)

        CardBack.WILD_WASTELAND -> getWildWastelandName(card)
        CardBack.NUCLEAR -> if (!card.isAlt) getNuclearCard() else getNuclearAltCard()

        CardBack.CHINESE -> getChineseName(card)
        CardBack.ENCLAVE -> getEnclaveName(card)
        CardBack.MADNESS -> getMadnessName(card)
        CardBack.VIKING -> getVikingName(card)
        CardBack.NCR -> getNcrName(card)
        CardBack.LEGION -> getLegionName(card)
    }
}

private fun getNuclearCard(): String {
    return "wild/nuclear_front.webp"
}
private fun getNuclearAltCard(): String {
    return "wild/ccp_nuclear_front.webp"
}

private fun getSmCleanName(card: Card): String {
    return "sm_clean/${getStandardName(card)}"
}

private fun getGomorrahName(card: Card): String {
    return when (card.rank to card.suit) {
        Rank.TWO to Suit.CLUBS -> "FNV_2_of_Clubs_-_Gomorrah.webp"
        Rank.TWO to Suit.DIAMONDS -> "FNV_2_of_Diamonds_-_Gomorrah.webp"
        Rank.TWO to Suit.HEARTS -> "FNV_2_of_Hearts_-_Gomorrah.webp"
        Rank.TWO to Suit.SPADES -> "FNV_2_of_Spades_-_Gomorrah.webp"
        Rank.THREE to Suit.CLUBS -> "FNV_3_of_Clubs_-_Gomorrah.webp"
        Rank.THREE to Suit.DIAMONDS -> "FNV_3_of_Diamonds_-_Gomorrah.webp"
        Rank.THREE to Suit.HEARTS -> "FNV_3_of_Hearts_-_Gomorrah.webp"
        Rank.THREE to Suit.SPADES -> "FNV_3_of_Spades_-_Gomorrah.webp"
        Rank.FOUR to Suit.CLUBS -> "FNV_4_of_Clubs_-_Gomorrah.webp"
        Rank.FOUR to Suit.DIAMONDS -> "FNV_4_of_Diamonds_-_Gomorrah.webp"
        Rank.FOUR to Suit.HEARTS -> "FNV_4_of_Hearts_-_Gomorrah.webp"
        Rank.FOUR to Suit.SPADES -> "FNV_4_of_Spades_-_Gomorrah.webp"
        Rank.FIVE to Suit.CLUBS -> "FNV_5_of_Clubs_-_Gomorrah.webp"
        Rank.FIVE to Suit.DIAMONDS -> "FNV_5_of_Diamonds_-_Gomorrah.webp"
        Rank.FIVE to Suit.HEARTS -> "FNV_5_of_Hearts_-_Gomorrah.webp"
        Rank.FIVE to Suit.SPADES -> "FNV_5_of_Spades_-_Gomorrah.webp"
        Rank.SIX to Suit.CLUBS -> "FNV_6_of_Clubs_-_Gomorrah.webp"
        Rank.SIX to Suit.DIAMONDS -> "FNV_6_of_Diamonds_-_Gomorrah.webp"
        Rank.SIX to Suit.HEARTS -> "FNV_6_of_Hearts_-_Gomorrah.webp"
        Rank.SIX to Suit.SPADES -> "FNV_6_of_Spades_-_Gomorrah.webp"
        Rank.SEVEN to Suit.CLUBS -> "FNV_7_of_Clubs_-_Gomorrah.webp"
        Rank.SEVEN to Suit.DIAMONDS -> "FNV_7_of_Diamonds_-_Gomorrah.webp"
        Rank.SEVEN to Suit.HEARTS -> "FNV_7_of_Hearts_-_Gomorrah.webp"
        Rank.SEVEN to Suit.SPADES -> "FNV_7_of_Spades_-_Gomorrah.webp"
        Rank.EIGHT to Suit.CLUBS -> "FNV_8_of_Clubs_-_Gomorrah.webp"
        Rank.EIGHT to Suit.DIAMONDS -> "FNV_8_of_Diamonds_-_Gomorrah.webp"
        Rank.EIGHT to Suit.HEARTS -> "FNV_8_of_Hearts_-_Gomorrah.webp"
        Rank.EIGHT to Suit.SPADES -> "FNV_8_of_Spades_-_Gomorrah.webp"
        Rank.NINE to Suit.CLUBS -> "FNV_9_of_Clubs_-_Gomorrah.webp"
        Rank.NINE to Suit.DIAMONDS -> "FNV_9_of_Diamonds_-_Gomorrah.webp"
        Rank.NINE to Suit.HEARTS -> "FNV_9_of_Hearts_-_Gomorrah.webp"
        Rank.NINE to Suit.SPADES -> "FNV_9_of_Spades_-_Gomorrah.webp"
        Rank.TEN to Suit.CLUBS -> "FNV_10_of_Clubs_-_Gomorrah.webp"
        Rank.TEN to Suit.DIAMONDS -> "FNV_10_of_Diamonds_-_Gomorrah.webp"
        Rank.TEN to Suit.HEARTS -> "FNV_10_of_Hearts_-_Gomorrah.webp"
        Rank.TEN to Suit.SPADES -> "FNV_10_of_Spades_-_Gomorrah.webp"
        Rank.JACK to Suit.CLUBS -> "FNV_Jack_of_Clubs_-_Gomorrah.webp"
        Rank.JACK to Suit.DIAMONDS -> "FNV_Jack_of_Diamonds_-_Gomorrah.webp"
        Rank.JACK to Suit.HEARTS -> "FNV_Jack_of_Hearts_-_Gomorrah.webp"
        Rank.JACK to Suit.SPADES -> "FNV_Jack_of_Spades_-_Gomorrah.webp"
        Rank.QUEEN to Suit.CLUBS -> "FNV_Queen_of_Clubs_-_Gomorrah.webp"
        Rank.QUEEN to Suit.DIAMONDS -> "FNV_Queen_of_Diamonds_-_Gomorrah.webp"
        Rank.QUEEN to Suit.HEARTS -> "FNV_Queen_of_Hearts_-_Gomorrah.webp"
        Rank.QUEEN to Suit.SPADES -> "FNV_Queen_of_Spades_-_Gomorrah.webp"
        Rank.KING to Suit.CLUBS -> "FNV_King_of_Clubs_-_Gomorrah.webp"
        Rank.KING to Suit.DIAMONDS -> "FNV_King_of_Diamonds_-_Gomorrah.webp"
        Rank.KING to Suit.HEARTS -> "FNV_King_of_Hearts_-_Gomorrah.webp"
        Rank.KING to Suit.SPADES -> "FNV_King_of_Spades_-_Gomorrah.webp"
        Rank.ACE to Suit.CLUBS -> "FNV_Ace_of_Clubs_-_Gomorrah.webp"
        Rank.ACE to Suit.DIAMONDS -> "FNV_Ace_of_Diamonds_-_Gomorrah.webp"
        Rank.ACE to Suit.HEARTS -> "FNV_Ace_of_Hearts_-_Gomorrah.webp"
        Rank.ACE to Suit.SPADES -> "FNV_Ace_of_Spades_-_Gomorrah.webp"
        Rank.JOKER to Suit.HEARTS -> "FNV_Joker_1_-_Gomorrah.webp"
        else -> "FNV_Joker_2_-_Gomorrah.webp"
    }
}

private fun getStandardName(card: Card): String {
    return when (card.rank to card.suit) {
        Rank.TWO to Suit.CLUBS -> "FNV_2_of_Clubs.webp"
        Rank.TWO to Suit.DIAMONDS -> "FNV_2_of_Diamonds.webp"
        Rank.TWO to Suit.HEARTS -> "FNV_2_of_Hearts.webp"
        Rank.TWO to Suit.SPADES -> "FNV_2_of_Spades.webp"
        Rank.THREE to Suit.CLUBS -> "FNV_3_of_Clubs.webp"
        Rank.THREE to Suit.DIAMONDS -> "FNV_3_of_Diamonds.webp"
        Rank.THREE to Suit.HEARTS -> "FNV_3_of_Hearts.webp"
        Rank.THREE to Suit.SPADES -> "FNV_3_of_Spades.webp"
        Rank.FOUR to Suit.CLUBS -> "FNV_4_of_Clubs.webp"
        Rank.FOUR to Suit.DIAMONDS -> "FNV_4_of_Diamonds.webp"
        Rank.FOUR to Suit.HEARTS -> "FNV_4_of_Hearts.webp"
        Rank.FOUR to Suit.SPADES -> "FNV_4_of_Spades.webp"
        Rank.FIVE to Suit.CLUBS -> "FNV_5_of_Clubs.webp"
        Rank.FIVE to Suit.DIAMONDS -> "FNV_5_of_Diamonds.webp"
        Rank.FIVE to Suit.HEARTS -> "FNV_5_of_Hearts.webp"
        Rank.FIVE to Suit.SPADES -> "FNV_5_of_Spades.webp"
        Rank.SIX to Suit.CLUBS -> "FNV_6_of_Clubs.webp"
        Rank.SIX to Suit.DIAMONDS -> "FNV_6_of_Diamonds.webp"
        Rank.SIX to Suit.HEARTS -> "FNV_6_of_Hearts.webp"
        Rank.SIX to Suit.SPADES -> "FNV_6_of_Spades.webp"
        Rank.SEVEN to Suit.CLUBS -> "FNV_7_of_Clubs.webp"
        Rank.SEVEN to Suit.DIAMONDS -> "FNV_7_of_Diamonds.webp"
        Rank.SEVEN to Suit.HEARTS -> "FNV_7_of_Hearts.webp"
        Rank.SEVEN to Suit.SPADES -> "FNV_7_of_Spades.webp"
        Rank.EIGHT to Suit.CLUBS -> "FNV_8_of_Clubs.webp"
        Rank.EIGHT to Suit.DIAMONDS -> "FNV_8_of_Diamonds.webp"
        Rank.EIGHT to Suit.HEARTS -> "FNV_8_of_Hearts.webp"
        Rank.EIGHT to Suit.SPADES -> "FNV_8_of_Spades.webp"
        Rank.NINE to Suit.CLUBS -> "FNV_9_of_Clubs.webp"
        Rank.NINE to Suit.DIAMONDS -> "FNV_9_of_Diamonds.webp"
        Rank.NINE to Suit.HEARTS -> "FNV_9_of_Hearts.webp"
        Rank.NINE to Suit.SPADES -> "FNV_9_of_Spades.webp"
        Rank.TEN to Suit.CLUBS -> "FNV_10_of_Clubs.webp"
        Rank.TEN to Suit.DIAMONDS -> "FNV_10_of_Diamonds.webp"
        Rank.TEN to Suit.HEARTS -> "FNV_10_of_Hearts.webp"
        Rank.TEN to Suit.SPADES -> "FNV_10_of_Spades.webp"
        Rank.JACK to Suit.CLUBS -> "FNV_Jack_of_Clubs.webp"
        Rank.JACK to Suit.DIAMONDS -> "FNV_Jack_of_Diamonds.webp"
        Rank.JACK to Suit.HEARTS -> "FNV_Jack_of_Hearts.webp"
        Rank.JACK to Suit.SPADES -> "FNV_Jack_of_Spades.webp"
        Rank.QUEEN to Suit.CLUBS -> "FNV_Queen_of_Clubs.webp"
        Rank.QUEEN to Suit.DIAMONDS -> "FNV_Queen_of_Diamonds.webp"
        Rank.QUEEN to Suit.HEARTS -> "FNV_Queen_of_Hearts.webp"
        Rank.QUEEN to Suit.SPADES -> "FNV_Queen_of_Spades.webp"
        Rank.KING to Suit.CLUBS -> "FNV_King_of_Clubs.webp"
        Rank.KING to Suit.DIAMONDS -> "FNV_King_of_Diamonds.webp"
        Rank.KING to Suit.HEARTS -> "FNV_King_of_Hearts.webp"
        Rank.KING to Suit.SPADES -> "FNV_King_of_Spades.webp"
        Rank.ACE to Suit.CLUBS -> "FNV_Ace_of_Clubs.webp"
        Rank.ACE to Suit.DIAMONDS -> "FNV_Ace_of_Diamonds.webp"
        Rank.ACE to Suit.HEARTS -> "FNV_Ace_of_Hearts.webp"
        Rank.ACE to Suit.SPADES -> "FNV_Ace_of_Spades.webp"
        Rank.JOKER to Suit.HEARTS -> "FNV_Joker_1.webp"
        else -> "FNV_Joker_2.webp"
    }
}

private fun getTopsName(card: Card): String {
    return when (card.rank to card.suit) {
        Rank.JACK to Suit.CLUBS -> "FNV_Jack_of_Clubs_-_Tops.webp"
        Rank.JACK to Suit.DIAMONDS -> "FNV_Jack_of_Diamonds_-_Tops.webp"
        Rank.JACK to Suit.HEARTS -> "FNV_Jack_of_Hearts_-_Tops.webp"
        Rank.JACK to Suit.SPADES -> "FNV_Jack_of_Spades_-_Tops.webp"
        Rank.QUEEN to Suit.CLUBS -> "FNV_Queen_of_Clubs_-_Tops.webp"
        Rank.QUEEN to Suit.DIAMONDS -> "FNV_Queen_of_Diamonds_-_Tops.webp"
        Rank.QUEEN to Suit.HEARTS -> "FNV_Queen_of_Hearts_-_Tops.webp"
        Rank.QUEEN to Suit.SPADES -> "FNV_Queen_of_Spades_-_Tops.webp"
        Rank.KING to Suit.CLUBS -> "FNV_King_of_Clubs_-_Tops.webp"
        Rank.KING to Suit.DIAMONDS -> "FNV_King_of_Diamonds_-_Tops.webp"
        Rank.KING to Suit.HEARTS -> "FNV_King_of_Hearts_-_Tops.webp"
        Rank.KING to Suit.SPADES -> "FNV_King_of_Spades_-_Tops.webp"
        Rank.ACE to Suit.SPADES -> "FNV_Ace_of_Spades_-_Tops.webp"
        else -> getStandardName(card)
    }
}

private fun getUltraLuxeName(card: Card): String {
    return when (card.rank to card.suit) {
        Rank.JACK to Suit.CLUBS -> "FNV_Jack_of_Clubs_-_Ultra-Luxe.webp"
        Rank.JACK to Suit.DIAMONDS -> "FNV_Jack_of_Diamonds_-_Ultra-Luxe.webp"
        Rank.JACK to Suit.HEARTS -> "FNV_Jack_of_Hearts_-_Ultra-Luxe.webp"
        Rank.JACK to Suit.SPADES -> "FNV_Jack_of_Spades_-_Ultra-Luxe.webp"
        Rank.QUEEN to Suit.CLUBS -> "FNV_Queen_of_Clubs_-_Ultra-Luxe.webp"
        Rank.QUEEN to Suit.DIAMONDS -> "FNV_Queen_of_Diamonds_-_Ultra-Luxe.webp"
        Rank.QUEEN to Suit.HEARTS -> "FNV_Queen_of_Hearts_-_Ultra-Luxe.webp"
        Rank.QUEEN to Suit.SPADES -> "FNV_Queen_of_Spades_-_Ultra-Luxe.webp"
        Rank.KING to Suit.CLUBS -> "FNV_King_of_Clubs_-_Ultra-Luxe.webp"
        Rank.KING to Suit.DIAMONDS -> "FNV_King_of_Diamonds_-_Ultra-Luxe.webp"
        Rank.KING to Suit.HEARTS -> "FNV_King_of_Hearts_-_Ultra-Luxe.webp"
        Rank.KING to Suit.SPADES -> "FNV_King_of_Spades_-_Ultra-Luxe.webp"
        Rank.ACE to Suit.CLUBS -> "FNV_Ace_of_Clubs_-_Ultra-Luxe.webp"
        Rank.ACE to Suit.DIAMONDS -> "FNV_Ace_of_Diamonds_-_Ultra-Luxe.webp"
        Rank.ACE to Suit.HEARTS -> "FNV_Ace_of_Hearts_-_Ultra-Luxe.webp"
        Rank.ACE to Suit.SPADES -> "FNV_Ace_of_Spades_-_Ultra-Luxe.webp"
        else -> getStandardName(card)
    }
}

private fun getNcrName(card: Card): String {
    return when (card.rank to card.suit) {
        Rank.JOKER to Suit.CLUBS -> "ncr/ncr_joker_2.webp"
        Rank.ACE to Suit.SPADES -> "ncr/ncr_ace_of_spades.webp"
        else -> getStandardName(card)
    }
}

private fun getLucky38Name(card: Card): String {
    return when (card.rank to card.suit) {
        Rank.ACE to Suit.SPADES -> "FNV_Ace_of_Spades_-_Lucky_38.webp"
        else -> getStandardName(card)
    }
}

private fun getLucky38AltName(card: Card): String {
    if (card.rank == Rank.JOKER) {
        return if (card.suit == Suit.HEARTS) {
            "lucky38ALT/1J.webp"
        } else {
            "lucky38ALT/2J.webp"
        }
    }
    return getSvgName(card, "lucky38ALT")
}

private fun getVault21AltName(card: Card): String {
    return getSvgName(card, "vault21ALT")
}

private fun getVault21Name(card: Card): String {
    return getSvgName(card, "vault21")
}

private fun getSvgName(card: Card, dirName: String): String {
    if (card.rank == Rank.JOKER) {
        return if (card.suit == Suit.HEARTS) {
            "$dirName/1J.svg"
        } else {
            "$dirName/2J.svg"
        }
    }

    val letter = when (card.rank.value) {
        in (2..9) -> card.rank.value.toString()
        else -> card.rank.name.first().toString()
    }
    val suit = card.suit.name.first()
    return "$dirName/$letter$suit.svg"
}

private fun getWildWastelandName(card: Card): String {
    val prefix = "ww_deck"
    if (card.rank.value <= 10) {
        return "$prefix/${card.rank.value}_${card.suit.name.first().uppercase()}.webp"
    }

    return "$prefix/" + when (card.getWildWastelandType()) {
        Card.WildWastelandCardType.CAZADOR -> "cazador.webp"
        Card.WildWastelandCardType.DIFFICULT_PETE -> "difficult_pete.webp"
        Card.WildWastelandCardType.FEV -> "fev.webp"
        Card.WildWastelandCardType.MUGGY -> "muggy.webp"
        Card.WildWastelandCardType.UFO -> "ufo.webp"
        Card.WildWastelandCardType.YES_MAN -> "yes_man.webp"
        null -> "" // TODO: card not found!!
    }
}

private fun getMadnessName(card: Card): String {
    return getOGCardName(card, "madness")
}
private fun getChineseName(card: Card): String {
    return getOGCardName(card, "chinese")
}
private fun getEnclaveName(card: Card): String {
    return getOGCardName(card, "enclave")
}
private fun getVikingName(card: Card): String {
    return getOGCardName(card, "viking")
}
private fun getLegionName(card: Card): String {
    return getOGCardName(card, "legion")
}

private fun getOGCardName(card: Card, dirName: String): String {
    if (card.rank == Rank.JOKER) {
        return if (card.suit == Suit.HEARTS) {
            "$dirName/Joker_1.webp"
        } else {
            "$dirName/Joker_2.webp"
        }
    }

    val letter = when (card.rank.value) {
        in (1..10) -> card.rank.value.toString()
        else -> card.rank.name.first().toString()
    }
    val suit = card.suit.name.first()
    return "$dirName/${letter}_$suit.webp"
}