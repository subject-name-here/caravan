package com.unicorns.invisible.caravan

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ColorMatrixColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.FixedScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.sebaslogen.resaca.rememberScoped
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.challenge.Challenge
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.save.Save
import com.unicorns.invisible.caravan.save.loadGDSave
import com.unicorns.invisible.caravan.save.loadLocalSave
import com.unicorns.invisible.caravan.save.processOldSave
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.SliderCustom
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableCancel
import com.unicorns.invisible.caravan.utils.clickableOk
import com.unicorns.invisible.caravan.utils.cronetEngine
import com.unicorns.invisible.caravan.utils.dpToPx
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getDialogBackground
import com.unicorns.invisible.caravan.utils.getDialogTextColor
import com.unicorns.invisible.caravan.utils.getDividerColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getMusicPanelColor
import com.unicorns.invisible.caravan.utils.getMusicTextColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.nextSong
import com.unicorns.invisible.caravan.utils.pauseActivitySound
import com.unicorns.invisible.caravan.utils.pauseRadio
import com.unicorns.invisible.caravan.utils.playNotificationSound
import com.unicorns.invisible.caravan.utils.resumeActivitySound
import com.unicorns.invisible.caravan.utils.resumeRadio
import com.unicorns.invisible.caravan.utils.scrollbar
import com.unicorns.invisible.caravan.utils.setAmbientVolume
import com.unicorns.invisible.caravan.utils.setRadioVolume
import com.unicorns.invisible.caravan.utils.startRadio
import com.unicorns.invisible.caravan.utils.stopSoundEffects
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.chromium.net.CronetEngine
import kotlin.random.Random


var save = Save(isUsable = false)
var soundReduced: Boolean = false
    set(value) {
        field = value
        soundReducedLiveData.postValue(value)
    }
private val soundReducedLiveData = MutableLiveData(soundReduced)

val isHorror = MutableLiveData(false)
val restartSwitch = MutableLiveData(false)

@Suppress("MoveLambdaOutsideParentheses")
class MainActivity : SaveDataActivity() {
    val styleId
        get() = Style.entries.getOrElse(save.styleId) { Style.PIP_BOY }

    val isSaveLoaded = MutableLiveData(save.isUsable)

    override fun onPause() {
        super.onPause()
        pauseActivitySound(save.playRadioInBack)
        stopSoundEffects()
    }

    override fun onResume() {
        super.onResume()
        resumeActivitySound()
    }

    override fun onSnapshotClientInitialized() {
        CoroutineScope(Dispatchers.IO).launch {
            if (!save.isUsable) {
                // TODO: When we change profile, we should use save from GD
                val localSave = loadLocalSave(this@MainActivity)
                if (localSave != null) {
                    save = localSave
                } else {
                    val loadedSave = loadGDSave(this@MainActivity)
                    if (loadedSave != null) {
                        save = loadedSave
                    } else {
                        save = Save(isUsable = true)
                        processOldSave(this@MainActivity)
                        saveData(this@MainActivity)
                    }
                }

                isSaveLoaded.postValue(true)

                startRadio(this@MainActivity)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (cronetEngine == null) {
            try {
                val myBuilder = CronetEngine.Builder(this)
                cronetEngine = myBuilder.build()
            } catch (_: Exception) {
                Toast.makeText(
                    this,
                    "Failed to init CronetEngine. Multiplayer is unavailable.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        val advice = listOf(
            R.string.intro_tip_2,
            R.string.intro_tip_3,
            R.string.intro_tip_4,
            R.string.intro_tip_5,
            R.string.intro_tip_6,
            R.string.intro_tip_9,
            R.string.intro_tip_10,
            R.string.intro_tip_11,
            R.string.intro_tip_12,
            R.string.intro_tip_13,
            R.string.intro_tip_14,
            R.string.intro_tip_15,
            R.string.intro_tip_vault,
            R.string.intro_tip_desert,
            R.string.intro_tip_arctic,
            R.string.intro_tip_enclave,
            R.string.intro_tip_new_world,
            R.string.intro_tip_snuffles,
            R.string.intro_tip_33,
            R.string.intro_tip_34,
            R.string.intro_tip_35,
        ).random()

        setContent {
            val (textColor, strokeColor, backgroundColor) = Triple(
                getTextColor(this), getTextStrokeColor(this), getBackgroundColor(this)
            )
            var isIntroScreen by rememberScoped { mutableStateOf(true) }

            val restartSwitchState by restartSwitch.observeAsState()
            if (restartSwitchState == true) {
                isIntroScreen = true
            }

            if (isIntroScreen) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(backgroundColor)
                        .clickableOk(this) {
                            if (save.isUsable) {
                                restartSwitch.postValue(false)
                                isIntroScreen = false
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val k by isSaveLoaded.observeAsState()
                    key(k) {
                        Column(
                            Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Column(
                                Modifier
                                    .fillMaxSize()
                                    .padding(4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(Modifier.fillMaxWidth().weight(0.25f).padding(vertical = 4.dp)) {
                                    Column(
                                        Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Bottom,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        if (save.isUsable) {
                                            TextFallout(
                                                "CARAVAN",
                                                textColor,
                                                strokeColor,
                                                40.sp,
                                                Alignment.TopCenter,
                                                Modifier.padding(top = 8.dp),
                                                TextAlign.Center
                                            )
                                            TextFallout(
                                                stringResource(R.string.tap_to_play),
                                                textColor,
                                                strokeColor,
                                                24.sp,
                                                Alignment.TopCenter,
                                                Modifier.padding(4.dp),
                                                TextAlign.Center
                                            )
                                        } else {
                                            TextFallout(
                                                "PLEASE\nSTAND BY",
                                                textColor,
                                                strokeColor,
                                                32.sp,
                                                Alignment.TopCenter,
                                                Modifier.padding(4.dp),
                                                TextAlign.Center
                                            )
                                        }
                                        Spacer(Modifier.height(8.dp))
                                        TextFallout(
                                            getString(advice),
                                            textColor,
                                            strokeColor,
                                            18.sp,
                                            Alignment.Center,
                                            Modifier.padding(vertical = 4.dp, horizontal = 12.dp),
                                            TextAlign.Center
                                        )
                                    }
                                }

                                // TODO: test landscape
                                if (isHorror.value == true) {
                                    Box(
                                        Modifier.fillMaxWidth().weight(0.70f)
                                            .paint(
                                                painterResource(R.drawable.brother),
                                                contentScale = ContentScale.Fit,
                                                colorFilter = ColorMatrixColorFilter(
                                                    ColorMatrix(
                                                        floatArrayOf(
                                                            0f, 0f, 1f, 0f, 0f,
                                                            0f, 1f, 0f, 0f, 0f,
                                                            1f, 0f, 0f, 0f, 0f,
                                                            0f, 0f, 0f, 1f, 0f
                                                        )
                                                    )
                                                )
                                            )
                                    )
                                } else {
                                    Box(
                                        Modifier.fillMaxWidth().weight(0.70f)
                                            .paint(
                                                painterResource(R.drawable.caravan_main2),
                                                contentScale = ContentScale.Fit
                                            )
                                    )
                                }


                                Box(Modifier.fillMaxWidth().weight(0.05f)) {
                                    val annotatedString = buildAnnotatedString {
                                        append("Pic creator: ")
                                        withLink(
                                            link = LinkAnnotation.Url(
                                                url = "https://steamcommunity.com/profiles/76561199409356196/",
                                                styles = TextLinkStyles(
                                                    style = SpanStyle(
                                                        color = textColor,
                                                        fontFamily = FontFamily(Font(R.font.monofont)),
                                                        textDecoration = TextDecoration.Underline
                                                    )
                                                )
                                            ),
                                        ) {
                                            append("bunkeran")
                                        }
                                    }
                                    TextFallout(
                                        annotatedString,
                                        textColor,
                                        strokeColor,
                                        12.sp,
                                        Alignment.BottomEnd,
                                        Modifier.fillMaxSize(),
                                        TextAlign.End
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Screen()

                if (isHorror.value == true) {
                    var visible by remember { mutableStateOf(false) }
                    var alignment by remember { mutableStateOf(Alignment.Center) }
                    fun updateAlignment() {
                        alignment = listOf(
                            Alignment.Center,
                            Alignment.CenterStart,
                            Alignment.CenterEnd,
                            Alignment.TopStart,
                            Alignment.TopCenter,
                            Alignment.TopEnd,
                            Alignment.BottomStart,
                            Alignment.BottomCenter,
                            Alignment.BottomEnd,
                        ).random()
                    }

                    LaunchedEffect(Unit) {
                        while (isActive) {
                            delay(Random.nextInt(2000, 4750).toLong())
                            updateAlignment()
                            visible = true
                            delay(150L)
                            visible = false
                            delay(750L)
                            updateAlignment()
                            visible = true
                            delay(150L)
                            visible = false
                        }
                    }

                    Box(Modifier.fillMaxSize().padding(top = 48.dp, end = 8.dp, bottom = 48.dp), contentAlignment = alignment) {
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(tween(100)), exit = fadeOut(tween(333))
                        ) {
                            Box(
                                Modifier
                                    .paint(
                                        painterResource(R.drawable.brother2),
                                        contentScale = FixedScale(0.69f),
                                    )
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun Screen() {
        var showPvE by rememberSaveable { mutableStateOf(false) }
        var showPvP by rememberSaveable { mutableStateOf(false) }
        var deckSelection by rememberSaveable { mutableStateOf(false) }
        var showRules by rememberSaveable { mutableStateOf(false) }
        var showDailys by rememberSaveable { mutableStateOf(false) }
        var showMarket by rememberSaveable { mutableStateOf(false) }

        var showAbout by rememberSaveable { mutableStateOf(false) }
        var showSettings by rememberSaveable { mutableStateOf(false) }
        var showVision by rememberSaveable { mutableStateOf(false) }
        var styleIdForTop by rememberSaveable { mutableStateOf(styleId) }

        var showSoundSettings by remember { mutableStateOf(false) }
        var showSoundSettings2 by remember { mutableStateOf(false) }

        var showAlertDialog by remember { mutableStateOf(false) }
        var showAlertDialog2 by remember { mutableStateOf(false) }
        var alertDialogHeader by remember { mutableStateOf("") }
        var alertDialogMessage by remember { mutableStateOf("") }
        var alertGoBack: (() -> Unit)? by rememberScoped { mutableStateOf(null) }

        fun showAlertDialog(header: String, message: String, goBack: (() -> Unit)?) {
            alertDialogHeader = header
            alertDialogMessage = message
            alertGoBack = goBack
            showAlertDialog = true
        }

        fun hideAlertDialog() {
            showAlertDialog = false
            showAlertDialog2 = false
        }

        if (showAlertDialog) {
            LaunchedEffect(Unit) {
                delay(50L)
                playNotificationSound(this@MainActivity) { showAlertDialog2 = true }
            }

            if (showAlertDialog2) {
                AlertDialog(
                    modifier = Modifier.border(width = 4.dp, color = getTextColor(this)),
                    onDismissRequest = { hideAlertDialog() },
                    confirmButton = {
                        TextFallout(
                            stringResource(R.string.close),
                            getDialogBackground(this),
                            getDialogBackground(this),
                            18.sp, Alignment.Center,
                            Modifier
                                .background(getDialogTextColor(this))
                                .clickableCancel(this) { hideAlertDialog() }
                                .padding(4.dp),
                            TextAlign.Center
                        )
                    },
                    dismissButton = {
                        if (alertGoBack != null) {
                            TextFallout(
                                stringResource(R.string.back_to_menu),
                                getDialogBackground(this),
                                getDialogBackground(this), 18.sp, Alignment.Center,
                                Modifier
                                    .background(getDialogTextColor(this))
                                    .clickableCancel(this) {
                                        hideAlertDialog()
                                        alertGoBack?.invoke()
                                    }
                                    .padding(4.dp),
                                TextAlign.Center
                            )
                        }
                    },
                    title = {
                        TextFallout(
                            alertDialogHeader,
                            getDialogTextColor(this),
                            getDialogTextColor(this),
                            24.sp, Alignment.CenterStart, Modifier,
                            TextAlign.Start
                        )
                    },
                    text = {
                        TextFallout(
                            alertDialogMessage,
                            getDialogTextColor(this),
                            getDialogTextColor(this),
                            16.sp, Alignment.CenterStart, Modifier,
                            TextAlign.Start
                        )
                    },
                    containerColor = getDialogBackground(this),
                    textContentColor = getDialogTextColor(this),
                    shape = RectangleShape,
                )
            }
        }

        fun hideSoundSettings() {
            showSoundSettings2 = false
            showSoundSettings = false
        }
        if (showSoundSettings && isHorror.value != true) {
            LaunchedEffect(Unit) {
                delay(50L)
                playNotificationSound(this@MainActivity) { showSoundSettings2 = true }
            }

            if (showSoundSettings2) {
                AlertDialog(
                    modifier = Modifier.border(width = 4.dp, color = getKnobColor(this)),
                    onDismissRequest = { saveData(this); hideSoundSettings() },
                    confirmButton = {
                        TextFallout(
                            stringResource(R.string.save),
                            getDialogBackground(this),
                            getDialogBackground(this),
                            18.sp,
                            Alignment.Center,
                            Modifier
                                .background(getDialogTextColor(this))
                                .clickableCancel(this) { saveData(this); hideSoundSettings() }
                                .padding(4.dp),
                            TextAlign.Center
                        )
                    },
                    title = {
                        TextFallout(
                            stringResource(R.string.sound),
                            getDialogTextColor(this),
                            getDialogTextColor(this),
                            24.sp,
                            Alignment.Center,
                            Modifier,
                            TextAlign.Center
                        )
                    },
                    text = {
                        var radioVolume by remember { mutableFloatStateOf(save.radioVolume) }
                        var soundVolume by remember { mutableFloatStateOf(save.soundVolume) }
                        var ambientVolume by remember { mutableFloatStateOf(save.ambientVolume) }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                TextFallout(
                                    stringResource(R.string.radio),
                                    getDialogTextColor(this@MainActivity),
                                    getDialogTextColor(this@MainActivity),
                                    16.sp,
                                    Alignment.CenterStart,
                                    modifier = Modifier.fillMaxWidth(0.33f),
                                    TextAlign.Start,
                                )

                                SliderCustom(
                                    this@MainActivity,
                                    { radioVolume },
                                    {
                                        radioVolume = it
                                        save.radioVolume = it
                                        setRadioVolume(it)
                                    }
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                TextFallout(
                                    stringResource(R.string.ambient),
                                    getDialogTextColor(this@MainActivity),
                                    getDialogTextColor(this@MainActivity),
                                    16.sp,
                                    Alignment.CenterStart,
                                    modifier = Modifier.fillMaxWidth(0.33f),
                                    TextAlign.Start
                                )

                                SliderCustom(this@MainActivity, { ambientVolume }, {
                                    ambientVolume = it
                                    save.ambientVolume = it
                                    setAmbientVolume(it / 2)
                                })
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                TextFallout(
                                    stringResource(R.string.sfx),
                                    getDialogTextColor(this@MainActivity),
                                    getDialogTextColor(this@MainActivity),
                                    16.sp,
                                    Alignment.CenterStart,
                                    modifier = Modifier.fillMaxWidth(0.33f),
                                    TextAlign.Start
                                )

                                SliderCustom(this@MainActivity, { soundVolume }, {
                                    soundVolume = it
                                    save.soundVolume = it
                                }, { playNotificationSound(this@MainActivity) {} })
                            }
                        }
                    },
                    containerColor = getDialogBackground(this),
                    textContentColor = getDialogTextColor(this),
                    shape = RectangleShape,
                )
            }
        }

        Scaffold(
            topBar = {
                var isPaused by remember { mutableStateOf(false) }
                val soundReducedObserver by soundReducedLiveData.observeAsState()
                key(styleIdForTop, soundReducedObserver) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .background(getMusicPanelColor(this))
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        TextFallout(
                            if (!isPaused && !soundReduced)
                                stringResource(R.string.next_song)
                            else
                                stringResource(R.string.none),
                            getMusicTextColor(this@MainActivity),
                            getMusicTextColor(this@MainActivity),
                            18.sp,
                            Alignment.Center,
                            Modifier
                                .weight(1f)
                                .wrapContentWidth()
                                .clickableOk(this@MainActivity) {
                                    if (isHorror.value == true) {
                                        return@clickableOk
                                    }
                                    if (!isPaused && !soundReduced) {
                                        nextSong(this@MainActivity)
                                    }
                                }
                                .background(getTextBackgroundColor(this@MainActivity))
                                .padding(4.dp),
                            TextAlign.Center
                        )

                        TextFallout(
                            when {
                                soundReduced -> stringResource(R.string.none)
                                isPaused -> stringResource(R.string.resume_radio)
                                else -> stringResource(R.string.pause_radio)
                            },
                            getMusicTextColor(this@MainActivity),
                            getMusicTextColor(this@MainActivity),
                            18.sp,
                            Alignment.Center,
                            Modifier
                                .weight(1f)
                                .wrapContentWidth()
                                .clickableOk(this@MainActivity) {
                                    if (soundReduced || isHorror.value == true) {
                                        return@clickableOk
                                    }
                                    if (isPaused) {
                                        resumeRadio()
                                        isPaused = false
                                    } else {
                                        pauseRadio()
                                        isPaused = true
                                    }
                                }
                                .background(getTextBackgroundColor(this@MainActivity))
                                .padding(4.dp),
                            TextAlign.Center
                        )
                        TextFallout(
                            stringResource(R.string.sound),
                            getMusicTextColor(this@MainActivity),
                            getMusicTextColor(this@MainActivity),
                            18.sp,
                            Alignment.Center,
                            Modifier
                                .weight(1f)
                                .wrapContentWidth()
                                .clickableOk(this@MainActivity) {
                                    showSoundSettings = true
                                }
                                .background(getTextBackgroundColor(this@MainActivity))
                                .padding(4.dp),
                            TextAlign.Center
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(Modifier.padding(innerPadding)) {
                when {
                    showRules -> {
                        ShowRules(this@MainActivity) { showRules = false }
                    }
                    deckSelection -> {
                        DeckSelection(this@MainActivity) { deckSelection = false }
                    }
                    showAbout -> {
                        ShowAbout(this@MainActivity) { showAbout = false }
                    }
                    showPvE -> {
                        if (!CResources(save.getCustomDeckCopy()).isCustomDeckValid()) {
                            showAlertDialog(
                                stringResource(R.string.custom_deck_is_illegal),
                                stringResource(R.string.deck_illegal_body),
                                null
                            )
                            showPvE = false
                        } else {
                            ShowSelectPvE(this@MainActivity, ::showAlertDialog) { showPvE = false }
                        }
                    }
                    showPvP -> {
                        if (!CResources(save.getCustomDeckCopy()).isCustomDeckValid()) {
                            showAlertDialog(
                                stringResource(R.string.custom_deck_is_illegal),
                                stringResource(R.string.deck_illegal_body),
                                null
                            )
                            showPvP = false
                        } else if (cronetEngine == null) {
                            Toast.makeText(
                                this@MainActivity,
                                "Failed to init CronetEngine. Multiplayer is unavailable.",
                                Toast.LENGTH_LONG
                            ).show()
                            showPvP = false
                        } else {
                            ShowPvP(this@MainActivity, ::showAlertDialog) { showPvP = false }
                        }
                    }
                    showVision -> {
                        ShowStyles(activity = this@MainActivity, {
                            styleIdForTop = Style.entries[it]
                            save.styleId = it
                            saveData(this@MainActivity)
                        }) { showVision = false }
                    }
                    showSettings -> {
                        ShowTrueSettings(this@MainActivity) { showSettings = false }
                    }
                    showDailys -> {
                        ShowDailys(this@MainActivity) { showDailys = false }
                    }
                    showMarket -> {
                        ShowTraders(this@MainActivity) { showMarket = false }
                    }
                    else -> {
                        LaunchedEffect(Unit) {
                            val currentHash = save.getCurrentDateHashCode()
                            if (currentHash != save.challengesHash) {
                                val capsFound = Random.nextInt(15, 25)
                                showAlertDialog(
                                    getString(R.string.daily_update_head),
                                    getString(R.string.daily_update_body, capsFound.toString()),
                                    null
                                )
                                save.challengesHash = currentHash
                                save.updateChallenges()
                                save.updateDailyStats()
                                save.capsInHand += capsFound
                                saveData(this@MainActivity)
                            }
                        }

                        BoxWithConstraints {
                            val width = maxWidth.dpToPx().toInt()
                            val height = maxHeight.dpToPx().toInt()
                            MainMenu(
                                { StylePicture(this@MainActivity, styleId, width, height) },
                                { deckSelection = true },
                                { showAbout = true },
                                { showPvE = true },
                                { showPvP = true },
                                { showRules = true },
                                { showVision = true },
                                { showSettings = true },
                                { showDailys = true },
                                { showMarket = true }
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun MainMenu(
        showStyledMenu: @Composable () -> Unit,
        showDeckSelection: () -> Unit,
        showAbout: () -> Unit,
        showPvE: () -> Unit,
        showPvP: () -> Unit,
        showRules: () -> Unit,
        showVision: () -> Unit,
        showSettings: () -> Unit,
        showDailys: () -> Unit,
        showMarket: () -> Unit,
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(getBackgroundColor(this))
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    Modifier
                        .fillMaxWidth(0.66f)
                        .fillMaxHeight()
                        .padding(start = 12.dp)
                        .drawBehind {
                            drawPath(
                                Path().apply {
                                    moveTo(0f, size.height)
                                    lineTo(0f, size.height / 4)
                                    lineTo(size.width, size.height / 4)
                                    lineTo(size.width, size.height)
                                },
                                color = getDividerColor(this@MainActivity),
                                style = Stroke(width = 8f),
                            )
                        }) {
                    TextFallout(
                        text = getString(R.string.app_name),
                        getTextColor(this@MainActivity),
                        getTextStrokeColor(this@MainActivity),
                        28.sp,
                        Alignment.CenterStart,
                        Modifier
                            .wrapContentWidth()
                            .align(Alignment.Top)
                            .padding(start = 12.dp, top = 0.dp, end = 12.dp)
                            .background(getBackgroundColor(this@MainActivity))
                            .padding(start = 4.dp, top = 0.dp, end = 4.dp),
                        TextAlign.Start
                    )

                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
                        TextFallout(
                            "VER #${
                                packageManager.getPackageInfo(
                                    "com.unicorns.invisible.caravan",
                                    PackageManager.MATCH_ALL
                                ).versionName
                            }",
                            getTextColor(this@MainActivity),
                            getTextStrokeColor(this@MainActivity),
                            14.sp,
                            Alignment.CenterEnd,
                            Modifier.padding(end = 12.dp),
                            TextAlign.End
                        )
                    }
                }

                Row(
                    Modifier
                        .fillMaxSize()
                        .padding(start = 12.dp, end = 12.dp)
                        .drawBehind {
                            drawPath(
                                Path().apply {
                                    moveTo(0f, size.height / 4)
                                    lineTo(size.width, size.height / 4)
                                    lineTo(size.width, size.height)
                                },
                                color = getDividerColor(this@MainActivity),
                                style = Stroke(width = 8f),
                            )
                        },
                ) {
                    val discord = buildAnnotatedString {
                        withLink(
                            link = LinkAnnotation.Url(
                                url = "https://discord.gg/xSTJpjvzJV",
                                styles = TextLinkStyles(
                                    style = SpanStyle(
                                        color = getTextStrokeColor(this@MainActivity),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily(Font(R.font.monofont)),
                                        textDecoration = TextDecoration.Underline,
                                        drawStyle = Stroke()
                                    )
                                )
                            ),
                        ) {
                            append(stringResource(R.string.menu_discord))
                        }
                    }
                    Box(Modifier.fillMaxSize()) {
                        TextFallout(
                            discord,
                            getTextColor(this@MainActivity),
                            getTextStrokeColor(this@MainActivity),
                            18.sp,
                            Alignment.Center,
                            Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 12.dp),
                            TextAlign.Center
                        )
                        TextFallout(
                            stringResource(R.string.menu_discord),
                            getTextColor(this@MainActivity),
                            Color.Transparent,
                            18.sp,
                            Alignment.Center,
                            Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 12.dp),
                            TextAlign.Center
                        )
                    }
                }
            }

            BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Row(
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 4.dp)
                            .drawBehind {
                                drawPath(
                                    Path().apply {
                                        moveTo(0f, 0f)
                                        lineTo(0f, size.height * 3 / 4)
                                        lineTo(size.width, size.height * 3 / 4)
                                        lineTo(size.width, 0f)
                                    },
                                    color = getDividerColor(this@MainActivity),
                                    style = Stroke(width = 8f),
                                )
                            },
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        TextFallout(
                            getString(R.string.menu_vision),
                            getTextColor(this@MainActivity),
                            getTextStrokeColor(this@MainActivity),
                            18.sp,
                            Alignment.Center,
                            Modifier
                                .background(getBackgroundColor(this@MainActivity))
                                .padding(horizontal = 4.dp)
                                .background(getTextBackgroundColor(this@MainActivity))
                                .clickableOk(this@MainActivity) {
                                    if (isHorror.value == true) {
                                        return@clickableOk
                                    }
                                    showVision()
                                }
                                .padding(4.dp),
                            TextAlign.Center
                        )

                        TextFallout(
                            getString(R.string.menu_settings),
                            getTextColor(this@MainActivity),
                            getTextStrokeColor(this@MainActivity),
                            18.sp,
                            Alignment.Center,
                            Modifier
                                .background(getBackgroundColor(this@MainActivity))
                                .padding(horizontal = 4.dp)
                                .clickableOk(this@MainActivity) {
                                    if (isHorror.value == true) {
                                        return@clickableOk
                                    }
                                    showSettings()
                                }
                                .background(getTextBackgroundColor(this@MainActivity))
                                .padding(4.dp),
                            TextAlign.Center
                        )

                        TextFallout(
                            getString(R.string.menu_about),
                            getTextColor(this@MainActivity),
                            getTextStrokeColor(this@MainActivity),
                            18.sp,
                            Alignment.Center,
                            Modifier
                                .background(getBackgroundColor(this@MainActivity))
                                .padding(horizontal = 4.dp)
                                .clickableOk(this@MainActivity) {
                                    if (isHorror.value == true) {
                                        return@clickableOk
                                    }
                                    showAbout()
                                }
                                .background(getTextBackgroundColor(this@MainActivity))
                                .padding(4.dp),
                            TextAlign.Center
                        )
                    }
                }
            }

            showStyledMenu()

            val state = rememberLazyListState()
            LazyColumn(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight()
                    .padding(bottom = 48.dp, top = 32.dp)
                    .scrollbar(
                        state,
                        alignEnd = false,
                        knobColor = getKnobColor(this@MainActivity),
                        trackColor = getTrackColor(this@MainActivity),
                        horizontal = false
                    ),
                state = state
            ) {
                item {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        @Composable
                        fun MenuItem(text: String, onClick: () -> Unit, isHorrorClickable: Boolean = false) {
                            TextFallout(
                                text,
                                getTextColor(this@MainActivity),
                                getTextStrokeColor(this@MainActivity),
                                20.sp,
                                Alignment.CenterStart,
                                Modifier
                                    .clickableOk(this@MainActivity) {
                                        if (isHorror.value == true && !isHorrorClickable) {
                                            return@clickableOk
                                        }
                                        onClick()
                                    }
                                    .background(getTextBackgroundColor(this@MainActivity))
                                    .padding(8.dp),
                                TextAlign.Start
                            )
                        }
                        Spacer(Modifier.height(32.dp))
                        MenuItem(stringResource(R.string.menu_pve), showPvE, isHorrorClickable = true)
                        Spacer(modifier = Modifier.height(16.dp))
                        MenuItem(stringResource(R.string.menu_pvp), showPvP)
                        Spacer(modifier = Modifier.height(16.dp))
                        MenuItem(stringResource(R.string.menu_rules), showRules)
                        Spacer(modifier = Modifier.height(16.dp))
                        MenuItem(stringResource(R.string.menu_deck), showDeckSelection)
                        Spacer(modifier = Modifier.height(16.dp))
                        MenuItem(stringResource(R.string.missions), showDailys)
                        Spacer(modifier = Modifier.height(16.dp))
                        MenuItem(stringResource(R.string.market), showMarket)
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }

    fun processChallengesMove(move: Challenge.Move, game: Game) {
        save.challenges.forEach { challenge ->
            challenge.processMove(move, game)
        }
    }
    fun processChallengesGameOver(game: Game) {
        save.challenges.forEach { challenge ->
            challenge.processGameResult(game)
        }
    }
}
