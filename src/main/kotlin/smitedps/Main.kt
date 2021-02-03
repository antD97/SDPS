package smitedps

import java.awt.Dimension
import java.awt.Toolkit
import javax.swing.*

fun main() {
    PopupUncaughtExceptionHandler.set()
    val dpsTracker = DPSTracker()

    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

    SwingUtilities.invokeAndWait {
        PopupUncaughtExceptionHandler.set()

        JFrame("Smite DPS Calculator - antD").apply {
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE

            add(MainPanel(dpsTracker))
            pack()

            minimumSize = Dimension(size.width - 275, size.height - 75)
            setLocation(
                Toolkit.getDefaultToolkit().screenSize.width - this.width - 10,
                10)
            isAlwaysOnTop = true
            isVisible = true
        }
    }

    dpsTracker.run()
}
