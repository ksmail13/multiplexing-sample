package io.github.ksmail13.nio.server

import java.time.LocalDateTime

class ServerShutdown : IllegalStateException("Server shutdown") {
    private val time : LocalDateTime = LocalDateTime.now()

    fun time(): LocalDateTime = time
}
