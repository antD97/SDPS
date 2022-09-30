/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps.gui

import antd.sdps.SharedInstances.initConfig
import antd.sdps.SharedInstances.mainPanel
import antd.sdps.SharedInstances.version
import java.awt.Dimension
import javax.swing.JFrame

/** Main tool window. */
class MainJFrame : JFrame() {

    init {
        title = "SDPS v$version - antD"
        defaultCloseOperation = EXIT_ON_CLOSE
        add(mainPanel)

        // load init config
        if (initConfig.size != null) size = initConfig.size else pack()
        if (initConfig.loc != null) location = initConfig.loc else setLocationRelativeTo(null)
        isAlwaysOnTop = initConfig.onTop
        updateMinSize(initConfig.sidebar)

        isVisible = true
    }

/* -------------------------------------- Public Functions -------------------------------------- */

    /** Updates the minimum size of the [JFrame] window according to [sidebarEnabled]. */
    fun updateMinSize(sidebarEnabled: Boolean) {
        minimumSize =
            if (sidebarEnabled) Dimension(300, 425)
            else Dimension(150, 100)
    }
}
