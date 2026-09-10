package com.antoninclouet.hanzilock.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antoninclouet.hanzilock.ui.theme.Brand

/** Le repère de marque : sceau chinois 印 gravé en réserve sur un aplat vermillon. */
@Composable
fun BrandSeal(size: Dp = 44.dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(size)
            .background(Brand.seal, RoundedCornerShape(size * 0.13f))
            .padding(size * 0.09f)
            .border(
                width = (size.value * 0.02f).coerceAtLeast(1f).dp,
                color = Brand.paper,
                shape = RoundedCornerShape(size * 0.08f),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "印",
            color = Brand.paper,
            fontFamily = Brand.hanziFamily,
            fontWeight = FontWeight.Black,
            fontSize = (size.value * 0.52f).sp,
            textAlign = TextAlign.Center,
        )
    }
}
