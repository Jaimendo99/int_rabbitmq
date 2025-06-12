package com.mendoza

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.ConnectionFactory
import io.github.viartemev.rabbitmq.channel.confirmChannel
import io.github.viartemev.rabbitmq.channel.publish
import io.github.viartemev.rabbitmq.publisher.OutboundMessage
import io.github.viartemev.rabbitmq.queue.QueueSpecification
import io.github.viartemev.rabbitmq.queue.declareQueue
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    // 1. Get the AMQP connection URL from environment variables
    val amqpUrl = System.getenv("AMQP_URL")
    if (amqpUrl.isNullOrBlank()) {
        println("Error: AMQP_URL environment variable not set or is empty.")
        println("Please set it before running the application.")
        exitProcess(1)
    }

    val queueName = "integracion"
    val connectionFactory = ConnectionFactory()
    connectionFactory.setUri(amqpUrl)
    connectionFactory.virtualHost = "/" // Default virtual host

    println("Attempting to connect to RabbitMQ...")

    try {
        connectionFactory.newConnection().use { connection ->
            connection.confirmChannel {
                declareQueue(QueueSpecification(name = queueName, durable = true))
                println("[✔] Successfully connected! Starting producer loop...")

                var messageCount = 1
                while (true) {
                    val timestamp =
                        LocalDateTime.now()
                            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    val body =
                        "Message #$messageCount from Kotlin producer at $timestamp"

                    val properties = AMQP.BasicProperties.Builder()
                        .deliveryMode(2) // 2 makes the message persistent
                        .contentType("text/plain")
                        .build()

                    val message = OutboundMessage(
                        exchange = "",
                        routingKey = queueName,
                        properties = properties,
                        msg = body.toByteArray()
                    )

                    publish {
                        val acked = publishWithConfirm(message)
                        if (acked) {
                            println(" [✔] Sent: '$body'")
                        } else {
                            println(" [x] Message was NACKed by broker. Not sent.")
                        }
                    }

                    messageCount++
                    delay(5000L)
                }
            }
        }
    } catch (e: Exception) {
        println("\n[!] An error occurred: ${e.message}")
        println("Please check your connection URL and ensure RabbitMQ is running.")
        e.printStackTrace()
    }
}