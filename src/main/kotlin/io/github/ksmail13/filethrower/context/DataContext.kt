package io.github.ksmail13.filethrower.context

import io.github.ksmail13.nio.context.ConnectionContext
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

/**
 * 전체 데이터를 읽는 context
 */
data class DataContext(
    val size: Int,
    val byteBuffer: ByteBuffer = ByteBuffer.allocate(size),
    val already: Int = 0,
    val lastReadByte: Int = already
) : ConnectionContext<ByteBuffer> {

    override fun read(socketChannel: SocketChannel): ConnectionContext<ByteBuffer> {
        val read = socketChannel.read(byteBuffer)
        return DataContext(size, byteBuffer, already + read, read)
    }

    override fun complete(): Boolean {
        return already == size
    }

    override fun data(): ByteBuffer {
        throwIfUnComplete()
        byteBuffer.flip()
        return byteBuffer
    }

    override fun lastReadByte(): Int {
        return lastReadByte
    }

    override fun next(): ConnectionContext<*> {
        return this
    }

}