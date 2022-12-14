/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps.workers

import antd.sdps.PopupUncaughtExceptionHandler
import antd.sdps.SharedInstances.autoCombatResetWorker
import antd.sdps.SharedInstances.initConfig
import antd.sdps.SharedInstances.mainPanel
import antd.sdps.SharedInstances.obsWriter
import antd.sdps.SharedInstances.outputTable
import antd.sdps.SharedInstances.sidebarPanel
import antd.sdps.SharedInstances.statusLabel
import antd.sdps.util.CombatLine
import antd.sdps.util.StrCombatLine
import java.io.BufferedReader
import java.io.File
import javax.swing.JFileChooser
import javax.swing.SwingWorker
import javax.swing.table.DefaultTableModel
import kotlin.properties.Delegates

/** Monitors the newest combat log file and updates a [DefaultTableModel] with damage
 *  information. */
class CombatTracker :
    ExceptionHandlingSwingWorker<Unit, CombatTracker.ProcessTask>(PopupUncaughtExceptionHandler) {

    // --- gui --- //

    /** The [DefaultTableModel] to update. */
    private val tableModel = outputTable.model as DefaultTableModel

    // --- constants --- //

    /** Combat log line names for damage. */
    private val damageTypes = listOf("Damage", "CritDamage", "Backstab", "HolyCrit")

    /** Combat log line names for crowd control. */
    private val crowdControlTypes = listOf("Status", "CrowdControl")

    /** Source/targets to ignore while [godsOnly] is true. */
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

    enum class FileTrackStopReason { LOG_CLOSED, EXIT_REQUESTED }

    // --- tracking options --- //

    /** Player to track. Modify with [updateIGN]. */
    @Volatile
    private var ign = initConfig.ign ?: ""

    /** Toggles damage tracking. */
    @Volatile
    var trackDamage = initConfig.trackDamage

    /** Toggles heal received tracking. */
    @Volatile
    var trackHealReceived = initConfig.trackHealReceived

    /** Toggles heal applied tracking. */
    @Volatile
    var trackHealApplied = initConfig.trackHealApplied

    /** Toggles only tracking combat involving gods. */
    @Volatile
    var godsOnly = initConfig.godsOnly

    // --- loop control --- //

    /** When true, starts DPS timer on next damage dealt and resets total damage/healing values.
     * Modify with the [resetTracking] function. */
    @Volatile
    private var resetTracking = true

    /** When true, tells the loop to exit safely. Modify with [resetTracking] or
     * [reloadCombatLog]. */
    @Volatile
    private var exitLoop = false

    // --- process task --- //

    /** A task [publish]ed by this [SwingWorker] to be [process]ed on the EDT. */
    sealed class ProcessTask {
        /** [ProcessTask] to add a table row to [tableModel]. */
        data class AddCombat(val strCombatLine: StrCombatLine) : ProcessTask()

        /** [ProcessTask] to mark the last combat with the hidden combat "*". */
        object AddPotentialHiddenCombat : ProcessTask()

        /** [ProcessTask] to clear the hidden combat "*" from the last combat. */
        object ClearPotentialHiddenCombat : ProcessTask()

        /** [ProcessTask] to update the [sidebarPanel] combat log field text. */
        data class UpdateCombatLogField(val fileName: String) : ProcessTask()

        /** [ProcessTask] to update the [sidebarPanel] name field text. */
        data class UpdateNameField(val ign: String) : ProcessTask()

        /** [ProcessTask] to clear [outputTable]. */
        object ClearTable : ProcessTask()
    }

    // --- combat log file --- //

    private val documentsDir = JFileChooser().fileSystemView.defaultDirectory
    private val defaultSmiteLogsDir =
        File("${documentsDir.absolutePath}\\My Games\\Smite\\BattleGame\\Logs")

    /** The tracked combat log. */
    private var combatLog: File? by Delegates.observable(null) { _, _, new ->
        publish(ProcessTask.UpdateCombatLogField(if (new != null) new.name else "No file"))
    }

    /** Whether the end of the current combat log has been reached. */
    private var combatLogClosed = false

    // --- tracking --- //

    /** If the loop has caught up to the last line in the combat log. */
    private var caughtUp = false

    private var totalDamage = 0
    private var totalMitigated = 0
    private var totalHealReceived = 0
    private var totalHealApplied = 0

    /** The in-game time of the first combat line for tracking DPS and heal/damage totals. */
    private var combatStartTime = 0.0

    /** Whether there could be hidden combat following the last tracked combat. */
    private var potentialHiddenCombat = true

/* ------------------------------------------ Main Loop ----------------------------------------- */

    /** Continuously tracks combat using [combatLog] to generate [ProcessTask]s for [process]. */
    override fun doInBackgroundCatchExceptions() {
        while (!isCancelled) {

            // track player
            if (combatLog != null && !combatLogClosed) {

                combatLog!!.bufferedReader().use { br ->
                    when (monitorCombatFile(br)) {
                        FileTrackStopReason.LOG_CLOSED -> combatLogClosed = true
                        else -> {}
                    }
                }
            }

            // use newest combat log
            val foundCombatLog = if (defaultSmiteLogsDir.isDirectory) {
                defaultSmiteLogsDir.listFiles()!!
                    .filter { it.name.contains("CombatLog_") }
                    .filterNot { it.name.contains("backup") }
                    .maxByOrNull { it.lastModified() }
            } else null

            // combat log found
            // and (no current log or found log is newer than current)
            if (foundCombatLog != null
                && (combatLog == null || foundCombatLog.lastModified() > combatLog!!.lastModified())
            ) {
                // update combat log
                combatLog = foundCombatLog
                combatLogClosed = false
            }

            // if there is no combat log, refresh slowly
            if (combatLog == null || combatLogClosed) Thread.sleep(2000)
        }
    }

    /** Continuously tracks a player with [ign] and updates [tableModel]. */
    private fun monitorCombatFile(br: BufferedReader): FileTrackStopReason {
        publish(ProcessTask.ClearTable)

        caughtUp = false

        totalDamage = 0
        totalMitigated = 0
        totalHealReceived = 0
        totalHealApplied = 0

        resetTracking = true
        combatStartTime = 0.0

        potentialHiddenCombat = false

        exitLoop = false
        while (!exitLoop && !isCancelled) {
            val line = br.readLine()

            // log closed
            if (line == "end" || line == ",{\"eventType\":\"end\"}") {
                publish(ProcessTask.AddCombat(StrCombatLine("End")))
                obsWriter.queueTask(ObsWriter.WriterTask.Clear)
                return FileTrackStopReason.LOG_CLOSED
            }
            // combat line
            else if (line != null && line != "") {

                // copy volatile settings
                val ignCopy = ign
                val trackDamageCopy = trackDamage
                val trackHealReceivedCopy = trackHealReceived
                val trackHealAppliedCopy = trackHealApplied
                val godsOnlyCopy = godsOnly
                val resetTrackingCopy = resetTracking

                handleCombatLine(
                    line, ignCopy, trackDamageCopy, trackHealReceivedCopy, trackHealAppliedCopy,
                    godsOnlyCopy, resetTrackingCopy
                )
            }
            // caught up
            else {
                caughtUp = true
                Thread.sleep(100)
            }
        }

        return FileTrackStopReason.EXIT_REQUESTED
    }

    /** Handles a single line of combat. */
    private fun handleCombatLine(
        lineCopy: String,
        ignCopy: String,
        trackDamageCopy: Boolean,
        trackHealReceivedCopy: Boolean,
        trackHealAppliedCopy: Boolean,
        godsOnlyCopy: Boolean,
        resetTrackingCopy: Boolean
    ) {
        var ignCopy = ignCopy

        potentialHiddenCombat = false
        publish(ProcessTask.ClearPotentialHiddenCombat)

        val lineData = readLineData(lineCopy)
        val type = lineData[1]

        val typeSplit = type.split("_")
        // damage
        if (typeSplit.size == 2 && damageTypes.contains(typeSplit[1])) {
            val source = lineData[9]
            val target = lineData[11]

            // update the gui name field
            if (ign == "" && caughtUp && !nonGodNames.contains(source)) {
                ign = source
                ignCopy = source
                publish(ProcessTask.UpdateNameField(source))
            }

            // damage dealt
            if (ignCopy != "" && source == ignCopy) {
                val time = lineData[8].toDouble()
                val damage = lineData[12].toInt()
                val mitigated = lineData[13].toInt()
                val reason = lineData[7]

                addRowAndHandleResets(
                    trackDamageCopy && (!godsOnlyCopy || !nonGodNames.contains(target)),
                    resetTrackingCopy,
                    time,
                    reason,
                    damage = damage,
                    mitigated = mitigated
                )
            }

            // todo damage received?
//            else if (ign != "" && target == ign) {
//
//            }
        }
        // heal
        else if (typeSplit.size == 2 && typeSplit[1] == "Healing") {
            val source = lineData[9]
            val target = lineData[11]

            // healing received
            if (ignCopy != "" && target == ignCopy) {
                val time = lineData[8].toDouble()
                val healReceived = lineData[12].toInt()
                // todo confirm empty reason always means lifesteal
                val reason = if (lineData[7] != "") lineData[7] else "Lifesteal"

                addRowAndHandleResets(
                    trackHealReceivedCopy && (!godsOnlyCopy || reason != "Jungle Practice Fountain"),
                    resetTrackingCopy,
                    time,
                    reason,
                    healReceived = healReceived
                )
            }
            // healing applied
            else if (ignCopy != "" && source == ignCopy) {
                val time = lineData[8].toDouble()
                val healApplied = lineData[12].toInt()
                val reason = lineData[7]

                addRowAndHandleResets(
                    trackHealAppliedCopy && (!godsOnlyCopy || !nonGodNames.contains(target)),
                    resetTrackingCopy,
                    time,
                    reason,
                    healApplied = healApplied
                )
            }
        }
        // cc
        else if (typeSplit.size == 2 && crowdControlTypes.contains(typeSplit[1])) {
            val source = lineData[9]

            if (source == ignCopy) {
                potentialHiddenCombat = true
                publish(ProcessTask.AddPotentialHiddenCombat)
            }
        }
    }

    /** Adds a new row to the table if [track] is true and appropriately handles hidden damage. */
    private fun addRowAndHandleResets(
        track: Boolean,
        resetTrackingCopy: Boolean,
        time: Double,
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

        fun makeCombatLine() = CombatLine(
            time - combatStartTime,
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

        // collect tasks created in this function
        val tasksToPublish = mutableListOf<ProcessTask>()

        // track damage & gods only check
        if (track) {
            if (resetTrackingCopy) {
                this.resetTracking = false
                combatStartTime = time

                totalDamage = 0
                totalMitigated = 0
                totalHealReceived = 0
                totalHealApplied = 0

                updateTotals()

                tasksToPublish.add(ProcessTask.AddCombat(makeCombatLine().toStrCombatLine()))
            }
            // not reset tracking
            else {
                updateTotals()
                tasksToPublish.add(ProcessTask.AddCombat(makeCombatLine().toStrCombatLine()))
            }
        }

        // anything caused by the player
        potentialHiddenCombat = true
        tasksToPublish.add(ProcessTask.AddPotentialHiddenCombat)

        // publish all created tasks
        publish(*tasksToPublish.toTypedArray())
    }

/* ----------------------------------------- Processing ----------------------------------------- */

    /** Handles [ProcessTask]s generated by [doInBackground] to update GUI and [obsWriter]. */
    override fun process(tasks: MutableList<ProcessTask>?) {
        val tasks = tasks ?: return

        for (task in tasks) {
            when (task) {

                is ProcessTask.AddCombat -> {
                    tableModel.addRow(task.strCombatLine.toArray())
                    mainPanel.scrollTableToBottom()

                    if (task.strCombatLine.time.contains("Reset"))
                        obsWriter.queueTask(ObsWriter.WriterTask.Clear)
                    else obsWriter.queueTask(ObsWriter.WriterTask.AddCombat(task.strCombatLine))
                }

                is ProcessTask.AddPotentialHiddenCombat -> {
                    val lastRow = tableModel.getRow(tableModel.rowCount - 1)

                    if (lastRow != null
                        && !lastRow[0].contains("Reset")
                        && !lastRow[0].endsWith("*")
                    ) {

                        tableModel.removeRow(tableModel.rowCount - 1)

                        val newRow = lastRow.toList()
                            .map { if (it.isNotEmpty()) "$it*" else "" }
                            .toTypedArray()
                        tableModel.addRow(newRow)
                        mainPanel.scrollTableToBottom()
                        obsWriter.queueTask(
                            ObsWriter.WriterTask.ReplaceLastCombat(StrCombatLine(newRow))
                        )
                    }

                    statusLabel.text = "Potentially hidden combat (hover for details)"
                    statusLabel.toolTipText = "Resetting is disabled when there is potentially " +
                            "hidden combat. Starting and then cancelling a recall will reveal " +
                            "any hidden combat. See the readme for additional details."

                    autoCombatResetWorker.hiddenCombatState()
                }

                is ProcessTask.ClearPotentialHiddenCombat -> {
                    val lastRow = tableModel.getRow(tableModel.rowCount - 1)

                    if (lastRow == null) autoCombatResetWorker.reset()
                    else if (!lastRow[0].contains("Reset") && lastRow[0].endsWith("*")) {

                        tableModel.removeRow(tableModel.rowCount - 1)

                        val newRow = lastRow.toList().map { it.removeSuffix("*") }.toTypedArray()
                        tableModel.addRow(newRow)
                        mainPanel.scrollTableToBottom()
                        obsWriter.queueTask(
                            ObsWriter.WriterTask.ReplaceLastCombat(StrCombatLine(newRow))
                        )

                        statusLabel.reset()

                        autoCombatResetWorker.reset()
                    }
                }

                is ProcessTask.UpdateCombatLogField -> {
                    sidebarPanel.combatLogField.text = task.fileName
                }

                is ProcessTask.UpdateNameField -> sidebarPanel.nameField.text = task.ign

                is ProcessTask.ClearTable -> {
                    outputTable.clearTable()
                    obsWriter.queueTask(ObsWriter.WriterTask.Clear)
                }
            }
        }
    }

/* -------------------------------------- Public Functions -------------------------------------- */

    /** Update the in-game name to the source of the next god damage. */
    fun updateIGN() {
        ign = ""
        exitLoop = true
    }

    /** Unload the current combat log and load the newest one. */
    fun reloadCombatLog() {
        exitLoop = true
    }

    /** Resets the DPS timer and total damage/heal values. */
    fun resetTracking() {
        if (!resetTracking && !potentialHiddenCombat) {
            resetTracking = true
            publish(ProcessTask.AddCombat(StrCombatLine("Reset")))
        }
    }

/* ------------------------------------------- Helpers ------------------------------------------ */

    private fun DefaultTableModel.getRow(row: Int): Array<String>? {
        if (row < 0 || row >= this.rowCount) return null
        val result = Array(this.columnCount) { "" }
        for (i in 0 until this.columnCount) {
            result[i] = this.getValueAt(row, i).toString()
        }
        return result
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
}
