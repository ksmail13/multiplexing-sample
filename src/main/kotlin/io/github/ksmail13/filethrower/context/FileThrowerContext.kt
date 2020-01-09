package io.github.ksmail13.filethrower.context

import io.github.ksmail13.nio.context.ConnectionContext
import io.github.ksmail13.nio.context.ContextFactory
import java.lang.IllegalStateException
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

class FileThrowerContext(private val context:ConnectionContext<*>) : ConnectionContext<ByteBuffer> {

    override fun read(socketChannel: SocketChannel): ConnectionContext<*> {
        if (context is LengthContext && context.complete()) {
            return FileThrowerContext(context.next().read(socketChannel))
        }
        return FileThrowerContext(context.read(socketChannel))
    }

    override fun complete(): Boolean {
        return context is DataContext && context.complete()
    }

    override fun data(): ByteBuffer {
        context.throwIfUnComplete()
        return when(context) {
            is DataContext -> context.data()
            else -> throw IllegalStateException()
        }
    }

    override fun next(): ConnectionContext<*> {
        return context.next()
    }

    override fun lastReadByte(): Int {
        return context.lastReadByte()
    }

}