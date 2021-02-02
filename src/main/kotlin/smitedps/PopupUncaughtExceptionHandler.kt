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
            SwingUtilities.invokeAndWait { ErrorPopup(e) }
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
            isResizable = false
            isVisible = true
        }
    }
}