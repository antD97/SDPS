/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps.ui

import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

/** JPanel with a [JLabel] followed by the specified [JComponent]s. */
class LabelPanel(
    label: String,
    vararg components: JComponent,
    gap: Int = 0
) : JPanel(GridBagLayout()) {

    init {
        val c = GridBagConstraints()

        // label
        c.gridx = 0
        c.gridy = 0
        add(JLabel(label).apply {
            if (gap > 0) {
                preferredSize = Dimension(preferredSize.width + gap, preferredSize.height)
            }
        }, c)

        // components
        c.insets = Insets(0, 5, 0, 0)
        for (component in components) {
            c.gridx++
            add(component, c)
        }
    }
}
