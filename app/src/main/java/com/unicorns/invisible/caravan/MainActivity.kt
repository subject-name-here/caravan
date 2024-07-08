package com.unicorns.invisible.caravan

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.colorResource
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
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.challenge.Challenge
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.save.Save
import com.unicorns.invisible.caravan.save.getSaveFile
import com.unicorns.invisible.caravan.save.json
import com.unicorns.invisible.caravan.save.loadFromGD
import com.unicorns.invisible.caravan.save.loadLocalSave
import com.unicorns.invisible.caravan.save.saveOnGD
import com.unicorns.invisible.caravan.utils.CheckboxCustom
import com.unicorns.invisible.caravan.utils.SliderCustom
import com.unicorns.invisible.caravan.utils.SwitchCustom
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableCancel
import com.unicorns.invisible.caravan.utils.clickableOk
import com.unicorns.invisible.caravan.utils.dpToPx
import com.unicorns.invisible.caravan.utils.effectPlayers
import com.unicorns.invisible.caravan.utils.effectPlayersLock
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
import com.unicorns.invisible.caravan.utils.isRadioStopped
import com.unicorns.invisible.caravan.utils.nextSong
import com.unicorns.invisible.caravan.utils.pause
import com.unicorns.invisible.caravan.utils.playClickSound
import com.unicorns.invisible.caravan.utils.playCloseSound
import com.unicorns.invisible.caravan.utils.playNotificationSound
import com.unicorns.invisible.caravan.utils.playQuitMultiplayer
import com.unicorns.invisible.caravan.utils.resume
import com.unicorns.invisible.caravan.utils.scrollbar
import com.unicorns.invisible.caravan.utils.sendRequest
import com.unicorns.invisible.caravan.utils.setAmbientVolume
import com.unicorns.invisible.caravan.utils.setRadioVolume
import com.unicorns.invisible.caravan.utils.startRadio
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okio.withLock
import org.chromium.net.CronetEngine
import java.util.UUID


const val crvnUrl = "http://crvnserver.onrender.com"

var saveGlobal: Save? = null

var userId = ""

private var readyFlag = MutableLiveData(false)

var isQPinging = MutableLiveData(false)

@Suppress("MoveLambdaOutsideParentheses")
class MainActivity : SaveDataActivity() {
    val save: Save?
        get() = saveGlobal

    var goBack: (() -> Unit)? = null

    var styleId: Style = Style.PIP_BOY

    var animationSpeed = MutableLiveData(AnimationSpeed.NORMAL)

    fun checkIfCustomDeckCanBeUsedInGame(playerCResources: CResources): Boolean {
        return playerCResources.deckSize >= MIN_DECK_SIZE && playerCResources.numOfNumbers >= MIN_NUM_OF_NUMBERS
    }

    override fun onPause() {
        super.onPause()
        stopQPing()
        pause()
        effectPlayersLock.withLock {
            effectPlayers.forEach { if (it.isPlaying) it.stop() }
        }
    }

    override fun onResume() {
        super.onResume()
        resume()
    }

    override fun onDestroy() {
        stopQPing()
        super.onDestroy()
    }

    override fun onSnapshotClientInitialized() {
        CoroutineScope(Dispatchers.Default).launch {
            val savedOnGDData = fetchDataFromDrive()
            if (savedOnGDData == null || savedOnGDData.isEmpty()) {
                val saveFile = getSaveFile(this@MainActivity)
                saveGlobal = if (saveFile.exists()) {
                    loadLocalSave(this@MainActivity) ?: Save()
                } else {
                    Save()
                }
                if (saveOnGD(this@MainActivity).await()) {
                    if (saveFile.exists()) {
                        saveFile.delete()
                    }
                }
            }
            loadFromGD(this@MainActivity)
            readyFlag.postValue(true)
            styleId = Style.entries[save!!.styleId]
            startRadio(this@MainActivity)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (userId == "") {
            userId = UUID.randomUUID().toString()
        }
        if (cronetEngine == null) {
            try {
                val myBuilder = CronetEngine.Builder(this)
                cronetEngine = myBuilder.build()
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "Failed to init CronetEngine. Multiplayer is unavailable.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        val advice = listOf(
            R.string.intro_tip_1,
            R.string.intro_tip_2,
            R.string.intro_tip_3,
            R.string.intro_tip_4,
            R.string.intro_tip_5,
            R.string.intro_tip_6,
            R.string.intro_tip_7,
            R.string.intro_tip_8,
            R.string.intro_tip_9,
            R.string.intro_tip_10,
            R.string.intro_tip_11,
            R.string.intro_tip_12,
            R.string.intro_tip_13,
            R.string.intro_tip_14,
            R.string.intro_tip_15,
            R.string.intro_tip_16,
            R.string.intro_tip_17,
            R.string.intro_tip_vault,
            R.string.intro_tip_desert,
        ).random()

        setContent {
            val k by readyFlag.observeAsState()
            var isIntroScreen by rememberSaveable { mutableStateOf(k != true) }

            @Composable
            fun getColors(): Triple<Color, Color, Color> {
                return Triple(
                    (if (k == true) getTextColor(this) else colorResource(R.color.colorText)),
                    (if (k == true) getBackgroundColor(this) else colorResource(R.color.colorBack)),
                    (if (k == true) getTextStrokeColor(this) else colorResource(R.color.colorTextStroke)),
                )
            }

            styleId = Style.entries[save?.styleId ?: 1]
            animationSpeed.value = save?.animationSpeed ?: AnimationSpeed.NORMAL
            val (textColor, backgroundColor, strokeColor) = getColors()
            val modifier = if (k == true) {
                Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .clickableOk(this) {
                        if (readyFlag.value == true) {
                            isIntroScreen = false
                        }
                    }
            } else {
                Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
            }
            if (isIntroScreen) {
                Box(
                    modifier,
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (k == true) {
                            TextFallout(
                                "CARAVAN",
                                textColor,
                                strokeColor,
                                40.sp,
                                Alignment.Center,
                                Modifier.padding(4.dp),
                                TextAlign.Center
                            )
                            TextFallout(
                                stringResource(R.string.tap_to_play),
                                textColor,
                                strokeColor,
                                24.sp,
                                Alignment.Center,
                                Modifier.padding(4.dp),
                                TextAlign.Center
                            )
                            Spacer(Modifier.height(8.dp))
                        } else {
                            TextFallout(
                                "PLEASE\nSTAND BY",
                                textColor,
                                strokeColor,
                                32.sp,
                                Alignment.Center,
                                Modifier.padding(4.dp),
                                TextAlign.Center
                            )
                        }
                        TextFallout(
                            getString(advice),
                            textColor,
                            strokeColor,
                            16.sp,
                            Alignment.Center,
                            Modifier.padding(vertical = 4.dp, horizontal = 12.dp),
                            TextAlign.Center
                        )
                    }
                }
            } else {
                Screen()
            }
        }
    }

    @Composable
    fun Screen() {
        var deckSelection by rememberSaveable { mutableStateOf(false) }
        var showPvP by rememberSaveable { mutableStateOf(false) }
        var showHouse by rememberSaveable { mutableStateOf(false) }
        var showAbout by rememberSaveable { mutableStateOf(false) }
        var showGameStats by rememberSaveable { mutableStateOf(false) }
        var showTutorial by rememberSaveable { mutableStateOf(false) }
        var showRules by rememberSaveable { mutableStateOf(false) }
        var showSettings by rememberSaveable { mutableStateOf(false) }
        var showStock by rememberSaveable { mutableStateOf(false) }
        var showDailys by rememberSaveable { mutableStateOf(false) }

        var showVision by rememberSaveable { mutableStateOf(false) }
        var styleIdForTop by rememberSaveable { mutableStateOf(styleId) }

        var showSoundSettings by remember { mutableStateOf(false) }
        var showSoundSettings2 by remember { mutableStateOf(false) }

        var selectedDeck by rememberSaveable {
            mutableStateOf(
                save?.selectedDeck ?: (CardBack.STANDARD to false)
            )
        }

        var showAlertDialog by remember { mutableStateOf(false) }
        var showAlertDialog2 by remember { mutableStateOf(false) }
        var alertDialogHeader by remember { mutableStateOf("") }
        var alertDialogMessage by remember { mutableStateOf("") }

        fun showAlertDialog(header: String, message: String) {
            showAlertDialog = true
            alertDialogHeader = header
            alertDialogMessage = message
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
                        if (goBack != null) {
                            TextFallout(
                                stringResource(R.string.back_to_menu),
                                getDialogBackground(this),
                                getDialogBackground(this), 18.sp, Alignment.Center,
                                Modifier
                                    .background(getDialogTextColor(this))
                                    .clickableCancel(this) {
                                        hideAlertDialog(); goBack?.invoke(); goBack = null
                                    }
                                    .padding(4.dp),
                                TextAlign.Center
                            )
                        }
                    },
                    title = {
                        TextFallout(
                            alertDialogHeader, getDialogTextColor(this), getDialogTextColor(this),
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
        if (showSoundSettings) {
            LaunchedEffect(Unit) {
                delay(50L)
                playNotificationSound(this@MainActivity) { showSoundSettings2 = true }
            }

            if (showSoundSettings2) {
                AlertDialog(
                    modifier = Modifier.border(width = 4.dp, color = getKnobColor(this)),
                    onDismissRequest = {
                        saveOnGD(this); hideSoundSettings()
                    },
                    confirmButton = {
                        TextFallout(
                            stringResource(R.string.save),
                            getDialogBackground(this),
                            getDialogBackground(this),
                            18.sp,
                            Alignment.Center,
                            Modifier
                                .background(getDialogTextColor(this))
                                .clickableCancel(this) { saveOnGD(this); hideSoundSettings() }
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
                        var radioVolume by remember { mutableFloatStateOf(save?.radioVolume ?: 1f) }
                        var soundVolume by remember { mutableFloatStateOf(save?.soundVolume ?: 1f) }
                        var ambientVolume by remember {
                            mutableFloatStateOf(
                                save?.ambientVolume ?: 1f
                            )
                        }
                        var intro by remember { mutableStateOf(save?.useCaravanIntro ?: true) }
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
                                        save?.radioVolume = it
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
                                    ambientVolume = it; save?.ambientVolume = it
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
                                    soundVolume = it; save?.soundVolume = it
                                }, { playNotificationSound(this@MainActivity) {} })
                            }
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextFallout(
                                    stringResource(R.string.intro_music),
                                    getDialogTextColor(this@MainActivity),
                                    getDialogTextColor(this@MainActivity),
                                    20.sp,
                                    Alignment.CenterStart,
                                    Modifier.fillMaxWidth(0.66f),
                                    TextAlign.Start
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                SwitchCustom(this@MainActivity, { intro }) {
                                    intro = !intro
                                    save?.let {
                                        it.useCaravanIntro = !it.useCaravanIntro
                                        if (it.useCaravanIntro) {
                                            playClickSound(this@MainActivity)
                                        } else {
                                            playCloseSound(this@MainActivity)
                                        }
                                        saveOnGD(this@MainActivity)
                                    }
                                }
                            }
                        }
                    },
                    containerColor = getDialogBackground(this),
                    textContentColor = getDialogTextColor(this),
                    shape = RectangleShape,
                )
            }
        }

        var showQDialog by remember { mutableStateOf(false) }
        var showQDialog2 by remember { mutableStateOf(false) }
        var roomNumber by remember { mutableIntStateOf(0) }

        fun showQDialog(room: Int) {
            showQDialog = true
            roomNumber = room
        }
        fun hideQDialog() {
            showQDialog = false
            showQDialog2 = false
        }

        var showQSetDialog by remember { mutableStateOf(false) }
        var showQSetDialog2 by remember { mutableStateOf(false) }
        var useCustomDeckQ by rememberSaveable { mutableStateOf(false) }
        val isQPingingInner = isQPinging.observeAsState()
        fun showQSetDialog() {
            showQSetDialog = true
        }
        fun hideQSetDialog() {
            showQSetDialog = false
            showQSetDialog2 = false
        }
        if (showQSetDialog) {
            LaunchedEffect(Unit) {
                delay(50L)
                playNotificationSound(this@MainActivity) { showQSetDialog2 = true }
            }

            if (showQSetDialog2) {
                AlertDialog(
                    modifier = Modifier.border(width = 4.dp, color = getTextColor(this)),
                    onDismissRequest = {},
                    confirmButton = {},
                    dismissButton = {
                        TextFallout(
                            stringResource(R.string.back_to_menu),
                            getDialogBackground(this),
                            getDialogBackground(this), 18.sp, Alignment.Center,
                            Modifier
                                .background(getDialogTextColor(this))
                                .clickableCancel(this) {
                                    hideQSetDialog()
                                }
                                .padding(4.dp),
                            TextAlign.Center
                        )
                    },
                    title = {
                        TextFallout(
                            stringResource(R.string.q_pinging), getDialogTextColor(this), getDialogTextColor(this),
                            24.sp, Alignment.CenterStart, Modifier,
                            TextAlign.Start
                        )
                    },
                    text = {
                        Column {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextFallout(
                                    stringResource(R.string.q_pinging_explanation),
                                    getDialogTextColor(this@MainActivity),
                                    getDialogTextColor(this@MainActivity),
                                    20.sp,
                                    Alignment.CenterStart,
                                    Modifier.fillMaxWidth(),
                                    TextAlign.Start
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextFallout(
                                    stringResource(R.string.q_pinging_warning),
                                    getDialogTextColor(this@MainActivity),
                                    getDialogTextColor(this@MainActivity),
                                    20.sp,
                                    Alignment.CenterStart,
                                    Modifier.fillMaxWidth(),
                                    TextAlign.Start
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextFallout(
                                    stringResource(R.string.q_pinging_switch),
                                    getDialogTextColor(this@MainActivity),
                                    getDialogTextColor(this@MainActivity),
                                    20.sp,
                                    Alignment.CenterStart,
                                    Modifier.fillMaxWidth(0.66f),
                                    TextAlign.Start
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                SwitchCustom(this@MainActivity, { isQPingingInner.value == true }) {
                                    if (isQPingingInner.value == false) {
                                        launchQPing(useCustomDeckQ, ::showQDialog)
                                    } else {
                                        stopQPing()
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextFallout(
                                    stringResource(R.string.pve_use_custom_deck),
                                    getDialogTextColor(this@MainActivity),
                                    getDialogTextColor(this@MainActivity),
                                    20.sp,
                                    Alignment.CenterStart,
                                    Modifier.fillMaxWidth(0.66f),
                                    TextAlign.Start
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                CheckboxCustom(this@MainActivity, { useCustomDeckQ }, {
                                    useCustomDeckQ = !useCustomDeckQ
                                    if (useCustomDeckQ) {
                                        playClickSound(this@MainActivity)
                                    } else {
                                        playCloseSound(this@MainActivity)
                                    }
                                }, { isQPingingInner.value == false })
                            }
                        }
                    },
                    containerColor = getDialogBackground(this),
                    textContentColor = getDialogTextColor(this),
                    shape = RectangleShape,
                )
            }
        }
        if (showQDialog) {
            LaunchedEffect(Unit) {
                delay(50L)
                playNotificationSound(this@MainActivity) { showQDialog2 = true }
            }

            if (showQDialog2) {
                AlertDialog(
                    modifier = Modifier.border(width = 4.dp, color = getTextColor(this)),
                    onDismissRequest = {},
                    confirmButton = {
                        TextFallout(
                            stringResource(R.string.i_ll_join),
                            getDialogBackground(this),
                            getDialogBackground(this),
                            18.sp, Alignment.Center,
                            Modifier
                                .background(getDialogTextColor(this))
                                .clickableCancel(this) {
                                    hideQDialog()
                                }
                                .padding(4.dp),
                            TextAlign.Center
                        )
                    },
                    dismissButton = {
                        TextFallout(
                            stringResource(R.string.nah_i_ll_pass),
                            getDialogBackground(this),
                            getDialogBackground(this), 18.sp, Alignment.Center,
                            Modifier
                                .background(getDialogTextColor(this))
                                .clickableCancel(this) {
                                    hideQDialog()
                                    sendQNegativeResponse(roomNumber)
                                    playQuitMultiplayer(this)
                                }
                                .padding(4.dp),
                            TextAlign.Center
                        )
                    },
                    title = {
                        TextFallout(
                            stringResource(R.string.you_have_a_match), getDialogTextColor(this), getDialogTextColor(this),
                            24.sp, Alignment.CenterStart, Modifier,
                            TextAlign.Start
                        )
                    },
                    text = {
                        TextFallout(
                            stringResource(R.string.q_pinging_message, roomNumber),
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

        Scaffold(
            topBar = {
                var isPaused by remember { mutableStateOf(false) }
                key(styleIdForTop) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .background(getMusicPanelColor(this))
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        TextFallout(
                            "Q",
                            if (isQPingingInner.value == true) Color.Green else Color.Red,
                            getMusicTextColor(this@MainActivity),
                            18.sp,
                            Alignment.Center,
                            Modifier
                                .weight(1f)
                                .wrapContentWidth()
                                .clickableOk(this@MainActivity) {
                                    showQSetDialog()
                                }
                                .background(getTextBackgroundColor(this@MainActivity))
                                .padding(4.dp),
                            TextAlign.Center
                        )

                        TextFallout(
                            if (isPaused) "NONE" else ">|",
                            getMusicTextColor(this@MainActivity),
                            getMusicTextColor(this@MainActivity),
                            18.sp,
                            Alignment.Center,
                            Modifier
                                .weight(1f)
                                .wrapContentWidth()
                                .clickableOk(this@MainActivity) {
                                    if (!isPaused) {
                                        nextSong(this@MainActivity)
                                    }
                                }
                                .background(getTextBackgroundColor(this@MainActivity))
                                .padding(4.dp),
                            TextAlign.Center
                        )

                        TextFallout(
                            if (isPaused) "|>" else "||",
                            getMusicTextColor(this@MainActivity),
                            getMusicTextColor(this@MainActivity),
                            18.sp,
                            Alignment.Center,
                            Modifier
                                .weight(1f)
                                .wrapContentWidth()
                                .clickableOk(this@MainActivity) {
                                    if (isPaused) {
                                        isRadioStopped = false
                                        resume(byButton = true)
                                        isPaused = false
                                    } else {
                                        isRadioStopped = true
                                        pause(byButton = true)
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
                        ShowRules(activity = this@MainActivity) { showRules = false }
                    }

                    showTutorial -> {
                        Tutorial(activity = this@MainActivity) { showTutorial = false }
                    }

                    deckSelection -> {
                        DeckSelection(
                            this@MainActivity,
                            { selectedDeck },
                            { back, isAlt -> selectedDeck = back to isAlt }
                        ) { deckSelection = false }
                    }

                    showAbout -> {
                        ShowAbout(activity = this@MainActivity) { showAbout = false }
                    }

                    showHouse -> {
                        ShowHouse(
                            activity = this@MainActivity,
                            selectedDeck = { selectedDeck },
                            ::showAlertDialog
                        ) { showHouse = false }
                    }


                    showGameStats -> {
                        ShowPvE(
                            activity = this@MainActivity,
                            selectedDeck = { selectedDeck },
                            ::showAlertDialog
                        ) { showGameStats = false }
                    }

                    showPvP -> {
                        if (!checkIfCustomDeckCanBeUsedInGame(CResources(save?.getCustomDeckCopy() ?: CustomDeck()))) {
                            showAlertDialog(
                                stringResource(R.string.custom_deck_is_too_small),
                                stringResource(R.string.custom_deck_is_too_small_message)
                            )
                            showPvP = false
                        } else {
                            ShowPvP(
                                activity = this@MainActivity,
                                selectedDeck = { selectedDeck },
                                ::showAlertDialog,
                                if (roomNumber == 0) null else roomNumber,
                            ) { roomNumber = 0; showPvP = false }
                        }
                    }

                    showVision -> {
                        ShowSettings(activity = this@MainActivity, { styleId }, {
                            styleId = Style.entries[it]
                            styleIdForTop = styleId
                            save?.let { s ->
                                s.styleId = styleId.ordinal
                                saveOnGD(this@MainActivity)
                            }
                        }, ::showAlertDialog) { showVision = false }
                    }

                    showSettings -> {
                        ShowTrueSettings(
                            this@MainActivity,
                            { animationSpeed.value!! },
                            {
                                animationSpeed.value = it
                                save!!.animationSpeed = it
                                saveOnGD(this@MainActivity)
                            }
                        ) { showSettings = false }
                    }

                    showStock -> {
                        StockMarket(this@MainActivity) { showStock = false }
                    }

                    showDailys -> {
                        ShowDailys(this@MainActivity) { showDailys = false }
                    }

                    else -> {
                        BoxWithConstraints {
                            val width = maxWidth.dpToPx().toInt()
                            val height = maxHeight.dpToPx().toInt()
                            MainMenu(
                                { deckSelection = true },
                                { showAbout = true },
                                { showGameStats = true },
                                { showHouse = true },
                                { showPvP = true },
                                { showTutorial = true },
                                { showRules = true },
                                { showVision = true },
                                { showSettings = true },
                                { showDailys = true },
                                { showStock = true },
                                ::showAlertDialog,
                            )
                            StylePicture(this@MainActivity, styleId, userId.hashCode(), width, height)
                        }
                    }
                }
            }
        }

    }

    @Composable
    fun MainMenu(
        showDeckSelection: () -> Unit,
        showAbout: () -> Unit,
        showPvE: () -> Unit,
        showHouse: () -> Unit,
        showPvP: () -> Unit,
        showTutorial: () -> Unit,
        showRules: () -> Unit,
        showVision: () -> Unit,
        showSettings: () -> Unit,
        showDailys: () -> Unit,
        showStock: () -> Unit,
        showAlertDialog: (String, String) -> Unit,
    ) {
        Spacer(Modifier.height(32.dp))
        Column(
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


            val state = rememberLazyListState()
            LazyColumn(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
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
                        fun MenuItem(text: String, onClick: () -> Unit) {
                            TextFallout(
                                text,
                                getTextColor(this@MainActivity),
                                getTextStrokeColor(this@MainActivity),
                                20.sp,
                                Alignment.CenterStart,
                                Modifier
                                    .clickableOk(this@MainActivity) { onClick() }
                                    .background(getTextBackgroundColor(this@MainActivity))
                                    .padding(8.dp),
                                TextAlign.Start
                            )
                        }
                        Spacer(Modifier.height(32.dp))
                        MenuItem(stringResource(R.string.menu_pve), showPvE)
                        Spacer(modifier = Modifier.height(20.dp))
                        MenuItem(stringResource(R.string.lucky_38), showHouse)
                        Spacer(modifier = Modifier.height(20.dp))
                        MenuItem(stringResource(R.string.menu_pvp), showPvP)
//                        Spacer(modifier = Modifier.height(20.dp))
//                        MenuItem(stringResource(R.string.q_pinging), showQ)
                        Spacer(modifier = Modifier.height(20.dp))
                        MenuItem(stringResource(R.string.menu_tutorial), showTutorial)
                        Spacer(modifier = Modifier.height(20.dp))
                        MenuItem(stringResource(R.string.menu_rules), showRules)
                        Spacer(modifier = Modifier.height(20.dp))
                        MenuItem(stringResource(R.string.menu_deck), showDeckSelection)
                        Spacer(modifier = Modifier.height(20.dp))
                        MenuItem(stringResource(R.string.missions), showDailys)
//                        Spacer(modifier = Modifier.height(20.dp))
//                        MenuItem(stringResource(R.string.stack_market), showStock)
                        Spacer(modifier = Modifier.height(32.dp))
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
                                    showAbout()
                                }
                                .background(getTextBackgroundColor(this@MainActivity))
                                .padding(4.dp),
                            TextAlign.Center
                        )
                    }
                }
            }
        }

        LaunchedEffect(Unit) {
            if (save?.updateSoldCards() == true) {
                saveOnGD(this@MainActivity)
//                showAlertDialog(
//                    getString(R.string.card_prices_update),
//                    getString(R.string.some_cards_are_now_more_expensive)
//                )
            }
            save?.updateChallenges()
        }
    }

    private var qJob: Job? = null
    private fun launchQPing(isCustom: Boolean, showAlertDialog: (Int) -> Unit) {
        isQPinging.value = true
        qJob = CoroutineScope(Dispatchers.IO).launch {
            fun sendQPing() {
                sendRequest("$crvnUrl/crvn/q_ping?vid=${userId}&is_custom=${isCustom.toPythonBool()}") { result ->
                    if (result.getString("body") == "0") {
                        CoroutineScope(Dispatchers.Unconfined).launch {
                            delay(9500L)
                            sendQPing()
                        }
                        return@sendRequest
                    }
                    val response = try {
                        json.decodeFromString<Int>(result.getString("body"))
                    } catch (e: Exception) {
                        CoroutineScope(Dispatchers.Unconfined).launch {
                            delay(9500L)
                            sendQPing()
                        }
                        return@sendRequest
                    }

                    if (response in (10..22229)) {
                        showAlertDialog(response)
                        isQPinging.postValue(false)
                    } else {
                        CoroutineScope(Dispatchers.Unconfined).launch {
                            delay(9500L)
                            sendQPing()
                        }
                    }
                }
            }
            sendQPing()
        }
    }
    private fun stopQPing() {
        qJob?.cancel()
        qJob = null
        isQPinging.value = false
    }
    private fun sendQNegativeResponse(room: Int) {
        sendRequest("$crvnUrl/crvn/q_ping_response?room=${room}") {}
    }

    fun processChallengesMove(move: Challenge.Move, game: Game) {
        save?.let {
            it.challenges.forEach {
                it.processMove(move, game)
            }
        }
    }
    fun processChallengesGameOver(game: Game) {
        save?.let {
            it.challenges.forEach {
                it.processGameResult(game)
            }
        }
    }

    companion object {
        const val MIN_DECK_SIZE = 30
        const val MIN_NUM_OF_NUMBERS = 15
    }
}
