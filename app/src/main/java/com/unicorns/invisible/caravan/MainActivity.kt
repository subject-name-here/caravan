package com.unicorns.invisible.caravan

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.save.Save
import com.unicorns.invisible.caravan.save.loadSave
import com.unicorns.invisible.caravan.save.save
import com.unicorns.invisible.caravan.utils.currentPlayer
import com.unicorns.invisible.caravan.utils.getAccentColor
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getDividerColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.nextSong
import com.unicorns.invisible.caravan.utils.pause
import com.unicorns.invisible.caravan.utils.playNotificationSound
import com.unicorns.invisible.caravan.utils.radioPlayer
import com.unicorns.invisible.caravan.utils.resume
import com.unicorns.invisible.caravan.utils.scrollbar
import com.unicorns.invisible.caravan.utils.startRadio
import kotlinx.coroutines.delay
import org.chromium.net.CronetEngine
import java.util.UUID


const val crvnUrl = "http://crvnserver.onrender.com"


@Suppress("MoveLambdaOutsideParentheses")
class MainActivity : AppCompatActivity() {
    var save: Save? = null

    var goBack: (() -> Unit)? = null

    var id = ""

    var styleId = 1

    fun checkIfCustomDeckCanBeUsedInGame(playerCResources: CResources): Boolean {
        return playerCResources.deckSize >= MIN_DECK_SIZE && playerCResources.numOfNumbers >= MIN_NUM_OF_NUMBERS
    }

    override fun onPause() {
        super.onPause()
        radioPlayer?.pause()
        currentPlayer?.pause()
    }

    override fun onResume() {
        super.onResume()
        radioPlayer?.start()
        currentPlayer?.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id = UUID.randomUUID().toString()

        save = loadSave(this) ?: run {
            save(this, Save())
            loadSave(this)!!
        }
        styleId = save?.styleId ?: 1
        startRadio(this)

        if (cronetEngine == null) {
            try {
                val myBuilder = CronetEngine.Builder(this)
                cronetEngine = myBuilder.build()
            } catch (e: Exception) {
                Toast.makeText(this, "Failed to init CronetEngine. Multiplayer is unavailable.", Toast.LENGTH_LONG).show()
            }
        }

        setContent {
            var deckSelection by rememberSaveable { mutableStateOf(false) }
            var showPvP by rememberSaveable { mutableStateOf(false) }
            var showAbout by rememberSaveable { mutableStateOf(false) }
            var showGameStats by rememberSaveable { mutableStateOf(false) }
            var showTutorial by rememberSaveable { mutableStateOf(false) }
            var showRules by rememberSaveable { mutableStateOf(false) }

            var showSettings by rememberSaveable { mutableIntStateOf(0) }
            var styleIdForTop by rememberSaveable { mutableIntStateOf(styleId) }

            var showSoundSettings by remember { mutableStateOf(false) }
            var showSoundSettings2 by remember { mutableStateOf(false) }

            var selectedDeck by rememberSaveable { mutableStateOf(save?.selectedDeck ?: (CardBack.STANDARD to false)) }

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
                        modifier = Modifier.border(width = 4.dp, color = getAccentColor(this)),
                        onDismissRequest = { hideAlertDialog() },
                        confirmButton = { Text(text = stringResource(R.string.close), color = getAccentColor(this), modifier = Modifier.clickable { hideAlertDialog() }) },
                        dismissButton = { if (goBack != null) {
                            Text(
                                text = stringResource(R.string.back_to_menu), color = getAccentColor(this),
                                modifier = Modifier.clickable { hideAlertDialog(); goBack?.invoke(); goBack = null }
                            )
                        } },
                        title = { Text(text = alertDialogHeader, color = getTextColor(this)) },
                        text = { Text(text = alertDialogMessage) },
                        containerColor = getTextBackgroundColor(this),
                        textContentColor = getTextColor(this),
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
                        modifier = Modifier.border(width = 4.dp, color = getAccentColor(this)),
                        onDismissRequest = {
                            save(this, this.save!!)
                            hideSoundSettings()
                        },
                        confirmButton = {
                            Text(text = stringResource(R.string.save), color = getAccentColor(this), modifier = Modifier.clickable {
                                save(this, this.save!!); hideSoundSettings()
                            })
                        },
                        title = { Text(text = stringResource(R.string.sound), color = getTextColor(this)) },
                        text = {
                            var radioVolume by remember { mutableFloatStateOf(save?.radioVolume ?: 1f) }
                            var soundVolume by remember { mutableFloatStateOf(save?.soundVolume ?: 1f) }
                            var ambientVolume by remember { mutableFloatStateOf(save?.ambientVolume ?: 1f) }
                            var intro by remember { mutableStateOf(save?.useCaravanIntro ?: true) }
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = stringResource(R.string.radio),
                                        modifier = Modifier
                                            .clickable {
                                                nextSong(this@MainActivity)
                                            }
                                            .fillMaxWidth(0.33f),
                                        fontFamily = FontFamily(Font(R.font.monofont)),
                                        style = TextStyle(
                                            color = getTextColor(this@MainActivity),
                                            fontSize = 16.sp,
                                            textAlign = TextAlign.Center,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    )

                                    Slider(radioVolume, onValueChange = {
                                        radioVolume = it; save?.radioVolume = it
                                        radioPlayer?.setVolume(it, it)
                                    }, colors = SliderColors(
                                        thumbColor = getAccentColor(this@MainActivity),
                                        activeTrackColor = getAccentColor(this@MainActivity),
                                        activeTickColor = getAccentColor(this@MainActivity),
                                        inactiveTickColor = getAccentColor(this@MainActivity),
                                        inactiveTrackColor = getAccentColor(this@MainActivity),
                                        disabledThumbColor = Color.Gray,
                                        disabledActiveTrackColor = Color.Gray,
                                        disabledActiveTickColor = Color.Gray,
                                        disabledInactiveTickColor = Color.Gray,
                                        disabledInactiveTrackColor = Color.Gray,
                                    ))
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = stringResource(R.string.ambient),
                                        modifier = Modifier
                                            .clickable {
                                                nextSong(this@MainActivity)
                                            }
                                            .fillMaxWidth(0.33f),
                                        fontFamily = FontFamily(Font(R.font.monofont)),
                                        style = TextStyle(
                                            color = getTextColor(this@MainActivity),
                                            fontSize = 16.sp,
                                            textAlign = TextAlign.Center,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    )

                                    Slider(ambientVolume, onValueChange = {
                                        ambientVolume = it; save?.ambientVolume = it
                                        currentPlayer?.setVolume(it / 2, it / 2)
                                    }, colors = SliderColors(
                                        thumbColor = getAccentColor(this@MainActivity),
                                        activeTrackColor = getAccentColor(this@MainActivity),
                                        activeTickColor = getAccentColor(this@MainActivity),
                                        inactiveTickColor = getAccentColor(this@MainActivity),
                                        inactiveTrackColor = getAccentColor(this@MainActivity),
                                        disabledThumbColor = Color.Gray,
                                        disabledActiveTrackColor = Color.Gray,
                                        disabledActiveTickColor = Color.Gray,
                                        disabledInactiveTickColor = Color.Gray,
                                        disabledInactiveTrackColor = Color.Gray,
                                    ))
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = stringResource(R.string.sfx),
                                        modifier = Modifier
                                            .clickable {
                                                nextSong(this@MainActivity)
                                            }
                                            .fillMaxWidth(0.33f),
                                        fontFamily = FontFamily(Font(R.font.monofont)),
                                        style = TextStyle(
                                            color = getTextColor(this@MainActivity),
                                            fontSize = 16.sp,
                                            textAlign = TextAlign.Center,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    )

                                    Slider(soundVolume, onValueChange = {
                                        soundVolume = it; save?.soundVolume = it
                                    }, colors = SliderColors(
                                        thumbColor = getAccentColor(this@MainActivity),
                                        activeTrackColor = getAccentColor(this@MainActivity),
                                        activeTickColor = getAccentColor(this@MainActivity),
                                        inactiveTickColor = getAccentColor(this@MainActivity),
                                        inactiveTrackColor = getAccentColor(this@MainActivity),
                                        disabledThumbColor = Color.Gray,
                                        disabledActiveTrackColor = Color.Gray,
                                        disabledActiveTickColor = Color.Gray,
                                        disabledInactiveTickColor = Color.Gray,
                                        disabledInactiveTrackColor = Color.Gray,
                                    ), onValueChangeFinished = { playNotificationSound(this@MainActivity) {} })
                                }
                                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        modifier = Modifier.fillMaxWidth(0.66f),
                                        text = stringResource(R.string.intro_music),
                                        fontFamily = FontFamily(Font(R.font.monofont)),
                                        style = TextStyle(color = getTextColor(this@MainActivity), fontSize = 20.sp, textAlign = TextAlign.Center)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Switch(checked = intro, onCheckedChange = {
                                        intro = !intro
                                        save?.let {
                                            it.useCaravanIntro = !it.useCaravanIntro
                                            save(this@MainActivity, it)
                                        }
                                    }, colors = SwitchColors(
                                        checkedThumbColor = getTextBackgroundColor(this@MainActivity),
                                        checkedTrackColor = getAccentColor(this@MainActivity),
                                        checkedBorderColor = Color.Transparent,
                                        checkedIconColor = Color.Transparent,
                                        uncheckedThumbColor = getTextBackgroundColor(this@MainActivity),
                                        uncheckedTrackColor = getAccentColor(this@MainActivity),
                                        uncheckedBorderColor = Color.Transparent,
                                        uncheckedIconColor = Color.Transparent,
                                        disabledCheckedThumbColor = colorResource(R.color.red),
                                        disabledCheckedTrackColor = colorResource(R.color.white),
                                        disabledCheckedBorderColor = Color.Transparent,
                                        disabledCheckedIconColor = Color.Transparent,
                                        disabledUncheckedThumbColor = colorResource(R.color.red),
                                        disabledUncheckedTrackColor = colorResource(R.color.white),
                                        disabledUncheckedBorderColor = Color.Transparent,
                                        disabledUncheckedIconColor = Color.Transparent,
                                    )
                                    )
                                }
                            }
                        },
                        containerColor = getTextBackgroundColor(this),
                        textContentColor = getTextColor(this),
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
                                .background(getTextBackgroundColor(this))
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Text(
                                text = ">|",
                                modifier = Modifier.clickable {
                                    nextSong(this@MainActivity)
                                },
                                fontFamily = FontFamily(Font(R.font.monofont)),
                                style = TextStyle(
                                    color = getTextColor(this@MainActivity),
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )

                            Text(
                                text = if (isPaused) "|>" else "||",
                                modifier = Modifier.clickable {
                                    if (isPaused) {
                                        resume()
                                        isPaused = false
                                    } else {
                                        pause()
                                        isPaused = true
                                    }
                                },
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily(Font(R.font.monofont)),
                                style = TextStyle(
                                    color = getTextColor(this@MainActivity),
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                )
                            )

                            Text(
                                text = stringResource(R.string.sound),
                                modifier = Modifier.clickable {
                                    showSoundSettings = true
                                },
                                fontFamily = FontFamily(Font(R.font.monofont)),
                                style = TextStyle(
                                    color = getTextColor(this@MainActivity),
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                ),
                                fontWeight = FontWeight.ExtraBold
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
                        showGameStats -> {
                            ShowPvE(
                                activity = this@MainActivity,
                                selectedDeck = { selectedDeck },
                                ::showAlertDialog
                            ) { showGameStats = false }
                        }
                        showPvP -> {
                            if (!checkIfCustomDeckCanBeUsedInGame(CResources(save!!.getCustomDeckCopy()))) {
                                showAlertDialog(
                                    stringResource(R.string.custom_deck_is_too_small),
                                    stringResource(R.string.custom_deck_is_too_small_message)
                                )
                                showPvP = false
                            } else {
                                ShowPvP(
                                    activity = this@MainActivity,
                                    selectedDeck = { selectedDeck },
                                    ::showAlertDialog
                                ) { showPvP = false }
                            }
                        }
                        showSettings > 0 -> {
                            ShowSettings(activity = this@MainActivity, { styleId }, {
                                styleId = 1 - styleId
                                showSettings = 3 - showSettings
                                styleIdForTop = styleId
                                save?.let {
                                    it.styleId = styleId
                                    save(this@MainActivity, it)
                                }
                            }) { showSettings = 0 }
                        }
                        else -> {
                            MainMenu(
                                { deckSelection = true },
                                { showAbout = true },
                                { showGameStats = true },
                                { showPvP = true },
                                { showTutorial = true },
                                { showRules = true },
                                { showSettings = 1 }
                            )
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
        showPvP: () -> Unit,
        showTutorial: () -> Unit,
        showRules: () -> Unit,
        showSettings: () -> Unit,
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
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
                    Text(
                        modifier = Modifier
                            .wrapContentWidth()
                            .align(Alignment.Top)
                            .padding(start = 12.dp, top = 0.dp, end = 12.dp)
                            .background(getBackgroundColor(this@MainActivity))
                            .padding(start = 4.dp, top = 0.dp, end = 4.dp),
                        text = getString(R.string.app_name),
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily(Font(R.font.monofont)),
                        textAlign = TextAlign.Start,
                        style = TextStyle(color = getTextColor(this@MainActivity), fontSize = 28.sp)
                    )

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Bottom)
                            .padding(end = 12.dp),
                        text = "VERSION #${
                            packageManager.getPackageInfo(
                                "com.unicorns.invisible.caravan",
                                PackageManager.MATCH_ALL
                            ).versionName
                        }",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.monofont)),
                        textAlign = TextAlign.End,
                        style = TextStyle(
                            color = getTextColor(this@MainActivity),
                            background = getBackgroundColor(this@MainActivity),
                            fontSize = 14.sp
                        )
                    )
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
                                        color = getTextColor(this@MainActivity),
                                        background = getBackgroundColor(this@MainActivity),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily(Font(R.font.monofont)),
                                        textDecoration = TextDecoration.Underline
                                    )
                                )
                            ),
                        ) {
                            append(stringResource(R.string.menu_discord))
                        }
                    }
                    Box(Modifier.fillMaxSize()) {
                        Text(
                            text = discord,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 12.dp),
                            style = TextStyle(
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily(Font(R.font.monofont)),
                            )
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
                        knobColor = getKnobColor(this@MainActivity),
                        trackColor = getTrackColor(this@MainActivity),
                        horizontal = false
                    )
                    .padding(start = 16.dp),
                state = state
            ) {
                item {
                    Spacer(Modifier.height(64.dp))
                    Text(
                        text = getString(R.string.menu_pve),
                        modifier = Modifier
                            .clickable {
                                showPvE()
                            }
                            .background(getTextBackgroundColor(this@MainActivity))
                            .padding(8.dp),
                        style = TextStyle(
                            color = getTextColor(this@MainActivity),
                            fontSize = 20.sp,
                            fontFamily = FontFamily(Font(R.font.monofont)),
                        )
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = getString(R.string.menu_pvp),
                        modifier = Modifier
                            .clickable {
                                showPvP()
                            }
                            .background(getTextBackgroundColor(this@MainActivity))
                            .padding(8.dp),
                        style = TextStyle(
                            color = getTextColor(this@MainActivity),
                            fontSize = 20.sp,
                            fontFamily = FontFamily(Font(R.font.monofont)),
                        )
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = getString(R.string.menu_tutorial),
                        modifier = Modifier
                            .clickable {
                                showTutorial()
                            }
                            .background(getTextBackgroundColor(this@MainActivity))
                            .padding(8.dp),
                        style = TextStyle(
                            color = getTextColor(this@MainActivity),
                            fontSize = 20.sp,
                            fontFamily = FontFamily(Font(R.font.monofont)),
                        )
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = getString(R.string.menu_rules),
                        modifier = Modifier
                            .clickable {
                                showRules()
                            }
                            .background(getTextBackgroundColor(this@MainActivity))
                            .padding(8.dp),
                        style = TextStyle(
                            color = getTextColor(this@MainActivity),
                            fontSize = 20.sp,
                            fontFamily = FontFamily(Font(R.font.monofont)),
                        )
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = getString(R.string.menu_deck),
                        modifier = Modifier
                            .clickable {
                                showDeckSelection()
                            }
                            .background(getTextBackgroundColor(this@MainActivity))
                            .padding(8.dp),
                        style = TextStyle(
                            color = getTextColor(this@MainActivity),
                            fontSize = 20.sp,
                            fontFamily = FontFamily(Font(R.font.monofont)),
                        )
                    )
                    Spacer(modifier = Modifier.height(32.dp))
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
                            .fillMaxWidth(0.66f)
                            .fillMaxHeight()
                            .padding(start = 12.dp)
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
                            }
                            .align(Alignment.CenterVertically)
                    ) {

                        BoxWithConstraints(Modifier.fillMaxSize()) {
                            Text(
                                text = getString(R.string.menu_settings),
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .align(Alignment.TopEnd)
                                    .clickable {
                                        showSettings()
                                    }
                                    .background(getTextBackgroundColor(this@MainActivity))
                                    .padding(4.dp),
                                style = TextStyle(
                                    color = getTextColor(this@MainActivity),
                                    fontSize = 18.sp,
                                    fontFamily = FontFamily(Font(R.font.monofont)),
                                )
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
                                        moveTo(0f, size.height * 3 / 4)
                                        lineTo(size.width, size.height * 3 / 4)
                                        lineTo(size.width, 0f)
                                    },
                                    color = getDividerColor(this@MainActivity),
                                    style = Stroke(width = 8f),
                                )
                            },
                    ) {
                        BoxWithConstraints(Modifier.fillMaxSize()) {
                            Text(
                                text = getString(R.string.menu_about),
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .clickable {
                                        showAbout()
                                    }
                                    .padding(end = 8.dp)
                                    .background(getTextBackgroundColor(this@MainActivity))
                                    .padding(4.dp),
                                style = TextStyle(
                                    color = getTextColor(this@MainActivity),
                                    fontSize = 18.sp,
                                    fontFamily = FontFamily(Font(R.font.monofont)),
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val MIN_DECK_SIZE = 30
        const val MIN_NUM_OF_NUMBERS = 15
    }
}
