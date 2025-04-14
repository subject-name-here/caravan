package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
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
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.ambient
import caravan.composeapp.generated.resources.app_name
import caravan.composeapp.generated.resources.back_to_menu
import caravan.composeapp.generated.resources.caravan_main2
import caravan.composeapp.generated.resources.close
import caravan.composeapp.generated.resources.custom_deck_is_illegal
import caravan.composeapp.generated.resources.daily_update_body
import caravan.composeapp.generated.resources.daily_update_head
import caravan.composeapp.generated.resources.deck_custom
import caravan.composeapp.generated.resources.deck_illegal_body
import caravan.composeapp.generated.resources.intro_tip_10
import caravan.composeapp.generated.resources.intro_tip_11
import caravan.composeapp.generated.resources.intro_tip_12
import caravan.composeapp.generated.resources.intro_tip_13
import caravan.composeapp.generated.resources.intro_tip_14
import caravan.composeapp.generated.resources.intro_tip_15
import caravan.composeapp.generated.resources.intro_tip_2
import caravan.composeapp.generated.resources.intro_tip_3
import caravan.composeapp.generated.resources.intro_tip_33
import caravan.composeapp.generated.resources.intro_tip_34
import caravan.composeapp.generated.resources.intro_tip_35
import caravan.composeapp.generated.resources.intro_tip_36
import caravan.composeapp.generated.resources.intro_tip_37
import caravan.composeapp.generated.resources.intro_tip_38
import caravan.composeapp.generated.resources.intro_tip_4
import caravan.composeapp.generated.resources.intro_tip_5
import caravan.composeapp.generated.resources.intro_tip_6
import caravan.composeapp.generated.resources.intro_tip_9
import caravan.composeapp.generated.resources.intro_tip_arctic
import caravan.composeapp.generated.resources.intro_tip_desert
import caravan.composeapp.generated.resources.intro_tip_enclave
import caravan.composeapp.generated.resources.intro_tip_l10
import caravan.composeapp.generated.resources.intro_tip_l11
import caravan.composeapp.generated.resources.intro_tip_new_world
import caravan.composeapp.generated.resources.intro_tip_snuffles
import caravan.composeapp.generated.resources.intro_tip_vault
import caravan.composeapp.generated.resources.market
import caravan.composeapp.generated.resources.menu_about
import caravan.composeapp.generated.resources.menu_deck
import caravan.composeapp.generated.resources.menu_discord
import caravan.composeapp.generated.resources.menu_pve
import caravan.composeapp.generated.resources.menu_pvp
import caravan.composeapp.generated.resources.menu_rules
import caravan.composeapp.generated.resources.menu_settings
import caravan.composeapp.generated.resources.menu_vision
import caravan.composeapp.generated.resources.missions
import caravan.composeapp.generated.resources.monofont
import caravan.composeapp.generated.resources.next_song
import caravan.composeapp.generated.resources.none
import caravan.composeapp.generated.resources.pause_radio
import caravan.composeapp.generated.resources.pve_stats
import caravan.composeapp.generated.resources.radio
import caravan.composeapp.generated.resources.resume_radio
import caravan.composeapp.generated.resources.save
import caravan.composeapp.generated.resources.sfx
import caravan.composeapp.generated.resources.sound
import caravan.composeapp.generated.resources.tap_to_play
import com.sebaslogen.resaca.rememberScoped
import com.unicorns.invisible.caravan.color.Colors
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.challenge.Challenge
import com.unicorns.invisible.caravan.save.Save
import com.unicorns.invisible.caravan.save.loadLocalSave
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.SliderCustom
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.VertScrollbar
import com.unicorns.invisible.caravan.utils.clickableCancel
import com.unicorns.invisible.caravan.utils.clickableOk
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
import com.unicorns.invisible.caravan.utils.getMusicPanelColorByStyle
import com.unicorns.invisible.caravan.utils.getMusicTextBackColor
import com.unicorns.invisible.caravan.utils.getMusicTextColor
import com.unicorns.invisible.caravan.utils.getStrokeColorByStyle
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextColorByStyle
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.nextSong
import com.unicorns.invisible.caravan.utils.pauseRadio
import com.unicorns.invisible.caravan.utils.playNotificationSound
import com.unicorns.invisible.caravan.utils.resumeRadio
import com.unicorns.invisible.caravan.utils.setAmbientVolume
import com.unicorns.invisible.caravan.utils.setRadioVolume
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.random.Random


var save = Save(null)
var isSaveLoaded by mutableStateOf(false)

var soundReduced: Boolean = false
    set(value) {
        field = value
        soundReducedLiveData = value
    }
private var soundReducedLiveData by mutableStateOf(soundReduced)
var playingSongName by mutableStateOf("")

val styleId
    get() = Style.entries.getOrElse(save.styleId) { Style.PIP_BOY }
private var styleIdMutableData by mutableStateOf(styleId)

@Composable
fun App() {
    val advice = listOf(
        Res.string.intro_tip_2,
        Res.string.intro_tip_3,
        Res.string.intro_tip_4,
        Res.string.intro_tip_5,
        Res.string.intro_tip_6,
        Res.string.intro_tip_9,
        Res.string.intro_tip_10,
        Res.string.intro_tip_11,
        Res.string.intro_tip_12,
        Res.string.intro_tip_13,
        Res.string.intro_tip_14,
        Res.string.intro_tip_15,
        Res.string.intro_tip_vault,
        Res.string.intro_tip_desert,
        Res.string.intro_tip_arctic,
        Res.string.intro_tip_enclave,
        Res.string.intro_tip_new_world,
        Res.string.intro_tip_snuffles,
        Res.string.intro_tip_33,
        Res.string.intro_tip_34,
        Res.string.intro_tip_35,
        Res.string.intro_tip_36,
        Res.string.intro_tip_37,
        Res.string.intro_tip_38,
        Res.string.intro_tip_l10,
        Res.string.intro_tip_l11,
    ).random(Random(id.hashCode()))
    // TODO: more advices!!!!


    var isIntroScreen by rememberScoped { mutableStateOf(true) }
    Box(
        Modifier
            .fillMaxSize()
            .background(getMusicPanelColorByStyle(styleIdMutableData))
            .statusBarsPadding()
            .displayCutoutPadding()
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(getBackByStyle(styleIdMutableData))
                .navigationBarsPadding()

        ) {
            if (isIntroScreen) {
                val localSave by produceState<Save?>(Save()) {
                    value = loadLocalSave()
                }
                val (textColor, strokeColor, backColor) = if (localSave == null) {
                    Triple(
                        Colors.ColorText,
                        Colors.ColorTextStroke,
                        Colors.ColorBack
                    )
                } else {
                    val style = Style.entries[localSave!!.styleId]
                    styleIdMutableData = style
                    Triple(
                        getTextColorByStyle(style),
                        getStrokeColorByStyle(style),
                        getBackByStyle(style)
                    )
                }
                Box(
                    if (isSaveLoaded == true) {
                        Modifier
                            .fillMaxSize()
                            .background(backColor)
                            .clickableOk {
                                isIntroScreen = false
                            }
                    } else {
                        Modifier
                            .fillMaxSize()
                            .background(backColor)
                    },
                    contentAlignment = Alignment.Center
                ) {
                    @Composable
                    fun ColumnScope.CaravanTitle(weight: Float) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .weight(weight)
                                .padding(vertical = 4.dp)
                                .zIndex(5f)
                        ) {
                            Column(
                                Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (isSaveLoaded == true) {
                                    TextFallout(
                                        "CARAVAN",
                                        textColor,
                                        strokeColor,
                                        40.sp,
                                        Modifier.padding(top = 8.dp),
                                    )
                                    TextFallout(
                                        stringResource(Res.string.tap_to_play),
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
                                    stringResource(advice),
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
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .weight(weight)
                                .paint(
                                    painterResource(Res.drawable.caravan_main2),
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
                                                fontFamily = FontFamily(Font(Res.font.monofont)),
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
                    key(isSaveLoaded) {
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
    var showPvE by rememberScoped { mutableStateOf(false) }
    var showPvP by rememberScoped { mutableStateOf(false) }
    var deckSelection by rememberScoped { mutableStateOf(false) }
    var customDeckSelection by rememberScoped { mutableStateOf(false) }
    var showRules by rememberScoped { mutableStateOf(false) }
    var showDailys by rememberScoped { mutableStateOf(false) }
    var showMarket by rememberScoped { mutableStateOf(false) }

    var showAbout by rememberScoped { mutableStateOf(false) }
    var showSettings by rememberScoped { mutableStateOf(false) }
    var showVision by rememberScoped { mutableStateOf(false) }
    var showStats by rememberScoped { mutableStateOf(false) }

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
        playNotificationSound()
        showAlertDialog = true
    }

    fun hideAlertDialog() {
        showAlertDialog = false
        isCustomDeckAlert = false
    }

    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = { hideAlertDialog() },
            confirmButton = @Composable {
                TextFallout(
                    stringResource(Res.string.close),
                    getDialogBackground(),
                    getDialogBackground(),
                    18.sp,
                    Modifier
                        .padding(bottom = 4.dp)
                        .background(getDialogTextColor())
                        .clickableCancel { hideAlertDialog() }
                        .padding(4.dp),
                    textAlignment = TextAlign.Start
                )
            },
            modifier = Modifier.border(width = 4.dp, color = getTextColor()),
            dismissButton = @Composable {
                if (alertGoBack != null) {
                    TextFallout(
                        if (isCustomDeckAlert) {
                            stringResource(Res.string.deck_custom)
                        } else {
                            stringResource(Res.string.back_to_menu)
                        },
                        getDialogBackground(),
                        getDialogBackground(), 18.sp,
                        Modifier
                            .padding(bottom = 4.dp)
                            .background(getDialogTextColor())
                            .clickableCancel {
                                hideAlertDialog()
                                alertGoBack?.invoke()
                            }
                            .padding(4.dp)
                    )
                }
            },
            title = @Composable {
                TextFallout(
                    alertDialogHeader,
                    getDialogTextColor(),
                    getDialogTextColor(),
                    24.sp, Modifier,
                    textAlignment = TextAlign.Start
                )
            },
            text = @Composable {
                TextFallout(
                    alertDialogMessage,
                    getDialogTextColor(),
                    getDialogTextColor(),
                    16.sp, Modifier,
                    textAlignment = TextAlign.Start
                )
            },
            shape = RectangleShape,
            backgroundColor = getDialogBackground(),
            contentColor = getDialogTextColor(),
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = true
            )
        )
    }

    fun hideSoundSettings() {
        showSoundSettings = false
    }
    if (showSoundSettings) {
        LaunchedEffect(Unit) {
            playNotificationSound()
        }

        AlertDialog(
            modifier = Modifier.border(width = 4.dp, color = getKnobColor()),
            onDismissRequest = {
                saveData()
                hideSoundSettings()
            },
            confirmButton = @Composable {
                TextFallout(
                    stringResource(Res.string.save),
                    getDialogBackground(),
                    getDialogBackground(),
                    18.sp,
                    Modifier
                        .background(getDialogTextColor())
                        .clickableCancel {
                            saveData()
                            hideSoundSettings()
                        }
                        .padding(4.dp),
                )
            },
            title = @Composable {
                TextFallout(
                    stringResource(Res.string.sound),
                    getDialogTextColor(),
                    getDialogTextColor(),
                    24.sp,
                    Modifier,
                )
            },
            text = @Composable {
                var radioVolume by remember { mutableFloatStateOf(save.radioVolume) }
                var soundVolume by remember { mutableFloatStateOf(save.soundVolume) }
                var ambientVolume by remember { mutableFloatStateOf(save.ambientVolume) }

                @Composable
                fun Setting(title: String, get: () -> Float, set: (Float) -> Unit, onFinished: () -> Unit) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextFallout(
                            title,
                            getDialogTextColor(),
                            getDialogTextColor(),
                            16.sp,
                            Modifier.weight(1f),
                        )

                        Box(Modifier.weight(2.5f)) {
                            SliderCustom(get, set, onFinished)
                        }
                    }
                }

                Column {
                    Setting(stringResource(Res.string.radio), { radioVolume }, {
                        radioVolume = it
                        save.radioVolume = it
                        setRadioVolume(it)
                    }, {})
                    Setting(stringResource(Res.string.ambient), { ambientVolume }, {
                        ambientVolume = it
                        save.ambientVolume = it
                        setAmbientVolume(it / 2)
                    }, {})
                    Setting(stringResource(Res.string.sfx), { soundVolume }, {
                        soundVolume = it
                        save.soundVolume = it
                    }) {
                        CoroutineScope(Dispatchers.Unconfined).launch {
                            playNotificationSound()
                        }
                    }
                }
            },
            backgroundColor = getDialogBackground(),
            contentColor = getDialogTextColor(),
            shape = RectangleShape,
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = true
            )
        )
    }

    Scaffold(
        topBar = {
            var isPaused by rememberScoped { mutableStateOf(false) }
            key(styleIdMutableData, soundReducedLiveData, playingSongName) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                        .background(getMusicPanelColor())
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = playingSongName.let {
                            if (it.isEmpty() || isPaused || soundReducedLiveData)
                                "[NONE]"
                            else
                                it
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.45f)
                            .padding(horizontal = 2.dp)
                            .basicMarquee(Int.MAX_VALUE),
                        color = getMusicMarqueesColor(),
                        fontFamily = FontFamily(Font(Res.font.monofont)),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                    )
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BoxWithConstraints(Modifier.fillMaxSize()) {
                            val button1Text = if (!isPaused && !soundReducedLiveData)
                                stringResource(Res.string.next_song)
                            else
                                stringResource(Res.string.none)
                            val button2Text = when {
                                soundReducedLiveData -> stringResource(Res.string.none)
                                isPaused -> stringResource(Res.string.resume_radio)
                                else -> stringResource(Res.string.pause_radio)
                            }
                            val button3Text = stringResource(Res.string.sound)

                            val style = TextStyle(
                                color = getMusicTextColor(),
                                fontFamily = FontFamily(Font(Res.font.monofont)),
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
                                            .clickableOk(onClick)
                                            .background(getMusicTextBackColor())
                                            .padding(horizontal = 2.dp),
                                        style = style,
                                        fontSize = 16.dp.dpToSp(),
                                        maxLines = 1
                                    )
                                }

                                PlayerButton(button1Text) {
                                    if (!isPaused && !soundReduced) {
                                        nextSong()
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
                stringResource(Res.string.custom_deck_is_illegal),
                stringResource(Res.string.deck_illegal_body)
            ) { customDeckSelection = true }
        }

        Box(Modifier.padding(innerPadding)) {
            when {
                showRules -> {
                    ShowRules { showRules = false }
                }
                customDeckSelection -> {
                    isCustomDeckAlert = false
                    SetCustomDeck { customDeckSelection = false }
                }
                deckSelection -> {
                    DeckSelection { deckSelection = false }
                }
                showAbout -> {
                    ShowAbout { showAbout = false }
                }
                showPvE -> {
                    if (!save.getCurrentCustomDeck().isCustomDeckValid()) {
                        showAlertCustomDeck()
                        showPvE = false
                    } else {
                        ShowSelectPvE(::showAlertDialog) { showPvE = false }
                    }
                }
                showPvP -> {
                    if (!save.getCurrentCustomDeck().isCustomDeckValid()) {
                        showAlertCustomDeck()
                        showPvP = false
                    } else {
                        ShowPvP(::showAlertDialog) { showPvP = false }
                    }
                }
                showVision -> {
                    ShowStyles({
                        save.styleId = it
                        styleIdMutableData = Style.entries[it]
                        saveData()
                    }) { showVision = false }
                }
                showSettings -> {
                    ShowTrueSettings(::showAlertDialog) { showSettings = false }
                }
                showDailys -> {
                    ShowDailys { showDailys = false }
                }
                showMarket -> {
                    ShowTraders { showMarket = false }
                }
                showStats -> {
                    ShowStats { showStats = false }
                }
                else -> {
                    LaunchedEffect(Unit) {
                        val currentHash = getCurrentDateHashCode()
                        if (currentHash != save.dailyHash) {
                            val capsFound = Random.nextInt(15, 31)
                            showAlertDialog(
                                getString(Res.string.daily_update_head),
                                getString(Res.string.daily_update_body, capsFound.toString()),
                                null
                            )
                            save.dailyHash = currentHash
                            save.updateChallenges()
                            save.updateEnemiesBanks()
                            save.capsInHand += capsFound
                            saveData()
                        }
                    }

                    BoxWithConstraints {
                        val width = maxWidth.dpToPx().toInt()
                        val height = maxHeight.dpToPx().toInt()
                        MainMenu(
                            { StylePicture(styleId, width, height) },
                            { deckSelection = true },
                            { showAbout = true },
                            { showPvE = true },
                            { showPvP = true },
                            { showRules = true },
                            { showVision = true },
                            { showSettings = true },
                            { showDailys = true },
                            { showMarket = true },
                            { showStats = true }
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
    showStats: () -> Unit
) {
    BoxWithConstraints(
        Modifier
            .fillMaxSize()
            .background(getBackgroundColor())
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
                            color = getDividerColor(),
                            style = Stroke(width = 8f),
                        )
                    }
            ) {
                TextFallout(
                    text = stringResource(Res.string.app_name),
                    getTextColor(),
                    getTextStrokeColor(),
                    28.sp,
                    Modifier
                        .wrapContentWidth()
                        .align(Alignment.Top)
                        .padding(start = 12.dp, top = 0.dp, end = 12.dp)
                        .background(getBackgroundColor())
                        .padding(start = 4.dp, top = 0.dp, end = 4.dp),
                )

                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
                    TextFallout(
                        "VER #${getVersion()}",
                        getTextColor(),
                        getTextStrokeColor(),
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
                            color = getDividerColor(),
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
                                    color = getTextStrokeColor(),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily(Font(Res.font.monofont)),
                                    textDecoration = TextDecoration.Underline,
                                    drawStyle = Stroke()
                                )
                            )
                        ),
                    ) {
                        append(stringResource(Res.string.menu_discord))
                    }
                }
                Box(Modifier.fillMaxSize()) {
                    TextFallout(
                        discord,
                        getTextColor(),
                        getTextStrokeColor(),
                        18.sp,
                        Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 12.dp),
                    )
                    TextFallout(
                        stringResource(Res.string.menu_discord),
                        getTextColor(),
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
                                color = getDividerColor(),
                                style = Stroke(width = 8f),
                            )
                        },
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    TextFallout(
                        stringResource(Res.string.menu_vision),
                        getTextColor(),
                        getTextStrokeColor(),
                        18.sp,
                        Modifier
                            .background(getBackgroundColor())
                            .padding(horizontal = 4.dp)
                            .background(getTextBackgroundColor())
                            .clickableOk {
                                showVision()
                            }
                            .padding(4.dp),
                    )

                    TextFallout(
                        stringResource(Res.string.menu_settings),
                        getTextColor(),
                        getTextStrokeColor(),
                        18.sp,
                        Modifier
                            .background(getBackgroundColor())
                            .padding(horizontal = 4.dp)
                            .clickableOk {
                                showSettings()
                            }
                            .background(getTextBackgroundColor())
                            .padding(4.dp),
                    )

                    TextFallout(
                        stringResource(Res.string.menu_about),
                        getTextColor(),
                        getTextStrokeColor(),
                        18.sp,
                        Modifier
                            .background(getBackgroundColor())
                            .padding(horizontal = 4.dp)
                            .clickableOk {
                                showAbout()
                            }
                            .background(getTextBackgroundColor())
                            .padding(4.dp),
                    )
                }
            }
        }

        showStyledMenu()

        val state = rememberScrollState()

        VertScrollbar(state, alignment = Alignment.CenterStart)

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .fillMaxHeight()
                .padding(bottom = 48.dp, top = 32.dp)
                .padding(horizontal = 20.dp)
                .verticalScroll(state),
        ) {
            @Composable
            fun MenuItem(text: String, onClick: () -> Unit) {
                TextFallout(
                    text,
                    getTextColor(),
                    getTextStrokeColor(),
                    20.sp,
                    Modifier
                        .clickableOk {
                            onClick()
                        }
                        .background(getTextBackgroundColor())
                        .padding(8.dp),
                )
            }
            Spacer(Modifier.height(32.dp))
            MenuItem(stringResource(Res.string.menu_pve), showPvE)
            Spacer(modifier = Modifier.height(16.dp))
            MenuItem(stringResource(Res.string.menu_pvp), showPvP)
            Spacer(modifier = Modifier.height(16.dp))
            MenuItem(stringResource(Res.string.pve_stats), showStats)
            Spacer(modifier = Modifier.height(16.dp))
            MenuItem(stringResource(Res.string.menu_rules), showRules)
            Spacer(modifier = Modifier.height(16.dp))
            MenuItem(stringResource(Res.string.menu_deck), showDeckSelection)
            Spacer(modifier = Modifier.height(16.dp))
            MenuItem(stringResource(Res.string.missions), showDailys)
            Spacer(modifier = Modifier.height(16.dp))
            MenuItem(stringResource(Res.string.market), showMarket)
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