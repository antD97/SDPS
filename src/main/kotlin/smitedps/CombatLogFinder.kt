package smitedps

import java.io.File
import javax.swing.JComponent
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

object CombatLogFinder {

    private val documentsDir = JFileChooser().fileSystemView.defaultDirectory
    private val defaultSmiteLogsDir =
        File("${documentsDir.absolutePath}\\My Games\\Smite\\BattleGame\\Logs")

    /** Tries to locate the combat log file automatically. If it can't be found, returns null. */
    fun auto(): File? {

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

    /**
     * Asks the user to locate the combat log file. Returns null if cancelled.
     * @param component The parent component of the file open dialog
     */
    fun manual(component: JComponent?): File? {

        val fileChooserDir =
            if (defaultSmiteLogsDir.isDirectory) defaultSmiteLogsDir
            else documentsDir

        val fc = JFileChooser(fileChooserDir).apply {
            fileSelectionMode = JFileChooser.FILES_ONLY
            fileFilter = FileNameExtensionFilter("Log Files", "log")
        }

        fc.showOpenDialog(component)
        return fc.selectedFile
    }
}
