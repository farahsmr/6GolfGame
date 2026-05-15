package service
import entity.Card
import entity.GolfGame
import entity.Player
import entity.CardValue
import entity.CardSuit

/**
 * Service layer class that provides the logic for actions not directly
 * related to a single player.
 *
 * @param rootService The [RootService] instance to access the other service
 */
class GameService(private val rootService: RootService) : AbstractRefreshingService() {

    /**
     * Starts a new game with the given player names.
     *
     * preconditions:
     * - no game is currently active
     * - 2-4 player names are provided
     * - no name is blank
     * - no duplicate names
     *
     * @param playerNames the names of the players
     * @param isRandom whether the player order should be randomized
     * @param isTestMode whether to use a test Mode is chosen
     * @throws IllegalStateException if a game is already active
     * @throws IllegalArgumentException if player count is not between 2 and 4
     * @throws IllegalArgumentException if any name is blank
     * @throws IllegalArgumentException if any names are duplicates
     */
    fun startNewGame(playerNames : List<String>, isRandom : Boolean, isTestMode: Boolean){
        // Get current game and check if it is still not running
        val game = rootService.currentGame
        check(game==null) { "A game is currently active" }

        // Make sure number of players is between 2 and 4
        require(playerNames.size in 2..4) { "Players number must be between 2 and 4" }

        // Make sure names aren't empty strings
        require(playerNames.none { it.isBlank() }) { "Names cannot be empty" }

        // Make sure no duplicates
        require(playerNames.size == playerNames.toSet().size) { "Names must be unique" }

        // Create players by mapping the player names to player objects
        var playerList = playerNames.map { Player(it) }
        // Shuffle players if random order is selected
        if (isRandom) playerList = playerList.shuffled()

        // Create the draw pile
        val drawPile = createDrawPile(isTestMode)

        // Take top card from draw pile and put on discard pile
        val topCard = drawPile.removeFirst()

        // Deal six cards to each player by iterating over the player list
        playerList.forEach { player ->
            repeat(6) {
                player.train.add(drawPile.removeFirst())
            }
        }

        // Set the current game of the root service to the created game
        rootService.currentGame = GolfGame(
            currentPlayerIndex = 0,
            players = playerList.toMutableList(),
            drawPile = drawPile,
            discardPile = mutableListOf(topCard)
        )

        // notify the GUI
        onAllRefreshables {
            refreshAfterGameStart()
        }
    }

    /**
     * Creates a shuffled draw pile for the game.
     *
     * @param isTestMode whether to use a reduced test deck
     * @return a shuffled [MutableList] of [Card]s
     */
    private fun createDrawPile(isTestMode: Boolean): MutableList<Card> {
        // create empty draw pile
        val drawPile = mutableListOf<Card>()

        if (isTestMode) {
            // test mode: only 3 values (TWO, KING, QUEEN) repeated for 52 cards
            val testValues = listOf(CardValue.TWO, CardValue.KING, CardValue.QUEEN)
            // cycle through 4 suits and 3 test values to create 52 cards
            for (i in 0 until 52) {
                drawPile.add(Card(CardSuit.entries[i % 4], testValues[i % 3]))
            }
        } else {
            // normal mode: all 52 cards (4 suits x 13 values)
            for (suit in CardSuit.entries) {
                for (value in CardValue.entries) {
                    drawPile.add(Card(suit, value))
                }
            }
        }

        // shuffle the draw pile
        drawPile.shuffle()

        return drawPile
    }

    /**
     * Removes a row of identical cards from the player's train and adds them to the discard pile.
     *
     * @param rowIndex the index of the row to remove (0 = top row, 1 = bottom row)
     * @param player the player whose row is being removed
     */
    private fun removeTripleRow(rowIndex: Int, player: Player) {
        // Get current game and check if it is still not running
        val game = checkNotNull(rootService.currentGame) { "No game is currently active" }

        // row 0 = cards 0,1,2 and row 1 = cards 3,4,5
        val startIndex = rowIndex * 3

        // remove 3 cards from train and add to discard pile
        repeat(3) {
            val card = player.train.removeAt(startIndex)
            game.discardPile.add(0, card)
        }

        // notify the GUI
        onAllRefreshables { refreshAfterRowDiscarded(rowIndex) }
    }
    /**
     * Checks if all cards of the current player are revealed
     */
    fun checkAllRevealed() {
        val game = checkNotNull(rootService.currentGame) { "No game is currently active" }
        val player = game.players[game.currentPlayerIndex]

        // check if all cards in player's train are revealed
        if (player.train.all { it.isRevealed }) {
            player.finalRound = true
            checkAllRevealed()
        }
    }

    /**
     * Checks if any row in the player's train has identical card values
     * If so removes that row from the train
     *
     * @param player the player whose rows are being checked
     */
    fun checkRows(player: Player) {

        // check row 0 (cards 0,1,2) and row 1 (cards 3,4,5)
        for (rowIndex in 0..1) {
            val startIndex = rowIndex * 3

            // only check row if it still has 3 cards
            if (player.train.size >= startIndex + 3) {
                val card1 = player.train[startIndex]
                val card2 = player.train[startIndex + 1]
                val card3 = player.train[startIndex + 2]

                // if all 3 cards in row have same value remove the row
                if (card1.value == card2.value && card2.value == card3.value) {
                    removeTripleRow(rowIndex, player)
                }
            }
        }
    }

    /**
     * Calculates the score for each player and stores it in their [Player.score] property.
     */
    private fun calculateScores() {
        val game = checkNotNull(rootService.currentGame) { "No game is currently active" }

        game.players.forEach { player ->
            var score = 0
            for (col in 0..2) {
                val topCard = player.train.getOrNull(col)
                val bottomCard = player.train.getOrNull(col + 3)

                // identical column = 0 points
                if (topCard != null && bottomCard != null && topCard.value == bottomCard.value) continue

                // easier if done in enum CardValue
                // add points for top card
                score += when (topCard?.value) {
                    CardValue.ACE -> 1
                    CardValue.TWO -> -2
                    CardValue.THREE -> 3
                    CardValue.FOUR -> 4
                    CardValue.FIVE -> 5
                    CardValue.SIX -> 6
                    CardValue.SEVEN -> 7
                    CardValue.EIGHT -> 8
                    CardValue.NINE -> 9
                    CardValue.TEN -> 10
                    CardValue.JACK, CardValue.QUEEN -> 10
                    CardValue.KING -> 0
                    null -> 0
                }
                // add points for bottom card
                score += when (bottomCard?.value) {
                    CardValue.ACE -> 1
                    CardValue.TWO -> -2
                    CardValue.THREE -> 3
                    CardValue.FOUR -> 4
                    CardValue.FIVE -> 5
                    CardValue.SIX -> 6
                    CardValue.SEVEN -> 7
                    CardValue.EIGHT -> 8
                    CardValue.NINE -> 9
                    CardValue.TEN -> 10
                    CardValue.JACK, CardValue.QUEEN -> 10
                    CardValue.KING -> 0
                    null -> 0
                }
            }
            player.score = score
        }
    }
    /**
     * Ends the game, calculates final scores and reveals all remaining cards
     */
    fun endGame() {
        val game = checkNotNull(rootService.currentGame) { "No game is currently active" }

        // reveal all remaining hidden cards
        game.players.forEach { player ->
            player.train.forEach { card ->
                card.isRevealed = true
            }
        }

        // check rows one last time for each player
        game.players.forEach { player ->
            checkRows(player)
        }

        // calculate final scores
        calculateScores()

        // notify the GUI
        onAllRefreshables {
            refreshAfterScoresRevealed()
            refreshAfterGameWon()
        }
    }
}


