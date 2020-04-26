package pl.alorenc.etesco.tescobot.adapters.web.responses

data class AvailableShoppingSlotsResponseBody(val slots: List<Slot>) {
    data class Slot(
        val startDate: String,
        val endDate: String
    )
}