package service


import entity.GameState
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.*

/**
 * Class for testing the [PlayerActionService.drawCardFromDiscardPile] method.
 */
class DrawCardFromDiscardPileTest {
    private lateinit var rootService: RootService
    private lateinit var testRefreshable: TestRefreshable

    @BeforeTest
    fun setUp() {
        rootService = RootService()
        testRefreshable = TestRefreshable()
        rootService.addRefreshable(testRefreshable)
        rootService.gameService.startNewGame(playerNames = listOf("Alice", "Bob"), isRandom = false, isTestMode = false)
    }

    /** Test drawing a card from the discard pile */
    @Test
    fun testDrawCardFromDiscardPile() {
        val game = rootService.currentGame
        checkNotNull(game)

        val player = game.players[game.currentPlayerIndex]
        player.gameState = GameState.NONE_REVEALED

        val pileSizeBefore = game.discardPile.size

        // Test: Drawing from discard pile does not throw
        assertDoesNotThrow { rootService.playerActionService.drawCardFromDiscardPile() }

        // Test: Discard pile decreased by 1
        assertEquals(pileSizeBefore - 1, game.discardPile.size)

        // Test: Player has a card in hand
        assertNotNull(player.hand)

        // Test: Player state updated
        assertEquals(GameState.DREW_FROM_DISCARD, player.gameState)

        // Test: Refresh was called
        assertTrue(testRefreshable.refreshAfterCardDrawnCalled)
    }

    /** Test drawing a card from an empty discard pile */
    @Test
    fun testDrawCardFromDiscardPileEmpty() {
        val game = rootService.currentGame
        checkNotNull(game)

        val player = game.players[game.currentPlayerIndex]
        player.gameState = GameState.NONE_REVEALED

        // Empty the discard pile
        game.discardPile.clear()

        // Test: Drawing from empty discard pile throws
        assertThrows<IllegalStateException> {
            rootService.playerActionService.drawCardFromDiscardPile()
        }
    }

    /** Test drawing a card from discard pile in wrong state */
    @Test
    fun testDrawCardFromDiscardPileWrongState() {
        val game = rootService.currentGame
        checkNotNull(game)

        val player = game.players[game.currentPlayerIndex]
        player.gameState = GameState.DREW_FROM_DISCARD

        // Test: Drawing in wrong state throws
        assertThrows<IllegalStateException> {
            rootService.playerActionService.drawCardFromDiscardPile()
        }
    }

    /** Test drawing a card from discard pile with no game active */
    @Test
    fun testDrawCardFromDiscardPileNoGame() {
        rootService.currentGame = null

        assertThrows<IllegalStateException> {
            rootService.playerActionService.drawCardFromDiscardPile()
        }
    }
}