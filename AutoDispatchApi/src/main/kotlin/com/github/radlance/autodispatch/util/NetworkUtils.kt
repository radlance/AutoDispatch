package com.github.radlance.autodispatch.util

import java.net.NetworkInterface

val localIpAddress: String = NetworkInterface.getNetworkInterfaces().asSequence()
    .filter { it.isUp && !it.isLoopback }
    .flatMap { it.inetAddresses.asSequence() }
    .filter { it.address.size == 4 }
    .map { it.hostAddress }
    .firstOrNull { it.startsWith("192.168.") || it.startsWith("10.") }
    ?: "localhost"
