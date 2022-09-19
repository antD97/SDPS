/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps.ui

import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.ActionEvent
import javax.swing.JButton
import javax.swing.JPanel

class MinSidebarPanel(private val sidebarPanel: SidebarPanel) : JPanel(GridBagLayout()) {

/* ----------------------------------------- UI Content ----------------------------------------- */

    private val maximizeSidebarButton = JButton()
        .apply {
            preferredSize = Dimension(10, 20)
            addActionListener(::maximizeSidebarButtonClick)
            toolTipText = "Maximize the sidebar (Escape)"
        }

/* -------------------------------------------- Init -------------------------------------------- */

    init {
        val c = GridBagConstraints()

        // maximize side bar button
        c.weighty = 1.0
        c.anchor = GridBagConstraints.PAGE_START
        add(maximizeSidebarButton, c)

        minimumSize = preferredSize
    }

/* ------------------------------------------ Listeners ----------------------------------------- */

    @Suppress("UNUSED_PARAMETER")
    fun maximizeSidebarButtonClick(e: ActionEvent?) {
        sidebarPanel.isVisible = true
        isVisible = false
        topLevelAncestor.minimumSize = MainJFrame.sidebarDisabledMinSize
    }
}
