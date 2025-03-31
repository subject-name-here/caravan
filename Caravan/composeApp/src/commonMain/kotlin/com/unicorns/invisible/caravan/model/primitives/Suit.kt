package com.unicorns.invisible.caravan.model.primitives

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.clubs_name
import caravan.composeapp.generated.resources.diamonds_name
import caravan.composeapp.generated.resources.hearts_name
import caravan.composeapp.generated.resources.spades_name
import org.jetbrains.compose.resources.StringResource


enum class Suit(val nameId: StringResource) {
    HEARTS(Res.string.hearts_name),
    CLUBS(Res.string.clubs_name),
    DIAMONDS(Res.string.diamonds_name),
    SPADES(Res.string.spades_name),
}