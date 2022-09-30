/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps.gui.sidebar

import antd.sdps.SharedInstances.columnCheckBoxesPanel
import antd.sdps.SharedInstances.combatTracker
import antd.sdps.SharedInstances.initConfig
import antd.sdps.SharedInstances.mainJFrame
import antd.sdps.SharedInstances.minSidebarPanel
import antd.sdps.SharedInstances.outputTable
import antd.sdps.gui.components.LabelPanel
import antd.sdps.gui.OutputTable
import java.awt.*
import javax.swing.*

/** Sidebar panel containing the majority of the user controls. */
class SidebarPanel : JPanel(GridBagLayout()) {

/* ----------------------------------------- UI Content ----------------------------------------- */

    val minimizeSidebarButton = JButton()
        .apply {
            preferredSize = Dimension(20, 10)
            toolTipText = "Minimize the sidebar (Esc)"

            addActionListener {
                minSidebarPanel.isVisible = true
                this@SidebarPanel.isVisible = false
                mainJFrame.updateMinSize(false)
            }
        }

    val onTopCheckBox = JCheckBox("Window Always On Top")
        .apply {
            toolTipText = "Force this window to display on top of Smite and other windows (T)"
            isSelected = initConfig.onTop

            addActionListener {
                mainJFrame.isAlwaysOnTop = (it.source as JCheckBox).isSelected
            }
        }

    val trackDamageCheckBox = JCheckBox("Track Damage")
        .apply {
            toolTipText = "Track & print damage onto the table (1)"
            background = OutputTable.damageColor
            isSelected = initConfig.trackDamage

            addActionListener {
                combatTracker.trackDamage = (it.source as JCheckBox).isSelected
            }
        }

    val trackHealReceivedCheckBox = JCheckBox("Track Heal Received")
        .apply {
            toolTipText = "Track & print heal received onto the table (2)"
            background = OutputTable.healReceivedColor
            isSelected = initConfig.trackHealReceived

            addActionListener {
                combatTracker.trackHealReceived = (it.source as JCheckBox).isSelected
            }
        }

    val trackHealAppliedCheckBox = JCheckBox("Track Heal Applied")
        .apply {
            toolTipText = "Track & print heal applied onto the table (3)"
            background = OutputTable.healAppliedColor
            isSelected = initConfig.trackHealApplied

            addActionListener {
                combatTracker.trackHealApplied = (it.source as JCheckBox).isSelected
            }
        }

    val godsOnlyCheckBox = JCheckBox("Gods Only")
        .apply {
            toolTipText = "Ignore minions/structures (G)"
            isSelected = initConfig.godsOnly

            addActionListener {
                combatTracker.godsOnly = (it.source as JCheckBox).isSelected
            }
        }

    val autoResetCheckBox = JCheckBox("Auto-Reset")
        .apply {
            toolTipText = "Automatically reset after 5 seconds of inactivity (Ctrl+R)"
            isSelected = initConfig.autoReset

            addActionListener {
                TODO()
            }
        }

    val nameField = JTextField(10)
        .apply {
            text = initConfig.ign ?: "Searching..."
            isEditable = false
        }

    private val nameFieldResetButton = JButton("↺")
        .apply {
            toolTipText = "Resets the in-game name tracked; will be set on the next tick of damage"

            addActionListener {
                nameField.text = "Searching..."
                combatTracker.updateIGN()
            }
        }

    val combatLogField = JTextField(10)
        .apply {
            text = "No file"
            isEditable = false
        }

    private val combatLogResetButton = JButton("↺")
        .apply {
            toolTipText = "Loads the most recent combat log file"

            addActionListener {
                combatTracker.reloadCombatLog()
            }
        }

    private val rowSizeSpinner = JSpinner(SpinnerNumberModel(initConfig.rowSize, 1, 100, 1))
        .apply {
            toolTipText = "Adjust table font size"
            model.value = initConfig.rowSize

            addChangeListener {
                val rowSize = ((it.source as JSpinner).model as SpinnerNumberModel).number.toInt()
                outputTable.font = Font(font.name, font.style, rowSize)
                outputTable.tableHeader.font = outputTable.font
                outputTable.rowHeight = outputTable.font.size + 5
            }
        }

    val resetCombatButton = JButton("Reset")
        .apply {
            toolTipText = "Makes your next tick of damage reset the DPS timer and resets damage " +
                    "& healing totals (R)"

            addActionListener {
                combatTracker.resetTracking()
            }
        }

    val clearTableButton = JButton("Clear Table")
        .apply {
            toolTipText = "Clears the table of all rows and resets (C)"

            addActionListener {
                outputTable.clearTable()
                combatTracker.resetTracking()
            }
        }

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
        add(godsOnlyCheckBox, c)

        // auto-reset checkbox
        c.gridy++
        c.insets = Insets(0, 0, 10, 0)
        add(autoResetCheckBox, c)

        // in-game name field
        c.gridy++
        add(LabelPanel("In-Game Name", nameField, nameFieldResetButton), c)

        // combat log
        c.gridy++
        add(LabelPanel("Combat Log", combatLogField, combatLogResetButton, gap = 14), c)

        // row size spinner
        c.gridy++
        add(LabelPanel("Table Font Size", rowSizeSpinner, gap = 82), c)

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
        JScrollPane(columnCheckBoxesPanel).apply {
            horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
            verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
            minimumSize = Dimension(0, 0)
            preferredSize = Dimension(0, 0)
        }.also { add(it, c) }

        minimumSize = preferredSize

        // load init config
        if (!initConfig.sidebar) isVisible = false
    }
}
