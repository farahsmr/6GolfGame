package service


import entity.GameState
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.*

/**
 * Class for testing the [PlayerActionService.revealCard] method
 */
class RevealCardTest {
    private lateinit var rootService: RootService
    private lateinit var testRefreshable: TestRefreshable

    @BeforeTest
    fun setUp() {
        rootService = RootService()
        testRefreshable = TestRefreshable()
        rootService.addRefreshable(testRefreshable)
        rootService.gameService.startNewGame(playerNames = listOf("Alice", "Bob"), isRandom = false, isTestMode = false)
    }

    /** Test revealing a card in MUST_REVEAL_TWO state */
    @Test
    fun testRevealCardMustRevealTwo() {
        val game = rootService.currentGame
        checkNotNull(game)

        val player = game.players[game.currentPlayerIndex]
        player.gameState = GameState.MUST_REVEAL_TWO

        // Test: Revealing a card does not throw
        assertDoesNotThrow { rootService.playerActionService.revealCard(0) }

        // Test: Card is revealed
        assertTrue(player.train[0].isRevealed)

        // Test: State updated to MUST_REVEAL_ONE
        assertEquals(GameState.MUST_REVEAL_ONE, player.gameState)

        // Test: Refresh was called
        assertTrue(testRefreshable.refreshAfterCardRevealedCalled)
    }

    /** Test revealing a card in NONE_REVEALED state */
    @Test
    fun testRevealCardNoneRevealed() {
        val game = rootService.currentGame
        checkNotNull(game)

        val player = game.players[game.currentPlayerIndex]
        player.gameState = GameState.NONE_REVEALED

        // Test: Revealing a card does not throw
        assertDoesNotThrow { rootService.playerActionService.revealCard(0) }

        // Test: State updated to ONE_REVEALED
        assertEquals(GameState.ONE_REVEALED, player.gameState)
    }

    /** Test revealing an already revealed card. */
    @Test
    fun testRevealCardAlreadyRevealed() {
        val game = rootService.currentGame
        checkNotNull(game)

        val player = game.players[game.currentPlayerIndex]
        player.gameState = GameState.NONE_REVEALED
        player.train[0].isRevealed = true

        // Test: Revealing an already revealed card throws
        assertThrows<IllegalStateException> {
            rootService.playerActionService.revealCard(0)
        }
    }

    /** Test revealing second card goes to MUST_END_TURN */
    @Test
    fun testRevealCardOneRevealedToMustEndTurn() {
        val game = rootService.currentGame
        checkNotNull(game)

        val player = game.players[game.currentPlayerIndex]
        player.gameState = GameState.ONE_REVEALED

        assertDoesNotThrow { rootService.playerActionService.revealCard(0) }
        assertEquals(GameState.MUST_END_TURN, player.gameState)
    }

    /** Test revealCard in wrong state  */
    @Test
    fun testRevealCardWrongState() {
        val game = rootService.currentGame
        checkNotNull(game)

        val player = game.players[game.currentPlayerIndex]
        player.gameState = GameState.MUST_END_TURN

        assertThrows<IllegalStateException> {
            rootService.playerActionService.revealCard(0)
        }
    }
    /** Test revealing a card with no game active */
    @Test
    fun testRevealCardNoGame() {
        rootService.currentGame = null

        assertThrows<IllegalStateException> {
            rootService.playerActionService.revealCard(0)
        }
    }
}