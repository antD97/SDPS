/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps.ui

import java.awt.Color
import java.awt.Component
import java.awt.Font
import javax.swing.JTable
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableCellRenderer

/** Customized table with output columns and colorized rows. */
class OutputTable(rowSize: Int) : JTable() {

    companion object {
        val damageColor: Color = Color.decode("#eab4b7")
        val darkDamageColor: Color = Color.decode("#cc9699")
        val healReceivedColor: Color = Color.decode("#b8dfef")
        val healAppliedColor: Color = Color.decode("#c0efb8")
    }

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

        c.background = background // default

        val modelRow = convertRowIndexToModel(row)

        val damage = model.getValueAt(modelRow, 2) as String
        val healReceived = model.getValueAt(modelRow, 6) as String
        val healApplied = model.getValueAt(modelRow, 8) as String

        when {
            healReceived == "" && healApplied == "" -> {
                c.background = damageColor
                if ((model.getValueAt(modelRow, 3) as String).endsWith("*")) {
                    c.background = darkDamageColor
                }
            }
            damage == "" && healApplied == "" -> c.background = healReceivedColor
            damage == "" && healReceived == "" -> c.background = healAppliedColor
        }

        return c;
    }
}