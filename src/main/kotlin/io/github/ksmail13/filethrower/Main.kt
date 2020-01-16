package io.github.ksmail13.filethrower

import io.github.ksmail13.filethrower.context.FileThrowerContextFactory
import io.github.ksmail13.nio.server.RxServer
import io.github.ksmail13.nio.server.ServerOption
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer

fun main() {
    val logger = LoggerFactory.getLogger("main")

    RxServer(ServerOption(timeout = 3000), FileThrowerContextFactory())
        .runRx()
        .subscribe { (msg, channel) ->
            val data = String(msg.array())
            logger.info("receive {}", data)
            channel.write(ByteBuffer.wrap(data.toByteArray()))
        }
}