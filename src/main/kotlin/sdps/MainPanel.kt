/*
 * Copyright © 2021 antD97
 * Licensed under the MIT License https://mit-license.org/
 */
package sdps

import java.awt.*
import java.awt.event.ActionEvent
import java.io.File
import javax.swing.*
import javax.swing.table.DefaultTableModel

/** The main UI panel. */
class MainPanel(private val dpsTracker: DPSTracker) : JPanel(GridBagLayout()) {

    private val nameFile = File("in-game_name.txt")

    private var windowSidebarMinSize: Dimension? = null
    private val windowSmallMinSize = Dimension(150, 100)

/* --------------------------------------- GUI Components --------------------------------------- */

    private val dpsTable = JTable(DefaultTableModel(arrayOf(), arrayOf("Time", "DPS", "Damage", "Reason")))
        .apply {
            setDefaultEditor(Object::class.java, null)
            columnModel.getColumn(3).preferredWidth = 175
        }
    private val dpsTableScrollPane = JScrollPane(dpsTable)
        .apply { preferredSize = Dimension(350, 300) }

    private val sidebar = JPanel(GridBagLayout())

    private val minimizeSidebarButton = JButton()
        .apply {
            preferredSize = Dimension(20, 10)
            addActionListener(::minimizeSidebarButtonPress)
            toolTipText = "Minimize the sidebar"
        }

    private val alwaysOnTopCheckBox = JCheckBox("Window always on top")
        .apply {
            isSelected = true
            addActionListener(::alwaysOnTopCheckBoxPress)
        }

    private val nameField = JTextField(10)
        .apply { addActionListener(::nameFieldEnterPress) }
    private val nameSaveButton = JButton(UIManager.getIcon("FileView.floppyDriveIcon"))
        .apply {
            preferredSize = Dimension(45, 30)
            addActionListener(::nameSaveButtonPress)
            toolTipText = "Save the current name to file"
        }

    private val combatLogField = JTextField(15)
        .apply {
            text = "no file"
            isEditable = false
        }

    private val clearLogButton = JButton("Clear Log")
        .apply { addActionListener(::clearLogButtonPress) }
    private val resetTimerButton = JButton("Reset DPS Timer")
        .apply { addActionListener(::resetTimerButtonPress) }

    private val minimizedBar = JPanel(GridBagLayout())
    private val maximizeSidebarButton = JButton()
        .apply {
            preferredSize = Dimension(10, 20)
            addActionListener(::maximizeSidebarButtonPress)
            toolTipText = "Maximize the sidebar"
        }

/* ----------------------------------------- Constructor ---------------------------------------- */

    init {
        val c = GridBagConstraints()

        // main table
        c.gridx = 2
        c.weightx = 1.0; c.weighty = 1.0
        c.fill = GridBagConstraints.BOTH
        c.insets = Insets(10, 0, 10, 10)
        add(dpsTableScrollPane, c)

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
            add(JLabel("Smite DPS Calculator"), c2)

            c2.gridy++
            c2.fill = GridBagConstraints.HORIZONTAL
            add(JSeparator(), c2)

            // always on top checkbox
            c2.gridy++
            c2.fill = GridBagConstraints.NONE
            c2.anchor = GridBagConstraints.FIRST_LINE_START
            add(alwaysOnTopCheckBox, c2)

            // in-game name input field
            c2.gridy++
            add(LabelPanel("In-game name", nameField, nameSaveButton), c2)

            // combat log file
            c2.gridy++
            add(LabelPanel("Combat log file", combatLogField), c2)

            // 2x button group
            c2.gridy++
            c2.weighty = 1.0
            c2.fill = GridBagConstraints.HORIZONTAL
            c2.anchor = GridBagConstraints.PAGE_START
            JPanel(GridBagLayout()).apply {
                val c3 = GridBagConstraints()

                // clear log button
                c3.gridx = 0; c3.gridy = 0
                add(resetTimerButton, c3)

                // reset timer button
                c3.gridx++
                c3.insets = Insets(0, 15, 0, 0)
                add(clearLogButton, c3)
            }.also { add(it, c2) }

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
            isVisible = false

        }.also { add(it, c) }

        nameField.text = loadName()
        dpsTracker.updateIGN(nameField.text.toLowerCase())

        dpsTracker.dpsTableModel = dpsTable.model as DefaultTableModel

        dpsTracker.addDPSTableListener(::dpsTableUpdate)
        dpsTracker.addCombatLogListener(::combatLogUpdate)
    }

    /** Tries to load the saved name. If it can't be found, returns null. */
    private fun loadName(): String? {
        if (nameFile.isFile) {
            val lines = nameFile.readLines()
            if (lines.isNotEmpty() && lines[0] != "")
                return lines [0]
        }
        return null
    }

/* ------------------------------------------ Listeners ----------------------------------------- */

    /** Minimizes the sidebar. */
    @Suppress("UNUSED_PARAMETER")
    private fun minimizeSidebarButtonPress(e: ActionEvent?) {

        // change min size
        if (windowSidebarMinSize == null) windowSidebarMinSize = topLevelAncestor.minimumSize
        topLevelAncestor.minimumSize = windowSmallMinSize

        sidebar.isVisible = false
        minimizedBar.isVisible = true
    }

    /** Toggles having the window on top of all other windows. */
    @Suppress("UNUSED_PARAMETER")
    private fun alwaysOnTopCheckBoxPress(e: ActionEvent?) {
        (topLevelAncestor as JFrame).isAlwaysOnTop = alwaysOnTopCheckBox.isSelected
    }

    /** Updates the in-game name used by the DPS tracker. */
    @Suppress("UNUSED_PARAMETER")
    private fun nameFieldEnterPress(e: ActionEvent?) { dpsTracker.updateIGN(nameField.text.toLowerCase()) }

    /** Saves the name in [nameField] to "in-game_name.txt". */
    @Suppress("UNUSED_PARAMETER")
    private fun nameSaveButtonPress(e: ActionEvent?) {
        nameFieldEnterPress(null)
        when {
            nameFile.isDirectory -> JOptionPane.showMessageDialog(this,
                "Could not save because \"in-game_name.txt\" is already a folder.")

            nameField.text == "" -> nameFile.delete()
            else -> nameFile.writeText(nameField.text)
        }
    }

    /** Clears the dps table of rows. */
    @Suppress("UNUSED_PARAMETER")
    private fun clearLogButtonPress(e: ActionEvent?) { dpsTracker.clearLog() }

    /** Resets the timer used by the DPS tracker. */
    @Suppress("UNUSED_PARAMETER")
    private fun resetTimerButtonPress(e: ActionEvent?) { dpsTracker.resetTimer() }

    /** Reveals the sidebar. */
    @Suppress("UNUSED_PARAMETER")
    private fun maximizeSidebarButtonPress(e: ActionEvent?) {
        sidebar.isVisible = true
        minimizedBar.isVisible = false
        topLevelAncestor.minimumSize = windowSidebarMinSize
    }

    /** Moves the table scroll to the bottom. */
    private fun dpsTableUpdate() {
        val scrollBar = dpsTableScrollPane.verticalScrollBar
        SwingUtilities.invokeLater {
            dpsTable.revalidate()
            scrollBar.value = scrollBar.maximum
        }
    }

    /** Updates the combat log field with the new combat log file name. */
    private fun combatLogUpdate(combatLog: File?) {
        combatLogField.text = if (combatLog == null) "no file" else combatLog.name
    }
}

/** Creates a JPanel with a JLabel followed by the specified JComponents. */
private class LabelPanel(label: String, vararg components: JComponent) : JPanel(GridBagLayout()) {
    init {
        val c = GridBagConstraints()

        // label
        c.gridx = 0; c.gridy = 0
        add(JLabel(label), c)

        // components
        c.insets = Insets(0, 5, 0, 0)
        for (component in components) {
            c.gridx++
            add(component, c)
        }
    }
}
