package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testDiscoveredTvHostModel() {
    val host = com.example.model.DiscoveredTvHost(
      ipAddress = "192.168.1.102",
      port = 6466,
      hostName = "living-room-tv.local",
      brandHint = "Google TV",
      latencyMs = 15
    )
    assertEquals("192.168.1.102", host.ipAddress)
    assertEquals(6466, host.port)
    assertTrue(host.isReachable)
  }

  @Test
  fun testSubnetRangeModel() {
    val subnet = com.example.service.NetworkTvScanner.SubnetRange(
      baseIp = "192.168.1",
      startHost = 1,
      endHost = 254,
      localIp = "192.168.1.45"
    )
    assertEquals("192.168.1", subnet.baseIp)
    assertEquals(254, subnet.endHost)
  }
}
