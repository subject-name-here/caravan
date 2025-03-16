package com.unicorns.invisible.caravan.story

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sebaslogen.resaca.rememberScoped
import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.Style
import com.unicorns.invisible.caravan.utils.TextClassic
import com.unicorns.invisible.caravan.utils.getStrokeColorByStyle
import com.unicorns.invisible.caravan.utils.getTextBackByStyle
import com.unicorns.invisible.caravan.utils.getTextColorByStyle
import com.unicorns.invisible.caravan.utils.playDeathPhrase
import com.unicorns.invisible.caravan.utils.playSelectSound
import com.unicorns.invisible.caravan.utils.playSlideSound
import com.unicorns.invisible.caravan.utils.pxToDp
import com.unicorns.invisible.caravan.utils.scrollbar
import com.unicorns.invisible.caravan.utils.weightedRandom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun DialogLine(activity: MainActivity, line: String, onClick: () -> Unit) {
    if (line == "") return
    TextClassic(
        line,
        getTextColorByStyle(activity, Style.PIP_BOY),
        getStrokeColorByStyle(activity, Style.PIP_BOY),
        18.sp,
        Modifier
            .clickable { onClick() }
            .background(getTextBackByStyle(activity, Style.PIP_BOY))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        textAlignment = TextAlign.Start
    )
    Spacer(Modifier.height(12.dp))
}

@Composable
fun StoryShow(activity: MainActivity, graph: DialogGraph, onEnd: () -> Unit) {
    var dialogState by rememberScoped { mutableIntStateOf(0) }
    val stableDialogState: DialogState = graph.states[dialogState]

    when (stableDialogState) {
        is DialogFinishState -> {
            ShowDeathScreen(activity, stableDialogState.code, onEnd)
        }
        is DialogMiddleState -> {
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
                    var isSlideVisible by rememberScoped { mutableStateOf(false) }
                    LaunchedEffect(dialogState) {
                        if (stableDialogState.intro == PicEffect.SLIDE) {
                            playSlideSound(activity)
                        }
                        isSlideVisible = true
                    }

                    Box(Modifier.width(640.pxToDp()).height(480.pxToDp()).clipToBounds(), contentAlignment = Alignment.TopCenter) {
                        AnimatedVisibility(
                            visible = stableDialogState.intro == PicEffect.SLIDE && isSlideVisible,
                            enter = slideInHorizontally(
                                animationSpec = spring(stiffness = Spring.StiffnessMediumLow, visibilityThreshold = IntOffset.VisibilityThreshold),
                                initialOffsetX = { it -> -it }
                            ),
                            exit = slideOutHorizontally(targetOffsetX = { it -> it })
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(activity)
                                    .data(stableDialogState.picId)
                                    .build(),
                                contentScale = ContentScale.FillBounds,
                                contentDescription = "",
                            )
                        }
                        Box(Modifier.fillMaxSize()
                            .paint(painterResource(id = R.drawable.crt_1))
                        )
                    }
                    Column(Modifier.fillMaxWidth()) {
                        val response = stringResource(stableDialogState.responseId)
                        if (response.isNotEmpty()) {
                            TextClassic(
                                response,
                                getTextColorByStyle(activity, Style.PIP_BOY),
                                getStrokeColorByStyle(activity, Style.PIP_BOY),
                                16.sp,
                                Modifier
                                    .background(getTextBackByStyle(activity, Style.PIP_BOY))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                textAlignment = TextAlign.Start
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        graph.edges.forEachIndexed { edgeIndex, edge ->
                            if (edge.oldState == dialogState) {
                                val edge = graph.edges[edgeIndex]
                                if (graph.visitedStates[edge.newState]) {
                                    return@forEachIndexed
                                }
                                DialogLine(activity, stringResource(edge.lineId)) {
                                    CoroutineScope(Dispatchers.Unconfined).launch {
                                        isSlideVisible = false
                                        when (stableDialogState.outro) {
                                            PicEffect.SLIDE -> {
                                                playSlideSound(activity)
                                                delay(1000L)
                                            }
                                            PicEffect.SELECT -> {
                                                playSelectSound(activity)
                                            }
                                            PicEffect.NONE -> {}
                                        }
                                        dialogState = edge.newState
                                        graph.visitedStates[dialogState] = true
                                        var visitedEdgeResult = edge.onVisited()
                                        while (visitedEdgeResult in graph.edges.indices) {
                                            val otherEdge = graph.edges[visitedEdgeResult]
                                            dialogState = otherEdge.newState
                                            graph.visitedStates[dialogState] = true
                                            visitedEdgeResult = otherEdge.onVisited()
                                        }
                                    }
                                }
                            }
                        }
                    }
                } }
            }
        }
    }
}

@Composable
fun ShowDeathScreen(activity: MainActivity, code: DeathCode, leave: () -> Unit) {
    if (code == DeathCode.ALIVE) {
        leave()
        return
    }

    val messages = listOf(
        "death_message_any_1.ogg" to R.string.death_message_any_1 to 50,
        "death_message_any_2.ogg" to R.string.death_message_any_2 to 100,
        "death_message_any_3.ogg" to R.string.death_message_any_3 to 100,
        "death_message_any_4.ogg" to R.string.death_message_any_4 to 100,
        "death_message_any_5.ogg" to R.string.death_message_any_5 to 100,
        "death_message_ch13_1.ogg" to R.string.death_message_ch_13_1 to 500,
        "death_message_ch13_2.ogg" to R.string.death_message_ch_13_2 to 500,
        "death_message_fight_death.ogg" to R.string.death_message_fight_death to 300,
        "death_message_fight_death_world_dies.ogg" to R.string.death_message_fight_death_world_dies to 1000,
        "death_message_joke_1.ogg" to R.string.death_message_joke_1 to 5,
        "death_message_joke_2.ogg" to R.string.death_message_joke_2 to 5,
        "death_message_on_fail_check.ogg" to R.string.death_message_on_fail_check to 100,
        "death_message_painful.ogg" to R.string.death_message_painful to 200,
        "death_message_starve.ogg" to R.string.death_message_starve to 300,
        "death_message_wasteland_1.ogg" to R.string.death_message_wasteland_1 to 200,
        "death_message_wasteland_2.ogg" to R.string.death_message_wasteland_2 to 200,
        "death_message_wasteland_3.ogg" to R.string.death_message_wasteland_3 to 200,
    )
    val map = mapOf(
        DeathCode.AGAINST_DEATH to listOf(0, 1, 2, 3, 4, 7, 9, 10, 12, 14, 15, 16),
    )
    val messagesInfos = map[code]?.mapNotNull { messages[it] } ?: run { leave(); return }
    val index = weightedRandom(messagesInfos.map { it.second })
    val info = messagesInfos[index]

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
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .paint(
                        painterResource(
                            id = R.drawable.death_screen
                        )
                    ))

            LaunchedEffect(Unit) {
                playDeathPhrase(activity, info.first.first)
            }

            Column(Modifier.fillMaxWidth()) {
                TextClassic(
                    stringResource(info.first.second),
                    getTextColorByStyle(activity, Style.PIP_BOY),
                    getStrokeColorByStyle(activity, Style.PIP_BOY),
                    16.sp,
                    Modifier
                        .background(getTextBackByStyle(activity, Style.PIP_BOY))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    textAlignment = TextAlign.Start
                )

                Spacer(Modifier.height(16.dp))

                DialogLine(activity, stringResource(R.string.finish)) { leave() }
            }
        } }
    }
}

enum class DeathCode(val code: Int) {
    ALIVE(0),
    AGAINST_DEATH(-13),
}
