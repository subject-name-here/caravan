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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.back_to_menu
import caravan.composeapp.generated.resources.crt_1
import caravan.composeapp.generated.resources.death_message_any_1
import caravan.composeapp.generated.resources.death_message_any_2
import caravan.composeapp.generated.resources.death_message_any_3
import caravan.composeapp.generated.resources.death_message_any_4
import caravan.composeapp.generated.resources.death_message_any_5
import caravan.composeapp.generated.resources.death_message_ch_13_1
import caravan.composeapp.generated.resources.death_message_ch_13_2
import caravan.composeapp.generated.resources.death_message_fight_death
import caravan.composeapp.generated.resources.death_message_fight_death_world_dies
import caravan.composeapp.generated.resources.death_message_joke_1
import caravan.composeapp.generated.resources.death_message_joke_2
import caravan.composeapp.generated.resources.death_message_on_fail_check
import caravan.composeapp.generated.resources.death_message_painful
import caravan.composeapp.generated.resources.death_message_starve
import caravan.composeapp.generated.resources.death_message_wasteland_1
import caravan.composeapp.generated.resources.death_message_wasteland_2
import caravan.composeapp.generated.resources.death_message_wasteland_3
import caravan.composeapp.generated.resources.death_screen
import caravan.composeapp.generated.resources.finish
import com.sebaslogen.resaca.rememberScoped
import com.unicorns.invisible.caravan.Style
import com.unicorns.invisible.caravan.utils.TextClassic
import com.unicorns.invisible.caravan.utils.clickableOk
import com.unicorns.invisible.caravan.utils.getStrokeColorByStyle
import com.unicorns.invisible.caravan.utils.getTextBackByStyle
import com.unicorns.invisible.caravan.utils.getTextColorByStyle
import com.unicorns.invisible.caravan.utils.playDeathPhrase
import com.unicorns.invisible.caravan.utils.playSelectSound
import com.unicorns.invisible.caravan.utils.playSlideSound
import com.unicorns.invisible.caravan.utils.pxToDp
import com.unicorns.invisible.caravan.utils.weightedRandom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


@Composable
fun DialogLine(line: String, onClick: () -> Unit) {
    if (line == "") return
    TextClassic(
        line,
        getTextColorByStyle(Style.PIP_BOY),
        getStrokeColorByStyle(Style.PIP_BOY),
        18.sp,
        Modifier
            .clickable { onClick() }
            .background(getTextBackByStyle(Style.PIP_BOY))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        textAlignment = TextAlign.Start
    )
    Spacer(Modifier.height(12.dp))
}

@Composable
fun StoryShow(graph: DialogGraph, onBadEnd: () -> Unit, onGoodEnd: () -> Unit) {
    var dialogState by rememberScoped { mutableIntStateOf(0) }
    when (val stableDialogState: DialogState = graph.states[dialogState]) {
        is DialogFinishState -> {
            ShowDeathScreen(stableDialogState.code, onBadEnd, onGoodEnd)
        }
        is DialogMiddleState -> {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black)) {
                val lazyListState = rememberLazyListState()
                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    state = lazyListState
                ) { item {
                    var isSlideVisible by rememberScoped { mutableStateOf(false) }
                    LaunchedEffect(dialogState) {
                        if (stableDialogState.intro == PicEffect.SLIDE) {
                            playSlideSound()
                        }
                        isSlideVisible = true
                    }

                    Box(Modifier.width(640.pxToDp()).height(480.pxToDp()).clipToBounds(), contentAlignment = Alignment.TopCenter) {
                        AnimatedVisibility(
                            visible = stableDialogState.intro == PicEffect.SLIDE && isSlideVisible,
                            enter = slideInHorizontally(
                                animationSpec = spring(stiffness = Spring.StiffnessMediumLow, visibilityThreshold = IntOffset.VisibilityThreshold),
                                initialOffsetX = { -it }
                            ),
                            exit = slideOutHorizontally(targetOffsetX = { it })
                        ) {
                            Box(Modifier.fillMaxSize().paint(
                                painterResource(stableDialogState.picId),
                                contentScale = ContentScale.FillBounds,
                            ))
                        }
                        Box(Modifier.fillMaxSize()
                            .paint(painterResource(Res.drawable.crt_1))
                        )
                    }
                    Column(Modifier.fillMaxWidth()) {
                        val response = stringResource(stableDialogState.responseId)
                        if (response.isNotEmpty()) {
                            TextClassic(
                                response,
                                getTextColorByStyle(Style.PIP_BOY),
                                getStrokeColorByStyle(Style.PIP_BOY),
                                16.sp,
                                Modifier
                                    .fillMaxWidth()
                                    .background(getTextBackByStyle(Style.PIP_BOY))
                                    .padding(horizontal = 8.dp, vertical = 2.dp),
                                textAlignment = TextAlign.Start,
                                boxAlignment = Alignment.CenterStart
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        graph.edges.forEachIndexed { edgeIndex, e ->
                            if (e.oldState == dialogState) {
                                val edge = graph.edges[edgeIndex]
                                if (graph.visitedStates[edge.newState]) {
                                    return@forEachIndexed
                                }
                                DialogLine(stringResource(edge.lineId)) {
                                    if (isSlideVisible) {
                                        isSlideVisible = false
                                        CoroutineScope(Dispatchers.Unconfined).launch {
                                            when (stableDialogState.outro) {
                                                PicEffect.SLIDE -> {
                                                    playSlideSound()
                                                    delay(1000L)
                                                }
                                                PicEffect.SELECT -> {
                                                    playSelectSound()
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
                    }
                } }
            }
        }
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        TextClassic(
            stringResource(Res.string.back_to_menu),
            getTextColorByStyle(Style.PIP_BOY),
            getStrokeColorByStyle(Style.PIP_BOY),
            16.sp,
            Modifier
                .fillMaxWidth()
                .background(getTextBackByStyle(Style.PIP_BOY))
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .clickableOk { onBadEnd() },
            textAlignment = TextAlign.Start
        )
    }
}

@Composable
fun ShowDeathScreen(code: DeathCode, onBadEnd: () -> Unit, onGoodEnd: () -> Unit) {
    if (code == DeathCode.ALIVE) {
        onGoodEnd()
        return
    }

    val messages = listOf(
        "death_message_any_1.ogg" to Res.string.death_message_any_1 to 50,
        "death_message_any_2.ogg" to Res.string.death_message_any_2 to 100,
        "death_message_any_3.ogg" to Res.string.death_message_any_3 to 100,
        "death_message_any_4.ogg" to Res.string.death_message_any_4 to 100,
        "death_message_any_5.ogg" to Res.string.death_message_any_5 to 100,
        "death_message_ch13_1.ogg" to Res.string.death_message_ch_13_1 to 500,
        "death_message_ch13_2.ogg" to Res.string.death_message_ch_13_2 to 500,
        "death_message_fight_death.ogg" to Res.string.death_message_fight_death to 300,
        "death_message_fight_death_world_dies.ogg" to Res.string.death_message_fight_death_world_dies to 1000,
        "death_message_joke_1.ogg" to Res.string.death_message_joke_1 to 5,
        "death_message_joke_2.ogg" to Res.string.death_message_joke_2 to 5,
        "death_message_on_fail_check.ogg" to Res.string.death_message_on_fail_check to 100,
        "death_message_painful.ogg" to Res.string.death_message_painful to 200,
        "death_message_starve.ogg" to Res.string.death_message_starve to 300,
        "death_message_wasteland_1.ogg" to Res.string.death_message_wasteland_1 to 200,
        "death_message_wasteland_2.ogg" to Res.string.death_message_wasteland_2 to 200,
        "death_message_wasteland_3.ogg" to Res.string.death_message_wasteland_3 to 200,
    )
    val map = mapOf(
        DeathCode.AGAINST_DEATH to listOf(0, 1, 2, 3, 4, 7, 9, 10, 12, 14, 15, 16),
        DeathCode.EXPLODED to listOf(0, 1, 2, 3, 4, 9, 10, 11, 12, 14, 16),
        DeathCode.STABBED_BY_CAZADORS to listOf(0, 1, 2, 3, 4, 7, 9, 10, 12, 14, 15, 16),
        DeathCode.STABBED_BY_CAZADORS_ON_THE_RUN to listOf(0, 1, 2, 3, 4, 7, 9, 10, 11, 12, 14, 15, 16),
        DeathCode.GOT_LOST to listOf(1, 10, 11, 13),
        DeathCode.SHOT to listOf(0, 1, 2, 3, 4, 9, 10, 12, 14, 15, 16),
    )
    val messagesInfos = map[code]?.map { messages[it] } ?: run { onBadEnd(); return }
    val index = weightedRandom(messagesInfos.map { it.second })
    val info = messagesInfos[index]

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)) {
        val lazyListState = rememberLazyListState()
        LazyColumn(
            Modifier
                .fillMaxSize()
                .background(Color.Black),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            state = lazyListState
        ) { item {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .paint(
                        painterResource(Res.drawable.death_screen),
                        contentScale = ContentScale.Fit
                    ))

            LaunchedEffect(Unit) {
                playDeathPhrase(info.first.first)
            }

            Column(Modifier.fillMaxWidth()) {
                TextClassic(
                    stringResource(info.first.second),
                    getTextColorByStyle(Style.PIP_BOY),
                    getStrokeColorByStyle(Style.PIP_BOY),
                    16.sp,
                    Modifier
                        .background(getTextBackByStyle(Style.PIP_BOY))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    textAlignment = TextAlign.Start
                )

                Spacer(Modifier.height(16.dp))

                DialogLine(stringResource(Res.string.finish)) { onBadEnd() }
            }
        } }
    }
}

enum class DeathCode(val code: Int) {
    ALIVE(0),
    EXPLODED(-1),
    STABBED_BY_CAZADORS(-2),
    STABBED_BY_CAZADORS_ON_THE_RUN(-3),
    GOT_LOST(-4),
    SHOT(-5),
    AGAINST_DEATH(-13),
}
