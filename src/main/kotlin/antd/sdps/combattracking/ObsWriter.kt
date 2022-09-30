/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps.combattracking

import antd.sdps.PopupUncaughtExceptionHandler
import antd.sdps.SharedInstances.initConfig
import antd.sdps.gui.OutputTable
import antd.sdps.util.StrCombatLine
import java.io.File
import java.util.concurrent.BlockingQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.LinkedBlockingQueue
import javax.swing.SwingWorker

/** Used to write formatted combat text to an output file that can be used as an OBS text source. */
class ObsWriter : SwingWorker<Unit, Unit>() {

    /** The OBS source file this [ObsWriter] writes to. */
    private val outputFile = File("obs-source.txt")

    /** Tracked combat lines. **Only modify on [ObsWriter] worker thread**. */
    private val combatLines = mutableListOf<StrCombatLine>()

    /** Used to indicate when this task has been successfully cancelled. */
    private val cancelledLatch = CountDownLatch(1)

    // --- write settings --- //

    /** Whether this [ObsWriter] writes to the [outputFile]. Modify with [enable] and [disable]. */
    @Volatile
    private var enabled = initConfig.obsEnabled

    /** Whether the first printed row contains headers for each printed column. */
    @Volatile
    var printHeadersRow = initConfig.obsPrintHeadersRow

    /** Whether the "time" column is printed. */
    @Volatile
    var printTime =
        if (initConfig.colToWidthMap == null) !OutputTable.initHiddenColumns.contains("Time")
        else initConfig.colToWidthMap!!["Time"] != 0

    /** Whether the "DPS" column is printed. */
    @Volatile
    var printDPS =
        if (initConfig.colToWidthMap == null) !OutputTable.initHiddenColumns.contains("DPS")
        else initConfig.colToWidthMap!!["DPS"] != 0

    /** Whether the "damage" column is printed. */
    @Volatile
    var printDamage =
        if (initConfig.colToWidthMap == null) !OutputTable.initHiddenColumns.contains("Damage")
        else initConfig.colToWidthMap!!["Damage"] != 0

    /** Whether the "total damage" column is printed. */
    @Volatile
    var printTotalDamage =
        if (initConfig.colToWidthMap == null)
            !OutputTable.initHiddenColumns.contains("Total Damage")
        else initConfig.colToWidthMap!!["Σ Damage"] != 0

    /** Whether the "mitigated" column is printed. */
    @Volatile
    var printMitigated =
        if (initConfig.colToWidthMap == null) !OutputTable.initHiddenColumns.contains("Mitigated")
        else initConfig.colToWidthMap!!["Mitigated"] != 0

    /** Whether the "total mitigated" column is printed. */
    @Volatile
    var printTotalMitigated =
        if (initConfig.colToWidthMap == null)
            !OutputTable.initHiddenColumns.contains("Total Mitigated")
        else initConfig.colToWidthMap!!["Σ Mitigated"] != 0

    /** Whether the "heal received" column is printed. */
    @Volatile
    var printHealReceived =
        if (initConfig.colToWidthMap == null)
            !OutputTable.initHiddenColumns.contains("Heal Received")
        else initConfig.colToWidthMap!!["Heal Received"] != 0

    /** Whether the "total heal received" column is printed. */
    @Volatile
    var printTotalHealReceived =
        if (initConfig.colToWidthMap == null)
            !OutputTable.initHiddenColumns.contains("Total Heal Received")
        else initConfig.colToWidthMap!!["Σ Heal Received"] != 0

    /** Whether the "heal applied" column is printed. */
    @Volatile
    var printHealApplied =
        if (initConfig.colToWidthMap == null)
            !OutputTable.initHiddenColumns.contains("Heal Applied")
        else initConfig.colToWidthMap!!["Heal Applied"] != 0

    /** Whether the "total heal applied" column is printed. */
    @Volatile
    var printTotalHealApplied =
        if (initConfig.colToWidthMap == null)
            !OutputTable.initHiddenColumns.contains("Total Heal Applied")
        else initConfig.colToWidthMap!!["Σ Heal Applied"] != 0

    /** Whether the "reason" column is printed. */
    @Volatile
    var printReason =
        if (initConfig.colToWidthMap == null) !OutputTable.initHiddenColumns.contains("Reason")
        else initConfig.colToWidthMap!!["Reason"] != 0

    /** Whether the last printed row contains the totals for each printed column. */
    @Volatile
    var printTotalsRow = initConfig.obsPrintTotalsRow

    /** The width of each printed column in number of characters (excludes reason column). */
    @Volatile
    var columnWidth = initConfig.obsColumnWidth

    /** The width of the reason column in number of characters. */
    @Volatile
    var reasonColumnWidth = initConfig.obsReasonColumnWidth

    /** The number of line to write to the [outputFile]. */
    @Volatile
    var maxLines = initConfig.obsMaxLines

    // --- writer task --- //

    /** Queue of [WriterTask]s for this [ObsWriter] to process. */
    private val writeTaskQueue: BlockingQueue<WriterTask> = LinkedBlockingQueue()

    /** A task for this [ObsWriter] to process. */
    sealed class WriterTask {
        /** [WriterTask] to add a line to [combatLines] and write it to [outputFile]. */
        data class AddCombat(val combatLine: StrCombatLine) : WriterTask()

        /** [WriterTask] to replace the last line in [combatLines] and write it to [outputFile]. */
        data class ReplaceLastCombat(val combatLine: StrCombatLine) : WriterTask()

        /** [WriterTask] to clear [combatLines] and write "" to [outputFile]. */
        object Clear : WriterTask()

        /** [WriterTask] to rewrite the [outputFile]. */
        object Rewrite : WriterTask()
    }

/* -------------------------------------- Public Functions -------------------------------------- */

    /** Set this [ObsWriter] to accept [WriterTask]s. */
    fun enable() { enabled = true }

    /** Set this [ObsWriter] to not accept [WriterTask]s and queue a [ClearTask]. */
    fun disable() {
        enabled = false
        writeTaskQueue.add(WriterTask.Clear)
    }

    /** Adds [task] to the [writeTaskQueue]. */
    fun queueTask(task: WriterTask) {
        if (enabled) writeTaskQueue.add(task)
    }

/* ------------------------------------------ Main Loop ----------------------------------------- */

    /** Continuously processes [writeTaskQueue]. */
    override fun doInBackground() {
        // uncaught exception handlers
        Thread.currentThread().uncaughtExceptionHandler = PopupUncaughtExceptionHandler

        while (!isCancelled) {

            // wait for new tasks
            val tasks = mutableListOf<WriterTask>()
            try { tasks.add(writeTaskQueue.take()) }
            catch (_: InterruptedException) { break }

            // collect all tasks to process
            while (writeTaskQueue.isNotEmpty()) tasks.add(writeTaskQueue.remove())

            // process tasks
            for (task in tasks) {
                when (task) {
                    is WriterTask.AddCombat -> combatLines.add(task.combatLine)
                    is WriterTask.ReplaceLastCombat ->
                        combatLines[combatLines.lastIndex] = task.combatLine
                    is WriterTask.Clear -> combatLines.clear()
                    is WriterTask.Rewrite -> { /* do nothing. rewrite is always done at the end */ }
                }
            }

            // rewrite output file
            writeOutputFile()
        }

        // clear the output file
        outputFile.writeText("")

        // cancellation completed
        cancelledLatch.countDown()
    }

    /** Write formatted [combatLines] to [outputFile] with the set settings. */
    private fun writeOutputFile() {
        val sb = StringBuilder()

        if (combatLines.size != 0) {

            // copy volatile settings
            val printHeadersRowCopy = printHeadersRow
            val printTimeCopy = printTime
            val printDPSCopy = printDPS
            val printDamageCopy = printDamage
            val printTotalDamageCopy = printTotalDamage
            val printMitigatedCopy = printMitigated
            val printTotalMitigatedCopy = printTotalMitigated
            val printHealReceivedCopy = printHealReceived
            val printTotalHealReceivedCopy = printTotalHealReceived
            val printHealAppliedCopy = printHealApplied
            val printTotalHealAppliedCopy = printTotalHealApplied
            val printReasonCopy = printReason
            val printTotalsRowCopy = printTotalsRow
            val columnWidthCopy = columnWidth
            val reasonColumnWidthCopy = reasonColumnWidth
            val maxLinesCopy = maxLines

            // row headers
            if (printHeadersRowCopy) {
                if (printTimeCopy) sb.append("Time".setLength(columnWidthCopy))
                if (printDPSCopy) sb.append("DPS".setLength(columnWidthCopy))
                if (printDamageCopy) sb.append("Damage".setLength(columnWidthCopy))
                if (printTotalDamageCopy) sb.append("Σ Damage".setLength(columnWidthCopy))
                if (printMitigatedCopy) sb.append("Mitigated".setLength(columnWidthCopy))
                if (printTotalMitigatedCopy) sb.append("Σ Mitigated".setLength(columnWidthCopy))
                if (printHealReceivedCopy) sb.append("Heal Received".setLength(columnWidthCopy))
                if (printTotalHealReceivedCopy)
                    sb.append("Σ Heal Received".setLength(columnWidthCopy))
                if (printHealAppliedCopy) sb.append("Heal Applied".setLength(columnWidthCopy))
                if (printTotalHealAppliedCopy)
                    sb.append("Σ Heal Applied".setLength(columnWidthCopy))
                if (printReasonCopy) sb.append("Reason".setLength(reasonColumnWidthCopy, false))
                sb.append("\n")
            }

            val numCombatLines = maxLinesCopy -
                    (if (printHeadersRowCopy) 1 else 0) -
                    (if (printTotalsRowCopy) 1 else 0)

            // combat lines
            for (combatLine in combatLines.takeLast(numCombatLines)) {

                if (printTimeCopy) sb.append(combatLine.time.setLength(columnWidthCopy))
                if (printDPSCopy) sb.append(combatLine.dps.setLength(columnWidthCopy))
                if (printDamageCopy) sb.append(combatLine.damage.setLength(columnWidthCopy))
                if (printTotalDamageCopy)
                    sb.append(combatLine.totalDamage.setLength(columnWidthCopy))
                if (printMitigatedCopy) sb.append(combatLine.mitigated.setLength(columnWidthCopy))
                if (printTotalMitigatedCopy)
                    sb.append(combatLine.totalMitigated.setLength(columnWidthCopy))
                if (printHealReceivedCopy)
                    sb.append(combatLine.healReceived.setLength(columnWidthCopy))
                if (printTotalHealReceivedCopy)
                    sb.append(combatLine.totalHealReceived.setLength(columnWidthCopy))
                if (printHealAppliedCopy)
                    sb.append(combatLine.healApplied.setLength(columnWidthCopy))
                if (printTotalHealAppliedCopy)
                    sb.append(combatLine.totalHealApplied.setLength(columnWidthCopy))
                if (printReasonCopy)
                    sb.append(combatLine.reason.setLength(reasonColumnWidthCopy, false))

                sb.append("\n")
            }

            // blank lines
            for (i in 0 until (numCombatLines - combatLines.size)) {
                if (printTimeCopy) sb.append("".setLength(columnWidthCopy))
                if (printDPSCopy) sb.append("".setLength(columnWidthCopy))
                if (printDamageCopy) sb.append("".setLength(columnWidthCopy))
                if (printTotalDamageCopy) sb.append("".setLength(columnWidthCopy))
                if (printMitigatedCopy) sb.append("".setLength(columnWidthCopy))
                if (printTotalMitigatedCopy) sb.append("".setLength(columnWidthCopy))
                if (printHealReceivedCopy) sb.append("".setLength(columnWidthCopy))
                if (printTotalHealReceivedCopy) sb.append("".setLength(columnWidthCopy))
                if (printHealAppliedCopy) sb.append("".setLength(columnWidthCopy))
                if (printTotalHealAppliedCopy) sb.append("".setLength(columnWidthCopy))
                if (printReasonCopy) sb.append("".setLength(reasonColumnWidthCopy, false))

                if (i != numCombatLines - combatLines.size - 1 || printTotalsRowCopy)
                    sb.append("\n")
            }

            val lastCombatLine = combatLines.last()

            // totals row
            if (printTotalsRowCopy) {
                if (printTimeCopy) sb.append("".setLength(columnWidthCopy))
                if (printDPSCopy) sb.append("".setLength(columnWidthCopy))
                if (printDamageCopy)
                    sb.append(lastCombatLine.totalDamage.setLength(columnWidthCopy))
                if (printTotalDamageCopy) sb.append("".setLength(columnWidthCopy))
                if (printMitigatedCopy)
                    sb.append(lastCombatLine.totalMitigated.setLength(columnWidthCopy))
                if (printTotalMitigatedCopy) sb.append("".setLength(columnWidthCopy))
                if (printHealReceivedCopy)
                    sb.append(lastCombatLine.totalHealReceived.setLength(columnWidthCopy))
                if (printTotalHealReceivedCopy) sb.append("".setLength(columnWidthCopy))
                if (printHealAppliedCopy)
                    sb.append(lastCombatLine.totalHealApplied.setLength(columnWidthCopy))
                if (printTotalHealAppliedCopy) sb.append("".setLength(columnWidthCopy))
                if (printReasonCopy) sb.append("".setLength(reasonColumnWidthCopy, false))
            }
        }

        outputFile.writeText(sb.toString())
    }

    fun awaitCancellation() = cancelledLatch.await()

/* ------------------------------------------- Helpers ------------------------------------------ */

    /** Pads the end of [this] with spaces to reach [targetLength]. [String]s longer than
     * [targetLength] are shortened with a trailing "…".
     * @param trailingGap whether there should be a two space gap at the end. */
    private fun String.setLength(targetLength: Int, trailingGap: Boolean = true): String {
        val trailingGapSize = if (trailingGap) 2 else 0
        return if (length > targetLength - trailingGapSize)
            "${substring(0, targetLength - trailingGapSize - 1).trimEnd()}…".padEnd(targetLength)
        else padEnd(targetLength)
    }
}
