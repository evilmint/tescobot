package pl.alorenc.etesco.tescobot.adapters.web.routing

import org.springframework.stereotype.Component
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse.ok
import pl.alorenc.etesco.tescobot.adapters.web.responses.AvailableShoppingSlotsResponseBody
import pl.alorenc.etesco.tescobot.domain.usecases.AvailableShoppingSlotsResult
import pl.alorenc.etesco.tescobot.domain.usecases.CheckAvailableShoppingSlotsUseCase
import java.time.format.DateTimeFormatter

@Component
class DeliveryRouteHandler(
    private val checkAvailableShoppingSlotsUseCase: CheckAvailableShoppingSlotsUseCase
) {
    fun handleSlots(request: ServerRequest) =
        ok().body(checkAvailableShoppingSlotsUseCase.listAvailableShoppingSlots().toResponseBody())

    private fun AvailableShoppingSlotsResult.toResponseBody() =
        AvailableShoppingSlotsResponseBody(
            slots = slots.map {
                AvailableShoppingSlotsResponseBody.Slot(
                    startDate = it.startDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                    endDate = it.startDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                )
            }
        )
}