/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps

import antd.sdps.ConfigManager.save
import antd.sdps.combattracking.ObsWriter
import antd.sdps.gui.MainJFrame
import antd.sdps.gui.OutputTable
import antd.sdps.gui.sidebar.SidebarPanel

/** [Thread] that saves user configuration to file. */
class SaveConfigHook : Thread() {

    private val obsWriter: ObsWriter by SharedInstances::obsWriter

    private val mainJFrame: MainJFrame by SharedInstances::mainJFrame
    private val sidebarPanel: SidebarPanel by SharedInstances::sidebarPanel
    private val outputTable: OutputTable by SharedInstances::outputTable

    override fun run() {
        // reload config data
        val configData = ConfigManager.load() ?: ConfigManager.ConfigData()

        // save config data
        ConfigManager.ConfigData(
            loc = mainJFrame.location,
            size = mainJFrame.size,
            ign = if (sidebarPanel.nameField.text != "Searching...") sidebarPanel.nameField.text
            else null,
            sidebar = sidebarPanel.isVisible,
            onTop = sidebarPanel.onTopCheckBox.isSelected,
            trackDamage = sidebarPanel.trackDamageCheckBox.isSelected,
            trackHealReceived = sidebarPanel.trackHealReceivedCheckBox.isSelected,
            trackHealApplied = sidebarPanel.trackHealAppliedCheckBox.isSelected,
            godsOnly = sidebarPanel.godsOnlyCheckBox.isSelected,
            columnOrder = outputTable.columnModel.columns.toList().map { it.headerValue as String },
            columnWidths = outputTable.columnModel.columns.toList().map { it.width },
            rowSize = outputTable.font.size,
            autoReset = sidebarPanel.autoResetCheckBox.isSelected,
            updateCheck = configData.updateCheck,
            // todo change the following to gui check like the others
            obsEnabled = true,
            obsPrintHeadersRow = true,
            obsPrintTotalsRow = true,
            obsColumnWidth = obsWriter.columnWidth,
            obsReasonColumnWidth = obsWriter.reasonColumnWidth,
            obsMaxLines = obsWriter.maxLines
        ).save()

        // clear obs writer output file
        obsWriter.cancel(true)
        obsWriter.awaitCancellation()
    }
}
