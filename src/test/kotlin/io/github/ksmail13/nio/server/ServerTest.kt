package io.github.ksmail13.nio.server

import io.github.ksmail13.filethrower.context.FileThrowerContext
import io.github.ksmail13.filethrower.context.FileThrowerContextFactory
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

internal class ServerTest {

    companion object {
        private var target: RxServer? = null
        private val logger: Logger = LoggerFactory.getLogger(ServerTest::class.java)

        @BeforeAll
        @JvmStatic
        fun init() {
            target = RxServer(ServerOption(timeout = 500), FileThrowerContextFactory())

            target?.runRx()?.subscribe({
                (msgBuf, channel) ->
                val msg = String(msgBuf.array())
                logger.info("receive {}", msg)
                channel.write(ByteBuffer.wrap(msg.toByteArray()))
            }, { t -> logger.error("error", t) })
            Thread.sleep(1000)
        }

        @AfterAll
        @JvmStatic
        fun finish() {
            Thread.sleep(1000)
            target?.stop()
        }
    }

    @Test
    fun readSimpleData() {
        val open = SocketChannel.open(InetSocketAddress("127.0.0.1", 8000))
        var data = ByteBuffer.allocate(10)
        data = data.putInt(2)
        logger.debug("remain {}", data.remaining())
        data = data.putChar('a')
        logger.debug("remain {}", data.remaining())
        data.flip()
        val write = open.write(data)
        data.clear()
        data.putInt(0).putInt(0).clear()
        open.read(data)
        data.flip()
        val response = StringBuilder(data.asCharBuffer()).toString()
        open.close()
        assertEquals(6, write)
        assertEquals("a", response)
    }
}