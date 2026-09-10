package com.antoninclouet.hanzilock.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antoninclouet.hanzilock.data.HskData
import com.antoninclouet.hanzilock.data.ReviewStore
import com.antoninclouet.hanzilock.ui.theme.Brand

@Composable
fun ProgressScreen(refreshKey: Int, isPro: Boolean, onOpenPaywall: () -> Unit) {
    val context = LocalContext.current
    val store = remember { ReviewStore.get(context) }
    val deck = remember(refreshKey, isPro) { store.activeDeck(isPro) }
    val mastered = remember(refreshKey, isPro) { store.masteredCount(deck) }
    val fraction = if (deck.isEmpty()) 0f else mastered.toFloat() / deck.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(24.dp))
        Text(
            "$mastered / ${deck.size}",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 48.sp,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text("caractères maîtrisés dans ta sélection", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(24.dp))

        Bar(fraction)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Maîtrisés : $mastered", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Restants : ${deck.size - mastered}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Spacer(Modifier.height(32.dp))
        Text(
            "Détail par niveau",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))

        if (isPro) {
            val all = remember(refreshKey) { HskData.deck }
            HskData.allLevels.forEach { lv ->
                val levelDeck = all.filter { it.hskLevel == lv }
                if (levelDeck.isNotEmpty()) {
                    val m = store.masteredCount(levelDeck)
                    LevelRow(lv, m, levelDeck.size)
                }
            }
        } else {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                    .clickable(onClick = onOpenPaywall)
                    .padding(16.dp),
            ) {
                Text("🔒  Progression détaillée par niveau HSK", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Débloquée avec HanziLock Pro, avec HSK 1 à 6.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun Bar(fraction: Float) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(24.dp)
            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
    ) {
        Box(
            Modifier
                .fillMaxWidth(fraction.coerceIn(0f, 1f))
                .height(24.dp)
                .background(Brand.jade, RoundedCornerShape(12.dp)),
        )
    }
}

@Composable
private fun LevelRow(level: Int, mastered: Int, total: Int) {
    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("HSK $level", color = MaterialTheme.colorScheme.onBackground)
            Text("$mastered / $total", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(4.dp))
        Bar(if (total == 0) 0f else mastered.toFloat() / total)
    }
}
