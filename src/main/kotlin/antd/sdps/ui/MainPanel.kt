/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps.ui

import antd.sdps.combat.CombatTracker
import antd.sdps.ConfigManager
import java.awt.*
import java.awt.event.*
import java.io.File
import javax.swing.*
import javax.swing.event.ChangeEvent
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableColumn

/** The main UI panel. */
class MainPanel(
    private val combatTracker: CombatTracker,
    configData: ConfigManager.ConfigData,
    private val windowSidebarMinSize: Dimension,
    private val windowSmallMinSize: Dimension
) : JPanel(GridBagLayout()) {

    val ign: String
        get() { return nameField.text  }

    val sidebarEnabled: Boolean
        get() { return sidebar.isVisible }

    val onTopEnabled: Boolean
        get() { return onTopCheckBox.isSelected }

    val trackDamageEnabled: Boolean
        get() { return trackDamageCheckBox.isSelected }

    val trackHealReceivedEnabled: Boolean
        get() { return trackHealReceivedCheckBox.isSelected }

    val trackHealAppliedEnabled: Boolean
        get() { return trackHealAppliedCheckBox.isSelected }

    val godsOnlyEnabled: Boolean
        get() { return godsOnlyCheckBox.isSelected }

    val columnOrder: List<String>
        get() { return table.columnModel.columns.toList().map { it.headerValue as String } }

    val columnWidths: List<Int>
        get() { return table.columnModel.columns.toList().map { it.width } }

    val rowSize: Int
        get() { return table.font.size }

/* --------------------------------------- GUI Components --------------------------------------- */

    private val colMaxWidth = Int.MAX_VALUE
    private val colMinWidth = 15

    private val table = OutputTable(configData.rowSize)
    private val tableScrollPane = JScrollPane(table)
        .apply { preferredSize = Dimension(275, 300) }

    private val sidebar = JPanel(GridBagLayout())

    private val minimizeSidebarButton = JButton()
        .apply {
            preferredSize = Dimension(20, 10)
            addActionListener(::minimizeSidebarButtonClick)
            toolTipText = "Minimize the sidebar (Esc)"
        }

    private val onTopCheckBox = JCheckBox("Window Always On Top")
        .apply {
            addActionListener(::onTopCheckBoxClick)
            toolTipText = "Force this window to display on top of Smite and other windows (T)"
        }

    private val trackDamageCheckBox = JCheckBox("Track Damage")
        .apply {
            addActionListener(::trackDamageCheckBoxClick)
            toolTipText = "Track & print damage onto the table (1)"
            isSelected = true
            background = OutputTable.damageColor
        }

    private val trackHealReceivedCheckBox = JCheckBox("Track Heal Received")
        .apply {
            addActionListener(::trackHealReceivedCheckBoxClick)
            toolTipText = "Track & print heal received onto the table (2)"
            isSelected = false
            background = OutputTable.healReceivedColor
        }

    private val trackHealAppliedCheckBox = JCheckBox("Track Heal Applied")
        .apply {
            addActionListener(::trackHealAppliedCheckBoxClick)
            toolTipText = "Track & print heal applied onto the table (3)"
            isSelected = false
            background = OutputTable.healAppliedColor
        }

    private val godsOnlyCheckBox = JCheckBox("Gods Only")
        .apply {
            addActionListener(::godsOnlyCheckBoxClick)
            toolTipText = "Ignore minions/structures (G)"
            isSelected = true
        }

    private val nameField = JTextField(8)
        .apply {
            text = "Searching..."
            isEditable = false
        }

    private val nameFieldResetButton = JButton("↺")
        .apply {
            addActionListener(::nameFieldResetButtonClick)
            toolTipText = "Resets the in-game name tracked; will be set on the next tick of damage"
        }

    private val combatLogField = JTextField(13)
        .apply {
            text = "no file"
            isEditable = false
        }

    private val rowSizeSpinner = JSpinner(SpinnerNumberModel(configData.rowSize, 1, 100, 1))
        .apply {
            addChangeListener(::rowSizeSpinnerChange)
            toolTipText = "Adjust table font size"
        }

    private val resetCombatButton = JButton("Reset")
        .apply {
            addActionListener(::resetCombatButtonClick)
            toolTipText = "Makes your next tick of damage reset the DPS timer and resets damage & healing totals (R)"
        }
    private val clearTableButton = JButton("Clear Table")
        .apply {
            addActionListener(::clearTableButtonClick)
            toolTipText = "Clears the table of all rows and resets (C)"
        }

    private val minimizedBar = JPanel(GridBagLayout())
    private val maximizeSidebarButton = JButton()
        .apply {
            preferredSize = Dimension(10, 20)
            addActionListener(::maximizeSidebarButtonClick)
            toolTipText = "Maximize the sidebar (Escape)"
        }

    private val timeCheckBox = JCheckBox("Time")
        .apply {
            addActionListener(::timeCheckBoxClick)
            isSelected = true
        }

    private val dpsCheckBox = JCheckBox("DPS")
        .apply {
            addActionListener(::dpsCheckBoxClick)
            isSelected = true
        }
    private val damageCheckBox = JCheckBox("Damage")
        .apply {
            addActionListener(::damageCheckBoxClick)
            isSelected = true
        }
    private val totalDamageCheckBox = JCheckBox("Total Damage")
        .apply {
            addActionListener(::totalDamageCheckBoxClick)
            isSelected = true
        }
    private val mitigatedCheckBox = JCheckBox("Mitigated")
        .apply {
            addActionListener(::mitigatedCheckBoxClick)
            isSelected = true
        }
    private val totalMitigatedCheckBox = JCheckBox("Total Mitigated")
        .apply {
            addActionListener(::totalMitigatedCheckBoxClick)
            isSelected = true
        }

    private val healReceivedCheckBox = JCheckBox("Heal Received")
        .apply {
            addActionListener(::healReceivedCheckBoxClick)
            isSelected = true
        }
    private val totalHealReceivedCheckBox = JCheckBox("Total Heal Received")
        .apply {
            addActionListener(::totalHealReceivedCheckBoxClick)
            isSelected = true
        }
    private val healAppliedCheckBox = JCheckBox("Heal Applied")
        .apply {
            addActionListener(::healAppliedCheckBoxClick)
            isSelected = true
        }
    private val totalHealAppliedCheckBox = JCheckBox("Total Heal Applied")
        .apply {
            addActionListener(::totalHealAppliedCheckBoxClick)
            isSelected = true
        }

    private val reasonCheckBox = JCheckBox("Reason")
        .apply {
            addActionListener(::reasonCheckBoxClick)
            isSelected = true
        }

/* ----------------------------------------- Constructor ---------------------------------------- */

    init {
        val c = GridBagConstraints()

        // main table
        c.gridx = 2
        c.weightx = 1.0; c.weighty = 1.0
        c.fill = GridBagConstraints.BOTH
        c.insets = Insets(10, 0, 10, 10)
        add(tableScrollPane, c)

        // sidebar
        c.gridx = 1
        c.weightx = 0.0
        c.insets = Insets(10, 10, 10, 10)

        sidebar.apply {
            val c2 = GridBagConstraints()

            // minimize sidebar button
            c2.gridx = 0; c2.gridy = 0
            c2.weightx = 1.0; c2.weighty = 0.0
            c2.anchor = GridBagConstraints.LINE_START
            c2.insets = Insets(0, 0, 0, 0)
            add(minimizeSidebarButton, c2)

            // title
            c2.gridy++
            c2.anchor = GridBagConstraints.PAGE_START
            c2.insets = Insets(0, 0, 10, 0)
            add(JLabel("Smite Damage Tracker"), c2)

            // separator
            c2.gridy++
            c2.fill = GridBagConstraints.HORIZONTAL
            add(JSeparator(), c2)

            // on top checkbox
            c2.gridy++
            c2.fill = GridBagConstraints.NONE
            c2.anchor = GridBagConstraints.FIRST_LINE_START
            c2.insets = Insets(0, 0, 0, 0)
            add(onTopCheckBox, c2)

            // track damage checkbox
            c2.gridy++
            c2.fill = GridBagConstraints.HORIZONTAL
            add(trackDamageCheckBox, c2)

            // track heal received checkbox
            c2.gridy++
            add(trackHealReceivedCheckBox, c2)

            // track damage checkbox
            c2.gridy++
            add(trackHealAppliedCheckBox, c2)

            // gods only checkbox
            c2.gridy++
            c2.fill = GridBagConstraints.NONE
            c2.insets = Insets(0, 0, 10, 0)
            add(godsOnlyCheckBox, c2)

            // in-game name field
            c2.gridy++
            add(LabelPanel("In-game name", nameField, nameFieldResetButton), c2)

            // combat log file
            c2.gridy++
            add(LabelPanel("Combat log file", combatLogField, gap = 3), c2)

            // row size spinner
            c2.gridy++
            add(
                LabelPanel(
                "Table Font Size",
                rowSizeSpinner.apply { preferredSize = Dimension(40, preferredSize.height) },
                gap = 71
            ), c2)

            // 2x button group
            c2.gridy++
            c2.fill = GridBagConstraints.HORIZONTAL
            JPanel(GridBagLayout()).apply {
                val c3 = GridBagConstraints()

                // reset combat button
                c3.gridx = 0; c3.gridy = 0
                add(resetCombatButton, c3)

                // clear table button
                c3.gridx++
                c3.insets = Insets(0, 15, 0, 0)
                add(clearTableButton, c3)
            }.also { add(it, c2) }

            // checkboxes
            c2.gridy++
            c2.weighty = 1.0
            c2.fill = GridBagConstraints.BOTH
            c2.anchor = GridBagConstraints.PAGE_START
            val checkboxScrollPane = JScrollPane(JPanel(GridBagLayout()).apply {
                val c3 = GridBagConstraints()

                // time checkbox
                c3.gridy = 0
                c3.fill = GridBagConstraints.HORIZONTAL
                c3.weightx = 1.0
                c3.anchor = GridBagConstraints.FIRST_LINE_START
                c3.insets = Insets(0, 0, 0, 0)
                add(timeCheckBox, c3)

                // separator
                c3.gridy++
                add(JSeparator(), c3)

                // dps checkbox
                c3.gridy++
                add(dpsCheckBox, c3)

                // damage checkbox
                c3.gridy++
                add(damageCheckBox, c3)

                // total damage checkbox
                c3.gridy++
                add(totalDamageCheckBox, c3)

                // mitigated checkbox
                c3.gridy++
                add(mitigatedCheckBox, c3)

                // total mitigated checkbox
                c3.gridy++
                add(totalMitigatedCheckBox, c3)

                // separator
                c3.gridy++
                add(JSeparator(), c3)

                // heal received checkbox
                c3.gridy++
                add(healReceivedCheckBox, c3)

                // total heal received checkbox
                c3.gridy++
                add(totalHealReceivedCheckBox, c3)

                // heal applied checkbox
                c3.gridy++
                add(healAppliedCheckBox, c3)

                // total heal applied checkbox
                c3.gridy++
                add(totalHealAppliedCheckBox, c3)

                // separator
                c3.gridy++
                add(JSeparator(), c3)

                // reason checkbox
                c3.gridy++
                c3.weighty = 1.0
                c3.fill = GridBagConstraints.HORIZONTAL
                c3.anchor = GridBagConstraints.PAGE_START
                add(reasonCheckBox, c3)
            })

            checkboxScrollPane.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
            checkboxScrollPane.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
            checkboxScrollPane.minimumSize = Dimension(0, 0)
            checkboxScrollPane.preferredSize = Dimension(0, 0)

            add(checkboxScrollPane, c2)

            minimumSize = preferredSize

        }.also { add(it, c) }

        // minimized sidebar
        c.gridx = 0
        c.insets = Insets(10, 5, 5, 5)
        minimizedBar.apply {
            val c2 = GridBagConstraints()

            // maximize side bar button
            c2.weightx = 1.0; c2.weighty = 1.0
            c2.anchor = GridBagConstraints.PAGE_START
            add(maximizeSidebarButton, c2)

            minimumSize = preferredSize

        }.also { add(it, c) }

        // initially hidden columns
        timeCheckBox.isSelected = false
        timeCheckBoxClick(null)
        mitigatedCheckBox.isSelected = false
        mitigatedCheckBoxClick(null)
        totalMitigatedCheckBox.isSelected = false
        totalMitigatedCheckBoxClick(null)
        healReceivedCheckBox.isSelected = false
        healReceivedCheckBoxClick(null)
        totalHealReceivedCheckBox.isSelected = false
        totalHealReceivedCheckBoxClick(null)
        healAppliedCheckBox.isSelected = false
        healAppliedCheckBoxClick(null)
        totalHealAppliedCheckBox.isSelected = false
        totalHealAppliedCheckBoxClick(null)

        // key binds
        isFocusable = true
        addMouseListener(object : MouseListener {
            override fun mouseClicked(e: MouseEvent?) { }
            override fun mousePressed(e: MouseEvent?) { requestFocus() }
            override fun mouseReleased(e: MouseEvent?) { }
            override fun mouseEntered(e: MouseEvent?) { }
            override fun mouseExited(e: MouseEvent?) { }
        })

        fun Component.onEachComponent(condition: (Component) -> Boolean, action: (Component) -> Unit) {
            if (condition(this)) {
                action(this)
                if (this is Container) {
                    for (comp in components) comp.onEachComponent(condition, action)
                }
            }
        }

        onEachComponent({ component: Component -> component !is JTextField },
            {
                it.addKeyListener(object : KeyListener {
                    override fun keyTyped(e: KeyEvent?) { }
                    override fun keyPressed(e: KeyEvent?) { shortcutButtonPress(e) }
                    override fun keyReleased(e: KeyEvent?) { }
                })
            })

        // load settings
        if (configData.ign != null) nameField.text = configData.ign

        sidebar.isVisible = configData.sidebar
        minimizedBar.isVisible = !configData.sidebar

        onTopCheckBox.isSelected = configData.onTop

        trackDamageCheckBox.isSelected = configData.trackDamage
        trackHealReceivedCheckBox.isSelected = configData.trackHealReceived
        trackHealAppliedCheckBox.isSelected = configData.trackHealApplied

        godsOnlyCheckBox.isSelected = configData.godsOnly

        if (configData.columnOrder != null) {
            for (i in 0..configData.columnOrder!!.lastIndex) {
                val targetCol = configData.columnOrder!![i]
                val tableColumns = table.columnModel.columns.toList()

                for (j in i+1..tableColumns.lastIndex) {
                    if (tableColumns[j].headerValue as String == targetCol) {
                        // move the column to the correct spot
                        table.columnModel.moveColumn(j, i)
                    }
                }
            }
        }

        if (configData.columnWidths != null) {
            for (i in configData.columnWidths!!.indices) {

                val column = table.columnModel.getColumn(i)
                val selected = configData.columnWidths!![i] != 0

                when (column.headerValue) {
                    "Time" -> timeCheckBox.isSelected = selected
                    "DPS" -> dpsCheckBox.isSelected = selected
                    "Damage" -> damageCheckBox.isSelected = selected
                    "Σ Damage" -> totalDamageCheckBox.isSelected = selected
                    "Mitigated" -> mitigatedCheckBox.isSelected = selected
                    "Σ Mitigated" -> totalMitigatedCheckBox.isSelected = selected
                    "Heal Received" -> healReceivedCheckBox.isSelected = selected
                    "Σ Heal Received" -> totalHealReceivedCheckBox.isSelected = selected
                    "Heal Applied" -> healAppliedCheckBox.isSelected = selected
                    "Σ Heal Applied" -> totalHealAppliedCheckBox.isSelected = selected
                    "Reason" -> reasonCheckBox.isSelected = selected
                }

                if (selected) {
                    column.maxWidth = colMaxWidth
                    column.minWidth = colMinWidth
                    column.preferredWidth = configData.columnWidths!![i]
                }
                else {
                    column.minWidth = 0
                    column.maxWidth = 0
                }
            }
        }

        // update damage tracker
        combatTracker.nameField = nameField

        combatTracker.tableModel = table.model as DefaultTableModel

        combatTracker.addTableListener(::tableUpdate)
        combatTracker.addCombatLogListener(::combatLogUpdate)
    }

/* ------------------------------------------ Listeners ----------------------------------------- */


    /** Handles shortcut key actions */
    private fun shortcutButtonPress(e: KeyEvent?) {
        if (e != null) {
            when (e.keyCode) {
                KeyEvent.VK_ESCAPE -> {
                    if (sidebar.isVisible) minimizeSidebarButtonClick(null)
                    else maximizeSidebarButtonClick(null)
                }
                KeyEvent.VK_T -> {
                    onTopCheckBox.isSelected = !onTopCheckBox.isSelected
                    onTopCheckBoxClick(null)
                }
                KeyEvent.VK_1 -> {
                    trackDamageCheckBox.isSelected = !trackDamageCheckBox.isSelected
                    trackDamageCheckBoxClick(null)
                }
                KeyEvent.VK_2 -> {
                    trackHealReceivedCheckBox.isSelected = !trackHealReceivedCheckBox.isSelected
                    trackHealReceivedCheckBoxClick(null)
                }
                KeyEvent.VK_3 -> {
                    trackHealAppliedCheckBox.isSelected = !trackHealAppliedCheckBox.isSelected
                    trackHealAppliedCheckBoxClick(null)
                }
                KeyEvent.VK_G -> {
                    godsOnlyCheckBox.isSelected = !godsOnlyCheckBox.isSelected
                    godsOnlyCheckBoxClick(null)
                }
                KeyEvent.VK_R -> { resetCombatButtonClick(null) }
                KeyEvent.VK_C -> { clearTableButtonClick(null) }
            }
        }
    }

    /** Minimizes the sidebar. */
    @Suppress("UNUSED_PARAMETER")
    private fun minimizeSidebarButtonClick(e: ActionEvent?) {
        topLevelAncestor.minimumSize = windowSmallMinSize
        sidebar.isVisible = false
        minimizedBar.isVisible = true
    }

    /** Toggles having the window on top of all other windows. */
    @Suppress("UNUSED_PARAMETER")
    private fun onTopCheckBoxClick(e: ActionEvent?) {
        (topLevelAncestor as JFrame).isAlwaysOnTop = onTopCheckBox.isSelected
    }

    /** Toggles using only using God damage or healing in the output table & DPS calculations. */
    @Suppress("UNUSED_PARAMETER")
    private fun trackDamageCheckBoxClick(e: ActionEvent?) {
        combatTracker.trackDamage = trackDamageCheckBox.isSelected
    }

    /** Toggles using only using God damage or healing in the output table & DPS calculations. */
    @Suppress("UNUSED_PARAMETER")
    private fun trackHealReceivedCheckBoxClick(e: ActionEvent?) {
        combatTracker.trackHealReceived = trackHealReceivedCheckBox.isSelected
    }

    /** Toggles using only using God damage or healing in the output table & DPS calculations. */
    @Suppress("UNUSED_PARAMETER")
    private fun trackHealAppliedCheckBoxClick(e: ActionEvent?) {
        combatTracker.trackHealApplied = trackHealAppliedCheckBox.isSelected
    }

    /** Toggles using only using God damage or healing in the output table & DPS calculations. */
    @Suppress("UNUSED_PARAMETER")
    private fun godsOnlyCheckBoxClick(e: ActionEvent?) {
        combatTracker.godsOnly = godsOnlyCheckBox.isSelected
    }

    /** Resets the name field. */
    @Suppress("UNUSED_PARAMETER")
    private fun nameFieldResetButtonClick(e: ActionEvent?) {
        nameField.text = "Searching..."
        combatTracker.updateIGN("")
    }

    /** Sets the font size for the table rows. */
    @Suppress("UNUSED_PARAMETER")
    private fun rowSizeSpinnerChange(e: ChangeEvent) {
        val rowSize = (rowSizeSpinner.model as SpinnerNumberModel).number.toInt()
        table.font = Font(font.name, font.style, rowSize)
        table.tableHeader.font = table.font
        table.rowHeight = table.font.size + 5
    }

    /** Resets the combat tracker. */
    @Suppress("UNUSED_PARAMETER")
    private fun resetCombatButtonClick(e: ActionEvent?) {
        combatTracker.resetTracking()
    }

    /** Resets the time and clears the table. */
    @Suppress("UNUSED_PARAMETER")
    private fun clearTableButtonClick(e: ActionEvent?) {
        combatTracker.clearTable(false)
        combatTracker.resetTracking()
    }

    /** Reveals the sidebar. */
    @Suppress("UNUSED_PARAMETER")
    private fun maximizeSidebarButtonClick(e: ActionEvent?) {
        sidebar.isVisible = true
        minimizedBar.isVisible = false
        topLevelAncestor.minimumSize = windowSidebarMinSize
    }

    /** Moves the table scroll to the bottom. */
    private fun tableUpdate() {
        val scrollBar = tableScrollPane.verticalScrollBar
        SwingUtilities.invokeLater {
            table.revalidate()
            scrollBar.value = scrollBar.maximum
        }
    }


    /** Updates the combat log field with the new combat log file name. */
    private fun combatLogUpdate(combatLog: File?) {
        combatLogField.text = if (combatLog == null) "no file" else combatLog.name
    }

    /** Toggles the table time column. */
    @Suppress("UNUSED_PARAMETER")
    private fun timeCheckBoxClick(actionEvent: ActionEvent?) {
        setColumnVisible("Time", timeCheckBox.isSelected)
    }

    /** Toggles the table DPS column. */
    @Suppress("UNUSED_PARAMETER")
    private fun dpsCheckBoxClick(actionEvent: ActionEvent?) {
        setColumnVisible("DPS", dpsCheckBox.isSelected)
    }

    /** Toggles the table damage column. */
    @Suppress("UNUSED_PARAMETER")
    private fun damageCheckBoxClick(actionEvent: ActionEvent?) {
        setColumnVisible("Damage", damageCheckBox.isSelected)
    }

    /** Toggles the table total damage column. */
    @Suppress("UNUSED_PARAMETER")
    private fun totalDamageCheckBoxClick(actionEvent: ActionEvent?) {
        setColumnVisible("Σ Damage", totalDamageCheckBox.isSelected)
    }

    /** Toggles the table mitigated column. */
    @Suppress("UNUSED_PARAMETER")
    private fun mitigatedCheckBoxClick(actionEvent: ActionEvent?) {
        setColumnVisible("Mitigated", mitigatedCheckBox.isSelected)
    }

    /** Toggles the table total mitigated column. */
    @Suppress("UNUSED_PARAMETER")
    private fun totalMitigatedCheckBoxClick(actionEvent: ActionEvent?) {
        setColumnVisible("Σ Mitigated", totalMitigatedCheckBox.isSelected)
    }

    /** Toggles the table heal received column. */
    @Suppress("UNUSED_PARAMETER")
    private fun healReceivedCheckBoxClick(actionEvent: ActionEvent?) {
        setColumnVisible("Heal Received", healReceivedCheckBox.isSelected)
    }

    /** Toggles the table total heal received column. */
    @Suppress("UNUSED_PARAMETER")
    private fun totalHealReceivedCheckBoxClick(actionEvent: ActionEvent?) {
        setColumnVisible("Σ Heal Received", totalHealReceivedCheckBox.isSelected)
    }

    /** Toggles the table heal applied column. */
    @Suppress("UNUSED_PARAMETER")
    private fun healAppliedCheckBoxClick(actionEvent: ActionEvent?) {
        setColumnVisible("Heal Applied", healAppliedCheckBox.isSelected)
    }

    /** Toggles the table total heal applied column. */
    @Suppress("UNUSED_PARAMETER")
    private fun totalHealAppliedCheckBoxClick(actionEvent: ActionEvent?) {
        setColumnVisible("Σ Heal Applied", totalHealAppliedCheckBox.isSelected)
    }

    /** Toggles the table reason column. */
    @Suppress("UNUSED_PARAMETER")
    private fun reasonCheckBoxClick(actionEvent: ActionEvent?) {
        setColumnVisible("Reason", reasonCheckBox.isSelected)
    }

/* -------------------------------------------- Util -------------------------------------------- */

    /** Shows or hides the specified column.  */
    private fun setColumnVisible(header: String, isVisible: Boolean) {
        var column: TableColumn? = null
        for (c in table.columnModel.columns)
            if (c.headerValue as String == header) column = c

        if (column != null) {
            // visible
            if (isVisible) {
                column.maxWidth = colMaxWidth
                column.minWidth = colMinWidth
                column.preferredWidth = 66
            }
            // hidden
            else {
                column.minWidth = 0
                column.maxWidth = 0
            }
        }
    }

    /** Creates a JPanel with a JLabel followed by the specified JComponents. */
    private class LabelPanel(label: String, vararg components: JComponent, gap: Int = 0)
        : JPanel(GridBagLayout()) {

        init {
            val c = GridBagConstraints()

            // label
            c.gridx = 0; c.gridy = 0
            add(JLabel(label).apply {
                if (gap > 0) {
                    preferredSize = Dimension(preferredSize.width + gap, preferredSize.height)
                }
            }, c)

            // components
            c.insets = Insets(0, 5, 0, 0)
            for (component in components) {
                c.gridx++
                add(component, c)
            }
        }
    }
}
