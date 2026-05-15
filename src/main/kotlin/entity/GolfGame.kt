package entity
/**
 * Entity class that represents a game state of "6 Card Golf". The game consists of
 * 2 to 4 players, a draw pile, a discard pile, a [currentPlayerIndex] to
 * track turns, and a log
 *
 * @property currentPlayerIndex the index of the current player
 * @property log describes the log of the game
 * @property players the list of players in the game
 * @property drawPile the pile of cards to draw from
 * @property discardPile the pile of discarded cards
*/

class GolfGame (var currentPlayerIndex:Int, val log: MutableList<String> = mutableListOf(),
                val players :MutableList<Player> = mutableListOf(),
                val drawPile: MutableList<Card> = mutableListOf(),
                val discardPile: MutableList<Card> = mutableListOf())
