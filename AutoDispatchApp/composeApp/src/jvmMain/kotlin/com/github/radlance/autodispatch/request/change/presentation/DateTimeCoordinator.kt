package com.github.radlance.autodispatch.request.change.presentation

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateTimeCoordinator {

    private fun now(): LocalDateTime = LocalDateTime.now()

    fun parse(raw: String): LocalDateTime? {
        if (raw.isBlank()) return null

        return runCatching { OffsetDateTime.parse(raw).toLocalDateTime() }.getOrNull()
            ?: runCatching { LocalDateTime.parse(raw) }.getOrNull()
    }

    fun toServer(value: LocalDateTime): String {
        val offset = ZoneId.systemDefault().rules.getOffset(value)
        return OffsetDateTime.of(value, offset)
            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }

    fun updateLoad(
        currentUnload: String,
        newLoad: LocalDateTime,
        isEditing: Boolean = false
    ): Pair<String, String> {

        val unload = parse(currentUnload)

        val adjustedLoad = when {
            !isEditing && newLoad < now() -> now()
            unload != null && newLoad > unload -> unload
            else -> newLoad
        }

        val adjustedUnload = unload?.let {
            if (it < adjustedLoad) adjustedLoad else it
        }

        return toServer(adjustedLoad) to
                (adjustedUnload?.let { toServer(it) } ?: currentUnload)
    }

    fun updateUnload(
        currentLoad: String,
        newUnload: LocalDateTime,
        isEditing: Boolean = false
    ): Pair<String, String> {

        val load = parse(currentLoad)

        val min = if (isEditing) load else load ?: now()

        val adjustedUnload = if (min != null && newUnload < min) min else newUnload

        val adjustedLoad = load?.let {
            if (it > adjustedUnload) adjustedUnload else it
        } ?: now().let {
            if (!isEditing && it > adjustedUnload) adjustedUnload else it
        }

        return toServer(adjustedLoad) to
                toServer(adjustedUnload)
    }

    fun isValid(load: String, unload: String, isEditing: Boolean = false): Boolean {
        val l = parse(load) ?: return false
        val u = parse(unload) ?: return false
        val now = now()

        return if (isEditing) {
            l <= u
        } else {
            l in now..u
        }
    }
}