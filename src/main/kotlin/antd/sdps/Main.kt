/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps

import antd.sdps.combattracking.CombatTracker
import antd.sdps.combattracking.ObsWriter
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

    // init obs writer
    val obsWriter = ObsWriter(configData)

    // init combat tracker
    val combatTracker = CombatTracker(configData, obsWriter)

    SwingUtilities.invokeAndWait {
        // create main window
        val mainJFrame = MainJFrame(configData, obsWriter, combatTracker, version)

        // add save config shutdown hook
        Runtime.getRuntime().addShutdownHook(ShutdownHook(obsWriter, mainJFrame))
    }

    // check for new release
    if (configData.updateCheck) UpdateChecker.check(version)

    // start combat tracker
    combatTracker.run()
}
