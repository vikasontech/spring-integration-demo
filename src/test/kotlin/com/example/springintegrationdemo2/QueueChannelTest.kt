package com.example.springintegrationdemo2

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.integration.annotation.Poller
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.channel.QueueChannel
import org.springframework.integration.dsl.PollerFactory
import org.springframework.integration.scheduling.PollerMetadata
import org.springframework.integration.test.context.SpringIntegrationTest
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.GenericMessage
import org.springframework.stereotype.Component
import java.time.Duration

@SpringIntegrationTest
@SpringBootTest
class QueueChannelTest {

    @Autowired
    lateinit var messageSender: MessageSender

    @Test
    internal fun sampleTest() {
        messageSender.sendMessage("hello")
        Thread.sleep(Duration.ofSeconds(20).toMillis())
    }
}


@Component
class MessageSender(
        @Qualifier("myQueueChannel")
        val myQueueChannel: QueueChannel
) {

    fun sendMessage(message: String): Unit {
        log.info("sending message ...")
        myQueueChannel.send( GenericMessage(message) )
        log.info("message sent.")

    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)

    }

}

@Component
class MyConfig {

    @Bean
    fun myQueueChannel(): QueueChannel {
       return QueueChannel(10)
    }
}

@Component
class MyMessageConsumer {


    @ServiceActivator(inputChannel = "myQueueChannel", poller = [Poller(fixedRate = "1")])
    fun consumeMyMessage(message: Message<String>): Unit {
        log.info("got the message processing it...")
        Thread.sleep(Duration.ofSeconds(15).toMillis())
        log.info("message processed.")
        println("The message Received: ${message.payload}")
    }

    @ServiceActivator(inputChannel = "myQueueChannel", poller = [Poller(fixedRate = "1")])
    fun consumeMyMessage2(message: Message<String>): Unit {
        log.info("got the message processing it...")
        Thread.sleep(Duration.ofSeconds(15).toMillis())
        log.info("message processed.")
        println("The message Received: ${message.payload}")
    }
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)

    }

}