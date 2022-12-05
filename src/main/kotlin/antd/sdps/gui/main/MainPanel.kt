/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps.gui.main

import antd.sdps.SharedInstances.minSidebarPanel
import antd.sdps.SharedInstances.outputTable
import antd.sdps.SharedInstances.sidebarPanel
import antd.sdps.SharedInstances.statusLabel
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextField
import javax.swing.SwingUtilities

/** The tool's main window content panel. */
class MainPanel : JPanel(GridBagLayout()) {

/* ----------------------------------------- UI Content ----------------------------------------- */

    private val outputTableScrollPane = JScrollPane(outputTable)
        .apply { preferredSize = Dimension(400, 300) }

/* -------------------------------------------- Init -------------------------------------------- */

    init {
        val c = GridBagConstraints()

        // sidebar
        c.gridx = 0
        c.fill = GridBagConstraints.BOTH
        c.weightx = 0.0
        c.weighty = 1.0
        c.insets = Insets(10, 10, 10, 10)

        add(sidebarPanel, c)

        // minimized sidebar
        c.gridx = 1
        c.insets = Insets(10, 5, 10, 5)
        add(minSidebarPanel, c)

        // table + status message
        c.gridx = 2
        c.weightx = 1.0
        c.insets = Insets(10, 0, 10, 10)
        add(
            JPanel(GridBagLayout()).apply {
                val c2 = GridBagConstraints()

                // main table
                c2.gridx = 0
                c2.gridy = 0
                c2.fill = GridBagConstraints.BOTH
                c2.weightx = 1.0
                c2.weighty = 1.0

                add(outputTableScrollPane, c2)

                // status message
                c2.gridy++
                c2.weighty = 0.0
                c2.insets = Insets(5, 0, 0, 0)

                add(statusLabel, c2)
            },
            c
        )

        // keyboard shortcuts
        isFocusable = true
        addMouseListener(object : MouseListener {
            override fun mouseClicked(e: MouseEvent?) {}
            override fun mousePressed(e: MouseEvent?) = requestFocus()
            override fun mouseReleased(e: MouseEvent?) {}
            override fun mouseEntered(e: MouseEvent?) {}
            override fun mouseExited(e: MouseEvent?) {}
        })

        fun Component.onEachChildComponent(
            condition: (Component) -> Boolean,
            action: (Component) -> Unit
        ) {
            if (condition(this)) {
                action.invoke(this)
                if (this is Container) {
                    for (comp in components) comp.onEachChildComponent(condition, action)
                }
            }
        }

        onEachChildComponent({ component: Component -> component !is JTextField },
            {
                it.addKeyListener(object : KeyListener {
                    override fun keyTyped(e: KeyEvent?) {}
                    override fun keyPressed(e: KeyEvent?) = shortcutButtonPress(e)
                    override fun keyReleased(e: KeyEvent?) {}
                })
            })
    }

/* -------------------------------------- Public Functions -------------------------------------- */

    /** Move the table scroll to the bottom. */
    fun scrollTableToBottom() {
        SwingUtilities.invokeLater {
            val scrollBar = outputTableScrollPane.verticalScrollBar
            scrollBar.value = scrollBar.maximum
        }
    }

/* ------------------------------------------- Helpers ------------------------------------------ */

    /** Handles shortcut key actions */
    private fun shortcutButtonPress(e: KeyEvent?) {
        if (e != null) {
            when (e.keyCode) {
                KeyEvent.VK_ESCAPE -> {
                    if (sidebarPanel.isVisible) sidebarPanel.minimizeSidebarButton.doClick()
                    else minSidebarPanel.maximizeSidebarButton.doClick()
                }
                KeyEvent.VK_T -> sidebarPanel.onTopCheckBox.doClick()
                KeyEvent.VK_1 -> sidebarPanel.trackDamageCheckBox.doClick()
                KeyEvent.VK_2 -> sidebarPanel.trackHealReceivedCheckBox.doClick()
                KeyEvent.VK_3 -> sidebarPanel.trackHealAppliedCheckBox.doClick()
                KeyEvent.VK_G -> sidebarPanel.godsOnlyCheckBox.doClick()
                KeyEvent.VK_R -> {
                    if (e.isControlDown) sidebarPanel.autoResetCheckBox.doClick()
                    else sidebarPanel.resetCombatButton.doClick()
                }
                KeyEvent.VK_C -> sidebarPanel.clearTableButton.doClick()
            }
        }
    }
}
