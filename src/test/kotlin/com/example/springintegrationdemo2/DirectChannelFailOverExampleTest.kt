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
import org.springframework.integration.channel.RendezvousChannel
import org.springframework.integration.test.context.SpringIntegrationTest
import org.springframework.messaging.Message
import org.springframework.messaging.support.GenericMessage
import org.springframework.stereotype.Component
import java.lang.Exception
import java.time.Duration
import java.util.stream.IntStream

@SpringIntegrationTest
@SpringBootTest
class DirectChannelFailOverExampleTest {

    @Autowired
    lateinit var messageSender: MessageSenderForDirectChannel

    @Test
    internal fun sampleTest() {

        IntStream.range(1, 10)
                .forEach {
                    log.info("hello value: $it")
                    messageSender.sendMessage(it.toString())
                }
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}


@Component
class MessageSenderForDirectChannel(
        @Qualifier("myChannelDirectChannel")
        val channel: DirectChannel
) {

    fun sendMessage(message: String): Unit {
        log.info("sending message ...")
        channel.send(GenericMessage(message))
        log.info("message sent.")

    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)

    }

}

@Component
class MyConfigMessageSenderForDirectChannel {

    @Bean
    fun myChannelDirectChannel(): DirectChannel {
        return DirectChannel()
    }
}

@Component
class MyMessageConsumerDirectChannelChannel {


    @ServiceActivator(inputChannel = "myChannelDirectChannel")
    fun consumeMyMessage(message: Message<String>): Unit {
        log.info("received and consumer# 1: with value: ${message.payload}")
        throw Exception("throw some exception ...")
    }

    @ServiceActivator(inputChannel = "myChannelDirectChannel")
    fun consumeMyMessage2(message: Message<String>): Unit {
        log.info("received and consumer# 2: with value: ${message.payload}")
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}