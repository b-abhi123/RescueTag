package com.example.ui.public

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShareLocation
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppState
import com.example.i18n.AppLanguage
import com.example.i18n.Strings
import com.example.model.PayloadType
import com.example.model.PriorityLevel
import com.example.ui.components.AppleRadarView
import com.example.ui.components.TactileTagButtons
import com.example.ui.theme.SosEmergencyRed
import com.example.ui.theme.SosNeonGreen
import com.example.ui.theme.SosWarningAmber

@Composable
fun EmergencySOSScreen(
    state: AppState,
    currentLanguage: AppLanguage,
    onTriggerDistress: (PayloadType) -> Unit,
    onCancelDistress: () -> Unit,
    onSimulateCluster: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val mySector = state.sectors.find { sec ->
        sec.id == (state.householdProfiles[state.myRegisteredDevEui]?.sectorId ?: "SEC-ALPHA")
    }

    val isSectorCritical = mySector?.priorityLevel == PriorityLevel.CRITICAL

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top status chip row (DevEUI & Battery & Gateway)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(SosNeonGreen)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tag: ${state.myRegisteredDevEui}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            Text(
                text = "Li-SOCl₂ Battery: 94%",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = SosNeonGreen,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Apple-style Radar Satellite/Gateway alignment visualizer
        AppleRadarView(
            isConnected = true,
            statusText = if (state.activeMyDistress != null) "TRANSMITTING UPLINK" else "GATEWAY IN RANGE",
            instructionText = if (state.activeMyDistress != null) "Hold Steady • Direct Uplink Active" else Strings.get("point_direction", currentLanguage),
            rssiDbm = state.activeMyDistress?.rssiDbm ?: -84,
            snrDb = state.activeMyDistress?.snrDb ?: 8.2,
            gatewayName = "GW-FIXED-01 (Ridge Base)"
        )

        // Area Priority Escalation Alert Banner if multiple signals clustered
        AnimatedVisibility(visible = isSectorCritical) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(SosEmergencyRed.copy(alpha = 0.16f))
                    .border(1.5.dp, SosEmergencyRed, RoundedCornerShape(18.dp))
                    .padding(14.dp)
                    .testTag("critical_escalation_banner")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CrisisAlert,
                        contentDescription = null,
                        tint = SosEmergencyRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = Strings.get("priority_critical", currentLanguage),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = SosEmergencyRed
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = Strings.get("multiple_signals_alert", currentLanguage),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Active Distress Status Pill (if user triggered)
        if (state.activeMyDistress != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(SosEmergencyRed.copy(alpha = 0.2f))
                    .border(1.5.dp, SosEmergencyRed, RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DISTRESS UPLINK ACTIVE",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = SosEmergencyRed
                            )
                        )
                        OutlinedButton(
                            onClick = onCancelDistress,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = SosEmergencyRed
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.testTag("btn_cancel_distress")
                        ) {
                            Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("End Distress", fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Broadcasting LoRaWAN packet (DevEUI: ${state.activeMyDistress.devEui}) to listening gateways. Rescue command updated.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Section header for tactile physical buttons
        Text(
            text = "Press Dedicated RescueTag Button",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Apple styled 3 tactile buttons
        TactileTagButtons(
            onTriggerPayload = onTriggerDistress,
            activePayload = state.activeMyDistress?.payloadType
        )

        Spacer(modifier = Modifier.height(16.dp))

        // GPS Sensor Coordinates & Direct Emergency Dialer Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(22.dp))
                .padding(16.dp)
                .testTag("gps_coordinates_card")
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = SosNeonGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Exact Offline Coordinates & Plus Code",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "27.7172° N, 85.3240° E  •  Alt: 1,350m",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = "Plus Code: 7MV7PJ9F+VW (Read this to responders over radio/voice)",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // One-tap emergency dialer
                ElevatedButton(
                    onClick = {
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"))
                        context.startActivity(dialIntent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_emergency_dialer"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = SosEmergencyRed,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Call Disaster Emergency Line (112)",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Simulation Trigger for multiple signals / area priority escalation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = null,
                        tint = SosWarningAmber,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Multi-Signal Escalation Simulator",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Test the priority escalation engine: Simulates multiple households triggering tags in your sector, automatically increasing command triage to CRITICAL.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onSimulateCluster,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_simulate_cluster_mobile"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = SosWarningAmber
                    )
                ) {
                    Icon(Icons.Default.CrisisAlert, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Strings.get("simulate_cluster", currentLanguage),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
