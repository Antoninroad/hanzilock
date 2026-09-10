package com.antoninclouet.hanzilock.ui

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.antoninclouet.hanzilock.data.AppSettings
import com.antoninclouet.hanzilock.data.HskData
import com.antoninclouet.hanzilock.data.ReviewStore
import com.antoninclouet.hanzilock.data.ThemeMode
import com.antoninclouet.hanzilock.notif.ReminderScheduler
import com.antoninclouet.hanzilock.ui.theme.Brand

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isPro: Boolean,
    onBack: () -> Unit,
    onThemeChange: (ThemeMode) -> Unit,
    onOpenPaywall: () -> Unit,
) {
    val context = LocalContext.current
    val store = remember { ReviewStore.get(context) }
    val settings = remember { AppSettings.get(context) }

    var levels by remember { mutableStateOf(store.enabledLevels()) }
    var freeLimit by remember { mutableStateOf(store.freeLimit()) }
    var theme by remember { mutableStateOf(settings.themeMode) }
    var reminderOn by remember { mutableStateOf(settings.reminderEnabled) }
    var reminderMin by remember { mutableStateOf(settings.reminderMinutes) }
    var lockCardOn by remember { mutableStateOf(settings.lockScreenCard) }
    var showReset by remember { mutableStateOf(false) }
    var pendingPermFor by remember { mutableStateOf<String?>(null) }

    val notifPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        when (pendingPermFor) {
            "reminder" -> { reminderOn = granted; settings.reminderEnabled = granted }
            "lock" -> { lockCardOn = granted; settings.lockScreenCard = granted }
        }
        pendingPermFor = null
        ReminderScheduler.apply(context)
    }

    fun setToggle(key: String, want: Boolean, persist: (Boolean) -> Unit) {
        val needsPerm = want && Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        if (needsPerm) {
            pendingPermFor = key
            notifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            persist(want)
            ReminderScheduler.apply(context)
        }
    }

    fun toggleLevel(level: Int) {
        if (level !in HskData.availableLevels) return
        if (level !in ReviewStore.FREE_LEVELS && !isPro) {
            onOpenPaywall()
            return
        }
        levels = if (level in levels) (levels - level).ifEmpty { setOf(1) } else levels + level
        store.setEnabledLevels(levels)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Réglages") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            SectionTitle("Niveaux HSK")
            Text(
                if (isPro) "Choisis les niveaux à réviser."
                else "HSK 1 et 2 sont gratuits. HSK 3 à 6 sont inclus dans HanziLock Pro.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            HskData.allLevels.forEach { level ->
                val available = level in HskData.availableLevels
                val locked = available && level !in ReviewStore.FREE_LEVELS && !isPro
                Row(
                    Modifier
                        .fillMaxWidth()
                        .toggleable(
                            value = level in levels && !locked,
                            enabled = available,
                            onValueChange = { toggleLevel(level) },
                        )
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = level in levels && available && !locked,
                        enabled = available && !locked,
                        onCheckedChange = null,
                    )
                    Text(
                        "  HSK $level",
                        color = if (available) MaterialTheme.colorScheme.onBackground
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (!available) {
                        Text("  · Bientôt", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else if (locked) {
                        Text("  🔒 Pro", style = MaterialTheme.typography.labelSmall, color = Brand.seal)
                    }
                }
            }
            Text(
                "${store.activeDeck(isPro).size} caractères dans ta sélection",
                style = MaterialTheme.typography.labelMedium,
                color = Brand.seal,
            )

            Divider16()
            SectionTitle("Révisions par jour")
            if (isPro) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(20, 50, 100, store.unlimited).forEach { value ->
                        FilterChip(
                            selected = freeLimit == value,
                            onClick = { freeLimit = value; store.setFreeLimit(value) },
                            label = { Text(if (value >= store.unlimited) "∞" else "$value") },
                        )
                    }
                }
            } else {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onOpenPaywall)
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "${ReviewStore.FREE_DAILY_REVIEWS} / jour — illimité avec Pro",
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text("🔒 Pro", style = MaterialTheme.typography.labelSmall, color = Brand.seal)
                }
            }

            Divider16()
            SectionTitle("Thème")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemeMode.entries.forEach { mode ->
                    FilterChip(
                        selected = theme == mode,
                        onClick = {
                            theme = mode
                            settings.themeMode = mode
                            onThemeChange(mode)
                        },
                        label = {
                            Text(
                                when (mode) {
                                    ThemeMode.SYSTEM -> "Système"
                                    ThemeMode.LIGHT -> "Clair"
                                    ThemeMode.DARK -> "Sombre"
                                },
                            )
                        },
                    )
                }
            }

            Divider16()
            SectionTitle("Écran verrouillé")
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Afficher le caractère du jour", color = MaterialTheme.colorScheme.onBackground)
                Switch(
                    checked = lockCardOn,
                    onCheckedChange = { want ->
                        setToggle("lock", want) { lockCardOn = it; settings.lockScreenCard = it }
                    },
                )
            }
            Text(
                "Android n'autorise pas les vrais widgets d'écran verrouillé : c'est une notification " +
                    "discrète et permanente, visible sur l'écran verrouillé.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Divider16()
            SectionTitle("Rappel quotidien")
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Me rappeler chaque jour", color = MaterialTheme.colorScheme.onBackground)
                Switch(
                    checked = reminderOn,
                    onCheckedChange = { want ->
                        setToggle("reminder", want) { reminderOn = it; settings.reminderEnabled = it }
                    },
                )
            }
            if (reminderOn) {
                TextButton(onClick = {
                    TimePickerDialog(
                        context,
                        { _, h, m ->
                            reminderMin = h * 60 + m
                            settings.reminderMinutes = reminderMin
                            ReminderScheduler.apply(context)
                        },
                        reminderMin / 60, reminderMin % 60, true,
                    ).show()
                }) {
                    Text("Heure : %02d:%02d".format(reminderMin / 60, reminderMin % 60), color = Brand.seal)
                }
            }

            Divider16()
            SectionTitle("Progression")
            TextButton(onClick = { showReset = true }) {
                Text("Réinitialiser ma progression", color = Brand.seal)
            }

            Spacer(Modifier.height(24.dp))
            Text(
                "HanziLock 1.0 · aucune donnée collectée",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }

    if (showReset) {
        AlertDialog(
            onDismissRequest = { showReset = false },
            title = { Text("Réinitialiser ?") },
            text = { Text("Toutes les cartes apprises et le compteur du jour seront remis à zéro. Action irréversible.") },
            confirmButton = {
                TextButton(onClick = {
                    store.resetProgress()
                    showReset = false
                }) { Text("Réinitialiser", color = Brand.seal) }
            },
            dismissButton = {
                TextButton(onClick = { showReset = false }) { Text("Annuler") }
            },
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp),
    )
}

@Composable
private fun Divider16() {
    Spacer(Modifier.height(12.dp))
    HorizontalDivider()
    Spacer(Modifier.height(4.dp))
}
