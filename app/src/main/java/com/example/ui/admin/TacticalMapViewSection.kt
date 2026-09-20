package com.example.ui.admin

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import com.example.i18n.AppLanguage
import com.example.i18n.Strings
import com.example.model.DistressSignal
import com.example.model.GatewayNode
import com.example.model.PayloadType
import com.example.model.SectorArea
import com.example.ui.theme.SosEmergencyRed
import com.example.ui.theme.SosMedicalBlue
import com.example.ui.theme.SosNeonGreen
import com.example.ui.theme.SosWarningAmber

@Composable
fun TacticalMapViewSection(
    sectors: List<SectorArea>,
    signals: List<DistressSignal>,
    gateways: List<GatewayNode>,
    onSelectSignal: (DistressSignal) -> Unit,
    currentLanguage: AppLanguage = AppLanguage.ENGLISH,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var focusedSignal by remember { mutableStateOf<DistressSignal?>(signals.firstOrNull()) }

    // Geographic bounding box for the disaster operations zone
    val minLat = 27.6850
    val maxLat = 27.7350
    val minLng = 85.2800
    val maxLng = 85.3500

    Column(modifier = modifier.fillMaxSize()) {
        // Map Controls & Legend
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f, fill = false)) {
                Icon(Icons.Default.Map, contentDescription = null, tint = SosNeonGreen, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = Strings.get("admin_map_title", currentLanguage),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Legend indicators
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                LegendBadge(color = SosEmergencyRed, label = "SOS")
                LegendBadge(color = SosMedicalBlue, label = "Med")
                LegendBadge(color = SosWarningAmber, label = "Supplies")
                LegendBadge(color = SosNeonGreen, label = "GW")
            }
        }

        // TACTICAL MAP CANVAS (Direct coordinate mapping)
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF101317))
                .border(1.5.dp, Color(0xFF252A30), RoundedCornerShape(20.dp))
                .testTag("tactical_map_canvas")
        ) {
            val widthPx = constraints.maxWidth.toFloat()
            val heightPx = constraints.maxHeight.toFloat()

            fun projectToScreen(lat: Double, lng: Double): Offset {
                val normX = ((lng - minLng) / (maxLng - minLng)).coerceIn(0.05, 0.95).toFloat()
                val normY = (1f - ((lat - minLat) / (maxLat - minLat))).coerceIn(0.05, 0.95).toFloat()
                return Offset(normX * widthPx, normY * heightPx)
            }

            // Draw grid lines, sectors, and RF radiuses
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gridColor = Color(0xFF1C2229)
                val gridLines = 5
                for (i in 1..gridLines) {
                    val y = heightPx * (i.toFloat() / (gridLines + 1))
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(widthPx, y),
                        strokeWidth = 1f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
                    )
                    val x = widthPx * (i.toFloat() / (gridLines + 1))
                    drawLine(
                        color = gridColor,
                        start = Offset(x, 0f),
                        end = Offset(x, heightPx),
                        strokeWidth = 1f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
                    )
                }

                // Gateway Coverage Circles
                gateways.forEach { gw ->
                    val gwLat = 27.7100 + ((gw.id.hashCode() % 10) * 0.002)
                    val gwLng = 85.3150 + ((gw.name.hashCode() % 10) * 0.002)
                    val pos = projectToScreen(gwLat, gwLng)
                    drawCircle(
                        color = SosNeonGreen.copy(alpha = 0.08f),
                        radius = 65f,
                        center = pos
                    )
                    drawCircle(
                        color = SosNeonGreen.copy(alpha = 0.5f),
                        radius = 65f,
                        center = pos,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
                    )
                    drawCircle(
                        color = SosNeonGreen,
                        radius = 6f,
                        center = pos
                    )
                }
            }

            // Render interactive signal markers overlaid on the map
            signals.forEach { sig ->
                val pos = projectToScreen(sig.latitude, sig.longitude)
                val isFocused = focusedSignal?.id == sig.id
                val pinColor = when (sig.payloadType) {
                    PayloadType.SOS_CRITICAL -> SosEmergencyRed
                    PayloadType.MEDICAL_URGENT -> SosMedicalBlue
                    PayloadType.ESSENTIAL_SUPPLIES -> SosWarningAmber
                    else -> SosNeonGreen
                }

                Box(
                    modifier = Modifier
                        .size(if (isFocused) 36.dp else 24.dp)
                        .align(Alignment.TopStart)
                        .padding(start = (pos.x - (if (isFocused) 18 else 12)).coerceAtLeast(0f).dp / 2.7f, top = (pos.y - (if (isFocused) 18 else 12)).coerceAtLeast(0f).dp / 2.7f)
                        .clickable {
                            focusedSignal = sig
                            onSelectSignal(sig)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(if (isFocused) 22.dp else 14.dp)
                            .clip(CircleShape)
                            .background(pinColor)
                            .border(2.dp, Color.White, CircleShape)
                    )
                }
            }

            // Compass / Orientation HUD in corner
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "N ↑ (27.71° N, 85.32° E)",
                    style = MaterialTheme.typography.labelSmall,
                    color = SosNeonGreen,
                    fontSize = 9.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Selected Coordinate Targeting Dossier Bar
        focusedSignal?.let { sig ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Target: ${sig.devEui} (${sig.householdName})",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = "GPS: ${sig.latitude}, ${sig.longitude} • Plus Code: ${sig.plusCode}",
                            style = MaterialTheme.typography.labelSmall,
                            color = SosNeonGreen,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(
                            onClick = {
                                clipboardManager.setText(
                                    AnnotatedString("${sig.latitude}, ${sig.longitude} (Plus Code: ${sig.plusCode})")
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.size(36.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy Coordinates", modifier = Modifier.size(16.dp))
                        }

                        Button(
                            onClick = { onSelectSignal(sig) },
                            colors = ButtonDefaults.buttonColors(containerColor = SosNeonGreen, contentColor = Color.Black),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = Strings.get("admin_view_dossier", currentLanguage),
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendBadge(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color(0xFFAAAAAA), fontSize = 10.sp)
    }
}
