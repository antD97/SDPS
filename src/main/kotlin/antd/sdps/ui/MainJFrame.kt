/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps.ui

import antd.sdps.ConfigManager
import antd.sdps.combattracking.CombatTracker
import antd.sdps.combattracking.ObsWriter
import java.awt.Dimension
import javax.swing.JFrame

/** Main tool window. */
class MainJFrame(
    configData: ConfigManager.ConfigData,
    obsWriter: ObsWriter,
    combatTracker: CombatTracker,
    version: String
) : JFrame() {

    companion object {
        val sidebarDisabledMinSize = Dimension(300, 425)
        val sidebarEnabledMinSize = Dimension(150, 100)
    }

    val content = MainPanel(configData, obsWriter, combatTracker)

    init {
        title = "SDPS v$version - antD"
        defaultCloseOperation = EXIT_ON_CLOSE
        add(content)

        // load saved window data
        if (configData.size != null) size = configData.size else pack()
        if (configData.loc != null) location = configData.loc else setLocationRelativeTo(null)
        minimumSize = if (configData.sidebar) sidebarDisabledMinSize else sidebarEnabledMinSize
        isAlwaysOnTop = configData.onTop

        isVisible = true
    }
}
