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
    /**
     * Sets up a new [RootService] and [TestRefreshable] before each test
     * and starts a new game with two players (Alice, Bob).
     */
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

        // Set remaining cards to known values
        player.train[1] = Card(CardSuit.CLUBS, CardValue.ACE)
        player.train[2] = Card(CardSuit.CLUBS, CardValue.THREE)
        player.train[4] = Card(CardSuit.DIAMONDS, CardValue.FOUR)
        player.train[5] = Card(CardSuit.SPADES, CardValue.TWO)

        assertDoesNotThrow { rootService.gameService.endGame() }

        // identical column 0 = 0 points, remaining 6 points
        assertEquals(6, player.score)
    }

    /** Test endGame with test mode deck */
    @Test
    fun testEndGameTestMode() {
        rootService.currentGame = null
        rootService.gameService.startNewGame(listOf("Alice", "Bob"), false, true)

        val game = rootService.currentGame
        checkNotNull(game)

        assertDoesNotThrow { rootService.gameService.endGame() }
        assertTrue(testRefreshable.refreshAfterGameWonCalled)
    }
    /**
     * Tests if [GameService.endGame] correctly calculates scores for all card values.
     */

    @Test
    fun testEndGameAllCardValues() {
        val game = rootService.currentGame
        checkNotNull(game)
        val player = game.players[0]

        player.train[0] = Card(CardSuit.CLUBS, CardValue.SIX)
        player.train[1] = Card(CardSuit.CLUBS, CardValue.SEVEN)
        player.train[2] = Card(CardSuit.CLUBS, CardValue.EIGHT)
        player.train[3] = Card(CardSuit.SPADES, CardValue.NINE)
        player.train[4] = Card(CardSuit.SPADES, CardValue.KING)
        player.train[5] = Card(CardSuit.SPADES, CardValue.FIVE)

        assertDoesNotThrow { rootService.gameService.endGame() }
        // 6+7+8+9+0+5 = 35
        assertEquals(35, player.score)
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