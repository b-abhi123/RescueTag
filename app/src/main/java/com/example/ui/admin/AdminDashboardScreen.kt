package com.example.ui.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppState
import com.example.i18n.AppLanguage
import com.example.i18n.Strings
import com.example.model.DistressSignal
import com.example.model.GatewayModality
import com.example.model.GatewayNode
import com.example.model.GatewayStatus
import com.example.model.HouseholdProfile
import com.example.model.PayloadType
import com.example.model.PriorityLevel
import com.example.model.SectorArea
import com.example.ui.components.SectorPriorityCard
import com.example.ui.theme.SosEmergencyRed
import com.example.ui.theme.SosMedicalBlue
import com.example.ui.theme.SosNeonGreen
import com.example.ui.theme.SosWarningAmber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminDashboardScreen(
    state: AppState,
    currentLanguage: AppLanguage,
    onReturnToPublic: () -> Unit,
    onSimulateClusterEscalation: (String) -> Unit,
    onBroadcastSearchPing: (String) -> Unit,
    onSyncGateway: (String) -> Unit,
    onDismissAlert: () -> Unit,
    onInjectLiveSignal: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedVictimSignal by remember { mutableStateOf<DistressSignal?>(null) }
    var activeAdminTab by remember { mutableStateOf(0) } // 0: Sectors & Triage, 1: Live Ingest, 2: Map View, 3: Gateway Fleet

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Desktop Command Center Header
        CommandCenterHeader(
            currentLanguage = currentLanguage,
            onReturnToPublic = onReturnToPublic
        )

        // ADVANCED STATISTICS BAR
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .testTag("admin_advanced_statistics_bar")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = Strings.get("admin_stat_intercept", currentLanguage),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 9.sp,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(text = "11.4 min", style = MaterialTheme.typography.titleSmall, color = SosNeonGreen, maxLines = 1, softWrap = false)
                }
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = Strings.get("admin_stat_reliability", currentLanguage),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 9.sp,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(text = "98.7% PDR", style = MaterialTheme.typography.titleSmall, color = SosNeonGreen, maxLines = 1, softWrap = false)
                }
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = Strings.get("admin_stat_critical", currentLanguage),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 9.sp,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                    val critCount = state.distressSignals.count { it.payloadType == PayloadType.SOS_CRITICAL && !it.resolved }
                    Text(text = "$critCount", style = MaterialTheme.typography.titleSmall, color = if (critCount > 0) SosEmergencyRed else SosNeonGreen, maxLines = 1, softWrap = false)
                }
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = Strings.get("admin_stat_mesh", currentLanguage),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 9.sp,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                    val cachedCount = state.gateways.sumOf { it.cachedPacketCount }
                    Text(text = "$cachedCount", style = MaterialTheme.typography.titleSmall, color = SosWarningAmber, maxLines = 1, softWrap = false)
                }
            }
        }

        // Cluster Escalation Global Alert Banner
        state.clusterEscalationAlert?.let { alertMsg ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SosEmergencyRed.copy(alpha = 0.2f))
                    .border(1.5.dp, SosEmergencyRed, RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .testTag("admin_cluster_alert")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CrisisAlert, contentDescription = null, tint = SosEmergencyRed, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = alertMsg,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SosEmergencyRed,
                                lineHeight = 16.sp
                            )
                        )
                    }
                    IconButton(onClick = onDismissAlert, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = SosEmergencyRed, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // Sub-navigation pills (Sectors, Live Ingest, Map View, Gateway Fleet)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AdminTabPill(
                title = Strings.get("admin_tab_sectors", currentLanguage),
                subtitle = "${state.sectors.count { it.priorityLevel == PriorityLevel.CRITICAL }} Crit",
                isSelected = activeAdminTab == 0,
                onClick = { activeAdminTab = 0 },
                testTagId = "tab_admin_sectors",
                modifier = Modifier.weight(1f)
            )
            AdminTabPill(
                title = Strings.get("admin_tab_ingest", currentLanguage),
                subtitle = "${state.distressSignals.size} Pkts",
                isSelected = activeAdminTab == 1,
                onClick = { activeAdminTab = 1 },
                testTagId = "tab_admin_ingest",
                modifier = Modifier.weight(1f)
            )
            AdminTabPill(
                title = Strings.get("admin_tab_map", currentLanguage),
                subtitle = "Coord",
                isSelected = activeAdminTab == 2,
                onClick = { activeAdminTab = 2 },
                testTagId = "tab_admin_map",
                modifier = Modifier.weight(1f)
            )
            AdminTabPill(
                title = Strings.get("admin_tab_gateways", currentLanguage),
                subtitle = "${state.gateways.size} Nodes",
                isSelected = activeAdminTab == 3,
                onClick = { activeAdminTab = 3 },
                testTagId = "tab_admin_gateways",
                modifier = Modifier.weight(1f)
            )
        }

        // Main Content Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            when (activeAdminTab) {
                0 -> SectorTriageSection(
                    sectors = state.sectors,
                    currentLanguage = currentLanguage,
                    onSimulateClusterEscalation = onSimulateClusterEscalation,
                    onBroadcastSearchPing = onBroadcastSearchPing
                )
                1 -> LiveIngestStreamSection(
                    signals = state.distressSignals,
                    currentLanguage = currentLanguage,
                    onSelectSignal = { selectedVictimSignal = it },
                    onInjectLiveSignal = onInjectLiveSignal
                )
                2 -> TacticalMapViewSection(
                    sectors = state.sectors,
                    signals = state.distressSignals,
                    gateways = state.gateways,
                    currentLanguage = currentLanguage,
                    onSelectSignal = { selectedVictimSignal = it }
                )
                3 -> GatewayFleetSection(
                    gateways = state.gateways,
                    onSyncGateway = onSyncGateway
                )
            }
        }
    }

    // Modal Victim & Household Card
    selectedVictimSignal?.let { sig ->
        val profile = state.householdProfiles[sig.devEui]
        VictimProfileDialog(
            signal = sig,
            profile = profile,
            currentLanguage = currentLanguage,
            onDismiss = { selectedVictimSignal = null }
        )
    }
}

@Composable
fun CommandCenterHeader(
    currentLanguage: AppLanguage,
    onReturnToPublic: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Return to public mode button (compact, properly scaled, no vertical wrap)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                .clickable(onClick = onReturnToPublic)
                .padding(horizontal = 10.dp, vertical = 8.dp)
                .testTag("btn_return_to_public")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = Strings.get("return_public", currentLanguage),
                    tint = SosNeonGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = Strings.get("return_public", currentLanguage),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    maxLines = 1,
                    softWrap = false
                )
            }
        }

        // Title and Subtitle with flexible weight so they never push buttons
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = Strings.get("admin_command_title", currentLanguage),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = Strings.get("admin_command_subtitle", currentLanguage),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Live status dot
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(SosEmergencyRed)
        )
    }
}

@Composable
fun AdminTabPill(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTagId: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) SosNeonGreen.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = if (isSelected) SosNeonGreen else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .testTag(testTagId),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                    color = if (isSelected) SosNeonGreen else MaterialTheme.colorScheme.onSurface,
                    fontSize = 11.sp
                ),
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 9.sp
                ),
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SectorTriageSection(
    sectors: List<SectorArea>,
    currentLanguage: AppLanguage,
    onSimulateClusterEscalation: (String) -> Unit,
    onBroadcastSearchPing: (String) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(sectors) { sector ->
            Column {
                SectorPriorityCard(sector = sector)

                Spacer(modifier = Modifier.height(6.dp))

                // Action buttons for simulation & search mode
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onSimulateClusterEscalation(sector.id) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = SosWarningAmber
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_escalate_${sector.id}")
                    ) {
                        Icon(Icons.Default.CrisisAlert, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = Strings.get("admin_btn_escalate", currentLanguage),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    OutlinedButton(
                        onClick = { onBroadcastSearchPing(sector.id) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = SosNeonGreen
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Radar, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = Strings.get("admin_btn_ping", currentLanguage),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun LiveIngestStreamSection(
    signals: List<DistressSignal>,
    currentLanguage: AppLanguage = AppLanguage.ENGLISH,
    onSelectSignal: (DistressSignal) -> Unit,
    onInjectLiveSignal: () -> Unit = {}
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Live Packet Ingest Queue (${signals.size})",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onInjectLiveSignal,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SosNeonGreen.copy(alpha = 0.2f),
                        contentColor = SosNeonGreen
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("btn_inject_packet")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = Strings.get("admin_inject_packet", currentLanguage),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }

        items(signals) { sig ->
            val (typeColor, typeLabel) = when (sig.payloadType) {
                PayloadType.SOS_CRITICAL -> SosEmergencyRed to "CRITICAL SOS"
                PayloadType.MEDICAL_URGENT -> SosMedicalBlue to "MEDICAL URGENT"
                PayloadType.ESSENTIAL_SUPPLIES -> SosWarningAmber to "SUPPLIES"
                PayloadType.SEARCH_ACK -> SosNeonGreen to "SEARCH ACK"
                PayloadType.TEST_DRILL -> SosNeonGreen to "DRILL"
            }

            val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val timeStr = timeFormat.format(Date(sig.timestamp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, typeColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .clickable { onSelectSignal(sig) }
                    .padding(14.dp)
                    .testTag("ingest_packet_${sig.id}")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(typeColor)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = typeLabel,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "DevEUI: ${sig.devEui}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }

                        Text(
                            text = timeStr,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Household: ${sig.householdName}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Gateway: ${sig.gatewayId} (${sig.rssiDbm} dBm / +${sig.snrDb} dB)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                        Text(
                            text = "Sector: ${sig.sectorId}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SosNeonGreen,
                                fontSize = 11.sp
                            )
                        )
                    }

                    if (sig.isStoreAndForward) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SosWarningAmber.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "STORE & FORWARD (Cached in field)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SosWarningAmber,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun GatewayFleetSection(
    gateways: List<GatewayNode>,
    onSyncGateway: (String) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "Operational Gateway Fleet & Store-and-Forward Cache",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        items(gateways) { gw ->
            val (icon, modalityLabel) = when (gw.modality) {
                GatewayModality.FIXED_HIGH_GROUND -> Icons.Default.CellTower to "Fixed High Ground"
                GatewayModality.MOBILE_BACKPACK -> Icons.Default.Hiking to "Foot Patrol Backpack"
                GatewayModality.AERIAL_DRONE -> Icons.Default.Flight to "Aerial Drone Recon"
                GatewayModality.VEHICLE_BOAT -> Icons.Default.DirectionsBoat to "Rescue Boat Unit"
            }

            val (statusColor, statusText) = when (gw.status) {
                GatewayStatus.ONLINE_STREAMING -> SosNeonGreen to "ONLINE (Live Backhaul)"
                GatewayStatus.OFFLINE_STORE_AND_FORWARD -> SosWarningAmber to "STORE & FORWARD (Offline Cache)"
                GatewayStatus.SYNCING_CACHE -> SosMedicalBlue to "SYNCING CACHE"
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(statusColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = null, tint = statusColor, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = gw.name,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = "${gw.id} • $modalityLabel",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(statusColor.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = statusText,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = statusColor,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Coverage: ${gw.coverageRadiusKm} km  •  Battery: ${gw.batteryLevel}%",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        if (gw.cachedPacketCount > 0) {
                            Text(
                                text = "${gw.cachedPacketCount} Packets Cached",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SosWarningAmber
                                )
                            )
                        }
                    }

                    if (gw.cachedPacketCount > 0) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { onSyncGateway(gw.id) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SosNeonGreen,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Bulk Sync Offline Packets (${gw.cachedPacketCount})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun VictimProfileDialog(
    signal: DistressSignal,
    profile: HouseholdProfile?,
    currentLanguage: AppLanguage = AppLanguage.ENGLISH,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = SosNeonGreen)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = Strings.get("admin_dossier_title", currentLanguage),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "DevEUI: ${signal.devEui}",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = SosNeonGreen
                    )
                )
                Text(
                    text = "Household: ${profile?.primaryContactName ?: signal.householdName}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = "Emergency Phone: ${profile?.phoneNumber ?: "Not on file"}",
                    style = MaterialTheme.typography.bodySmall
                )
                profile?.let {
                    Text(
                        text = "Family Size: ${it.totalOccupants} (${it.elderlyCount} elderly, ${it.infantCount} infants)",
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (it.medicalConditions.isNotBlank()) {
                        Text(
                            text = "Special Medical Needs: ${it.medicalConditions}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SosEmergencyRed
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Sector: ${signal.sectorId}  •  Plus Code: ${signal.plusCode}",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Text(
                    text = "Signal: ${signal.rssiDbm} dBm  •  Gateway: ${signal.gatewayId}",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = SosNeonGreen, contentColor = Color.Black)
            ) {
                Text(
                    text = Strings.get("admin_close_dossier", currentLanguage),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    )
}
