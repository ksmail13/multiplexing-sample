package io.github.ksmail13.filethrower.server

import java.util.logging.Level

data class ServerOption(
    val port: Int = 8000,
    val host: String = "0.0.0.0",
    val logLevel: Level = Level.FINE,
    val timeout: Int)