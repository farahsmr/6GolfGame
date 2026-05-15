package service


import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.*

/**
 * Class for testing the [GameService.endGame] method
 */
class EndGameTest {
    private lateinit var rootService: RootService
    private lateinit var testRefreshable: TestRefreshable

    @BeforeTest
    fun setUp() {
        rootService = RootService()
        testRefreshable = TestRefreshable()
        rootService.addRefreshable(testRefreshable)
        rootService.gameService.startNewGame(listOf("Alice", "Bob"), false, false)
    }

    /** Test ending the game reveals all cards and calculates scores */
    @Test
    fun testEndGame() {
        val game = rootService.currentGame
        checkNotNull(game)

        // Test: endGame does not throw
        assertDoesNotThrow { rootService.gameService.endGame() }

        // Test: All cards are revealed
        game.players.forEach { player ->
            player.train.forEach { card ->
                assertTrue(card.isRevealed)
            }
        }

        // Test: Scores are calculated
        game.players.forEach { player ->
            assertNotNull(player.score)
        }

        // Test: Refreshes were called
        assertTrue(testRefreshable.refreshAfterScoresRevealedCalled)
        assertTrue(testRefreshable.refreshAfterGameWonCalled)
    }

    /** Test ending the game with no game active. */
    @Test
    fun testEndGameNoGame() {
        rootService.currentGame = null

        assertThrows<IllegalStateException> {
            rootService.gameService.endGame()
        }
    }
}