package com.unicorns.invisible.caravan

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.save.Save
import com.unicorns.invisible.caravan.save.loadSave
import com.unicorns.invisible.caravan.save.save
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getDividerColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.scrollbar
import com.unicorns.invisible.caravan.utils.sendRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id = UUID.randomUUID().toString()
        save = loadSave(this) ?: run {
            save(this, Save())
            loadSave(this)!!
        }
        styleId = save?.styleId ?: 1

        if (cronetEngine == null) {
            val myBuilder = CronetEngine.Builder(this)
            cronetEngine = myBuilder.build()
        }

        setContent {
            var deckSelection by rememberSaveable { mutableStateOf(false) }
            var showPvP by rememberSaveable { mutableStateOf(false) }
            var showAbout by rememberSaveable { mutableStateOf(false) }
            var showGameStats by rememberSaveable { mutableStateOf(false) }
            var showTutorial by rememberSaveable { mutableStateOf(false) }
            var showRules by rememberSaveable { mutableStateOf(false) }

            var showSettings by rememberSaveable { mutableIntStateOf(0) }

            var selectedDeck by rememberSaveable { mutableStateOf(save?.selectedDeck ?: CardBack.STANDARD) }

            var showAlertDialog by remember { mutableStateOf(false) }
            var alertDialogHeader by remember { mutableStateOf("") }
            var alertDialogMessage by remember { mutableStateOf("") }

            fun showAlertDialog(header: String, message: String) {
                showAlertDialog = true
                alertDialogHeader = header
                alertDialogMessage = message
            }
            fun hideAlertDialog() {
                showAlertDialog = false
            }

            var pingServer by rememberSaveable { mutableIntStateOf(0) }
            var areThereRooms by rememberSaveable { mutableStateOf(false) }
            val effectKey by rememberSaveable { mutableStateOf(true) }
            LaunchedEffect(effectKey) {
                while (isActive) {
                    if (pingServer != 0) {
                        pingServer = 2
                        sendRequest("$crvnUrl/crvn/is_there_a_room/") {
                            val res = it.getString("body").toIntOrNull()
                            areThereRooms = res != null && res != 0
                            if (pingServer == 2) {
                                pingServer = 1
                            }
                        }

                        delay(9500L)
                        pingServer = 2
                        delay(9500L)
                    }
                    delay(9500L)
                }
            }

            if (showAlertDialog) {
                AlertDialog(
                    onDismissRequest = { hideAlertDialog() },
                    confirmButton = { Text(text = stringResource(R.string.close), modifier = Modifier.clickable { hideAlertDialog() }) },
                    dismissButton = { if (goBack != null) {
                        Text(
                            text = stringResource(R.string.back_to_menu),
                            modifier = Modifier.clickable { hideAlertDialog(); goBack?.invoke(); goBack = null }
                        )
                    } },
                    title = { Text(text = alertDialogHeader) },
                    text = { Text(text = alertDialogMessage) },
                )
            }

            when {
                showRules -> {
                    ShowRules(activity = this) { showRules = false }
                }
                showTutorial -> {
                    Tutorial(activity = this) { showTutorial = false }
                }
                deckSelection -> {
                    DeckSelection(
                        this,
                        { selectedDeck },
                        { selectedDeck = it }
                    ) { deckSelection = false }
                }
                showAbout -> {
                    ShowAbout(activity = this) { showAbout = false }
                }
                showGameStats -> {
                    ShowPvE(
                        activity = this,
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
                            activity = this,
                            selectedDeck = { selectedDeck },
                            ::showAlertDialog
                        ) { showPvP = false }
                    }
                }
                showSettings > 0 -> {
                    ShowSettings(activity = this, { styleId }, {
                        styleId = 1 - styleId
                        showSettings = 3 - showSettings
                        save?.let {
                            it.styleId = styleId
                            save(this, it)
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
                        pushStringAnnotation(tag = "discord", annotation = "https://discord.gg/xSTJpjvzJV")
                        withStyle(style = SpanStyle(
                            color = getTextColor(this@MainActivity),
                            background = getBackgroundColor(this@MainActivity),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.monofont)),
                            textDecoration = TextDecoration.Underline
                        )) {
                            append(stringResource(R.string.menu_discord))
                        }
                        pop()
                    }
                    val uriHandler = LocalUriHandler.current
                    Box(Modifier.fillMaxSize()) {
                        ClickableText(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 12.dp),
                            text = discord,
                            style = TextStyle(
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily(Font(R.font.monofont)),
                            ),
                        ) { offset ->
                            discord.getStringAnnotations(tag = "discord", start = offset, end = offset).firstOrNull()?.let {
                                uriHandler.openUri(it.item)
                            }
                        }
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

            Row(
                Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = getString(R.string.menu_settings),
                    modifier = Modifier
                        .clickable {
                            showSettings()
                        }
                        .background(getTextBackgroundColor(this@MainActivity))
                        .padding(8.dp),
                    style = TextStyle(
                        color = getTextColor(this@MainActivity),
                        fontSize = 24.sp,
                        fontFamily = FontFamily(Font(R.font.monofont)),
                    )
                )

                Text(
                    text = getString(R.string.menu_about),
                    modifier = Modifier
                        .clickable {
                            showAbout()
                        }
                        .background(getTextBackgroundColor(this@MainActivity))
                        .padding(8.dp),
                    style = TextStyle(
                        color = getTextColor(this@MainActivity),
                        fontSize = 24.sp,
                        fontFamily = FontFamily(Font(R.font.monofont)),
                    )
                )
            }
        }
    }

    companion object {
        const val MIN_DECK_SIZE = 30
        const val MIN_NUM_OF_NUMBERS = 15
    }
}
