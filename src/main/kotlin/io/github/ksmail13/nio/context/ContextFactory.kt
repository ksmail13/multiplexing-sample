package io.github.ksmail13.nio.context

import java.nio.channels.SocketChannel

/**
 * Socket connection context factory
 */
interface ContextFactory<T> {

    /**
     * create context
     * @param socketChannel channel that receive data
     * @return new context
     */
    fun createContext(socketChannel: SocketChannel): ConnectionContext<T>

}
