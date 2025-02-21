package com.unicorns.invisible.caravan.story

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.Style
import com.unicorns.invisible.caravan.utils.TextClassic
import com.unicorns.invisible.caravan.utils.clickableSelect
import com.unicorns.invisible.caravan.utils.getStrokeColorByStyle
import com.unicorns.invisible.caravan.utils.getTextBackByStyle
import com.unicorns.invisible.caravan.utils.getTextColorByStyle
import com.unicorns.invisible.caravan.utils.scrollbar


@Composable
fun DialogLine(activity: MainActivity, line: String, isSelect: Boolean = true, onClick: () -> Unit) {
    val modifier = if (isSelect) {
        Modifier.clickableSelect(activity) { onClick() }
    } else {
        Modifier.clickable { onClick() }
    }
    TextClassic(
        line,
        getTextColorByStyle(activity, Style.PIP_BOY),
        getStrokeColorByStyle(activity, Style.PIP_BOY),
        18.sp,
        modifier
            .background(getTextBackByStyle(activity, Style.PIP_BOY))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        textAlignment = TextAlign.Start
    )
    Spacer(Modifier.height(12.dp))
}

@Composable
fun StoryShow(activity: MainActivity, graph: DialogGraph, onEnd: () -> Unit) {
    // TODO: leave the story?
    var update by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        graph.onEdgeVisitedMap[0]?.invoke(graph)
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)) {
        val state = rememberLazyListState()
        LazyColumn(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
                .scrollbar(
                    state,
                    knobColor = getTextColorByStyle(activity, Style.PIP_BOY),
                    trackColor = getStrokeColorByStyle(activity, Style.PIP_BOY),
                    horizontal = false,
                ),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            state = state
        ) { item {
            key(update) {

            }
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .paint(
                        painterResource(
                            id = graph.getCurrentState().picId
                        )
                    ))

            Column(Modifier.fillMaxWidth()) {
                TextClassic(
                    stringResource(graph.getEntranceEdge().responseId),
                    getTextColorByStyle(activity, Style.PIP_BOY),
                    getStrokeColorByStyle(activity, Style.PIP_BOY),
                    16.sp,
                    Modifier
                        .background(getTextBackByStyle(activity, Style.PIP_BOY))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    textAlignment = TextAlign.Start
                )

                Spacer(Modifier.height(16.dp))

                graph.getCurrentState().possibleEdges.forEach { edgeIndex ->
                    if (!graph.visitedEdges[edgeIndex]) {
                        val edge = graph.edges[edgeIndex]
                        DialogLine(activity, stringResource(edge.lineId)) {
                            graph.currentState = edge.newState
                            if (graph.currentState < 0) {
                                onEnd()
                            } else {
                                graph.entranceEdge = edgeIndex
                                graph.visitedEdges[graph.entranceEdge] = true
                                graph.onEdgeVisitedMap[graph.entranceEdge]?.invoke(graph)
                            }
                            update = !update
                        }
                    }
                }
            }
        } }
    }
}