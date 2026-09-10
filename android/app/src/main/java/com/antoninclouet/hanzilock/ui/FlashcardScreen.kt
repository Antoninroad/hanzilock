package com.antoninclouet.hanzilock.ui

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antoninclouet.hanzilock.data.HanziCard
import com.antoninclouet.hanzilock.data.ReviewStore
import com.antoninclouet.hanzilock.ui.theme.Brand

@Composable
fun FlashcardScreen(isPro: Boolean, refreshKey: Int, onOpenPaywall: () -> Unit) {
    val context = LocalContext.current
    val store = remember { ReviewStore.get(context) }

    var queue by remember(refreshKey, isPro) { mutableStateOf(store.dueCards(store.activeDeck(isPro)).shuffled()) }
    var revealed by remember { mutableStateOf(false) }
    var refreshTick by remember { mutableIntStateOf(0) }

    val remaining = remember(refreshTick, isPro) { store.reviewsRemainingToday(isPro) }

    fun answer(known: Boolean) {
        val card = queue.firstOrNull() ?: return
        if (known) store.markKnown(card.id) else store.markUnknown(card.id)
        revealed = false
        queue = queue.drop(1)
        refreshTick++
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val current = queue.firstOrNull()
        when {
            remaining <= 0 -> {
                Message("Limite quotidienne atteinte", "Passe à Pro pour des révisions illimitées, ou reviens demain.")
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onOpenPaywall,
                    colors = ButtonDefaults.buttonColors(containerColor = Brand.seal, contentColor = Brand.paper),
                ) { Text("Débloquer Pro") }
            }

            current == null -> Message(
                "Tout est révisé pour aujourd'hui !",
                "Reviens demain pour de nouvelles cartes dues.",
            )

            else -> {
                Card(current, revealed) { revealed = !revealed }
                Spacer(Modifier.height(24.dp))
                AnimatedVisibility(revealed) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedButton(onClick = { answer(false) }) { Text("À revoir") }
                        Button(
                            onClick = { answer(true) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Brand.seal,
                                contentColor = Brand.paper,
                            ),
                        ) { Text("Je connais") }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    if (isPro) "${queue.size} carte(s) restante(s)"
                    else "$remaining révision(s) gratuite(s) restante(s) aujourd'hui",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun Card(card: HanziCard, revealed: Boolean, onTap: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
            .clickable(onClick = onTap)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            card.hanzi,
            fontFamily = Brand.hanziFamily,
            fontWeight = FontWeight.Black,
            fontSize = 72.sp,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (revealed) {
            Spacer(Modifier.height(12.dp))
            Text(card.pinyin, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(card.meaningFr, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
        } else {
            Spacer(Modifier.height(12.dp))
            Text("Touche la carte pour révéler", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun Message(title: String, body: String) {
    Text(title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
    Spacer(Modifier.height(8.dp))
    Text(
        body,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )
}
