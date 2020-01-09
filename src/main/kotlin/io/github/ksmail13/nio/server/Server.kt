package io.github.ksmail13.nio.server

import io.github.ksmail13.filethrower.context.FileThrowerContext
import io.github.ksmail13.nio.context.ConnectionContext
import io.github.ksmail13.nio.context.ContextFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.InetSocketAddress
import java.net.StandardSocketOptions
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel

class Server(
    private val option: ServerOption,
    private val contextFactory: ContextFactory<*>,
    private val resultHandler: (ByteBuffer, SocketChannel) -> Unit
) : Runnable {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(Server::class.java)
    }

    private val selector: Selector = Selector.open()
    private var isAlive = true

    init {
        logger.info("Init server with $option")
    }

    private fun initServerSocket(): ServerSocketChannel {
        val open = ServerSocketChannel.open()
        open.setOption(StandardSocketOptions.SO_REUSEADDR, true)
        open.configureBlocking(false)
        open.bind(InetSocketAddress(option.host, option.port))
        return open
    }

    fun runOnBackground() {
        Thread(this, "Server-main").start()
    }

    override fun run() {
        if (!isAlive) {
            throw IllegalStateException("Dead server")
        }

        val serverSocketChannel = initServerSocket()
        selector.registerAccept(serverSocketChannel)
        logger.debug("init Server")

        while (isAlive) {
            val select = selector.select(option.timeout.toLong())
            logger.trace("select {} sockets", select)

            selector.select(option.timeout.toLong()) { selected ->
                try {
                    handleEvent(selected)
                } catch (e: IOException) {
                    logger.debug("connection error", e)
                    selected.channel().close()
                } catch (e: Exception) {
                    logger.debug("Error occurred", e)
                }
            }
        }

        logger.info("server down")
        selector.keys().forEach { it.channel().close() }
    }

    private fun handleEvent(selected: SelectionKey) {
        if (!selected.isValid) {
            selected.channel().close()
        }
        if (selected.isAcceptable) {
            val channel: ServerSocketChannel = selected.channel() as ServerSocketChannel
            selector.registerRead(channel.accept())
        }
        if (selected.isReadable) {
            val socketChannel = selected.channel() as SocketChannel
            when (val attachment = selected.attachment()) {
                null -> selected.attach(contextFactory.createContext(socketChannel))
                is FileThrowerContext -> {
                    if (ConnectionContext.isEos(attachment)) {
                        logger.debug("close {}", socketChannel.remoteAddress)
                        socketChannel.close()
                        return
                    }
                    val read = attachment.read(socketChannel)
                    if (read.complete()) {
                        resultHandler((read as FileThrowerContext).data(), socketChannel)
                        selected.attach(null)
                    } else {
                        selected.attach(read)
                    }
                }
                else -> {
                    throw IllegalStateException("Unknown type ${attachment::class.java.simpleName}")
                }
            }
        }
    }

    fun stop() {
        isAlive = false
    }
}