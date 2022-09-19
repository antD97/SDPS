/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps.combattracking

import antd.sdps.ConfigManager
import antd.sdps.util.edtInvokeAndWaitIfNeeded
import java.io.BufferedReader
import javax.swing.JTextField
import javax.swing.table.DefaultTableModel

/** Used to continuously track a player's combat and update a [DefaultTableModel]. */
class PlayerTracker(
    configData: ConfigManager.ConfigData
) {

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

    /** In-game time of first tick of DPS calculation damage. */
    private var startGameTime = -1L
    /** System time of first tick of DPS calculation damage. */
    private var startSysTime = -1L
    /** System time of when reset was clicked. */
    private var resetSysTime = 0L

    /** The last row in the table. */
    private var lastRow: Array<String>? = null

/* ------------------------------------------ Main Loop ----------------------------------------- */

    /** Continuously tracks a player with [ign] combat and updates [tableModel]. */
    fun run(br: BufferedReader): StopReason {
        clearTable()

        caughtUp = false

        totalDamage = 0
        totalMitigated = 0
        totalHealReceived = 0
        totalHealApplied = 0

        startGameTime = -1L
        startSysTime = -1L
        resetSysTime = 0L

        resetTracking = true

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
            handleHealLine(lineData)
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

            // track damage & gods only check
            if (trackDamage && (!godsOnly || !nonGodNames.contains(target))) {

                if (resetTracking) {
                    // time of when reset was clicked in game time
                    val resetGameTime = resetSysTime - (startSysTime - startGameTime)

                    // delayed combat log lines that have to go before the reset row
                    if (time < resetGameTime) {
                        // don't add the row if there's no reset row to place it before
                        if (tableModel.rowCount != 0) {

                            totalDamage += damage
                            totalMitigated += mitigated

                            replaceLastRow(
                                time = (time.msToSec() - startGameTime.msToSec()).timeFormat(),
                                dps = calcDPS(startGameTime.msToSec(), time.msToSec(), totalDamage),
                                damage = "$damage",
                                totalDamage = "$totalDamage",
                                mitigated = "$mitigated",
                                totalMitigated = "$totalMitigated",
                                healReceived = "",
                                totalHealReceived = "$totalHealReceived",
                                healApplied = "",
                                totalHealApplied = "$totalHealApplied",
                                reason = reason
                            )

                            // re-add the last reset row
                            addResetTableRow()
                        }
                    }
                    // timer reset on this hit
                    else {
                        resetTracking = false
                        startGameTime = time
                        startSysTime = System.currentTimeMillis()

                        totalDamage = damage
                        totalMitigated = mitigated
                        totalHealReceived = 0
                        totalHealApplied = 0

                        addTableRow(
                            time = "0.00s",
                            dps = "0.00",
                            damage = "$damage",
                            totalDamage = "$totalDamage",
                            mitigated = "$mitigated",
                            totalMitigated = "$totalMitigated",
                            healReceived = "",
                            totalHealReceived = "0",
                            healApplied = "",
                            totalHealApplied = "0",
                            reason = reason
                        )
                    }
                }
                // not reset tracking
                else {
                    totalDamage += damage
                    totalMitigated += mitigated

                    addTableRow(
                        time = (time.msToSec() - startGameTime.msToSec()).timeFormat(),
                        dps = calcDPS(startGameTime.msToSec(), time.msToSec(), totalDamage),
                        damage = "$damage",
                        totalDamage = "$totalDamage",
                        mitigated = "$mitigated",
                        totalMitigated = "$totalMitigated",
                        healReceived = "",
                        totalHealReceived = "$totalHealReceived",
                        healApplied = "",
                        totalHealApplied = "$totalHealApplied",
                        reason = reason
                    )
                }
            }

            // any damage caused by the player
            addPotentialHiddenCombat()
        }
    }

    /** Handles a single line of healing. */
    private fun handleHealLine(lineData: List<String>) {
        val source = lineData[9]
        val target = lineData[11]

        // healing received
        if (ign != "" && target == ign) handleHealReceivedLine(lineData)
        // healing applied
        else if (ign != "" && source == ign && target != ign) handleHealAppliedLine(lineData)
    }

    /** Handles a single line of received healing. */
    private fun handleHealReceivedLine(lineData: List<String>) {
        val time = lineData[8].toDouble().secToMs()
        val healAmount = lineData[12].toInt()
        val reason = lineData[7]

        // track heal received & gods only check
        if (trackHealReceived && (!godsOnly || reason != "Jungle Practice Fountain")) {

            if (resetTracking) {
                // time of when reset was clicked in game time
                val resetGameTime = resetSysTime - (startSysTime - startGameTime)

                // delayed combat log lines that have to go before the reset row
                if (time < resetGameTime) {
                    // don't add the row if there's no reset row to place it before
                    if (tableModel.rowCount != 0) {

                        totalHealReceived += healAmount

                        replaceLastRow(
                            time = (time.msToSec() - startGameTime.msToSec()).timeFormat(),
                            dps = calcDPS(startGameTime.msToSec(), time.msToSec(), totalDamage),
                            damage = "",
                            totalDamage = "$totalDamage",
                            mitigated = "",
                            totalMitigated = "$totalMitigated",
                            healReceived = "$healAmount",
                            totalHealReceived = "$totalHealReceived",
                            healApplied = "",
                            totalHealApplied = "$totalHealApplied",
                            reason = reason
                        )

                        // re-add the last reset row
                        addResetTableRow()
                    }
                }
                // timer reset on this hit
                else {
                    resetTracking = false
                    startGameTime = time
                    startSysTime = System.currentTimeMillis()

                    totalDamage = 0
                    totalMitigated = 0
                    totalHealReceived = healAmount
                    totalHealApplied = 0

                    addTableRow(
                        time = "0.00s",
                        dps = "0.00",
                        damage = "",
                        totalDamage = "0",
                        mitigated = "",
                        totalMitigated = "0",
                        healReceived = "$healAmount",
                        totalHealReceived = "$totalHealReceived",
                        healApplied = "",
                        totalHealApplied = "0",
                        reason = reason
                    )
                }
            }
            // not reset tracking
            else {
                totalHealReceived += healAmount

                addTableRow(
                    time = (time.msToSec() - startGameTime.msToSec()).timeFormat(),
                    dps = calcDPS(startGameTime.msToSec(), time.msToSec(), totalDamage),
                    damage = "",
                    totalDamage = "$totalDamage",
                    mitigated = "",
                    totalMitigated = "$totalMitigated",
                    healReceived = "$healAmount",
                    totalHealReceived = "$totalHealReceived",
                    healApplied = "",
                    totalHealApplied = "$totalHealApplied",
                    reason = reason
                )
            }
        }

        // any healing received by the player (TODO probably?)
        addPotentialHiddenCombat()
    }

    /** Handles a single line of applied healing. */
    private fun handleHealAppliedLine(lineData: List<String>) {
        val target = lineData[11]
        val time = lineData[8].toDouble().secToMs()
        val healAmount = lineData[12].toInt()
        val reason = lineData[7]

        // track heal applied & gods only check
        if (trackHealApplied && (!godsOnly || !nonGodNames.contains(target))) {

            if (resetTracking) {
                // time of when reset was clicked in game time
                val resetGameTime = resetSysTime - (startSysTime - startGameTime)

                // delayed combat log lines that have to go before the reset row
                if (time < resetGameTime) {
                    // don't add the row if there's no reset row to place it before
                    if (tableModel.rowCount != 0) {

                        totalHealApplied += healAmount

                        replaceLastRow(
                            time = (time.msToSec() - startGameTime.msToSec()).timeFormat(),
                            dps = calcDPS(startGameTime.msToSec(), time.msToSec(), totalDamage),
                            damage = "",
                            totalDamage = "$totalDamage",
                            mitigated = "",
                            totalMitigated = "$totalMitigated",
                            healReceived = "",
                            totalHealReceived = "$totalHealReceived",
                            healApplied = "$healAmount",
                            totalHealApplied = "$totalHealApplied",
                            reason = reason
                        )

                        // re-add the last reset row
                        addResetTableRow()
                    }
                }
                // timer reset on this hit
                else {
                    resetTracking = false
                    startGameTime = time
                    startSysTime = System.currentTimeMillis()

                    totalDamage = 0
                    totalMitigated = 0
                    totalHealReceived = 0
                    totalHealApplied = healAmount

                    addTableRow(
                        time = "0.00s",
                        dps = "0.00",
                        damage = "",
                        totalDamage = "0",
                        mitigated = "",
                        totalMitigated = "0",
                        healReceived = "",
                        totalHealReceived = "0",
                        healApplied = "$healAmount",
                        totalHealApplied = "$totalHealApplied",
                        reason = reason
                    )
                }
            }
            // not reset tracking
            else {
                totalHealApplied += healAmount

                addTableRow(
                    time = (time.msToSec() - startGameTime.msToSec()).timeFormat(),
                    dps = calcDPS(startGameTime.msToSec(), time.msToSec(), totalDamage),
                    damage = "",
                    totalDamage = "$totalDamage",
                    mitigated = "",
                    totalMitigated = "$totalMitigated",
                    healReceived = "",
                    totalHealReceived = "$totalHealReceived",
                    healApplied = "$healAmount",
                    totalHealApplied = "$totalHealApplied",
                    reason = reason
                )
            }
        }

        // any healing applied by the player (TODO probably?)
        addPotentialHiddenCombat()
    }

/* ---------------------------------------- Loop Control ---------------------------------------- */

    /** Resets the DPS timer and total damage/heal values. */
    fun resetTracking() {
        if (!resetTracking) {
            resetTracking = true
            resetSysTime = System.currentTimeMillis()
            addResetTableRow()
        }
    }

/* ------------------------------------------- Helpers ------------------------------------------ */


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

        lastRow = rowArray
        tableListeners.forEach { it.invoke() }
    }

    /** [addTableRow] with "End" in all columns. */
    private fun addEndTableRow() {
        addTableRow("End", "End", "End", "End", "End", "End", "End", "End", "End", "End", "End")
    }

    /** [addTableRow] with "Reset" in all columns. */
    private fun addResetTableRow() {

        if (lastRow?.get(0)?.endsWith("*") == true) {
            clearPotentialHiddenCombat()
            addTableRow(
                "Reset*", "Reset*", "Reset*", "Reset*", "Reset*", "Reset*", "Reset*", "Reset*",
                "Reset*", "Reset*", "Reset*"
            )
        } else {
            addTableRow(
                "Reset", "Reset", "Reset", "Reset", "Reset", "Reset", "Reset", "Reset", "Reset",
                "Reset", "Reset"
            )
        }
    }

    /** Removes all rows from the table. */
    fun clearTable() {
        edtInvokeAndWaitIfNeeded { tableModel.rowCount = 0 }
        lastRow = null
        tableListeners.forEach { it.invoke() }
    }

    /** Replaces the last row in the table. */
    private fun replaceLastRow(
        time: String, dps: String, damage: String, totalDamage: String, mitigated: String,
        totalMitigated: String, healReceived: String, totalHealReceived: String,
        healApplied: String, totalHealApplied: String, reason: String
    ) {
        if (tableModel.rowCount > 0) {
            edtInvokeAndWaitIfNeeded { tableModel.removeRow(tableModel.rowCount - 1) }
        }
        addTableRow(time, dps, damage, totalDamage, mitigated, totalMitigated, healReceived,
            totalHealReceived, healApplied, totalHealApplied, reason)
    }

    /** Remove the trailing "*" indicator from the last row for potential hidden combat. */
    private fun clearPotentialHiddenCombat() {
        if (lastRow?.get(3)?.endsWith("*") == true) {
            replaceLastRow(
                time = lastRow!![0].removeSuffix("*"),
                dps = lastRow!![1].removeSuffix("*"),
                damage = lastRow!![2].removeSuffix("*"),
                totalDamage = lastRow!![3].removeSuffix(("*")),
                mitigated = lastRow!![4].removeSuffix("*"),
                totalMitigated = lastRow!![5].removeSuffix("*"),
                healReceived = lastRow!![6].removeSuffix("*"),
                totalHealReceived = lastRow!![7].removeSuffix("*"),
                healApplied = lastRow!![8].removeSuffix("*"),
                totalHealApplied = lastRow!![9].removeSuffix("*"),
                reason = lastRow!![10].removeSuffix("*"),
            )
        }
    }

    /** Add a trailing "*" indicator to the last row for potential hidden combat. */
    private fun addPotentialHiddenCombat() {
        fun String.addAsterisksIfNotEmpty() = if (this.isNotEmpty()) "$this*" else ""

        if (lastRow?.get(3)?.endsWith("*") == false) {
            replaceLastRow(
                time = lastRow!![0].addAsterisksIfNotEmpty(),
                dps = lastRow!![1].addAsterisksIfNotEmpty(),
                damage = lastRow!![2].addAsterisksIfNotEmpty(),
                totalDamage = lastRow!![3].addAsterisksIfNotEmpty(),
                mitigated = lastRow!![4].addAsterisksIfNotEmpty(),
                totalMitigated = lastRow!![5].addAsterisksIfNotEmpty(),
                healReceived = lastRow!![6].addAsterisksIfNotEmpty(),
                totalHealReceived = lastRow!![7].addAsterisksIfNotEmpty(),
                healApplied = lastRow!![8].addAsterisksIfNotEmpty(),
                totalHealApplied = lastRow!![9].addAsterisksIfNotEmpty(),
                reason = lastRow!![10].addAsterisksIfNotEmpty(),
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
