/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps.combattracking

import antd.sdps.ConfigManager
import java.io.File

/** Used to write formatted combat text to an output file that can be used as an OBS text source. */
class ObsWriter(configData: ConfigManager.ConfigData) {

    private val outputFile = File("obs-source.txt")

    /** Tracked combat lines. */
    private var combatLines = mutableListOf<CombatLine>()

    // --- print settings --- //

    private val controlLock = Any()

    /** Whether this [ObsWriter] writes to the [outputFile]. */
    private var _enabled = configData.obsEnabled
    /**
     * **Synchronized with [addLine], [replaceLastLine], [clear], and all other settings.** See
     * [_enabled].
     */
    var enabled: Boolean
        set(value) = synchronized(controlLock) {
            _enabled = value
            if (!_enabled && outputFile.isFile) outputFile.delete()
        }
        get() = synchronized(controlLock) { _enabled }

    /** Whether the first printed row contains headers for each printed column. */
    private var _printHeadersRow = configData.obsPrintHeadersRow
    /**
     * **Synchronized with [addLine], [replaceLastLine], [clear], and all other settings.** See
     * [_printHeadersRow].
     */
    var printHeadersRow: Boolean
        set(value) = synchronized(controlLock) {
            _printHeadersRow = value
            writeToFile()
        }
        get() = synchronized(controlLock) { _printHeadersRow }

    /** Whether the "time" column is printed. */
    private var _printTime = true
    /**
     * **Synchronized with [addLine], [replaceLastLine], [clear], and all other settings.** See
     * [_printTime].
     */
    var printTime: Boolean
        set(value) = synchronized(controlLock) {
            _printTime = value
            writeToFile()
        }
        get() = synchronized(controlLock) { _printTime }

    /** Whether the "DPS" column is printed. */
    private var _printDPS = true
    /**
     * **Synchronized with [addLine], [replaceLastLine], [clear], and all other settings.** See
     * [_printDPS].
     */
    var printDPS: Boolean
        set(value) = synchronized(controlLock) {
            _printDPS = value
            writeToFile()
        }
        get() = synchronized(controlLock) { _printDPS }

    /** Whether the "damage" column is printed. */
    private var _printDamage = true
    /**
     * **Synchronized with [addLine], [replaceLastLine], [clear], and all other settings.** See
     * [_printDamage].
     */
    var printDamage: Boolean
        set(value) = synchronized(controlLock) {
            _printDamage = value
            writeToFile()
        }
        get() = synchronized(controlLock) { _printDamage }

    /** Whether the "total damage" column is printed. */
    private var _printTotalDamage = true
    /**
     * **Synchronized with [addLine], [replaceLastLine], [clear], and all other settings.** See
     * [_printTotalDamage].
     */
    var printTotalDamage: Boolean
        set(value) = synchronized(controlLock) {
            _printTotalDamage = value
            writeToFile()
        }
        get() = synchronized(controlLock) { _printTotalDamage }

    /** Whether the "mitigated" column is printed. */
    private var _printMitigated = true
    /**
     * **Synchronized with [addLine], [replaceLastLine], [clear], and all other settings.** See
     * [_printMitigated].
     */
    var printMitigated: Boolean
        set(value) = synchronized(controlLock) {
            _printMitigated = value
            writeToFile()
        }
        get() = synchronized(controlLock) { _printMitigated }

    /** Whether the "total mitigated" column is printed. */
    private var _printTotalMitigated = true
    /**
     * **Synchronized with [addLine], [replaceLastLine], [clear], and all other settings.** See
     * [_printTotalMitigated].
     */
    var printTotalMitigated: Boolean
        set(value) = synchronized(controlLock) {
            _printTotalMitigated = value
            writeToFile()
        }
        get() = synchronized(controlLock) { _printTotalMitigated }

    /** Whether the "heal received" column is printed. */
    private var _printHealReceived = true
    /**
     * **Synchronized with [addLine], [replaceLastLine], [clear], and all other settings.** See
     * [_printHealReceived].
     */
    var printHealReceived: Boolean
        set(value) = synchronized(controlLock) {
            _printHealReceived = value
            writeToFile()
        }
        get() = synchronized(controlLock) { _printHealReceived }

    /** Whether the "total heal received" column is printed. */
    private var _printTotalHealReceived = true
    /**
     * **Synchronized with [addLine], [replaceLastLine], [clear], and all other settings.** See
     * [_printTotalHealReceived].
     */
    var printTotalHealReceived: Boolean
        set(value) = synchronized(controlLock) {
            _printTotalHealReceived = value
            writeToFile()
        }
        get() = synchronized(controlLock) { _printTotalHealReceived }

    /** Whether the "heal applied" column is printed. */
    private var _printHealApplied = true
    /**
     * **Synchronized with [addLine], [replaceLastLine], [clear], and all other settings.** See
     * [_printHealApplied].
     */
    var printHealApplied: Boolean
        set(value) = synchronized(controlLock) {
            _printHealApplied = value
            writeToFile()
        }
        get() = synchronized(controlLock) { _printHealApplied }

    /** Whether the "total heal applied" column is printed. */
    private var _printTotalHealApplied = true
    /**
     * **Synchronized with [addLine], [replaceLastLine], [clear], and all other settings.** See
     * [_printTotalHealApplied].
     */
    var printTotalHealApplied: Boolean
        set(value) = synchronized(controlLock) {
            _printTotalHealApplied = value
            writeToFile()
        }
        get() = synchronized(controlLock) { _printTotalHealApplied }

    /** Whether the "reason" column is printed. */
    private var _printReason = true
    /**
     * **Synchronized with [addLine], [replaceLastLine], [clear], and all other settings.** See
     * [_printReason].
     */
    var printReason: Boolean
        set(value) = synchronized(controlLock) {
            _printReason = value
            writeToFile()
        }
        get() = synchronized(controlLock) { _printReason }

    /** Whether the last printed row contains the totals for each printed column. */
    private var _printTotalsRow = configData.obsPrintTotalsRow
    /**
     * **Synchronized with [addLine], [replaceLastLine], [clear], and all other settings.** See
     * [_printTotalsRow].
     */
    var printTotalsRow: Boolean
        set(value) = synchronized(controlLock) {
            _printTotalsRow = value
            writeToFile()
        }
        get() = synchronized(controlLock) { _printTotalsRow }

    /** The width of each printed column in number of characters (excludes reason column). */
    private var _columnWidth = configData.obsColumnWidth
    /**
     * **Synchronized with [addLine], [replaceLastLine], [clear], and all other settings.** See
     * [_columnWidth].
     */
    var columnWidth: Int
        set(value) = synchronized(controlLock) {
            _columnWidth = value
            writeToFile()
        }
        get() = synchronized(controlLock) { _columnWidth }

    /** The width of the reason column in number of characters. */
    private var _reasonColumnWidth = configData.obsReasonColumnWidth
    /**
     * **Synchronized with [addLine], [replaceLastLine], [clear], and all other settings.** See
     * [_reasonColumnWidth].
     */
    var reasonColumnWidth: Int
        set(value) = synchronized(controlLock) {
            _reasonColumnWidth = value
            writeToFile()
        }
        get() = synchronized(controlLock) { _reasonColumnWidth }

    /** The number of line to write to the [outputFile]. */
    private var _maxLines = configData.obsMaxLines
    /**
     * **Synchronized with [addLine], [replaceLastLine], [clear], and all other settings.** See
     * [_maxLines].
     */
    var maxLines: Int
        set(value) = synchronized(controlLock) {
            _maxLines = value
            writeToFile()
        }
        get() = synchronized(controlLock) { _maxLines }

/* -------------------------------------- Public Functions -------------------------------------- */

    /** Adds a line of combat to be written to the OBS output file. */
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

    /** Replaces the last line of combat written to the OBS output file. */
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

    /** Clears all combat. */
    fun clear() {
        synchronized(controlLock) {
            combatLines.clear()
            writeToFile()
        }
    }

    /** Deletes the [outputFile]. */
    fun deleteFile() {
        synchronized(controlLock) {
            if (outputFile.isFile) outputFile.delete()
        }
    }

/* ------------------------------------------- Helpers ------------------------------------------ */

    /**
     * Writes combat to [outputFile] using the set settings. **Must be called [synchronized] with
     * [controlLock].**
     */
    private fun writeToFile() {
        if (!_enabled) return

        val sb = StringBuilder()

        if (combatLines.size != 0) {

            // row headers
            if (_printHeadersRow) {
                if (_printTime) sb.append("Time".setLength(_columnWidth))
                if (_printDPS) sb.append("DPS".setLength(_columnWidth))
                if (_printDamage) sb.append("Damage".setLength(_columnWidth))
                if (_printTotalDamage) sb.append("Σ Damage".setLength(_columnWidth))
                if (_printMitigated) sb.append("Mitigated".setLength(_columnWidth))
                if (_printTotalMitigated) sb.append("Σ Mitigated".setLength(_columnWidth))
                if (_printHealReceived) sb.append("Heal Received".setLength(_columnWidth))
                if (_printTotalHealReceived) sb.append("Σ Heal Received".setLength(_columnWidth))
                if (_printHealApplied) sb.append("Heal Applied".setLength(_columnWidth))
                if (_printTotalHealApplied) sb.append("Σ Heal Applied".setLength(_columnWidth))
                if (_printReason) sb.append("Reason".setLength(_reasonColumnWidth, false))
                sb.append("\n")
            }

            val numCombatLines = _maxLines -
                    (if (_printHeadersRow) 1 else 0) -
                    (if (_printTotalsRow) 1 else 0)

            // combat lines
            for ((i, combatLine) in combatLines.withIndex().toList().takeLast(numCombatLines)) {

                if (_printTime) sb.append(combatLine.time.setLength(_columnWidth))
                if (_printDPS) sb.append(combatLine.dps.setLength(_columnWidth))
                if (_printDamage) sb.append(combatLine.damage.setLength(_columnWidth))
                if (_printTotalDamage) sb.append(combatLine.totalDamage.setLength(_columnWidth))
                if (_printMitigated) sb.append(combatLine.mitigated.setLength(_columnWidth))
                if (_printTotalMitigated) sb.append(combatLine.totalMitigated.setLength(_columnWidth))
                if (_printHealReceived) sb.append(combatLine.healReceived.setLength(_columnWidth))
                if (_printTotalHealReceived)
                    sb.append(combatLine.totalHealReceived.setLength(_columnWidth))
                if (_printHealApplied) sb.append(combatLine.healApplied.setLength(_columnWidth))
                if (_printTotalHealApplied)
                    sb.append(combatLine.totalHealApplied.setLength(_columnWidth))
                if (_printReason) sb.append(combatLine.reason.setLength(_reasonColumnWidth, false))

                sb.append("\n")
            }

            // blank lines
            for (i in 0 until (numCombatLines - combatLines.size)) {
                if (_printTime) sb.append("".setLength(_columnWidth))
                if (_printDPS) sb.append("".setLength(_columnWidth))
                if (_printDamage) sb.append("".setLength(_columnWidth))
                if (_printTotalDamage) sb.append("".setLength(_columnWidth))
                if (_printMitigated) sb.append("".setLength(_columnWidth))
                if (_printTotalMitigated) sb.append("".setLength(_columnWidth))
                if (_printHealReceived) sb.append("".setLength(_columnWidth))
                if (_printTotalHealReceived) sb.append("".setLength(_columnWidth))
                if (_printHealApplied) sb.append("".setLength(_columnWidth))
                if (_printTotalHealApplied) sb.append("".setLength(_columnWidth))
                if (_printReason) sb.append("".setLength(_reasonColumnWidth, false))

                if (i != numCombatLines - combatLines.size - 1 || _printTotalsRow) sb.append("\n")
            }

            val lastCombatLine = combatLines.last()

            // totals row
            if (_printTotalsRow) {
                if (_printTime) sb.append("".setLength(_columnWidth))
                if (_printDPS) sb.append("".setLength(_columnWidth))
                if (_printDamage) sb.append(lastCombatLine.totalDamage.setLength(_columnWidth))
                if (_printTotalDamage) sb.append("".setLength(_columnWidth))
                if (_printMitigated) sb.append(lastCombatLine.totalMitigated.setLength(_columnWidth))
                if (_printTotalMitigated) sb.append("".setLength(_columnWidth))
                if (_printHealReceived)
                    sb.append(lastCombatLine.totalHealReceived.setLength(_columnWidth))
                if (_printTotalHealReceived) sb.append("".setLength(_columnWidth))
                if (_printHealApplied)
                    sb.append(lastCombatLine.totalHealApplied.setLength(_columnWidth))
                if (_printTotalHealApplied) sb.append("".setLength(_columnWidth))
                if (_printReason) sb.append("".setLength(_reasonColumnWidth, false))
            }
        }

        outputFile.writeText(sb.toString())
    }

    /**
     * Pads the end of [this] with spaces to reach [targetLength]. [String]s longer than
     * [targetLength] are shortened with a trailing "…".
     * @param trailingGap whether there should be a two space gap at the end
     */
    private fun String.setLength(targetLength: Int, trailingGap: Boolean = true): String {
        val trailingGapSize = if (trailingGap) 2 else 0
        return if (length > targetLength - trailingGapSize)
            "${substring(0, targetLength - trailingGapSize - 1).trimEnd()}…".padEnd(targetLength)
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
