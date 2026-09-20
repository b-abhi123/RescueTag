package com.example.ui.public

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppState
import com.example.i18n.AppLanguage
import com.example.i18n.Strings
import com.example.model.HouseholdProfile
import com.example.ui.theme.SosNeonGreen

@Composable
fun TagRegistrationScreen(
    state: AppState,
    currentLanguage: AppLanguage,
    onRegister: (HouseholdProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    var devEui by remember { mutableStateOf(state.myRegisteredDevEui) }
    var headName by remember { mutableStateOf(state.householdProfiles[state.myRegisteredDevEui]?.primaryContactName ?: "") }
    var phone by remember { mutableStateOf(state.householdProfiles[state.myRegisteredDevEui]?.phoneNumber ?: "") }
    var secondaryPhone by remember { mutableStateOf(state.householdProfiles[state.myRegisteredDevEui]?.secondaryEmergencyPhone ?: "") }
    var occupants by remember { mutableStateOf((state.householdProfiles[state.myRegisteredDevEui]?.totalOccupants ?: 4).toString()) }
    var elderly by remember { mutableStateOf((state.householdProfiles[state.myRegisteredDevEui]?.elderlyCount ?: 1).toString()) }
    var infants by remember { mutableStateOf((state.householdProfiles[state.myRegisteredDevEui]?.infantCount ?: 1).toString()) }
    var medical by remember { mutableStateOf(state.householdProfiles[state.myRegisteredDevEui]?.medicalConditions ?: "") }
    var selectedSectorId by remember { mutableStateOf(state.householdProfiles[state.myRegisteredDevEui]?.sectorId ?: "SEC-ALPHA") }

    var showSuccessBanner by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        // Header
        Text(
            text = Strings.get("registration_title", currentLanguage),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = Strings.get("registration_desc", currentLanguage),
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Success Confirmation
        AnimatedVisibility(visible = showSuccessBanner) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(SosNeonGreen.copy(alpha = 0.16f))
                    .border(1.5.dp, SosNeonGreen, RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SosNeonGreen)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = Strings.get("registration_success", currentLanguage),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SosNeonGreen
                        )
                    )
                }
            }
        }

        // Form Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(22.dp))
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // DevEUI Input with QR Scan Hint
                OutlinedTextField(
                    value = devEui,
                    onValueChange = { devEui = it.uppercase() },
                    label = { Text(Strings.get("input_deveui", currentLanguage)) },
                    trailingIcon = {
                        Icon(
                            Icons.Default.QrCodeScanner,
                            contentDescription = "Scan Barcode on Tag",
                            tint = SosNeonGreen
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SosNeonGreen,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_deveui")
                )

                // Head of household name
                OutlinedTextField(
                    value = headName,
                    onValueChange = { headName = it },
                    label = { Text(Strings.get("input_head_name", currentLanguage)) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SosNeonGreen,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_head_name")
                )

                // Phone numbers
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(Strings.get("input_phone", currentLanguage)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SosNeonGreen,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_phone")
                )

                OutlinedTextField(
                    value = secondaryPhone,
                    onValueChange = { secondaryPhone = it },
                    label = { Text("Secondary Emergency Hotline/Phone") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SosNeonGreen,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Sector Location Selector
                Column {
                    Text(
                        text = "Assigned Operational Sector:",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        state.sectors.take(3).forEach { sec ->
                            val isSelected = sec.id == selectedSectorId
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) SosNeonGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                                    .border(1.dp, if (isSelected) SosNeonGreen else Color.Transparent, RoundedCornerShape(12.dp))
                                    .clickable { selectedSectorId = sec.id }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = sec.name.replace("Sector ", ""),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) SosNeonGreen else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // Family Demographics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = occupants,
                        onValueChange = { occupants = it },
                        label = { Text("Total") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_occupants")
                    )
                    OutlinedTextField(
                        value = elderly,
                        onValueChange = { elderly = it },
                        label = { Text("Elderly") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_elderly")
                    )
                    OutlinedTextField(
                        value = infants,
                        onValueChange = { infants = it },
                        label = { Text("Infants") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_infants")
                    )
                }

                // Pre-existing medical conditions
                OutlinedTextField(
                    value = medical,
                    onValueChange = { medical = it },
                    label = { Text(Strings.get("input_medical", currentLanguage)) },
                    placeholder = { Text("e.g., Asthma, insulin dependence, mobility restriction") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SosNeonGreen,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_medical")
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Submit Button
                Button(
                    onClick = {
                        val profile = HouseholdProfile(
                            devEui = devEui.trim(),
                            primaryContactName = headName.ifBlank { "Household ${devEui.trim()}" },
                            phoneNumber = phone.ifBlank { "+977-9840000000" },
                            sectorId = selectedSectorId,
                            totalOccupants = occupants.toIntOrNull() ?: 4,
                            elderlyCount = elderly.toIntOrNull() ?: 0,
                            infantCount = infants.toIntOrNull() ?: 0,
                            medicalConditions = medical.trim(),
                            secondaryEmergencyPhone = secondaryPhone.trim()
                        )
                        onRegister(profile)
                        showSuccessBanner = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_submit_registration"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SosNeonGreen,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(Icons.Default.AppRegistration, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Strings.get("submit_registration", currentLanguage),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
