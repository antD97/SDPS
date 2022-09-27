/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps.ui.sidebar

import antd.sdps.combattracking.ObsWriter
import antd.sdps.ui.OutputTable
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JCheckBox
import javax.swing.JPanel
import javax.swing.JSeparator
import javax.swing.table.TableColumn

class ColumnCheckboxesPanel(
    private val obsWriter: ObsWriter,
    private val outputTable: OutputTable
) : JPanel(GridBagLayout()) {

/* ----------------------------------------- UI Content ----------------------------------------- */

    val columnCheckBoxGroups = OutputTable.columnNameGroups
        .map { group ->
            group.map { columnName ->
                JCheckBox(columnName.full).apply {
                    addActionListener { setColumnVisible(columnName.short, this.isSelected) }
                    isSelected = true
                }
            }
        }

/* -------------------------------------------- Init -------------------------------------------- */

    init {
        val c = GridBagConstraints()

        c.gridy = 0
        c.fill = GridBagConstraints.HORIZONTAL
        c.weightx = 1.0
        c.anchor = GridBagConstraints.FIRST_LINE_START
        c.insets = Insets(0, 0, 0, 0)

        // check box groups
        for (i in columnCheckBoxGroups.indices) {
            val checkBoxGroup = columnCheckBoxGroups[i]

            // checkboxes
            for (j in checkBoxGroup.indices) {
                val checkBox = checkBoxGroup[j]

                // last checkbox fills vertically
                if (i == columnCheckBoxGroups.lastIndex && j == checkBoxGroup.lastIndex) {
                    c.weighty = 1.0
                }

                add(checkBox, c)
                c.gridy++
            }

            // checkbox group dividers
            if (i != columnCheckBoxGroups.lastIndex) {
                add(JSeparator(), c)
                c.gridy++
            }
        }
    }

/* ------------------------------------------- Helpers ------------------------------------------ */

    /** Shows or hides the specified column.  */
    private fun setColumnVisible(header: String, isVisible: Boolean) {
        var column: TableColumn? = null
        for (c in outputTable.columnModel.columns)
            if (c.headerValue as String == header) column = c

        if (column != null) {
            // visible
            if (isVisible) {
                column.maxWidth = OutputTable.columnMaxWidth
                column.minWidth = OutputTable.columnMinWidth
                column.preferredWidth = 66
            }
            // hidden
            else {
                column.minWidth = 0
                column.maxWidth = 0
            }

            // update obs writer print column
            when (header) {
                "Time" -> obsWriter.printTime = isVisible
                "DPS" -> obsWriter.printDPS = isVisible
                "Damage" -> obsWriter.printDamage = isVisible
                "Σ Damage" -> obsWriter.printTotalDamage = isVisible
                "Mitigated" -> obsWriter.printMitigated = isVisible
                "Σ Mitigated" -> obsWriter.printTotalMitigated = isVisible
                "Heal Received" -> obsWriter.printHealReceived = isVisible
                "Σ Heal Received" -> obsWriter.printTotalHealReceived = isVisible
                "Heal Applied" -> obsWriter.printHealApplied = isVisible
                "Σ Heal Applied" -> obsWriter.printTotalHealApplied = isVisible
                "Reason" -> obsWriter.printReason = isVisible
            }
        }
    }
}
