package gui

import entity.Card
import entity.GameState
import service.RootService
import service.Refreshable
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.components.layoutviews.GridPane
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.core.Color
import tools.aqua.bgw.util.BidirectionalMap
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual

/**
 * The main [BoardGameScene] where the 6 Card Golf game is played
 *
 * @param rootService The [RootService] instance to access the other service methods and entity layer
 */
class SixCardGolfGameScene(private val rootService: RootService) :
    BoardGameScene(1920, 1080), Refreshable {

    /** Card image loader for front and back images */
    private val cardImageLoader = CardImageLoader()

    /**
     * Bidirectional map from [Card] entity to [CardView].
     * Used to find the view for a card and vice versa.
     */
    private val cardMap: BidirectionalMap<Card, CardView> = BidirectionalMap()



    /** Player grids based on number of player */
    // Card size
    private val cardW = 130
    private val cardH = 190


    /**
     * Grid positions based on player count:
     * 2 players: [0]=left, [1]=right
     * 3 players: [0]=left, [1]=right, [2]=top center
     * 4 players: [0]=left, [1]=right, [2]=top left, [3]=top right
     */
    private val gridPositions = listOf(
        Pair(300, 800),   // player 1 :left
        Pair(1500, 800),  // player 2 :right when 2 players
        Pair(675, 50),    // player 3 : top center (3 players) or top left (4 players)
        Pair(1050, 50)    // player 4 : right (4 players)
    )

    private val playerGrid0 = GridPane<CardView>(
        posX = gridPositions[0].first, posY = gridPositions[0].second,
        rows = 2, columns = 3, spacing = 10
    )
    private val playerGrid1 = GridPane<CardView>(
        posX = gridPositions[1].first, posY = gridPositions[1].second,
        rows = 2, columns = 3, spacing = 10
    )
    private val playerGrid2 = GridPane<CardView>(
        posX = gridPositions[2].first, posY = gridPositions[2].second,
        rows = 2, columns = 3, spacing = 10
    )
    private val playerGrid3 = GridPane<CardView>(
        posX = gridPositions[3].first, posY = gridPositions[3].second,
        rows = 2, columns = 3, spacing = 10
    )
    private val playerGrids = listOf(playerGrid0, playerGrid1, playerGrid2, playerGrid3)

    private val playerLabel0 = Label(
        posX = gridPositions[0].first - 120, posY = gridPositions[0].second + 200,
        width = 250, height = 35, text = "Player 1",
        font = Font(size = 22, fontWeight = Font.FontWeight.BOLD, color = Color(255, 255, 255))
    )
    private val playerLabel1 = Label(
        posX = gridPositions[1].first - 120, posY = gridPositions[1].second + 200,
        width = 250, height = 35, text = "Player 2",
        font = Font(size = 22, fontWeight = Font.FontWeight.BOLD, color = Color(255, 255, 255))
    )
    private val playerLabel2 = Label(
        posX = gridPositions[2].first - 120, posY = gridPositions[2].second + 200,
        width = 250, height = 35, text = "Player 3",
        font = Font(size = 22, fontWeight = Font.FontWeight.BOLD, color = Color(255, 255, 255))
    )
    private val playerLabel3 = Label(
        posX = gridPositions[3].first - 120, posY = gridPositions[3].second + 200,
        width = 250, height = 35, text = "Player 4",
        font = Font(size = 22, fontWeight = Font.FontWeight.BOLD, color = Color(255, 255, 255))
    )
    private val playerLabels = listOf(playerLabel0, playerLabel1, playerLabel2, playerLabel3)

    /**
     * Draw pile
     * Only allowed when player state is [GameState.NONE_REVEALED] and hand is empty
     */
    private val drawPileView = CardView(
        posX = 800, posY = 700,
        width = cardW, height = cardH,
        front = cardImageLoader.blankImage,
        back = cardImageLoader.backImage
    ).apply {
        showBack()
        onMouseClicked = {
            val game = rootService.currentGame
            val player = game?.players?.get(game.currentPlayerIndex)
            if (game != null &&
                player?.gameState == GameState.NONE_REVEALED &&
                player.hand == null
            ) {
                try {
                    rootService.playerActionService.drawCardFromDrawPile()
                } catch (e: IllegalArgumentException) {
                    println(e.message)
                }
            }
        }
    }


    /**
     * Discard pile
     * Only allowed when player state is [GameState.NONE_REVEALED] and hand is empty
     */
    private val discardPileView = CardView(
        posX = 960, posY = 700,
        width = cardW, height = cardH,
        front = cardImageLoader.blankImage,
        back = cardImageLoader.backImage
    ).apply {
        showFront()
        onMouseClicked = {
            val game = rootService.currentGame
            val player = game?.players?.get(game.currentPlayerIndex)
            if (game != null &&
                player?.gameState == GameState.NONE_REVEALED &&
                player.hand == null
            ) {
                try {
                    rootService.playerActionService.drawCardFromDiscardPile()
                } catch (e: IllegalArgumentException) {
                    println(e.message)
                }
            }
        }
    }


    /**
     * Hand card view : shown when player has drawn a card
     * Hidden by default
     */
    private val handCardView = CardView(
        posX = 865, posY = 250,
        width = cardW, height = cardH,
        front = cardImageLoader.blankImage,
        back = cardImageLoader.backImage
    ).apply { isVisible = false }

    /** Hand card label: shown when player has a card in hand */
    private val handLabel = Label(
        posX = 855, posY = 215, width = 150, height = 30,
        text = "In Hand",
        font = Font(size = 15, color = Color(255, 255, 0))
    ).apply { isVisible = false }

    /** Current player indicator */
    private val currentPlayerLabel = Label(
        posX = 700, posY = 620, width = 500, height = 45,
        text = "Current Player: -",
        font = Font(
            size = 32,
            fontWeight = Font.FontWeight.BOLD,
            color = Color(241, 230, 178)
    )
    )



    /**
     * Discard card button: discards the drawn card back to discard pile.
     */
    private val discardCardButton = Button(
        posX = 770, posY = 930, width = 170, height = 55,
        visual = ImageVisual("discard-button.png")
    ).apply {
        onMouseClicked = {
            try {
                rootService.playerActionService.discardCard()
            } catch (e: IllegalArgumentException) {
                println(e.message)
            }
        }
    }

    /** End turn button */
    private val endTurnButton = Button(
        posX = 980, posY = 930, width = 170, height = 55,
        visual = ImageVisual("end-turn-button.png")
    ).apply {
        onMouseClicked = {
            try {
                rootService.playerActionService.endTurn()
            } catch (e: IllegalArgumentException) {
                println(e.message)
            }
        }
    }
// would probably need an errorLabel too (add it later)

    /** Game log show game events */
    private val gameLogLabel = Label(
        posX = 1300, posY = 100, width = 600, height = 50,
        text = "Game Log:",
        font = Font(size = 30, color = Color(255, 255, 255)),
        visual = ColorVisual(0, 40, 0, 220)
    )
    private val gameLogIcon = Label(
        posX = 1450, posY = 20,
        width = 340, height = 70,
        visual = ImageVisual("game-log-button.png")
    )

    init {
        background = ImageVisual("background.png")
        playerGrids.forEach { it.isVisible = false }
        playerLabels.forEach { it.isVisible = false }

        addComponents(
            playerGrid0, playerGrid1, playerGrid2, playerGrid3,
            playerLabel0, playerLabel1, playerLabel2, playerLabel3,
            drawPileView, discardPileView, handCardView,
             handLabel,
            currentPlayerLabel,
            discardCardButton, endTurnButton,
            gameLogIcon, gameLogLabel
        )
    }

    // region Refreshes

    /**
     * Called after a new game starts.
     * Initializes all card grids for each player.
     */
    override fun refreshAfterGameStart() {
        val game = checkNotNull(rootService.currentGame) { "No game found." }
        cardMap.clear()

        when (game.players.size) {
            2 -> {
                playerGrid0.posX = 400.0; playerGrid0.posY = 450.0
                playerGrid1.posX = 1520.0; playerGrid1.posY = 450.0
                playerLabel0.posX = 270.0; playerLabel0.posY = 650.0
                playerLabel1.posX = 1380.0; playerLabel1.posY = 650.0
            }

            3 -> {
                playerGrid0.posX = 300.0; playerGrid0.posY = 800.0
                playerGrid1.posX = 1550.0; playerGrid1.posY = 800.0
                playerGrid2.posX = 950.0; playerGrid2.posY = 250.0
                playerLabel0.posX = 180.0; playerLabel0.posY = 1000.0
                playerLabel1.posX = 1430.0; playerLabel1.posY = 1000.0
                playerLabel2.posX = 830.0; playerLabel2.posY = 450.0

            }


            4 -> {
                playerGrid0.posX = 300.0; playerGrid0.posY = 800.0
                playerGrid3.posX = 1500.0; playerGrid3.posY = 800.0
                playerGrid1.posX = 600.0; playerGrid1.posY = 350.0
                playerGrid2.posX = 1100.0; playerGrid2.posY = 350.0
                playerLabel0.posX = 180.0; playerLabel0.posY = 1000.0
                playerLabel3.posX = 1380.0; playerLabel3.posY = 1000.0
                playerLabel1.posX = 470.0; playerLabel1.posY = 550.0
                playerLabel2.posX = 1000.0; playerLabel2.posY = 550.0
            }
        }
        if (game.players.size >= 3) {
            handCardView.posX = 1550.0
            handCardView.posY = 230.0
            handLabel.posX = 1540.0
            handLabel.posY = 200.0
        }
        // Show only active player grids and labels
        playerGrids.forEachIndexed { i, grid -> grid.isVisible = i < game.players.size }
        playerLabels.forEachIndexed { i, label ->
            label.isVisible = i < game.players.size
            if (i < game.players.size) label.text = game.players[i].playerName
        }

        // Initialize each player's 2x3 card grid
        game.players.forEachIndexed { playerIndex, player ->
            initializePlayerGrid(playerIndex, player.train)
        }

        updateDiscardPile()
        updateCurrentPlayerLabel()
        updateButtons()
        updateGameLog()
    }

    /**
     * Initializes the 2x3 card grid for a player.
     * Creates a [CardView] for each card in the player's train.
     * Cards are shown face up if revealed, face down otherwise.
     *
     * @param playerIndex index of the player
     * @param train list of cards in the player's train
     */

    private fun initializePlayerGrid(playerIndex: Int, train: List<Card>) {
        val grid = playerGrids[playerIndex]

        train.forEachIndexed { cardIndex, card ->
            val cardView = CardView(
                width = cardW,
                height = cardH,
                front = cardImageLoader.frontImageFor(card.suit, card.value),
                back = cardImageLoader.backImage
            )

            if (card.isRevealed) cardView.showFront() else cardView.showBack()

            cardView.onMouseClicked = {
                handleCardClick(playerIndex, cardIndex)
            }

            cardMap.add(card to cardView)
            grid[cardIndex % 3, cardIndex / 3] = cardView
        }
    }


    /**
     * Handles clicking on a card in the current player's grid.
     *
     * - [GameState.MUST_REVEAL_TWO], [GameState.MUST_REVEAL_ONE] → reveal card
     * - [GameState.NONE_REVEALED], [GameState.ONE_REVEALED] → reveal card (normal turn)
     * - [GameState.MUST_END_TURN], [GameState.DREW_FROM_DISCARD] → swap card with hand
     *
     * @param playerIndex index of the player who owns the card
     * @param cardIndex index of the card in the player's train
     */
    private fun handleCardClick(playerIndex: Int, cardIndex: Int) {
        val game = rootService.currentGame ?: return
        if (playerIndex != game.currentPlayerIndex) return
        val player = game.players[playerIndex]

        runCatching {
            when (player.gameState) {
                GameState.MUST_REVEAL_TWO,
                GameState.MUST_REVEAL_ONE -> {
                    rootService.playerActionService.revealCard(cardIndex)
                }

                GameState.NONE_REVEALED -> {
                    // if player has hand card → swap, otherwise reveal
                    if (player.hand != null) {
                        rootService.playerActionService.swapCard(cardIndex)
                    } else {
                        rootService.playerActionService.revealCard(cardIndex)
                    }
                }

                GameState.ONE_REVEALED -> {
                    rootService.playerActionService.revealCard(cardIndex)
                }

                GameState.MUST_END_TURN,
                GameState.DREW_FROM_DISCARD -> {
                    if (player.hand != null) {
                        rootService.playerActionService.swapCard(cardIndex)
                    }
                }
            }
        }.onFailure { println(it.message) }
    }

    /**
     * Updates the discard pile view with the top card.
     */
    private fun updateDiscardPile() {
        val game = rootService.currentGame ?: return
        if (game.discardPile.isNotEmpty()) {
            val top = game.discardPile[0]
            discardPileView.frontVisual = cardImageLoader.frontImageFor(top.suit, top.value)
            discardPileView.showFront()
        }
    }

    private fun updateCurrentPlayerLabel() {
        val game = rootService.currentGame ?: return
        currentPlayerLabel.text = "Current Player: ${game.players[game.currentPlayerIndex].playerName}"
    }



    /**
     * Updates button enabled/disabled state based on current game state.
     *
     * - End Turn: only enabled when [GameState.ONE_REVEALED] or [GameState.MUST_END_TURN]
     * - Discard Card: only enabled when [GameState.NONE_REVEALED] and player has a card in hand
     */
    private fun updateButtons() {
        val game = rootService.currentGame ?: return
        val player = game.players[game.currentPlayerIndex]

        endTurnButton.isDisabled = player.gameState != GameState.ONE_REVEALED &&
                player.gameState != GameState.MUST_END_TURN

        discardCardButton.isDisabled = player.hand == null ||
                player.gameState != GameState.NONE_REVEALED
    }

    /**
     * Updates the game log with the last event
     */
    private fun updateGameLog() {
        val game = rootService.currentGame ?: return
        gameLogLabel.text = game.log.takeLast(1).joinToString("\n")
    }

    override fun refreshAfterCardDrawn(card: Card) {
        val game = rootService.currentGame ?: return
        val player = game.players[game.currentPlayerIndex]

        player.hand?.let { hand ->
            handCardView.frontVisual = cardImageLoader.frontImageFor(hand.suit, hand.value)
            handCardView.showFront()
            handCardView.isVisible = true
            handLabel.isVisible = true
        }

        updateDiscardPile()
        updateButtons()
        updateGameLog()
    }


    override fun refreshAfterCardRevealed(cardIndex: Int) {
        val game = rootService.currentGame ?: return
        val player = game.players[game.currentPlayerIndex]

        // Flip card to front
        if (cardIndex < player.train.size) {
            val card = player.train[cardIndex]
            cardMap.forward(card).showFront()
        }

        updateButtons()
        updateGameLog()
    }

    override fun refreshAfterCardSwapped(newCard: Card, cardIndex: Int) {
        // Hide hand card and reinitialize grids
        handCardView.isVisible = false
        handLabel.isVisible = false
        refreshAfterGameStart()
    }

    override fun refreshAfterCardDiscarded(card: Card) {
        // Hide hand card after discarding
        handCardView.isVisible = false
        handLabel.isVisible = false
        updateDiscardPile()
        updateButtons()
        updateGameLog()
    }

    override fun refreshAfterTurnEnd() {
        // Hide hand card and update for next player
        handCardView.isVisible = false
        handLabel.isVisible = false
        updateCurrentPlayerLabel()
        updateButtons()
        updateGameLog()
        // Reinitialize grids to show correct card states for next player
        refreshAfterGameStart()
    }

    override fun refreshAfterRowDiscarded(rowIndex: Int) {
        refreshAfterGameStart()
    }

    override fun refreshAfterScoresRevealed() {
        refreshAfterGameStart()
    }

    override fun refreshAfterGameWon() {
        updateGameLog()
    }

//known issue : Currently re-initializes the entire scene everytime
//fix: Implement an update function ( updatePlayerGrid)
}