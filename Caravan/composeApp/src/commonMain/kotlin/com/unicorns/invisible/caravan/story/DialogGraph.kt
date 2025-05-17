package com.unicorns.invisible.caravan.story

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

enum class PicEffect {
    SLIDE,
    SELECT,
    NONE
}

sealed interface DialogState
class DialogFinishState(val code: DeathCode) : DialogState
class DialogMiddleState(
    val picId: DrawableResource, val responseId: StringResource,
    val intro: PicEffect = PicEffect.NONE, val outro: PicEffect = PicEffect.SELECT
) : DialogState

data class DialogEdge(
    val oldState: Int, val newState: Int,
    val lineId: StringResource,
    val onVisited: suspend () -> Int = { -1 } // returns edge to visit; useful for Frank appearance
)

class DialogGraph(
    val states: List<DialogState>,
    val edges: List<DialogEdge>,
) {
    val visitedStates = Array(states.size) { false }
}