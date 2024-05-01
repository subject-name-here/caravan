package com.unicorns.invisible.caravan

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.save.save


@Composable
fun ShowAbout(activity: MainActivity, goBack: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.about_1),
            style = TextStyle(color = Color(activity.getColor(R.color.colorPrimaryDark)), fontSize = 20.sp, textAlign = TextAlign.Center)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.about_2),
            style = TextStyle(color = Color(activity.getColor(R.color.colorPrimary)), fontSize = 20.sp, textAlign = TextAlign.Center)
        )
        Spacer(modifier = Modifier.height(16.dp))

        val annotatedString = buildAnnotatedString {
            append(stringResource(R.string.about_3_1))
            pushStringAnnotation(tag = "e-mail", annotation = "mailto:unicornsinvisible@gmail.com")
            withStyle(style = SpanStyle(color = Color(activity.getColor(R.color.colorAccent)), textDecoration = TextDecoration.Underline)) {
                append("unicornsinvisible@gmail.com")
            }
            pop()
            append(stringResource(R.string.about_3_2))
        }
        val uriHandler = LocalUriHandler.current
        ClickableText(text = annotatedString,
            style = TextStyle(color = Color(activity.getColor(R.color.colorPrimaryDark)), fontSize = 20.sp, textAlign = TextAlign.Center),
        ) { offset ->
            annotatedString.getStringAnnotations(tag = "e-mail", start = offset, end = offset).firstOrNull()?.let {
                uriHandler.openUri(it.item)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        val annotatedString2 = buildAnnotatedString {
            append(stringResource(R.string.about_4_1))
            pushStringAnnotation(tag = "reddit", annotation = "https://www.reddit.com/r/cardgames/comments/97c7g2/caravan_card_game_in_reallife_detailed_rules/")
            withStyle(style = SpanStyle(color = Color(activity.getColor(R.color.colorAccent)), textDecoration = TextDecoration.Underline)) {
                append(stringResource(R.string.about_4_2))
            }
            pop()
            append(stringResource(R.string.about_4_3))
        }
        ClickableText(text = annotatedString2,
            style = TextStyle(color = Color(activity.getColor(R.color.colorPrimary)), fontSize = 20.sp, textAlign = TextAlign.Center),
        ) { offset ->
            annotatedString2.getStringAnnotations(tag = "reddit", start = offset, end = offset).firstOrNull()?.let {
                uriHandler.openUri(it.item)
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = stringResource(R.string.menu_back),
            modifier = Modifier.clickable {
                goBack()
            },
            style = TextStyle(color = Color(activity.getColor(R.color.colorPrimaryDark)), fontSize = 24.sp)
        )
    }
}