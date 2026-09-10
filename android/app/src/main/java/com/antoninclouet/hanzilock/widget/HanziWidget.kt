package com.antoninclouet.hanzilock.widget

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.antoninclouet.hanzilock.data.HskData
import com.antoninclouet.hanzilock.data.ReviewStore

private val Paper = Color(0xFFEDE7DC)
private val Ink = Color(0xFF1C1A17)
private val InkSoft = Color(0xFF5B564C)

class HanziWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val store = ReviewStore.get(context)
        val isPro = com.antoninclouet.hanzilock.billing.BillingManager.get(context).isPro.value
        val deck = store.activeDeck(isPro).ifEmpty { HskData.deck }
        val card = store.cardOfTheDay(deck)
        val mastered = store.masteredCount(deck)
        val total = deck.size

        provideContent {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(Paper)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    card.hanzi,
                    style = TextStyle(
                        color = ColorProvider(Ink),
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        textAlign = TextAlign.Center,
                    ),
                )
                Text(
                    card.pinyin,
                    style = TextStyle(color = ColorProvider(InkSoft), fontSize = 13.sp, textAlign = TextAlign.Center),
                )
                Text(
                    card.meaningFr,
                    style = TextStyle(color = ColorProvider(Ink), fontSize = 12.sp, textAlign = TextAlign.Center),
                )
                Text(
                    "$mastered / $total",
                    style = TextStyle(color = ColorProvider(InkSoft), fontSize = 10.sp, textAlign = TextAlign.Center),
                )
            }
        }
    }
}
