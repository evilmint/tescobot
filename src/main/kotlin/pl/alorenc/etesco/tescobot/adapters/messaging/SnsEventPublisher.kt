package pl.alorenc.etesco.tescobot.adapters.messaging

import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.AmazonSNSAsync
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder
import com.amazonaws.services.sns.AmazonSNSClientBuilder
import com.amazonaws.services.sns.model.PublishRequest
import com.fasterxml.jackson.databind.ObjectMapper
import pl.alorenc.etesco.tescobot.domain.ports.Event
import pl.alorenc.etesco.tescobot.domain.ports.EventPublisher

class SnsEventPublisher(
    private val publishTopicArn: String,
    private val serviceEndpoint: String,
    private val region: String,
    private val serializeEventBody: (Any) -> String
) : EventPublisher {
    private lateinit var snsClient: AmazonSNS

    init {
        if (publishTopicArn.isNotBlank() && serviceEndpoint.isNotBlank() && region.isNotBlank()) {
            snsClient = makeSnsClient()
        }
    }

    override fun publish(event: Event) {
        if (!::snsClient.isInitialized) {
            return
        }

        val publishRequest = PublishRequest(publishTopicArn, serializeEventBody(event.body))
        snsClient.publish(publishRequest)
    }

    private fun makeSnsClient(): AmazonSNS {
        val clientBuilder = AmazonSNSClientBuilder.standard()
        clientBuilder.setEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(serviceEndpoint, region))
        return clientBuilder.build()
    }
}
