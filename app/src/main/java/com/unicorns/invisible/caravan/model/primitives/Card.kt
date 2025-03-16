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
    fun getBackNumber(): Int
    fun getRankPriceMult(): Double
    fun getPriceOfCard(): Int {
        val base = 15.0
        val back = getBack()
        val backNumber = getBackNumber()
        val rarityMult = back.getRarityMult(backNumber)
        return (base * getRankPriceMult() * rarityMult).toInt()
    }
}

@Serializable
data class CardNumber(
    override val rank: RankNumber,
    override val suit: Suit,
    val back: CardBack,
    val backNumber: Int,
) : CardBase(), CardWithPrice {
    override fun getBack() = back
    override fun getBackNumber() = backNumber
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
data class CardNumberWW(
    override val rank: RankNumber,
    override val suit: Suit,
) : CardBase()

@Serializable
sealed class CardFace : CardModifier(), CardWithPrice {
    abstract val rank: RankFace
    abstract val back: CardBack
    abstract val backNumber: Int

    override fun getBack() = back
    override fun getBackNumber() = backNumber
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
data class CardJoker(
    val number: Number,
    override val back: CardBack,
    override val backNumber: Int
) : CardFace() {
    enum class Number {
        ONE,
        TWO
    }

    override val rank
        get() = RankFace.JOKER
}
@Serializable
data class CardFaceSuited(
    override val rank: RankFace,
    val suit: Suit,
    override val back: CardBack,
    override val backNumber: Int,
) : CardFace()

enum class WWType {
    CAZADOR,
    YES_MAN,
    MUGGY,
    UFO,
    DIFFICULT_PETE,
    FEV
}
data class CardWildWasteland(val type: WWType) : CardModifier()

sealed class CardNuclear : CardModifier()
data class CardAtomic(val dummy: Unit = Unit) : CardNuclear()
data class CardFBomb(val dummy: Unit = Unit) : CardNuclear()