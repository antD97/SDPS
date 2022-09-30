/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps.gui.sidebar

import antd.sdps.SharedInstances.initConfig
import antd.sdps.SharedInstances.mainJFrame
import antd.sdps.SharedInstances.sidebarPanel
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JButton
import javax.swing.JPanel

/** Minimized sidebar panel. */
class MinSidebarPanel : JPanel(GridBagLayout()) {

/* ----------------------------------------- UI Content ----------------------------------------- */

    val maximizeSidebarButton = JButton()
        .apply {
            preferredSize = Dimension(10, 20)
            toolTipText = "Maximize the sidebar (Escape)"

            addActionListener {
                sidebarPanel.isVisible = true
                this@MinSidebarPanel.isVisible = false
                mainJFrame.updateMinSize(true)
            }
        }

/* -------------------------------------------- Init -------------------------------------------- */

    init {
        val c = GridBagConstraints()

        // maximize side bar button
        c.weighty = 1.0
        c.anchor = GridBagConstraints.PAGE_START
        add(maximizeSidebarButton, c)

        minimumSize = preferredSize

        // load init config
        if (initConfig.sidebar) isVisible = false
    }
}
