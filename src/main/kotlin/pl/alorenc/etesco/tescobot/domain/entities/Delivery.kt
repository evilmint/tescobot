package pl.alorenc.etesco.tescobot.domain.entities

data class Delivery(val status: Status, val start: String, val end: String) {
    enum class Status {
        AVAILABLE,
        UNAVAILABLE;
    }
}
