package pl.alorenc.etesco.tescobot.main

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Component
@Configuration
@ConfigurationProperties(prefix = "tescobot")
data class TescobotProperties(
    var email: String = "",
    var password: String = "",
    var serviceUrl: String = "",
    var snsClient: SnsClientProperties = SnsClientProperties("", "", "")
)

data class SnsClientProperties(
    var serviceEndpoint: String,
    var region: String,
    var publishTopicArn: String
)
