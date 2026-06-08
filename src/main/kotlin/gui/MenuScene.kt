package gui

import service.RootService
import service.Refreshable
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.CheckBox
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import tools.aqua.bgw.core.Color


/**
 * [MenuScene] that is used for configuring and starting a new game.
 *
 * @param rootService [RootService] instance to access the service methods and entity layer
 */
class MenuScene(private val rootService: RootService) : MenuScene(1920, 1080), Refreshable {

    // Card visuals from card_deck
    private val clubsVisual = ImageVisual("card_deck.png", offsetX = 0, offsetY = 0, width = 130, height = 200)
    private val heartsVisual = ImageVisual("card_deck.png", offsetX = 0, offsetY = 400, width = 130, height = 200)
    private val diamondsVisual = ImageVisual("card_deck.png", offsetX = 0, offsetY = 200, width = 130, height = 200)
    private val spadesVisual = ImageVisual("card_deck.png", offsetX = 0, offsetY = 600, width = 130, height = 200)
    private var playerCount = 2

    // Player 1 card
    private val card1 = Button(
        posX = 400, posY = 200,
        width = 250, height = 350,
        visual = clubsVisual
    )

    // Player 2 card
    private val card2 = Button(
        posX = 700, posY = 200,
        width = 250, height = 350,
        visual = heartsVisual,
    )
    private val backVisual = ImageVisual("card_deck.png", offsetX = 260, offsetY = 800, width = 130, height = 200)

    // Player 3 card, shows + initially
    private val card3 = Button(
        posX = 1000, posY = 200,
        width = 250, height = 350,
        font = Font(size = 120, fontWeight = Font.FontWeight.BOLD, color = Color(0, 0, 0)),
        text = "+",
        visual = backVisual
    )

    // Player 4 card, shows + initially
    private val card4 = Button(
        posX = 1300, posY = 200,
        width = 250, height = 350,
        font = Font(size = 120, fontWeight = Font.FontWeight.BOLD, color = Color(0, 0, 0)),
        text = "+",
        visual = backVisual

    )


    // Player name inputs
    private val player1Input: TextField = TextField(
        width = 200, height = 65,
        posX = 425, posY = 570,
        text = "Player 1",
        font = Font(size = 25)
    )

    private val player2Input: TextField = TextField(
        width = 200, height = 65,
        posX = 725, posY = 570,
        text = "Player 2",
        font = Font(size = 25)
    )

    private val player3Input: TextField = TextField(
        width = 200, height = 65,
        posX = 1025, posY = 570,
        text = "",
        font = Font(size = 25)
    ).apply { isVisible = false }

    private val player4Input: TextField = TextField(
        width = 200, height = 65,
        posX = 1325, posY = 570,
        text = "",
        font = Font(size = 25)
    ).apply { isVisible = false }


    // Bottom buttons
    private val testModeCheckBox = CheckBox(
        width = 200, height = 50,
        posX = 690, posY = 650,
        text = "Test Mode",
        font = Font(size = 30, fontWeight = Font.FontWeight.BOLD, color = Color(255, 255, 255))
    )

    private val randomOrderCheckBox = CheckBox(
        width = 270, height = 50,
        posX = 990, posY = 650,
        text = "Random Order",
        font = Font(size = 30, fontWeight = Font.FontWeight.BOLD, color = Color(255, 255, 255))
    )

    val exitButton = Button(
        width = 250, height = 110,
        posX = 700, posY = 750,
        visual = ImageVisual("exit-button.png")
    )

    private val playButton = Button(
        width = 250, height = 110,
        posX = 1000, posY = 750,
        visual = ImageVisual("play-button.png")
    ).apply {
        onMouseClicked = {
            val players = mutableListOf(player1Input.text.trim(), player2Input.text.trim())
            if (playerCount >= 3) players.add(player3Input.text.trim())
            if (playerCount >= 4) players.add(player4Input.text.trim())

            rootService.gameService.startNewGame(
                players.filter { it.isNotBlank() },
                randomOrderCheckBox.isChecked,
                testModeCheckBox.isChecked
            )
        }
    }
    /**
     * Resets the [MenuScene] to its default state with 2 players
     * Called when returning to the menu after a game ends
     */
    fun reset() {
        playerCount = 2

        // Reset card3 to + state and re-register click handler
        card3.visual = backVisual
        card3.text = "+"
        card3.onMouseClicked = {
            if (playerCount == 2) {
                card3.visual = diamondsVisual
                card3.text = ""
                player3Input.isVisible = true
                playerCount = 3
            }
        }

        // Reset card4 to + state and re-register click handler
        card4.visual = backVisual
        card4.text = "+"
        card4.onMouseClicked = {
            if (playerCount == 3) {
                card4.visual = spadesVisual
                card4.text = ""
                player4Input.isVisible = true
                playerCount = 4
                card4.onMouseClicked = null
            }
        }

        // Hide and clear player 3 input
        player3Input.isVisible = false
        player3Input.text = ""

        // Hide and clear player 4 input
        player4Input.isVisible = false
        player4Input.text = ""
    }

    init {
        background = ImageVisual("background.png")
        backgroundOpacity = 1.0

        card3.onMouseClicked = {
            if (playerCount == 2) {
                card3.visual = diamondsVisual
                card3.text = ""
                player3Input.isVisible = true
                playerCount = 3
            }
        }

        card4.onMouseClicked = {
            if (playerCount == 3) {
                card4.visual = spadesVisual
                card4.text = ""
                player4Input.isVisible = true
                playerCount = 4
                card4.onMouseClicked = null
            }
        }


        addComponents(
            card1, card2, card3, card4,
            player1Input, player2Input, player3Input, player4Input,
            testModeCheckBox, randomOrderCheckBox,
            playButton, exitButton
        )
    }
}