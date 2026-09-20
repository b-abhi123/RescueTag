package com.example

import com.example.data.RescueTagRepository
import com.example.i18n.AppLanguage
import com.example.i18n.Strings
import com.example.model.HouseholdProfile
import com.example.model.PayloadType
import com.example.model.PriorityLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testBroadcastDistress() {
    val repo = RescueTagRepository()
    val initialSignalsCount = repo.state.value.distressSignals.size

    repo.broadcastDistress(PayloadType.SOS_CRITICAL)

    val updatedState = repo.state.value
    assertNotNull(updatedState.activeMyDistress)
    assertEquals(PayloadType.SOS_CRITICAL, updatedState.activeMyDistress?.payloadType)
    assertEquals(initialSignalsCount + 1, updatedState.distressSignals.size)
  }

  @Test
  fun testClusterEscalationPriority() {
    val repo = RescueTagRepository()
    val targetSector = "SEC-BETA"

    repo.simulateClusterEscalation(targetSector)

    val updatedState = repo.state.value
    val beta = updatedState.sectors.find { it.id == targetSector }
    assertNotNull(beta)
    assertTrue("Sector active signal count should be at least 3", beta!!.activeSignalCount >= 3)
    assertEquals(PriorityLevel.CRITICAL, beta.priorityLevel)
    assertNotNull(updatedState.clusterEscalationAlert)
  }

  @Test
  fun testHouseholdRegistration() {
    val repo = RescueTagRepository()
    val newProfile = HouseholdProfile(
      devEui = "RT-TEST-99",
      primaryContactName = "Test Contact",
      phoneNumber = "+1-555-0199",
      sectorId = "SEC-ALPHA",
      totalOccupants = 5,
      elderlyCount = 2,
      infantCount = 1,
      medicalConditions = "Cardiac care",
      secondaryEmergencyPhone = "+1-555-0198"
    )

    repo.registerHousehold(newProfile)

    val stored = repo.state.value.householdProfiles["RT-TEST-99"]
    assertNotNull(stored)
    assertEquals("Test Contact", stored?.primaryContactName)
    assertEquals(5, stored?.totalOccupants)
  }

  @Test
  fun testSyncGatewayCache() {
    val repo = RescueTagRepository()
    val initialIngestCount = repo.state.value.distressSignals.size
    val mobileGwId = "GW-PACK-03"

    repo.syncGatewayCache(mobileGwId)

    val updatedState = repo.state.value
    assertTrue(updatedState.distressSignals.size > initialIngestCount)
    val gw = updatedState.gateways.find { it.id == mobileGwId }
    assertEquals(0, gw?.cachedPacketCount)
  }

  @Test
  fun testLocalizationStrings() {
    for (lang in AppLanguage.values()) {
      val title = Strings.get("app_title", lang)
      assertTrue("Title in ${lang.displayName} should not be empty", title.isNotBlank())
    }
  }
}
