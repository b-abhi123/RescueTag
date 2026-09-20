package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PayloadType
import com.example.ui.theme.SosEmergencyRed
import com.example.ui.theme.SosMedicalBlue
import com.example.ui.theme.SosWarningAmber

@Composable
fun TactileTagButtons(
    onTriggerPayload: (PayloadType) -> Unit,
    activePayload: PayloadType?,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Red SOS Button
        AppleEmergencyButton(
            title = "Life-Threatening SOS",
            subtitle = "Trapped, immediate danger, structural collapse",
            icon = Icons.Default.Warning,
            accentColor = SosEmergencyRed,
            isActive = activePayload == PayloadType.SOS_CRITICAL,
            testTagId = "btn_sos_critical",
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onTriggerPayload(PayloadType.SOS_CRITICAL)
            }
        )

        // Blue Medical Button
        AppleEmergencyButton(
            title = "Medical Emergency",
            subtitle = "Severe trauma, acute illness, medication needed",
            icon = Icons.Default.LocalHospital,
            accentColor = SosMedicalBlue,
            isActive = activePayload == PayloadType.MEDICAL_URGENT,
            testTagId = "btn_medical_urgent",
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onTriggerPayload(PayloadType.MEDICAL_URGENT)
            }
        )

        // Yellow Supplies Button
        AppleEmergencyButton(
            title = "Essential Supplies Depleted",
            subtitle = "No potable water or food, stranded household",
            icon = Icons.Default.WaterDrop,
            accentColor = SosWarningAmber,
            isActive = activePayload == PayloadType.ESSENTIAL_SUPPLIES,
            testTagId = "btn_supplies_depleted",
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onTriggerPayload(PayloadType.ESSENTIAL_SUPPLIES)
            }
        )
    }
}

@Composable
fun AppleEmergencyButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    isActive: Boolean,
    testTagId: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(120),
        label = "btn_press_scale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isActive -> accentColor.copy(alpha = 0.22f)
            isPressed -> MaterialTheme.colorScheme.surfaceVariant
            else -> MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(180),
        label = "btn_bg_color"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isActive -> accentColor
            isPressed -> accentColor.copy(alpha = 0.6f)
            else -> MaterialTheme.colorScheme.outline
        },
        animationSpec = tween(180),
        label = "btn_border_color"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(22.dp))
            .background(backgroundColor)
            .border(
                width = if (isActive) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(22.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 18.dp, vertical = 16.dp)
            .testTag(testTagId),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rounded icon container with tactile accent
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.18f))
                .border(1.dp, accentColor.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = accentColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            )
        }

        if (isActive) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "ACTIVE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                )
            }
        }
    }
}
