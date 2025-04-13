package com.unicorns.invisible.caravan.model.primitives

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.unicorns.invisible.caravan.model.CardBack
import kotlinx.serialization.Serializable


sealed class Card {
    var handAnimationMark by mutableStateOf(AnimationMark.NEW)
    var caravanAnimationMark by mutableStateOf(AnimationMark.NEW)

    enum class AnimationMark {
        STABLE,
        NEW,
        MOVING_OUT,
        MOVING_OUT_ALT,
        MOVED_OUT;
    }
}

sealed class CardModifier : Card()

sealed class CardBase : Card() {
    abstract val rank: RankNumber
    abstract val suit: Suit
}

@Serializable
sealed interface CardWithPrice {
    fun getBack(): CardBack
    fun getRankPriceMult(): Double
    fun getPriceOfCard(): Int {
        val base = 15.0
        val back = getBack()
        val rarityMult = back.getRarityMult()
        return (base * getRankPriceMult() * rarityMult).toInt()
    }
}

@Serializable
class CardNumber(
    override val rank: RankNumber,
    override val suit: Suit,
    val cardBack: CardBack,
) : CardBase(), CardWithPrice {
    override fun getBack() = cardBack
    override fun getRankPriceMult(): Double {
        return when (rank) {
            RankNumber.ACE, RankNumber.SIX -> 1.0
            RankNumber.TWO, RankNumber.THREE -> 0.8
            RankNumber.FOUR, RankNumber.FIVE -> 0.9
            RankNumber.SEVEN, RankNumber.EIGHT -> 1.1
            RankNumber.NINE -> 1.15
            RankNumber.TEN -> 1.2
        }
    }
}
class CardNumberWW(
    override val rank: RankNumber,
    override val suit: Suit,
) : CardBase()

@Serializable
sealed class CardFace : CardModifier(), CardWithPrice {
    abstract val rank: RankFace
    abstract val cardBack: CardBack

    override fun getBack() = cardBack
    override fun getRankPriceMult(): Double {
        return when (rank) {
            RankFace.JACK -> 1.35
            RankFace.QUEEN -> 1.0
            RankFace.KING -> 1.5
            RankFace.JOKER -> 1.75
        }
    }
}
@Serializable
class CardJoker(
    val number: Number,
    override val cardBack: CardBack,
) : CardFace() {
    enum class Number(val n: Int) {
        ONE(1),
        TWO(2)
    }

    override val rank
        get() = RankFace.JOKER
}
@Serializable
class CardFaceSuited(
    override val rank: RankFace,
    val suit: Suit,
    override val cardBack: CardBack,
) : CardFace()

enum class WWType {
    CAZADOR,
    YES_MAN,
    MUGGY,
    UFO,
    DIFFICULT_PETE,
    FEV
}
class CardWildWasteland(val type: WWType) : CardModifier()

sealed class CardNuclear : CardModifier()
class CardAtomic(val dummy: Unit = Unit) : CardNuclear()
class CardFBomb(val dummy: Unit = Unit) : CardNuclear()