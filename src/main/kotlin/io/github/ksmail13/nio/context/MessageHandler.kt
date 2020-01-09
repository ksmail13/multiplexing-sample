package io.github.ksmail13.nio.context

import io.reactivex.Flowable
import io.reactivex.processors.PublishProcessor
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

class MessageHandler : Function2<ByteBuffer, SocketChannel, Unit> {
    val processor: PublishProcessor<Pair<ByteBuffer, SocketChannel>> = PublishProcessor.create()

    override fun invoke(p1: ByteBuffer, p2: SocketChannel) {
        processor.onNext(p1 to p2)
    }

    fun toFlowable(): Flowable<Pair<ByteBuffer, SocketChannel>> {
        return processor.publish().autoConnect()
    }
}
