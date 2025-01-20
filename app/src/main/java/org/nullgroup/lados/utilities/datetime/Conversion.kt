package org.nullgroup.lados.utilities.datetime

import android.icu.util.TimeZone
import com.google.firebase.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun Long.toDateStringFromEpochMillis(
    currentZoneId: String = "UTC",
    pattern: String = "dd/MM/yyyy",
): String {
    return LocalDateTime.ofInstant(
        Instant.ofEpochMilli(this),
        ZoneId.of(currentZoneId)
    ).format(
        DateTimeFormatter.ofPattern(pattern)
    )
}

const val SECONDS_IN_DAY = 86400L
fun Long.toDayCountsFromSeconds(): Long {
    return this / SECONDS_IN_DAY
}
fun Long.toTimeOfDayInSecondsFromSeconds(): Long {
    return this % SECONDS_IN_DAY
}
fun Long.toTimeOfDayMillisFromSeconds(): Long {
    return (this % SECONDS_IN_DAY) * 1000
}
fun secondsFrom(dayCounts: Long, timeOfDayInSecond: Long): Long {
    return dayCounts * SECONDS_IN_DAY + timeOfDayInSecond
}

fun currentHostTimeZone() = TimeZone.getDefault()
fun currentHostTimeZoneInString() = currentHostTimeZone().id

fun LocalDateTime.toTimestamp(zoneId: String): Timestamp {
    val zonedDateTime = ZonedDateTime.of(this, ZoneId.of(zoneId))
    return Timestamp(zonedDateTime.toInstant().epochSecond, 0)
}

fun Timestamp.toLocalDateTime(zoneId: String): LocalDateTime {
    val zonedDateTime = ZonedDateTime.ofInstant(this.toDate().toInstant(), ZoneId.of(zoneId))
    return zonedDateTime.toLocalDateTime()
}

fun Timestamp.toInstant24(): Instant {
    return Instant.ofEpochSecond(this.seconds, this.nanoseconds.toLong())
}

// Can't use: this.toInstant().toEpochMilli(), requires API 26
fun Timestamp.toEpochMillis(): Long {
    return this.toInstant24().toEpochMilli()
}

fun Timestamp.toDateMillis(): Long {
    return this.toEpochMillis() / DAY_IN_MILLIS * DAY_IN_MILLIS
}

fun Timestamp.toZoneOffset(zoneId: String): ZoneOffset {
    return ZonedDateTime.ofInstant(this.toInstant24(), ZoneId.of(zoneId)).offset
}

fun Long.toTimeOfDateMillisWith(reversedFromOffSet: ZoneOffset): Long {
    return this - reversedFromOffSet.totalSeconds * 1000
}

const val DAY_IN_MILLIS = 86400000L
fun Timestamp.toTimeOfDayMillis(): Long {
    return this.toEpochMillis() % DAY_IN_MILLIS
}

fun timestampFrom(dateMillis: Long, timeOfDayMillis: Long): Timestamp {
    return Timestamp((dateMillis + timeOfDayMillis) / 1000, 0)
}

fun timestampFromNow(seconds: Long = 0): Timestamp {
    val currentDate = System.currentTimeMillis()
    val futureDate = currentDate + seconds * 1000
    return Timestamp(futureDate / 1000, 0)
}

fun Long.toDurationInSeconds(): Duration {
    return this.toDuration(DurationUnit.SECONDS)
}

fun Duration.toLongFromSeconds(): Long {
    return this.toLong(DurationUnit.SECONDS)
}