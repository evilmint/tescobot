package pl.alorenc.etesco.tescobot.domain.services

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import pl.alorenc.etesco.tescobot.domain.entities.Delivery
import pl.alorenc.etesco.tescobot.domain.ports.Event
import pl.alorenc.etesco.tescobot.domain.ports.EventPublisher
import pl.alorenc.etesco.tescobot.domain.ports.TescoClient
import pl.alorenc.etesco.tescobot.domain.usecases.AvailableShoppingSlotsResult
import pl.alorenc.etesco.tescobot.domain.usecases.AvailableShoppingSlotsResult.Slot
import pl.alorenc.etesco.tescobot.domain.usecases.CheckAvailableShoppingSlotsUseCase
import java.time.OffsetDateTime

@Component
class TescoService(
    private val tescoClient: TescoClient,
    private val eventPublisher: EventPublisher
) : CheckAvailableShoppingSlotsUseCase {
    override fun listAvailableShoppingSlots(): AvailableShoppingSlotsResult {
        val deliveries = getAvailableDeliveries()

        eventPublisher.publish(Event(body = deliveries))

        return AvailableShoppingSlotsResult(slots = deliveries.map { it.toSlot() })
    }

    private fun getAvailableDeliveries() = tescoClient.getAllDeliveries()
        .filter { it.status != Delivery.Status.UNAVAILABLE }

    private fun Delivery.toSlot() =
        Slot(startDate = OffsetDateTime.parse(start), endDate = OffsetDateTime.parse(end))
}
