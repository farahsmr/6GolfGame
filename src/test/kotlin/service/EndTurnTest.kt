package service

import entity.GameState
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.*

/**
 * Class for testing the [PlayerActionService.endTurn] method
 */
class EndTurnTest {
    private lateinit var rootService: RootService
    private lateinit var testRefreshable: TestRefreshable

    @BeforeTest
    fun setUp() {
        rootService = RootService()
        testRefreshable = TestRefreshable()
        rootService.addRefreshable(testRefreshable)
        rootService.gameService.startNewGame(listOf("Alice", "Bob"), isRandom = false, isTestMode = false)
    }

    /** Test ending a turn in ONE_REVEALED state */
    @Test
    fun testEndTurnOneRevealed() {
        val game = rootService.currentGame
        checkNotNull(game)

        val player = game.players[game.currentPlayerIndex]
        player.gameState = GameState.ONE_REVEALED

        // Test: Ending a turn does not throw
        assertDoesNotThrow { rootService.playerActionService.endTurn() }

        // Test: Current player index incremented
        assertEquals(1, game.currentPlayerIndex)

        // Test: Refresh was called
        assertTrue(testRefreshable.refreshAfterTurnEndCalled)
    }

    /** Test ending a turn in MUST_END_TURN state. */
    @Test
    fun testEndTurnMustEndTurn() {
        val game = rootService.currentGame
        checkNotNull(game)

        val player = game.players[game.currentPlayerIndex]
        player.gameState = GameState.MUST_END_TURN

        // Test: Ending a turn does not throw
        assertDoesNotThrow { rootService.playerActionService.endTurn() }

        // Test: Current player index incremented
        assertEquals(1, game.currentPlayerIndex)
    }

    /** Test ending a turn wraps around to first player. */
    @Test
    fun testEndTurnWrapsAround() {
        val game = rootService.currentGame
        checkNotNull(game)

        // Set to last player
        game.currentPlayerIndex = 1
        val player = game.players[game.currentPlayerIndex]
        player.gameState = GameState.ONE_REVEALED

        // Test: Ending turn wraps around to player 0
        assertDoesNotThrow { rootService.playerActionService.endTurn() }
        assertEquals(0, game.currentPlayerIndex)
    }

    /** Test ending a turn in wrong state. */
    @Test
    fun testEndTurnWrongState() {
        val game = rootService.currentGame
        checkNotNull(game)

        val player = game.players[game.currentPlayerIndex]
        player.gameState = GameState.NONE_REVEALED

        // Test: Ending turn in wrong state throws
        assertThrows<IllegalStateException> {
            rootService.playerActionService.endTurn()
        }
    }

    /** Test ending a turn with no game active. */
    @Test
    fun testEndTurnNoGame() {
        rootService.currentGame = null

        assertThrows<IllegalStateException> {
            rootService.playerActionService.endTurn()
        }
    }
}