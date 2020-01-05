package io.github.ksmail13.filethrower.server

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.net.StandardSocketOptions
import java.nio.ByteBuffer
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel

class Server(private val option: ServerOption) : Runnable {
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
                        null -> selected.attach(ConnectionContext.init(socketChannel))
                        ConnectionContext.isEos(attachment) -> {
                            logger.debug("close {}", socketChannel.remoteAddress)
                            socketChannel.close()
                        }
                        is LengthContext -> {
                            if (attachment.complete()) {
                                selected.attach(attachment.toDataContext())
                            } else {
                                attachment.read(socketChannel)
                            }
                        }
                        else -> {
                            val dataContext = attachment as DataContext
                            val context = dataContext.read(socketChannel)
                            if (context.complete()) {
                                val string = String(context.data().array())
                                logger.info("receive : {}", string)
                                socketChannel.write(ByteBuffer.wrap(string.toByteArray()))
                            } else {
                                selected.attach(context)
                            }
                        }
                    }
                }
            }
        }

        logger.info("server down")
        selector.keys().forEach { it.channel().close() }
    }

    fun stop() {
        isAlive = false
    }
}