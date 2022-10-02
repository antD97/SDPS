/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps

import antd.sdps.ConfigManager.save
import antd.sdps.SharedInstances.mainFrame
import org.kohsuke.github.GitHub
import java.awt.Desktop
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.event.WindowEvent
import java.net.URI
import javax.swing.*

/** Used to check if a newer version of the tool has been released. */
object UpdateChecker {

    /** Displays a message if the newest version found on GitHub does not match [currVer]. */
    fun check(currVer: String) {
        val latestTag = GitHub.connect().getRepository("antD97/SDPS").listReleases()
            .find { !it.isPrerelease && !it.isDraft }!!
            .tagName

        if ("v$currVer" != latestTag) {

            SwingUtilities.invokeAndWait {

                // create new window
                val dialog = JDialog(mainFrame, true)

                dialog.apply {
                    title = "New Version"

                    // content
                    add(JPanel(GridBagLayout()).apply {
                        val c = GridBagConstraints()
                        c.gridx = 0
                        c.gridy = 0
                        c.gridwidth = 3
                        c.anchor = GridBagConstraints.LINE_START
                        c.weightx = 1.0
                        c.weighty = 1.0
                        c.insets = Insets(10, 10, 0, 10)
                        add(JLabel("A new version of SDPS has been found: $latestTag\n"), c)

                        c.gridy++
                        c.insets = Insets(0, 10, 10, 10)
                        add(JLabel("You are currently using: v$currVer"), c)

                        c.gridy++
                        c.gridwidth = 1
                        c.anchor = GridBagConstraints.CENTER
                        c.insets = Insets(0, 10, 10, 5)
                        JButton("Download").apply {
                            addActionListener {
                                Desktop.getDesktop().browse(
                                    URI("https://github.com/antD97/SDPS/releases/tag/$latestTag")
                                )
                            }
                        }.also { add(it, c) }

                        c.gridx++
                        c.insets = Insets(0, 5, 10, 5)
                        JButton("Ignore").apply {
                            addActionListener {
                                dialog.dispatchEvent(
                                    WindowEvent(dialog, WindowEvent.WINDOW_CLOSING)
                                )
                            }
                        }.also { add(it, c) }

                        c.gridx++
                        c.insets = Insets(0, 5, 10, 10)
                        JButton("Always Ignore").apply {
                            addActionListener {
                                // update & save config
                                val configData = ConfigManager.load() ?: ConfigManager.ConfigData()
                                configData.updateCheck = false
                                configData.save()

                                dialog.dispatchEvent(
                                    WindowEvent(dialog, WindowEvent.WINDOW_CLOSING)
                                )
                            }
                        }.also { add(it, c) }
                    })

                    pack()
                    setLocationRelativeTo(null)
                    defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
                    isAlwaysOnTop = true
                    isResizable = false
                    isVisible = true
                }
            }
        }
    }
}
