package com.example.springintegrationdemo2

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.integration.annotation.Poller
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.integration.channel.RendezvousChannel
import org.springframework.integration.test.context.SpringIntegrationTest
import org.springframework.messaging.Message
import org.springframework.messaging.support.GenericMessage
import org.springframework.stereotype.Component
import java.time.Duration

@SpringIntegrationTest
@SpringBootTest
class RendezvousChannelTest {

    @Autowired
    lateinit var messageSender: MessageSenderForRendezvousChannel

    @Test
    internal fun sampleTest() {
        messageSender.sendMessage("hello")
        Thread.sleep(Duration.ofSeconds(20).toMillis())
    }
}


@Component
class MessageSenderForRendezvousChannel(
        @Qualifier("myChannelRendezvousChannel")
        val channel: RendezvousChannel
) {

    fun sendMessage(message: String): Unit {
        log.info("sending message ...")
        channel.send( GenericMessage(message) )
        log.info("message sent.")

    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)

    }

}

@Component
class MyConfigMessageSenderForRendezvousChannel {

    @Bean
    fun myChannelRendezvousChannel(): RendezvousChannel{
       return RendezvousChannel()
    }
}

@Component
class MyMessageConsumerRendezvousChannel {


    @ServiceActivator(inputChannel = "myChannelRendezvousChannel", poller = [Poller(fixedDelay = "12")])
    fun consumeMyMessage(message: Message<String>): Unit {
        log.info("got the message processing it...")
        Thread.sleep(Duration.ofSeconds(15).toMillis())
        log.info("message processed.")
        println("The message Received: ${message.payload}")
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}