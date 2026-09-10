package com.antoninclouet.hanzilock.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.antoninclouet.hanzilock.billing.BillingManager
import com.antoninclouet.hanzilock.data.ThemeMode

private enum class Tab(val label: String, val icon: ImageVector) {
    TODAY("Aujourd'hui", Icons.Filled.WbSunny),
    CARDS("Cartes", Icons.AutoMirrored.Filled.ViewList),
    PROGRESS("Progrès", Icons.Filled.BarChart),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onThemeChange: (ThemeMode) -> Unit) {
    val context = LocalContext.current
    val billing = remember { BillingManager.get(context) }
    val isPro by billing.isPro.collectAsStateWithLifecycle()

    var tab by remember { mutableStateOf(Tab.TODAY) }
    var showSettings by remember { mutableStateOf(false) }
    var showPaywall by remember { mutableStateOf(false) }
    var refreshKey by remember { mutableIntStateOf(0) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (showSettings) {
        SettingsScreen(
            isPro = isPro,
            onBack = { showSettings = false; refreshKey++ },
            onThemeChange = onThemeChange,
            onOpenPaywall = { showSettings = false; refreshKey++; showPaywall = true },
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tab.label) },
                actions = {
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Réglages")
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                Tab.entries.forEach { t ->
                    NavigationBarItem(
                        selected = tab == t,
                        onClick = { tab = t },
                        icon = { Icon(t.icon, contentDescription = t.label) },
                        label = { Text(t.label) },
                    )
                }
            }
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (tab) {
                Tab.TODAY -> TodayScreen(refreshKey, isPro)
                Tab.CARDS -> FlashcardScreen(
                    isPro = isPro,
                    refreshKey = refreshKey,
                    onOpenPaywall = { showPaywall = true },
                )
                Tab.PROGRESS -> ProgressScreen(refreshKey, isPro, onOpenPaywall = { showPaywall = true })
            }
        }
    }

    if (showPaywall) {
        ModalBottomSheet(onDismissRequest = { showPaywall = false }, sheetState = sheetState) {
            PaywallScreen()
        }
    }
}
