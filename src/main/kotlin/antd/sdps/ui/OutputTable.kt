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
        const val columnMaxWidth = Int.MAX_VALUE
        const val columnMinWidth = 15

        val columnNameGroups = listOf(
            listOf(ColumnNames("Time")),
            listOf(
                ColumnNames("DPS"),
                ColumnNames("Damage"),
                ColumnNames("Total Damage", "Σ Damage"),
                ColumnNames("Mitigated"),
                ColumnNames("Total Mitigated", "Σ Mitigated"),
            ),
            listOf(
                ColumnNames("Heal Received"),
                ColumnNames("Total Heal Received", "Σ Heal Received"),
                ColumnNames("Heal Applied"),
                ColumnNames("Total Heal Applied", "Σ Heal Applied"),
            ),
            listOf(ColumnNames("Reason"))
        )

        val darkResetColor: Color = Color.decode("#afafaf")
        val damageColor: Color = Color.decode("#eab4b7")
        val darkDamageColor: Color = Color.decode("#cc9699")
        val healReceivedColor: Color = Color.decode("#b4dbea")
        val darkHealReceivedColor: Color = Color.decode("#96bccc")
        val healAppliedColor: Color = Color.decode("#bceab4")
        val darkHealAppliedColor: Color = Color.decode("#9ecc96")

        data class ColumnNames(val full: String, val short: String = full)
    }

    init {
        model =
            DefaultTableModel(arrayOf(), columnNameGroups.flatten().map { it.short }.toTypedArray())
                .apply {
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
            // Reset* row
            damage.contains("Reset*") -> c.background = darkResetColor
            // damage row
            healReceived == "" && healApplied == "" -> {
                if (damage.endsWith("*")) c.background = darkDamageColor
                else c.background = damageColor
            }
            // heal received row
            damage == "" && healApplied == "" -> {
                if (healReceived.endsWith("*")) c.background = darkHealReceivedColor
                else c.background = healReceivedColor
            }
            // heal applied row
            damage == "" && healReceived == "" -> {
                if (healApplied.endsWith("*")) c.background = darkHealAppliedColor
                else c.background = healAppliedColor
            }
        }

        return c
    }
}
