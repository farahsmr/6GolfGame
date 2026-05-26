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
        rootService.gameService.startNewGame(listOf("Alice", "Bob"), isRandom = false, true)

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

        assertEquals(35, player.score)
    }
    /** Testy all enum values  */
    @Test
    fun testEndGameCoverageForAllValues() {
        val game = rootService.currentGame
        checkNotNull(game)

        // Setup Player 0 with the first 6 values
        val p0 = game.players[0]
        p0.train[0] = Card(CardSuit.CLUBS, CardValue.ACE)   //  1
        p0.train[1] = Card(CardSuit.CLUBS, CardValue.TWO)   // -2
        p0.train[2] = Card(CardSuit.CLUBS, CardValue.THREE) //  3
        p0.train[3] = Card(CardSuit.CLUBS, CardValue.FOUR)  //  4
        p0.train[4] = Card(CardSuit.CLUBS, CardValue.FIVE)  //  5
        p0.train[5] = Card(CardSuit.CLUBS, CardValue.SIX)   //  6

        // Setup Player 1 with the remaining 7 values
        val p1 = game.players[1]
        p1.train[0] = Card(CardSuit.DIAMONDS, CardValue.SEVEN) //  7
        p1.train[1] = Card(CardSuit.DIAMONDS, CardValue.EIGHT) //  8
        p1.train[2] = Card(CardSuit.DIAMONDS, CardValue.NINE)  //  9
        p1.train[3] = Card(CardSuit.DIAMONDS, CardValue.TEN)   // 10
        p1.train[4] = Card(CardSuit.DIAMONDS, CardValue.JACK)  // 10
        p1.train[5] = Card(CardSuit.DIAMONDS, CardValue.QUEEN) // 10

        assertDoesNotThrow { rootService.gameService.endGame() }

        assertEquals(17, p0.score)
        assertEquals(54, p1.score)
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