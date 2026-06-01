package service

import entity.Card
import entity.CardSuit
import entity.CardValue
import kotlin.test.Test

/**
 * Tests that the empty default implementations of the [Refreshable] interface can be
 * called without error.
 */
class RefreshableTest {

    /** A [Refreshable] that keeps every default (empty) implementation. */
    private val refreshable = object : Refreshable {}

    private val card = Card(CardSuit.HEARTS, CardValue.ACE)

    /** Calls every default method once */
    @Test
    fun testDefaultImplementationsDoNotThrow() {
        refreshable.refreshAfterGameStart()
        refreshable.refreshAfterTurnEnd()
        refreshable.refreshAfterCardDrawn(card)
        refreshable.refreshAfterCardRevealed(0)
        refreshable.refreshAfterCardDiscarded(card)
        refreshable.refreshAfterCardSwapped(card, 0)
        refreshable.refreshAfterScoresRevealed()
        refreshable.refreshAfterRowDiscarded(0)
        refreshable.refreshAfterGameWon()
    }
}