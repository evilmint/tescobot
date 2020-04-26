package pl.alorenc.etesco.tescobot.domain.usecases

import java.time.OffsetDateTime

data class AvailableShoppingSlotsResult(
    val slots: List<Slot>
) {
    data class Slot(
        val startDate: OffsetDateTime,
        val endDate: OffsetDateTime
    )
}

interface CheckAvailableShoppingSlotsUseCase {
    fun listAvailableShoppingSlots(): AvailableShoppingSlotsResult
}