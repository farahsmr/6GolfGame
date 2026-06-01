package gui

import service.RootService
import service.Refreshable
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.Color
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual

/**
 * [MenuScene] shown after the game ends, displaying the final scores and the winner.
 *
 * Lowest score wins (standard Golf scoring). Only the winner — or all tied winners —
 * is highlighted in gold; every other player is shown in a plain row.
 *
 * @param rootService [RootService] instance to access the service methods and entity layer
 */
class ResultMenuScene(private val rootService: RootService) : MenuScene(1920, 1080), Refreshable {

    private val goldBg = ColorVisual(74, 58, 12, 235)
    private val neutralBg = ColorVisual(22, 42, 26, 200)
    private val goldText = Color(255, 215, 0)
    private val neutralText = Color(210, 218, 210)
    private val lineColor = ColorVisual(170, 140, 70)

    /** Central translucent panel that frames the results. */
    private val panel = Label(
        posX = 560, posY = 110,
        width = 800, height = 840,
        visual = ColorVisual(8, 26, 14, 235)
    )

    /** Small "GAME OVER" caption at the top of the panel. */
    private val gameOverLabel = Label(
        posX = 560, posY = 140,
        width = 800, height = 40,
        text = "G A M E   O V E R",
        font = Font(size = 24, fontWeight = Font.FontWeight.BOLD, color = Color(130, 180, 130)),
        alignment = Alignment.CENTER
    )

    /** Trophy icon. */
    private val trophyLabel = Label(
        posX = 560, posY = 180,
        width = 800, height = 90,
        text = "🏆",
        font = Font(size = 70),
        alignment = Alignment.CENTER
    )

    /** Winner headline. */
    private val winnerLabel = Label(
        posX = 560, posY = 280,
        width = 800, height = 70,
        text = "",
        font = Font(size = 46, fontWeight = Font.FontWeight.BOLD, color = goldText),
        alignment = Alignment.CENTER
    )

    private val divider1 = Label(
        posX = 620, posY = 370, width = 680, height = 2,
        visual = lineColor
    )

    /** "FINAL SCORES" section header. */
    private val scoreHeader = Label(
        posX = 560, posY = 388,
        width = 800, height = 36,
        text = "FINAL SCORES",
        font = Font(size = 22, fontWeight = Font.FontWeight.BOLD, color = Color(150, 200, 150)),
        alignment = Alignment.CENTER
    )

    // One row per player: a background, the name (left) and the score (right).
    private val rowBgs = mutableListOf<Label>()
    private val nameLabels = mutableListOf<Label>()
    private val scoreLabels = mutableListOf<Label>()

    private val divider2 = Label(
        posX = 620, posY = 780, width = 680, height = 2,
        visual = lineColor
    )

    /** Play Again button (wired up by [SixCardGolfApplication]). */
    val playAgainButton = Button(
        posX = 690, posY = 820,
        width = 220, height = 70,
        visual = ImageVisual("play-again-button.png")
    )

    /** Exit button (wired up by [SixCardGolfApplication]). */
    val exitButton = Button(
        posX = 1010, posY = 820,
        width = 220, height = 70,
        visual = ImageVisual("exit-button.png")
    )

    init {
        background = ImageVisual("background.png")
        backgroundOpacity = 1.0

        addComponents(panel, gameOverLabel, trophyLabel, winnerLabel, divider1, scoreHeader)

        // Build the four score rows up front; visibility/colour is set per game.
        for (i in 0 until 4) {
            val rowY = 450 + i * 80

            val bg = Label(
                posX = 620, posY = rowY, width = 680, height = 66,
                visual = neutralBg
            ).apply { isVisible = false }

            val name = Label(
                posX = 660, posY = rowY, width = 420, height = 66,
                text = "",
                font = Font(size = 28, color = neutralText),
                alignment = Alignment.CENTER_LEFT
            ).apply { isVisible = false }

            val score = Label(
                posX = 1100, posY = rowY, width = 180, height = 66,
                text = "",
                font = Font(size = 30, color = neutralText),
                alignment = Alignment.CENTER_RIGHT
            ).apply { isVisible = false }

            rowBgs += bg
            nameLabels += name
            scoreLabels += score
            addComponents(bg, name, score)
        }

        addComponents(divider2, playAgainButton, exitButton)
    }

    /**
     * Called after the game is won: fills in the winner headline and the score rows,
     * sorted from lowest (winner) to highest score. All players sharing the lowest
     * score are treated as winners and shown in gold.
     */
    override fun refreshAfterGameWon() {
        val game = rootService.currentGame ?: return

        // Lowest score wins; there may be a tie.
        val sortedPlayers = game.players.sortedBy { it.score }
        val bestScore = sortedPlayers.first().score
        val winners = sortedPlayers.filter { it.score == bestScore }

        winnerLabel.text = if (winners.size == 1) {
            "${winners.first().playerName} wins!"
        } else {
            "${winners.joinToString(", ") { it.playerName }} win!"
        }

        for (i in 0 until 4) {
            val show = i < sortedPlayers.size
            rowBgs[i].isVisible = show
            nameLabels[i].isVisible = show
            scoreLabels[i].isVisible = show

            if (show) {
                val player = sortedPlayers[i]
                val isWinner = player.score == bestScore

                rowBgs[i].visual = if (isWinner) goldBg else neutralBg
                val color = if (isWinner) goldText else neutralText
                val weight = if (isWinner) Font.FontWeight.BOLD else Font.FontWeight.NORMAL

                nameLabels[i].text = player.playerName
                nameLabels[i].font = Font(size = 28, fontWeight = weight, color = color)

                scoreLabels[i].text =
                    if (player.score == Int.MIN_VALUE) "-∞ pts" else "${player.score} pts"
                scoreLabels[i].font = Font(size = 30, fontWeight = weight, color = color)
            }
        }
    }
}