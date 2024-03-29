/*
 * Copyright © 2021 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package sdps

import java.io.File
import javax.swing.JFileChooser

object CombatLogFinder {

    private val documentsDir = JFileChooser().fileSystemView.defaultDirectory
    private val defaultSmiteLogsDir =
        File("${documentsDir.absolutePath}\\My Games\\Smite\\BattleGame\\Logs")

    /** Tries to locate the combat log file automatically. If it can't be found, returns null. */
    fun search(): File? {

        if (defaultSmiteLogsDir.isDirectory) {

            val combatLogs = mutableListOf<File>()
            for (f in defaultSmiteLogsDir.listFiles()!!) {
                if (f.name.contains("CombatLog_") && !f.name.contains("backup"))
                    combatLogs.add(f)
            }

            combatLogs.sortBy { it.lastModified() }
            if (combatLogs.isNotEmpty()) return combatLogs.last()
        }

        return null
    }
}
