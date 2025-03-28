package com.unicorns.invisible.caravan

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.MutableLiveData
import com.sebaslogen.resaca.rememberScoped
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.challenge.Challenge
import com.unicorns.invisible.caravan.save.Save
import com.unicorns.invisible.caravan.save.loadGDSave
import com.unicorns.invisible.caravan.save.loadLocalSave
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.SliderCustom
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableCancel
import com.unicorns.invisible.caravan.utils.clickableOk
import com.unicorns.invisible.caravan.utils.cronetEngine
import com.unicorns.invisible.caravan.utils.dpToPx
import com.unicorns.invisible.caravan.utils.dpToSp
import com.unicorns.invisible.caravan.utils.getBackByStyle
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getCurrentDateHashCode
import com.unicorns.invisible.caravan.utils.getDialogBackground
import com.unicorns.invisible.caravan.utils.getDialogTextColor
import com.unicorns.invisible.caravan.utils.getDividerColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getMusicMarqueesColor
import com.unicorns.invisible.caravan.utils.getMusicPanelColor
import com.unicorns.invisible.caravan.utils.getMusicTextBackColor
import com.unicorns.invisible.caravan.utils.getMusicTextColor
import com.unicorns.invisible.caravan.utils.getStrokeColorByStyle
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextColorByStyle
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.nextSong
import com.unicorns.invisible.caravan.utils.pauseActivitySound
import com.unicorns.invisible.caravan.utils.pauseRadio
import com.unicorns.invisible.caravan.utils.playNotificationSound
import com.unicorns.invisible.caravan.utils.resumeActivitySound
import com.unicorns.invisible.caravan.utils.resumeRadio
import com.unicorns.invisible.caravan.utils.setAmbientVolume
import com.unicorns.invisible.caravan.utils.setRadioVolume
import com.unicorns.invisible.caravan.utils.startRadio
import com.unicorns.invisible.caravan.utils.stopSoundEffects
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.chromium.net.CronetEngine
import kotlin.random.Random


var save = Save(null)
val isSaveLoaded = MutableLiveData(false)

var soundReduced: Boolean = false
    set(value) {
        field = value
        soundReducedLiveData.postValue(value)
    }
private val soundReducedLiveData = MutableLiveData(soundReduced)
val playingSongName = MutableLiveData("")

@Suppress("MoveLambdaOutsideParentheses")
class MainActivity : SaveDataActivity() {
    val styleId
        get() = Style.entries.getOrElse(save.styleId) { Style.PIP_BOY }

    override fun onPause() {
        super.onPause()
        pauseActivitySound(save.playRadioInBack)
        stopSoundEffects()
    }

    override fun onResume() {
        super.onResume()
        resumeActivitySound()
    }

    override fun onSnapshotClientInitialized(isInited: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            if (save.playerId == null) {
                val playerId = if (isInited) getPlayerId() else ""

                val localSave = loadLocalSave(this@MainActivity)
                if (localSave != null && (localSave.playerId == playerId || playerId == "")) {
                    save = localSave
                } else {
                    val loadedSave = loadGDSave(this@MainActivity)
                    if (loadedSave != null) {
                        save = loadedSave
                    } else {
                        save = Save(playerId)
                        saveData(this@MainActivity)
                    }
                }

                if (save.playerId == "" && playerId != "") {
                    save.playerId = playerId
                    saveData(this@MainActivity)
                }

                isSaveLoaded.postValue(true)
                startRadio(this@MainActivity)
            }
        }
    }

    @SuppressLint("UnusedBoxWithConstraintsScope")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (cronetEngine == null) {
            try {
                val myBuilder = CronetEngine.Builder(this)
                cronetEngine = myBuilder.build()
            } catch (_: Throwable) {
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
            R.string.intro_tip_36,
            R.string.intro_tip_37,
            R.string.intro_tip_38,
            R.string.intro_tip_l10,
            R.string.intro_tip_l11,
        ).random(Random(id.hashCode()))
        // TODO: more advices!!!!

        setContent {
            Box(Modifier.safeDrawingPadding()) {
                var isIntroScreen by rememberScoped { mutableStateOf(true) }
                if (isIntroScreen) {
                    val localSave by rememberScoped { mutableStateOf(loadLocalSave(this@MainActivity)) }
                    val (textColor, strokeColor, backColor) = if (localSave == null) {
                        Triple(
                            Color(this@MainActivity.getColor(R.color.colorText)),
                            Color(this@MainActivity.getColor(R.color.colorTextStroke)),
                            Color(this@MainActivity.getColor(R.color.colorBack))
                        )
                    } else {
                        val style = Style.entries[localSave!!.styleId]
                        Triple(
                            getTextColorByStyle(this@MainActivity, style),
                            getStrokeColorByStyle(this@MainActivity, style),
                            getBackByStyle(this@MainActivity, style)
                        )
                    }
                    Box(
                        if (isSaveLoaded.value == true) {
                            Modifier
                                .fillMaxSize()
                                .background(backColor)
                                .clickableOk(this@MainActivity) {
                                    isIntroScreen = false
                                }
                        } else {
                            Modifier
                                .fillMaxSize()
                                .background(backColor)
                        },
                        contentAlignment = Alignment.Center
                    ) {
                        val k by isSaveLoaded.observeAsState()

                        @Composable
                        fun ColumnScope.CaravanTitle(weight: Float) {
                            Box(Modifier
                                .fillMaxWidth()
                                .weight(weight)
                                .padding(vertical = 4.dp)
                                .zIndex(5f)) {
                                Column(
                                    Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    if (k == true) {
                                        TextFallout(
                                            "CARAVAN",
                                            textColor,
                                            strokeColor,
                                            40.sp,
                                            Modifier.padding(top = 8.dp),
                                        )
                                        TextFallout(
                                            stringResource(R.string.tap_to_play),
                                            textColor,
                                            strokeColor,
                                            24.sp,
                                            Modifier.padding(4.dp),
                                        )
                                    } else {
                                        TextFallout(
                                            "PLEASE\nSTAND BY",
                                            textColor,
                                            strokeColor,
                                            32.sp,
                                            Modifier.padding(4.dp),
                                        )
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    TextFallout(
                                        getString(advice),
                                        textColor,
                                        strokeColor,
                                        18.sp,
                                        Modifier.padding(vertical = 4.dp, horizontal = 12.dp),
                                    )
                                }
                            }
                        }
                        @Composable
                        fun ColumnScope.Picture(weight: Float) {
                            Box(Modifier
                                .fillMaxWidth()
                                .weight(weight)
                                .paint(
                                    painterResource(R.drawable.caravan_main2),
                                    contentScale = ContentScale.Fit
                                )
                            )
                        }
                        @Composable
                        fun ColumnScope.PicAuthorLink(weight: Float) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .weight(weight),
                                contentAlignment = Alignment.BottomEnd
                            ) {
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
                                    14.sp,
                                    Modifier,
                                )
                            }
                        }
                        key(k) {
                            BoxWithConstraints(Modifier.fillMaxSize()) {
                                if (maxHeight > maxWidth) {
                                    Column(
                                        Modifier
                                            .fillMaxSize()
                                            .padding(4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        CaravanTitle(0.29f)
                                        Picture(0.66f)
                                        PicAuthorLink(0.05f)
                                    }
                                } else {
                                    Row(Modifier.fillMaxSize()) {
                                        Column(
                                            Modifier
                                                .fillMaxHeight()
                                                .weight(2f),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            CaravanTitle(1f)
                                        }
                                        Column(
                                            Modifier
                                                .fillMaxHeight()
                                                .weight(1f),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Box(Modifier.weight(0.05f))
                                            Picture(0.9f)
                                            PicAuthorLink(0.05f)
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Screen()
                }
            }
        }
    }

    @Composable
    fun Screen() {
        var showPvE by rememberSaveable { mutableStateOf(false) }
        var showPvP by rememberSaveable { mutableStateOf(false) }
        var deckSelection by rememberSaveable { mutableStateOf(false) }
        var customDeckSelection by rememberSaveable { mutableStateOf(false) }
        var showRules by rememberSaveable { mutableStateOf(false) }
        var showDailys by rememberSaveable { mutableStateOf(false) }
        var showMarket by rememberSaveable { mutableStateOf(false) }

        var showAbout by rememberSaveable { mutableStateOf(false) }
        var showSettings by rememberSaveable { mutableStateOf(false) }
        var showVision by rememberSaveable { mutableStateOf(false) }
        var styleIdForTop by rememberSaveable { mutableStateOf(styleId) }

        var showSoundSettings by remember { mutableStateOf(false) }

        var showAlertDialog by remember { mutableStateOf(false) }
        var alertDialogHeader by remember { mutableStateOf("") }
        var alertDialogMessage by remember { mutableStateOf("") }
        var alertGoBack: (() -> Unit)? by rememberScoped { mutableStateOf(null) }
        var isCustomDeckAlert by remember { mutableStateOf(false) }

        fun showAlertDialog(header: String, message: String, goBack: (() -> Unit)?) {
            alertDialogHeader = header
            alertDialogMessage = message
            alertGoBack = goBack
            showAlertDialog = true
        }

        fun hideAlertDialog() {
            showAlertDialog = false
            isCustomDeckAlert = false
        }

        if (showAlertDialog) {
            LaunchedEffect(Unit) {
                playNotificationSound(this@MainActivity)
            }
            AlertDialog(
                modifier = Modifier.border(width = 4.dp, color = getTextColor(this)),
                onDismissRequest = { hideAlertDialog() },
                confirmButton = {
                    TextFallout(
                        stringResource(R.string.close),
                        getDialogBackground(this),
                        getDialogBackground(this),
                        18.sp,
                        Modifier
                            .background(getDialogTextColor(this))
                            .clickableCancel(this) { hideAlertDialog() }
                            .padding(4.dp),
                        textAlignment = TextAlign.Start
                    )
                },
                dismissButton = {
                    if (alertGoBack != null) {
                        TextFallout(
                            if (isCustomDeckAlert) {
                                stringResource(R.string.deck_custom)
                            } else {
                                stringResource(R.string.back_to_menu)
                            },
                            getDialogBackground(this),
                            getDialogBackground(this), 18.sp,
                            Modifier
                                .background(getDialogTextColor(this))
                                .clickableCancel(this) {
                                    hideAlertDialog()
                                    alertGoBack?.invoke()
                                }
                                .padding(4.dp)
                        )
                    }
                },
                title = {
                    TextFallout(
                        alertDialogHeader,
                        getDialogTextColor(this),
                        getDialogTextColor(this),
                        24.sp, Modifier,
                        textAlignment = TextAlign.Start
                    )
                },
                text = {
                    TextFallout(
                        alertDialogMessage,
                        getDialogTextColor(this),
                        getDialogTextColor(this),
                        16.sp, Modifier,
                        textAlignment = TextAlign.Start
                    )
                },
                containerColor = getDialogBackground(this),
                textContentColor = getDialogTextColor(this),
                shape = RectangleShape
            )
        }

        fun hideSoundSettings() {
            showSoundSettings = false
        }
        if (showSoundSettings) {
            LaunchedEffect(Unit) {
                playNotificationSound(this@MainActivity)
            }

            AlertDialog(
                modifier = Modifier.border(width = 4.dp, color = getKnobColor(this)),
                onDismissRequest = { saveData(this); hideSoundSettings() },
                confirmButton = {
                    TextFallout(
                        stringResource(R.string.save),
                        getDialogBackground(this),
                        getDialogBackground(this),
                        18.sp,
                        Modifier
                            .background(getDialogTextColor(this))
                            .clickableCancel(this) { saveData(this); hideSoundSettings() }
                            .padding(4.dp),
                    )
                },
                title = {
                    TextFallout(
                        stringResource(R.string.sound),
                        getDialogTextColor(this),
                        getDialogTextColor(this),
                        24.sp,
                        Modifier,
                    )
                },
                text = {
                    var radioVolume by remember { mutableFloatStateOf(save.radioVolume) }
                    var soundVolume by remember { mutableFloatStateOf(save.soundVolume) }
                    var ambientVolume by remember { mutableFloatStateOf(save.ambientVolume) }

                    @Composable
                    fun Setting(title: String, get: () -> Float, set: (Float) -> Unit, onFinished: () -> Unit) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextFallout(
                                title,
                                getDialogTextColor(this@MainActivity),
                                getDialogTextColor(this@MainActivity),
                                16.sp,
                                Modifier.weight(1f),
                            )

                            Box(Modifier.weight(2.5f)) {
                                SliderCustom(this@MainActivity, get, set, onFinished)
                            }
                        }
                    }

                    Column {
                        Setting(stringResource(R.string.radio), { radioVolume }, {
                            radioVolume = it
                            save.radioVolume = it
                            setRadioVolume(it)
                        }, {})
                        Setting(stringResource(R.string.ambient), { ambientVolume }, {
                            ambientVolume = it
                            save.ambientVolume = it
                            setAmbientVolume(it / 2)
                        }, {})
                        Setting(stringResource(R.string.sfx), { soundVolume }, {
                            soundVolume = it
                            save.soundVolume = it
                        }) { playNotificationSound(this@MainActivity) }
                    }
                },
                containerColor = getDialogBackground(this),
                textContentColor = getDialogTextColor(this),
                shape = RectangleShape
            )
        }

        Scaffold(
            topBar = {
                var isPaused by rememberScoped { mutableStateOf(false) }
                val soundReducedObserver by soundReducedLiveData.observeAsState()
                val songName by playingSongName.observeAsState()
                key(styleIdForTop, soundReducedObserver, songName) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                            .background(getMusicPanelColor(this@MainActivity))
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = songName.let { if (it.isNullOrEmpty() || isPaused || soundReduced) "[NONE]" else it },
                            modifier = Modifier
                                .fillMaxWidth(0.45f)
                                .padding(horizontal = 2.dp)
                                .basicMarquee(Int.MAX_VALUE),
                            color = getMusicMarqueesColor(this@MainActivity),
                            fontFamily = FontFamily(Font(R.font.monofont)),
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                        )
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BoxWithConstraints(Modifier.fillMaxSize()) {
                                val button1Text = if (!isPaused && !soundReduced)
                                    stringResource(R.string.next_song)
                                else
                                    stringResource(R.string.none)
                                val button2Text = when {
                                    soundReduced -> stringResource(R.string.none)
                                    isPaused -> stringResource(R.string.resume_radio)
                                    else -> stringResource(R.string.pause_radio)
                                }
                                val button3Text = stringResource(R.string.sound)

                                val style = TextStyle(
                                    color = getMusicTextColor(this@MainActivity),
                                    fontFamily = FontFamily(Font(R.font.monofont)),
                                    textAlign = TextAlign.Center,
                                )

                                Row(
                                    Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    @Composable
                                    fun PlayerButton(text: String, onClick: () -> Unit) {
                                        Text(
                                            text = text,
                                            modifier = Modifier
                                                .clickableOk(this@MainActivity, onClick)
                                                .background(getMusicTextBackColor(this@MainActivity))
                                                .padding(horizontal = 2.dp),
                                            style = style,
                                            fontSize = 16.dp.dpToSp(),
                                            maxLines = 1
                                        )
                                    }

                                    PlayerButton(button1Text) {
                                        if (!isPaused && !soundReduced) {
                                            nextSong(this@MainActivity)
                                        }
                                    }
                                    PlayerButton(button2Text) {
                                        if (soundReduced) {
                                            return@PlayerButton
                                        }
                                        if (isPaused) {
                                            resumeRadio()
                                        } else {
                                            pauseRadio()
                                        }
                                        isPaused = !isPaused
                                    }
                                    PlayerButton(button3Text) { showSoundSettings = true }
                                }
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            @Composable
            fun showAlertCustomDeck() {
                isCustomDeckAlert = true
                showAlertDialog(
                    stringResource(R.string.custom_deck_is_illegal),
                    stringResource(R.string.deck_illegal_body),
                    { customDeckSelection = true }
                )
            }

            Box(Modifier.padding(innerPadding)) {
                when {
                    showRules -> {
                        ShowRules(this@MainActivity) { showRules = false }
                    }
                    customDeckSelection -> {
                        isCustomDeckAlert = false
                        SetCustomDeck(this@MainActivity) { customDeckSelection = false }
                    }
                    deckSelection -> {
                        DeckSelection(this@MainActivity) { deckSelection = false }
                    }
                    showAbout -> {
                        ShowAbout(this@MainActivity) { showAbout = false }
                    }
                    showPvE -> {
                        if (!save.getCurrentCustomDeck().isCustomDeckValid()) {
                            showAlertCustomDeck()
                            showPvE = false
                        } else {
                            ShowSelectPvE(this@MainActivity, ::showAlertDialog) { showPvE = false }
                        }
                    }
                    showPvP -> {
                        if (!save.getCurrentCustomDeck().isCustomDeckValid()) {
                            showAlertCustomDeck()
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
                        ShowTrueSettings(this@MainActivity, ::showAlertDialog) { showSettings = false }
                    }
                    showDailys -> {
                        ShowDailys(this@MainActivity) { showDailys = false }
                    }
                    showMarket -> {
                        ShowTraders(this@MainActivity) { showMarket = false }
                    }
                    else -> {
                        LaunchedEffect(Unit) {
                            val currentHash = getCurrentDateHashCode()
                            if (currentHash != save.dailyHash) {
                                val capsFound = Random.nextInt(15, 31)
                                showAlertDialog(
                                    getString(R.string.daily_update_head),
                                    getString(R.string.daily_update_body, capsFound.toString()),
                                    null
                                )
                                save.dailyHash = currentHash
                                save.updateChallenges()
                                save.updateEnemiesBanks()
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
                        Modifier
                            .wrapContentWidth()
                            .align(Alignment.Top)
                            .padding(start = 12.dp, top = 0.dp, end = 12.dp)
                            .background(getBackgroundColor(this@MainActivity))
                            .padding(start = 4.dp, top = 0.dp, end = 4.dp),
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
                            Modifier.padding(end = 12.dp),
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
                            Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 12.dp),
                        )
                        TextFallout(
                            stringResource(R.string.menu_discord),
                            getTextColor(this@MainActivity),
                            Color.Transparent,
                            18.sp,
                            Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 12.dp),
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
                            Modifier
                                .background(getBackgroundColor(this@MainActivity))
                                .padding(horizontal = 4.dp)
                                .background(getTextBackgroundColor(this@MainActivity))
                                .clickableOk(this@MainActivity) {
                                    showVision()
                                }
                                .padding(4.dp),
                        )

                        TextFallout(
                            getString(R.string.menu_settings),
                            getTextColor(this@MainActivity),
                            getTextStrokeColor(this@MainActivity),
                            18.sp,
                            Modifier
                                .background(getBackgroundColor(this@MainActivity))
                                .padding(horizontal = 4.dp)
                                .clickableOk(this@MainActivity) {
                                    showSettings()
                                }
                                .background(getTextBackgroundColor(this@MainActivity))
                                .padding(4.dp),
                        )

                        TextFallout(
                            getString(R.string.menu_about),
                            getTextColor(this@MainActivity),
                            getTextStrokeColor(this@MainActivity),
                            18.sp,
                            Modifier
                                .background(getBackgroundColor(this@MainActivity))
                                .padding(horizontal = 4.dp)
                                .clickableOk(this@MainActivity) {
                                    showAbout()
                                }
                                .background(getTextBackgroundColor(this@MainActivity))
                                .padding(4.dp),
                        )
                    }
                }
            }

            showStyledMenu()

            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight()
                    .padding(bottom = 48.dp, top = 32.dp)
                    .padding(horizontal = 20.dp),
            ) {
                @Composable
                fun MenuItem(text: String, onClick: () -> Unit) {
                    TextFallout(
                        text,
                        getTextColor(this@MainActivity),
                        getTextStrokeColor(this@MainActivity),
                        20.sp,
                        Modifier
                            .clickableOk(this@MainActivity) {
                                onClick()
                            }
                            .background(getTextBackgroundColor(this@MainActivity))
                            .padding(8.dp),
                    )
                }
                Spacer(Modifier.height(32.dp))
                MenuItem(stringResource(R.string.menu_pve), showPvE)
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

    fun processChallengesMove(move: Challenge.Move, game: Game) {
        (save.challengesNew + save.challengesInf).forEach { challenge ->
            challenge.processMove(move, game)
        }
    }
    fun processChallengesGameOver(game: Game) {
        (save.challengesNew + save.challengesInf).forEach { challenge ->
            challenge.processGameResult(game)
        }
    }
}
