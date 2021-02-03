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
import java.awt.Toolkit
import javax.swing.*

fun main() {
    PopupUncaughtExceptionHandler.set()
    val dpsTracker = DPSTracker()

    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

    SwingUtilities.invokeAndWait {
        PopupUncaughtExceptionHandler.set()

        JFrame("Smite DPS Calculator - antD").apply {
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE

            add(MainPanel(dpsTracker))
            pack()

            minimumSize = Dimension(size.width - 275, size.height - 75)
            setLocation(
                Toolkit.getDefaultToolkit().screenSize.width - this.width - 10,
                10)
            isAlwaysOnTop = true
            isVisible = true
        }
    }

    dpsTracker.run()
}
