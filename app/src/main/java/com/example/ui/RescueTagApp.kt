package com.example.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.RescueTagRepository
import com.example.i18n.AppLanguage
import com.example.i18n.Strings
import com.example.ui.admin.AdminDashboardScreen
import com.example.ui.public.PublicMainScreen
import com.example.ui.theme.RescueTagTheme
import com.example.ui.theme.SosNeonGreen

enum class AppPlatformMode {
    PUBLIC_USER,
    ADMIN_COMMAND
}

@Composable
fun RescueTagApp(
    repository: RescueTagRepository = remember { RescueTagRepository() }
) {
    val state by repository.state.collectAsState()

    var currentMode by remember { mutableStateOf(AppPlatformMode.PUBLIC_USER) }
    var isDarkMode by remember { mutableStateOf(true) }
    var currentLanguage by remember { mutableStateOf(AppLanguage.ENGLISH) }
    var showAdminLoginDialog by remember { mutableStateOf(false) }
    var adminPasscode by remember { mutableStateOf("RESCUE-ADMIN") }

    RescueTagTheme(darkTheme = isDarkMode) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (currentMode) {
                AppPlatformMode.PUBLIC_USER -> {
                    PublicMainScreen(
                        state = state,
                        currentLanguage = currentLanguage,
                        onLanguageChange = { currentLanguage = it },
                        isDarkMode = isDarkMode,
                        onDarkModeToggle = { isDarkMode = it },
                        onTriggerDistress = { payload -> repository.broadcastDistress(payload) },
                        onCancelDistress = { repository.cancelActiveDistress() },
                        onSimulateCluster = { repository.simulateClusterEscalation("SEC-BETA") },
                        onCompleteDrill = { success, details -> repository.completePreSeasonDrill(success, details) },
                        onRegisterHousehold = { profile -> repository.registerHousehold(profile) },
                        onResolveSignal = { sigId -> repository.markDistressResolved(sigId) },
                        onSwitchToAdmin = { showAdminLoginDialog = true }
                    )
                }
                AppPlatformMode.ADMIN_COMMAND -> {
                    AdminDashboardScreen(
                        state = state,
                        currentLanguage = currentLanguage,
                        onReturnToPublic = { currentMode = AppPlatformMode.PUBLIC_USER },
                        onSimulateClusterEscalation = { sectorId -> repository.simulateClusterEscalation(sectorId) },
                        onBroadcastSearchPing = { sectorId -> repository.triggerDownlinkSearchMode(sectorId) },
                        onSyncGateway = { gwId -> repository.syncGatewayCache(gwId) },
                        onDismissAlert = { repository.dismissClusterAlert() },
                        onInjectLiveSignal = { repository.injectSimulatedLiveSignal() }
                    )
                }
            }

            // Admin Login Modal Dialog
            if (showAdminLoginDialog) {
                AlertDialog(
                    onDismissRequest = { showAdminLoginDialog = false },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = SosNeonGreen,
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    title = {
                        Text(
                            text = Strings.get("admin_passcode_prompt", currentLanguage),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    text = {
                        Column {
                            Text(
                                text = "Incident Commander access for Search-and-Rescue operators, tactical sector triage, and LoRaWAN gateway health.",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = adminPasscode,
                                onValueChange = { adminPasscode = it },
                                label = { Text("Commander Passkey") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_admin_passkey")
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showAdminLoginDialog = false
                                currentMode = AppPlatformMode.ADMIN_COMMAND
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SosNeonGreen,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.testTag("btn_confirm_admin_login")
                        ) {
                            Text(
                                text = Strings.get("login_as_admin", currentLanguage),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAdminLoginDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}
