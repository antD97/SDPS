/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps

import antd.sdps.SharedInstances.autoCombatResetWorker
import antd.sdps.SharedInstances.autoCombatResetWorkerInitializer
import antd.sdps.SharedInstances.columnCheckBoxesPanelInitializer
import antd.sdps.SharedInstances.combatTracker
import antd.sdps.SharedInstances.combatTrackerInitializer
import antd.sdps.SharedInstances.initConfig
import antd.sdps.SharedInstances.initConfigInitializer
import antd.sdps.SharedInstances.mainFrame
import antd.sdps.SharedInstances.mainJFrameInitializer
import antd.sdps.SharedInstances.mainPanelInitializer
import antd.sdps.SharedInstances.minSidebarPanelInitializer
import antd.sdps.SharedInstances.obsDialog
import antd.sdps.SharedInstances.obsJFrameInitializer
import antd.sdps.SharedInstances.obsPanelInitializer
import antd.sdps.SharedInstances.obsWriter
import antd.sdps.SharedInstances.obsWriterInitializer
import antd.sdps.SharedInstances.outputTableInitializer
import antd.sdps.SharedInstances.sidebarPanelInitializer
import antd.sdps.SharedInstances.version
import antd.sdps.SharedInstances.versionInitializer
import antd.sdps.gui.main.MainFrame
import antd.sdps.gui.main.MainPanel
import antd.sdps.gui.main.OutputTable
import antd.sdps.gui.main.sidebar.ColumnCheckBoxesPanel
import antd.sdps.gui.main.sidebar.MinSidebarPanel
import antd.sdps.gui.main.sidebar.SidebarPanel
import antd.sdps.gui.obs.ObsDialog
import antd.sdps.gui.obs.ObsPanel
import antd.sdps.workers.AutoCombatResetWorker
import antd.sdps.workers.CombatTracker
import antd.sdps.workers.ObsWriter
import java.util.concurrent.CancellationException
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
    autoCombatResetWorkerInitializer = { AutoCombatResetWorker() }

    mainJFrameInitializer = { MainFrame() }
    mainPanelInitializer = { MainPanel() }
    sidebarPanelInitializer = { SidebarPanel() }
    columnCheckBoxesPanelInitializer = { ColumnCheckBoxesPanel() }
    minSidebarPanelInitializer = { MinSidebarPanel() }
    outputTableInitializer = { OutputTable() }

    obsJFrameInitializer = { ObsDialog() }
    obsPanelInitializer = { ObsPanel() }

    // save config shutdown hook
    Runtime.getRuntime().addShutdownHook(ShutdownHook())

    // load jframes
    mainFrame
    obsDialog

    // check for new release
    if (initConfig.updateCheck) UpdateChecker.check(version)

    // load & start workers
    combatTracker.execute()
    autoCombatResetWorker.execute()
    obsWriter.execute()
}
