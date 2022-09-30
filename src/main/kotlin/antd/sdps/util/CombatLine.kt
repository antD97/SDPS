/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps.util

/** A single row of combat to be printed as [String]s. */
data class CombatLine(
    /** Time since combat was reset in seconds. */
    val time: Double,
    val damage: Int?,
    val totalDamage: Int,
    val mitigated: Int?,
    val totalMitigated: Int,
    val healReceived: Int?,
    val totalHealReceived: Int,
    val healApplied: Int?,
    val totalHealApplied: Int,
    val reason: String
) {

    private val dps = totalDamage / time

    fun toStrCombatLine() = StrCombatLine(
        "${"%.2f".format(time)}s",
        if ("$dps" == "Infinity") "∞" else "%.2f".format(dps),
        damage?.toString() ?: "",
        totalDamage.toString(),
        mitigated?.toString() ?: "",
        totalMitigated.toString(),
        healReceived?.toString() ?: "",
        totalHealReceived.toString(),
        healApplied?.toString() ?: "",
        totalHealApplied.toString(),
        reason
    )
}
