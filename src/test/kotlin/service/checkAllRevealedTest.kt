package service

import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.*

/**
 * Class for testing the [GameService.checkAllRevealed] method of the [GameService]
 */
class CheckAllRevealedTest {
    /** The [RootService] used for testing. Will be initialized before each test */
    private lateinit var rootService: RootService
    /** The [TestRefreshable] used for testing. Will be initialized before each test */
    private lateinit var testRefreshable: TestRefreshable

    /**
     * Initialize service to set up the test environment. This function is executed before every test.
     */
    @BeforeTest
    fun setUp() {
        rootService = RootService()
        testRefreshable = TestRefreshable()
        rootService.addRefreshable(testRefreshable)
        rootService.gameService.startNewGame(listOf("Alice", "Bob"), false, false)
    }

    /** Test that checkAllRevealed does nothing when not all cards are revealed. */
    @Test
    fun testCheckAllRevealedNotAllRevealed() {
        val game = rootService.currentGame
        checkNotNull(game)

        val player = game.players[game.currentPlayerIndex]

        // reveal only 3 of 6 cards
        player.train[0].isRevealed = true
        player.train[1].isRevealed = true
        player.train[2].isRevealed = true

        // Test: checkAllRevealed does not trigger finalRound
        assertDoesNotThrow { rootService.gameService.checkAllRevealed() }
        assertFalse(player.finalRound)
    }

    /** Test that checkAllRevealed triggers finalRound when all cards are revealed. */
    @Test
    fun testCheckAllRevealedAllRevealed() {
        val game = rootService.currentGame
        checkNotNull(game)

        val player = game.players[game.currentPlayerIndex]

        // reveal all 6 cards
        player.train.forEach { it.isRevealed = true }

        // Test: checkAllRevealed triggers finalRound
        assertDoesNotThrow { rootService.gameService.checkAllRevealed() }
        assertTrue(player.finalRound)
    }

    /** Test checkAllRevealed with no game active. */
    @Test
    fun testCheckAllRevealedNoGame() {
        rootService.currentGame = null

        assertFailsWith<IllegalStateException> {
            rootService.gameService.checkAllRevealed()
        }
    }
}