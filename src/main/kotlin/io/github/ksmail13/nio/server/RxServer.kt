package io.github.ksmail13.nio.server

import io.github.ksmail13.nio.context.ContextFactory
import io.github.ksmail13.nio.context.MessageHandler
import io.reactivex.Flowable
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

class RxServer(
    option: ServerOption,
    contextFactory: ContextFactory<*>,
    private val handler: MessageHandler = MessageHandler()
) : Server(option, contextFactory, handler) {

    fun runRx(): Flowable<Pair<ByteBuffer, SocketChannel>> {
        runOnBackground()
        return handler.toFlowable()
    }

    override fun stop() {
        super.stop()
        handler.processor.onComplete()
    }

}