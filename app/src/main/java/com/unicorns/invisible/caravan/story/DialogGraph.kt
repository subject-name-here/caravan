package com.unicorns.invisible.caravan.story

data class DialogState(val picId: Int, val possibleEdges: List<Int>)

data class DialogEdge(val lineId: Int, val responseId: Int, val newState: Int)

class DialogGraph(
    val states: List<DialogState>,
    val edges: List<DialogEdge>,
    val onEdgeVisitedMap: Map<Int, DialogGraph.() -> Unit> = emptyMap()
) {
    var currentState: Int = 0
    var entranceEdge: Int = 0
    val visitedEdges = edges.map { false }.toMutableList()
    fun getCurrentState() = states[currentState]
    fun getEntranceEdge() = edges[entranceEdge]
}