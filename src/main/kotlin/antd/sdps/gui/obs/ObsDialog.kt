/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps.gui.obs

import antd.sdps.SharedInstances.mainFrame
import antd.sdps.SharedInstances.obsPanel
import javax.swing.JDialog

/** OBS settings dialog window. */
class ObsDialog : JDialog(mainFrame, true) {

    init {
        title = "OBS Text Source Settings"
        defaultCloseOperation = HIDE_ON_CLOSE

        add(obsPanel)
        isResizable = false
        pack()
    }
}
