package com.unicorns.invisible.caravan.model

import com.unicorns.invisible.caravan.R
import kotlinx.serialization.Serializable


@Serializable
enum class CardBack(val deckName: Int?, val backFileName: String, val altBackFileName: String?) {
    STANDARD(R.string.standard_deck_name, "FNV_Caravan_card_back_-_standard.webp", "FNV_Caravan_card_back_-_st_alt.webp"),
    TOPS(R.string.tops_deck_name, "FNV_Caravan_card_back_-_Tops.webp", "tops_alt.webp"),
    ULTRA_LUXE(R.string.ultra_luxe_deck_name, "FNV_Caravan_card_back_-_Ultra-Luxe.webp", "ultra_luxe_alt.webp"),
    GOMORRAH(R.string.gomorrah_deck_name, "FNV_Caravan_card_back_-_Gomorrah.webp", "gomorrah_alt.webp"),
    LUCKY_38(R.string.lucky_38_deck_name, "FNV_Caravan_card_back_-_Lucky_38.webp", "lucky_38_alt.webp"),
    VAULT_21(R.string.vault_21_deck_name, "standard_alt.webp", "sm_alt.webp"),
    SIERRA_MADRE(R.string.sierra_madre_deck_name, "FNV_Caravan_card_back_-_Sierra_Madre.webp", "sm_clean.webp"),

    // UNPLAYABLE!!
    WILD_WASTELAND(null, "ww_back.webp", null),
    NUCLEAR(null, "nuclear_back.webp", "ccp_alt_back.webp"),

    // PLAYABLE, BUT NO ALT!
    MADNESS(R.string.madness_deck_name, "madness_back.webp", null),
    CHINESE(R.string.chinese_deck_name, "ccp_back.webp", null),
    ENCLAVE(R.string.enclave_deck_name, "enclave_back.webp", null),
    VIKING(R.string.viking_deck_name, "viking_back.webp", null),
    NCR(R.string.ncr_deck_name, "TODO", null), // TODO
    LEGION(R.string.legion_deck_name, "TODO", null); // TODO

    fun hasAlt() = altBackFileName != null
}