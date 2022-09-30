/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps

import antd.sdps.SharedInstances.columnCheckBoxesPanelInitializer
import antd.sdps.SharedInstances.combatTracker
import antd.sdps.SharedInstances.combatTrackerInitializer
import antd.sdps.SharedInstances.initConfig
import antd.sdps.SharedInstances.initConfigInitializer
import antd.sdps.SharedInstances.mainJFrame
import antd.sdps.SharedInstances.mainJFrameInitializer
import antd.sdps.SharedInstances.mainPanelInitializer
import antd.sdps.SharedInstances.minSidebarPanelInitializer
import antd.sdps.SharedInstances.obsWriter
import antd.sdps.SharedInstances.obsWriterInitializer
import antd.sdps.SharedInstances.outputTableInitializer
import antd.sdps.SharedInstances.sidebarPanelInitializer
import antd.sdps.SharedInstances.version
import antd.sdps.SharedInstances.versionInitializer
import antd.sdps.combattracking.CombatTracker
import antd.sdps.combattracking.ObsWriter
import antd.sdps.gui.MainJFrame
import antd.sdps.gui.MainPanel
import antd.sdps.gui.OutputTable
import antd.sdps.gui.sidebar.ColumnCheckBoxesPanel
import antd.sdps.gui.sidebar.MinSidebarPanel
import antd.sdps.gui.sidebar.SidebarPanel
import javax.swing.SwingUtilities
import javax.swing.UIManager

fun main() {

    // uncaught exception handlers
    Thread.currentThread().uncaughtExceptionHandler = PopupUncaughtExceptionHandler
    SwingUtilities.invokeAndWait {
        Thread.currentThread().uncaughtExceptionHandler = PopupUncaughtExceptionHandler
    }

    // look and feel
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

    // shared instances initializers
    versionInitializer = {
        object {}.javaClass.getResource("/version.txt").readText()
    }
    initConfigInitializer = { ConfigManager.load() ?: ConfigManager.ConfigData() }

    combatTrackerInitializer = { CombatTracker() }
    obsWriterInitializer = { ObsWriter() }

    mainJFrameInitializer = { MainJFrame() }
    mainPanelInitializer = { MainPanel() }
    sidebarPanelInitializer = { SidebarPanel() }
    columnCheckBoxesPanelInitializer = { ColumnCheckBoxesPanel() }
    minSidebarPanelInitializer = { MinSidebarPanel() }
    outputTableInitializer = { OutputTable() }

    // save config shutdown hook
    Runtime.getRuntime().addShutdownHook(SaveConfigHook())

    // load main jframe
    mainJFrame

    // check for new release
    if (initConfig.updateCheck) UpdateChecker.check(version)

    // load & start combat tracker
    combatTracker.execute()

    // load & start obs writer
    obsWriter.execute()
}
