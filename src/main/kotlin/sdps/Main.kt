/*
 * Copyright © 2021 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package sdps

import sdps.ConfigManager.save
import java.awt.Dimension
import javax.swing.*


fun main() {

    val version = object {}.javaClass.getResource("/version.txt").readText()

    PopupUncaughtExceptionHandler.set()
    val combatTracker = CombatTracker()

    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    val jFrame = JFrame()
    // min size of window with sidebar
    val windowSidebarMinSize = Dimension(300, 425)
    // min size of window without sidebar
    val windowSmallMinSize = Dimension(150, 100)

    // load config data. if none, use defaults
    val loadConfigData =
        if (ConfigManager.load() != null) ConfigManager.load()!!
        else ConfigManager.ConfigData()

    combatTracker.godsOnly = loadConfigData.godsOnly
    combatTracker.trackDamage = loadConfigData.trackDamage
    combatTracker.trackHealReceived = loadConfigData.trackHealReceived
    combatTracker.trackHealApplied = loadConfigData.trackHealApplied

    if (loadConfigData.ign != null) combatTracker.updateIGN(loadConfigData.ign!!)

    val mainPanel = MainPanel(combatTracker, loadConfigData, windowSidebarMinSize, windowSmallMinSize)

    SwingUtilities.invokeAndWait {
        PopupUncaughtExceptionHandler.set()

        jFrame.apply {
            title = "SDPS v$version - antD"
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE

            add(mainPanel)

            // load saved window data
            if (loadConfigData.size != null) size = loadConfigData.size else pack()
            if (loadConfigData.loc != null) location = loadConfigData.loc!! else setLocationRelativeTo(null)
            minimumSize = if (loadConfigData.sidebar) windowSidebarMinSize else windowSmallMinSize
            isAlwaysOnTop = loadConfigData.onTop

            isVisible = true
        }
    }

    // save config on close
    Runtime.getRuntime().addShutdownHook(object : Thread() {
        override fun run() {
            // reload config data
            val saveConfigData =
                if (ConfigManager.load() != null) ConfigManager.load()!!
                else ConfigManager.ConfigData()

            ConfigManager.ConfigData(
                jFrame.location,
                jFrame.size,
                if (mainPanel.ign != "Searching...") mainPanel.ign else null,
                mainPanel.sidebarEnabled,
                mainPanel.onTopEnabled,
                mainPanel.trackDamageEnabled,
                mainPanel.trackHealReceivedEnabled,
                mainPanel.trackHealAppliedEnabled,
                mainPanel.godsOnlyEnabled,
                mainPanel.columnOrder,
                mainPanel.columnWidths,
                mainPanel.rowSize,
                saveConfigData.updateCheck
            ).save()
        }
    })

    // check for new release
    if (loadConfigData.updateCheck) {
        UpdateChecker.check(version)
    }

    combatTracker.run()
}
