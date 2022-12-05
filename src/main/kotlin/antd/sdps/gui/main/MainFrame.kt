/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps.gui.main

import antd.sdps.SharedInstances.initConfig
import antd.sdps.SharedInstances.mainPanel
import antd.sdps.SharedInstances.version
import java.awt.Dimension
import javax.swing.JFrame

/** Main tool window. */
class MainFrame : JFrame() {

    init {
        title = "SDPS v$version - antD"
        defaultCloseOperation = EXIT_ON_CLOSE
        add(mainPanel)

        // load init config
        size = if (initConfig.size != null) initConfig.size
        else {
            pack()
            Dimension(size.width, size.height + 100)
        }
        if (initConfig.loc != null) location = initConfig.loc else setLocationRelativeTo(null)
        isAlwaysOnTop = initConfig.onTop
        updateMinSize(initConfig.sidebar)

        isVisible = true
    }

/* -------------------------------------- Public Functions -------------------------------------- */

    /** Updates the minimum size of the [JFrame] window according to [sidebarEnabled]. */
    fun updateMinSize(sidebarEnabled: Boolean) {
        minimumSize =
            if (sidebarEnabled) Dimension(300, 450)
            else Dimension(150, 100)
    }
}