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

    var displayDPSCol = true
        set(value) = synchronized(displayDPSColLock) { field = value }
        get() = synchronized(displayDPSColLock) { field }
    private var displayDPSColLock = Any()

    var displayDamageCol = true
        set(value) = synchronized(displayDamageColLock) { field = value }
        get() = synchronized(displayDamageColLock) { field }
    private var displayDamageColLock = Any()

    var displayTotalDamageCol = true
        set(value) = synchronized(displayTotalDamageColLock) { field = value }
        get() = synchronized(displayTotalDamageColLock) { field }
    private var displayTotalDamageColLock = Any()

    var displayMitigatedCol = true
        set(value) = synchronized(displayMitigatedColLock) { field = value }
        get() = synchronized(displayMitigatedColLock) { field }
    private var displayMitigatedColLock = Any()

    var displayTotalMitigatedCol = true
        set(value) = synchronized(displayTotalMitigatedColLock) { field = value }
        get() = synchronized(displayTotalMitigatedColLock) { field }
    private var displayTotalMitigatedColLock = Any()

    var displayHealReceivedCol = true
        set(value) = synchronized(displayHealReceivedColLock) { field = value }
        get() = synchronized(displayHealReceivedColLock) { field }
    private var displayHealReceivedColLock = Any()

    var displayTotalHealReceivedCol = true
        set(value) = synchronized(displayTotalHealReceivedColLock) { field = value }
        get() = synchronized(displayTotalHealReceivedColLock) { field }
    private var displayTotalHealReceivedColLock = Any()

    var displayHealAppliedCol = true
        set(value) = synchronized(displayHealAppliedColLock) { field = value }
        get() = synchronized(displayHealAppliedColLock) { field }
    private var displayHealAppliedColLock = Any()

    var displayTotalHealAppliedCol = true
        set(value) = synchronized(displayTotalHealAppliedColLock) { field = value }
        get() = synchronized(displayTotalHealAppliedColLock) { field }
    private var displayTotalHealAppliedColLock = Any()

    var displayTotalsRow = true
        set(value) = synchronized(displayTotalsRowLock) { field = value }
        get() = synchronized(displayTotalsRowLock) { field }
    private var displayTotalsRowLock = Any()

    var displayRowHeaders = true
        set(value) = synchronized(displayRowHeadersLock) { field = value }
        get() = synchronized(displayRowHeadersLock) { field }
    private var displayRowHeadersLock = Any()

    init {
        if (!outputFile.isFile) outputFile.createNewFile()
    }

    /** todo Run on main thread. */
    fun print(str: String) {
        synchronized(outputFile) {
            outputFile.appendText("$str\n")
        }
    }

    /** todo */
    fun clear() {
        synchronized(outputFile) {
            outputFile.writeText("")
        }
    }
}
