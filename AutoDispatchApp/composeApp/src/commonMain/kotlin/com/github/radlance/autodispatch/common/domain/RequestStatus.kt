package com.github.radlance.autodispatch.common.domain

enum class RequestStatus(
    val id: Int,
    val title: String
) {
    Waiting(1, "Ожидает"),
    Assigned(2, "Назначена"),
    InProgress(3, "В пути"),
    Completed(4, "Завершена"),
    Canceled(5, "Отменена"),
    OnCheck(6, "На проверке"),
    Rejected(7, "Отклонена");

    companion object {
        fun fromId(id: Int?): RequestStatus? =
            entries.firstOrNull { it.id == id }
    }
}

fun Int.toRequestStatus(): RequestStatus =
    RequestStatus.fromId(this)
        ?: error("Unknown request status id=$this")

