/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps.combat

import java.io.File
import javax.swing.JFileChooser

/** Used to find the combat log file. */
object CombatLogFinder {

    private val documentsDir = JFileChooser().fileSystemView.defaultDirectory
    private val defaultSmiteLogsDir =
        File("${documentsDir.absolutePath}\\My Games\\Smite\\BattleGame\\Logs")

    /** Tries to locate the combat log file automatically. If it can't be found, returns null. */
    fun search(): File? {
        return if (defaultSmiteLogsDir.isDirectory) {
            defaultSmiteLogsDir.listFiles()!!
                .filter { it.name.contains("CombatLog_") }
                .filterNot { it.name.contains("backup") }
                .maxByOrNull { it.lastModified() }
        } else null
    }
}
