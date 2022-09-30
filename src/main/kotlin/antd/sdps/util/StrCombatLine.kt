/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps.util

/** A single row of combat to be printed as [String]s. */
data class StrCombatLine(
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
) {

    /** Creates a [StrCombatLine] with [s] for every value. */
    constructor(s: String) : this(s, s, s, s, s, s, s, s, s, s, s)

    constructor(arr: Array<String>) : this(
        arr[0], arr[1], arr[2], arr[3], arr[4], arr[5], arr[6], arr[7], arr[8], arr[9], arr[10]
    )

    fun toArray() = arrayOf(
        time, dps, damage, totalDamage, mitigated, totalMitigated, healReceived,
        totalHealReceived, healApplied, totalHealApplied, reason
    )
}
