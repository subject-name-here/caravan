package com.unicorns.invisible.caravan.model

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import com.unicorns.invisible.caravan.R


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

    fun isPlayable() = deckName != null
    fun hasAlt() = altBackFileName != null

    fun getFilter(isAlt: Boolean): ColorFilter {
        if (!isAlt) {
            return ColorFilter.colorMatrix(ColorMatrix())
        }
        return when (this) {
            STANDARD -> ColorFilter.colorMatrix(ColorMatrix(
                floatArrayOf(
                    0f, 0f, 1f, 0f, 0f,
                    0f, 1f, 0f, 0f, 0f,
                    1f, 0f, 0f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )
            ))

            TOPS -> ColorFilter.colorMatrix(ColorMatrix().apply {
                timesAssign(
                    ColorMatrix(
                        floatArrayOf(
                            0.9f, 2f, 0f, 0f, 0f,
                            0.3f, 2f, 0f, 0f, 0f,
                            0.15f, 2f, 0.1f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
                timesAssign(
                    ColorMatrix(
                        floatArrayOf(
                            1f, 0f, 0f, 0f, 0f,
                            0f, 1f, -1f, 0f, 0f,
                            0f, 0f, 1f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
                timesAssign(
                    ColorMatrix(
                        floatArrayOf(
                            1f, 0f, 0f, 0f, 0f,
                            0f, -1f, 0f, 0f, 255f,
                            0f, 0f, -1f, 0f, 255f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
                timesAssign(
                    ColorMatrix(
                        floatArrayOf(
                            0f, 0f, 1f, 0f, 0f,
                            0f, 1f, 0f, 0f, 0f,
                            1f, 0f, 0f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
            })

            ULTRA_LUXE -> ColorFilter.colorMatrix(ColorMatrix().apply {
                timesAssign(
                    ColorMatrix(
                        floatArrayOf(
                            1f, 0f, 0f, 0f, 0f,
                            0f, 1f, 0f, 0f, 0f,
                            0f, 0f, 16f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
                timesAssign(
                    ColorMatrix(
                        floatArrayOf(
                            -1f, 0f, 0f, 0f, 255f,
                            0f, -1f, 0f, 0f, 255f,
                            0f, 0f, -1f, 0f, 255f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
                timesAssign(
                    ColorMatrix(
                        floatArrayOf(
                            0f, 0f, 2f, 0f, 0f,
                            0f, 2f, 0f, 0f, 0f,
                            2f, 0f, 0f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
            })

            GOMORRAH -> ColorFilter.colorMatrix(ColorMatrix().apply {
                timesAssign(
                    ColorMatrix(
                        floatArrayOf(
                            0f, 1f, 0f, 0f, 0f,
                            0.5f, 0.5f, 0f, 0f, 0f,
                            1f, 0f, 0f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
                timesAssign(
                    ColorMatrix(
                        floatArrayOf(
                            -1f, 0f, 0f, 0f, 255f,
                            0f, -1f, 0f, 0f, 255f,
                            0f, 0f, -1f, 0f, 255f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
                timesAssign(
                    ColorMatrix(
                        floatArrayOf(
                            2f, 0f, 0f, 0f, 0f,
                            0f, 2f, 0f, 0f, 0f,
                            0f, 0f, 2f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
            })

            else -> ColorFilter.colorMatrix(ColorMatrix())
        }
    }
}