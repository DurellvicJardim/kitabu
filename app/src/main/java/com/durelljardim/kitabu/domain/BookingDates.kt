package com.durelljardim.kitabu.domain

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

// The database stores dates as the millisecond value at the start of that day.
fun LocalDate.toEpochMillis(): Long =
    this.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()

// Whole days between today and the deadline. Negative when the deadline has passed.
fun daysUntil(deadlineMillis: Long): Long =
    ChronoUnit.DAYS.between(LocalDate.now(), deadlineMillis.toLocalDate())
