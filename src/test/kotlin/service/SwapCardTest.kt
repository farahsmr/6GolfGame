package service

import entity.Card
import entity.CardSuit
import entity.CardValue
import entity.GameState
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.*

/**
 * Class for testing the [PlayerActionService.swapCard] method.
 */
class SwapCardTest {
    private lateinit var rootService: RootService
    private lateinit var testRefreshable: TestRefreshable

    @BeforeTest
    fun setUp() {
        rootService = RootService()
        testRefreshable = TestRefreshable()
        rootService.addRefreshable(testRefreshable)
        rootService.gameService.startNewGame(listOf("Alice", "Bob"), isRandom = false, isTestMode = false)
    }

    /** Test swapping a card in NONE_REVEALED state */
    @Test
    fun testSwapCard() {
        val game = rootService.currentGame
        checkNotNull(game)

        val player = game.players[game.currentPlayerIndex]
        player.gameState = GameState.NONE_REVEALED

        val newCard = Card(CardSuit.HEARTS, CardValue.ACE)
        player.hand = newCard

        val oldCard = player.train[0]

        // Test: Swapping a card does not throw
        assertDoesNotThrow { rootService.playerActionService.swapCard(0) }

        // Test: New card is in train
        assertEquals(newCard, player.train[0])

        // Test: Old card is on discard pile
        assertEquals(oldCard, game.discardPile[0])

        // Test: Hand is null
        assertNull(player.hand)

        // Test: State updated to MUST_END_TURN
        assertEquals(GameState.MUST_END_TURN, player.gameState)

        // Test: Refresh was called
        assertTrue(testRefreshable.refreshAfterCardSwappedCalled)
    }

    /** Test swapping a card with no card in hand */
    @Test
    fun testSwapCardNoHand() {
        val game = rootService.currentGame
        checkNotNull(game)

        val player = game.players[game.currentPlayerIndex]
        player.gameState = GameState.NONE_REVEALED
        player.hand = null

        // Test: Swapping with no card in hand throws
        assertThrows<IllegalStateException> {
            rootService.playerActionService.swapCard(0)
        }
    }

    /** Test swapping a card with wrong state. */
    @Test
    fun testSwapCardWrongState() {
        val game = rootService.currentGame
        checkNotNull(game)

        val player = game.players[game.currentPlayerIndex]
        player.gameState = GameState.MUST_REVEAL_TWO
        player.hand = Card(CardSuit.HEARTS, CardValue.ACE)

        // Test: Swapping in wrong state throws
        assertThrows<IllegalStateException> {
            rootService.playerActionService.swapCard(0)
        }
    }

    /** Test swapping a card with no game active */
    @Test
    fun testSwapCardNoGame() {
        rootService.currentGame = null

        assertThrows<IllegalStateException> {
            rootService.playerActionService.swapCard(0)
        }
    }
}