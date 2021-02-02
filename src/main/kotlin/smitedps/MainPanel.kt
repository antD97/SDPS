package smitedps

import java.awt.*
import java.awt.event.ActionEvent
import java.io.File
import javax.swing.*
import javax.swing.table.DefaultTableModel

/** The main UI panel. */
class MainPanel(private val dpsTracker: DPSTracker) : JPanel(GridBagLayout()) {

    private val nameFile = File("in-game_name.txt")

    private val dpsTable = JTable(
        DefaultTableModel(arrayOf(), arrayOf("Time", "DPS", "Damage", "Reason")))
        .apply { columnModel.getColumn(3).preferredWidth = 175 }

    private val alwaysOnTopCheckBox = JCheckBox("Window always on top")
        .apply { addActionListener(::alwaysOnTopCheckBoxPress) }

    private val nameField = JTextField(10)
        .apply { addActionListener(::nameFieldEnterPress) }
    private val nameSaveButton = JButton(UIManager.getIcon("FileView.floppyDriveIcon"))
        .apply {
            preferredSize = Dimension(45, 30)
            addActionListener(::nameSaveButtonPress)
            toolTipText = "Save the current name to file"
        }

    private val combatLogField = JTextField(14)
        .apply {
            text = "no file"
            isEditable = false
        }

    private val resetTimerButton = JButton("Reset DPS Timer")
        .apply { addActionListener(::resetTimerButtonPress) }

    init {
        val c = GridBagConstraints()

        // main table
        c.gridx = 1
        c.weightx = 1.0; c.weighty = 1.0
        c.fill = GridBagConstraints.BOTH
        c.insets = Insets(10, 10, 10, 10)
        JScrollPane(dpsTable).apply { preferredSize = Dimension(300, 300) }.also { add(it, c) }

        // sidebar
        c.gridx = 0
        c.weightx = 0.0
        c.insets = Insets(10, 10, 10, 0)
        JPanel(GridBagLayout()).apply {
            val c2 = GridBagConstraints()

            // title
            c2.gridx = 0; c2.gridy = 0
            c2.weightx = 1.0; c2.weighty = 0.0
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

            // reset timer button
            c2.weighty = 1.0
            c2.gridy++
            c2.anchor = GridBagConstraints.PAGE_START
            add(resetTimerButton, c2)

            minimumSize = preferredSize

        }.also { add(it, c) }

        nameField.text = loadName()
        dpsTracker.updateIGN(nameField.text)
        dpsTracker.tableModel = dpsTable.model as DefaultTableModel
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

    /** Toggles having the window on top of all other windows. */
    @Suppress("UNUSED_PARAMETER")
    private fun alwaysOnTopCheckBoxPress(e: ActionEvent?) {
        (topLevelAncestor as JFrame).isAlwaysOnTop = alwaysOnTopCheckBox.isSelected
    }

    /** Updates the in-game name used by the DPS tracker. */
    @Suppress("UNUSED_PARAMETER")
    private fun nameFieldEnterPress(e: ActionEvent?) { dpsTracker.updateIGN(nameField.text) }

    /** Saves the name in [nameField] to "in-game_name.txt". */
    @Suppress("UNUSED_PARAMETER")
    private fun nameSaveButtonPress(e: ActionEvent?) {
        when {
            nameFile.isDirectory -> JOptionPane.showMessageDialog(this,
                "Could not save because \"in-game_name.txt\" is already a folder.")

            nameField.text == "" -> nameFile.delete()
            else -> nameFile.writeText(nameField.text)
        }
    }

    /** Resets the timer used by the DPS tracker. */
    @Suppress("UNUSED_PARAMETER")
    private fun resetTimerButtonPress(e: ActionEvent?) { dpsTracker.resetTimer() }
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
