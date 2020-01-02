package io.github.ksmail13.filethrower

import io.github.ksmail13.filethrower.server.Server
import io.github.ksmail13.filethrower.server.ServerOption

fun main() {
    Server(ServerOption(timeout = 3000)).start()
}