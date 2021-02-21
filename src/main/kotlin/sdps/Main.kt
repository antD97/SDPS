/*
 * Copyright © 2021 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package sdps

import sdps.ConfigManager.save
import java.awt.Dimension
import javax.swing.*

fun main() {
    PopupUncaughtExceptionHandler.set()
    val dpsTracker = DPSTracker()

    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    val jFrame = JFrame("SDPS - antD")
    // min size of window with sidebar
    val windowSidebarMinSize = Dimension(300, 360)
    // min size of window without sidebar
    val windowSmallMinSize = Dimension(150, 100)

    // load save data. if none, use defaults
    val configData =
        if (ConfigManager.load() != null) ConfigManager.load()!!
        else ConfigManager.ConfigData()

    val mainPanel = MainPanel(dpsTracker, configData, windowSidebarMinSize, windowSmallMinSize)

    SwingUtilities.invokeAndWait {
        PopupUncaughtExceptionHandler.set()

        jFrame.apply {
            title = "SDPS - antD"
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE

            add(mainPanel)

            // load saved window data
            if (configData.size != null) size = configData.size else pack()
            if (configData.loc != null) location = configData.loc!! else setLocationRelativeTo(null)
            minimumSize = if (configData.sidebar) windowSidebarMinSize else windowSmallMinSize
            isAlwaysOnTop = configData.onTop

            isVisible = true
        }
    }

    // save config on close
    Runtime.getRuntime().addShutdownHook(object : Thread() {
        override fun run() {
            ConfigManager.ConfigData(
                jFrame.location,
                jFrame.size,
                if (mainPanel.ign != "") mainPanel.ign else null,
                mainPanel.isSidebarEnabled,
                mainPanel.isOnTopEnabled,
                mainPanel.columnOrder,
                mainPanel.columnWidths
            ).save()
        }
    })

    dpsTracker.run()
}
