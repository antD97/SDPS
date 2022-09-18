/*
 * Copyright © 2021 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package antd.sdps

import antd.sdps.ConfigManager.save
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.awt.Dimension
import java.awt.Point
import java.io.File

class ConfigManagerTest {

    @Test
    fun configManager() {
        val tempConfFile = File.createTempFile("sdps","conf")

        val configData = ConfigManager.ConfigData(
            loc = Point(111, 222),
            size = Dimension(333, 444),
            ign = "ign",
            sidebar = false,
            onTop = true,
            trackDamage = false,
            trackHealReceived = true,
            trackHealApplied = true,
            godsOnly = false,
            columnOrder = listOf("a", "b", "c"),
            columnWidths = listOf(555, 666, 777),
            rowSize = 888,
            updateCheck = false
        )

        configData.save(tempConfFile.absolutePath)

        assertEquals(configData, ConfigManager.load(tempConfFile.absolutePath))
    }
}
