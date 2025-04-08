package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.custom_deck_1
import caravan.composeapp.generated.resources.custom_deck_2
import caravan.composeapp.generated.resources.custom_deck_3
import caravan.composeapp.generated.resources.custom_deck_4
import caravan.composeapp.generated.resources.custom_deck_non_faces
import caravan.composeapp.generated.resources.custom_deck_num_of_decks
import caravan.composeapp.generated.resources.custom_deck_size
import caravan.composeapp.generated.resources.deck_custom
import caravan.composeapp.generated.resources.deselect_all
import caravan.composeapp.generated.resources.empty_string
import caravan.composeapp.generated.resources.select_all
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CardNumber
import com.unicorns.invisible.caravan.model.primitives.CardWithPrice
import com.unicorns.invisible.caravan.model.primitives.CollectibleDeck
import com.unicorns.invisible.caravan.model.primitives.RankNumber
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.MenuItemOpenNoScroll
import com.unicorns.invisible.caravan.utils.ShowCard
import com.unicorns.invisible.caravan.utils.ShowCardBack
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableSelect
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getDividerColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getSelectionColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.playCloseSound
import com.unicorns.invisible.caravan.utils.playSelectSound
import com.unicorns.invisible.caravan.utils.pxToDp
import com.unicorns.invisible.caravan.utils.spToPx
import io.github.oikvpqya.compose.fastscroller.HorizontalScrollbar
import io.github.oikvpqya.compose.fastscroller.ScrollbarStyle
import io.github.oikvpqya.compose.fastscroller.ThumbStyle
import io.github.oikvpqya.compose.fastscroller.TrackStyle
import io.github.oikvpqya.compose.fastscroller.rememberScrollbarAdapter
import org.jetbrains.compose.resources.stringResource


@Composable
fun SetCustomDeck(
    goBack: () -> Unit,
) {
    fun isInCustomDeck(card: CardWithPrice) = card in save.getCurrentCustomDeck()
    fun isAvailable(card: CardWithPrice) = save.isCardAvailableAlready(card)

    fun toggleToCustomDeck(card: CardWithPrice) {
        save.getCurrentCustomDeck().let { deck ->
            if (card in deck) {
                deck.remove(card)
            } else {
                deck.add(card)
            }
        }
        saveData()
    }

    var selectedDeck by rememberSaveable { mutableIntStateOf(save.activeCustomDeck) }
    var updateCharacteristics by remember { mutableStateOf(false) }
    var updateAll by remember { mutableStateOf(false) }

    fun getSelectedDeckIndex() = selectedDeck - 1
    fun selectDeck(deck: Int) {
        selectedDeck = deck
        save.activeCustomDeck = deck
        updateCharacteristics = !updateCharacteristics
        updateAll = !updateAll
        saveData()
    }

    MenuItemOpenNoScroll(stringResource(Res.string.deck_custom), "<-", goBack) {
        Column(Modifier
            .fillMaxSize()
            .background(getBackgroundColor())
        ) {
            TabRow(
                getSelectedDeckIndex(), Modifier.fillMaxWidth(),
                containerColor = getBackgroundColor(),
                indicator = { tabPositions ->
                    if (getSelectedDeckIndex() < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[getSelectedDeckIndex()]),
                            color = getSelectionColor()
                        )
                    }
                },
                divider = {
                    HorizontalDivider(color = getDividerColor())
                }
            ) {
                @Composable
                fun CustomDeckTab(d: Int) {
                    Tab(
                        selectedDeck == d, { playSelectSound(); selectDeck(d) },
                        selectedContentColor = getSelectionColor(),
                        unselectedContentColor = getTextBackgroundColor()
                    ) {
                        TextFallout(
                            stringResource(when (d) {
                                1 -> Res.string.custom_deck_1
                                2 -> Res.string.custom_deck_2
                                3 -> Res.string.custom_deck_3
                                4 -> Res.string.custom_deck_4
                                else -> Res.string.empty_string
                            }),
                            getTextColor(),
                            getTextStrokeColor(),
                            16.sp,
                            Modifier.padding(4.dp),
                        )
                    }
                }
                CustomDeckTab(1)
                CustomDeckTab(2)
                CustomDeckTab(3)
                CustomDeckTab(4)
            }

            key (updateCharacteristics) {
                ShowCharacteristics()
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = getDividerColor())
            Spacer(modifier = Modifier.height(12.dp))

            BoxWithConstraints(Modifier.fillMaxSize().padding(4.dp)) {
                val state = rememberScrollState()
                val horizontalState = rememberLazyListState()
                HorizontalScrollbar(
                    modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth().padding(horizontal = 4.dp),
                    adapter = rememberScrollbarAdapter(state),
                    style = ScrollbarStyle(
                        minimalHeight = 0.dp,
                        thickness = 4.dp,
                        hoverDurationMillis = 0,
                        thumbStyle = ThumbStyle(
                            shape = RoundedCornerShape(100),
                            unhoverColor = getKnobColor(),
                            hoverColor = getKnobColor()
                        ),
                        trackStyle = TrackStyle(
                            shape = RoundedCornerShape(100),
                            unhoverColor = getTrackColor(),
                            hoverColor = getTrackColor()
                        )
                    )
                )

                LazyRow(
                    Modifier.height(maxHeight).width(maxWidth).padding(top = 4.dp),
                    state = horizontalState,
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.Top
                ) {
                    items(CardBack.entries) { back ->
                        var updateInfo by remember { mutableStateOf(false) }
                        var updateCards by remember { mutableStateOf(false) }
                        val verticalState = rememberLazyListState()
                        key(updateAll, updateInfo) {
                            @Composable
                            fun Button(text: String, action: (CardWithPrice) -> Unit) {
                                TextFallout(
                                    text,
                                    getTextColor(),
                                    getTextStrokeColor(),
                                    18.sp,
                                    Modifier
                                        .fillMaxWidth()
                                        .background(getTextBackgroundColor())
                                        .clickableSelect {
                                            CollectibleDeck(back).toList()
                                                .filter { isAvailable(it) }
                                                .forEach(action)
                                            updateCharacteristics = !updateCharacteristics
                                            updateCards = !updateCards
                                        }
                                        .padding(vertical = 4.dp),
                                    boxAlignment = Alignment.Center
                                )
                            }
                            Column(Modifier.padding(horizontal = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                val name = stringResource(back.nameIdWithBackFileName.first)
                                Box(Modifier.height((14.sp.spToPx() * 4).toInt().pxToDp()), contentAlignment = Alignment.Center) {
                                    TextFallout(
                                        name.replace(" (", "\n("),
                                        getTextColor(),
                                        getTextStrokeColor(),
                                        14.sp,
                                        Modifier.fillMaxWidth(),
                                        boxAlignment = Alignment.Center
                                    )
                                }
                                ShowCardBack(
                                    CardNumber(RankNumber.ACE, Suit.CLUBS, back),
                                    Modifier.align(Alignment.CenterHorizontally),
                                )

                                Button(stringResource(Res.string.select_all)) {
                                    if (!isInCustomDeck(it)) {
                                        toggleToCustomDeck(it)
                                    }
                                }
                                HorizontalDivider(color = getDividerColor())
                                Button(stringResource(Res.string.deselect_all)) {
                                    if (isInCustomDeck(it)) {
                                        toggleToCustomDeck(it)
                                    }
                                }

                                key(updateAll, updateCards) {
                                    CardsColumn(
                                        back,
                                        verticalState,
                                        {
                                            updateCharacteristics = !updateCharacteristics
                                            updateCards = !updateCards
                                        },
                                        ::isInCustomDeck,
                                        ::isAvailable,
                                        ::toggleToCustomDeck
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CardsColumn(
    back: CardBack,
    state2: LazyListState,
    updateKeys: () -> Unit,
    isInCustomDeck: (CardWithPrice) -> Boolean,
    isAvailable: (CardWithPrice) -> Boolean,
    toggleToCustomDeck: (CardWithPrice) -> Unit,
) {
    LazyColumn(Modifier, state = state2) {
        items(CollectibleDeck(back).toList().sortedWith { o1, o2 ->
            when (o1) {
                is CardNumber -> {
                    if (o2 !is CardNumber) {
                        1
                    } else {
                        if (o1.rank != o2.rank) {
                            o2.rank.value - o1.rank.value
                        } else {
                            o1.suit.ordinal - o2.suit.ordinal
                        }
                    }
                }
                is CardFaceSuited -> {
                    when (o2) {
                        is CardJoker -> {
                            1
                        }
                        is CardFaceSuited -> {
                            if (o1.rank != o2.rank) {
                                o2.rank.value - o1.rank.value
                            } else {
                                o1.suit.ordinal - o2.suit.ordinal
                            }
                        }
                        is CardNumber -> {
                            -1
                        }
                    }
                }
                is CardJoker -> {
                    if (o2 is CardJoker) {
                        o2.number.ordinal - o1.number.ordinal
                    } else {
                        -1
                    }
                }
            }
        }) { card ->
            var isSelected by remember { mutableStateOf(isInCustomDeck(card)) }
            if (isAvailable(card)) {
                ShowCard(card as Card, Modifier
                    .clickable {
                        toggleToCustomDeck(card)
                        isSelected = !isSelected
                        if (isSelected) {
                            playSelectSound()
                        } else {
                            playCloseSound()
                        }
                        updateKeys()
                    }
                    .border(
                        width = (if (isSelected) 3 else 0).dp,
                        color = getSelectionColor()
                    )
                    .padding(4.dp)
                    .alpha(if (isSelected) 1f else 0.55f))
            } else {
                ShowCardBack(card as Card, Modifier
                    .padding(4.dp)
                    .alpha(0.33f))
            }
        }
    }
}

@Composable
fun ShowCharacteristics() {
    Column(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val deck = save.getCurrentDeckCopy()
        Row(
            Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val deckSizeMin = CResources.MIN_DECK_SIZE
            val color1 = if (deck.size < deckSizeMin) Color.Red else getTextColor()
            val color2 = if (deck.size < deckSizeMin) Color.Red else getTextStrokeColor()
            TextFallout(
                text = stringResource(Res.string.custom_deck_size, deck.size, deckSizeMin),
                color1,
                color2,
                14.sp,
                Modifier.fillMaxWidth(0.5f),
            )
            val nonFaces = deck.toList().count { it is CardNumber }
            val nonFacesMin = CResources.MIN_NUM_OF_NUMBERS
            val color3 = if (nonFaces < nonFacesMin) Color.Red else getTextColor()
            val color4 = if (nonFaces < nonFacesMin) Color.Red else getTextStrokeColor()
            TextFallout(
                text = stringResource(Res.string.custom_deck_non_faces, nonFaces, nonFacesMin),
                color3,
                color4,
                14.sp,
                Modifier.fillMaxWidth(),
            )
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            val decksUsed = deck.toList().distinctBy { it.getBack() }.size
            val decksUsedMax = CResources.MAX_NUMBER_OF_DECKS
            val color1 = if (decksUsed > decksUsedMax) Color.Red else getTextColor()
            val color2 = if (decksUsed > decksUsedMax) Color.Red else getTextStrokeColor()
            TextFallout(
                text = stringResource(Res.string.custom_deck_num_of_decks, decksUsed, decksUsedMax),
                color1,
                color2,
                14.sp,
                Modifier.fillMaxWidth(0.5f),
            )
        }
    }
}