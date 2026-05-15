package service

import entity.Card

/**
 * This interface provides a mechanism for the service layer classes to communicate
 * (usually to the GUI classes) that certain changes have been made to the entity
 * layer, so that the user interface can be updated accordingly.
 *
 * Default (empty) implementations are provided for all methods, so that implementing
 * GUI classes only need to react to events relevant to them.
 *
 * @see AbstractRefreshingService
 */
interface Refreshable{
    /**
     * Called after a new game started
     */
    fun refreshAfterGameStart(){}
    /**
     * Called after the current player's turn has ended
     */
    fun refreshAfterTurnEnd(){}
    /**
     * Called after a card has been drawn from the draw pile
     * @param card The card that was drawn
     */
    fun refreshAfterCardDrawn(card : Card){}
    /**
     * Called after a card has been revealed in the current player's train
     * @param cardIndex The index of the revealed card
     */
    fun refreshAfterCardRevealed(cardIndex : Int){}

    /**
     * Called after a card has been discarded in the discard pile
     * @param card The card that was discarded
     */
    fun refreshAfterCardDiscarded(card : Card){}
    /**
     * Called after a card has been swapped with one of the player's cards
     * @param newCard The new card placed in the player's train
     * @param cardIndex The index of the swapped card in the player's train
     */
    fun refreshAfterCardSwapped(newCard : Card,cardIndex :Int){}
    /**
     * Called after the scores have been revealed at the end of the game
     */
    fun refreshAfterScoresRevealed(){}
    /**
     * Called after a row of identical cards has been discarded from a player's train
     * @param rowIndex The index of the discarded row
     */
    fun refreshAfterRowDiscarded(rowIndex : Int){}
    /**
     * Called after the game has been won and a winner is determined
     */
    fun refreshAfterGameWon(){}

}