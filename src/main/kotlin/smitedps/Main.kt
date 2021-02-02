package smitedps

import java.awt.Dimension
import javax.swing.*

fun main() {
    PopupUncaughtExceptionHandler.set()
    val dpsTracker = DPSTracker()

    SwingUtilities.invokeAndWait {
        PopupUncaughtExceptionHandler.set()

        JFrame("Smite DPS Calculator - antD").apply {
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            add(MainPanel(dpsTracker))
            pack()
            minimumSize = Dimension(size.width - 175, size.height - 100)
            setLocationRelativeTo(null)
            isVisible = true
        }
    }

    dpsTracker.run()
}
