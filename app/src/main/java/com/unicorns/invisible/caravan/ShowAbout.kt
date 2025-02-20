package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicorns.invisible.caravan.utils.MenuItemOpen
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.scrollbar


@Composable
fun ShowAbout(activity: MainActivity, goBack: () -> Unit) {
    LaunchedEffect(Unit) {
        activity.achievementsClient?.unlock(activity.getString(R.string.achievement_test_achievement))
    }

    MenuItemOpen(activity, stringResource(R.string.menu_about), "<-", goBack) {
        val state = rememberLazyListState()
        LazyColumn(
            state = state,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scrollbar(
                    state,
                    horizontal = false,
                    knobColor = getKnobColor(activity),
                    trackColor = getTrackColor(activity),
                )
                .fillMaxSize()
                .background(getBackgroundColor(activity))
                .padding(horizontal = 8.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                TextFallout(
                    stringResource(R.string.about_1),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    20.sp,
                    Modifier,
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextFallout(
                    stringResource(R.string.about_2),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    20.sp,
                    Modifier,
                )
                Spacer(modifier = Modifier.height(16.dp))

                val annotatedString = buildAnnotatedString {
                    append(stringResource(R.string.about_4_1))
                    withLink(
                        link = LinkAnnotation.Url(
                            url = "mailto:unicornsinvisible@gmail.com",
                            styles = TextLinkStyles(
                                style = SpanStyle(
                                    color = getTextColor(activity),
                                    fontFamily = FontFamily(Font(R.font.monofont)),
                                    textDecoration = TextDecoration.Underline
                                )
                            )
                        ),
                    ) {
                        append("unicornsinvisible@gmail.com")
                    }
                    append(stringResource(R.string.about_4_2))
                }
                TextFallout(
                    annotatedString,
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    20.sp,
                    Modifier,
                )
                Spacer(modifier = Modifier.height(24.dp))
                TextFallout(
                    stringResource(R.string.about_3_0),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    20.sp,
                    Modifier,
                )
                Spacer(modifier = Modifier.height(12.dp))
                TextFallout(
                    stringResource(R.string.about_3_1),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    20.sp,
                    Modifier,
                )
                Spacer(modifier = Modifier.height(12.dp))

                val annotatedString0 = buildAnnotatedString {
                    withLink(
                        link = LinkAnnotation.Url(
                            url = "https://www.youtube.com/@churchofmadness",
                            styles = TextLinkStyles(
                                style = SpanStyle(
                                    color = getTextColor(activity),
                                    fontFamily = FontFamily(Font(R.font.monofont)),
                                    textDecoration = TextDecoration.Underline
                                )
                            )
                        ),
                    ) {
                        append("Sobek")
                    }
                    append(stringResource(R.string.about_3_2))
                }
                TextFallout(
                    annotatedString0,
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    20.sp,
                    Modifier,
                )
                Spacer(modifier = Modifier.height(12.dp))
                val annotatedString2 = buildAnnotatedString {
                    append(stringResource(R.string.about_3_4_1))
                    withLink(
                        link = LinkAnnotation.Url(
                            url = "https://www.reddit.com/r/cardgames/comments/97c7g2/caravan_card_game_in_reallife_detailed_rules/",
                            styles = TextLinkStyles(
                                style = SpanStyle(
                                    color = getTextColor(activity),
                                    fontFamily = FontFamily(Font(R.font.monofont)),
                                    textDecoration = TextDecoration.Underline
                                )
                            )
                        ),
                    ) {
                        append(stringResource(R.string.about_3_4_2))
                    }
                    append(stringResource(R.string.about_3_4_3))
                }
                TextFallout(
                    annotatedString2,
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    20.sp,
                    Modifier,
                )
                Spacer(modifier = Modifier.height(12.dp))

                val annotatedStringCFF = buildAnnotatedString {
                    append(stringResource(R.string.about_3_5_1))
                    withLink(
                        link = LinkAnnotation.Url(
                            url = "https://github.com/Sasabmeg/fallout-classic-dialog-font",
                            styles = TextLinkStyles(
                                style = SpanStyle(
                                    color = getTextColor(activity),
                                    fontFamily = FontFamily(Font(R.font.monofont)),
                                    textDecoration = TextDecoration.Underline
                                )
                            )
                        ),
                    ) {
                        append(stringResource(R.string.about_3_5_2))
                    }
                    append(stringResource(R.string.about_3_5_3))
                }
                TextFallout(
                    annotatedStringCFF,
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    20.sp,
                    Modifier,
                )
                Spacer(modifier = Modifier.height(12.dp))
                val annotatedString3 = buildAnnotatedString {
                    withLink(
                        link = LinkAnnotation.Url(
                            url = "https://www.youtube.com/@ithracael",
                            styles = TextLinkStyles(
                                style = SpanStyle(
                                    color = getTextColor(activity),
                                    fontFamily = FontFamily(Font(R.font.monofont)),
                                    textDecoration = TextDecoration.Underline
                                )
                            )
                        ),
                    ) {
                        append("Ithracael")
                    }
                    append(stringResource(R.string.about_3_6))
                }
                TextFallout(
                    annotatedString3,
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    20.sp,
                    Modifier,
                )
                Spacer(modifier = Modifier.height(12.dp))

                val annotatedStringOthers = buildAnnotatedString {
                    append(stringResource(R.string.about_3_7_1))
                    withLink(
                        link = LinkAnnotation.Url(
                            url = "https://discord.gg/xSTJpjvzJV",
                            styles = TextLinkStyles(
                                style = SpanStyle(
                                    color = getTextColor(activity),
                                    fontFamily = FontFamily(Font(R.font.monofont)),
                                    textDecoration = TextDecoration.Underline
                                )
                            )
                        ),
                    ) {
                        append(stringResource(R.string.about_3_7_2))
                    }
                    append(stringResource(R.string.about_3_7_3))
                }
                TextFallout(
                    annotatedStringOthers,
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    20.sp,
                    Modifier,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}