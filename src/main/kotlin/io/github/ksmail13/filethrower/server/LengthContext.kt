package io.github.ksmail13.filethrower.server

import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

/**
 * 패킷 길이를 확인하는 context
 */
data class LengthContext(val byteBuffer: ByteBuffer, val readed: Int) :
    ConnectionContext<Int> {
    companion object {
        const val SIZE = 4
    }

    override fun data(): Int {
        throwIfUnComplete()
        return byteBuffer.int
    }

    override fun read(socketChannel: SocketChannel): ConnectionContext<Int> {
        val remain = SIZE - readed
        val buffer = ByteBuffer.allocate(remain)
        val read = socketChannel.read(buffer)

        return LengthContext(
            byteBuffer.put(buffer),
            read + this.readed
        )
    }

    override fun complete(): Boolean {
        return readed == SIZE
    }

    fun toDataContext(): DataContext {
        return DataContext(data())
    }

}