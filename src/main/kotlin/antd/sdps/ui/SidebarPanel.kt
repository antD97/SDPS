/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps.ui

import antd.sdps.ConfigManager
import antd.sdps.combat.CombatTracker
import java.awt.*
import java.awt.event.ActionEvent
import javax.swing.*
import javax.swing.event.ChangeEvent

class SidebarPanel(
    private val combatTracker: CombatTracker,
    configData: ConfigManager.ConfigData,
    private val outputTable: OutputTable
) : JPanel(GridBagLayout()) {

    // minimized sidebar is instantiated after this sidebar panel
    var minimizedSidebar: JPanel? = null

/* ----------------------------------------- UI Content ----------------------------------------- */

    private val minimizeSidebarButton = JButton()
        .apply {
            preferredSize = Dimension(20, 10)
            addActionListener(::minimizeSidebarButtonClick)
            toolTipText = "Minimize the sidebar (Esc)"
        }

    val onTopCheckBox = JCheckBox("Window Always On Top")
        .apply {
            addActionListener(::onTopCheckBoxClick)
            toolTipText = "Force this window to display on top of Smite and other windows (T)"
        }

    val trackDamageCheckBox = JCheckBox("Track Damage")
        .apply {
            addActionListener(::trackDamageCheckBoxClick)
            toolTipText = "Track & print damage onto the table (1)"
            isSelected = true
            background = OutputTable.damageColor
        }

    val trackHealReceivedCheckBox = JCheckBox("Track Heal Received")
        .apply {
            addActionListener(::trackHealReceivedCheckBoxClick)
            toolTipText = "Track & print heal received onto the table (2)"
            isSelected = false
            background = OutputTable.healReceivedColor
        }

    val trackHealAppliedCheckBox = JCheckBox("Track Heal Applied")
        .apply {
            addActionListener(::trackHealAppliedCheckBoxClick)
            toolTipText = "Track & print heal applied onto the table (3)"
            isSelected = false
            background = OutputTable.healAppliedColor
        }

    val godsOnlyCheckBox = JCheckBox("Gods Only")
        .apply {
            addActionListener(::godsOnlyCheckBoxClick)
            toolTipText = "Ignore minions/structures (G)"
            isSelected = true
        }

    val nameField = JTextField(8)
        .apply {
            text = "Searching..."
            isEditable = false
        }

    private val nameFieldResetButton = JButton("↺")
        .apply {
            addActionListener(::nameFieldResetButtonClick)
            toolTipText = "Resets the in-game name tracked; will be set on the next tick of damage"
        }

    val combatLogField = JTextField(13)
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
            toolTipText = "Makes your next tick of damage reset the DPS timer and resets damage " +
                    "& healing totals (R)"
        }

    private val clearTableButton = JButton("Clear Table")
        .apply {
            addActionListener(::clearTableButtonClick)
            toolTipText = "Clears the table of all rows and resets (C)"
        }

    val columnCheckboxesPanel = ColumnCheckboxesPanel(outputTable)

/* -------------------------------------------- Init -------------------------------------------- */

    init {
        val c = GridBagConstraints()

        // minimize sidebar button
        c.gridx = 0
        c.gridy = 0
        c.weightx = 1.0
        c.weighty = 0.0
        c.anchor = GridBagConstraints.LINE_START
        c.insets = Insets(0, 0, 5, 0)
        add(minimizeSidebarButton, c)

        // title
        c.gridy++
        c.anchor = GridBagConstraints.PAGE_START
        c.insets = Insets(0, 0, 10, 0)
        add(JLabel("Smite Damage Tracker"), c)

        // separator
        c.gridy++
        c.fill = GridBagConstraints.HORIZONTAL
        add(JSeparator(), c)

        // on top checkbox
        c.gridy++
        c.fill = GridBagConstraints.NONE
        c.anchor = GridBagConstraints.FIRST_LINE_START
        c.insets = Insets(0, 0, 0, 0)
        add(onTopCheckBox, c)

        // track damage checkbox
        c.gridy++
        c.fill = GridBagConstraints.HORIZONTAL
        add(trackDamageCheckBox, c)

        // track heal received checkbox
        c.gridy++
        add(trackHealReceivedCheckBox, c)

        // track damage checkbox
        c.gridy++
        add(trackHealAppliedCheckBox, c)

        // gods only checkbox
        c.gridy++
        c.fill = GridBagConstraints.NONE
        c.insets = Insets(0, 0, 10, 0)
        add(godsOnlyCheckBox, c)

        // in-game name field
        c.gridy++
        add(LabelPanel("In-game name", nameField, nameFieldResetButton), c)

        // combat log file
        c.gridy++
        add(LabelPanel("Combat log file", combatLogField), c)

        // row size spinner
        c.gridy++
        add(LabelPanel("Table Font Size", rowSizeSpinner, gap = 0), c)

        // 2x button group
        c.gridy++
        c.fill = GridBagConstraints.HORIZONTAL
        JPanel(GridBagLayout()).apply {
            val c2 = GridBagConstraints()

            // reset combat button
            c2.gridx = 0; c2.gridy = 0
            add(resetCombatButton, c2)

            // clear table button
            c2.gridx++
            c2.insets = Insets(0, 15, 0, 0)
            add(clearTableButton, c2)
        }.also { add(it, c) }

        // checkboxes
        c.gridy++
        c.weighty = 1.0
        c.fill = GridBagConstraints.BOTH
        c.anchor = GridBagConstraints.PAGE_START
        JScrollPane(columnCheckboxesPanel).apply {
            horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
            verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
            minimumSize = Dimension(0, 0)
            preferredSize = Dimension(0, 0)
        }.also { add(it, c) }

        minimumSize = preferredSize
    }

/* ------------------------------------------ Listeners ----------------------------------------- */

    @Suppress("UNUSED_PARAMETER")
    fun minimizeSidebarButtonClick(e: ActionEvent?) {
        topLevelAncestor.minimumSize = MainJFrame.sidebarEnabledMinSize
        isVisible = false
        minimizedSidebar?.isVisible = true
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTopCheckBoxClick(e: ActionEvent?) {
        (topLevelAncestor as JFrame).isAlwaysOnTop = onTopCheckBox.isSelected
    }

    @Suppress("UNUSED_PARAMETER")
    fun trackDamageCheckBoxClick(e: ActionEvent?) {
        combatTracker.trackDamage = trackDamageCheckBox.isSelected
    }

    @Suppress("UNUSED_PARAMETER")
    fun trackHealReceivedCheckBoxClick(e: ActionEvent?) {
        combatTracker.trackHealReceived = trackHealReceivedCheckBox.isSelected
    }

    @Suppress("UNUSED_PARAMETER")
    fun trackHealAppliedCheckBoxClick(e: ActionEvent?) {
        combatTracker.trackHealApplied = trackHealAppliedCheckBox.isSelected
    }

    @Suppress("UNUSED_PARAMETER")
    fun godsOnlyCheckBoxClick(e: ActionEvent?) {
        combatTracker.godsOnly = godsOnlyCheckBox.isSelected
    }

    @Suppress("UNUSED_PARAMETER")
    fun nameFieldResetButtonClick(e: ActionEvent?) {
        nameField.text = "Searching..."
        combatTracker.updateIGN("")
    }

    @Suppress("UNUSED_PARAMETER")
    private fun rowSizeSpinnerChange(e: ChangeEvent) {
        val rowSize = (rowSizeSpinner.model as SpinnerNumberModel).number.toInt()
        outputTable.font = Font(font.name, font.style, rowSize)
        outputTable.tableHeader.font = outputTable.font
        outputTable.rowHeight = outputTable.font.size + 5
    }

    @Suppress("UNUSED_PARAMETER")
    fun resetCombatButtonClick(e: ActionEvent?) {
        combatTracker.resetTracking()
    }

    @Suppress("UNUSED_PARAMETER")
    fun clearTableButtonClick(e: ActionEvent?) {
        combatTracker.clearTable(false)
        combatTracker.resetTracking()
    }
}
