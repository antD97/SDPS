/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps

import antd.sdps.combat.CombatTracker
import antd.sdps.ui.MainJFrame
import javax.swing.SwingUtilities
import javax.swing.UIManager

fun main() {
    val version = object {}.javaClass.getResource("/version.txt").readText()

    // uncaught exception handlers
    Thread.currentThread().uncaughtExceptionHandler = PopupUncaughtExceptionHandler
    SwingUtilities.invokeAndWait {
        Thread.currentThread().uncaughtExceptionHandler = PopupUncaughtExceptionHandler
    }

    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

    // load config data. if none, use defaults
    val configData = ConfigManager.load() ?: ConfigManager.ConfigData()

    // init combat tracker
    val combatTracker = CombatTracker(configData)

    SwingUtilities.invokeAndWait {
        // create main window
        val mainJFrame = MainJFrame(combatTracker, configData, version)

        // add save config shutdown hook
        Runtime.getRuntime().addShutdownHook(SaveConfigShutdownHook(mainJFrame))
    }

    // check for new release
    if (configData.updateCheck) UpdateChecker.check(version)

    // start combat tracker
    combatTracker.run()
}
