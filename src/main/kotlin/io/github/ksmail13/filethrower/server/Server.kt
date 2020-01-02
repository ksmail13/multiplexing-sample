package io.github.ksmail13.filethrower.server

import java.net.InetSocketAddress
import java.net.StandardSocketOptions
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.util.logging.Logger

class Server(private val option: ServerOption) {
    private val logger: Logger = Logger.getLogger("io.github.ksmail13.Server")
    private val selector: Selector = Selector.open()

    init {
        logger.level = option.logLevel
        logger.config("Init server with $option")
    }

    private fun initServerSocket(): ServerSocketChannel {
        val open = ServerSocketChannel.open()
        open.setOption(StandardSocketOptions.SO_REUSEADDR, false)
        open.configureBlocking(false)
        open.bind(InetSocketAddress(option.host, option.port))
        return open
    }

    fun start() {
        val serverSocketChannel = initServerSocket()
        selector.registerAccept(serverSocketChannel)
        logger.fine { "init Server" }

        while (true) {
            val select = selector.select(option.timeout.toLong())
            logger.fine { "select $select sockets" }

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
                    val attachment = selected.attachment()
                    if (attachment == null) {
                        selected.attach(ConnectionContext.init(socketChannel))
                    } else if (attachment is LengthContext) {
                        if (attachment.complete()) {
                            selected.attach(attachment.toDataContext())
                        } else {
                            attachment.read(socketChannel)
                        }
                    } else {
                        val dataContext = attachment as DataContext
                        val context = dataContext.read(socketChannel)
                        if (context.complete()) {
                            logger.info { "receive : ${String(context.data().asCharBuffer().array())}" }
                        } else {
                            selected.attach(context)
                        }
                    }
                }
            }
        }
    }
}