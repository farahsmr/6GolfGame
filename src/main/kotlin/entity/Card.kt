package entity
/**
 * Data class for the main element of the 6 card Golf game : cards.
 *
 * It is characterized by a [CardSuit], a [CardValue] and a revealed status
 *
 * @property suit the suit of the card
 * @property value the value of the card
 * @property isRevealed whether the card is face up or not, initially set to false
 */

data class Card (var isRevealed: Boolean =false , val suit: CardSuit, val value: CardValue)