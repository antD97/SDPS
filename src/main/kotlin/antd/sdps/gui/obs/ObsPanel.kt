/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps.gui.obs

import antd.sdps.SharedInstances.initConfig
import antd.sdps.SharedInstances.obsWriter
import antd.sdps.gui.components.LabelPanel
import antd.sdps.workers.ObsWriter
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JCheckBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel

/** The OBS settings content panel. */
class ObsPanel : JPanel(GridBagLayout()) {

/* ----------------------------------------- UI Content ----------------------------------------- */

    val enabledCheckBox = JCheckBox("Enable OBS File Source").apply {
        toolTipText = "Toggles writing combat to the OBS text source file"
        isSelected = initConfig.obsEnabled

        addActionListener {
            if ((it.source as JCheckBox).isSelected)
                obsWriter.queueTask(ObsWriter.WriterTask.Enable)
            else obsWriter.queueTask(ObsWriter.WriterTask.Disable)
        }
    }

    val printHeadersCheckBox = JCheckBox("Print Headers").apply {
        toolTipText = "Toggles printing a column header line"
        isSelected = initConfig.obsPrintHeadersRow

        addActionListener {
            obsWriter.printHeadersRow = (it.source as JCheckBox).isSelected
            obsWriter.queueTask(ObsWriter.WriterTask.Rewrite)
        }
    }

    val printTotalsRowCheckBox = JCheckBox("Print Totals Row").apply {
        toolTipText = "Toggles printing a line containing column totals"
        isSelected = initConfig.obsPrintTotalsRow

        addActionListener {
            obsWriter.printTotalsRow = (it.source as JCheckBox).isSelected
            obsWriter.queueTask(ObsWriter.WriterTask.Rewrite)
        }
    }

    val columnWidthSpinner = JSpinner(SpinnerNumberModel(initConfig.obsColumnWidth, 1, 50, 1))
        .apply {
            toolTipText = "Width of columns in characters (excluding reason column)"

            addChangeListener {
                obsWriter.columnWidth = ((it.source as JSpinner).model as SpinnerNumberModel)
                    .number.toInt()
                obsWriter.queueTask(ObsWriter.WriterTask.Rewrite)
            }
        }

    val reasonColumnWidthSpinner =
        JSpinner(SpinnerNumberModel(initConfig.obsReasonColumnWidth, 1, 50, 1)).apply {
            toolTipText = "Width of the reason column in characters"

            addChangeListener {
                obsWriter.reasonColumnWidth = ((it.source as JSpinner).model as SpinnerNumberModel)
                    .number.toInt()
                obsWriter.queueTask(ObsWriter.WriterTask.Rewrite)
            }
        }

    val maxLinesSpinner =
        JSpinner(SpinnerNumberModel(initConfig.obsMaxLines, 2, 50, 1)).apply {
            toolTipText = "Number of lines written"

            addChangeListener {
                obsWriter.maxLines = ((it.source as JSpinner).model as SpinnerNumberModel).number
                    .toInt()
                obsWriter.queueTask(ObsWriter.WriterTask.Rewrite)
            }
        }

/* -------------------------------------------- Init -------------------------------------------- */

    init {
        val c = GridBagConstraints()

        c.gridy = 0
        c.anchor = GridBagConstraints.PAGE_START
        c.insets = Insets(20, 20, 10, 20)
        add(JLabel("OBS Text Source Settings"), c)

        c.gridy++
        c.anchor = GridBagConstraints.FIRST_LINE_START
        c.insets = Insets(0, 20, 0, 20)
        add(enabledCheckBox, c)

        c.gridy++
        add(printHeadersCheckBox, c)

        c.gridy++
        add(printTotalsRowCheckBox, c)

        c.gridy++
        c.insets = Insets(0, 20, 5, 20)
        add(LabelPanel("Column Width", columnWidthSpinner, gap = 39), c)

        c.gridy++
        add(LabelPanel("Reason Column Width", reasonColumnWidthSpinner), c)

        c.gridy++
        c.insets = Insets(0, 20, 20, 20)
        add(LabelPanel("Max Lines", maxLinesSpinner, gap = 58), c)
    }
}
