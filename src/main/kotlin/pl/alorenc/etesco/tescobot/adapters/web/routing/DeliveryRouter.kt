package pl.alorenc.etesco.tescobot.adapters.web.routing

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.servlet.function.router

@Configuration
class DeliveryRouter {
    @Bean
    fun deliveryRoute(deliveryRouteHandler: DeliveryRouteHandler) = router {
        ("/deliveries" and accept(APPLICATION_JSON)).nest {
            GET("/slots", deliveryRouteHandler::handleSlots)
        }
    }
}
