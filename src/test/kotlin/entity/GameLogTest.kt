package entity

import kotlin.test.*

/**
 * Test cases for [GameLog].
 */
class GameLogTest {

    private val log1 = GameLog()
    private val log2 = GameLog(entry = "farah discarded a card")

    /**
     * Tests if the GameLog's initial entry empty
     */
    @Test
    fun testGameLogEntryDefault() {
        assertEquals(expected = "", actual = log1.entry)
    }

    /**
     * Tests if a GameLog's entry is correctly set
     */
    @Test
    fun testGameLogEntry() {
        assertEquals(expected = "farah discarded a card", actual = log2.entry)
    }
}