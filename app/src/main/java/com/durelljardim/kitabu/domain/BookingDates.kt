package com.durelljardim.kitabu.domain

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

// The database stores dates as the millisecond value at the start of that day.
fun LocalDate.toEpochMillis(): Long =
    this.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
