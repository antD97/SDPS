package smitedps

import java.awt.*
import java.awt.event.ActionEvent
import java.io.File
import javax.swing.*
import javax.swing.table.DefaultTableModel

/** The main UI panel. */
class MainPanel(private val dpsTracker: DPSTracker) : JPanel(GridBagLayout()) {

    private val dpsTable = JTable(arrayOf(), arrayOf("Time", "DPS", "Reason", "Damage"))
        .apply { model = DefaultTableModel() }

    private val nameField = JTextField(10)
    private val nameRefreshButton = JButton("↻")
        .apply {
            preferredSize = Dimension(45, 30)
            addActionListener(::nameRefreshButtonPress)
        }

    private val combatLogField = JTextField(10)
        .apply {
            text = "no file"
            isEditable = false
        }
    private val combatLogRefreshButton = JButton("↻")
        .apply {
            preferredSize = Dimension(45, 30)
            addActionListener(::combatLogRefreshButtonPress)
        }
    private val combatLogFileButton = JButton(UIManager.getIcon("FileView.fileIcon"))
        .apply {
            preferredSize = Dimension(45, 30)
            addActionListener(::combatLogFileButtonPress)
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
        JScrollPane(dpsTable).also { add(it, c) }

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

            // in-game name input field
            c2.gridy++
            c2.fill = GridBagConstraints.NONE
            c2.anchor = GridBagConstraints.FIRST_LINE_START
            add(LabelPanel("In-game name", nameField, nameRefreshButton), c2)

            // combat log file
            c2.gridy++
            add(LabelPanel("Combat log file", combatLogField, combatLogRefreshButton, combatLogFileButton), c2)

            // reset timer button
            c2.weighty = 1.0
            c2.gridy++
            c2.anchor = GridBagConstraints.PAGE_START
            add(resetTimerButton, c2)

        }.also { add(it, c) }

        // load name & combat log at startup
        nameField.text = loadName()
        combatLogRefreshButtonPress(null)

        dpsTracker.tableModel = dpsTable.model as DefaultTableModel?
    }

    /** Tries to load the saved name. If it can't be found, returns an empty string. */
    private fun loadName(): String {
        val nameFile = File("in-game_name.txt")

        if (nameFile.isFile) {
            val value = nameFile.bufferedReader().readLine()
            if (value != null && value != "") return value
        }
        else if (!nameFile.exists()) nameFile.createNewFile()

        return ""
    }

/* ------------------------------------------ Listeners ----------------------------------------- */

    private fun nameRefreshButtonPress(e: ActionEvent?) { dpsTracker.updateIGN(nameField.text) }

    private fun combatLogRefreshButtonPress(e: ActionEvent?) {
        val combatLog = CombatLogFinder.auto()
        if (combatLog != null) {
            dpsTracker.combatLog = combatLog
            combatLogField.text = combatLog.name
        }
    }

    private fun combatLogFileButtonPress(e: ActionEvent?) {
        val combatLog = CombatLogFinder.manual(this)
        if (combatLog != null) {
            dpsTracker.combatLog = combatLog
            combatLogField.text = combatLog.name
        }
    }

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
