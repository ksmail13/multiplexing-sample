package io.github.ksmail13.filethrower

import io.github.ksmail13.filethrower.context.FileThrowerContextFactory
import io.github.ksmail13.nio.context.MessageHandler
import io.github.ksmail13.nio.server.Server
import io.github.ksmail13.nio.server.ServerOption
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer

fun main(args: Array<String>) {
    var logger = LoggerFactory.getLogger("main")
    val messageHandler = MessageHandler()
    messageHandler.toFlowable()
        .subscribe { p ->
            val (msg, channel) = p
            val data = String(msg.array())
            logger.info("receive {}", data)
            channel.write(ByteBuffer.wrap(data.toByteArray()))
        }
    Server(ServerOption(timeout = 3000), FileThrowerContextFactory(), messageHandler).run()
}