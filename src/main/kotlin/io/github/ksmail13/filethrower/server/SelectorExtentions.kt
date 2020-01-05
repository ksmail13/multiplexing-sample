package io.github.ksmail13.filethrower.server

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.channels.*

private val logger: Logger = LoggerFactory.getLogger(Selector::class.java)

fun Selector.register(serverSocketChannel: SelectableChannel, option: Int) {
    serverSocketChannel.register(this, option)
}

fun Selector.registerAccept(serverSocketChannel: ServerSocketChannel) {
    this.register(serverSocketChannel, SelectionKey.OP_ACCEPT)
    logger.debug("accept socket")
}

fun Selector.registerRead(socketChannel: SelectableChannel) {
    socketChannel.configureBlocking(false)
    this.register(socketChannel, SelectionKey.OP_READ)
    if (socketChannel is SocketChannel) {
        logger.debug("register read socket({})", socketChannel.remoteAddress)
    }
}

fun Selector.select(wait: Long, handler: (SelectionKey) -> Unit) {
    this.select(wait)

    val selectedKeys = this.selectedKeys()

    val iterator = selectedKeys.iterator()
    while(iterator.hasNext()) {
        handler(iterator.next())
        iterator.remove()
    }
}
