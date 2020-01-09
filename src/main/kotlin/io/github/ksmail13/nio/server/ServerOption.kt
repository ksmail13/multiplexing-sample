package io.github.ksmail13.nio.server

data class ServerOption(
    val port: Int = 8000,
    val host: String = "0.0.0.0",
    val timeout: Int)