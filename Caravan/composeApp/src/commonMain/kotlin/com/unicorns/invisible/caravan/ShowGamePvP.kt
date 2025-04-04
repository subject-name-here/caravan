package com.unicorns.invisible.caravan

//
//// TODO: update this!!!
//fun afterPlayerMove(
//    game: Game,
//    speed: AnimationSpeed,
//    roomNumber: Int,
//    isCreator: Boolean,
//    move: MoveResponse,
//    chosenSymbol: Int,
//    updateView: () -> Unit,
//    corrupt: (String) -> Unit,
//    startPinging: () -> Unit,
//) {
//    CoroutineScope(Dispatchers.Default).launch {
//        if (speed.delay != 0L) {
//            delay(speed.delay * 2)
//        }
//        game.isPlayerTurn = false
//        val isNewCardAdded = game.playerCResources.deckSize > 0 && game.playerCResources.hand.size < 5
//
//        game.processField(AnimationSpeed.NONE)
//        if (speed.delay != 0L) {
//            delay(speed.delay * 2) // Remove cards; move cards within caravan
//        }
//        updateView()
//
//        if (game.playerCResources.hand.size < 5 && game.playerCResources.deckSize > 0) {
//            game.playerCResources.addToHand()
//            updateView()
//            if (speed.delay != 0L) {
//                delay(speed.delay) // Take card into hand
//            }
//        }
//
//        updateView()
//
//        if (isNewCardAdded) {
//            val newCard = game.playerCResources.hand.last()
//            move.newCardInHandBack = newCard.back.ordinal
//            move.newCardInHandSuit = newCard.suit.ordinal
//            move.newCardInHandRank = newCard.rank.ordinal
//            move.isNewCardAlt = newCard.isAlt
//        } else {
//            move.newCardInHandBack = -1
//        }
//        game.checkOnGameOver()
//        updateView()
//
//        sendRequest(
//            "${crvnUrl}/crvn/move?room=$roomNumber" +
//                    "&is_creators_move=${isCreator.toPythonBool()}" +
//                    "&symbol=$chosenSymbol" +
//                    "&move_code=${move.moveCode}" +
//                    "&caravan_code=${move.caravanCode}" +
//                    "&hand_card_number=${move.handCardNumber}" +
//                    "&card_in_caravan_number=${move.cardInCaravanNumber}" +
//                    "&new_card_back_in_hand_code=${move.newCardInHandBack}" +
//                    "&new_card_rank_in_hand_code=${move.newCardInHandRank}" +
//                    "&new_card_suit_in_hand_code=${move.newCardInHandSuit}" +
//                    "&is_alt=${move.isNewCardAlt.toPythonBool()}"
//        ) { result ->
//            if (result.toString().contains("oom")) {
//                corrupt(result.toString())
//                return@sendRequest
//            }
//            if (game.isOver()) {
//                return@sendRequest
//            }
//            startPinging()
//        }
//    }
//}
//
//
//fun pingForMove(
//    game: Game, speed: AnimationSpeed, room: Int, isCreator: Boolean,
//    setEnemySymbol: (Int) -> Unit,
//    corrupt: (String) -> Unit,
//    updateView: () -> Unit,
//    afterEnemyMove: (Boolean) -> Unit
//) {
//    sendRequest("${crvnUrl}/crvn/get_move?room=$room&is_creators_move=${isCreator.toPythonBool()}") { result ->
//        if (result.getString("body") == "-1") {
//            CoroutineScope(Dispatchers.Unconfined).launch {
//                delay(1900L)
//                pingForMove(
//                    game,
//                    speed,
//                    room,
//                    isCreator,
//                    setEnemySymbol,
//                    corrupt,
//                    updateView,
//                    afterEnemyMove
//                )
//            }
//            return@sendRequest
//        }
//
//        try {
//            val enemyMove = decodeMove(result.getString("body"))
//            (game.enemy as EnemyPlayer).latestMoveResponse = enemyMove
//            setEnemySymbol(enemyMove.symbolNumber)
//        } catch (_: Exception) {
//            corrupt(result.toString())
//            return@sendRequest
//        }
//
//        CoroutineScope(Dispatchers.Default).launch {
//            if (speed.delay != 0L) {
//                delay(speed.delay) // Just break.
//            }
//            game.enemy.makeMove(game, AnimationSpeed.NONE)
//            updateView()
//
//            if (speed.delay != 0L) {
//                delay(speed.delay * 2) // Move card from hand; move card ontoField
//            }
//
//            game.processField(AnimationSpeed.NONE)
//            if (speed.delay != 0L) {
//                delay(speed.delay * 2) // Remove cards; move cards within caravan
//            }
//            updateView()
//
//            if (game.enemyCResources.hand.size < 5 && game.enemyCResources.deckSize > 0) {
//                game.enemyCResources.removeFirstCardFromDeck()
//                updateView()
//                if (speed.delay != 0L) {
//                    delay(speed.delay) // Take card into hand
//                }
//            }
//
//            game.isPlayerTurn = true
//            game.checkOnGameOver()
//            updateView()
//            if (speed.delay != 0L) {
//                delay(speed.delay)
//            }
//            afterEnemyMove(!game.isOver())
//        }
//    }
//}
//
//
//@Composable
//fun ShowGamePvP(
//    ,
//    game: Game,
//    isCreator: Boolean,
//    roomNumber: Int,
//    showAlert: (String, String, (() -> Unit)?) -> Unit,
//    updateEnemyHand: () -> Unit,
//    updateCaravans: () -> Unit,
//    updatePlayerHand: () -> Unit,
//    goBack: () -> Unit,
//) {
//    val speed = save.animationSpeed
//    var selectedCard by remember { mutableIntStateOf(-1) }
//    var selectedCaravan by remember { mutableIntStateOf(-1) }
//
//    var chosenSymbol by rememberSaveable { mutableIntStateOf(0) }
//    var enemyChosenSymbol by rememberSaveable { mutableIntStateOf(0) }
//
//    var timeOnTimer by rememberSaveable { mutableIntStateOf(0) }
//    var timeOnTimerTrigger by rememberSaveable { mutableStateOf(false) }
//
//    LaunchedEffect(key1 = timeOnTimerTrigger) {
//        if (game.isPlayerTurn) {
//            timeOnTimer = 38
//            while (isActive && timeOnTimer > 0) {
//                timeOnTimer--
//                if (timeOnTimer < 10) {
//                    playNoBeep(activity)
//                }
//                delay(1000L)
//            }
//        } else {
//            timeOnTimer = -1
//        }
//    }
//
//    fun onCardClicked(index: Int) {
//        if (game.isOver()) {
//            return
//        }
//        selectedCard = if (index == selectedCard) {
//            playCloseSound(activity)
//            -1
//        } else {
//            playSelectSound(activity)
//            index
//        }
//        selectedCaravan = -1
//    }
//
//    fun corruptGame(message: String) {
//        game.isCorrupted = true
//        if (currentGameId == game.id) {
//            showAlert("Corrupted!", message, null)
//        }
//    }
//
//    fun updateAll() {
//        updateEnemyHand(); updateCaravans(); updatePlayerHand()
//    }
//
//    fun resetSelected() {
//        selectedCaravan = -1
//        selectedCard = -1
//        timeOnTimer = 0
//    }
//
////    fun dropCardFromHand() {
////        if (!game.canPlayerMove) return
////        val selectedCardNN = selectedCard
////        if (selectedCardNN !in game.playerCResources.hand.indices) return
////        playVatsReady(activity)
////        game.playerCResources.dropCardFromHand(selectedCardNN)
////        resetSelected()
////        afterPlayerMove(game, speed, roomNumber, isCreator = isCreator, MoveResponse(
////            moveCode = 2,
////            handCardNumber = selectedCardNN,
////        ), chosenSymbol,
////            ::updateAll,
////            ::corruptGame,
////            {
////                pingForMove(
////                    game, speed, roomNumber, isCreator,
////                    { enemyChosenSymbol = it },
////                    ::corruptGame,
////                    ::updateAll,
////                    {
////                        if (it) {
////                            timeOnTimerTrigger = !timeOnTimerTrigger
////                        }
////                    }
////                )
////            }
////        )
////    }
////
////    fun dropCaravan() {
////        if (!game.canPlayerMove) return
////        val selectedCaravanNN = selectedCaravan
////        if (selectedCaravanNN == -1) return
////        playVatsReady(activity)
////        processChallengesMove(Challenge.Move(moveCode = 1), game)
////        game.playerCaravans[selectedCaravanNN].dropCaravan()
////        updateCaravans()
////        resetSelected()
////        afterPlayerMove(game, speed, roomNumber, isCreator = isCreator, MoveResponse(
////            moveCode = 1,
////            caravanCode = selectedCaravanNN,
////        ), chosenSymbol,
////            ::updateAll,
////            ::corruptGame,
////            {
////                pingForMove(
////                    game, speed, roomNumber, isCreator,
////                    { enemyChosenSymbol = it },
////                    ::corruptGame,
////                    ::updateAll,
////                    {
////                        if (it) {
////                            timeOnTimerTrigger = !timeOnTimerTrigger
////                        }
////                    }
////                )
////            }
////        )
////    }
////
////    fun addCardToCaravan(
////        caravan: Caravan,
////        caravanIndex: Int,
////        position: Int,
////        isEnemy: Boolean = false
////    ) {
////        if (!game.canPlayerMove) return
////        fun onCaravanCardInserted(cardIndex: Int, caravanIndex: Int, cardInCaravan: Int? = null) {
////            resetSelected()
////            updateCaravans()
////            if (cardInCaravan == null) {
////                afterPlayerMove(game, speed, roomNumber, isCreator = isCreator, MoveResponse(
////                    moveCode = 3,
////                    handCardNumber = cardIndex,
////                    caravanCode = caravanIndex
////                ), chosenSymbol,
////                    ::updateAll,
////                    ::corruptGame,
////                    {
////                        pingForMove(
////                            game, speed, roomNumber, isCreator,
////                            { enemyChosenSymbol = it },
////                            ::corruptGame,
////                            ::updateAll,
////                            {
////                                if (it) {
////                                    timeOnTimerTrigger = !timeOnTimerTrigger
////                                }
////                            }
////                        )
////                    }
////                )
////            } else {
////                afterPlayerMove(game, speed, roomNumber, isCreator = isCreator, MoveResponse(
////                    moveCode = 4,
////                    handCardNumber = cardIndex,
////                    cardInCaravanNumber = cardInCaravan,
////                    caravanCode = if (isEnemy) (-3 + caravanIndex) else caravanIndex
////                ), chosenSymbol,
////                    ::updateAll,
////                    ::corruptGame,
////                    {
////                        pingForMove(
////                            game, speed, roomNumber, isCreator,
////                            { enemyChosenSymbol = it },
////                            ::corruptGame,
////                            ::updateAll,
////                            {
////                                if (it) {
////                                    timeOnTimerTrigger = !timeOnTimerTrigger
////                                }
////                            }
////                        )
////                    }
////                )
////            }
////        }
////
////        val cardIndex = selectedCard
////        val card = game.playerCResources.hand.getOrNull(cardIndex) ?: return
////        if (game.isPlayerTurn && !game.isOver() && (!game.isInitStage() || !card.isModifier())) {
////            if (!card.isModifier()) {
////                if (position == caravan.cards.size && !isEnemy) {
////                    if (caravan.canPutCardOnTop(card)) {
////                        playCardFlipSound(activity)
////                        processChallengesMove(Challenge.Move(
////                            moveCode = 3,
////                            handCard = card
////                        ), game)
////                        caravan.putCardOnTop(game.playerCResources.removeFromHand(cardIndex))
////                        onCaravanCardInserted(cardIndex, caravanIndex, null)
////                    }
////                }
////            } else {
////                if (position in caravan.cards.indices && caravan.cards[position].canAddModifier(
////                        card
////                    )
////                ) {
////                    playCardFlipSound(activity)
////                    if (card.isOrdinary() && card.rank == Rank.JOKER) {
////                        playJokerSounds(activity)
////                    } else if (card.getWildWastelandType() != null) {
////                        playWWSound(activity)
////                    } else if (card.isNuclear()) {
////                        playNukeBlownSound(activity)
////                    }
////                    processChallengesMove(Challenge.Move(
////                        moveCode = 4,
////                        handCard = card
////                    ), game)
////                    caravan.cards[position].addModifier(
////                        game.playerCResources.removeFromHand(
////                            cardIndex
////                        )
////                    )
////                    onCaravanCardInserted(cardIndex, caravanIndex, position)
////                }
////            }
////        }
////    }
//
//    val symbols = listOf(
//        "\uD83D\uDC4B", // HI!
//        "\uD83D\uDC4F", // Good move
//        "\uD83D\uDE0A", // Happy face
//        "\uD83D\uDE15", // Errmmm...
//        "\uD83D\uDE28", // Devastated
//        "\uD83D\uDE0E", // deal with it.
//        "\uD83E\uDD2F", // mind-blowing move
//        "\uD83D\uDC80", // death
//        "\uD83D\uDE08", // Devil happy
//        "\uD83D\uDC7F", // Devil angry
//    )
//
////    ShowGameRaw(
////        activity,
////        true,
////        game,
////        goBack,
////        speed,
////        getEnemySymbol = { symbols[enemyChosenSymbol.coerceIn(0, symbols.size - 1)] },
////        getMySymbol = { symbols[chosenSymbol.coerceIn(0, symbols.size - 1)] },
////        setMySymbol = { chosenSymbol = (chosenSymbol + 1) % symbols.size },
////        { selectedCard },
////        ::onCardClicked,
////        getSelectedCaravan = { selectedCaravan },
////        setSelectedCaravan = lambda@ {
////            if (game.isOver()) {
////                return@lambda
////            }
////            selectedCaravan = it
////            if (selectedCaravan == -1) {
////                playCloseSound(activity)
////            } else {
////                playSelectSound(activity)
////            }
////            selectedCard = -1
////            updateCaravans()
////        },
////        ::addCardToCaravan,
////        ::dropCardFromHand,
////        ::dropCaravan,
////    )
//
//    key(timeOnTimer) {
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color.Transparent),
//            contentAlignment = Alignment.BottomEnd
//        ) {
//            TextFallout(
//                timeOnTimer.toString(),
//                getTextColor(activity),
//                getTextStrokeColor(activity),
//                14.sp,
//                Modifier
//                    .background(getTextBackgroundColor(activity))
//                    .padding(8.dp)
//            )
//        }
//    }
//}