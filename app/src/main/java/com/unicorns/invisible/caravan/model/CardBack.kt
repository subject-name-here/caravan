package com.unicorns.invisible.caravan.model

import com.unicorns.invisible.caravan.R
import kotlinx.serialization.Serializable


@Serializable
enum class CardBack(
    val nameIdWithBackFileName: Pair<Int, String>,
    val currency: Currency = Currency.CAPS
) {
    STANDARD(
        R.string.standard_deck_name_1 to "FNV_Caravan_card_back_-_standard.webp",
        Currency.NOT_FOR_SALE
    ),
    STANDARD_UNCOMMON(
        R.string.standard_deck_name_2 to "FNV_Caravan_card_back_-_standard.webp",
        Currency.NOT_FOR_SALE
    ),
    STANDARD_RARE(
        R.string.standard_deck_name_3 to "FNV_Caravan_card_back_-_standard.webp",
        Currency.NOT_FOR_SALE
    ),
    STANDARD_MYTHIC(
        R.string.standard_deck_name_4 to "FNV_Caravan_card_back_-_standard.webp",
        Currency.NOT_FOR_SALE
    ),
    STANDARD_LEGENDARY(
        R.string.standard_deck_name_5 to "FNV_Caravan_card_back_-_standard.webp",
        Currency.NOT_FOR_SALE
    ),
    TOPS(R.string.tops_deck_name to "FNV_Caravan_card_back_-_Tops.webp"),
    TOPS_RED(R.string.tops_deck_alt_name to "tops_alt.webp"),
    ULTRA_LUXE(R.string.ultra_luxe_deck_name to "FNV_Caravan_card_back_-_Ultra-Luxe.webp"),
    ULTRA_LUXE_CRIME(R.string.ultra_luxe_deck_alt_name to "ultra_luxe_alt.webp"),
    GOMORRAH(R.string.gomorrah_deck_name to "FNV_Caravan_card_back_-_Gomorrah.webp"),
    GOMORRAH_DARK(R.string.gomorrah_deck_alt_name to "gomorrah_alt.webp"),
    LUCKY_38(R.string.lucky_38_deck_name to "FNV_Caravan_card_back_-_Lucky_38.webp"),
    LUCKY_38_SPECIAL(R.string.lucky_38_deck_alt_name to "lucky_38_alt.webp"),
    VAULT_21_DAY(R.string.vault_21_deck_name to "standard_alt.webp"),
    VAULT_21_NIGHT(R.string.vault_21_deck_alt_name to "sm_alt.webp"),
    SIERRA_MADRE_DIRTY(
        R.string.sierra_madre_deck_name to "FNV_Caravan_card_back_-_Sierra_Madre.webp",
        Currency.SIERRA_MADRE_CHIPS
    ),
    SIERRA_MADRE_CLEAN(
        R.string.sierra_madre_deck_alt_name to "sm_clean.webp",
        Currency.SIERRA_MADRE_CHIPS
    ),

    CHINESE(R.string.chinese_deck_name to "ccp_back.webp", Currency.SIERRA_MADRE_CHIPS),
    ENCLAVE(R.string.enclave_deck_name to "enclave_back.webp", Currency.SIERRA_MADRE_CHIPS),

    NCR(R.string.fnv_deck_west_name to "ncr_back.webp", Currency.NOT_FOR_SALE),
    LEGION(R.string.fnv_deck_east_name to "legion_back.webp", Currency.NOT_FOR_SALE),
    MADNESS(R.string.madness_deck_name to "madness_back.webp", Currency.NOT_FOR_SALE),
    VIKING(R.string.viking_deck_name to "viking_back.webp", Currency.NOT_FOR_SALE);

    fun getRarityMult(): Double {
        return when (this) {
            STANDARD -> 1.0
            STANDARD_UNCOMMON -> 2.0
            STANDARD_RARE -> 3.0
            STANDARD_MYTHIC -> 4.0
            STANDARD_LEGENDARY -> 5.0
            TOPS -> 1.0
            TOPS_RED -> 3.0
            ULTRA_LUXE -> 1.0
            ULTRA_LUXE_CRIME -> 3.0
            GOMORRAH -> 1.0
            GOMORRAH_DARK -> 3.0
            LUCKY_38 -> 1.0
            LUCKY_38_SPECIAL -> 3.0
            VAULT_21_DAY -> 1.0
            VAULT_21_NIGHT -> 3.0
            SIERRA_MADRE_DIRTY -> 1.0
            SIERRA_MADRE_CLEAN -> 2.0
            NCR -> 1.0
            LEGION -> 1.0
            MADNESS -> 2.0
            CHINESE -> 2.5
            ENCLAVE -> 2.5
            VIKING -> 2.0
        }
    }

    companion object {
        const val BASE_CARD_COST = 10.0
    }
}