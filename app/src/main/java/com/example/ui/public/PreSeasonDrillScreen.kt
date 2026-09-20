package com.example.ui.public

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppState
import com.example.i18n.AppLanguage
import com.example.i18n.Strings
import com.example.ui.components.AppleRadarView
import com.example.ui.theme.SosNeonGreen
import com.example.ui.theme.SosWarningAmber
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PreSeasonDrillScreen(
    state: AppState,
    currentLanguage: AppLanguage,
    onCompleteDrill: (Boolean, String) -> Unit,
    onSimulateCluster: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    var drillStep by remember { mutableStateOf(0) } // 0: Idle, 1: Connecting/Aligning, 2: Transmitting, 3: Success
    var drillLog by remember { mutableStateOf("") }


    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hero Header Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(22.dp))
                .padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(SosNeonGreen.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = SosNeonGreen,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = Strings.get("drill_title", currentLanguage),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = Strings.get("drill_desc", currentLanguage),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Radar simulation view for alignment
        AppleRadarView(
            isConnected = drillStep >= 1,
            statusText = when (drillStep) {
                0 -> Strings.get("drill_radar_ready", currentLanguage)
                1 -> Strings.get("drill_radar_aligning", currentLanguage)
                2 -> Strings.get("drill_radar_transmitting", currentLanguage)
                else -> Strings.get("drill_radar_verified", currentLanguage)
            },
            instructionText = when (drillStep) {
                0 -> Strings.get("drill_radar_inst_0", currentLanguage)
                1 -> Strings.get("drill_radar_inst_1", currentLanguage)
                2 -> Strings.get("drill_radar_inst_2", currentLanguage)
                else -> Strings.get("drill_radar_inst_3", currentLanguage)
            },
            rssiDbm = -79,
            snrDb = 9.8,
            gatewayName = "GW-FIXED-01 (Ridge Base)"
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Step Guide Checklist
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DrillStepItem(
                stepNumber = "1",
                text = Strings.get("drill_step1", currentLanguage),
                isCompleted = drillStep >= 1,
                isActive = drillStep == 0
            )
            DrillStepItem(
                stepNumber = "2",
                text = Strings.get("drill_step2", currentLanguage),
                isCompleted = drillStep >= 2,
                isActive = drillStep == 1
            )
            DrillStepItem(
                stepNumber = "3",
                text = Strings.get("drill_step3", currentLanguage),
                isCompleted = drillStep >= 3,
                isActive = drillStep == 2
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Result banner on completion
        AnimatedVisibility(visible = drillStep == 3) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(SosNeonGreen.copy(alpha = 0.16f))
                    .border(1.5.dp, SosNeonGreen, RoundedCornerShape(18.dp))
                    .padding(16.dp)
                    .testTag("drill_result_banner")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SosNeonGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = Strings.get("drill_tag_verified", currentLanguage),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SosNeonGreen
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = Strings.get("drill_success", currentLanguage),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }
        }

        // Action button
        Button(
            onClick = {
                if (drillStep == 0 || drillStep == 3) {
                    drillStep = 1
                    drillLog = "Aligning to nearest high-ground LoRaWAN station..."
                    scope.launch {
                        delay(1400)
                        drillStep = 2
                        drillLog = "Broadcasting Sub-GHz test uplink packet..."
                        delay(1600)
                        drillStep = 3
                        val result = "DevEUI ${state.myRegisteredDevEui} verified with Gateway GW-FIXED-01 (RSSI: -82 dBm, SNR: +8.5 dB)"
                        drillLog = result
                        onCompleteDrill(true, result)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("btn_start_drill"),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (drillStep == 3) MaterialTheme.colorScheme.surfaceVariant else SosNeonGreen,
                contentColor = if (drillStep == 3) MaterialTheme.colorScheme.onSurface else Color.Black
            ),
            enabled = drillStep != 1 && drillStep != 2
        ) {
            if (drillStep == 1 || drillStep == 2) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (drillStep == 1) Strings.get("drill_aligning_antenna", currentLanguage) else Strings.get("drill_awaiting_ack", currentLanguage),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    softWrap = false
                )
            } else {
                Icon(
                    imageVector = if (drillStep == 3) Icons.Default.Radio else Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (drillStep == 3) Strings.get("drill_run_again", currentLanguage) else Strings.get("start_drill", currentLanguage),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // MULTI-SIGNAL CLUSTER ESCALATION SIMULATOR
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                .padding(16.dp)
                .testTag("card_cluster_escalation_simulator")
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(com.example.ui.theme.SosEmergencyRed.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = com.example.ui.theme.SosEmergencyRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = Strings.get("drill_cluster_title", currentLanguage),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = Strings.get("drill_cluster_subtitle", currentLanguage),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = Strings.get("drill_cluster_body", currentLanguage),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onSimulateCluster,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = com.example.ui.theme.SosEmergencyRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_trigger_cluster_escalation")
                ) {
                    Text(
                        text = Strings.get("simulate_cluster", currentLanguage),
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        softWrap = false,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DrillStepItem(
    stepNumber: String,
    text: String,
    isCompleted: Boolean,
    isActive: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isCompleted -> SosNeonGreen
                        isActive -> SosWarningAmber
                        else -> Color.Gray.copy(alpha = 0.2f)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Text(
                    text = stepNumber,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isActive) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isActive || isCompleted) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isActive || isCompleted) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}
