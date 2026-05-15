package entity

/**
 * Represents the current state of a player's turn
 * Used to track the actions of a player and what they are allowed to do next
 */
    enum class GameState {
        // The first round
        MUST_REVEAL_TWO,
        MUST_REVEAL_ONE,
        NONE_REVEALED,
        ONE_REVEALED,
        MUST_END_TURN,
        DREW_FROM_DISCARD,

    }
