package smitedps

import java.io.File
import javax.swing.SwingUtilities
import javax.swing.table.DefaultTableModel

class DPSTracker {

    /** The table model that the tracker updates. */
    var tableModel = DefaultTableModel()

    private var ign = ""
    private var updatedIGN: String? = null

    private var combatLog: File? = null

    /** When true, starts DPS timer on next damage dealt. */
    private var resetTimer = true

    /** Tells the DPS tracker to update the in-game name. */
    fun updateIGN(ign: String) {
        if (this.ign != ign && updatedIGN != ign)
            updatedIGN = ign
    }

    /** Tracks the DPS using [ign] and [combatLog] to update [tableModel]. */
    fun run() {

        // true if not updating because there is no combat log, or reached eof
        var waiting = true

        while (true) {

            // start tracking
            if (!waiting) {
                clearDPSTable()
                val br = combatLog!!.bufferedReader()
                resetTimer = true
                var startTime = -1.0
                var totalDamage = 0
                var prevTime = -1.0
                var firstReset = true
                var eofReached = false

                // if ign hasn't been updated and didn't reach eof, keep tracking
                while (updatedIGN == null && !eofReached) {
                    val line = br.readLine()

                    // end of file
                    if (line == "end") {
                        waiting = true
                        eofReached = true
                    }

                    // all other lines
                    else if (line != null && line != "") {

                        val lineData = readLineData(line)
                        val type = lineData[1]

                        // damage dealt
                        if (type == "DIT_Damage" || type == "DIT_CritDamage") {
                            val source = lineData[9]
                            val damage = lineData[12].toInt()
                            val time = lineData[8].toDouble()
                            val reason = lineData[7]

                            // damage dealt by user
                            if (source.toLowerCase() == ign) {

                                if (resetTimer) {
                                    // hits that happened at the same time as the previous get
                                    // updated normally
                                    if (time - prevTime == 0.0) {
                                        totalDamage += damage
                                        addDPSRow(totalDamage, startTime, time, damage, reason)
                                    }
                                    // Timer reset on this hit
                                    else {
                                        resetTimer = false
                                        startTime = time
                                        totalDamage = damage

                                        if (!firstReset)
                                            addTableRow("", "", "", "DPS Timer Reset")
                                        addTableRow("0.00s", "0.00", damage.toString(), reason)
                                        firstReset = false
                                    }
                                } else {
                                    totalDamage += damage
                                    addDPSRow(totalDamage, startTime, time, damage, reason)
                                }
                                prevTime = time
                            }
                        }
                    } else Thread.sleep(500)
                }
                br.close()
            }

            // update ign
            if (updatedIGN != null) {
                ign = updatedIGN!!
                updatedIGN = null
                waiting = false
            }

            // use newest combat log
            val foundCombatLog = CombatLogFinder.search()
            if (foundCombatLog != combatLog) {
                combatLog = foundCombatLog
                waiting = false
            }

            // if there is no combat log, refresh slowly
            if (waiting) Thread.sleep(1000)
        }
    }

    /** Resets the DPS timer for the DPS calculation. */
    fun resetTimer() { resetTimer = true }

    /** Reads the data from a single log file line and stores it in a String list. */
    private fun readLineData(line: String): List<String> {

        // log type that uses "," delimiters
        return if (line.contains("{")) {
            line.trim { it == '{' || it == '}' || it == ','}
                .split(",")
                .map {
                    it.trim()
                        .split(":")[1]
                        .trim { c -> c == '"' }
                }
        }

        // log type that uses "|" delimiters
        else {
            line.split("|")
                .map {
                    if (it.contains("=")) it.split("=")[1]
                    else it
                }
        }
    }

    /** Safely adds a row to the dps table. */
    private fun addTableRow(time: String, dps: String, damage: String, reason: String) {
        SwingUtilities.invokeLater { tableModel.addRow(arrayOf(time, dps, damage, reason)) }
    }

    /** Safely removes all rows from the dps table. */
    private fun clearDPSTable() { SwingUtilities.invokeLater { tableModel.rowCount = 0 } }

    /** Calculates dps and creates a new row for the dps table. */
    private fun addDPSRow(totalDamage: Int,
                          startTime: Double,
                          endTime: Double,
                          damage: Int,
                          reason: String) {

        val timeStr = "${"%.2f".format(endTime - startTime)}s"
        val dpsStr = "%.2f".format(totalDamage / (endTime - startTime))
            .replace("Infinity", "0.00")

        addTableRow(timeStr, dpsStr, damage.toString(), reason)
    }
}
