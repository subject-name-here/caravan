package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
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

enum class Style {
    DESERT,
    PIP_BOY,
    ALASKA_FRONTIER,
    PIP_GIRL,
    OLD_WORLD,
    NEW_WORLD,
    SIERRA_MADRE,
    MADRE_ROJA,
    VAULT_21,
    VAULT_22;

    fun getMenuItem(): Int? {
        return when (this) {
            DESERT -> null
            PIP_BOY -> null
            ALASKA_FRONTIER -> null
            PIP_GIRL -> null
            OLD_WORLD -> null
            NEW_WORLD -> null
            SIERRA_MADRE -> null
            MADRE_ROJA -> null
            VAULT_21 -> null
            VAULT_22 -> null
        }
    }
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