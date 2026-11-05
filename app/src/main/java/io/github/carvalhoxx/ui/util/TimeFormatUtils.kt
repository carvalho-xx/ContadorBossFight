package io.github.carvalhoxx.ui.util

import java.util.Locale

fun formatDurationMillis(millis: Long): String {
    val safeMillis = millis.coerceAtLeast(0L)
    val hours = safeMillis / 3_600_000
    val minutes = (safeMillis % 3_600_000) / 60_000
    val seconds = (safeMillis % 60_000) / 1_000
    val centiseconds = (safeMillis % 1_000) / 10

    return if (hours > 0) {
        String.format(Locale.US, "%02d:%02d:%02d.%02d", hours, minutes, seconds, centiseconds)
    } else {
        String.format(Locale.US, "%02d:%02d.%02d", minutes, seconds, centiseconds)
    }
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
