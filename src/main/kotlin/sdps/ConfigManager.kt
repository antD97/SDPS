/*
 * Copyright © 2021 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
package sdps

import java.awt.Dimension
import java.awt.Point
import java.io.File

/** Saves and loads config data from a file. */
object ConfigManager {

    private const val defaultSavePath = "sdps.conf"

    fun ConfigData.save(path: String = defaultSavePath): Boolean {
        val f = File(path)

        return if (!f.exists() || f.isFile) {
            val sb = StringBuilder()

            fun StringBuilder.appendKeyValuePair(key: String, point: Point?) {
                if (point != null) append("$key=${point.x},${point.y}\n")
            }
            fun StringBuilder.appendKeyValuePair(key: String, dim: Dimension?) {
                if (dim != null) append("$key=${dim.width},${dim.height}\n")
            }
            fun StringBuilder.appendKeyValuePair(key: String, str: String?) {
                if (str != null) append("$key=$str\n")
            }
            fun StringBuilder.appendKeyValuePair(key: String, bool: Boolean?) {
                if (bool != null) append("$key=$bool\n")
            }
            fun StringBuilder.appendKeyValuePair(key: String, list: List<*>?) {
                if (list != null) append("$key=${list.joinToString(",")}\n")
            }
            fun StringBuilder.appendKeyValuePair(key: String, x: Int?) {
                if (x != null) append("$key=$x\n")
            }

            sb.appendKeyValuePair("loc", loc)
            sb.appendKeyValuePair("size", size)
            sb.appendKeyValuePair("ign", ign)
            sb.appendKeyValuePair("sidebar", sidebar)
            sb.appendKeyValuePair("onTop", onTop)
            sb.appendKeyValuePair("columnOrder", columnOrder)
            sb.appendKeyValuePair("columnWidths", columnWidths)
            sb.appendKeyValuePair("rowSize", rowSize)
            sb.appendKeyValuePair("updateCheck", updateCheck)

            f.writeText(sb.toString())
            true
        } else false
    }

    fun load(path: String = defaultSavePath): ConfigData? {
        val f = File(path)
        val sd = ConfigData()

        return if (f.isFile) {
            for (line in f.readLines()) {
                val lineData = line.split("=")

                if (lineData.size == 2) {
                    val key = lineData[0].trim()
                    val value = lineData[1].trim()

                    when (key) {
                        "loc" ->          sd.loc = value.toIntPairOrNull()?.toPoint()
                        "size" ->         sd.size = value.toIntPairOrNull()?.toDimension()
                        "ign" ->          sd.ign = value
                        "sidebar" ->      sd.sidebar = value.toBoolean()
                        "onTop" ->        sd.onTop = value.toBoolean()
                        "columnOrder" ->  sd.columnOrder = value.split(",").map { it.trim() }
                        "columnWidths" -> sd.columnWidths = value.toIntListOrNull()
                        "rowSize" ->      sd.rowSize = value.toInt()
                        "updateCheck" ->  sd.updateCheck = value.toBoolean()
                    }
                }
            }
            sd
        } else null
    }

    data class ConfigData(var loc: Point? = null,
                          var size: Dimension? = null,
                          var ign: String? = null,
                          var sidebar: Boolean = true,
                          var onTop: Boolean = false,
                          var columnOrder: List<String>? = null,
                          var columnWidths: List<Int>? = null,
                          var rowSize: Int = 12,
                          var updateCheck: Boolean = true)

/* -------------------------------------------- Util -------------------------------------------- */

    /**
     * Loads a [Pair]<Int, Int> from a comma separated [String]. Returns null, if the String format
     * is bad.
     */
    private fun String.toIntPairOrNull(): Pair<Int, Int>? {
        val split = split(",")
        if (split.size == 2) {
            val a = split[0].toIntOrNull()
            val b = split[1].toIntOrNull()
            if (a != null && b != null) return Pair(a, b)
        }
        return null
    }

    private fun Pair<Int, Int>.toDimension(): Dimension = Dimension(this.first, this.second)
    private fun Pair<Int, Int>.toPoint(): Point = Point(this.first, this.second)

    @Suppress("UNCHECKED_CAST")
    private fun String.toIntListOrNull(): List<Int>? {
        val ints = split(",")
            .map { it.trim().toIntOrNull() }
        return if (ints.all { it != null }) ints as List<Int> else null
    }
}
