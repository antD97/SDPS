package antd.sdps

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import javax.swing.SwingUtilities

internal class UtilTest {

    @Test
    fun edtInvokeAndWaitIfNeeded() {

        // main thread
        edtInvokeAndWaitIfNeeded {
            assertTrue(SwingUtilities.isEventDispatchThread())
        }

        // on edt thread
        SwingUtilities.invokeAndWait {
            edtInvokeAndWaitIfNeeded {
                assertTrue(SwingUtilities.isEventDispatchThread())
            }
        }
    }
}