package com.example.data

import com.example.model.DistressSignal
import com.example.model.GatewayModality
import com.example.model.GatewayNode
import com.example.model.GatewayStatus
import com.example.model.HouseholdProfile
import com.example.model.PayloadType
import com.example.model.PriorityLevel
import com.example.model.SectorArea
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

data class AppState(
    val myRegisteredDevEui: String = "RT-884A",
    val activeMyDistress: DistressSignal? = null,
    val sectors: List<SectorArea> = emptyList(),
    val distressSignals: List<DistressSignal> = emptyList(),
    val householdProfiles: Map<String, HouseholdProfile> = emptyMap(),
    val gateways: List<GatewayNode> = emptyList(),
    val searchModeActive: Boolean = false,
    val searchModeSectorId: String? = null,
    val isDrillInProgress: Boolean = false,
    val lastDrillResult: String? = null,
    val clusterEscalationAlert: String? = null
)

class RescueTagRepository {

    private val _state = MutableStateFlow(createInitialState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    private fun createInitialState(): AppState {
        val initialSectors = listOf(
            SectorArea(
                id = "SEC-ALPHA",
                name = "Sector Alpha (North Ridge)",
                description = "Highland residential settlement & school zone",
                latitude = 27.7172,
                longitude = 85.3240,
                activeSignalCount = 1,
                priorityLevel = PriorityLevel.NORMAL
            ),
            SectorArea(
                id = "SEC-BETA",
                name = "Sector Beta (River Basin)",
                description = "Low-lying agricultural hamlet & market hub",
                latitude = 27.7008,
                longitude = 85.3001,
                activeSignalCount = 2,
                priorityLevel = PriorityLevel.ELEVATED
            ),
            SectorArea(
                id = "SEC-GAMMA",
                name = "Sector Gamma (East Foothills)",
                description = "Steep terrain settlement with bridge connection",
                latitude = 27.6915,
                longitude = 85.3420,
                activeSignalCount = 0,
                priorityLevel = PriorityLevel.NORMAL
            ),
            SectorArea(
                id = "SEC-DELTA",
                name = "Sector Delta (West Access Pass)",
                description = "Access road checkpoint & supply transit corridor",
                latitude = 27.7289,
                longitude = 85.2890,
                activeSignalCount = 0,
                priorityLevel = PriorityLevel.NORMAL
            )
        )

        val initialHouseholds = mapOf(
            "RT-884A" to HouseholdProfile(
                devEui = "RT-884A",
                primaryContactName = "Kalyan Sharma",
                phoneNumber = "+977-9841234567",
                sectorId = "SEC-ALPHA",
                totalOccupants = 5,
                elderlyCount = 2,
                infantCount = 1,
                medicalConditions = "Insulin-dependent elderly grandparent",
                secondaryEmergencyPhone = "+977-9807654321"
            ),
            "RT-912C" to HouseholdProfile(
                devEui = "RT-912C",
                primaryContactName = "Maria Santos",
                phoneNumber = "+977-9812345678",
                sectorId = "SEC-BETA",
                totalOccupants = 4,
                elderlyCount = 0,
                infantCount = 1,
                medicalConditions = "Infant asthma, requires nebulizer",
                secondaryEmergencyPhone = "+977-9849876543"
            ),
            "RT-337F" to HouseholdProfile(
                devEui = "RT-337F",
                primaryContactName = "David Chen",
                phoneNumber = "+977-9823456789",
                sectorId = "SEC-BETA",
                totalOccupants = 3,
                elderlyCount = 1,
                infantCount = 0,
                medicalConditions = "Mobility impaired (wheelchair user)",
                secondaryEmergencyPhone = "+977-9811223344"
            ),
            "RT-402B" to HouseholdProfile(
                devEui = "RT-402B",
                primaryContactName = "Amina Khan",
                phoneNumber = "+977-9834567890",
                sectorId = "SEC-GAMMA",
                totalOccupants = 6,
                elderlyCount = 1,
                infantCount = 2,
                medicalConditions = "None reported",
                secondaryEmergencyPhone = "+977-9866554433"
            ),
            "RT-109X" to HouseholdProfile(
                devEui = "RT-109X",
                primaryContactName = "Community Sub-Health Post",
                phoneNumber = "+977-9855566778",
                sectorId = "SEC-BETA",
                totalOccupants = 12,
                elderlyCount = 3,
                infantCount = 2,
                medicalConditions = "Field triage station; trauma dressing exhausted",
                secondaryEmergencyPhone = "+977-9800112233"
            )
        )

        val now = System.currentTimeMillis()
        val initialSignals = listOf(
            DistressSignal(
                id = "SIG-101",
                devEui = "RT-884A",
                householdName = "Kalyan Sharma (5 occupants)",
                sectorId = "SEC-ALPHA",
                payloadType = PayloadType.ESSENTIAL_SUPPLIES,
                gatewayId = "GW-FIXED-01",
                rssiDbm = -89,
                snrDb = 7.2,
                batteryPct = 88,
                latitude = 27.7175,
                longitude = 85.3242,
                plusCode = "7MV7PJ9F+VW",
                timestamp = now - 180000,
                isStoreAndForward = false
            ),
            DistressSignal(
                id = "SIG-102",
                devEui = "RT-912C",
                householdName = "Maria Santos (4 occupants)",
                sectorId = "SEC-BETA",
                payloadType = PayloadType.MEDICAL_URGENT,
                gatewayId = "GW-PACK-03",
                rssiDbm = -96,
                snrDb = 4.8,
                batteryPct = 94,
                latitude = 27.7011,
                longitude = 85.3005,
                plusCode = "7MV7PJ22+8W",
                timestamp = now - 95000,
                isStoreAndForward = true
            ),
            DistressSignal(
                id = "SIG-103",
                devEui = "RT-337F",
                householdName = "David Chen (3 occupants)",
                sectorId = "SEC-BETA",
                payloadType = PayloadType.SOS_CRITICAL,
                gatewayId = "GW-DRONE-07",
                rssiDbm = -78,
                snrDb = 9.4,
                batteryPct = 91,
                latitude = 27.7003,
                longitude = 85.2998,
                plusCode = "7MV7PJ22+7V",
                timestamp = now - 40000,
                isStoreAndForward = false
            )
        )

        val initialGateways = listOf(
            GatewayNode(
                id = "GW-FIXED-01",
                name = "Sentinel Ridge Tower",
                modality = GatewayModality.FIXED_HIGH_GROUND,
                status = GatewayStatus.ONLINE_STREAMING,
                cachedPacketCount = 0,
                batteryLevel = 98,
                coverageRadiusKm = 15.0
            ),
            GatewayNode(
                id = "GW-PACK-03",
                name = "Foot Patrol Backpack Alpha",
                modality = GatewayModality.MOBILE_BACKPACK,
                status = GatewayStatus.OFFLINE_STORE_AND_FORWARD,
                cachedPacketCount = 14,
                batteryLevel = 84,
                coverageRadiusKm = 4.5
            ),
            GatewayNode(
                id = "GW-DRONE-07",
                name = "SkyWatch Recon Drone",
                modality = GatewayModality.AERIAL_DRONE,
                status = GatewayStatus.ONLINE_STREAMING,
                cachedPacketCount = 0,
                batteryLevel = 62,
                coverageRadiusKm = 8.0
            ),
            GatewayNode(
                id = "GW-BOAT-02",
                name = "Rescue Boat Unit Delta",
                modality = GatewayModality.VEHICLE_BOAT,
                status = GatewayStatus.SYNCING_CACHE,
                cachedPacketCount = 6,
                batteryLevel = 79,
                coverageRadiusKm = 6.0
            )
        )

        return AppState(
            sectors = initialSectors,
            distressSignals = initialSignals,
            householdProfiles = initialHouseholds,
            gateways = initialGateways
        )
    }

    // Trigger an emergency distress signal from the physical tag simulator
    fun broadcastDistress(type: PayloadType, customSectorId: String? = null) {
        val current = _state.value
        val devEui = current.myRegisteredDevEui
        val profile = current.householdProfiles[devEui]
        val sectorId = customSectorId ?: profile?.sectorId ?: "SEC-ALPHA"
        val sector = current.sectors.find { it.id == sectorId }

        val newSignal = DistressSignal(
            id = "SIG-${Random.nextInt(1000, 9999)}",
            devEui = devEui,
            householdName = profile?.primaryContactName ?: "Household $devEui",
            sectorId = sectorId,
            payloadType = type,
            gatewayId = "GW-FIXED-01",
            rssiDbm = -75 - Random.nextInt(0, 20),
            snrDb = 6.0 + Random.nextDouble(0.0, 5.0),
            batteryPct = 90 - Random.nextInt(0, 10),
            latitude = (sector?.latitude ?: 27.7172) + (Random.nextDouble(-0.002, 0.002)),
            longitude = (sector?.longitude ?: 85.3240) + (Random.nextDouble(-0.002, 0.002)),
            plusCode = "7MV7PJ${Random.nextInt(10, 99)}+${Random.nextInt(10, 99)}",
            timestamp = System.currentTimeMillis(),
            isStoreAndForward = false
        )

        val updatedSignals = listOf(newSignal) + current.distressSignals
        updateSectorsAndAlert(updatedSignals, activeMySignal = newSignal)
    }

    // Simulate multiple signals clustering in a sector to trigger area priority escalation
    fun simulateClusterEscalation(targetSectorId: String = "SEC-BETA") {
        val current = _state.value
        val sector = current.sectors.find { it.id == targetSectorId } ?: return

        val simulatedTagIds = listOf("RT-551D", "RT-620E", "RT-749K", "RT-831L")
        val simulatedNames = listOf("Sunita Rai (6 occupants)", "Gopal Thapa (3 occupants)", "Laxmi Gurung (4 occupants)", "Kamal Adhikari (5 occupants)")

        val generatedSignals = simulatedTagIds.mapIndexed { idx, devEui ->
            DistressSignal(
                id = "SIM-${Random.nextInt(1000, 9999)}",
                devEui = devEui,
                householdName = simulatedNames[idx],
                sectorId = targetSectorId,
                payloadType = if (idx % 2 == 0) PayloadType.SOS_CRITICAL else PayloadType.MEDICAL_URGENT,
                gatewayId = if (idx == 0) "GW-DRONE-07" else "GW-FIXED-01",
                rssiDbm = -82 - Random.nextInt(0, 15),
                snrDb = 5.5 + Random.nextDouble(0.0, 4.0),
                batteryPct = 85 + Random.nextInt(0, 12),
                latitude = sector.latitude + Random.nextDouble(-0.003, 0.003),
                longitude = sector.longitude + Random.nextDouble(-0.003, 0.003),
                plusCode = "7MV7${Random.nextInt(10, 99)}+${Random.nextInt(10, 99)}",
                timestamp = System.currentTimeMillis() - (idx * 15000),
                isStoreAndForward = false
            )
        }

        val updatedSignals = generatedSignals + current.distressSignals
        updateSectorsAndAlert(updatedSignals, activeMySignal = current.activeMyDistress, forceAlertSector = sector.name)
    }

    // Recalculate sector priorities based on signal density (Area Priority Escalation)
    private fun updateSectorsAndAlert(
        signals: List<DistressSignal>,
        activeMySignal: DistressSignal?,
        forceAlertSector: String? = null
    ) {
        val current = _state.value
        val activeSignals = signals.filter { !it.resolved }

        var escalatedSectorName: String? = forceAlertSector

        val updatedSectors = current.sectors.map { sector ->
            val count = activeSignals.count { it.sectorId == sector.id }
            val priority = when {
                count >= 3 -> {
                    if (escalatedSectorName == null && sector.priorityLevel != PriorityLevel.CRITICAL) {
                        escalatedSectorName = sector.name
                    }
                    PriorityLevel.CRITICAL
                }
                count == 2 -> PriorityLevel.ELEVATED
                count == 1 -> PriorityLevel.NORMAL
                else -> PriorityLevel.NORMAL
            }
            sector.copy(
                activeSignalCount = count,
                priorityLevel = priority,
                lastEscalationTimestamp = if (priority == PriorityLevel.CRITICAL) System.currentTimeMillis() else sector.lastEscalationTimestamp
            )
        }

        val alertMessage = escalatedSectorName?.let {
            "CRITICAL DENSITY ESCALATION: Multiple distress signals clustered in $it! Incident Command auto-elevated response priority to maximum."
        }

        _state.update {
            it.copy(
                sectors = updatedSectors,
                distressSignals = signals,
                activeMyDistress = activeMySignal,
                clusterEscalationAlert = alertMessage
            )
        }
    }

    fun dismissClusterAlert() {
        _state.update { it.copy(clusterEscalationAlert = null) }
    }

    fun cancelActiveDistress() {
        _state.update { it.copy(activeMyDistress = null) }
    }

    fun registerHousehold(profile: HouseholdProfile) {
        _state.update {
            val profiles = it.householdProfiles.toMutableMap()
            profiles[profile.devEui] = profile
            it.copy(
                myRegisteredDevEui = profile.devEui,
                householdProfiles = profiles
            )
        }
    }

    fun startPreSeasonDrill(onComplete: (Boolean) -> Unit) {
        _state.update { it.copy(isDrillInProgress = true, lastDrillResult = null) }
    }

    fun completePreSeasonDrill(success: Boolean, details: String) {
        _state.update {
            it.copy(
                isDrillInProgress = false,
                lastDrillResult = details
            )
        }
    }

    fun triggerDownlinkSearchMode(sectorId: String) {
        _state.update {
            it.copy(
                searchModeActive = true,
                searchModeSectorId = sectorId
            )
        }
    }

    fun deactivateSearchMode() {
        _state.update {
            it.copy(
                searchModeActive = false,
                searchModeSectorId = null
            )
        }
    }

    fun syncGatewayCache(gatewayId: String) {
        _state.update { state ->
            val targetGw = state.gateways.find { it.id == gatewayId }
            val countToSync = targetGw?.cachedPacketCount ?: 0

            val flushedSignals = if (countToSync > 0) {
                (1..countToSync.coerceAtMost(5)).map { idx ->
                    DistressSignal(
                        id = "SIG-FLUSH-$gatewayId-$idx-${System.currentTimeMillis()}",
                        devEui = "RT-SYNC-$idx",
                        householdName = "Forwarded Household #$idx",
                        payloadType = if (idx % 2 == 0) PayloadType.MEDICAL_URGENT else PayloadType.ESSENTIAL_SUPPLIES,
                        timestamp = System.currentTimeMillis() - (idx * 300_000L),
                        rssiDbm = -95 + (idx * 2),
                        snrDb = 4.5 + idx,
                        gatewayId = gatewayId,
                        isStoreAndForward = true,
                        latitude = 27.7050 + (idx * 0.003),
                        longitude = 85.3150 + (idx * 0.003),
                        plusCode = "7MV7PJ${idx}F+VW",
                        sectorId = "SEC-GAMMA",
                        batteryPct = 88
                    )
                }
            } else emptyList()

            val updatedGateways = state.gateways.map { gw ->
                if (gw.id == gatewayId) {
                    gw.copy(
                        status = GatewayStatus.ONLINE_STREAMING,
                        cachedPacketCount = 0
                    )
                } else gw
            }
            state.copy(
                gateways = updatedGateways,
                distressSignals = flushedSignals + state.distressSignals
            )
        }
    }

    fun injectSimulatedLiveSignal() {
        val current = _state.value
        val sec = current.sectors.random()
        val randomDev = "RT-${Random.nextInt(100, 999)}${listOf("A", "B", "C", "D", "E").random()}"
        val names = listOf("Maya Gurung", "Bikram Thapa", "Pooja KC", "Arjun Shrestha", "Sunita Magar")
        val pTypes = listOf(PayloadType.SOS_CRITICAL, PayloadType.MEDICAL_URGENT, PayloadType.ESSENTIAL_SUPPLIES)
        val newSignal = DistressSignal(
            id = "LIVE-${System.currentTimeMillis().toString().takeLast(6)}",
            devEui = randomDev,
            householdName = names.random(),
            sectorId = sec.id,
            payloadType = pTypes.random(),
            gatewayId = current.gateways.random().id,
            rssiDbm = -78 - Random.nextInt(0, 30),
            snrDb = 4.0 + Random.nextDouble(0.0, 7.0),
            batteryPct = Random.nextInt(75, 99),
            latitude = sec.latitude + Random.nextDouble(-0.003, 0.003),
            longitude = sec.longitude + Random.nextDouble(-0.003, 0.003),
            plusCode = "7MV7PJ${Random.nextInt(10, 99)}+${Random.nextInt(10, 99)}",
            timestamp = System.currentTimeMillis(),
            isStoreAndForward = false
        )
        val updated = listOf(newSignal) + current.distressSignals
        updateSectorsAndAlert(updated, activeMySignal = current.activeMyDistress)
    }

    fun markDistressResolved(signalId: String) {
        val current = _state.value
        val updated = current.distressSignals.map {
            if (it.id == signalId) it.copy(resolved = true) else it
        }
        val remainingActive = if (current.activeMyDistress?.id == signalId) null else current.activeMyDistress
        updateSectorsAndAlert(updated, activeMySignal = remainingActive)
    }
}

