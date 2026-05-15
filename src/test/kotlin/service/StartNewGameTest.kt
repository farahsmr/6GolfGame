package service

import entity.GameState
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.*

/**
 * Class for testing the [GameService.startNewGame] method of the [GameService].
 */
class StartNewGameTest {
    /** The [RootService] used for testing. Will be initialized before each test. */
    private lateinit var rootService: RootService
    /** The [TestRefreshable] used for testing. Will be initialized before each test. */
    private lateinit var testRefreshable: TestRefreshable

    /**
     * Initialize service to set up the test environment. This function is executed before every test.
     */
    @BeforeTest
    fun setUp() {
        rootService = RootService()
        testRefreshable = TestRefreshable()
        rootService.addRefreshable(testRefreshable)
    }

    /** Test starting a new game with valid players. */
    @Test
    fun testStartNewGame() {
        assertDoesNotThrow {
            rootService.gameService.startNewGame(listOf("Alice", "Bob"), false, false)
        }

        val game = rootService.currentGame
        checkNotNull(game)

        // Test: The game has been created with the correct players
        assertEquals(2, game.players.size)
        assertEquals("Alice", game.players[0].playerName)
        assertEquals("Bob", game.players[1].playerName)

        // Test: Draw pile and discard pile are correct
        assertEquals(39, game.drawPile.size)
        assertEquals(1, game.discardPile.size)

        // Test: Each player has 6 cards
        game.players.forEach { player ->
            assertEquals(6, player.train.size)
        }

        // Test: Refresh was called
        assertTrue(testRefreshable.refreshAfterGameStartCalled)
    }
    /** Test all players start with MUST_REVEAL_TWO state. */
    @Test
    fun testStartNewGamePlayerState() {
        rootService.gameService.startNewGame(listOf("Alice", "Bob"), false, false)
        val game = rootService.currentGame
        checkNotNull(game)

        game.players.forEach { player ->
            assertEquals(GameState.MUST_REVEAL_TWO, player.gameState)
            assertNull(player.hand)
            assertFalse(player.finalRound)
            assertEquals(0, player.score)
        }
    }

    /** Test starting a new game with random order */
    @Test
    fun testStartNewGameRandom() {
        assertDoesNotThrow {
            rootService.gameService.startNewGame(listOf("Alice", "Bob", "Charlie", "Dave"), true, false)
        }

        val game = rootService.currentGame
        checkNotNull(game)

        // Test: All 4 players are present
        assertEquals(4, game.players.size)
    }

    /** Test starting a new game in test mode. */
    @Test
    fun testStartNewGameTestMode() {
        assertDoesNotThrow {
            rootService.gameService.startNewGame(listOf("Alice", "Bob"), false, true)
        }

        val game = rootService.currentGame
        checkNotNull(game)

        // Test: Game was created correctly
        assertEquals(2, game.players.size)
        assertEquals(39, game.drawPile.size)
    }

    /** Test starting a new game with too few players. */
    @Test
    fun testStartNewGameTooFewPlayers() {
        assertThrows<IllegalArgumentException> {
            rootService.gameService.startNewGame(listOf("Alice"), false, false)
        }
    }

    /** Test starting a new game with too many players. */
    @Test
    fun testStartNewGameTooManyPlayers() {
        assertThrows<IllegalArgumentException> {
            rootService.gameService.startNewGame(listOf("Alice", "Bob", "Charlie", "Dave", "Eve"), false, false)
        }
    }

    /** Test starting a new game with duplicate names. */
    @Test
    fun testStartNewGameDuplicateNames() {
        assertThrows<IllegalArgumentException> {
            rootService.gameService.startNewGame(listOf("Alice", "Alice"), false, false)
        }
    }

    /** Test starting a new game with blank names. */
    @Test
    fun testStartNewGameBlankNames() {
        assertThrows<IllegalArgumentException> {
            rootService.gameService.startNewGame(listOf("Alice", ""), false, false)
        }
    }

    /** Test starting a new game when a game is already active. */
    @Test
    fun testStartNewGameAlreadyActive() {
        rootService.gameService.startNewGame(listOf("Alice", "Bob"), false, false)
        assertThrows<IllegalStateException> {
            rootService.gameService.startNewGame(listOf("Alice", "Bob"), false, false)
        }
    }
}