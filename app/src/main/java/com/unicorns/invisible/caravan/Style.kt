package com.unicorns.invisible.caravan

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.unicorns.invisible.caravan.Style.ALASKA_FRONTIER
import com.unicorns.invisible.caravan.Style.DESERT
import com.unicorns.invisible.caravan.Style.MADRE_ROJA
import com.unicorns.invisible.caravan.Style.NEW_WORLD
import com.unicorns.invisible.caravan.Style.OLD_WORLD
import com.unicorns.invisible.caravan.Style.PIP_BOY
import com.unicorns.invisible.caravan.Style.PIP_GIRL
import com.unicorns.invisible.caravan.Style.SIERRA_MADRE
import com.unicorns.invisible.caravan.Style.VAULT_21
import com.unicorns.invisible.caravan.Style.VAULT_22


enum class Style(val styleName: String, val price: Int) {
    DESERT("Desert", 0),
    ALASKA_FRONTIER("Frontier of Anchorage", 500),
    PIP_BOY("Pip-boy", 0),
    PIP_GIRL("Pip-Girl", 2500),
    OLD_WORLD("Old World", 2000),
    NEW_WORLD("New World", 3000),
    SIERRA_MADRE("Sierra Madre", 2000),
    MADRE_ROJA("Madre Roja", 3000),
    VAULT_21("Vault 21", 2100),
    VAULT_22("Vault 22", 2200);
}


@Composable
fun Modifier.getTableBackground(style: Style): Modifier {
    return paint(
        painterResource(id = when (style) {
            DESERT -> R.drawable.table_wood
            ALASKA_FRONTIER -> R.drawable.table_black
            PIP_BOY -> R.drawable.table_blue
            PIP_GIRL -> R.drawable.table_black // Future pink-ish
            OLD_WORLD -> R.drawable.table_amber
            NEW_WORLD -> R.drawable.table_wood
            SIERRA_MADRE -> R.drawable.table_brown
            MADRE_ROJA -> R.drawable.table_brown
            VAULT_21 -> R.drawable.table_blue
            VAULT_22 -> R.drawable.table_green
        }),
        contentScale = ContentScale.Crop, // TODO!
        colorFilter = if (style != PIP_GIRL) {
            ColorFilter.colorMatrix(ColorMatrix())
        } else {
            ColorFilter.colorMatrix(ColorMatrix().apply {
                timesAssign(
                    ColorMatrix(
                        floatArrayOf(
                            2f, 0f, 0f, 0f, 0f,
                            0f, 1.75f, 0f, 0f, 0f,
                            0f, 0f, 1.75f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
            })
        }
    )
}