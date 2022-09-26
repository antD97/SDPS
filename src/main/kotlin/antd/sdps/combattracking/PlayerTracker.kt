/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps.combattracking

import antd.sdps.ConfigManager
import antd.sdps.util.edtInvokeAndWaitIfNeeded
import java.io.BufferedReader
import java.util.*
import javax.swing.JTextField
import javax.swing.table.DefaultTableModel
import kotlin.math.max

/** Used to continuously track a player's combat and update a [DefaultTableModel]. */
class PlayerTracker(configData: ConfigManager.ConfigData) {

    enum class StopReason { LOG_CLOSED, EXIT_REQUESTED }

    // --- constants --- //

    private val damageTypes = listOf("Damage", "CritDamage", "Backstab", "HolyCrit")

    private val nonGodNames = listOf(
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

    // --- GUI --- //

    /** The name field that the tracker updates with the user's in-game name. */
    var nameField = JTextField()

    /** The table model that the tracker updates. */
    var tableModel = DefaultTableModel()
    var tableListeners = mutableListOf<() -> Unit>()

    // --- tracking options --- //

    /** Player to track. */
    var ign: String = configData.ign ?: ""
        set(value) = synchronized(ignLock) { field = value }
        get() = synchronized(ignLock) { field }
    private val ignLock = Any()

    /** Toggles damage tracking. */
    var trackDamage: Boolean = configData.trackDamage
        set(value) = synchronized(trackDamageLock) { field = value }
        get() = synchronized(trackDamageLock) { field }
    private var trackDamageLock = Any()

    /** Toggles heal received tracking. */
    var trackHealReceived: Boolean = configData.trackHealReceived
        set(value) = synchronized(trackHealReceivedLock) { field = value }
        get() = synchronized(trackHealReceivedLock) { field }
    private val trackHealReceivedLock = Any()

    /** Toggles heal applied tracking. */
    var trackHealApplied: Boolean = configData.trackHealApplied
        set(value) = synchronized(trackHealAppliedLock) { field = value }
        get() = synchronized(trackHealAppliedLock) { field }
    private val trackHealAppliedLock = Any()

    /** Toggles only tracking combat involving gods. */
    var godsOnly: Boolean = configData.godsOnly
        set(value) = synchronized(godsOnlyLock) { field = value }
        get() = synchronized(godsOnlyLock) { field }
    private val godsOnlyLock = Any()

    // --- loop control --- //

    /** When true, starts DPS timer on next damage dealt and resets total damage/healing values. */
    private var resetTracking = true
        set(value) = synchronized(resetTrackingLock) { field = value }
        get() = synchronized(resetTrackingLock) { field }
    private val resetTrackingLock = Any()

    /** When true, tells the loop to exit safely. */
    var exitLoop = false
        set(value) = synchronized(exitLoopLock) { field = value }
        get() = synchronized(resetTrackingLock) { field }
    private val exitLoopLock = Any()

    // --- other --- //

    /** If the loop has caught up to the last line in the combat log. */
    private var caughtUp = false

    private var totalDamage = 0
    private var totalMitigated = 0
    private var totalHealReceived = 0
    private var totalHealApplied = 0

    /** The in-game time of the first combat line for tracking DPS and heal/damage totals. */
    private var combatStartTime = 0L

    /** The in-game time of the last tracked combat line. */
    private var prevCombatLineTime = 0L

/* ------------------------------------------ Main Loop ----------------------------------------- */

    /** Continuously tracks a player with [ign] combat and updates [tableModel]. */
    fun run(br: BufferedReader): StopReason {
        clearTable()

        caughtUp = false

        totalDamage = 0
        totalMitigated = 0
        totalHealReceived = 0
        totalHealApplied = 0

        resetTracking = true
        combatStartTime = 0
        prevCombatLineTime = 0

        exitLoop = false
        while (!exitLoop) {
            val line = br.readLine()

            // log closed
            if (line == "end" || line == ",{\"eventType\":\"end\"}") {
                addEndTableRow()
                return StopReason.LOG_CLOSED
            }
            // combat line
            else if (line != null && line != "") handleCombatLine(line, caughtUp)
            // caught up
            else {
                caughtUp = true
                Thread.sleep(100)
            }
        }

        return StopReason.EXIT_REQUESTED
    }

    /** Handles a single line of combat. */
    private fun handleCombatLine(line: String, caughtUp: Boolean) {

        clearPotentialHiddenCombat()

        val lineData = readLineData(line)
        val type = lineData[1]

        val typeSplit = type.split("_")
        // damage
        if (typeSplit.size == 2 && damageTypes.contains(typeSplit[1])) {
            handleDamageLine(lineData, caughtUp)
        }
        // heal
        else if (typeSplit.size == 2 && typeSplit[1] == "Healing") {
            val source = lineData[9]
            val target = lineData[11]

            // healing received
            if (ign != "" && target == ign) handleHealReceivedLine(lineData)
            // healing applied
            else if (ign != "" && source == ign && target != ign) handleHealAppliedLine(lineData)
        }
    }

    /** Handles a single line of damage. */
    private fun handleDamageLine(lineData: List<String>, caughtUp: Boolean) {
        val source = lineData[9]
        val target = lineData[11]
        val time = lineData[8].toDouble().secToMs()
        val damage = lineData[12].toInt()
        val mitigated = lineData[13].toInt()
        val reason = lineData[7]

        // update the gui name field
        if (ign == "" && caughtUp && !nonGodNames.contains(source)) {
            ign = source
            edtInvokeAndWaitIfNeeded { nameField.text = source }
        }

        // damage dealt by user
        if (ign != "" && source == ign) {
            addRowAndHandleResets(
                trackDamage && (!godsOnly || !nonGodNames.contains(target)),
                time,
                reason,
                damage = damage,
                mitigated = mitigated
            )
        }
    }

    /** Handles a single line of received healing. */
    private fun handleHealReceivedLine(lineData: List<String>) {
        val time = lineData[8].toDouble().secToMs()
        val healReceived = lineData[12].toInt()
        val reason = lineData[7]

        addRowAndHandleResets(
            trackHealReceived && (!godsOnly || reason != "Jungle Practice Fountain"),
            time,
            reason,
            healReceived = healReceived
        )
    }

    /** Handles a single line of applied healing. */
    private fun handleHealAppliedLine(lineData: List<String>) {
        val target = lineData[11]
        val time = lineData[8].toDouble().secToMs()
        val healApplied = lineData[12].toInt()
        val reason = lineData[7]

        addRowAndHandleResets(
            trackHealApplied && (!godsOnly || !nonGodNames.contains(target)),
            time,
            reason,
            healApplied = healApplied
        )
    }

    /** Adds a new row to the table if [track] is true and appropriately handles hidden damage. */
    private fun addRowAndHandleResets(
        track: Boolean,
        time: Long,
        reason: String,
        damage: Int? = null,
        mitigated: Int? = null,
        healReceived: Int? = null,
        healApplied: Int? = null
    ) {
        fun updateTotals() {
            if (damage != null) totalDamage += damage
            if (mitigated != null) totalMitigated += mitigated
            if (healReceived != null) totalHealReceived += healReceived
            if (healApplied != null) totalHealApplied += healApplied
        }

        // track damage & gods only check
        if (track) {

            if (resetTracking) {

                // delayed combat log lines that have to go before the reset row
                if (time == prevCombatLineTime) {

                    // don't add the row if there's no reset row to place it before
                    if (tableModel.rowCount != 0) {

                        updateTotals()

                        replaceRow(
                            tableModel.rowCount - 1,
                            time = (time - combatStartTime).msToSec().timeFormat(),
                            dps = calcDPS(
                                combatStartTime.msToSec(),
                                time.msToSec(),
                                totalDamage
                            ),
                            damage = damage?.toString() ?: "",
                            totalDamage = "$totalDamage",
                            mitigated = mitigated?.toString() ?: "",
                            totalMitigated = "$totalMitigated",
                            healReceived = healReceived?.toString() ?: "",
                            totalHealReceived = "$totalHealReceived",
                            healApplied = healApplied?.toString() ?: "",
                            totalHealApplied = "$totalHealApplied",
                            reason = reason
                        )

                        // re-add the last reset row
                        addResetTableRow()
                    }
                }
                // combat reset on this event
                else {
                    resetTracking = false
                    combatStartTime = time

                    totalDamage = 0
                    totalMitigated = 0
                    totalHealReceived = 0
                    totalHealApplied = 0

                    updateTotals()

                    addTableRow(
                        time = "0.00s",
                        dps = "0.00",
                        damage = damage?.toString() ?: "",
                        totalDamage = "$totalDamage",
                        mitigated = mitigated?.toString() ?: "",
                        totalMitigated = "$totalMitigated",
                        healReceived = healReceived?.toString() ?: "",
                        totalHealReceived = "$totalHealReceived",
                        healApplied = healApplied?.toString() ?: "",
                        totalHealApplied = "$totalHealApplied",
                        reason = reason
                    )

                    prevCombatLineTime = time
                }
            }
            // not reset tracking
            else {
                updateTotals()

                addTableRow(
                    time = (time - combatStartTime).msToSec().timeFormat(),
                    dps = calcDPS(combatStartTime.msToSec(), time.msToSec(), totalDamage),
                    damage = damage?.toString() ?: "",
                    totalDamage = "$totalDamage",
                    mitigated = mitigated?.toString() ?: "",
                    totalMitigated = "$totalMitigated",
                    healReceived = healReceived?.toString() ?: "",
                    totalHealReceived = "$totalHealReceived",
                    healApplied = healApplied?.toString() ?: "",
                    totalHealApplied = "$totalHealApplied",
                    reason = reason
                )

                prevCombatLineTime = time
            }
        }

        // anything caused by the player
        addPotentialHiddenCombat()
    }

/* ---------------------------------------- Loop Control ---------------------------------------- */

    /** Resets the DPS timer and total damage/heal values. */
    fun resetTracking() {
        if (!resetTracking) {
            resetTracking = true
            addResetTableRow()
        }
    }

/* ------------------------------------------- Helpers ------------------------------------------ */

    /** Get the row at index [row] from [tableModel] as a [String] array. */
    private fun getTableRow(row: Int): Array<String>? {
        if (row >= tableModel.rowCount) return null

        val result = Array(tableModel.columnCount) { "" }
        for (i in 0 until tableModel.columnCount) {
            result[i] = tableModel.getValueAt(row, i).toString()
        }
        return result
    }

    /** Adds a row to the table. */
    private fun addTableRow(
        time: String, dps: String, damage: String, totalDamage: String, mitigated: String,
        totalMitigated: String, healReceived: String, totalHealReceived: String,
        healApplied: String, totalHealApplied: String, reason: String
    ) {
        val rowArray = arrayOf(
            time, dps, damage, totalDamage, mitigated, totalMitigated,
            healReceived, totalHealReceived, healApplied, totalHealApplied, reason
        )

        edtInvokeAndWaitIfNeeded { tableModel.addRow(rowArray) }
        tableListeners.forEach { it.invoke() }
    }

    /** [addTableRow] with "End" in all columns. */
    private fun addEndTableRow() {
        addTableRow("End", "End", "End", "End", "End", "End", "End", "End", "End", "End", "End")
    }

    /** [addTableRow] with "Reset" in all columns. */
    private fun addResetTableRow() {
        addTableRow(
            "Reset", "Reset", "Reset", "Reset", "Reset", "Reset", "Reset", "Reset",
            "Reset", "Reset", "Reset"
        )
    }

    /** Removes all rows from the table. */
    fun clearTable() {
        edtInvokeAndWaitIfNeeded { tableModel.rowCount = 0 }
        tableListeners.forEach { it.invoke() }
    }

    /** Replaces the [i]th row in the table. */
    private fun replaceRow(
        i: Int,
        time: String, dps: String, damage: String, totalDamage: String, mitigated: String,
        totalMitigated: String, healReceived: String, totalHealReceived: String,
        healApplied: String, totalHealApplied: String, reason: String
    ) {
        val removedRows = LinkedList<Array<String>>()

        edtInvokeAndWaitIfNeeded {
            // pop all the rows leading up to the ith row
            for (j in (tableModel.rowCount - 1) downTo max(i, 0)) {
                removedRows.add(getTableRow(j)!!)
                tableModel.removeRow(j)
            }

            // add the replacement row
            addTableRow(
                time, dps, damage, totalDamage, mitigated, totalMitigated, healReceived,
                totalHealReceived, healApplied, totalHealApplied, reason
            )

            // discard the replaced row
            removedRows.removeLast()

            // push the remaining rows
            while (removedRows.isNotEmpty()) {
                val row = removedRows.removeLast()
                addTableRow(
                    row[0], row[1], row[2], row[3], row[4], row[5], row[6], row[7], row[8],
                    row[9], row[10]
                )
            }
        }
    }

    /** Remove the trailing "*" indicator from the last row for potential hidden combat. */
    private fun clearPotentialHiddenCombat() {
        // find the last non-reset row
        var row: Array<String>? = null
        var rowI = -1
        for (i in (tableModel.rowCount - 1) downTo 0) {
            if (getTableRow(i)?.get(0) != "Reset") {
                row = getTableRow(i)
                rowI = i
                break
            }
        }

        // remove "*" from that row
        if (row != null && row[0].endsWith("*")) {
            replaceRow(
                rowI,
                time = row[0].removeSuffix("*"),
                dps = row[1].removeSuffix("*"),
                damage = row[2].removeSuffix("*"),
                totalDamage = row[3].removeSuffix(("*")),
                mitigated = row[4].removeSuffix("*"),
                totalMitigated = row[5].removeSuffix("*"),
                healReceived = row[6].removeSuffix("*"),
                totalHealReceived = row[7].removeSuffix("*"),
                healApplied = row[8].removeSuffix("*"),
                totalHealApplied = row[9].removeSuffix("*"),
                reason = row[10].removeSuffix("*"),
            )
        }
    }

    /** Add a trailing "*" indicator to the last row for potential hidden combat. */
    private fun addPotentialHiddenCombat() {
        fun String.addAsterisksIfNotEmpty() = if (this.isNotEmpty()) "$this*" else ""

        // find the last non-reset row
        var row: Array<String>? = null
        var rowI = -1
        for (i in (tableModel.rowCount - 1) downTo 0) {
            if (getTableRow(i)?.get(0) != "Reset") {
                row = getTableRow(i)
                rowI = i
                break
            }
        }

        // add "*" to that row
        if (row != null && !row[3].endsWith("*")) {
            replaceRow(
                rowI,
                time = row[0].addAsterisksIfNotEmpty(),
                dps = row[1].addAsterisksIfNotEmpty(),
                damage = row[2].addAsterisksIfNotEmpty(),
                totalDamage = row[3].addAsterisksIfNotEmpty(),
                mitigated = row[4].addAsterisksIfNotEmpty(),
                totalMitigated = row[5].addAsterisksIfNotEmpty(),
                healReceived = row[6].addAsterisksIfNotEmpty(),
                totalHealReceived = row[7].addAsterisksIfNotEmpty(),
                healApplied = row[8].addAsterisksIfNotEmpty(),
                totalHealApplied = row[9].addAsterisksIfNotEmpty(),
                reason = row[10].addAsterisksIfNotEmpty(),
            )
        }
    }

    /** Reads the data from a single log file line and stores it in a String list. */
    private fun readLineData(line: String): List<String> {
        // log type that uses "," delimiters
        return if (line.contains("{")) {
            line.trim { it == '{' || it == '}' || it == ',' }.split(",").map {
                it.trim().split(":")[1].trim { c -> c == '"' }
            }
        }
        // log type that uses "|" delimiters
        else {
            line.split("|").map {
                if (it.contains("=")) it.split("=")[1]
                else it
            }
        }
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
}
