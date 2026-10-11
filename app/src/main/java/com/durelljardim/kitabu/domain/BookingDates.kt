package com.durelljardim.kitabu.domain

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

fun LocalDate.toEpochMillis(): Long =
    this.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()

fun daysUntil(deadlineMillis: Long): Long =
    ChronoUnit.DAYS.between(LocalDate.now(), deadlineMillis.toLocalDate())
