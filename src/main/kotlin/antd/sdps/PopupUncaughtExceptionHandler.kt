/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps

import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.io.PrintWriter
import java.io.StringWriter
import javax.swing.*

/** [Thread.UncaughtExceptionHandler] that creates a popup window with the exception message. */
object PopupUncaughtExceptionHandler : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread?, e: Throwable?) {
        if (e != null) {
            e.printStackTrace()
            SwingUtilities.invokeLater { ErrorPopup(e) }
        }
    }

    /** [JFrame] that displays the message from the [Throwable].. */
    private class ErrorPopup(e: Throwable) : JFrame() {

        init {
            defaultCloseOperation = EXIT_ON_CLOSE
            title = "Error"

            val exceptionStr = StringWriter().also { sw ->
                PrintWriter(sw).also { pw -> e.printStackTrace(pw) }
            }.toString()

            add(JPanel(GridBagLayout()).apply {
                val c = GridBagConstraints()

                c.gridx = 0
                c.gridy = 0
                c.anchor = GridBagConstraints.LINE_START
                c.weightx = 1.0
                c.weighty = 1.0
                c.insets = Insets(10, 10, 10, 10)
                add(JLabel("There was an error. Here's what happened:"), c)

                c.gridx = 0
                c.gridy = 1
                c.fill = GridBagConstraints.BOTH
                c.weightx = 1.0
                c.weighty = 1.0
                c.insets = Insets(0, 10, 10, 10)
                add(
                    JScrollPane(JTextArea(exceptionStr))
                        .apply { preferredSize = Dimension(500, 250) },
                    c
                )
            })

            pack()
            setLocationRelativeTo(null)
            isAlwaysOnTop = true
            isResizable = false
            isVisible = true
        }
    }
}
