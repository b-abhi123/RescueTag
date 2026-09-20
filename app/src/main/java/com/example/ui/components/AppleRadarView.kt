package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SosNeonGreen
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AppleRadarView(
    modifier: Modifier = Modifier,
    isConnected: Boolean = true,
    statusText: String = "CONNECTED",
    instructionText: String = "LoRaWAN Gateway in Direct Range",
    rssiDbm: Int = -84,
    snrDb: Double = 8.2,
    gatewayName: String = "GW-FIXED-01 (Ridge Tower)"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar_anim")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Radar circular display
        Box(
            modifier = Modifier
                .size(220.dp)
                .testTag("apple_radar_canvas_container"),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(210.dp)
                    .testTag("apple_radar_canvas")
            ) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val maxRadius = size.width / 2f - 12.dp.toPx()

                // Outer boundary circle
                drawCircle(
                    color = Color.Gray.copy(alpha = 0.25f),
                    radius = maxRadius,
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )

                // Middle concentric ring
                drawCircle(
                    color = Color.Gray.copy(alpha = 0.18f),
                    radius = maxRadius * 0.65f,
                    center = center,
                    style = Stroke(width = 1.2.dp.toPx())
                )

                // Inner concentric ring
                drawCircle(
                    color = Color.Gray.copy(alpha = 0.15f),
                    radius = maxRadius * 0.35f,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )

                if (isConnected) {
                    // Radar sweep sector beam (arc)
                    drawArc(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                SosNeonGreen.copy(alpha = 0.45f),
                                SosNeonGreen.copy(alpha = 0.12f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = maxRadius
                        ),
                        startAngle = sweepAngle - 35f,
                        sweepAngle = 70f,
                        useCenter = true,
                        topLeft = Offset(center.x - maxRadius, center.y - maxRadius),
                        size = Size(maxRadius * 2f, maxRadius * 2f)
                    )

                    // Directional green alignment arc
                    drawArc(
                        color = SosNeonGreen.copy(alpha = 0.85f),
                        startAngle = 230f,
                        sweepAngle = 80f,
                        useCenter = false,
                        topLeft = Offset(center.x - maxRadius * 0.9f, center.y - maxRadius * 0.9f),
                        size = Size(maxRadius * 1.8f, maxRadius * 1.8f),
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Target Gateway Node Orb on the perimeter arc
                    val orbAngleRad = Math.toRadians(270.0).toFloat()
                    val orbRadius = maxRadius * 0.9f
                    val orbCenter = Offset(
                        center.x + orbRadius * cos(orbAngleRad),
                        center.y + orbRadius * sin(orbAngleRad)
                    )

                    drawCircle(
                        color = Color.White,
                        radius = 8.dp.toPx(),
                        center = orbCenter
                    )
                    drawCircle(
                        color = SosNeonGreen,
                        radius = 5.dp.toPx(),
                        center = orbCenter
                    )
                }

                // Center glowing beacon
                drawCircle(
                    color = if (isConnected) SosNeonGreen.copy(alpha = 0.25f * pulseScale) else Color.Gray.copy(alpha = 0.2f),
                    radius = 28.dp.toPx() * pulseScale,
                    center = center
                )

                drawCircle(
                    color = if (isConnected) SosNeonGreen else Color.Gray,
                    radius = 16.dp.toPx(),
                    center = center
                )
            }

            // Center device icon inside radar
            Icon(
                imageVector = Icons.Default.Radio,
                contentDescription = "Device Center",
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Status pill / text like the Apple UI
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (isConnected) SosNeonGreen.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.15f))
                .border(
                    width = 1.dp,
                    color = if (isConnected) SosNeonGreen.copy(alpha = 0.4f) else Color.Gray.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isConnected) SosNeonGreen else Color.Gray)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = statusText.uppercase(),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = if (isConnected) SosNeonGreen else MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = instructionText,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Telemetry readout
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.CellTower,
                contentDescription = "Gateway",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$gatewayName  •  RSSI: $rssiDbm dBm  •  SNR: +$snrDb dB",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
