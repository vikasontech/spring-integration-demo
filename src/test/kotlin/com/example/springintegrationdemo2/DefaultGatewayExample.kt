package com.example.springintegrationdemo2

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.*
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.test.context.SpringIntegrationTest
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component


@SpringIntegrationTest
@SpringBootTest
class DefaultGatewayExampleTest {

    @Autowired
    lateinit var cafeGateway: CafeGateway

    @org.junit.jupiter.api.Test
    internal fun sample() {
        cafeGateway.coffeeSupplier(10)
    }

}

@MessagingGateway
interface CafeGateway {
    @Gateway(requestChannel = "order",
            replyTimeout = 1,
            requestTimeout = 1)
    fun coffeeSupplier(@Payload quantity: Int): Coffee
}

data class Coffee(
        val quantity: Int
)

@Configuration
class GatewayConfig {

    @Bean
    fun order(): DirectChannel {
        return DirectChannel()
    }

    @Bean
    fun supplier(): DirectChannel {
        return DirectChannel()
    }

    @Bean
    fun anotherSupplier(): DirectChannel {
        return DirectChannel()
    }
}

@Component
class MessageProcessor(
        @Qualifier("order")
        val order: DirectChannel,
        @Qualifier("supplier")
        val supplier: DirectChannel
) {
    @ServiceActivator(inputChannel = "order", outputChannel = "supplier")
fun sendMessageToGateway(quantity: Int): Coffee {
    log.info("Order received for the quantity: $quantity")
    log.info("Coffees sent for the request")
    return Coffee(quantity = quantity)
}

@ServiceActivator(inputChannel = "supplier")
fun receivedChannel(@Payload coffee: Coffee): Unit {
    log.info("Got the Coffees from the supplier: $coffee")
}

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}

