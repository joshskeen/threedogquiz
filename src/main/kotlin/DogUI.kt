import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.swing.Swing
import java.awt.Dimension
import java.awt.Image
import javax.swing.*

object DogUI {

    lateinit var correctAnswer: String

    private const val WIDTH = 700
    private val content = JPanel().apply {
        minimumSize = Dimension(WIDTH, WIDTH)
    }

    val frame = JFrame("ThreeDogQuiz").apply {
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        isVisible = true
        isResizable = false
        contentPane = content
        minimumSize = Dimension(WIDTH, 300)
    }

    fun loadQuiz() {
        launch(Swing) {
            val images = JPanel()
            content.removeAll()
            content.add(images)
            val correctIndex = (0..2).shuffled().last()
            (0 until 3).forEach {
                val dogImage = DogData.randomDogImage().await()
                if (it == correctIndex) {
                    correctAnswer = dogImage.first
                    val dogLabel = JLabel()
                    dogLabel.text = "choose the $correctAnswer"
                    content.add(JPanel().add(dogLabel))
                }
                val dogButton = createDogButton(dogImage.second)
                dogButton.name = dogImage.first
                images.add(dogButton)
                dogButton.addActionListener {
                    if ((it.source as JButton).name == correctAnswer) {
                        content.repaint()
                        content.validate()
                        println("Correct!")
                        loadQuiz()
                    } else {
                        println("Incorrect!")
                    }
                }
            }
            content.repaint()
            content.validate()
        }
    }

    private fun createDogButton(bytes: ByteArray): JButton {
        val imageIcon = ImageIcon(bytes)
        val scaled = imageIcon.image.getScaledInstance(200,
                200,
                Image.SCALE_DEFAULT)
        return JButton().apply {
            icon = ImageIcon(scaled)
        }
    }
}

fun main(args: Array<String>) {
    DogUI.loadQuiz()
}