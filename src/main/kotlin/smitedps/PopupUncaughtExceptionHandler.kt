/*
 * The MIT License (MIT)
 *
 * Copyright © 2021 antD97
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the “Software”), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package smitedps

import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.io.PrintWriter
import java.io.StringWriter
import javax.swing.*

object PopupUncaughtExceptionHandler {

    /** Sets the uncaught exception handler for the current thread to use a popup window. */
    fun set() {
        Thread.currentThread().setUncaughtExceptionHandler { _, e ->
            e.printStackTrace()
            ErrorPopup(e)
        }
    }

    /** JFrame that displays the stacktrace of an error and closes on exit. */
    private class ErrorPopup(e: Throwable) : JFrame() {

        init {
            defaultCloseOperation = EXIT_ON_CLOSE
            title = "Error"

            val exceptionStr = StringWriter().also {
                PrintWriter(it).also { pw -> e.printStackTrace(pw) }
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
                add(JScrollPane(JTextArea(exceptionStr))
                        .apply { preferredSize = Dimension(500, 250) }, c)
            })

            pack()
            setLocationRelativeTo(null)
            isAlwaysOnTop = true
            isResizable = false
            isVisible = true
        }
    }
}