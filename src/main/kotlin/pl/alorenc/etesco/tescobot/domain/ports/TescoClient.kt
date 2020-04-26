package pl.alorenc.etesco.tescobot.domain.ports

import pl.alorenc.etesco.tescobot.domain.entities.Delivery

interface TescoClient {
    fun getAllDeliveries(): List<Delivery>
}
