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
var initDamage = -1
var initTime = -1.0
var totalDamage = 0

fun main() {

    println(" ----- Smite DPS Calculator by antD ----- ")

    val name = getName() ?: exitProcess(0)
    val combatLogFile = getCombatLogFile() ?: exitProcess(0)

    startResetThread()

    println("\nDo damage to start the DPS timer.")
    println("Hit enter to reset the timer.\n")

    trackDamage(combatLogFile, name)
}

/** Tries to use the saved name. If it can't be found, asks the user for it. */
fun getName(): String? {
    val defaultNameFile = File("in-game_name.txt")

    // locate saved name
    if (defaultNameFile.isFile) {
        val value = defaultNameFile.bufferedReader().readLine()
        if (value != null && value != "") {
            println("Loaded name: $value")
            return value
        }
    }
    // create the file
    else if (!defaultNameFile.exists()) defaultNameFile.createNewFile()

    // ask user for name
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
fun trackDamage(combatLogFile: File, name: String) {
    val br = combatLogFile.bufferedReader()

    var prevTime = -10.0

    var running = true
    while (running) {
        val line = br.readLine()

        if (line == "end") running = false  // EOF

        else if (line != null) {
            val lineData = line.split("|")
            val type = lineData[1].split("=")[1]

            if (lineData[0] == "combatmsg" && (type == "DIT_Damage" || type == "DIT_CritDamage")) {
                val source = lineData[9].split("=")[1].toLowerCase()

                if (source == name) {
                    val time = lineData[8].split("=")[1].toDouble()
                    val damage = lineData[12].split("=")[1].toInt()
                    val reason = lineData[7].split("=")[1]

                    val damageStr = "${"%.2f".format(totalDamage / (time - initTime))} DPS"
                        .replace("Infinity", "---")
                    val timeStr = "@ ${"%.2f".format(time - initTime)}s"
                    val resultStr = "${damageStr.spacing(16)}${timeStr.spacing(16)}$reason"

                    if (resetTimer) {

                        // has to have a delay from previous hit due to a bug with the log not updating right away with
                        // damage ticks that occur at the same time
                        if (time - prevTime > 0.5) {
                            resetTimer = false
                            initDamage = damage
                            initTime = time
                            totalDamage = damage

                            println("${"Timer started.".spacing(32)}$reason")

                        } else {
                            totalDamage += damage
                            println("$resultStr (from previous DPS timer, but the file updated late)")
                        }

                    } else {
                        totalDamage += damage
                        println(resultStr)
                    }

                    prevTime = time
                }
            }
        }
    }

    println("End of combat log.")
}

/** Expands the width of [this] by appending whitespace to the end. */
fun String.spacing(w: Int): String {
    val sb = StringBuilder(this)
    while (sb.length < w) sb.append(" ")
    return sb.toString()
}
