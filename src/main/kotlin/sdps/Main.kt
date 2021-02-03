/*
 * Copyright © 2021 antD97
 * Licensed under the MIT License https://mit-license.org/
 */
package sdps

import java.awt.Dimension
import java.awt.Toolkit
import javax.swing.*

fun main() {
    PopupUncaughtExceptionHandler.set()
    val dpsTracker = DPSTracker()

    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

    SwingUtilities.invokeAndWait {
        PopupUncaughtExceptionHandler.set()

        JFrame("SDPS - antD").apply {
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE

            val mainPanel = MainPanel(dpsTracker)
            add(mainPanel)
            pack()
            minimumSize = Dimension(size.width - 325, size.height - 105)

            setLocation(
                Toolkit.getDefaultToolkit().screenSize.width - this.width - 10,
                10)
            isAlwaysOnTop = true
            isVisible = true
        }
    }

    dpsTracker.run()
}
