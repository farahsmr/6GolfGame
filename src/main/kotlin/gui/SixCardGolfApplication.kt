package gui

import service.RootService
import service.Refreshable
import tools.aqua.bgw.core.BoardGameApplication

/**
 * Implementation of the BGW [BoardGameApplication] for the card game "6 Card Golf"
 */
class SixCardGolfApplication : BoardGameApplication("6 Card Golf"), Refreshable {

    // Central service from which all others are created/accessed
    private val rootService: RootService = RootService()

    // This is where the actual game takes place
    private val gameScene: SixCardGolfGameScene = SixCardGolfGameScene(rootService)

    // This menu scene is shown after the game ends
    private val resultMenuScene: ResultMenuScene = ResultMenuScene(rootService)

    // This menu scene is shown to configure the game
    private val menuScene: MenuScene = MenuScene(rootService)

    // This menu scene is shown after application start
    private val mainScene: MainScene = MainScene(rootService).apply {
        startButton.onMouseClicked = {
            this@SixCardGolfApplication.showMenuScene(menuScene)
        }
        exitButton.onMouseClicked = {
            exit()
        }
    }

    init {
        menuScene.apply {
            exitButton.onMouseClicked = {
                this@SixCardGolfApplication.showMenuScene(mainScene)
            }
        }

        resultMenuScene.apply {
            playAgainButton.onMouseClicked = {
                this@SixCardGolfApplication.showMenuScene(mainScene)
            }
            exitButton.onMouseClicked = {
                exit()
            }
        }

        rootService.addRefreshables(
            this,
            gameScene,
            mainScene,
            menuScene,
            resultMenuScene
        )

        this.showGameScene(gameScene)
        this.showMenuScene(mainScene, 0)
    }

    override fun refreshAfterGameStart() {
        this.hideMenuScene()
    }

    override fun refreshAfterGameWon() {
        this.showMenuScene(resultMenuScene)
    }
}