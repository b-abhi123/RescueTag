package com.example.model

enum class PayloadType(val code: String, val titleKey: String) {
    SOS_CRITICAL("0x01", "sos_critical"),
    MEDICAL_URGENT("0x02", "medical_urgent"),
    ESSENTIAL_SUPPLIES("0x03", "essential_supplies"),
    SEARCH_ACK("0x04", "search_ack"),
    TEST_DRILL("0x0F", "test_drill")
}

enum class PriorityLevel(val rank: Int) {
    NORMAL(1),
    ELEVATED(2),
    HIGH(3),
    CRITICAL(4)
}

enum class GatewayStatus {
    ONLINE_STREAMING,
    OFFLINE_STORE_AND_FORWARD,
    SYNCING_CACHE
}

enum class GatewayModality(val label: String) {
    FIXED_HIGH_GROUND("Fixed High-Ground"),
    MOBILE_BACKPACK("Mobile Backpack Patrol"),
    VEHICLE_BOAT("Vehicle / Rescue Boat"),
    AERIAL_DRONE("Drone Reconnaissance")
}

data class SectorArea(
    val id: String,
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val activeSignalCount: Int = 0,
    val priorityLevel: PriorityLevel = PriorityLevel.NORMAL,
    val lastEscalationTimestamp: Long = 0L
)

data class DistressSignal(
    val id: String,
    val devEui: String,
    val householdName: String,
    val sectorId: String,
    val payloadType: PayloadType,
    val gatewayId: String,
    val rssiDbm: Int,
    val snrDb: Double,
    val batteryPct: Int,
    val latitude: Double,
    val longitude: Double,
    val plusCode: String,
    val timestamp: Long,
    val isStoreAndForward: Boolean = false,
    val resolved: Boolean = false
)

data class HouseholdProfile(
    val devEui: String,
    val primaryContactName: String,
    val phoneNumber: String,
    val sectorId: String,
    val totalOccupants: Int,
    val elderlyCount: Int,
    val infantCount: Int,
    val medicalConditions: String,
    val secondaryEmergencyPhone: String,
    val registrationDate: Long = System.currentTimeMillis()
)

data class GatewayNode(
    val id: String,
    val name: String,
    val modality: GatewayModality,
    val status: GatewayStatus,
    val cachedPacketCount: Int = 0,
    val batteryLevel: Int = 95,
    val coverageRadiusKm: Double = 12.5,
    val lastPingTimestamp: Long = System.currentTimeMillis()
)
