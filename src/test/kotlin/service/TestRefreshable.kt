package service

import entity.Card

/**
 * [Refreshable] implementation that refreshes nothing, but remembers
 * if a refresh method has been called (since last [reset])
 */
class TestRefreshable : Refreshable {

    var refreshAfterGameStartCalled: Boolean = false
        private set

    var refreshAfterTurnEndCalled: Boolean = false
        private set

    var refreshAfterCardDrawnCalled: Boolean = false
        private set

    var refreshAfterCardRevealedCalled: Boolean = false
        private set

    var refreshAfterCardDiscardedCalled: Boolean = false
        private set

    var refreshAfterCardSwappedCalled: Boolean = false
        private set

    var refreshAfterScoresRevealedCalled: Boolean = false
        private set

    var refreshAfterRowDiscardedCalled: Boolean = false
        private set

    var refreshAfterGameWonCalled: Boolean = false
        private set

    /**
     * Resets all called properties to false
     */
    fun reset() {
        refreshAfterGameStartCalled = false
        refreshAfterTurnEndCalled = false
        refreshAfterCardDrawnCalled = false
        refreshAfterCardRevealedCalled = false
        refreshAfterCardDiscardedCalled = false
        refreshAfterCardSwappedCalled = false
        refreshAfterScoresRevealedCalled = false
        refreshAfterRowDiscardedCalled = false
        refreshAfterGameWonCalled = false
    }

    override fun refreshAfterGameStart() {
        refreshAfterGameStartCalled = true
    }

    override fun refreshAfterTurnEnd() {
        refreshAfterTurnEndCalled = true
    }

    override fun refreshAfterCardDrawn(card: Card) {
        refreshAfterCardDrawnCalled = true
    }

    override fun refreshAfterCardRevealed(cardIndex: Int) {
        refreshAfterCardRevealedCalled = true
    }

    override fun refreshAfterCardDiscarded(card: Card) {
        refreshAfterCardDiscardedCalled = true
    }

    override fun refreshAfterCardSwapped(newCard: Card, cardIndex: Int) {
        refreshAfterCardSwappedCalled = true
    }

    override fun refreshAfterScoresRevealed() {
        refreshAfterScoresRevealedCalled = true
    }

    override fun refreshAfterRowDiscarded(rowIndex: Int) {
        refreshAfterRowDiscardedCalled = true
    }

    override fun refreshAfterGameWon() {
        refreshAfterGameWonCalled = true
    }
}