/*
 * Copyright © 2021 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package sdps

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
        val latestTag = GitHub.connect().getRepository("antD97/SDPS").latestRelease.tagName

        if ("v$currVer" != latestTag) {

            // create the message window
            val jFrame = JFrame("New Version")

            SwingUtilities.invokeAndWait {
                jFrame.apply {
                    defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE

                    // window content
                    add(JPanel(GridBagLayout()).apply {
                        val c = GridBagConstraints()
                        c.gridx = 0
                        c.gridy = 0
                        c.gridwidth = 2
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
                        c.insets = Insets(0, 10, 10, 10)
                        JButton("Download").apply {
                            addActionListener {
                                Desktop.getDesktop()
                                    .browse(URI("https://github.com/antD97/SDPS/releases/tag/$latestTag"))
                            }
                        }.also { add(it, c) }

                        c.gridx++
                        c.insets = Insets(0, 0, 10, 10)
                        JButton("Ignore").apply {
                            addActionListener {
                                jFrame.dispatchEvent(WindowEvent(jFrame, WindowEvent.WINDOW_CLOSING))
                            }
                        }.also { add(it, c) }
                    })

                    pack()
                    setLocationRelativeTo(null)
                    isAlwaysOnTop = true
                    isResizable = false
                    isVisible = true
                }
            }
        }
    }
}