package com.unicorns.invisible.caravan.story

sealed interface DialogState
class DialogFinishState(val code: DeathCode) : DialogState
class DialogMiddleState(val picId: Int, val responseId: Int) : DialogState

data class DialogEdge(
    val oldState: Int, val newState: Int,
    val lineId: Int,
    val onVisited: suspend () -> Int = { -1 } // returns edge to visit
)

class DialogGraph(
    val states: List<DialogState>,
    val edges: List<DialogEdge>,
) {
    val visitedEdges = Array<Boolean>(edges.size) { false }
}