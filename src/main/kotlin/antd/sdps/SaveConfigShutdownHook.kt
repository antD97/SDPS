/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps

import antd.sdps.ConfigManager.save
import antd.sdps.ui.MainJFrame

/** [Thread] that saves user configuration to file. */
class SaveConfigShutdownHook(private val mainJFrame: MainJFrame) : Thread() {

    override fun run() {
        val mainPanel = mainJFrame.content
        val configData = ConfigManager.load() ?: ConfigManager.ConfigData()

        ConfigManager.ConfigData(
            loc = mainJFrame.location,
            size = mainJFrame.size,
            ign = if (mainPanel.getIgn() != "Searching...") mainPanel.getIgn() else null,
            sidebar = mainPanel.isSidebarEnabled(),
            onTop = mainPanel.isOnTopEnabled(),
            trackDamage = mainPanel.isTrackDamageEnabled(),
            trackHealReceived = mainPanel.isTrackHealReceivedEnabled(),
            trackHealApplied = mainPanel.isTrackHealAppliedEnabled(),
            godsOnly = mainPanel.isGodsOnlyEnabled(),
            columnOrder = mainPanel.getColumnOrder(),
            columnWidths = mainPanel.getColumnWidths(),
            rowSize = mainPanel.getRowSize(),
            updateCheck = configData.updateCheck
        ).save()
    }
}
