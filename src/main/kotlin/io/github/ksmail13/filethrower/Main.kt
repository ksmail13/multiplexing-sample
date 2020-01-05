package io.github.ksmail13.filethrower

import io.github.ksmail13.filethrower.server.Server
import io.github.ksmail13.filethrower.server.ServerOption

fun main(args: Array<String>) {
    Server(ServerOption(timeout = 3000)).run()
}