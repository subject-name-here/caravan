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
    activity.save?.let {
        it.availableDecks[CardBack.LUCKY_38] = true
        save(activity, it)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize().padding(horizontal = 8.dp)
    ) {
        Text(
            text = "Caravan is the card game from video game \"Fallout: New Vegas\", created by Obsidian and Bethesda.",
            style = TextStyle(color = Color(activity.getColor(R.color.colorPrimaryDark)), fontSize = 20.sp, textAlign = TextAlign.Center)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "This port is created by subject_name_here, who is in no way affiliated with the creators of the original game.",
            style = TextStyle(color = Color(activity.getColor(R.color.colorPrimary)), fontSize = 20.sp, textAlign = TextAlign.Center)
        )
        Spacer(modifier = Modifier.height(16.dp))

        val annotatedString = buildAnnotatedString {
            append("If you want, contact me at ")
            pushStringAnnotation(tag = "e-mail", annotation = "mailto:unicornsinvisible@gmail.com")
            withStyle(style = SpanStyle(color = Color(activity.getColor(R.color.colorAccent)), textDecoration = TextDecoration.Underline)) {
                append("unicornsinvisible@gmail.com")
            }
            pop()
            append(". Alternatively, you can leave a review in Play Store.")
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
        Text(
            text = "If you want some features or have encountered a bug, please, let me know.",
            style = TextStyle(color = Color(activity.getColor(R.color.colorPrimary)), fontSize = 20.sp, textAlign = TextAlign.Center)
        )

        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "Back to Menu",
            modifier = Modifier.clickable {
                goBack()
            },
            style = TextStyle(color = Color(activity.getColor(R.color.colorPrimaryDark)), fontSize = 16.sp)
        )
    }
}