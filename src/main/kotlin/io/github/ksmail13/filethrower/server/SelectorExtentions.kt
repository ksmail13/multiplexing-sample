package io.github.ksmail13.filethrower.server

import java.nio.channels.SelectableChannel
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel

fun Selector.register(serverSocketChannel: SelectableChannel, option: Int) {
    serverSocketChannel.register(this, option)
}

fun Selector.registerAccept(serverSocketChannel: ServerSocketChannel) {
    this.register(serverSocketChannel, SelectionKey.OP_ACCEPT)
}

fun Selector.registerRead(socketChannel: SelectableChannel) {
    socketChannel.configureBlocking(false)
    this.register(socketChannel, SelectionKey.OP_READ)
}

fun Selector.select(wait: Long, handler: (SelectionKey) -> Unit) {
    this.select(wait)

    val selectedKeys = this.selectedKeys()
    selectedKeys.forEach {
        handler(it)
        selectedKeys.remove(it)
    }
}
