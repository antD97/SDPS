/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps.combattracking

import java.io.File

/** todo */
object ObsPrinter {

    /** todo */
    private val outputFile = File("obs-source.txt")

    private var combatLines = mutableListOf<CombatLine>()

    // --- printer settings --- //

    private val controlLock = Any()

    private var printRowHeaders = true
    fun setPrintRowHeaders(value: Boolean) {
        synchronized(controlLock) {
            printRowHeaders = value
            writeToFile()
        }
    }

    private var printTime = false
    fun setPrintTime(value: Boolean) {
        synchronized(controlLock) {
            printTime = value
            writeToFile()
        }
    }

    private var printDPS = false
    fun setPrintDPS(value: Boolean) {
        synchronized(controlLock) {
            printDPS = value
            writeToFile()
        }
    }

    private var printDamage = true
    fun setPrintDamage(value: Boolean) {
        synchronized(controlLock) {
            printDamage = value
            writeToFile()
        }
    }

    private var printTotalDamage = false
    fun setPrintTotalDamage(value: Boolean) {
        synchronized(controlLock) {
            printTotalDamage = value
            writeToFile()
        }
    }

    private var printMitigated = true
    fun setPrintMitigated(value: Boolean) {
        synchronized(controlLock) {
            printMitigated = value
            writeToFile()
        }
    }

    private var printTotalMitigated = false
    fun setPrintTotalMitigated(value: Boolean) {
        synchronized(controlLock) {
            printTotalMitigated = value
            writeToFile()
        }
    }

    private var printHealReceived = false
    fun setPrintHealReceived(value: Boolean) {
        synchronized(controlLock) {
            printHealReceived = value
            writeToFile()
        }
    }

    private var printTotalHealReceived = false
    fun setPrintTotalHealReceived(value: Boolean) {
        synchronized(controlLock) {
            printTotalHealReceived = value
            writeToFile()
        }
    }

    private var printHealApplied = false
    fun setPrintHealApplied(value: Boolean) {
        synchronized(controlLock) {
            printHealApplied = value
            writeToFile()
        }
    }

    private var printTotalHealApplied = false
    fun setPrintTotalHealApplied(value: Boolean) {
        synchronized(controlLock) {
            printTotalHealApplied = value
            writeToFile()
        }
    }

    private var printReason = true
    fun setPrintReason(value: Boolean) {
        synchronized(controlLock) {
            printReason = value
            writeToFile()
        }
    }

    private var printTotalsRow = true
    fun setPrintTotalsRow(value: Boolean) {
        synchronized(controlLock) {
            printTotalsRow = value
            writeToFile()
        }
    }

    private var columnWidth = 10
    fun setColumnWidth(value: Int) {
        synchronized(controlLock) {
            columnWidth = value
            writeToFile()
        }
    }

    private var reasonColumnWidth = 20
    fun setReasonColumnWidth(value: Int) {
        synchronized(controlLock) {
            reasonColumnWidth = value
            writeToFile()
        }
    }

    private var maxLines = 10
    fun setMaxLines(value: Int) {
        synchronized(controlLock) {
            maxLines = value
            writeToFile()
        }
    }

/* -------------------------------------- Public Functions -------------------------------------- */

    /** todo */
    fun addLine(
        time: String, dps: String, damage: String, totalDamage: String, mitigated: String,
        totalMitigated: String, healReceived: String, totalHealReceived: String,
        healApplied: String, totalHealApplied: String, reason: String
    ) {
        synchronized(controlLock) {

            combatLines.add(CombatLine(time, dps, damage, totalDamage, mitigated, totalMitigated,
                healReceived, totalHealReceived, healApplied, totalHealApplied, reason))

            writeToFile()
        }
    }

    /** todo */
    fun replaceLastLine(
        time: String, dps: String, damage: String, totalDamage: String, mitigated: String,
        totalMitigated: String, healReceived: String, totalHealReceived: String,
        healApplied: String, totalHealApplied: String, reason: String
    ) {
        synchronized(controlLock) {

            if (combatLines.isNotEmpty()) {
                combatLines[combatLines.lastIndex] = CombatLine(
                    time, dps, damage, totalDamage, mitigated, totalMitigated, healReceived,
                    totalHealReceived, healApplied, totalHealApplied, reason
                )
            }

            writeToFile()
        }
    }

    /** todo */
    fun clear() {
        synchronized(controlLock) {
            combatLines.clear()
            writeToFile()
        }
    }

/* ------------------------------------------- Helpers ------------------------------------------ */

    /** **Must be called [synchronized] with [controlLock].** */
    private fun writeToFile() {
        val sb = StringBuilder()

        if (combatLines.size != 0) {

            // row headers
            if (printRowHeaders) {
                if (printTime) sb.append("Time".setLength(columnWidth))
                if (printDPS) sb.append("DPS".setLength(columnWidth))
                if (printDamage) sb.append("Damage".setLength(columnWidth))
                if (printTotalDamage) sb.append("Σ Damage".setLength(columnWidth))
                if (printMitigated) sb.append("Mitigated".setLength(columnWidth))
                if (printTotalMitigated) sb.append("Σ Mitigated".setLength(columnWidth))
                if (printHealReceived) sb.append("Heal Received".setLength(columnWidth))
                if (printTotalHealReceived) sb.append("Σ Heal Received".setLength(columnWidth))
                if (printHealApplied) sb.append("Heal Applied".setLength(columnWidth))
                if (printTotalHealApplied) sb.append("Σ Heal Applied".setLength(columnWidth))
                if (printReason) sb.append("Reason".setLength(reasonColumnWidth, true))
                sb.append("\n")
            }

            val numCombatLines = maxLines -
                    (if (printRowHeaders) 1 else 0) -
                    (if (printTotalsRow) 1 else 0)

            // combat lines
            for ((i, combatLine) in combatLines.withIndex().toList().takeLast(numCombatLines)) {

                if (printTime) sb.append(combatLine.time.setLength(columnWidth))
                if (printDPS) sb.append(combatLine.dps.setLength(columnWidth))
                if (printDamage) sb.append(combatLine.damage.setLength(columnWidth))
                if (printTotalDamage) sb.append(combatLine.totalDamage.setLength(columnWidth))
                if (printMitigated) sb.append(combatLine.mitigated.setLength(columnWidth))
                if (printTotalMitigated) sb.append(combatLine.totalMitigated.setLength(columnWidth))
                if (printHealReceived) sb.append(combatLine.healReceived.setLength(columnWidth))
                if (printTotalHealReceived)
                    sb.append(combatLine.totalHealReceived.setLength(columnWidth))
                if (printHealApplied) sb.append(combatLine.healApplied.setLength(columnWidth))
                if (printTotalHealApplied)
                    sb.append(combatLine.totalHealApplied.setLength(columnWidth))
                if (printReason) sb.append(combatLine.reason.setLength(reasonColumnWidth, true))

                if (i != combatLines.lastIndex || printTotalsRow) sb.append("\n")
            }

            // blank lines
            for (i in 0 until (numCombatLines - combatLines.size)) {
                if (printTime) sb.append("".setLength(columnWidth))
                if (printDPS) sb.append("".setLength(columnWidth))
                if (printDamage) sb.append("".setLength(columnWidth))
                if (printTotalDamage) sb.append("".setLength(columnWidth))
                if (printMitigated) sb.append("".setLength(columnWidth))
                if (printTotalMitigated) sb.append("".setLength(columnWidth))
                if (printHealReceived) sb.append("".setLength(columnWidth))
                if (printTotalHealReceived) sb.append("".setLength(columnWidth))
                if (printHealApplied) sb.append("".setLength(columnWidth))
                if (printTotalHealApplied) sb.append("".setLength(columnWidth))
                if (printReason) sb.append("".setLength(reasonColumnWidth, true))
                sb.append("\n")
            }

            val lastCombatLine = combatLines.last()

            // totals row
            if (printTotalsRow) {
                if (printTime) sb.append("".setLength(columnWidth))
                if (printDPS) sb.append("".setLength(columnWidth))
                if (printDamage) sb.append(lastCombatLine.totalDamage.setLength(columnWidth))
                if (printTotalDamage) sb.append("".setLength(columnWidth))
                if (printMitigated) sb.append(lastCombatLine.totalMitigated.setLength(columnWidth))
                if (printTotalMitigated) sb.append("".setLength(columnWidth))
                if (printHealReceived)
                    sb.append(lastCombatLine.totalHealReceived.setLength(columnWidth))
                if (printTotalHealReceived) sb.append("".setLength(columnWidth))
                if (printHealApplied)
                    sb.append(lastCombatLine.totalHealApplied.setLength(columnWidth))
                if (printTotalHealApplied) sb.append("".setLength(columnWidth))
                if (printReason) sb.append("".setLength(reasonColumnWidth, true))
            }
        }

        outputFile.writeText(sb.toString())
    }

    private fun String.setLength(targetLength: Int, excludeTrailingSpace: Boolean = false): String {
        val substringTrimSize = if (excludeTrailingSpace) 1 else 2
        return if (length >= targetLength)
            "${substring(0, targetLength - substringTrimSize).trimEnd()}…".padEnd(targetLength)
        else padEnd(targetLength)
    }

    private data class CombatLine(
        val time: String,
        val dps: String,
        val damage: String,
        val totalDamage: String,
        val mitigated: String,
        val totalMitigated: String,
        val healReceived: String,
        val totalHealReceived: String,
        val healApplied: String,
        val totalHealApplied: String,
        val reason: String
    )
}
