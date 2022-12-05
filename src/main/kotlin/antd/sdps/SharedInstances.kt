/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps

import antd.sdps.gui.main.MainFrame
import antd.sdps.gui.main.MainPanel
import antd.sdps.gui.main.OutputTable
import antd.sdps.gui.main.StatusLabel
import antd.sdps.gui.main.sidebar.ColumnCheckBoxesPanel
import antd.sdps.gui.main.sidebar.MinSidebarPanel
import antd.sdps.gui.main.sidebar.SidebarPanel
import antd.sdps.gui.obs.ObsDialog
import antd.sdps.gui.obs.ObsPanel
import antd.sdps.workers.AutoCombatResetWorker
import antd.sdps.workers.CombatTracker
import antd.sdps.workers.ObsWriter
import java.awt.Component
import javax.swing.SwingUtilities

/** Used to lazily retrieve singleton dependency instances. */
object SharedInstances {

    private val initializers = mutableMapOf<String, () -> Any>()
    private val guiInitializers = mutableMapOf<String, () -> Component>()

    var versionInitializer by initializers
    var initConfigInitializer by initializers

    var combatTrackerInitializer by initializers
    var obsWriterInitializer by initializers
    var autoCombatResetWorkerInitializer by initializers

    var mainJFrameInitializer by guiInitializers
    var mainPanelInitializer by guiInitializers
    var sidebarPanelInitializer by guiInitializers
    var columnCheckBoxesPanelInitializer by guiInitializers
    var minSidebarPanelInitializer by guiInitializers
    var outputTableInitializer by guiInitializers
    var statusLabelInitializer by guiInitializers

    var obsJFrameInitializer by guiInitializers
    var obsPanelInitializer by guiInitializers

    val version by lazy { versionInitializer() as String }
    val initConfig by lazy { initConfigInitializer() as ConfigManager.ConfigData }

    val combatTracker by lazy { combatTrackerInitializer() as CombatTracker }
    val obsWriter by lazy { obsWriterInitializer() as ObsWriter }
    val autoCombatResetWorker by lazy {
        autoCombatResetWorkerInitializer() as AutoCombatResetWorker
    }

    val mainFrame by lazy { edtInvokeAndWaitIfNeeded { mainJFrameInitializer() as MainFrame } }
    val mainPanel by lazy { edtInvokeAndWaitIfNeeded { mainPanelInitializer() as MainPanel } }
    val sidebarPanel by lazy {
        edtInvokeAndWaitIfNeeded { sidebarPanelInitializer() as SidebarPanel }
    }
    val columnCheckBoxesPanel by lazy {
        edtInvokeAndWaitIfNeeded { columnCheckBoxesPanelInitializer() as ColumnCheckBoxesPanel }
    }
    val minSidebarPanel by lazy {
        edtInvokeAndWaitIfNeeded { minSidebarPanelInitializer() as MinSidebarPanel }
    }
    val outputTable by lazy { edtInvokeAndWaitIfNeeded { outputTableInitializer() as OutputTable } }
    val statusLabel by lazy { edtInvokeAndWaitIfNeeded { statusLabelInitializer() as StatusLabel } }

    val obsDialog by lazy { edtInvokeAndWaitIfNeeded { obsJFrameInitializer() as ObsDialog } }
    val obsPanel by lazy { edtInvokeAndWaitIfNeeded { obsPanelInitializer() as ObsPanel } }

    /** Invoke and wait [doRun] on the EDT if not already on the EDT. */
    private fun <T>edtInvokeAndWaitIfNeeded(doRun: () -> T): T {
        var result: T? = null
        if (SwingUtilities.isEventDispatchThread()) result = doRun()
        else SwingUtilities.invokeAndWait { result = doRun() }
        return result ?: throw IllegalStateException()
    }
}
