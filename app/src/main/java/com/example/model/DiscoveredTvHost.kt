package com.example.model

data class DiscoveredTvHost(
  val ipAddress: String,
  val port: Int = 6466,
  val hostName: String = "",
  val brandHint: String = "Android TV",
  val latencyMs: Long = 0,
  val isReachable: Boolean = true,
  val discoveryMethod: String = "Port Probe (6466)"
)
