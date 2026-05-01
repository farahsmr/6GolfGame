package entity

import kotlin.test.*

/**
 * Test cases for [GolfGame].
 */
class GolfGameTest {

    private val game1 = GolfGame(currentPlayerIndex = 0)
    private val game2 = GolfGame(currentPlayerIndex = 3)

    /**
     * Tests if current player index is correctly set
     */
    @Test
    fun testCurrentPlayerIndex() {
        assertEquals(0, game1.currentPlayerIndex)
        assertEquals( 3, game2.currentPlayerIndex)
    }

    /**
     * Tests if GolfGame's players list is initially empty
     */
    @Test
    fun testPlayersInitial() {
        assertTrue(game1.players.isEmpty())
    }

    /**
     * Tests if GolfGame's drawPile is initially empty
     */
    @Test
    fun testDrawPileInitial() {
        assertTrue(game1.drawPile.isEmpty())
    }

    /**
     * Tests if GolfGame's discard pile is initially empty
     */
    @Test
    fun testDiscardPileInitial() {
        assertTrue(game1.discardPile.isEmpty())
    }

    /**
     * Tests if GolfGame's gameLog is correctly set
     */
    @Test
    fun testGameLog() {
        assertEquals("", game1.gameLog.entry)
    }
}