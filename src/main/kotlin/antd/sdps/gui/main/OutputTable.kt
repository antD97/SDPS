/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps.gui.main

import antd.sdps.SharedInstances.initConfig
import antd.sdps.SharedInstances.mainPanel
import java.awt.Color
import java.awt.Component
import java.awt.Font
import javax.swing.JTable
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableCellRenderer

/** Customized table with output columns and colorized rows. */
class OutputTable : JTable() {

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
            // todo
//            listOf(
//                ColumnNames("Damage Received"),
//                ColumnNames("Total Damage Received", "Σ Damage Received"),
//                ColumnNames("Mitigated Received"),
//                ColumnNames("Total Mitigated Received", "Σ Mitigated Received")
//            ),
            listOf(
                ColumnNames("Heal Received"),
                ColumnNames("Total Heal Received", "Σ Heal Received")
            ),
            listOf(
                ColumnNames("Heal Applied"),
                ColumnNames("Total Heal Applied", "Σ Heal Applied")
            ),
            listOf(ColumnNames("Reason"))
        )

        /** Full names of initially hidden columns. */
        val initHiddenColumns = listOf(
            "Time",
            "Mitigated",
            "Total Mitigated",
            "Heal Received",
            "Total Heal Received",
            "Heal Applied",
            "Total Heal Applied"
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

/* -------------------------------------------- Init -------------------------------------------- */

    init {
        model =
            DefaultTableModel(arrayOf(), columnNameGroups.flatten().map { it.short }.toTypedArray())
                .apply {
                    setDefaultEditor(Object::class.java, null)
                    font = Font(font.name, font.style, initConfig.rowSize)
                    tableHeader.font = font
                    rowHeight = font.size + 5
                }

        // load init

        // column order
        val columnOrder = initConfig.columnOrder
        if (columnOrder != null) {
            for (i in columnOrder.indices) {
                val targetCol = columnOrder[i]
                val columnList = columnModel.columns.toList()

                for (j in (i + 1)..columnList.lastIndex) {
                    if (columnList[j].headerValue as String == targetCol) {
                        // move the column to the correct spot
                        columnModel.moveColumn(j, i)
                    }
                }
            }
        }

        // column widths
        val columnWidths = initConfig.columnWidths
        if (columnWidths != null) {
            for ((i, width) in columnWidths.withIndex()) {
                val column = columnModel.getColumn(i)

                // enabled
                if (width != 0) {
                    column.maxWidth = columnMaxWidth
                    column.minWidth = columnMinWidth
                    column.preferredWidth = width
                }
                // disabled
                else {
                    column.minWidth = 0
                    column.maxWidth = 0
                }
            }
        }
        // default hidden columns
        else {
            val initHiddenColumnsShort = initHiddenColumns.map { fullName ->
                columnNameGroups.flatten().find { it.full == fullName }!!.short
            }

            for (column in columnModel.columns) {
                if (initHiddenColumnsShort.contains(column.headerValue)) {
                    column.minWidth = 0
                    column.maxWidth = 0
                }
            }
        }
    }

/* ---------------------------------------------------------------------------------------------- */

    /** Creates custom row coloring. */
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

    /** Removes all rows this table. */
    fun clearTable() {
        (model as DefaultTableModel).rowCount = 0
        mainPanel.scrollTableToBottom()
    }
}
