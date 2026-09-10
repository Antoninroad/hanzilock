package com.antoninclouet.hanzilock.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.antoninclouet.hanzilock.billing.BillingManager
import com.antoninclouet.hanzilock.billing.displayPrice
import com.antoninclouet.hanzilock.billing.periodLabel
import com.antoninclouet.hanzilock.ui.theme.Brand

private const val TERMS_URL = "https://antoninroad.github.io/hanzilock/terms.html"
private const val PRIVACY_URL = "https://antoninroad.github.io/hanzilock/privacy.html"

/** Bandeau d'offre de lancement — passe à false une fois la promo terminée (voir MONETIZATION.md). */
private const val SHOW_LAUNCH_OFFER = true
private const val LAUNCH_OFFER_TEXT = "Offre de lancement · −40 % sur l'annuel la 1ʳᵉ année"

/** Écran d'abonnement. Titre, durée, prix et avantages visibles avant l'achat + liens légaux. */
@Composable
fun PaywallScreen() {
    val context = LocalContext.current
    val activity = context as? Activity
    val billing = BillingManager.get(context)
    val products by billing.products.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BrandSeal(size = 64.dp)
        Spacer(Modifier.height(16.dp))
        Text(
            "Débloque HanziLock Pro",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(12.dp))

        if (SHOW_LAUNCH_OFFER) {
            Text(
                LAUNCH_OFFER_TEXT,
                style = MaterialTheme.typography.labelMedium,
                color = Brand.paper,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brand.seal, RoundedCornerShape(10.dp))
                    .padding(vertical = 8.dp, horizontal = 12.dp),
            )
            Spacer(Modifier.height(16.dp))
        }

        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Bullet("HSK 1 à 6 — près de 5000 caractères")
            Bullet("Révisions illimitées chaque jour")
            Bullet("Progression détaillée, niveau par niveau")
        }
        Spacer(Modifier.height(20.dp))

        if (products.isEmpty()) {
            CircularProgressIndicator(color = Brand.seal)
            Spacer(Modifier.height(8.dp))
            Text(
                "Chargement des offres…",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        } else {
            products.forEach { product ->
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { activity?.let { billing.purchase(it, product) } },
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                product.name,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                product.periodLabel,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Text(
                            product.displayPrice,
                            fontWeight = FontWeight.Bold,
                            color = Brand.seal,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        TextButton(onClick = { billing.restore() }) { Text("Restaurer mes achats", color = Brand.seal) }

        Spacer(Modifier.height(8.dp))
        Text(
            "Abonnement reconductible automatiquement, résiliable à tout moment dans le Play Store > Abonnements.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TextButton(onClick = { context.openUrl(TERMS_URL) }) {
                Text("Conditions d'utilisation", style = MaterialTheme.typography.labelSmall, color = Brand.seal)
            }
            TextButton(onClick = { context.openUrl(PRIVACY_URL) }) {
                Text("Confidentialité", style = MaterialTheme.typography.labelSmall, color = Brand.seal)
            }
        }
    }
}

@Composable
private fun Bullet(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text("印  ", color = Brand.seal, fontFamily = Brand.hanziFamily)
        Text(text, color = MaterialTheme.colorScheme.onBackground)
    }
}

private fun android.content.Context.openUrl(url: String) =
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
