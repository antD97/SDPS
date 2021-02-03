/*
 * Copyright © 2021 antD97
 * Licensed under the MIT License https://mit-license.org/
 */
package sdps

import java.io.File
import javax.swing.SwingUtilities
import javax.swing.table.DefaultTableModel

class DPSTracker {

    /** The table model that the tracker updates. */
    var dpsTableModel = DefaultTableModel()
    private var dpsTableListeners = mutableListOf<() -> Unit>()

    private var ign = ""
    private var updatedIGN: String? = null

    private var combatLog: File? = null
        set(value) {
            field = value
            combatLogListeners.forEach{ it(value) }
        }
    private var combatLogLastModified = 0L
    private var combatLogListeners = mutableListOf<(File?) -> Unit>()

    /** When true, starts DPS timer on next damage dealt. */
    private var resetTimer = true
    /** When true, dps is not updated. */
    private var waiting = true

    /** Tells the DPS tracker to update the in-game name. */
    fun updateIGN(ign: String) { updatedIGN = ign }

    /** Tracks the DPS using [ign] and [combatLog] to update [dpsTableModel]. */
    fun run() {

        while (true) {

            // start tracking
            if (!waiting) {
                clearDPSTable()
                val br = combatLog!!.bufferedReader()
                resetTimer = true
                var startTime = -1.0
                var totalDamage = 0
                var prevTime = -1.0
                var eofReached = false

                // if ign hasn't been updated and didn't reach eof, keep tracking
                while (updatedIGN == null && !eofReached) {
                    val line = br.readLine()

                    // end of file
                    if (line == "end" || line == ",{\"eventType\":\"end\"}") {
                        waiting = true
                        eofReached = true
                        addTableRow("","","","End of file. Searching...")
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
                                    // have to be go before the "DPS Timer Reset" row
                                    if (time - prevTime == 0.0 && dpsTableModel.rowCount != 0) {
                                        totalDamage += damage
                                        removeTableRow(dpsTableModel.rowCount - 1)
                                        addDPSRow(totalDamage, startTime, time, damage, reason)
                                        addTableRow("", "", "", "DPS Timer Reset")
                                    }
                                    // Timer reset on this hit
                                    else {
                                        resetTimer = false
                                        startTime = time
                                        totalDamage = damage

                                        addTableRow("0.00s", "0.00", damage.toString(), reason)
                                    }
                                } else {
                                    totalDamage += damage
                                    addDPSRow(totalDamage, startTime, time, damage, reason)
                                }
                                prevTime = time
                            }
                        }
                    } else Thread.sleep(100)
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
            if (foundCombatLog != null) {

                if (combatLog != null) {

                    // found is newer than current
                    if (foundCombatLog.lastModified() > combatLogLastModified) {
                        combatLog = foundCombatLog
                        combatLogLastModified = combatLog!!.lastModified()
                        waiting = false
                    }
                }
                // found first combat log
                else {
                    combatLog = foundCombatLog
                    combatLogLastModified = combatLog!!.lastModified()
                    waiting = false
                }
            }

            // if there is no combat log, refresh slowly
            if (waiting) Thread.sleep(3000)
        }
    }

    // this is used in a button event, so invoke later is not used
    /** Clears the DPS log and resets the timer. */
    fun clearLog() { dpsTableModel.rowCount = 0 }

    // this is used in a button event, so invoke later is not used
    /** Resets the DPS timer for the DPS calculation. */
    fun resetTimer() {
        if (!resetTimer && !waiting) {
            resetTimer = true
            dpsTableModel.addRow(arrayOf("", "", "", "DPS Timer Reset"))
            dpsTableListeners.forEach { it() }
        }
    }

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
        SwingUtilities.invokeAndWait { dpsTableModel.addRow(arrayOf(time, dps, damage, reason)) }
        dpsTableListeners.forEach { it() }
    }

    /** Safely removes a row from the dps table. */
    private fun removeTableRow(i: Int) {
        SwingUtilities.invokeAndWait { dpsTableModel.removeRow(i) }
    }

    /** Safely removes all rows from the dps table. */
    private fun clearDPSTable() {
        SwingUtilities.invokeAndWait { dpsTableModel.rowCount = 0 }
        dpsTableListeners.forEach { it() }
    }

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

    /** Adds a dps table listener. */
    fun addDPSTableListener(listener: () -> Unit) { dpsTableListeners.add(listener) }

    /** Adds a combat log update listener. */
    fun addCombatLogListener(listener: (File?) -> Unit) { combatLogListeners.add(listener) }
}
