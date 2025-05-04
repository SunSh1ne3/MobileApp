package com.savelyev.MobileApp.Utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class TimeManager {
    private val kaliningradZoneId: ZoneId = ZoneId.of("Europe/Kaliningrad")
    private val fullFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy HH:mm", Locale.getDefault())
        .withZone(kaliningradZoneId)
    private val withoutYearFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM HH:mm", Locale.getDefault())
        .withZone(kaliningradZoneId)

    fun getZoneId(): ZoneId = kaliningradZoneId

    fun getZoneTime(): OffsetDateTime {
        return OffsetDateTime.now(kaliningradZoneId)
    }

    fun getFormatZoneTime(): String {
        return fullFormatter.format(OffsetDateTime.now(kaliningradZoneId))
    }

    fun getFullFormatZoneTime(time: OffsetDateTime): String {
        return fullFormatter.format(time)
    }

    fun getWithoutYearFormatZoneTime(time: OffsetDateTime): String {
        val currentYear = OffsetDateTime.now().year
        val pattern = if (time.year == currentYear) {
            withoutYearFormatter
        } else {
            fullFormatter
        }

        return pattern.format(time)
    }

    fun fromEpochMilli(millis: Long): OffsetDateTime {
        return Instant.ofEpochMilli(millis).atZone(kaliningradZoneId).toOffsetDateTime()
    }

    fun toEpochMilli(dateTime: OffsetDateTime): Long {
        return dateTime.toInstant().toEpochMilli()
    }



}