package smitedps

import java.io.File
import java.lang.StringBuilder
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.concurrent.thread
import kotlin.system.exitProcess

val documentsPath = JFileChooser().fileSystemView.defaultDirectory.toString()
val defaultSmiteLogsPath = "$documentsPath\\My Games\\Smite\\BattleGame\\Logs"

var resetTimer = true
var initTime = -1.0
var totalDamage = 0

fun main() {

    println(" ----- Smite DPS Calculator by antD ----- ")

    val ign = getIGN() ?: exitProcess(0)
    val combatLogFile = getCombatLogFile() ?: exitProcess(0)

    startResetThread()

    println("\nDo damage to start the DPS timer.")
    println("Press enter to reset the timer.\n")

    trackDamage(combatLogFile, ign)
}

/** Tries to use the saved name. If it can't be found, asks the user for it. */
fun getIGN(): String? {
    val ignFile = File("in-game_name.txt")

    // locate saved name
    if (ignFile.isFile) {
        val value = ignFile.bufferedReader().readLine()
        if (value != null && value != "") {
            println("Loaded name: $value")
            return value
        }
    }
    // create the file
    else if (!ignFile.exists()) ignFile.createNewFile()

    // ask user for in-game name
    print("No name saved, please enter your in-game name: ")
    val input = readLine()?.toLowerCase()
    return if (input != "") input else null
}

/** Tries to locate the combat log file automatically. If it can't be found, asks the user to select the file. */
fun getCombatLogFile(): File? {
    val defaultSmiteLogsDir = File(defaultSmiteLogsPath)

    // automatically locate log file
    if (defaultSmiteLogsDir.isDirectory) {

        val combatLogs = mutableListOf<File>()
        for (f in defaultSmiteLogsDir.listFiles()!!) {
            if (f.name.contains("CombatLog_") && !f.name.contains("backup"))
                combatLogs.add(f)
        }

        combatLogs.sort()

        if (combatLogs.isNotEmpty()) {
            println("Loaded combat log file: ${combatLogs.last().name}")
            return combatLogs.last()
        }
    }

    // log file could not be found, ask the user to select it
    println("Please select the combat log file.")
    val fileChooserPath = if (File(defaultSmiteLogsPath).isDirectory) defaultSmiteLogsPath else documentsPath

    val fc = JFileChooser(fileChooserPath).apply {
        fileSelectionMode = JFileChooser.FILES_ONLY
        fileFilter = FileNameExtensionFilter("Log Files", "log")
    }

    fc.showOpenDialog(null)

    if (fc.selectedFile == null) exitProcess(0)
    return fc.selectedFile
}

/** Creates a thread to reset the DPS timer. */
fun startResetThread() {
    thread(start = true, isDaemon = true, null, null, block = {

        while (true) {
            readLine()
            Thread.sleep(1000)  // see first hit comment in trackDamage function
            resetTimer = true
            println("DPS timer reset.")
        }
    })
}

/** Prints updated DPS. */
fun trackDamage(combatLogFile: File, ign: String) {
    val br = combatLogFile.bufferedReader()

    var prevTime = -10.0

    var running = true
    while (running) {
        val line = br.readLine()

        if (line == "end") running = false  // EOF

        else if (line != null) {

            // log type that uses "," delimiters
            val lineData = if (line.contains("{")) {
                line.trim { it == '{' || it == '}' || it == ','}
                    .split(",")
                    .map {
                        it.trim()
                            .split(":")[1]
                            .trim { c -> c == '"' }
                    }
            }
            // log type that uses "|" delimiters
            else {
                line.split("|")
                    .map {
                        if (it.contains("=")) it.split("=")[1]
                        else it
                    }
            }

            val type = lineData[1]

            if (type == "DIT_Damage" || type == "DIT_CritDamage") {
                val source = lineData[9]
                val damage = lineData[12].toInt()
                val time = lineData[8].toDouble()
                val reason = lineData[7]

                // damage dealt by user
                if (source.toLowerCase() == ign) {

                    if (resetTimer) {

                        // has to have a delay from previous hit due to a bug with the log not updating right away with
                        // damage ticks that occur at the same time
                        if (time - prevTime > 0.5) {
                            resetTimer = false
                            initTime = time
                            totalDamage = damage

                            println("${"Timer started.".spacing(32)}$reason")

                        } else {
                            totalDamage += damage
                            println("${formatDamage(time, reason)} (from previous DPS timer, but the file updated late)")
                        }
                    } else {
                        totalDamage += damage
                        println(formatDamage(time, reason))
                    }

                    prevTime = time
                }
            }
        }
    }

    println("End of combat log.")
}

fun formatDamage(time: Double, reason: String): String {
    val damageStr = "${"%.2f".format(totalDamage / (time - initTime))} DPS"
        .replace("Infinity", "0.00")
    val timeStr = "@ ${"%.2f".format(time - initTime)}s"
    return "${damageStr.spacing(16)}${timeStr.spacing(16)}$reason"
}

/** Expands the width of [this] by appending whitespace to the end. */
fun String.spacing(w: Int): String {
    val sb = StringBuilder(this)
    while (sb.length < w) sb.append(" ")
    return sb.toString()
}
