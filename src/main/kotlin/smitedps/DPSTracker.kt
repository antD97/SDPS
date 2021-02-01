package smitedps

import java.io.File
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableModel

class DPSTracker() {

    var tableModel: DefaultTableModel? = null

    private var ign = ""
    private var updatedIGN: String? = null

    private var combatLog: File? = null
    private var updatedCombatLog: File? = null


//        set(value)  {
//            if (field != value) {
//                field = value
//                tableModel!!.rowCount = 0
//            }
//        }

    /** When true, starts DPS timer on next damage dealt. */
    private var resetTimer = true

    /** Tells the DPS tracker to update the in-game name. */
    fun updateIGN(ign: String) {
        if (this.ign != ign && updatedIGN != ign) {
            updatedIGN = ign
            tableModel!!.rowCount = 0
        }
    }

    fun updateCombatLog(combatLog: File) {
        if (this.combatLog != combatLog && )
    }

    /** Tracks the DPS using [ign] and [combatLog] to update [dpsUpdateListeners]. */
    fun run() {
        while (true) {
            TODO("implement")
        }
    }

    /** Resets the DPS timer for the DPS calculation. */
    fun resetTimer() { resetTimer = true }
}
