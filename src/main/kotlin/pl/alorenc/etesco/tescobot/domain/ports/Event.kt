package pl.alorenc.etesco.tescobot.domain.ports

data class Event(val body: Any)

interface EventPublisher {
    fun publish(event: Event)
}
