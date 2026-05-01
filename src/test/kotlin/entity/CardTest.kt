package entity

import kotlin.test.*

/**
 * Test cases for [Card]
 */
class CardTest {

    private val card1 = Card(suit = CardSuit.SPADES, value = CardValue.ACE)
    private val card2 = Card(suit = CardSuit.HEARTS, value = CardValue.JACK)

    /**
     * Tests if a card's suit is correctly set
     */
    @Test
    fun testCardSuit() {
        assertEquals(expected = CardSuit.SPADES, actual = card1.suit)
        assertEquals(expected = CardSuit.HEARTS, actual = card2.suit)
    }

    /**
     * Tests if a card's value is correctly set.
     */
    @Test
    fun testCardValue() {
        assertEquals(expected = CardValue.ACE, actual = card1.value)
        assertEquals(expected = CardValue.JACK, actual = card2.value)
    }

    /**
     * Tests if a card vis initially not revealed
     */
    @Test
    fun testCardIsRevealed() {
        assertEquals(expected = false, actual = card1.isRevealed)
        assertEquals(expected = false, actual = card2.isRevealed)
    }
}