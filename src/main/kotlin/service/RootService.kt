package service

import entity.GolfGame


/**
 * The root service class is responsible for managing services and the entity layer reference.
 * This class acts as a central hub for every other service within the application.
 *
 */
class RootService{
    /**
     * Main class of the service layer for the War card game. Provides access
     * to all other service classes and holds the [currentGame] state for these
     * services to access.
     */

        /** The connected [GameService] for this rootService */
        val gameService = GameService(this)
        /** The connected [PlayerActionService] for this rootService */
        val playerActionService = PlayerActionService(this)


        /**
         * The currently active game. Can be `null`, if no game has started yet.
         */
        var currentGame : GolfGame? = null

        /**
         * Adds the provided [newRefreshable] to all services connected
         * to this root service
         */
        fun addRefreshable(newRefreshable: Refreshable) {
            gameService.addRefreshable(newRefreshable)
            playerActionService.addRefreshable(newRefreshable)
        }

        /**
         * Adds each of the provided [newRefreshables] to all services
         * connected to this root service
         */
        fun addRefreshables(vararg newRefreshables: Refreshable) {
            newRefreshables.forEach { addRefreshable(it) }
        }

    }