package com.unicorns.invisible.caravan

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
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
import com.unicorns.invisible.caravan.utils.scrollbar
import com.unicorns.invisible.caravan.utils.sendRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.chromium.net.CronetEngine



@Suppress("MoveLambdaOutsideParentheses")
class MainActivity : AppCompatActivity() {
    var save: Save? = null

    var goBack: (() -> Unit)? = null

    fun checkIfCustomDeckCanBeUsedInGame(playerCResources: CResources): Boolean {
        return playerCResources.deckSize >= MIN_DECK_SIZE && playerCResources.numOfNumbers >= MIN_NUM_OF_NUMBERS
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        save = loadSave(this) ?: run {
            save(this, Save())
            loadSave(this)!!
        }

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

            var showSettings by rememberSaveable { mutableStateOf(false) }

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
                        sendRequest("http://crvnserver.onrender.com/crvn/is_there_a_room/") {
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
                    confirmButton = { Text(text = "OK", modifier = Modifier.clickable { hideAlertDialog() }) },
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
                showSettings -> {
                    ShowSettings(activity = this, { pingServer }, { pingServer = it }) { showSettings = false }
                }
                else -> {
                    MainMenu(
                        { deckSelection = true },
                        { showAbout = true },
                        { showGameStats = true },
                        { showPvP = true },
                        { showTutorial = true },
                        { showRules = true },
                        { showSettings = true }
                    )
                }
            }

            key (pingServer) {
                Text(
                    text = when (pingServer) {
                        1 -> {
                            if (areThereRooms && !showPvP) {
                                stringResource(R.string.someone_is_in_the_room_alone)
                            } else if (showGameStats || showPvP) {
                                ""
                            } else {
                                stringResource(R.string.it_is_either_empty_or_busy)
                            }
                        }
                        0 -> {
                            if (showGameStats || showPvP || deckSelection) {
                                ""
                            } else {
                                stringResource(R.string.no_server_ping)
                            }
                        }
                        else -> {
                            if (showGameStats || showPvP || deckSelection) {
                                ""
                            } else {
                                stringResource(R.string.pinging)
                            }
                        }
                    },
                    style = TextStyle(
                        color = Color(getColor(if (pingServer == 1 && areThereRooms) R.color.red else R.color.colorPrimaryDark)),
                        background = Color(getColor(R.color.colorAccent)),
                        fontSize = 14.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.66f)
                )
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
        val state = rememberLazyListState()
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().scrollbar(state, horizontal = false),
            state = state
        ) {
            item {
                Spacer(Modifier.height(64.dp))
                Text(
                    text = getString(R.string.app_name) +
                            "\n(${packageManager.getPackageInfo(
                                "com.unicorns.invisible.caravan",
                                PackageManager.MATCH_ALL
                            ).versionName})",
                    fontWeight = FontWeight.ExtraBold,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    style = TextStyle(color = Color(getColor(R.color.colorPrimaryDark)), fontSize = 32.sp)
                )
                Spacer(Modifier.height(64.dp))
                Text(
                    text = getString(R.string.menu_pve),
                    modifier = Modifier.clickable {
                        showPvE()
                    },
                    style = TextStyle(color = Color(getColor(R.color.colorPrimaryDark)), fontSize = 20.sp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = getString(R.string.menu_pvp),
                    modifier = Modifier.clickable {
                        showPvP()
                    },
                    style = TextStyle(color = Color(getColor(R.color.colorPrimaryDark)), fontSize = 20.sp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = getString(R.string.menu_tutorial),
                    modifier = Modifier.clickable {
                        showTutorial()
                    },
                    style = TextStyle(color = Color(getColor(R.color.colorPrimaryDark)), fontSize = 20.sp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = getString(R.string.menu_deck),
                    modifier = Modifier.clickable {
                        showDeckSelection()
                    },
                    style = TextStyle(color = Color(getColor(R.color.colorPrimaryDark)), fontSize = 20.sp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = getString(R.string.menu_rules),
                    modifier = Modifier.clickable {
                        showRules()
                    },
                    style = TextStyle(color = Color(getColor(R.color.colorPrimaryDark)), fontSize = 20.sp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = getString(R.string.menu_settings),
                    modifier = Modifier.clickable {
                        showSettings()
                    },
                    style = TextStyle(color = Color(getColor(R.color.colorPrimaryDark)), fontSize = 20.sp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = getString(R.string.menu_about),
                    modifier = Modifier.clickable {
                        showAbout()
                    },
                    style = TextStyle(color = Color(getColor(R.color.colorPrimaryDark)), fontSize = 20.sp)
                )
                Spacer(modifier = Modifier.height(32.dp))

                val discord = buildAnnotatedString {
                    pushStringAnnotation(tag = "discord", annotation = "https://discord.gg/utCrjvDk")
                    withStyle(style = SpanStyle(color = Color(getColor(R.color.colorPrimaryDark)), textDecoration = TextDecoration.Underline)) {
                        append(stringResource(R.string.menu_discord))
                    }
                    pop()
                }
                val uriHandler = LocalUriHandler.current
                ClickableText(text = discord,
                    style = TextStyle(color = Color(getColor(R.color.colorPrimaryDark)), fontSize = 20.sp),
                ) { offset ->
                    discord.getStringAnnotations(tag = "discord", start = offset, end = offset).firstOrNull()?.let {
                        uriHandler.openUri(it.item)
                    }
                }
                Spacer(Modifier.height(64.dp))
            }
        }
    }

    companion object {
        const val MIN_DECK_SIZE = 30
        const val MIN_NUM_OF_NUMBERS = 15
    }
}