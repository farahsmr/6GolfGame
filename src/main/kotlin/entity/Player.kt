package entity

/**
 * Entity to represent a player in the game "6 Card Golf". Besides having a [playerName] and a
 * [score], the player has a [hand] and a [train].
 *
 * @property playerName the name of the player
 * @property score the score of the player, initially set to 0
 * @property finalRound whether the player has triggered the final round
 * @property gameState the current state of the player's turn, initially [GameState.MUST_REVEAL_TWO]
 * @property hand the card currently in the player's hand, null if no card is held
 * @property train the list of up to 6 cards in the player's 2x3 grid
 */
class Player(val playerName: String) {
    var score: Int = 0
    var finalRound: Boolean = false
    var gameState: GameState = GameState.MUST_REVEAL_TWO
    var hand: Card? = null
    val train: MutableList<Card> = mutableListOf()
}