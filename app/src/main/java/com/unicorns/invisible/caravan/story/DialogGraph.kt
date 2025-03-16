package com.unicorns.invisible.caravan.story

enum class PicEffect {
    SLIDE,
    SELECT,
    NONE
}

sealed interface DialogState
class DialogFinishState(val code: DeathCode) : DialogState
class DialogMiddleState(
    val picId: Int, val responseId: Int,
    val intro: PicEffect = PicEffect.NONE, val outro: PicEffect = PicEffect.SELECT
) : DialogState

data class DialogEdge(
    val oldState: Int, val newState: Int,
    val lineId: Int,
    val onVisited: suspend () -> Int = { -1 } // returns edge to visit
)

class DialogGraph(
    val states: List<DialogState>,
    val edges: List<DialogEdge>,
) {
    val visitedStates = Array<Boolean>(states.size) { false }
}