/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps.combattracking

import antd.sdps.ConfigManager
import java.io.File
import javax.swing.JTextField
import javax.swing.table.DefaultTableModel

/**
 * Monitors the newest combat log file and updates a [DefaultTableModel] with damage information.
 */
class CombatTracker(configData: ConfigManager.ConfigData, obsWriter: ObsWriter) {

    // --- helpers --- //

    private val playerTracker = PlayerTracker(configData, obsWriter)

    // --- combat log --- //

    /** The tracked combat log. */
    private var combatLog: File? = null
        set(value) {
            field = value
            combatLogListeners.forEach { it(value) }
        }

    private var combatLogListeners = mutableListOf<(File?) -> Unit>()

    /** Whether EOF of the current combat log has been reached. */
    private var combatLogClosed = false

/* ------------------------------------------ Main Loop ----------------------------------------- */

    /** Continuously tracks combat using [combatLog] to update [tableModel]. */
    fun run() {
        while (true) {

            // track player
            if (combatLog != null && !combatLogClosed) {
                combatLog!!.bufferedReader().use { br ->
                    when (playerTracker.run(br)) {
                        PlayerTracker.StopReason.LOG_CLOSED -> combatLogClosed = true
                        else -> {}
                    }
                }
            }

            // use newest combat log
            val foundCombatLog = CombatLogFinder.search()

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

/* ---------------------------------------- Loop Control ---------------------------------------- */

    /** Make the [playerTracker] update the in-game name to the source of the next god damage. */
    fun updateIGN() {
        synchronized(playerTracker.loopControlLock) {
            playerTracker.ign = ""
            playerTracker.exitLoop = true
        }
    }

    /** Resets [playerTracker]'s DPS timer and total damage/heal values. */
    fun resetPlayerTracking() = playerTracker.resetTracking()

    /** Set whether [playerTracker] tracks damage. */
    fun setTrackDamage(trackDamage: Boolean) {
        synchronized(playerTracker.loopControlLock) {
            playerTracker.trackDamage = trackDamage
        }
    }

    /** Set whether [playerTracker] tracks heal received. */
    fun setTrackHealReceived(trackHealReceived: Boolean) {
        synchronized(playerTracker.loopControlLock) {
            playerTracker.trackHealReceived = trackHealReceived
        }
    }

    /** Set whether [playerTracker] tracks heal applied. */
    fun setTrackHealApplied(trackHealApplied: Boolean) {
        synchronized(playerTracker.loopControlLock) {
            playerTracker.trackHealApplied = trackHealApplied
        }
    }

    /** Set whether [playerTracker] tracks gods only. */
    fun setGodsOnly(godsOnly: Boolean) {
        synchronized(playerTracker.loopControlLock) {
            playerTracker.godsOnly = godsOnly
        }
    }

/* -------------------------------------- Public Functions -------------------------------------- */

    /** Assigns the name field to update to the player tracker. */
    fun setNameField(nameField: JTextField) {
        playerTracker.nameField = nameField
    }

    /** Assigns the table model to update to the player tracker. */
    fun setTableModel(tableModel: DefaultTableModel) {
        playerTracker.tableModel = tableModel
    }

    /** Adds a table listener. */
    fun addTableListener(listener: () -> Unit) {
        playerTracker.tableListeners.add(listener)
    }

    /** Adds a combat log update listener. */
    fun addCombatLogListener(listener: (File?) -> Unit) = combatLogListeners.add(listener)

    /** Removes all rows from the table. */
    fun clearTable() = playerTracker.clearTable()
}
