/*
 * Copyright © 2021 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package sdps

import java.io.File
import javax.swing.SwingUtilities
import javax.swing.table.DefaultTableModel

/** Monitors the newest combat log file and updates a table with DPS information. */
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
    /** The time of when reset was clicked. */
    private var resetTimerTime = 0L
    /** When true, dps is not updated. */
    private var waiting = true

    /** Tells the DPS tracker to update the in-game name. */
    fun updateIGN(ign: String) { updatedIGN = ign.toLowerCase() }

    /** Tracks the DPS using [ign] and [combatLog] to update [dpsTableModel]. */
    fun run() {

        while (true) {

            // start tracking
            if (!waiting) {
                clearDPSTable()
                val br = combatLog!!.bufferedReader()
                resetTimer = true
                var startTime = -1L
                var startTimeST = -1L   // start time in system time
                resetTimerTime = 0L
                var totalDamage = 0
                var totalMitigated = 0
                var eofReached = false

                // if ign hasn't been updated and didn't reach eof, keep tracking
                while (updatedIGN == null && !eofReached) {
                    val line = br.readLine()

                    // end of file
                    if (line == "end" || line == ",{\"eventType\":\"end\"}") {
                        waiting = true
                        eofReached = true
                        addTableRow("End", "End", "End", "End", "End", "End", "End")
                    }

                    // all other lines
                    else if (line != null && line != "") {

                        val lineData = readLineData(line)
                        val type = lineData[1]

                        // damage dealt
                        val damageTypes = listOf("Damage", "CritDamage", "Backstab")
                        val typeSplit = type.split("_")
                        if (typeSplit.size == 2) {
                            if (damageTypes.contains(typeSplit[1])) {
                                val source = lineData[9]
                                val time = (lineData[8].toDouble() * 1000).toLong()
                                val damage = lineData[12].toInt()
                                val mitigated = lineData[13].toInt()
                                val reason = lineData[7]

                                // damage dealt by user
                                if (source.toLowerCase() == ign) {

                                    if (resetTimer) {
                                        // reset timer time in game time
                                        val resetTimerGT = resetTimerTime - (startTimeST - startTime)

                                        // delayed combat log hits that have to be go before the
                                        // "DPS Timer Reset" row
                                        if (time - resetTimerGT < 0) {
                                            if (dpsTableModel.rowCount != 0) {
                                                totalDamage += damage
                                                totalMitigated += mitigated
                                                removeTableRow(dpsTableModel.rowCount - 1)
                                                addDPSRow(startTime / 1000.0, time / 1000.0, damage, totalDamage, mitigated, totalMitigated, reason)
                                                addTableRow("Reset", "Reset", "Reset", "Reset", "Reset",
                                                    "Reset", "Reset")
                                            }
                                        }
                                        // Timer reset on this hit
                                        else {
                                            resetTimer = false
                                            startTime = time
                                            startTimeST = System.currentTimeMillis()
                                            totalDamage = damage
                                            totalMitigated = mitigated

                                            addTableRow(
                                                "0.00s",
                                                "0.00",
                                                damage.toString(),
                                                totalDamage.toString(),
                                                mitigated.toString(),
                                                totalMitigated.toString(),
                                                reason)
                                        }
                                    } else {
                                        totalDamage += damage
                                        addDPSRow(startTime / 1000.0, time / 1000.0, damage, totalDamage, mitigated, totalMitigated, reason)
                                    }
                                }
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
                if (combatLog != null) waiting = false
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
    fun clearTable() { clearDPSTable(false) }

    // this is used in a button event, so invoke later is not used
    /** Resets the DPS timer for the DPS calculation. */
    fun resetTimer() {
        if (!resetTimer && !waiting) {
            resetTimer = true
            resetTimerTime = System.currentTimeMillis()
            addTableRow("Reset", "Reset", "Reset", "Reset", "Reset", "Reset", "Reset", false)
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

    /**
     * Adds a row to the dps table.
     * @param safe whether or not to invoke and wait on the EDT
     */
    private fun addTableRow(time: String,
                            dps: String,
                            damage: String,
                            totalDamage: String,
                            mitigated: String,
                            totalMitigated: String,
                            reason: String,
                            safe: Boolean = true) {

        if (safe) {
            SwingUtilities.invokeAndWait {
                dpsTableModel.addRow(arrayOf(time, dps, damage, totalDamage, mitigated, totalMitigated, reason))
            }
        } else dpsTableModel.addRow(arrayOf(time, dps, damage, totalDamage, mitigated, totalMitigated, reason))

        dpsTableListeners.forEach { it() }
    }

    /**
     * Safely removes a row from the dps table.
     * @param safe whether or not to invoke and wait on the EDT
     */
    private fun removeTableRow(i: Int, safe: Boolean = true) {
        if (safe) SwingUtilities.invokeAndWait { dpsTableModel.removeRow(i) }
        else dpsTableModel.removeRow(i)
    }

    /**
     * Safely removes all rows from the dps table.
     * @param safe whether or not to invoke and wait on the EDT
     */
    private fun clearDPSTable(safe: Boolean = true) {
        if (safe) SwingUtilities.invokeAndWait { dpsTableModel.rowCount = 0 }
        else dpsTableModel.rowCount = 0
        dpsTableListeners.forEach { it() }
    }

    /** Calculates dps and creates a new row for the dps table. */
    private fun addDPSRow(startTime: Double,
                          endTime: Double,
                          damage: Int,
                          totalDamage: Int,
                          mitigated: Int,
                          totalMitigated: Int,
                          reason: String,
                          safe: Boolean = true) {

        val timeStr = "${"%.2f".format(endTime - startTime)}s"
        val dpsStr = "%.2f".format(totalDamage / (endTime - startTime))
            .replace("Infinity", "0.00")

        addTableRow(timeStr, dpsStr, damage.toString(), totalDamage.toString(),
            mitigated.toString(), totalMitigated.toString(), reason, safe)
    }

    /** Adds a dps table listener. */
    fun addDPSTableListener(listener: () -> Unit) { dpsTableListeners.add(listener) }

    /** Adds a combat log update listener. */
    fun addCombatLogListener(listener: (File?) -> Unit) { combatLogListeners.add(listener) }
}
