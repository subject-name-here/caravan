package com.unicorns.invisible.caravan.model

import com.unicorns.invisible.caravan.R
import kotlinx.serialization.Serializable


@Serializable
enum class CardBack(
    val nameIdWithBackFileName: List<Pair<Int?, String>>
) {
    STANDARD(listOf(
        R.string.standard_deck_name_1 to "FNV_Caravan_card_back_-_standard.webp",
        R.string.standard_deck_name_2 to "FNV_Caravan_card_back_-_standard.webp",
        R.string.standard_deck_name_3 to "FNV_Caravan_card_back_-_standard.webp",
        R.string.standard_deck_name_4 to "FNV_Caravan_card_back_-_standard.webp",
        R.string.standard_deck_name_5 to "FNV_Caravan_card_back_-_standard.webp",
    )),
    TOPS(listOf(
        R.string.tops_deck_name to "FNV_Caravan_card_back_-_Tops.webp",
        R.string.tops_deck_alt_name to "tops_alt.webp"
    )),
    ULTRA_LUXE(listOf(
        R.string.ultra_luxe_deck_name to "FNV_Caravan_card_back_-_Ultra-Luxe.webp",
        R.string.ultra_luxe_deck_alt_name to "ultra_luxe_alt.webp"
    )),
    GOMORRAH(listOf(
        R.string.gomorrah_deck_name to "FNV_Caravan_card_back_-_Gomorrah.webp",
        R.string.gomorrah_deck_alt_name to "gomorrah_alt.webp"
    )),
    LUCKY_38(listOf(
        R.string.lucky_38_deck_name to "FNV_Caravan_card_back_-_Lucky_38.webp",
        R.string.lucky_38_deck_alt_name to "lucky_38_alt.webp"
    )),
    VAULT_21(listOf(
        R.string.vault_21_deck_name to "standard_alt.webp",
        R.string.vault_21_deck_alt_name to "sm_alt.webp"
    )),
    SIERRA_MADRE(listOf(
        R.string.sierra_madre_deck_name to "FNV_Caravan_card_back_-_Sierra_Madre.webp",
        R.string.sierra_madre_deck_alt_name to "sm_clean.webp"
    )),
    FNV_FACTION(listOf(
        R.string.fnv_deck_west_name to "ncr_back.webp",
        R.string.fnv_deck_east_name to "legion_back.webp"
    )),

    MADNESS(listOf(R.string.madness_deck_name to "madness_back.webp")),
    CHINESE(listOf(R.string.chinese_deck_name to "ccp_back.webp")),
    ENCLAVE(listOf(R.string.enclave_deck_name to "enclave_back.webp")),
    VIKING(listOf(R.string.viking_deck_name to "viking_back.webp"));

    fun getRarityMult(backNumber: Int): Double {
        return when (this) {
            STANDARD -> backNumber + 1.0
            TOPS -> backNumber + 1.0
            ULTRA_LUXE -> backNumber + 1.0
            GOMORRAH -> backNumber + 1.0
            LUCKY_38 -> backNumber + 1.0
            VAULT_21 -> backNumber + 1.0
            SIERRA_MADRE -> backNumber + 1.0
            FNV_FACTION -> 1.5
            CHINESE -> 2.5
            ENCLAVE -> 2.5
            VIKING -> 2.0
            MADNESS -> 2.0
        }
    }
}