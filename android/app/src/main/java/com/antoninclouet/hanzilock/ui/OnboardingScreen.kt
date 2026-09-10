package com.antoninclouet.hanzilock.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antoninclouet.hanzilock.ui.theme.Brand
import kotlinx.coroutines.launch

private data class Slide(val glyph: String, val title: String, val body: String)

private val slides = listOf(
    Slide("日", "Un caractère par jour",
        "Chaque jour, un nouveau hanzi HSK1 avec son pinyin et son sens. Le même pour l'app et le widget."),
    Slide("锁", "Sur ton écran d'accueil",
        "Ajoute le widget HanziLock à ton écran d'accueil : tu révises d'un coup d'œil, sans ouvrir l'app."),
    Slide("已", "Mémorisation espacée",
        "Le système Leitner te fait revoir chaque carte au bon moment. Marque « Je connais » et regarde ta progression monter."),
)

@Composable
fun OnboardingScreen(onFinish: () -> Unit, onOpenPaywall: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { slides.size })
    val scope = rememberCoroutineScope()
    val isLast by androidx.compose.runtime.remember {
        androidx.compose.runtime.derivedStateOf { pagerState.currentPage == slides.lastIndex }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
            val slide = slides[page]
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    slide.glyph,
                    fontFamily = Brand.hanziFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 110.sp,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    slide.title,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    slide.body,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }

        Button(
            onClick = {
                if (isLast) onOpenPaywall() else scope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Brand.seal, contentColor = Brand.paper),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
        ) {
            Text(if (isLast) "Commencer" else "Suivant")
        }
        TextButton(onClick = onFinish, modifier = Modifier.padding(bottom = 12.dp)) {
            Text("Ignorer", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
