package com.unicorns.invisible.caravan.model

import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CardAtomic
import com.unicorns.invisible.caravan.model.primitives.CardFBomb
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CardNumber
import com.unicorns.invisible.caravan.model.primitives.CardNumberWW
import com.unicorns.invisible.caravan.model.primitives.CardWildWasteland
import com.unicorns.invisible.caravan.model.primitives.CardWithPrice
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.RankFace
import com.unicorns.invisible.caravan.model.primitives.RankNumber
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.model.primitives.WWType


fun getCardName(card: Card): String {
    return when (card) {
        is CardNumberWW -> getWildWastelandNumberName(card)
        is CardAtomic -> getNuclearCard()
        is CardFBomb -> getNuclearAltCard()
        is CardWildWasteland -> getWildWastelandName(card)
        is CardWithPrice -> {
            val back = card.getBack()
            when (back) {
                CardBack.STANDARD -> getStandardName(card)
                CardBack.GOMORRAH -> getGomorrahName(card)
                CardBack.TOPS -> getTopsName(card)
                CardBack.ULTRA_LUXE -> getUltraLuxeName(card)
                CardBack.LUCKY_38 -> if (card.getBackNumber() == 0) getLucky38Name(card) else getLucky38AltName(card)
                CardBack.VAULT_21 -> if (card.getBackNumber() == 0) getVault21Name(card) else getVault21AltName(card)
                CardBack.SIERRA_MADRE -> if (card.getBackNumber() == 0) getStandardName(card) else getSmCleanName(card)
                CardBack.CHINESE -> getChineseName(card)
                CardBack.ENCLAVE -> getEnclaveName(card)
                CardBack.MADNESS -> getMadnessName(card)
                CardBack.VIKING -> getVikingName(card)
                CardBack.FNV_FACTION -> if (card.getBackNumber() == 0) getNcrName(card) else getLegionName(card)
            }
        }
    }
}

private fun getNuclearCard(): String {
    return "wild/nuclear_front.webp"
}
private fun getNuclearAltCard(): String {
    return "wild/ccp_nuclear_front.webp"
}

private fun getSmCleanName(card: CardWithPrice): String {
    return "sm_clean/${getStandardName(card)}"
}

private fun getGomorrahName(card: CardWithPrice): String {
    return when (card) {
        is CardFaceSuited -> {
            when (card.rank to card.suit) {
                RankFace.JACK to Suit.CLUBS -> "FNV_Jack_of_Clubs_-_Gomorrah.webp"
                RankFace.JACK to Suit.DIAMONDS -> "FNV_Jack_of_Diamonds_-_Gomorrah.webp"
                RankFace.JACK to Suit.HEARTS -> "FNV_Jack_of_Hearts_-_Gomorrah.webp"
                RankFace.JACK to Suit.SPADES -> "FNV_Jack_of_Spades_-_Gomorrah.webp"
                RankFace.QUEEN to Suit.CLUBS -> "FNV_Queen_of_Clubs_-_Gomorrah.webp"
                RankFace.QUEEN to Suit.DIAMONDS -> "FNV_Queen_of_Diamonds_-_Gomorrah.webp"
                RankFace.QUEEN to Suit.HEARTS -> "FNV_Queen_of_Hearts_-_Gomorrah.webp"
                RankFace.QUEEN to Suit.SPADES -> "FNV_Queen_of_Spades_-_Gomorrah.webp"
                RankFace.KING to Suit.CLUBS -> "FNV_King_of_Clubs_-_Gomorrah.webp"
                RankFace.KING to Suit.DIAMONDS -> "FNV_King_of_Diamonds_-_Gomorrah.webp"
                RankFace.KING to Suit.HEARTS -> "FNV_King_of_Hearts_-_Gomorrah.webp"
                RankFace.KING to Suit.SPADES -> "FNV_King_of_Spades_-_Gomorrah.webp"
                else -> getNuclearAltCard()
            }
        }
        is CardJoker -> {
            when (card.number) {
                CardJoker.Number.ONE -> "FNV_Joker_1_-_Gomorrah.webp"
                CardJoker.Number.TWO -> "FNV_Joker_2_-_Gomorrah.webp"
            }
        }
        is CardNumber -> {
            when (card.rank to card.suit) {
                RankNumber.ACE to Suit.CLUBS -> "FNV_Ace_of_Clubs_-_Gomorrah.webp"
                RankNumber.ACE to Suit.DIAMONDS -> "FNV_Ace_of_Diamonds_-_Gomorrah.webp"
                RankNumber.ACE to Suit.HEARTS -> "FNV_Ace_of_Hearts_-_Gomorrah.webp"
                RankNumber.ACE to Suit.SPADES -> "FNV_Ace_of_Spades_-_Gomorrah.webp"
                RankNumber.TWO to Suit.CLUBS -> "FNV_2_of_Clubs_-_Gomorrah.webp"
                RankNumber.TWO to Suit.DIAMONDS -> "FNV_2_of_Diamonds_-_Gomorrah.webp"
                RankNumber.TWO to Suit.HEARTS -> "FNV_2_of_Hearts_-_Gomorrah.webp"
                RankNumber.TWO to Suit.SPADES -> "FNV_2_of_Spades_-_Gomorrah.webp"
                RankNumber.THREE to Suit.CLUBS -> "FNV_3_of_Clubs_-_Gomorrah.webp"
                RankNumber.THREE to Suit.DIAMONDS -> "FNV_3_of_Diamonds_-_Gomorrah.webp"
                RankNumber.THREE to Suit.HEARTS -> "FNV_3_of_Hearts_-_Gomorrah.webp"
                RankNumber.THREE to Suit.SPADES -> "FNV_3_of_Spades_-_Gomorrah.webp"
                RankNumber.FOUR to Suit.CLUBS -> "FNV_4_of_Clubs_-_Gomorrah.webp"
                RankNumber.FOUR to Suit.DIAMONDS -> "FNV_4_of_Diamonds_-_Gomorrah.webp"
                RankNumber.FOUR to Suit.HEARTS -> "FNV_4_of_Hearts_-_Gomorrah.webp"
                RankNumber.FOUR to Suit.SPADES -> "FNV_4_of_Spades_-_Gomorrah.webp"
                RankNumber.FIVE to Suit.CLUBS -> "FNV_5_of_Clubs_-_Gomorrah.webp"
                RankNumber.FIVE to Suit.DIAMONDS -> "FNV_5_of_Diamonds_-_Gomorrah.webp"
                RankNumber.FIVE to Suit.HEARTS -> "FNV_5_of_Hearts_-_Gomorrah.webp"
                RankNumber.FIVE to Suit.SPADES -> "FNV_5_of_Spades_-_Gomorrah.webp"
                RankNumber.SIX to Suit.CLUBS -> "FNV_6_of_Clubs_-_Gomorrah.webp"
                RankNumber.SIX to Suit.DIAMONDS -> "FNV_6_of_Diamonds_-_Gomorrah.webp"
                RankNumber.SIX to Suit.HEARTS -> "FNV_6_of_Hearts_-_Gomorrah.webp"
                RankNumber.SIX to Suit.SPADES -> "FNV_6_of_Spades_-_Gomorrah.webp"
                RankNumber.SEVEN to Suit.CLUBS -> "FNV_7_of_Clubs_-_Gomorrah.webp"
                RankNumber.SEVEN to Suit.DIAMONDS -> "FNV_7_of_Diamonds_-_Gomorrah.webp"
                RankNumber.SEVEN to Suit.HEARTS -> "FNV_7_of_Hearts_-_Gomorrah.webp"
                RankNumber.SEVEN to Suit.SPADES -> "FNV_7_of_Spades_-_Gomorrah.webp"
                RankNumber.EIGHT to Suit.CLUBS -> "FNV_8_of_Clubs_-_Gomorrah.webp"
                RankNumber.EIGHT to Suit.DIAMONDS -> "FNV_8_of_Diamonds_-_Gomorrah.webp"
                RankNumber.EIGHT to Suit.HEARTS -> "FNV_8_of_Hearts_-_Gomorrah.webp"
                RankNumber.EIGHT to Suit.SPADES -> "FNV_8_of_Spades_-_Gomorrah.webp"
                RankNumber.NINE to Suit.CLUBS -> "FNV_9_of_Clubs_-_Gomorrah.webp"
                RankNumber.NINE to Suit.DIAMONDS -> "FNV_9_of_Diamonds_-_Gomorrah.webp"
                RankNumber.NINE to Suit.HEARTS -> "FNV_9_of_Hearts_-_Gomorrah.webp"
                RankNumber.NINE to Suit.SPADES -> "FNV_9_of_Spades_-_Gomorrah.webp"
                RankNumber.TEN to Suit.CLUBS -> "FNV_10_of_Clubs_-_Gomorrah.webp"
                RankNumber.TEN to Suit.DIAMONDS -> "FNV_10_of_Diamonds_-_Gomorrah.webp"
                RankNumber.TEN to Suit.HEARTS -> "FNV_10_of_Hearts_-_Gomorrah.webp"
                RankNumber.TEN to Suit.SPADES -> "FNV_10_of_Spades_-_Gomorrah.webp"
                else -> getNuclearAltCard()
            }
        }
    }
}

private fun getStandardName(card: CardWithPrice): String {
    return when (card) {
        is CardFaceSuited -> {
            when (card.rank to card.suit) {
                RankFace.JACK to Suit.CLUBS -> "FNV_Jack_of_Clubs.webp"
                RankFace.JACK to Suit.DIAMONDS -> "FNV_Jack_of_Diamonds.webp"
                RankFace.JACK to Suit.HEARTS -> "FNV_Jack_of_Hearts.webp"
                RankFace.JACK to Suit.SPADES -> "FNV_Jack_of_Spades.webp"
                RankFace.QUEEN to Suit.CLUBS -> "FNV_Queen_of_Clubs.webp"
                RankFace.QUEEN to Suit.DIAMONDS -> "FNV_Queen_of_Diamonds.webp"
                RankFace.QUEEN to Suit.HEARTS -> "FNV_Queen_of_Hearts.webp"
                RankFace.QUEEN to Suit.SPADES -> "FNV_Queen_of_Spades.webp"
                RankFace.KING to Suit.CLUBS -> "FNV_King_of_Clubs.webp"
                RankFace.KING to Suit.DIAMONDS -> "FNV_King_of_Diamonds.webp"
                RankFace.KING to Suit.HEARTS -> "FNV_King_of_Hearts.webp"
                RankFace.KING to Suit.SPADES -> "FNV_King_of_Spades.webp"
                else -> getNuclearAltCard()
            }
        }
        is CardJoker -> {
            when (card.number) {
                CardJoker.Number.ONE -> "FNV_Joker_1.webp"
                CardJoker.Number.TWO -> "FNV_Joker_2.webp"
            }
        }
        is CardNumber -> {
            when (card.rank to card.suit) {
                RankNumber.ACE to Suit.CLUBS -> "FNV_Ace_of_Clubs.webp"
                RankNumber.ACE to Suit.DIAMONDS -> "FNV_Ace_of_Diamonds.webp"
                RankNumber.ACE to Suit.HEARTS -> "FNV_Ace_of_Hearts.webp"
                RankNumber.ACE to Suit.SPADES -> "FNV_Ace_of_Spades.webp"
                RankNumber.TWO to Suit.CLUBS -> "FNV_2_of_Clubs.webp"
                RankNumber.TWO to Suit.DIAMONDS -> "FNV_2_of_Diamonds.webp"
                RankNumber.TWO to Suit.HEARTS -> "FNV_2_of_Hearts.webp"
                RankNumber.TWO to Suit.SPADES -> "FNV_2_of_Spades.webp"
                RankNumber.THREE to Suit.CLUBS -> "FNV_3_of_Clubs.webp"
                RankNumber.THREE to Suit.DIAMONDS -> "FNV_3_of_Diamonds.webp"
                RankNumber.THREE to Suit.HEARTS -> "FNV_3_of_Hearts.webp"
                RankNumber.THREE to Suit.SPADES -> "FNV_3_of_Spades.webp"
                RankNumber.FOUR to Suit.CLUBS -> "FNV_4_of_Clubs.webp"
                RankNumber.FOUR to Suit.DIAMONDS -> "FNV_4_of_Diamonds.webp"
                RankNumber.FOUR to Suit.HEARTS -> "FNV_4_of_Hearts.webp"
                RankNumber.FOUR to Suit.SPADES -> "FNV_4_of_Spades.webp"
                RankNumber.FIVE to Suit.CLUBS -> "FNV_5_of_Clubs.webp"
                RankNumber.FIVE to Suit.DIAMONDS -> "FNV_5_of_Diamonds.webp"
                RankNumber.FIVE to Suit.HEARTS -> "FNV_5_of_Hearts.webp"
                RankNumber.FIVE to Suit.SPADES -> "FNV_5_of_Spades.webp"
                RankNumber.SIX to Suit.CLUBS -> "FNV_6_of_Clubs.webp"
                RankNumber.SIX to Suit.DIAMONDS -> "FNV_6_of_Diamonds.webp"
                RankNumber.SIX to Suit.HEARTS -> "FNV_6_of_Hearts.webp"
                RankNumber.SIX to Suit.SPADES -> "FNV_6_of_Spades.webp"
                RankNumber.SEVEN to Suit.CLUBS -> "FNV_7_of_Clubs.webp"
                RankNumber.SEVEN to Suit.DIAMONDS -> "FNV_7_of_Diamonds.webp"
                RankNumber.SEVEN to Suit.HEARTS -> "FNV_7_of_Hearts.webp"
                RankNumber.SEVEN to Suit.SPADES -> "FNV_7_of_Spades.webp"
                RankNumber.EIGHT to Suit.CLUBS -> "FNV_8_of_Clubs.webp"
                RankNumber.EIGHT to Suit.DIAMONDS -> "FNV_8_of_Diamonds.webp"
                RankNumber.EIGHT to Suit.HEARTS -> "FNV_8_of_Hearts.webp"
                RankNumber.EIGHT to Suit.SPADES -> "FNV_8_of_Spades.webp"
                RankNumber.NINE to Suit.CLUBS -> "FNV_9_of_Clubs.webp"
                RankNumber.NINE to Suit.DIAMONDS -> "FNV_9_of_Diamonds.webp"
                RankNumber.NINE to Suit.HEARTS -> "FNV_9_of_Hearts.webp"
                RankNumber.NINE to Suit.SPADES -> "FNV_9_of_Spades.webp"
                RankNumber.TEN to Suit.CLUBS -> "FNV_10_of_Clubs.webp"
                RankNumber.TEN to Suit.DIAMONDS -> "FNV_10_of_Diamonds.webp"
                RankNumber.TEN to Suit.HEARTS -> "FNV_10_of_Hearts.webp"
                RankNumber.TEN to Suit.SPADES -> "FNV_10_of_Spades.webp"
                else -> getNuclearAltCard()
            }
        }
    }
}

private fun getTopsName(card: CardWithPrice): String {
    return when (card) {
        is CardFaceSuited -> {
            when (card.rank to card.suit) {
                RankFace.JACK to Suit.CLUBS -> "FNV_Jack_of_Clubs_-_Tops.webp"
                RankFace.JACK to Suit.DIAMONDS -> "FNV_Jack_of_Diamonds_-_Tops.webp"
                RankFace.JACK to Suit.HEARTS -> "FNV_Jack_of_Hearts_-_Tops.webp"
                RankFace.JACK to Suit.SPADES -> "FNV_Jack_of_Spades_-_Tops.webp"
                RankFace.QUEEN to Suit.CLUBS -> "FNV_Queen_of_Clubs_-_Tops.webp"
                RankFace.QUEEN to Suit.DIAMONDS -> "FNV_Queen_of_Diamonds_-_Tops.webp"
                RankFace.QUEEN to Suit.HEARTS -> "FNV_Queen_of_Hearts_-_Tops.webp"
                RankFace.QUEEN to Suit.SPADES -> "FNV_Queen_of_Spades_-_Tops.webp"
                RankFace.KING to Suit.CLUBS -> "FNV_King_of_Clubs_-_Tops.webp"
                RankFace.KING to Suit.DIAMONDS -> "FNV_King_of_Diamonds_-_Tops.webp"
                RankFace.KING to Suit.HEARTS -> "FNV_King_of_Hearts_-_Tops.webp"
                RankFace.KING to Suit.SPADES -> "FNV_King_of_Spades_-_Tops.webp"
                else -> getNuclearAltCard()
            }
        }
        is CardJoker -> getStandardName(card)
        is CardNumber -> {
            if (card.rank == RankNumber.ACE && card.suit == Suit.SPADES) {
                "FNV_Ace_of_Spades_-_Tops.webp"
            } else {
                getStandardName(card)
            }
        }
    }
}

private fun getUltraLuxeName(card: CardWithPrice): String {
    return when (card) {
        is CardFaceSuited -> {
            when (card.rank to card.suit) {
                RankFace.JACK to Suit.CLUBS -> "FNV_Jack_of_Clubs_-_Ultra-Luxe.webp"
                RankFace.JACK to Suit.DIAMONDS -> "FNV_Jack_of_Diamonds_-_Ultra-Luxe.webp"
                RankFace.JACK to Suit.HEARTS -> "FNV_Jack_of_Hearts_-_Ultra-Luxe.webp"
                RankFace.JACK to Suit.SPADES -> "FNV_Jack_of_Spades_-_Ultra-Luxe.webp"
                RankFace.QUEEN to Suit.CLUBS -> "FNV_Queen_of_Clubs_-_Ultra-Luxe.webp"
                RankFace.QUEEN to Suit.DIAMONDS -> "FNV_Queen_of_Diamonds_-_Ultra-Luxe.webp"
                RankFace.QUEEN to Suit.HEARTS -> "FNV_Queen_of_Hearts_-_Ultra-Luxe.webp"
                RankFace.QUEEN to Suit.SPADES -> "FNV_Queen_of_Spades_-_Ultra-Luxe.webp"
                RankFace.KING to Suit.CLUBS -> "FNV_King_of_Clubs_-_Ultra-Luxe.webp"
                RankFace.KING to Suit.DIAMONDS -> "FNV_King_of_Diamonds_-_Ultra-Luxe.webp"
                RankFace.KING to Suit.HEARTS -> "FNV_King_of_Hearts_-_Ultra-Luxe.webp"
                RankFace.KING to Suit.SPADES -> "FNV_King_of_Spades_-_Ultra-Luxe.webp"
                else -> getNuclearAltCard()
            }
        }
        is CardJoker -> getStandardName(card)
        is CardNumber -> {
            when (card.rank to card.suit) {
                RankNumber.ACE to Suit.CLUBS -> "FNV_Ace_of_Clubs_-_Ultra-Luxe.webp"
                RankNumber.ACE to Suit.DIAMONDS -> "FNV_Ace_of_Diamonds_-_Ultra-Luxe.webp"
                RankNumber.ACE to Suit.HEARTS -> "FNV_Ace_of_Hearts_-_Ultra-Luxe.webp"
                RankNumber.ACE to Suit.SPADES -> "FNV_Ace_of_Spades_-_Ultra-Luxe.webp"
                else -> getStandardName(card)
            }
        }
    }
}

private fun getNcrName(card: CardWithPrice): String {
    // TODO: apply stamps
    return getStandardName(card)
}

private fun getLucky38Name(card: CardWithPrice): String {
    return when (card) {
        is CardFaceSuited -> getStandardName(card)
        is CardJoker -> getStandardName(card)
        is CardNumber -> {
            if (card.rank == RankNumber.ACE && card.suit == Suit.SPADES) {
                "FNV_Ace_of_Spades_-_Lucky_38.webp"
            } else {
                getStandardName(card)
            }
        }
    }
}

private fun getLucky38AltName(card: CardWithPrice): String {
    if (card.rank == Rank.JOKER) {
        return if (card.suit == Suit.HEARTS) {
            "lucky38ALT/1J.webp"
        } else {
            "lucky38ALT/2J.webp"
        }
    }
    return getSvgName(card, "lucky38ALT")
}

private fun getVault21AltName(card: CardWithPrice): String {
    return getSvgName(card, "vault21ALT")
}

private fun getVault21Name(card: CardWithPrice): String {
    return getSvgName(card, "vault21")
}

private fun getSvgName(card: CardWithPrice, dirName: String): String {
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

private fun getWildWastelandNumberName(card: CardNumberWW): String {
    val prefix = "ww_deck"
    return "$prefix/${card.rank.value}_${card.suit.name.first().uppercase()}.webp"
}

private fun getWildWastelandName(card: CardWildWasteland): String {
    val prefix = "ww_deck"
    return "$prefix/" + when (card.type) {
        WWType.CAZADOR -> "cazador.webp"
        WWType.DIFFICULT_PETE -> "difficult_pete.webp"
        WWType.FEV -> "fev.webp"
        WWType.MUGGY -> "muggy.webp"
        WWType.UFO -> "ufo.webp"
        WWType.YES_MAN -> "yes_man.webp"
    }
}

private fun getMadnessName(card: CardWithPrice): String {
    return getOGCardName(card, "madness")
}
private fun getChineseName(card: CardWithPrice): String {
    return getOGCardName(card, "chinese")
}
private fun getEnclaveName(card: CardWithPrice): String {
    return getOGCardName(card, "enclave")
}
private fun getVikingName(card: CardWithPrice): String {
    return getOGCardName(card, "viking")
}
private fun getLegionName(card: CardWithPrice): String {
    return getOGCardName(card, "legion")
}

private fun getOGCardName(card: CardWithPrice, dirName: String): String {
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