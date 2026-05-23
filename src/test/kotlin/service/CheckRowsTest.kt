package service

import entity.Card
import entity.CardSuit
import entity.CardValue
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.*

/**
 * Class for testing the [GameService.checkRows] method and [GameService.removeTripleRow] indirectly
 */
class CheckRowsTest {
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

    /** Test that a row with identical values is removed */
    @Test
    fun testCheckRowsRemovesRow() {
        val game = rootService.currentGame
        checkNotNull(game)

        val player = game.players[0]

        // Set row 0 to all kings
        player.train[0] = Card(CardSuit.CLUBS, CardValue.KING)
        player.train[1] = Card(CardSuit.SPADES, CardValue.KING)
        player.train[2] = Card(CardSuit.HEARTS, CardValue.KING)

        val trainSizeBefore = player.train.size

        // Test: checkRows does not throw
        assertDoesNotThrow { rootService.gameService.checkRows(player) }

        // Test: Row was removed
        assertEquals(trainSizeBefore - 3, player.train.size)

        // Test: Cards were added to discard pile
        assertTrue(testRefreshable.refreshAfterRowDiscardedCalled)
    }


    /** Test that a row without identical values is not removed */
    @Test
    fun testCheckRowsNoRemoval() {
        val game = rootService.currentGame
        checkNotNull(game)

        val player = game.players[0]

        // Set row 0 to different values
        player.train[0] = Card(CardSuit.CLUBS, CardValue.ACE)
        player.train[1] = Card(CardSuit.SPADES, CardValue.KING)
        player.train[2] = Card(CardSuit.HEARTS, CardValue.QUEEN)

        val trainSizeBefore = player.train.size

        assertDoesNotThrow { rootService.gameService.checkRows(player) }

        // Test: No row was removed
        assertEquals(trainSizeBefore, player.train.size)
    }

    /** Test that row 1 with identical values is removed */
    @Test
    fun testCheckRowsRemovesSecondRow() {
        val game = rootService.currentGame
        checkNotNull(game)

        val player = game.players[0]

        // Set row 1 to all aces
        player.train[3] = Card(CardSuit.CLUBS, CardValue.ACE)
        player.train[4] = Card(CardSuit.SPADES, CardValue.ACE)
        player.train[5] = Card(CardSuit.HEARTS, CardValue.ACE)

        val trainSizeBefore = player.train.size

        // Test: checkRows does not throw
        assertDoesNotThrow { rootService.gameService.checkRows(player) }

        // Test: Row was removed
        assertEquals(trainSizeBefore - 3, player.train.size)
    }

    /** Test both rows removed when both have identical values */
    @Test
    fun testCheckRowsBothRowsRemoved() {
        val game = rootService.currentGame
        checkNotNull(game)

        val player = game.players[0]

        // Set both rows to identical values
        player.train[0] = Card(CardSuit.CLUBS, CardValue.ACE)
        player.train[1] = Card(CardSuit.SPADES, CardValue.ACE)
        player.train[2] = Card(CardSuit.HEARTS, CardValue.ACE)
        player.train[3] = Card(CardSuit.CLUBS, CardValue.KING)
        player.train[4] = Card(CardSuit.SPADES, CardValue.KING)
        player.train[5] = Card(CardSuit.HEARTS, CardValue.KING)

        assertDoesNotThrow { rootService.gameService.checkRows(player) }

        // Both rows removed
        assertEquals(0, player.train.size)
    }
}