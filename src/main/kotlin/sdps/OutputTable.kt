/*
 * Copyright © 2021 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package sdps

import java.awt.Color
import java.awt.Component
import java.awt.Font
import javax.swing.JTable
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableCellRenderer

/** Customized table with output columns and colorized rows. */
class OutputTable(rowSize: Int) : JTable() {

    init {
        model = DefaultTableModel(arrayOf(), arrayOf(
            "Time",
            "DPS",
            "Damage",
            "Σ Damage",
            "Mitigated",
            "Σ Mitigated",
            "Heal Received",
            "Σ Heal Received",
            "Heal Applied",
            "Σ Heal Applied",
            "Reason")
        ).apply {
            setDefaultEditor(Object::class.java, null)
            font = Font(font.name, font.style, rowSize)
            tableHeader.font = font
            rowHeight = font.size + 5
        }
    }

    override fun prepareRenderer(renderer: TableCellRenderer?, row: Int, column: Int): Component {
        val c = super.prepareRenderer(renderer, row, column)

        c.background = background   // default
        val damageColor = Color.decode("#eab4b7")
        val healReceivedColor = Color.decode("#b8dfef")
        val healAppliedColor = Color.decode("#c0efb8")

        val modelRow = convertRowIndexToModel(row)

        val damage = model.getValueAt(modelRow, 2) as String
        val healReceived = model.getValueAt(modelRow, 6) as String
        val healApplied = model.getValueAt(modelRow, 8) as String

        when {
            healReceived == "" && healApplied == "" -> c.background = damageColor
            damage == "" && healApplied == "" -> c.background = healReceivedColor
            damage == "" && healReceived == "" -> c.background = healAppliedColor
        }

        return c;
    }
}
