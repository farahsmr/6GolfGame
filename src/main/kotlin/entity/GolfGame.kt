package entity
/**
 * represents current state of the game
 */
class GolfGame (var currentPlayerIndex:Int, val log : MutableList<String> = mutableListOf(),
                val players :MutableList<Player> = mutableListOf(),
                val drawPile: MutableList<Card> = mutableListOf(),
                val discardPile: MutableList<Card> = mutableListOf()){
}