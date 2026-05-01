package entity
/**
 * Entity to represent a player in the game "6 Card Golf" . Besides having a [playerName] and a
 * [score], the player has a [hand] and a [train].
 *
 * @property playerName the name of the player
 * @property score the score of the player, initially set to 0
 * @property hand when the card currently in the player's hand, null if no card is held
 * @property train the list of cards in the player's train
 */
class Player (val playerName : String, var score:Int =0 ) {
    var hand: Card? = null
    val train: MutableList<Card> = mutableListOf()
}
