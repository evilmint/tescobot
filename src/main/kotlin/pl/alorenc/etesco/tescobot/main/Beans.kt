package pl.alorenc.etesco.tescobot.main

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.support.beans
import org.springframework.web.client.RestTemplate
import pl.alorenc.etesco.tescobot.adapters.http.TescoHttpClient
import pl.alorenc.etesco.tescobot.adapters.messaging.SnsEventPublisher
import pl.alorenc.etesco.tescobot.domain.ports.EventPublisher
import pl.alorenc.etesco.tescobot.domain.ports.TescoClient

fun beans() = beans {
    bean<RestTemplate> { RestTemplateBuilder().build() }

    bean<TescoClient> {
        val properties: TescobotProperties = ref()
        TescoHttpClient(properties.email, properties.password, ref(), properties.serviceUrl)
    }

    bean<EventPublisher> {
        val properties: TescobotProperties = ref()
        SnsEventPublisher(
            properties.snsClient.publishTopicArn,
            properties.snsClient.serviceEndpoint,
            properties.snsClient.region,
            eventBodySerializer()
        )
    }
}

private fun eventBodySerializer(): (body: Any) -> String = { eventBody -> ObjectMapper().writeValueAsString(eventBody) }

