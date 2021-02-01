package smitedps

import java.io.File
import java.lang.StringBuilder
import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.concurrent.thread
import kotlin.system.exitProcess

var resetTimer = true
var initDamage = -1
var initTime = -1.0
var totalDamage = 0

fun main() {

    val dpsTracker = DPSTracker()

    javax.swing.SwingUtilities.invokeLater {
        JFrame("Smite DPS Calculator - antD").apply {
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            add(MainPanel(dpsTracker))
            pack()
            setLocationRelativeTo(null)
            isVisible = true
        }
    }

    dpsTracker.run()
}

//    println(" ----- Smite DPS Calculator by antD ----- ")
//
//    val name = getName() ?: exitProcess(0)
////    val combatLogFile = getCombatLogFile() ?: exitProcess(0)
//
//    startResetThread()
//
//    println("\nDo damage to start the DPS timer.")
//    println("Hit enter to reset the timer.\n")

//    trackDamage(combatLogFile, name)

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

