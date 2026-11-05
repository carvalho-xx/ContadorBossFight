package io.github.carvalhoxx.ui.util

import java.util.Locale

fun formatDurationMillis(millis: Long): String {
    val safeMillis = millis.coerceAtLeast(0L)
    val hours = safeMillis / 3_600_000
    val minutes = (safeMillis % 3_600_000) / 60_000
    val seconds = (safeMillis % 60_000) / 1_000
    val centiseconds = (safeMillis % 1_000) / 10

    return String.format(Locale.US, "%02d:%02d:%02d.%02d", hours, minutes, seconds, centiseconds)
}

fun hmsToMillis(hours: Int, minutes: Int, seconds: Int, centiseconds: Int = 0): Long {
    val h = hours.coerceAtLeast(0).toLong()
    val m = minutes.coerceIn(0, 59).toLong()
    val s = seconds.coerceIn(0, 59).toLong()
    val cs = centiseconds.coerceIn(0, 99).toLong()
    return (h * 3_600_000L) + (m * 60_000L) + (s * 1_000L) + (cs * 10L)
}

fun toRomanNumeral(number: Int): String {
    val safe = number.coerceIn(1, 3999)
    val values = intArrayOf(1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1)
    val symbols = arrayOf("M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I")
    val sb = StringBuilder()
    var remaining = safe
    for (i in values.indices) {
        while (remaining >= values[i]) {
            remaining -= values[i]
            sb.append(symbols[i])
        }
    }
    return sb.toString()
}
