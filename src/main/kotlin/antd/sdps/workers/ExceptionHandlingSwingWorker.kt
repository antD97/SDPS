package antd.sdps.workers

import java.lang.Thread.UncaughtExceptionHandler
import javax.swing.SwingWorker
import kotlin.system.exitProcess

/** A [SwingWorker] that catches [RuntimeException]s in [doInBackground] and throws them using
 * [uncaughtExceptionHandler]. */
abstract class ExceptionHandlingSwingWorker<T, V>(
    private val uncaughtExceptionHandler: UncaughtExceptionHandler
) : SwingWorker<T, V>() {

    /** **Override [doInBackgroundCatchExceptions], not this.** */
    override fun doInBackground(): T? {
        try {
            return doInBackgroundCatchExceptions()
        } catch (e: RuntimeException) {
            uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e)
            throw e
        }
    }

    /** [SwingWorker.doInBackground] but runtime exceptions are caught using
     * [uncaughtExceptionHandler]. */
    abstract fun doInBackgroundCatchExceptions(): T
}
