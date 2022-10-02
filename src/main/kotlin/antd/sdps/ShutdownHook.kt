/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps

import antd.sdps.ConfigManager.save
import antd.sdps.SharedInstances.autoCombatResetWorker
import antd.sdps.SharedInstances.combatTracker
import antd.sdps.SharedInstances.mainFrame
import antd.sdps.SharedInstances.obsPanel
import antd.sdps.SharedInstances.obsWriter
import antd.sdps.SharedInstances.outputTable
import antd.sdps.SharedInstances.sidebarPanel
import javax.swing.SpinnerNumberModel

/** [Thread] that saves user configuration to file. */
class ShutdownHook : Thread() {

    override fun run() {
        // reload config data
        val configData = ConfigManager.load() ?: ConfigManager.ConfigData()

        // save config data
        ConfigManager.ConfigData(
            loc = mainFrame.location,
            size = mainFrame.size,
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
            obsEnabled = obsPanel.enabledCheckBox.isSelected,
            obsPrintHeadersRow = obsPanel.printHeadersCheckBox.isSelected,
            obsPrintTotalsRow = obsPanel.printTotalsRowCheckBox.isSelected,
            obsColumnWidth = (obsPanel.columnWidthSpinner.model as SpinnerNumberModel).number
                .toInt(),
            obsReasonColumnWidth = (obsPanel.reasonColumnWidthSpinner.model as SpinnerNumberModel)
                .number.toInt(),
            obsMaxLines = (obsPanel.maxLinesSpinner.model as SpinnerNumberModel).number.toInt()
        ).save()

        combatTracker.cancel(true)
        autoCombatResetWorker.cancel(true)
        obsWriter.cancel(true)

        // wait for the workers to cancel
        try { combatTracker.get() }
        catch (_: Exception) {}
        try { autoCombatResetWorker.get() }
        catch (_: Exception) {}
        try { obsWriter.get() }
        catch (_: Exception) {}

        // clear the obs writer output file
        obsWriter.outputFile.writeText("")
    }
}
