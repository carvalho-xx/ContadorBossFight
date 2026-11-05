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
