package service


import entity.GameState
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.*

/**
 * Class for testing the [PlayerActionService.drawCardFromDrawPile] method.
 */
class DrawCardFromDrawPileTest {
    private lateinit var rootService: RootService
    private lateinit var testRefreshable: TestRefreshable

    /**
     * Initialize service to set up the test environment. This function is executed before every test.
     */
    @BeforeTest
    fun setUp() {
        rootService = RootService()
        testRefreshable = TestRefreshable()
        rootService.addRefreshable(testRefreshable)
        rootService.gameService.startNewGame(listOf("Alice", "Bob"), isRandom = false, isTestMode = false)
    }

    /** Test drawing a card from the draw pile */
    @Test
    fun testDrawCardFromDrawPile() {
        val game = rootService.currentGame
        checkNotNull(game)

        val player = game.players[game.currentPlayerIndex]
        val pileSizeBefore = game.drawPile.size

        // Set player in correct state
        player.gameState = GameState.NONE_REVEALED

        // Test: Drawing a card does not throw
        assertDoesNotThrow { rootService.playerActionService.drawCardFromDrawPile() }

        // Test: Draw pile decreased by 1
        assertEquals(pileSizeBefore - 1, game.drawPile.size)

        // Test: Player has a card in hand
        assertNotNull(player.hand)

        // Test: Refresh was called
        assertTrue(testRefreshable.refreshAfterCardDrawnCalled)
    }

    /** Test drawing a card from an empty draw pile */
    @Test
    fun testDrawCardFromDrawPileEmpty() {
        val game = rootService.currentGame
        checkNotNull(game)

        val player = game.players[game.currentPlayerIndex]
        player.gameState = GameState.NONE_REVEALED

        // Empty the draw pile
        game.drawPile.clear()

        // Test: Drawing from empty pile throws
        assertThrows<IllegalStateException> {
            rootService.playerActionService.drawCardFromDrawPile()
        }
    }

    /** Test drawing a card with wrong game state */
    @Test
    fun testDrawCardFromDrawPileWrongState() {
        val game = rootService.currentGame
        checkNotNull(game)

        val player = game.players[game.currentPlayerIndex]
        player.gameState = GameState.DREW_FROM_DISCARD

        // Test: Drawing in wrong state throws
        assertThrows<IllegalStateException> {
            rootService.playerActionService.drawCardFromDrawPile()
        }
    }
    /** Test drawing resets after state changed */
    @Test
    fun testDrawCardFromDrawPileHandSet() {
        val game = rootService.currentGame
        checkNotNull(game)

        val player = game.players[game.currentPlayerIndex]
        player.gameState = GameState.NONE_REVEALED

        val topCard = game.drawPile.first()

        rootService.playerActionService.drawCardFromDrawPile()

        // Test hand is the top card
        assertEquals(topCard, player.hand)
    }

    /** Test drawing a card with no game active */
    @Test
    fun testDrawCardFromDrawPileNoGame() {
        rootService.currentGame = null

        assertThrows<IllegalStateException> {
            rootService.playerActionService.drawCardFromDrawPile()
        }
    }
}