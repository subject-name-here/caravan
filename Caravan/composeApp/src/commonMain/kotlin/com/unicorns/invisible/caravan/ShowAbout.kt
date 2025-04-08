package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.about_1
import caravan.composeapp.generated.resources.about_2
import caravan.composeapp.generated.resources.about_3_0
import caravan.composeapp.generated.resources.about_3_1
import caravan.composeapp.generated.resources.about_3_2
import caravan.composeapp.generated.resources.about_3_4_1
import caravan.composeapp.generated.resources.about_3_4_2
import caravan.composeapp.generated.resources.about_3_4_3
import caravan.composeapp.generated.resources.about_3_5_1
import caravan.composeapp.generated.resources.about_3_5_2
import caravan.composeapp.generated.resources.about_3_5_3
import caravan.composeapp.generated.resources.about_3_6
import caravan.composeapp.generated.resources.about_3_7_1
import caravan.composeapp.generated.resources.about_3_7_2
import caravan.composeapp.generated.resources.about_3_7_3
import caravan.composeapp.generated.resources.about_4_1
import caravan.composeapp.generated.resources.about_4_2
import caravan.composeapp.generated.resources.menu_about
import caravan.composeapp.generated.resources.monofont
import com.unicorns.invisible.caravan.utils.MenuItemOpen
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.stringResource


@Composable
fun ShowAbout(goBack: () -> Unit) {
    MenuItemOpen(stringResource(Res.string.menu_about), "<-", Alignment.Center, goBack) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(getBackgroundColor())
                .padding(horizontal = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            TextFallout(
                stringResource(Res.string.about_1),
                getTextColor(),
                getTextStrokeColor(),
                20.sp,
                Modifier,
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextFallout(
                stringResource(Res.string.about_2),
                getTextColor(),
                getTextStrokeColor(),
                20.sp,
                Modifier,
            )
            Spacer(modifier = Modifier.height(16.dp))

            val annotatedString = buildAnnotatedString {
                append(stringResource(Res.string.about_4_1))
                withLink(
                    link = LinkAnnotation.Url(
                        url = "mailto:unicornsinvisible@gmail.com",
                        styles = TextLinkStyles(
                            style = SpanStyle(
                                color = getTextColor(),
                                fontFamily = FontFamily(Font(Res.font.monofont)),
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    ),
                ) {
                    append("unicornsinvisible@gmail.com")
                }
                append(stringResource(Res.string.about_4_2))
            }
            TextFallout(
                annotatedString,
                getTextColor(),
                getTextStrokeColor(),
                20.sp,
                Modifier,
            )
            Spacer(modifier = Modifier.height(24.dp))
            TextFallout(
                stringResource(Res.string.about_3_0),
                getTextColor(),
                getTextStrokeColor(),
                20.sp,
                Modifier,
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextFallout(
                stringResource(Res.string.about_3_1),
                getTextColor(),
                getTextStrokeColor(),
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
                                color = getTextColor(),
                                fontFamily = FontFamily(Font(Res.font.monofont)),
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    ),
                ) {
                    append("Sobek")
                }
                append(stringResource(Res.string.about_3_2))
            }
            TextFallout(
                annotatedString0,
                getTextColor(),
                getTextStrokeColor(),
                20.sp,
                Modifier,
            )
            Spacer(modifier = Modifier.height(12.dp))
            val annotatedString2 = buildAnnotatedString {
                append(stringResource(Res.string.about_3_4_1))
                withLink(
                    link = LinkAnnotation.Url(
                        url = "https://www.reddit.com/r/cardgames/comments/97c7g2/caravan_card_game_in_reallife_detailed_rules/",
                        styles = TextLinkStyles(
                            style = SpanStyle(
                                color = getTextColor(),
                                fontFamily = FontFamily(Font(Res.font.monofont)),
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    ),
                ) {
                    append(stringResource(Res.string.about_3_4_2))
                }
                append(stringResource(Res.string.about_3_4_3))
            }
            TextFallout(
                annotatedString2,
                getTextColor(),
                getTextStrokeColor(),
                20.sp,
                Modifier,
            )
            Spacer(modifier = Modifier.height(12.dp))

            val annotatedStringCFF = buildAnnotatedString {
                append(stringResource(Res.string.about_3_5_1))
                withLink(
                    link = LinkAnnotation.Url(
                        url = "https://github.com/Sasabmeg/fallout-classic-dialog-font",
                        styles = TextLinkStyles(
                            style = SpanStyle(
                                color = getTextColor(),
                                fontFamily = FontFamily(Font(Res.font.monofont)),
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    ),
                ) {
                    append(stringResource(Res.string.about_3_5_2))
                }
                append(stringResource(Res.string.about_3_5_3))
            }
            TextFallout(
                annotatedStringCFF,
                getTextColor(),
                getTextStrokeColor(),
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
                                color = getTextColor(),
                                fontFamily = FontFamily(Font(Res.font.monofont)),
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    ),
                ) {
                    append("Ithracael")
                }
                append(stringResource(Res.string.about_3_6))
            }
            TextFallout(
                annotatedString3,
                getTextColor(),
                getTextStrokeColor(),
                20.sp,
                Modifier,
            )
            Spacer(modifier = Modifier.height(12.dp))

            val annotatedStringOthers = buildAnnotatedString {
                append(stringResource(Res.string.about_3_7_1))
                withLink(
                    link = LinkAnnotation.Url(
                        url = "https://discord.gg/xSTJpjvzJV",
                        styles = TextLinkStyles(
                            style = SpanStyle(
                                color = getTextColor(),
                                fontFamily = FontFamily(Font(Res.font.monofont)),
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    ),
                ) {
                    append(stringResource(Res.string.about_3_7_2))
                }
                append(stringResource(Res.string.about_3_7_3))
            }
            TextFallout(
                annotatedStringOthers,
                getTextColor(),
                getTextStrokeColor(),
                20.sp,
                Modifier,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}