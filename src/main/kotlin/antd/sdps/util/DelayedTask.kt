/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps.util

import javax.swing.SwingWorker

/** Waits for [delay] ms and then executes [task] on the EDT. Can be interrupted with [cancel]. */
class DelayedTask(
    private val delay: Long,
    private val task: () ->  Unit
) : SwingWorker<Unit, Unit>() {

    override fun doInBackground() {
        try {
            Thread.sleep(delay)
            publish(Unit)
        } catch (_: InterruptedException) {
        }
    }

    override fun process(tasks: MutableList<Unit>?) {
        task()
    }
}
