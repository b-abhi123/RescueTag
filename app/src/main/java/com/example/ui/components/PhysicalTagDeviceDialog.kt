package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.i18n.AppLanguage
import com.example.i18n.Strings
import com.example.model.DistressSignal
import com.example.model.PayloadType
import com.example.ui.theme.SosEmergencyRed
import com.example.ui.theme.SosMedicalBlue
import com.example.ui.theme.SosNeonGreen
import com.example.ui.theme.SosWarningAmber
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PhysicalTagDeviceDialog(
    devEui: String,
    activeDistress: DistressSignal?,
    currentLanguage: AppLanguage,
    onTriggerDistress: (PayloadType) -> Unit,
    onCancelDistress: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedPayload by remember {
        mutableStateOf(activeDistress?.payloadType ?: PayloadType.SOS_CRITICAL)
    }
    var isTransmitting by remember { mutableStateOf(false) }
    var transmitSuccessMsg by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(28.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(28.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Dialog header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Physical RescueTag Hardware",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Standalone Sub-GHz LoRaWAN Wearable",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("btn_close_physical_tag_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Portability & Specs Callout Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Size: 42×38×9 mm • 24g",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "IP68 Submersible",
                        style = MaterialTheme.typography.labelSmall,
                        color = SosNeonGreen
                    )
                    Text(
                        text = "Li-SOCl₂: 5-Yr Life",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // PHYSICAL HARDWARE FRAME SIMULATION
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF1E2124),
                                    Color(0xFF14171A),
                                    Color(0xFF0F1113)
                                )
                            )
                        )
                        .border(2.dp, Color(0xFF32383E), RoundedCornerShape(26.dp))
                        .padding(18.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Tag top bezel with keychain eyelet & LED
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Carabiner / Lanyard loop hole representation
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0A0C0E))
                                    .border(2.dp, Color(0xFF42484F), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black)
                                )
                            }

                            // Device model & DevEUI printed on hardware
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "RESCUETAG LORAWAN",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    letterSpacing = 1.sp,
                                    color = Color(0xFFAAAAAA)
                                )
                                Text(
                                    text = "DevEUI: $devEui",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White
                                )
                            }

                            // Physical hardware TX LED indicator
                            val ledColor = if (isTransmitting) SosNeonGreen
                            else if (activeDistress != null) SosEmergencyRed
                            else Color(0xFF333333)

                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isTransmitting || activeDistress != null) ledColor.copy(alpha = pulseAlpha) else ledColor
                                    )
                                    .border(1.dp, Color(0xFF555555), CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = Strings.get("tag_select_btn_label", currentLanguage),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFFCCCCCC),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // 3 Physical tactile buttons on the hardware face
                        PhysicalTagButton(
                            title = Strings.get("critical_sos", currentLanguage),
                            subtitle = Strings.get("critical_sos_desc", currentLanguage),
                            color = SosEmergencyRed,
                            icon = Icons.Default.Bolt,
                            isSelected = selectedPayload == PayloadType.SOS_CRITICAL,
                            onClick = { selectedPayload = PayloadType.SOS_CRITICAL },
                            testTagId = "btn_tag_select_sos"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        PhysicalTagButton(
                            title = Strings.get("medical_urgent", currentLanguage),
                            subtitle = Strings.get("medical_urgent_desc", currentLanguage),
                            color = SosMedicalBlue,
                            icon = Icons.Default.LocalHospital,
                            isSelected = selectedPayload == PayloadType.MEDICAL_URGENT,
                            onClick = { selectedPayload = PayloadType.MEDICAL_URGENT },
                            testTagId = "btn_tag_select_med"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        PhysicalTagButton(
                            title = Strings.get("supplies_urgent", currentLanguage),
                            subtitle = Strings.get("supplies_urgent_desc", currentLanguage),
                            color = SosWarningAmber,
                            icon = Icons.Default.WaterDrop,
                            isSelected = selectedPayload == PayloadType.ESSENTIAL_SUPPLIES,
                            onClick = { selectedPayload = PayloadType.ESSENTIAL_SUPPLIES },
                            testTagId = "btn_tag_select_supplies"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // TACTILE HARDWARE TRANSMIT BUTTON
                        Button(
                            onClick = {
                                scope.launch {
                                    isTransmitting = true
                                    transmitSuccessMsg = null
                                    delay(900)
                                    onTriggerDistress(selectedPayload)
                                    isTransmitting = false
                                    transmitSuccessMsg = "Signal Broadcast Complete! 12-byte Sub-GHz packet received by GW-FIXED-01."
                                }
                            },
                            enabled = !isTransmitting,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when (selectedPayload) {
                                    PayloadType.SOS_CRITICAL -> SosEmergencyRed
                                    PayloadType.MEDICAL_URGENT -> SosMedicalBlue
                                    else -> SosWarningAmber
                                },
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .shadow(8.dp, RoundedCornerShape(16.dp))
                                .testTag("btn_tag_transmit_signal")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Radio,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isTransmitting) Strings.get("tag_btn_transmitting", currentLanguage) else Strings.get("tag_btn_send", currentLanguage),
                                    style = MaterialTheme.typography.titleSmall,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Transmission feedback
                        AnimatedVisibility(visible = transmitSuccessMsg != null) {
                            transmitSuccessMsg?.let { msg ->
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = msg,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SosNeonGreen
                                )
                            }
                        }

                        if (activeDistress != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedButton(
                                onClick = onCancelDistress,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF6B6B)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_tag_cancel_active_distress")
                            ) {
                                Text("Reset / Clear Active Distress", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "No smartphone required during actual emergency. Tag operates purely via hardware RF.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun PhysicalTagButton(
    title: String,
    subtitle: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTagId: String
) {
    val borderColor = if (isSelected) color else Color(0xFF2C3238)
    val bgColor = if (isSelected) color.copy(alpha = 0.18f) else Color(0xFF181B1E)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag(testTagId),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(if (isSelected) color else Color(0xFF444444))
        )
        Spacer(modifier = Modifier.width(10.dp))
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) color else Color(0xFF888888),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 13.sp),
                color = if (isSelected) Color.White else Color(0xFFCCCCCC)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = Color(0xFF8E959E)
            )
        }
    }
}
