/*
 * The MIT License (MIT)
 *
 * Copyright © 2021 antD97
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the “Software”), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package smitedps

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

            combatLogs.sort()
            if (combatLogs.isNotEmpty()) return combatLogs.last()
        }

        return null
    }
}
