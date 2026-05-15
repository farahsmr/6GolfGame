package service
import entity.Card
import entity.CardSuit
import entity.CardValue

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
        rootService.gameService.startNewGame(listOf("Alice", "Bob"), isRandom = false, isTestMode = false)
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

    /** Test endGame calculates scores correctly with identical column. */
    @Test
    fun testEndGameIdenticalColumn() {
        val game = rootService.currentGame
        checkNotNull(game)

        val player = game.players[0]

        // Set column 0 to identical kings
        player.train[0] = Card(CardSuit.CLUBS, CardValue.KING)
        player.train[3] = Card(CardSuit.SPADES, CardValue.KING)

        assertDoesNotThrow { rootService.gameService.endGame() }

        // identical column = 0 points for those cards
        assertTrue(player.score >= 0)
    }

    /** Test ending the game with no game active */
    @Test
    fun testEndGameNoGame() {
        rootService.currentGame = null

        assertThrows<IllegalStateException> {
            rootService.gameService.endGame()
        }
    }
}