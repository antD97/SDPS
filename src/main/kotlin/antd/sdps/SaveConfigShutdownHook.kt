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
        val configData = ConfigManager.load() ?: ConfigManager.ConfigData()

        ConfigManager.ConfigData(
            loc = mainJFrame.location,
            size = mainJFrame.size,
            ign = if (mainJFrame.content.ign != "Searching...") mainJFrame.content.ign else null,
            sidebar = mainJFrame.content.sidebarEnabled,
            onTop = mainJFrame.content.onTopEnabled,
            trackDamage = mainJFrame.content.trackDamageEnabled,
            trackHealReceived = mainJFrame.content.trackHealReceivedEnabled,
            trackHealApplied = mainJFrame.content.trackHealAppliedEnabled,
            godsOnly = mainJFrame.content.godsOnlyEnabled,
            columnOrder = mainJFrame.content.columnOrder,
            columnWidths = mainJFrame.content.columnWidths,
            rowSize = mainJFrame.content.rowSize,
            updateCheck = configData.updateCheck
        ).save()
    }
}
