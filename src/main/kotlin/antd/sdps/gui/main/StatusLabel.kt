package antd.sdps.gui.main

import javax.swing.JLabel

/** Displays a status message. */
class StatusLabel : JLabel(" ") {

    /** Makes the label empty. */
    fun reset() {
        text = "Ready"
        toolTipText = ""
    }
}
