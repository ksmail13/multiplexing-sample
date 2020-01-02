package io.github.ksmail13.filethrower.server

import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

interface ConnectionContext<T> {
    companion object {
        fun init(socket: SocketChannel): ConnectionContext<Int> {
            val allocate = ByteBuffer.allocate(4)
            val readed = socket.read(allocate)
            return LengthContext(allocate, readed)
        }
    }

    fun read(socketChannel: SocketChannel): ConnectionContext<T>
    fun complete(): Boolean
    fun data(): T
    fun throwIfUnComplete() {
        if (!complete()) {
            throw IllegalStateException("Not complete")
        }
    }
}

