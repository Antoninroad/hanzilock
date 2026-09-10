package com.antoninclouet.hanzilock.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.sp
import com.antoninclouet.hanzilock.data.ReviewStore
import com.antoninclouet.hanzilock.ui.theme.Brand

@Composable
fun TodayScreen(refreshKey: Int, isPro: Boolean) {
    val context = LocalContext.current
    val card = remember(refreshKey, isPro) {
        val store = ReviewStore.get(context)
        store.cardOfTheDay(store.activeDeck(isPro))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(24.dp))
        Text(
            card.hanzi,
            fontFamily = Brand.hanziFamily,
            fontWeight = FontWeight.Black,
            fontSize = 96.sp,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(12.dp))
        Text(card.pinyin, fontSize = 22.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text(card.meaningFr, fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(8.dp))
        Text(
            "HSK ${card.hskLevel} · ${card.category}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(40.dp))
        WidgetHelpCard()
    }
}

@Composable
private fun WidgetHelpCard() {
    var expanded by remember { mutableStateOf(true) }

    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .clickable { expanded = !expanded }
            .padding(16.dp)
            .animateContentSize(),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "📌  Ajouter le widget à l'écran d'accueil",
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            Icon(
                if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (expanded) {
            Spacer(Modifier.height(10.dp))
            Step("1", "Reviens à ton écran d'accueil et reste appuyé quelques secondes sur une zone vide.")
            Step("2", "Touche « Widgets » dans le menu qui apparaît.")
            Step("3", "Fais défiler jusqu'à « HanziLock » (ou cherche « Hanzi »).")
            Step("4", "Reste appuyé sur le widget et fais-le glisser où tu veux sur l'écran.")
            Spacer(Modifier.height(8.dp))
            Text(
                "Il affichera le même caractère que cet écran, mis à jour chaque jour.\n\n" +
                    "Sur l'écran verrouillé : Android n'autorise pas les widgets tiers. " +
                    "Active « Écran verrouillé » dans les Réglages (⚙️) pour y voir le caractère " +
                    "sous forme de notification discrète.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun Step(n: String, text: String) {
    Row(Modifier.padding(vertical = 4.dp)) {
        Text(
            n,
            fontWeight = FontWeight.Bold,
            color = Brand.seal,
            modifier = Modifier.padding(end = 10.dp),
        )
        Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}
