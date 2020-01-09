package io.github.ksmail13.filethrower.context

import io.github.ksmail13.nio.context.ConnectionContext
import io.github.ksmail13.nio.context.ContextFactory
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

class FileThrowerContextFactory : ContextFactory<ByteBuffer> {

    override fun createContext(socketChannel: SocketChannel): ConnectionContext<ByteBuffer> {
        val allocate = ByteBuffer.allocate(LengthContext.SIZE)
        val readed = socketChannel.read(allocate)
        return FileThrowerContext(LengthContext(allocate, readed))
    }

}