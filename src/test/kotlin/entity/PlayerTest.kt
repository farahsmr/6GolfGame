package entity

import kotlin.test.*

/**
 * Test cases for [Player]
 */
class PlayerTest {
    private val player1 = Player("farah")
    private val player2 = Player("max").apply { score = 5 }
    private val player3 = Player("")
    private val player4 = Player("max").apply { score = -2 }

    /**
     * Tests if a player's name is correctly set
     */
    @Test
    fun testPlayerName() {
        assertEquals("farah", player1.playerName)
        assertEquals("", player3.playerName)
    }

    /**
     * Tests if a player's score is correctly set
     */
    @Test
    fun testPlayerScore() {
        assertEquals(0, player1.score)
        assertEquals(5, player2.score)
        assertEquals(-2, player4.score)
    }

    /**
     * Tests if a player's hand is initially null
     */
    @Test
    fun testPlayerHand() {
        assertNull(player1.hand)
    }

    /**
     * Tests if a player's train is initially empty
     */
    @Test
    fun testPlayerTrain() {
        assertTrue(player1.train.isEmpty())
    }

    /**
     * Tests if a player's game state is initially MUST_REVEAL_TWO
     */
    @Test
    fun testPlayerGameState() {
        assertEquals(GameState.MUST_REVEAL_TWO, player1.gameState)
    }

    /**
     * Tests if a player's finalRound is initially false
     */
    @Test
    fun testPlayerFinalRound() {
        assertFalse(player1.finalRound)
    }
}