package service

import entity.Card
import entity.CardSuit
import entity.CardValue
import entity.GameState
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.*

/**
 * Class for testing the [PlayerActionService.discardCard] method.
 */
class DiscardCardTest {
    private lateinit var rootService: RootService
    private lateinit var testRefreshable: TestRefreshable

    @BeforeTest
    fun setUp() {
        rootService = RootService()
        testRefreshable = TestRefreshable()
        rootService.addRefreshable(testRefreshable)
        rootService.gameService.startNewGame(listOf("Alice", "Bob"), false, false)
    }

    /** Test discarding a card in NONE_REVEALED state. */
    @Test
    fun testDiscardCard() {
        val game = rootService.currentGame
        checkNotNull(game)

        val player = game.players[game.currentPlayerIndex]
        player.gameState = GameState.NONE_REVEALED

        val card = Card(CardSuit.HEARTS, CardValue.ACE)
        player.hand = card

        val pileSizeBefore = game.discardPile.size

        assertDoesNotThrow { rootService.playerActionService.discardCard() }

        // Test: card is on top of discard pile
        assertEquals(card, game.discardPile[0])
        assertEquals(pileSizeBefore + 1, game.discardPile.size)

        // Test: hand is null
        assertNull(player.hand)

        // Test: state updated to MUST_REVEAL_ONE
        assertEquals(GameState.MUST_REVEAL_ONE, player.gameState)

        // Test: refresh was called
        assertTrue(testRefreshable.refreshAfterCardDiscardedCalled)
    }

    /** Test discarding a card drawn from discard pile throws. */
    @Test
    fun testDiscardCardFromDiscardPile() {
        val game = rootService.currentGame
        checkNotNull(game)

        val player = game.players[game.currentPlayerIndex]
        player.gameState = GameState.DREW_FROM_DISCARD
        player.hand = Card(CardSuit.HEARTS, CardValue.ACE)

        assertThrows<IllegalStateException> {
            rootService.playerActionService.discardCard()
        }
    }

    /** Test discarding with no card in hand throws. */
    @Test
    fun testDiscardCardNoHand() {
        val game = rootService.currentGame
        checkNotNull(game)

        val player = game.players[game.currentPlayerIndex]
        player.gameState = GameState.NONE_REVEALED
        player.hand = null

        assertThrows<IllegalStateException> {
            rootService.playerActionService.discardCard()
        }
    }

    /** Test discarding with no game active throws. */
    @Test
    fun testDiscardCardNoGame() {
        rootService.currentGame = null

        assertThrows<IllegalStateException> {
            rootService.playerActionService.discardCard()
        }
    }
}