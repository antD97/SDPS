/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps.ui

import antd.sdps.ConfigManager
import antd.sdps.combattracking.CombatTracker
import antd.sdps.ui.sidebar.MinSidebarPanel
import antd.sdps.ui.sidebar.SidebarPanel
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.io.File
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextField
import javax.swing.SwingUtilities
import javax.swing.table.DefaultTableModel

/** The tool's main UI panel. */
class MainPanel(
    combatTracker: CombatTracker,
    private val configData: ConfigManager.ConfigData
) : JPanel(GridBagLayout()) {

/* ----------------------------------------- UI Content ----------------------------------------- */

    private val outputTable = OutputTable(configData.rowSize)
    private val outputTableScrollPane = JScrollPane(outputTable)
        .apply { preferredSize = Dimension(275, 300) }

    private val sidebarPanel = SidebarPanel(combatTracker, configData, outputTable)

    private val minSidebarPanel = MinSidebarPanel(sidebarPanel)
        .also { sidebarPanel.minimizedSidebar = it }

/* ------------------------------------------- Getters ------------------------------------------ */

    fun getIgn(): String = sidebarPanel.nameField.text
    fun isSidebarEnabled(): Boolean = sidebarPanel.isVisible
    fun isOnTopEnabled(): Boolean = sidebarPanel.onTopCheckBox.isSelected
    fun isTrackDamageEnabled(): Boolean = sidebarPanel.trackDamageCheckBox.isSelected
    fun isTrackHealReceivedEnabled(): Boolean = sidebarPanel.trackHealReceivedCheckBox.isSelected
    fun isTrackHealAppliedEnabled(): Boolean = sidebarPanel.trackHealAppliedCheckBox.isSelected
    fun isGodsOnlyEnabled(): Boolean = sidebarPanel.godsOnlyCheckBox.isSelected
    fun getRowSize(): Int = outputTable.font.size

    fun getColumnOrder(): List<String> = outputTable.columnModel.columns.toList()
        .map { it.headerValue as String }

    fun getColumnWidths(): List<Int> = outputTable.columnModel.columns.toList()
        .map { it.width }

/* -------------------------------------------- Init -------------------------------------------- */

    init {
        val c = GridBagConstraints()

        // sidebar
        c.gridx = 0
        c.fill = GridBagConstraints.BOTH
        c.weightx = 0.0
        c.weighty = 1.0
        c.insets = Insets(10, 10, 10, 10)

        add(sidebarPanel, c)

        // minimized sidebar
        c.gridx = 1
        c.insets = Insets(10, 5, 10, 5)
        add(minSidebarPanel, c)

        // main table
        c.gridx = 2
        c.weightx = 1.0
        c.insets = Insets(10, 0, 10, 10)
        add(outputTableScrollPane, c)

        // deselect initially hidden columns
        val initHiddenCols = listOf(
            "Time",
            "Mitigated",
            "Total Mitigated",
            "Heal Received",
            "Total Heal Received",
            "Heal Applied",
            "Total Heal Applied"
        )

        for (checkBox in sidebarPanel.columnCheckboxesPanel.columnCheckBoxGroups.flatten()) {
            if (initHiddenCols.contains(checkBox.text)) {
                checkBox.isSelected = false
                for (action in checkBox.actionListeners) action.actionPerformed(null)
            }
        }

        // key binds
        isFocusable = true
        addMouseListener(object : MouseListener {
            override fun mouseClicked(e: MouseEvent?) {}
            override fun mousePressed(e: MouseEvent?) = requestFocus()
            override fun mouseReleased(e: MouseEvent?) {}
            override fun mouseEntered(e: MouseEvent?) {}
            override fun mouseExited(e: MouseEvent?) {}
        })

        fun Component.onEachChildComponent(
            condition: (Component) -> Boolean,
            action: (Component) -> Unit
        ) {
            if (condition(this)) {
                action(this)
                if (this is Container) {
                    for (comp in components) comp.onEachChildComponent(condition, action)
                }
            }
        }

        onEachChildComponent({ component: Component -> component !is JTextField },
            {
                it.addKeyListener(object : KeyListener {
                    override fun keyTyped(e: KeyEvent?) {}
                    override fun keyPressed(e: KeyEvent?) = shortcutButtonPress(e)
                    override fun keyReleased(e: KeyEvent?) {}
                })
            })

        // load user config
        loadConfigData()

        // supply combat tracker with necessary ui components
        combatTracker.setNameField(sidebarPanel.nameField)
        combatTracker.setTableModel(outputTable.model as DefaultTableModel)

        // combat tracker listeners
        combatTracker.addTableListener(::tableUpdate)
        combatTracker.addCombatLogListener(::combatLogUpdate)
    }

/* ------------------------------------------ Listeners ----------------------------------------- */

    /** Handles shortcut key actions */
    private fun shortcutButtonPress(e: KeyEvent?) {
        if (e != null) {
            when (e.keyCode) {
                KeyEvent.VK_ESCAPE -> {
                    if (sidebarPanel.isVisible) sidebarPanel.minimizeSidebarButtonClick(null)
                    else minSidebarPanel.maximizeSidebarButtonClick(null)
                }
                KeyEvent.VK_T -> {
                    sidebarPanel.onTopCheckBox.isSelected = !sidebarPanel.onTopCheckBox.isSelected
                    sidebarPanel.onTopCheckBoxClick(null)
                }
                KeyEvent.VK_1 -> {
                    sidebarPanel.trackDamageCheckBox.isSelected =
                        !sidebarPanel.trackDamageCheckBox.isSelected
                    sidebarPanel.trackDamageCheckBoxClick(null)
                }
                KeyEvent.VK_2 -> {
                    sidebarPanel.trackHealReceivedCheckBox.isSelected =
                        !sidebarPanel.trackHealReceivedCheckBox.isSelected
                    sidebarPanel.trackHealReceivedCheckBoxClick(null)
                }
                KeyEvent.VK_3 -> {
                    sidebarPanel.trackHealAppliedCheckBox.isSelected =
                        !sidebarPanel.trackHealAppliedCheckBox.isSelected
                    sidebarPanel.trackHealAppliedCheckBoxClick(null)
                }
                KeyEvent.VK_G -> {
                    sidebarPanel.godsOnlyCheckBox.isSelected =
                        !sidebarPanel.godsOnlyCheckBox.isSelected
                    sidebarPanel.godsOnlyCheckBoxClick(null)
                }
                KeyEvent.VK_R -> {
                    sidebarPanel.resetCombatButtonClick(null)
                }
                KeyEvent.VK_C -> {
                    sidebarPanel.clearTableButtonClick(null)
                }
            }
        }
    }

/* ------------------------------------------- Helpers ------------------------------------------ */

    /** Update UI to match user config data. */
    private fun loadConfigData() {
        if (configData.ign != null) sidebarPanel.nameField.text = configData.ign

        sidebarPanel.isVisible = configData.sidebar
        minSidebarPanel.isVisible = !configData.sidebar

        sidebarPanel.onTopCheckBox.isSelected = configData.onTop

        sidebarPanel.trackDamageCheckBox.isSelected = configData.trackDamage
        sidebarPanel.trackHealReceivedCheckBox.isSelected = configData.trackHealReceived
        sidebarPanel.trackHealAppliedCheckBox.isSelected = configData.trackHealApplied

        sidebarPanel.godsOnlyCheckBox.isSelected = configData.godsOnly

        // column order
        if (configData.columnOrder != null) {
            for (i in 0..configData.columnOrder!!.lastIndex) {
                val targetCol = configData.columnOrder!![i]
                val tableColumns = outputTable.columnModel.columns.toList()

                for (j in i + 1..tableColumns.lastIndex) {
                    if (tableColumns[j].headerValue as String == targetCol) {
                        // move the column to the correct spot
                        outputTable.columnModel.moveColumn(j, i)
                    }
                }
            }
        }

        // column widths/enabled
        if (configData.columnWidths != null) {
            for (i in configData.columnWidths!!.indices) {

                val column = outputTable.columnModel.getColumn(i)
                val selected = configData.columnWidths!![i] != 0

                val columnFullName = OutputTable.columnNameGroups.flatten()
                    .find { it.short == column.headerValue }!!.full

                // set the checkbox isSelected
                sidebarPanel.columnCheckboxesPanel.columnCheckBoxGroups.flatten()
                    .find { it.text == columnFullName }!!.isSelected = selected

                if (selected) {
                    column.maxWidth = OutputTable.columnMaxWidth
                    column.minWidth = OutputTable.columnMinWidth
                    column.preferredWidth = configData.columnWidths!![i]
                } else {
                    column.minWidth = 0
                    column.maxWidth = 0
                }
            }
        }
    }

    /** Moves the table scroll to the bottom. */
    private fun tableUpdate() {
        val scrollBar = outputTableScrollPane.verticalScrollBar
        SwingUtilities.invokeLater {
            outputTable.revalidate()
            scrollBar.value = scrollBar.maximum
        }
    }

    /** Updates the combat log field with the new combat log file name. */
    private fun combatLogUpdate(combatLog: File?) {
        sidebarPanel.combatLogField.text = if (combatLog == null) "no file" else combatLog.name
    }
}
