/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps.util

import javax.swing.SwingUtilities

/** Invoke and wait [doRun] on the EDT if not already on the EDT. */
fun edtInvokeAndWaitIfNeeded(doRun: Runnable) {
    if (SwingUtilities.isEventDispatchThread()) doRun.run()
    else SwingUtilities.invokeAndWait { doRun.run() }
}
