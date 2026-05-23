package service

import entity.GameState

/**
 * Service layer class that provides the logic for actions a player can take
 *
 * @param rootService The [RootService] instance to access the other service
 */
class PlayerActionService(private val rootService: RootService) : AbstractRefreshingService() {

    /**
     * Draws the top card from the draw pile and gives it to the current player
     *
     * @throws IllegalStateException if no game is currently active
     * @throws IllegalStateException if the draw pile is empty
     */
    fun drawCardFromDrawPile() {
        // Get current game and check if it is running
        val game = rootService.currentGame
        checkNotNull(game) { "No game is currently active" }

        // selecting the current player
        val player = game.players[game.currentPlayerIndex]

        // check draw pile is not empty
        check(game.drawPile.isNotEmpty()) { "Draw pile is empty" }

        // check player is in correct state
        check(player.gameState == GameState.NONE_REVEALED) { "Player already took an action" }

        // draw the top card
        val card = game.drawPile.removeFirst()

        // give it to the player
        player.hand = card

        game.log.add("${player.playerName}: drew a card from draw pile")

        // notify the GUI
        onAllRefreshables { refreshAfterCardDrawn(card) }
    }

    /**
     * Draws the top card from the discard pile and gives it to the current player.
     *
     * @throws IllegalStateException if no game is currently active
     * @throws IllegalStateException if the discard pile is empty
     * @throws IllegalStateException if the player is not in state [GameState.NONE_REVEALED]
     */
    fun drawCardFromDiscardPile() {
        // Get current game and check if it is running
        val game = rootService.currentGame
        checkNotNull(game) { "No running game is active" }

        // check discard pile is not empty
        check(game.discardPile.isNotEmpty()) { "No cards to draw" }

        // selecting the current player
        val player = game.players[game.currentPlayerIndex]

        // check player is in correct state
        check(player.gameState == GameState.NONE_REVEALED) { "Player already drew a card" }

        // draw the top card
        val card = game.discardPile.removeFirst()

        // give it to the player
        player.hand = card

        // update player state to drew from discard
        player.gameState = GameState.DREW_FROM_DISCARD

        game.log.add("${player.playerName}: drew a card from discard pile")

        // notify the GUI
        onAllRefreshables { refreshAfterCardDrawn(card) }
    }

    /**
     * Reveals a card at the given index in the current player's train.
     *
     * @param cardIndex The index of the card to reveal in the player's train
     * @throws IllegalStateException if no game is currently active
     * @throws IllegalStateException if the card is already revealed
     * @throws IllegalStateException if the player cannot reveal a card in current state
     */
    fun revealCard(cardIndex: Int) {
        // Get current game and check if it is running
        val game = rootService.currentGame
        checkNotNull(game) { "No game is currently active" }

        // selecting the current player
        val player = game.players[game.currentPlayerIndex]

        // make sure card is not already revealed
        check(!player.train[cardIndex].isRevealed) { "Card is already revealed" }

        // reveal the card
        player.train[cardIndex].isRevealed=true

        // update player state based on current state
        player.gameState = when (player.gameState) {
            GameState.MUST_REVEAL_TWO -> GameState.MUST_REVEAL_ONE
            GameState.MUST_REVEAL_ONE -> GameState.MUST_END_TURN
            GameState.NONE_REVEALED -> GameState.ONE_REVEALED
            GameState.ONE_REVEALED -> GameState.MUST_END_TURN
            else -> throw IllegalStateException("Cannot reveal card in current state")
        }

        // check if a row needs to be removed after reveal
        rootService.gameService.checkRows(player)

        // check if all cards are revealed (game might be over)
        rootService.gameService.checkAllRevealed()

        game.log.add("${player.playerName}: revealed card at index $cardIndex")

        // notify the GUI
        onAllRefreshables { refreshAfterCardRevealed(cardIndex) }
    }

    /**
     * Swaps the card in the current player's hand with the card at the given index
     *
     * @param cardIndex The index of the card to swap in the player's train
     * @throws IllegalStateException if no game is currently active
     * @throws IllegalStateException if the player has no card in hand
     * @throws IllegalStateException if the player is not in the correct state
     */
    fun swapCard(cardIndex: Int) {
        // Get current game and check if it is running
        val game = rootService.currentGame
        checkNotNull(game) { "No game is currently active" }

        // selecting the current player
        val player = game.players[game.currentPlayerIndex]

        // check player has a card in hand
        val newCard = checkNotNull(player.hand) { "Player has no card in hand" }

        // check player is in correct state
        check(
            player.gameState == GameState.NONE_REVEALED ||
                    player.gameState == GameState.DREW_FROM_DISCARD
        ) { "Player cannot swap card in current state" }

        // swap the card
        val oldCard = player.train[cardIndex]
        newCard.isRevealed = true
        player.train[cardIndex] = newCard
        player.hand = null

        // put old card on discard pile
        game.discardPile.add(0,oldCard)

        // update player state
        player.gameState = GameState.MUST_END_TURN

        // check if a row needs to be removed after swap
        rootService.gameService.checkRows(player)

        game.log.add("${player.playerName}: swapped card at index $cardIndex")

        // notify the GUI
        onAllRefreshables { refreshAfterCardSwapped(newCard, cardIndex) }
    }

    /**
     * Discards the card in the current player's hand onto the discard pile.
     * The player must then reveal one of their cards.
     *
     * @throws IllegalStateException if no game is currently active
     * @throws IllegalStateException if the player has no card in hand
     * @throws IllegalStateException if the player drew from the discard pile
     */
    fun discardCard() {
        // Get current game and check if it is running
        val game = rootService.currentGame
        checkNotNull(game) { "No game is currently active" }

        // selecting the current player
        val player = game.players[game.currentPlayerIndex]

        // check player has a card in hand
        val card = checkNotNull(player.hand) { "Player has no card in hand" }

        // check player drew from draw pile not discard pile
        check(player.gameState == GameState.NONE_REVEALED) {
            "Cannot discard a card drawn from the discard pile"
        }

        // put card on discard pile
        game.discardPile.add(0,card)

        // clear player's hand
        player.hand = null

        // update player state — must now reveal a card
        player.gameState = GameState.MUST_REVEAL_ONE

        game.log.add("${player.playerName}: discarded card")

        // notify the GUI
        onAllRefreshables { refreshAfterCardDiscarded(card) }
    }

    /**
     * Ends the current player's turn and moves to the next player
     *
     * @throws IllegalStateException if no game is currently active
     * @throws IllegalStateException if the player cannot end their turn yet
     */
    fun endTurn() {
        // Get current game and check if it is running
        val game = rootService.currentGame
        checkNotNull(game) { "No game is currently active" }

        // selecting the current player
        val player = game.players[game.currentPlayerIndex]

        // check player is allowed to end turn
        check(
            player.gameState == GameState.ONE_REVEALED ||
                    player.gameState == GameState.MUST_END_TURN
        ) { "Player cannot end turn yet" }

        // reset player state for next turn
        player.gameState = GameState.NONE_REVEALED

        // move to next player
        game.currentPlayerIndex = (game.currentPlayerIndex + 1) % game.players.size

        // check if final round is over
        if (game.players[game.currentPlayerIndex].finalRound) {
            rootService.gameService.endGame()
        }

        game.log.add("${player.playerName}: ended turn")

        // notify the GUI
        onAllRefreshables { refreshAfterTurnEnd() }
    }
}