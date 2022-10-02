/*
 * Copyright © 2021-2022 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps.workers

import antd.sdps.PopupUncaughtExceptionHandler
import antd.sdps.SharedInstances
import antd.sdps.SharedInstances.initConfig
import antd.sdps.SharedInstances.outputTable
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

class AutoCombatResetWorker :
    ExceptionHandlingSwingWorker<Unit, Unit>(PopupUncaughtExceptionHandler) {

    /** Time to wait to auto-reset in milliseconds. */
    private val delay = 5000L

    @Volatile
    private var enabled = initConfig.autoReset

    @Volatile
    private var hiddenCombatDisabled = false

    /** Stores reset requests. */
    private val resetTaskQueue = LinkedBlockingQueue<Unit>()

    override fun doInBackgroundCatchExceptions() {
        while (!isCancelled) {
            try {

                val enabledCopy = enabled

                // wait for a reset request or until the delay is over
                val result = resetTaskQueue.poll(delay, TimeUnit.MILLISECONDS)
                resetTaskQueue.clear()

                // if there was no reset request, reset tracking
                if (enabledCopy && !hiddenCombatDisabled && result == null)
                    SharedInstances.combatTracker.resetTracking()

            } catch (_: InterruptedException) {
            }
        }
    }

/* -------------------------------------- Public Functions -------------------------------------- */

    /** Enables auto-reset functionality */
    fun enable() {
        enabled = true
        hiddenCombatDisabled = false
        resetTaskQueue.add(Unit)
    }

    /** Disables auto-reset functionality. */
    fun disable() {
        enabled = false
        // cancel any currently running delays
        resetTaskQueue.add(Unit)
    }

    /** Restarts the delay timer. */
    fun reset() {
        hiddenCombatDisabled = false
        resetTaskQueue.add(Unit)
    }

    /** Temporarily disables the delay while there is potential hidden combat. */
    fun hiddenCombatState() {
        hiddenCombatDisabled = true
    }
}
