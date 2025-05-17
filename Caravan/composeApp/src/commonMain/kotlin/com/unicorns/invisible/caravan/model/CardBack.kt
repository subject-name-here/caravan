package com.unicorns.invisible.caravan.model

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.chinese_deck_name
import caravan.composeapp.generated.resources.enclave_deck_name
import caravan.composeapp.generated.resources.fnv_deck_east_name
import caravan.composeapp.generated.resources.fnv_deck_west_name
import caravan.composeapp.generated.resources.gomorrah_deck_alt_name
import caravan.composeapp.generated.resources.gomorrah_deck_name
import caravan.composeapp.generated.resources.lucky_38_deck_alt_name
import caravan.composeapp.generated.resources.lucky_38_deck_name
import caravan.composeapp.generated.resources.madness_deck_name
import caravan.composeapp.generated.resources.sierra_madre_deck_alt_name
import caravan.composeapp.generated.resources.sierra_madre_deck_name
import caravan.composeapp.generated.resources.standard_deck_name_1
import caravan.composeapp.generated.resources.standard_deck_name_2
import caravan.composeapp.generated.resources.standard_deck_name_3
import caravan.composeapp.generated.resources.standard_deck_name_4
import caravan.composeapp.generated.resources.standard_deck_name_5
import caravan.composeapp.generated.resources.tops_deck_alt_name
import caravan.composeapp.generated.resources.tops_deck_name
import caravan.composeapp.generated.resources.ultra_luxe_deck_alt_name
import caravan.composeapp.generated.resources.ultra_luxe_deck_name
import caravan.composeapp.generated.resources.vault_21_deck_alt_name
import caravan.composeapp.generated.resources.vault_21_deck_name
import caravan.composeapp.generated.resources.viking_deck_name
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource


@Serializable
enum class CardBack(
    val nameIdWithBackFileName: Pair<StringResource, String>,
    val currency: Currency = Currency.CAPS
) {
    STANDARD(
        Res.string.standard_deck_name_1 to "FNV_Caravan_card_back_-_standard.webp",
        Currency.NOT_FOR_SALE
    ),
    STANDARD_UNCOMMON(
        Res.string.standard_deck_name_2 to "FNV_Caravan_card_back_-_standard.webp",
        Currency.SIERRA_MADRE_CHIPS
    ),
    STANDARD_RARE(
        Res.string.standard_deck_name_3 to "FNV_Caravan_card_back_-_standard.webp",
        Currency.SIERRA_MADRE_CHIPS
    ),
    STANDARD_MYTHIC(
        Res.string.standard_deck_name_4 to "FNV_Caravan_card_back_-_standard.webp",
        Currency.SIERRA_MADRE_CHIPS
    ),
    STANDARD_LEGENDARY(
        Res.string.standard_deck_name_5 to "FNV_Caravan_card_back_-_standard.webp",
        Currency.SIERRA_MADRE_CHIPS
    ),
    TOPS(Res.string.tops_deck_name to "FNV_Caravan_card_back_-_Tops.webp"),
    TOPS_RED(Res.string.tops_deck_alt_name to "tops_alt.webp"),
    ULTRA_LUXE(Res.string.ultra_luxe_deck_name to "FNV_Caravan_card_back_-_Ultra-Luxe.webp"),
    ULTRA_LUXE_CRIME(Res.string.ultra_luxe_deck_alt_name to "ultra_luxe_alt.webp"),
    GOMORRAH(Res.string.gomorrah_deck_name to "FNV_Caravan_card_back_-_Gomorrah.webp"),
    GOMORRAH_DARK(Res.string.gomorrah_deck_alt_name to "gomorrah_alt.webp"),
    LUCKY_38(Res.string.lucky_38_deck_name to "FNV_Caravan_card_back_-_Lucky_38.webp"),
    LUCKY_38_SPECIAL(Res.string.lucky_38_deck_alt_name to "lucky_38_alt.webp"),
    VAULT_21_DAY(Res.string.vault_21_deck_name to "standard_alt.webp"),
    VAULT_21_NIGHT(Res.string.vault_21_deck_alt_name to "sm_alt.webp"),
    SIERRA_MADRE_DIRTY(
        Res.string.sierra_madre_deck_name to "FNV_Caravan_card_back_-_Sierra_Madre.webp",
        Currency.SIERRA_MADRE_CHIPS
    ),
    SIERRA_MADRE_CLEAN(
        Res.string.sierra_madre_deck_alt_name to "sm_clean.webp",
        Currency.SIERRA_MADRE_CHIPS
    ),

    CHINESE(Res.string.chinese_deck_name to "ccp_back.webp", Currency.SIERRA_MADRE_CHIPS),
    ENCLAVE(Res.string.enclave_deck_name to "enclave_back.webp", Currency.SIERRA_MADRE_CHIPS),

    NCR(Res.string.fnv_deck_west_name to "ncr_back.webp", Currency.NOT_FOR_SALE),
    LEGION(Res.string.fnv_deck_east_name to "legion_back.webp", Currency.NOT_FOR_SALE),
    MADNESS(Res.string.madness_deck_name to "madness_back.webp", Currency.NOT_FOR_SALE),
    VIKING(Res.string.viking_deck_name to "viking_back.webp", Currency.NOT_FOR_SALE);

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
        const val BASE_CARD_COST = 15.0
    }
}