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
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableCancel
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.scrollbar


@Composable
fun ShowAbout(activity: MainActivity, goBack: () -> Unit) {
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
                padding = 4.dp
            )
            .fillMaxSize()
            .background(getBackgroundColor(activity))
            .padding(16.dp)
    ) {
        item {
            TextFallout(
                stringResource(R.string.about_1),
                getTextColor(activity),
                getTextStrokeColor(activity),
                20.sp,
                Alignment.Center,
                Modifier,
                TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextFallout(
                stringResource(R.string.about_2),
                getTextColor(activity),
                getTextStrokeColor(activity),
                20.sp,
                Alignment.Center,
                Modifier,
                TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            val annotatedString = buildAnnotatedString {
                append(stringResource(R.string.about_3_1))
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
                append(stringResource(R.string.about_3_2))
            }

            TextFallout(
                annotatedString,
                getTextColor(activity),
                getTextStrokeColor(activity),
                20.sp,
                Alignment.Center,
                Modifier,
                TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            val annotatedString2 = buildAnnotatedString {
                append(stringResource(R.string.about_4_1))
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
                    append(stringResource(R.string.about_4_2))
                }
                append(stringResource(R.string.about_4_3))
            }
            TextFallout(
                annotatedString2,
                getTextColor(activity),
                getTextStrokeColor(activity),
                20.sp,
                Alignment.Center,
                Modifier,
                TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))
            TextFallout(
                stringResource(R.string.about_5),
                getTextColor(activity),
                getTextStrokeColor(activity),
                20.sp,
                Alignment.Center,
                Modifier,
                TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))
            TextFallout(
                stringResource(R.string.about_6),
                getTextColor(activity),
                getTextStrokeColor(activity),
                20.sp,
                Alignment.Center,
                Modifier,
                TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))
            val annotatedString3 = buildAnnotatedString {
                append(stringResource(R.string.about_8))
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
                append(", ")
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
            }
            TextFallout(
                annotatedString3,
                getTextColor(activity),
                getTextStrokeColor(activity),
                20.sp,
                Alignment.Center,
                Modifier,
                TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))
            TextFallout(
                stringResource(R.string.about_7),
                getTextColor(activity),
                getTextStrokeColor(activity),
                20.sp,
                Alignment.Center,
                Modifier,
                TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))
            TextFallout(
                stringResource(R.string.menu_back),
                getTextColor(activity),
                getTextStrokeColor(activity),
                24.sp,
                Alignment.Center,
                Modifier
                    .background(getTextBackgroundColor(activity))
                    .clickableCancel(activity) {
                        goBack()
                    }
                    .padding(8.dp),
                TextAlign.Center
            )
        }
    }
}