/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps.combat

import antd.sdps.ConfigManager
import java.io.File
import javax.swing.JTextField
import javax.swing.SwingUtilities
import javax.swing.table.DefaultTableModel

/** Monitors the newest combat log file and updates a table with damage information. */
class CombatTracker(configData: ConfigManager.ConfigData) {

    // gui name field
    var nameField = JTextField()

    /** Toggles damage tracking. */
    var trackDamage = configData.trackDamage
    /** Toggles heal received tracking. */
    var trackHealReceived = configData.trackHealReceived
    /** Toggles heal applied tracking. */
    var trackHealApplied = configData.trackHealApplied

    /** Toggles only tracking combat involving gods. */
    var godsOnly = configData.godsOnly

    /** The table model that the tracker updates. */
    var tableModel = DefaultTableModel()
    private var tableListeners = mutableListOf<() -> Unit>()

    private var ign = ""
    private var updatedIGN: String? = configData.ign

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
    /** When true, table is not updated. */
    private var waiting = true

    private var totalHealReceived = 0
    private var totalHealApplied = 0

    private var lastRow: Array<String>? = null

    /** Tells the damage tracker to update the in-game name. */
    fun updateIGN(ign: String) { updatedIGN = ign }

    /** Tracks damage using [ign] and [combatLog] to update [tableModel]. */
    fun run() {

        while (true) {

            // start tracking
            if (!waiting) {
                clearTable()
                val br = combatLog!!.bufferedReader()
                resetTimer = true
                var startTime = -1L
                var startTimeST = -1L   // start time in system time
                resetTimerTime = 0L
                var totalDamage = 0
                var totalMitigated = 0
                totalHealReceived = 0
                totalHealApplied = 0
                var eofReached = false

                // if ign hasn't been updated and didn't reach eof, keep tracking
                var caughtUp = false
                while (updatedIGN == null && !eofReached) {
                    val line = br.readLine()

                    // end of file
                    if (line == "end" || line == ",{\"eventType\":\"end\"}") {
                        waiting = true
                        eofReached = true
                        addTableRow("End", "End", "End", "End", "End", "End", "End", "End", "End",
                            "End", "End")
                    }

                    // all other lines
                    else if (line != null && line != "") {

                        // remove asterisks from rows that indicate potential hidden damage
                        if (lastRow?.get(3)?.endsWith("*") == true) {
                            val tempRow = lastRow!!
                            removeTableRow(tableModel.rowCount - 1)
                            addTableRow(
                                tempRow[0],
                                tempRow[1].removeSuffix("*"),
                                tempRow[2],
                                tempRow[3].removeSuffix("*"),
                                tempRow[4],
                                tempRow[5],
                                tempRow[6],
                                tempRow[7],
                                tempRow[8],
                                tempRow[9],
                                tempRow[10]
                            )
                        }

                        val lineData = readLineData(line)
                        val type = lineData[1]

                        val nonGodNames = listOf(
                            "Gold Fury",
                            "Fire Giant",
                            "Chaos Swordsman",
                            "Chaos Brute",
                            "Order Swordsman",
                            "Order Archer",
                            "Harpy",
                            "Elder Harpy",
                            "Manticore",
                            "Alpha Manticore",
                            "Spirit Satyr",
                            "Elder Satyr",
                            "Centaur",
                            "Chief Centaur",
                            "Chimera",
                            "Alpha Chimera",
                            "Chaos Tower",
                            "Chaos Titan",
                            "Chaos Phoenix"
                        )

                        val damageTypes = listOf("Damage", "CritDamage", "Backstab", "HolyCrit")

                        // damage type
                        val typeSplit = type.split("_")
                        if (trackDamage
                            && typeSplit.size == 2 && damageTypes.contains(typeSplit[1])) {
                            val source = lineData[9]
                            val target = lineData[11]
                            val time = lineData[8].toDouble().secToMs()
                            val damage = lineData[12].toInt()
                            val mitigated = lineData[13].toInt()
                            val reason = lineData[7]

                            // update the gui name field
                            if (ign == "" && caughtUp && !nonGodNames.contains(source)) {
                                ign = source
                                nameField.text = source
                            }

                            // damage dealt by user & gods only if enabled
                            if (ign != "" && source == ign
                                && (!godsOnly || !nonGodNames.contains(target))) {

                                if (resetTimer) {
                                    // reset timer time in game time
                                    val resetTimerGT = resetTimerTime - (startTimeST - startTime)

                                    // delayed combat log hits that have to be go before the
                                    // "Reset" row
                                    if (time - resetTimerGT < 0) {
                                        if (tableModel.rowCount != 0) {
                                            totalDamage += damage
                                            totalMitigated += mitigated

                                            removeTableRow(tableModel.rowCount - 1)

                                            addTableRow(
                                                (time.msToSec() - startTime.msToSec()).timeFormat(),
                                                "${calcDPS(startTime.msToSec(), time.msToSec(), totalDamage)}*",
                                                "$damage",
                                                "$totalDamage*",
                                                "$mitigated",
                                                "$totalMitigated",
                                                "",
                                                "$totalHealReceived",
                                                "",
                                                "$totalHealApplied",
                                                reason)

                                            addTableRow("Reset", "Reset", "Reset", "Reset",
                                                "Reset", "Reset", "Reset", "Reset", "Reset",
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
                                            "0.00*",
                                            "$damage",
                                            "$totalDamage*",
                                            "$mitigated",
                                            "$totalMitigated",
                                            "",
                                            "$totalHealReceived",
                                            "",
                                            "$totalHealApplied",
                                            reason)
                                    }
                                } else {
                                    totalDamage += damage
                                    totalMitigated += mitigated

                                    addTableRow(
                                        (time.msToSec() - startTime.msToSec()).timeFormat(),
                                        "${calcDPS(startTime.msToSec(), time.msToSec(), totalDamage)}*",
                                        "$damage",
                                        "$totalDamage*",
                                        "$mitigated",
                                        "$totalMitigated",
                                        "",
                                        "$totalHealReceived",
                                        "",
                                        "$totalHealApplied",
                                        reason)
                                }
                            }
                        }

                        // heal type
                        else if (typeSplit.size == 2 && typeSplit[1] == "Healing") {
                            val source = lineData[9]
                            val target = lineData[11]
                            val time = lineData[8].toDouble().secToMs()
                            val reason = lineData[7]
                            val healAmount = lineData[12].toInt()

                            // healing received & gods only if enabled
                            if (trackHealReceived
                                && ign != "" && target == ign
                                && (!godsOnly || reason != "Jungle Practice Fountain")) {
                                totalHealReceived += healAmount

                                addTableRow(
                                    (time.msToSec() - startTime.msToSec()).timeFormat(),
                                    calcDPS(startTime.msToSec(), time.msToSec(),
                                        totalDamage),
                                    "",
                                    totalDamage.toString(),
                                    "",
                                    totalMitigated.toString(),
                                    healAmount.toString(),
                                    totalHealReceived.toString(),
                                    "",
                                    totalHealApplied.toString(),
                                    reason)
                            }

                            // healing applied
                            else if (trackHealApplied
                                && ign != "" && source == ign && target != ign
                                && (!godsOnly || !nonGodNames.contains(target))) {

                                totalHealApplied = healAmount

                                addTableRow(
                                    (time.msToSec() - startTime.msToSec()).timeFormat(),
                                    calcDPS(startTime.msToSec(), time.msToSec(),
                                        totalDamage),
                                    "",
                                    totalDamage.toString(),
                                    "",
                                    totalMitigated.toString(),
                                    "",
                                    totalHealReceived.toString(),
                                    healAmount.toString(),
                                    totalHealApplied.toString(),
                                    reason)
                            }
                        }
                    } else {
                        caughtUp = true
                        Thread.sleep(100)
                    }
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
            if (waiting) Thread.sleep(2000)
        }
    }

    // this is used in a button event, so invoke later is not used
    /** Resets the DPS timer for the DPS calculation. */
    fun resetTracking() {
        if (!waiting
            && (!resetTimer || totalHealReceived != 0 || totalHealApplied != 0)) {

            resetTimer = true
            totalHealReceived = 0
            totalHealApplied = 0

            resetTimerTime = System.currentTimeMillis()
            addTableRow("Reset", "Reset", "Reset", "Reset", "Reset", "Reset", "Reset", "Reset",
                "Reset", "Reset", "Reset", false)
            tableListeners.forEach { it.invoke() }
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
     * Adds a row to the table.
     * @param invokeAndWait whether to invoke and wait on the EDT
     */
    private fun addTableRow(time: String,
                            dps: String,
                            damage: String,
                            totalDamage: String,
                            mitigated: String,
                            totalMitigated: String,
                            healReceived: String,
                            totalHealReceived: String,
                            healApplied: String,
                            totalHealApplied: String,
                            reason: String,
                            invokeAndWait: Boolean = true) {

        val rowArray = arrayOf(
            time,
            dps,
            damage,
            totalDamage,
            mitigated,
            totalMitigated,
            healReceived,
            totalHealReceived,
            healApplied,
            totalHealApplied,
            reason
        )

        if (invokeAndWait) SwingUtilities.invokeAndWait { tableModel.addRow(rowArray) }
        else tableModel.addRow(rowArray)

        lastRow = rowArray
        tableListeners.forEach { it.invoke() }
    }

    /** @param safe whether or not to invoke and wait on the EDT */
    private fun removeTableRow(i: Int, safe: Boolean = true) {
        if (safe) SwingUtilities.invokeAndWait { tableModel.removeRow(i) }
        else tableModel.removeRow(i)

        lastRow = null
    }

    /** @param safe whether or not to invoke and wait on the EDT */
    @Suppress("SameParameterValue")
    fun clearTable(safe: Boolean = true) {
        if (safe) SwingUtilities.invokeAndWait { tableModel.rowCount = 0 }
        else tableModel.rowCount = 0
        tableListeners.forEach { it.invoke() }
    }

    /** Formats the value to look good in the table. */
    private fun Double.timeFormat() = "${"%.2f".format(this)}s"

    /** Converts the value from milliseconds (Long) to seconds (Double). */
    private fun Long.msToSec() = this / 1000.0

    /** Converts the value from seconds (Double) to milliseconds (Long). */
    private fun Double.secToMs() = (this * 1000).toLong()

    /** Calculates DPS and formats it. */
    private fun calcDPS(startTime: Double, endTime: Double, totalDamage: Int): String {
        return "%.2f".format(totalDamage / (endTime - startTime))
            .replace("Infinity", "0.00")
    }

    /** Adds a table listener. */
    fun addTableListener(listener: () -> Unit) { tableListeners.add(listener) }

    /** Adds a combat log update listener. */
    fun addCombatLogListener(listener: (File?) -> Unit) { combatLogListeners.add(listener) }
}
