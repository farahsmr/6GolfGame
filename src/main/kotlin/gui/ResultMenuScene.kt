package gui

import service.RootService
import service.Refreshable
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.core.Color
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual

/**
 * [MenuScene] shown after the game ends with scores and winner.
 *
 * @param rootService [RootService] instance to access the service methods and entity layer
 */
class ResultMenuScene(private val rootService: RootService) : MenuScene(1920, 1080), Refreshable {

    /** Dark overlay for the center panel */
    private val panelBackground = Label(
        posX = 560, posY = 100,
        width = 800, height = 700,
        visual = ColorVisual(10, 40, 10, 220)
    )

    /** Trophy icon label */
    private val trophyLabel = Label(
        posX = 560, posY = 120,
        width = 800, height = 80,
        text = "🏆",
        font = Font(size = 60)
    )

    /** Winner label */
    private val winnerLabel = Label(
        posX = 560, posY = 200,
        width = 800, height = 80,
        text = "Player 1 Wins!",
        font = Font(size = 48, fontWeight = Font.FontWeight.BOLD, color = Color(255, 215, 0))
    )

    /** Divider line */
    private val dividerLabel = Label(
        posX = 600, posY = 290,
        width = 720, height = 3,
        visual = ColorVisual(100, 180, 100)
    )

    /** Score board header */
    private val scoreHeader = Label(
        posX = 560, posY = 305,
        width = 800, height = 40,
        text = "FINAL SCORES",
        font = Font(size = 20, fontWeight = Font.FontWeight.BOLD, color = Color(100, 200, 100))
    )

    /** Score label backgrounds */
    private val scoreBg0 = Label(
        posX = 590, posY = 355, width = 740, height = 55,
        visual = ColorVisual(20, 80, 20, 200)
    )
    private val scoreBg1 = Label(
        posX = 590, posY = 420, width = 740, height = 55,
        visual = ColorVisual(15, 60, 15, 200)
    )
    private val scoreBg2 = Label(
        posX = 590, posY = 485, width = 740, height = 55,
        visual = ColorVisual(15, 60, 15, 200)
    ).apply { isVisible = false }
    private val scoreBg3 = Label(
        posX = 590, posY = 550, width = 740, height = 55,
        visual = ColorVisual(15, 60, 15, 200)
    ).apply { isVisible = false }

    /** Score labels */
    private val scoreLabel0 = Label(
        posX = 600, posY = 360,
        width = 720, height = 45,
        text = "⭐1-   Player 1 :                         0 pts",
        font = Font(size = 22, fontWeight = Font.FontWeight.BOLD, color = Color(255, 215, 0))
    )
    private val scoreLabel1 = Label(
        posX = 600, posY = 425,
        width = 720, height = 45,
        text = "2-      Player 2 :                        0 pts",
        font = Font(size = 22, color = Color(220, 220, 220))
    )
    private val scoreLabel2 = Label(
        posX = 600, posY = 490,
        width = 720, height = 45,
        text = "3-      Player 3 :                         0 pts",
        font = Font(size = 22, color = Color(220, 220, 220))
    ).apply { isVisible = false }
    private val scoreLabel3 = Label(
        posX = 600, posY = 555,
        width = 720, height = 45,
        text = "4-      Player 4 :                         0 pts",
        font = Font(size = 22, color = Color(220, 220, 220))
    ).apply { isVisible = false }

    private val scoreLabels = listOf(scoreLabel0, scoreLabel1, scoreLabel2, scoreLabel3)
    private val scoreBgs = listOf(scoreBg0, scoreBg1, scoreBg2, scoreBg3)

    /** Second divider */
    private val dividerLabel2 = Label(
        posX = 600, posY = 625,
        width = 720, height = 3,
        visual = ColorVisual(100, 180, 100)
    )

    /** Play Again button */
    val playAgainButton = Button(
        posX = 720, posY = 680,
        width = 200, height = 60,
        visual = ImageVisual("play-again-button.png")
    )

    /** Exit button */
    val exitButton = Button(
        posX = 970, posY = 680,
        width = 200, height = 60,
        visual = ImageVisual("exit-button.png")
    )

    init {
        background = ImageVisual("background.png")
        backgroundOpacity = 1.0

        addComponents(
            panelBackground,
            trophyLabel,
            winnerLabel,
            dividerLabel,
            scoreHeader,
            scoreBg0, scoreBg1, scoreBg2, scoreBg3,
            scoreLabel0, scoreLabel1, scoreLabel2, scoreLabel3,
            dividerLabel2,
            playAgainButton, exitButton
        )
    }

    /**
     * Called after game is won: updates scores and winner label
     */
    override fun refreshAfterGameWon() {
        val game = rootService.currentGame ?: return

        // Sort players by score ascending (lowest score wins)
        val sortedPlayers = game.players.sortedBy { it.score }

        // Update winner label
        winnerLabel.text = "${sortedPlayers[0].playerName} Wins!"

        // Update score labels
        scoreLabels.forEachIndexed { index, label ->
            val bg = scoreBgs[index]
            if (index < sortedPlayers.size) {
                val player = sortedPlayers[index]
                val rank = index + 1
                val star = if (index == 0) "⭐" else "   "
                label.text = "$rank $star  ${player.playerName.padEnd(20)} ${player.score} pts"
                label.isVisible = true
                bg.isVisible = true

                // Winner name becomes gold
                if (index == 0) {
                    label.font = Font(size = 22, fontWeight = Font.FontWeight.BOLD, color = Color(255, 215, 0))
                } else {
                    label.font = Font(size = 22, color = Color(220, 220, 220))
                }
            } else {
                label.isVisible = false
                bg.isVisible = false
            }
        }
    }
}