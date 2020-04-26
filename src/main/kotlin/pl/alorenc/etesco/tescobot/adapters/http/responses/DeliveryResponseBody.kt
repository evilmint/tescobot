package pl.alorenc.etesco.tescobot.adapters.http.responses

data class DeliveryResponseBody(val resources: Resources) {
    data class Resources(val slot: Slot)
    data class Slot(val data: Data)
    data class Data(val delivery: List<Delivery>)
    data class Delivery(val start: String, val end: String, val status: String)

    val deliveries get() = resources.slot.data.delivery
}