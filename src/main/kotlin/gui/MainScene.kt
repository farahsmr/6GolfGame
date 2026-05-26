package gui

import service.RootService
import service.Refreshable
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.core.Scene
import tools.aqua.bgw.visual.ImageVisual

/**
 * The main menu scene shown when the application starts.
 */
class MainScene(private val rootService: RootService) : MenuScene(1920, 1080), Refreshable {

    val startButton = Button(
        width = 290,
        height = 110,
        posX = 620,
        posY = 560,
        visual = ImageVisual("start-button.png"),
    )

    val exitButton = Button(
        width = 290,
        height = 110,
        posX = 1080,
        posY = 560,
        visual = ImageVisual("exit-button.png")
    )

    init {
        backgroundOpacity = 1.0
        background = ImageVisual("main.png")
        addComponents(startButton, exitButton)
    }
}