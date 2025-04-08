package com.unicorns.invisible.caravan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.better_rules
import caravan.composeapp.generated.resources.better_rules_body_1
import caravan.composeapp.generated.resources.better_rules_body_2
import caravan.composeapp.generated.resources.better_rules_body_3
import caravan.composeapp.generated.resources.better_rules_body_4
import caravan.composeapp.generated.resources.better_rules_body_5
import caravan.composeapp.generated.resources.better_rules_body_6
import caravan.composeapp.generated.resources.better_rules_body_7
import caravan.composeapp.generated.resources.better_rules_body_finale
import caravan.composeapp.generated.resources.faq
import caravan.composeapp.generated.resources.faq_a1
import caravan.composeapp.generated.resources.faq_a2
import caravan.composeapp.generated.resources.faq_a3
import caravan.composeapp.generated.resources.faq_a4
import caravan.composeapp.generated.resources.faq_a5
import caravan.composeapp.generated.resources.faq_al
import caravan.composeapp.generated.resources.faq_q1
import caravan.composeapp.generated.resources.faq_q2
import caravan.composeapp.generated.resources.faq_q3
import caravan.composeapp.generated.resources.faq_q4
import caravan.composeapp.generated.resources.faq_q5
import caravan.composeapp.generated.resources.faq_ql
import caravan.composeapp.generated.resources.menu_rules
import caravan.composeapp.generated.resources.og_rules
import caravan.composeapp.generated.resources.rules
import com.unicorns.invisible.caravan.utils.MenuItemOpen
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getDividerColor
import com.unicorns.invisible.caravan.utils.getSelectionColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.playSelectSound
import org.jetbrains.compose.resources.stringResource


// TODO: check everything, update FAQ from discord!!!!
@Composable
fun ShowRules(goBack: () -> Unit) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val rules = stringResource(Res.string.rules)
    MenuItemOpen(stringResource(Res.string.menu_rules), "<-", Alignment.TopCenter, goBack) {
        Spacer(Modifier.height(8.dp))
        Column(Modifier, verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
            TabRow(
                selectedTab, Modifier.fillMaxWidth(),
                containerColor = getBackgroundColor(),
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = getSelectionColor()
                        )
                    }
                },
                divider = {
                    HorizontalDivider(color = getDividerColor())
                }
            ) {
                Tab(
                    selectedTab == 0, { playSelectSound(); selectedTab = 0 },
                    selectedContentColor = getSelectionColor(),
                    unselectedContentColor = getTextBackgroundColor()
                ) {
                    TextFallout(
                        stringResource(Res.string.better_rules),
                        getTextColor(),
                        getTextStrokeColor(),
                        16.sp,
                        Modifier.padding(4.dp),
                    )
                }
                Tab(
                    selectedTab == 1, { playSelectSound(); selectedTab = 1 },
                    selectedContentColor = getSelectionColor(),
                    unselectedContentColor = getTextBackgroundColor()
                ) {
                    TextFallout(
                        stringResource(Res.string.og_rules),
                        getTextColor(),
                        getTextStrokeColor(),
                        16.sp,
                        Modifier.padding(4.dp),
                    )
                }
                Tab(
                    selectedTab == 2, { playSelectSound(); selectedTab = 2 },
                    selectedContentColor = getSelectionColor(),
                    unselectedContentColor = getTextBackgroundColor()
                ) {
                    TextFallout(
                        stringResource(Res.string.faq),
                        getTextColor(),
                        getTextStrokeColor(),
                        16.sp,
                        Modifier.padding(4.dp),
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Spacer(Modifier.height(16.dp))
            when (selectedTab) {
                0 -> {
                    Column {
                        @Composable
                        fun ShowRulesSection(s: String) {
                            Spacer(modifier = Modifier.height(16.dp))
                            TextFallout(
                                s,
                                getTextColor(),
                                getTextStrokeColor(),
                                20.sp,
                                Modifier.padding(horizontal = 8.dp),
                                textAlignment = TextAlign.Start
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        ShowRulesSection(stringResource(Res.string.better_rules_body_1))
                        ShowRulesSection(stringResource(Res.string.better_rules_body_2))
                        ShowRulesSection(stringResource(Res.string.better_rules_body_3))
                        ShowRulesSection(stringResource(Res.string.better_rules_body_4))
                        ShowRulesSection(stringResource(Res.string.better_rules_body_5))
                        ShowRulesSection(stringResource(Res.string.better_rules_body_6))
                        ShowRulesSection(stringResource(Res.string.better_rules_body_7))
                        HorizontalDivider(color = getDividerColor())
                        ShowRulesSection(stringResource(Res.string.better_rules_body_finale))
                    }
                }
                1 -> {
                    TextFallout(
                        rules,
                        getTextColor(),
                        getTextStrokeColor(),
                        20.sp,
                        Modifier.padding(horizontal = 8.dp),
                        textAlignment = TextAlign.Start
                    )
                }
                2 -> {
                    @Composable
                    fun showQA(line: String) {
                        TextFallout(
                            line,
                            getTextColor(),
                            getTextStrokeColor(),
                            18.sp,
                            Modifier.padding(horizontal = 8.dp),
                            textAlignment = TextAlign.Start
                        )
                    }
                    Spacer(Modifier.height(20.dp))
                    showQA(stringResource(Res.string.faq_q1))
                    Spacer(Modifier.height(8.dp))
                    showQA(stringResource(Res.string.faq_a1))
                    Spacer(Modifier.height(20.dp))
                    showQA(stringResource(Res.string.faq_q2))
                    Spacer(Modifier.height(8.dp))
                    showQA(stringResource(Res.string.faq_a2))
                    Spacer(Modifier.height(20.dp))
                    showQA(stringResource(Res.string.faq_q3))
                    Spacer(Modifier.height(8.dp))
                    showQA(stringResource(Res.string.faq_a3))
                    Spacer(Modifier.height(20.dp))
                    showQA(stringResource(Res.string.faq_q4))
                    Spacer(Modifier.height(8.dp))
                    showQA(stringResource(Res.string.faq_a4))
                    Spacer(Modifier.height(20.dp))
                    showQA(stringResource(Res.string.faq_q5))
                    Spacer(Modifier.height(8.dp))
                    showQA(stringResource(Res.string.faq_a5))
                    Spacer(Modifier.height(20.dp))
                    showQA(stringResource(Res.string.faq_ql))
                    Spacer(Modifier.height(8.dp))
                    showQA(stringResource(Res.string.faq_al))
                    Spacer(Modifier.height(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}