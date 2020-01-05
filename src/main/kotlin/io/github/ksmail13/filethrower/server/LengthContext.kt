package io.github.ksmail13.filethrower.server

import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

/**
 * 패킷 길이를 확인하는 context
 */
data class LengthContext(val byteBuffer: ByteBuffer, val readBytes: Int, val lastRead: Int = readBytes) :
    ConnectionContext<Int> {
    companion object {
        const val SIZE = 4
    }

    override fun data(): Int {
        throwIfUnComplete()
        byteBuffer.flip()
        return byteBuffer.getInt(0)
    }

    override fun read(socketChannel: SocketChannel): ConnectionContext<Int> {
        val remain = SIZE - readBytes
        val buffer = ByteBuffer.allocate(remain)
        val read = socketChannel.read(buffer)

        return LengthContext(byteBuffer.put(buffer), read + this.readBytes, read)
    }

    override fun complete(): Boolean {
        return readBytes == SIZE
    }

    fun toDataContext(): DataContext {
        return DataContext(data())
    }

    override fun lastReadByte(): Int {
        return lastRead
    }

}