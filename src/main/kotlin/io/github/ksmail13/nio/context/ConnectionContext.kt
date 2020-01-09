package io.github.ksmail13.nio.context

import java.nio.channels.SocketChannel

interface ConnectionContext<T> {
    companion object {
        fun isEos(context: Any) : Boolean {
            return context is ConnectionContext<*> && (context.lastReadByte() == -1 || context.lastReadByte() == 0)
        }
    }

    fun read(socketChannel: SocketChannel): ConnectionContext<*>
    fun complete(): Boolean
    fun data(): T
    fun next() : ConnectionContext<*>
    fun lastReadByte(): Int
    fun throwIfUnComplete() {
        if (!complete()) {
            throw IllegalStateException("Not complete")
        }
    }
}

