package com.example.ui.public

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppState
import com.example.i18n.AppLanguage
import com.example.i18n.Strings
import com.example.model.HouseholdProfile
import com.example.model.PayloadType
import com.example.ui.theme.SosNeonGreen

enum class PublicTab {
    HOME,
    VOLUNTEER,
    DRILL,
    REGISTER,
    SETTINGS
}

@Composable
fun PublicMainScreen(
    state: AppState,
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    onTriggerDistress: (PayloadType) -> Unit,
    onCancelDistress: () -> Unit,
    onSimulateCluster: () -> Unit,
    onCompleteDrill: (Boolean, String) -> Unit,
    onRegisterHousehold: (HouseholdProfile) -> Unit,
    onResolveSignal: (String) -> Unit,
    onSwitchToAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(PublicTab.HOME) }

    Scaffold(
        topBar = {
            PublicTopAppBar(
                currentLanguage = currentLanguage,
                onSwitchToAdmin = onSwitchToAdmin
            )
        },
        bottomBar = {
            PublicBottomNavigationBar(
                activeTab = activeTab,
                onTabSelect = { activeTab = it },
                currentLanguage = currentLanguage
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                PublicTab.HOME -> HomeScreen(
                    state = state,
                    currentLanguage = currentLanguage,
                    onTriggerDistress = onTriggerDistress,
                    onCancelDistress = onCancelDistress,
                    onNavigateToVolunteer = { activeTab = PublicTab.VOLUNTEER },
                    onNavigateToDrill = { activeTab = PublicTab.DRILL },
                    onNavigateToRegister = { activeTab = PublicTab.REGISTER }
                )
                PublicTab.VOLUNTEER -> VolunteerScreen(
                    state = state,
                    currentLanguage = currentLanguage,
                    onResolveSignal = onResolveSignal
                )
                PublicTab.DRILL -> PreSeasonDrillScreen(
                    state = state,
                    currentLanguage = currentLanguage,
                    onCompleteDrill = onCompleteDrill,
                    onSimulateCluster = onSimulateCluster
                )
                PublicTab.REGISTER -> TagRegistrationScreen(
                    state = state,
                    currentLanguage = currentLanguage,
                    onRegister = onRegisterHousehold
                )
                PublicTab.SETTINGS -> GuidesAndSettingsScreen(
                    currentLanguage = currentLanguage,
                    onLanguageChange = onLanguageChange,
                    isDarkMode = isDarkMode,
                    onDarkModeToggle = onDarkModeToggle
                )
            }
        }
    }
}

@Composable
fun PublicTopAppBar(
    currentLanguage: AppLanguage,
    onSwitchToAdmin: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(SosNeonGreen)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = Strings.get("app_title", currentLanguage),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
                Text(
                    text = Strings.get("app_subtitle", currentLanguage),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                )
            }
        }

        // Mode switch pill to Command Center (Desktop / Admin)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                .clickable(onClick = onSwitchToAdmin)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .testTag("btn_switch_to_admin")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DesktopWindows,
                    contentDescription = "Desktop Command",
                    tint = SosNeonGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = Strings.get("admin_command", currentLanguage),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
}

@Composable
fun PublicBottomNavigationBar(
    activeTab: PublicTab,
    onTabSelect: (PublicTab) -> Unit,
    currentLanguage: AppLanguage
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(26.dp))
                .padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavBarItem(
                label = Strings.get("nav_home", currentLanguage),
                icon = Icons.Default.CrisisAlert,
                isSelected = activeTab == PublicTab.HOME,
                onClick = { onTabSelect(PublicTab.HOME) },
                testTagId = "tab_home",
                modifier = Modifier.weight(1f)
            )
            NavBarItem(
                label = Strings.get("nav_volunteer", currentLanguage),
                icon = Icons.Default.Search,
                isSelected = activeTab == PublicTab.VOLUNTEER,
                onClick = { onTabSelect(PublicTab.VOLUNTEER) },
                testTagId = "tab_volunteer",
                modifier = Modifier.weight(1f)
            )
            NavBarItem(
                label = Strings.get("nav_drill", currentLanguage),
                icon = Icons.Default.Shield,
                isSelected = activeTab == PublicTab.DRILL,
                onClick = { onTabSelect(PublicTab.DRILL) },
                testTagId = "tab_drill",
                modifier = Modifier.weight(1f)
            )
            NavBarItem(
                label = Strings.get("nav_register", currentLanguage),
                icon = Icons.Default.AppRegistration,
                isSelected = activeTab == PublicTab.REGISTER,
                onClick = { onTabSelect(PublicTab.REGISTER) },
                testTagId = "tab_register",
                modifier = Modifier.weight(1f)
            )
            NavBarItem(
                label = Strings.get("nav_guides", currentLanguage),
                icon = Icons.Default.MenuBook,
                isSelected = activeTab == PublicTab.SETTINGS,
                onClick = { onTabSelect(PublicTab.SETTINGS) },
                testTagId = "tab_settings",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun NavBarItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTagId: String,
    modifier: Modifier = Modifier
) {
    val contentColor = if (isSelected) SosNeonGreen else MaterialTheme.colorScheme.onSurfaceVariant
    val bgColor = if (isSelected) SosNeonGreen.copy(alpha = 0.15f) else Color.Transparent

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 6.dp)
            .testTag(testTagId),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = contentColor,
                fontSize = 10.sp
            ),
            maxLines = 1,
            softWrap = false,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}
