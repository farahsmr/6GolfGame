package entity
/**
 * information about a player in game : the name , score which is initialised by 0 as well
 * as cards in hand and in his train
 */
class Player (val playerName : String, var score:Int =0 ) {
    var hand: Card? = null
    val train: MutableList<Card> = mutableListOf()
}