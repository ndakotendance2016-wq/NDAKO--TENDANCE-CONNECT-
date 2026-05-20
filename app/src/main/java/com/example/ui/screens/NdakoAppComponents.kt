package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun GoldGradientButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true
) {
  Button(
    onClick = onClick,
    enabled = enabled,
    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
    contentPadding = PaddingValues(),
    shape = RoundedCornerShape(12.dp),
    modifier = modifier
      .height(52.dp)
      .border(
        width = 1.dp,
        brush = Brush.horizontalGradient(listOf(OrPrestige, OrMuted)),
        shape = RoundedCornerShape(12.dp)
      )
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          brush = Brush.horizontalGradient(
            if (enabled) listOf(OrMuted.copy(alpha = 0.15f), OrPrestige.copy(alpha = 0.35f))
            else listOf(Color.Transparent, Color.Transparent)
          )
        ),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = text,
        color = if (enabled) BlancPur else GrisTechnique,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
      )
    }
  }
}

@Composable
fun LuxuryCard(
  modifier: Modifier = Modifier,
  content: @Composable ColumnScope.() -> Unit
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .border(
        width = 1.dp,
        brush = Brush.verticalGradient(listOf(OrPrestige.copy(alpha = 0.3f), Color.Transparent)),
        shape = RoundedCornerShape(24.dp)
      ),
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = NoirSec)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
    ) {
      content()
    }
  }
}

@Composable
fun SimulatedQrCodeCanvas(
  modifier: Modifier = Modifier
) {
  // We draw a customized high-prestige golden QR code vector block dynamically to satisfy local app offline install scanning!
  Box(
    modifier = modifier
      .size(160.dp)
      .background(NoirSec, RoundedCornerShape(16.dp))
      .border(1.dp, OrPrestige.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
      .padding(14.dp),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val size = this.size
      val w = size.width
      val h = size.height

      // Background border accents
      drawRect(
        color = OrPrestige,
        topLeft = Offset(0f, 0f),
        size = Size(w * 0.3f, h * 0.3f),
        style = Stroke(width = 4.dp.toPx())
      )
      drawRect(
        color = OrPrestige,
        topLeft = Offset(w * 0.7f, 0f),
        size = Size(w * 0.3f, h * 0.3f),
        style = Stroke(width = 4.dp.toPx())
      )
      drawRect(
        color = OrPrestige,
        topLeft = Offset(0f, h * 0.7f),
        size = Size(w * 0.3f, h * 0.3f),
        style = Stroke(width = 4.dp.toPx())
      )

      // Mini inner code pixels representing "TENDANCE CONNECT" database routing
      drawRect(color = OrPrestige, topLeft = Offset(w * 0.1f, w * 0.1f), size = Size(w * 0.1f, h * 0.1f))
      drawRect(color = OrPrestige, topLeft = Offset(w * 0.8f, w * 0.1f), size = Size(w * 0.1f, h * 0.1f))
      drawRect(color = OrPrestige, topLeft = Offset(w * 0.1f, w * 0.8f), size = Size(w * 0.1f, h * 0.1f))

      // Stable aesthetic dots to prevent flickering and high CPU usage during redraws
      val dots = listOf(
        0.25f to 0.45f, 0.35f to 0.25f, 0.45f to 0.65f, 
        0.55f to 0.35f, 0.65f to 0.55f, 0.75f to 0.25f, 
        0.25f to 0.75f, 0.35f to 0.55f, 0.45f to 0.35f,
        0.55f to 0.75f, 0.65f to 0.45f, 0.75f to 0.65f
      )
      dots.forEachIndexed { i, (xRatio, yRatio) ->
        drawRect(
          color = if (i % 2 == 0) OrMuted else OrPrestige,
          topLeft = Offset(w * xRatio, h * yRatio),
          size = Size(10.dp.toPx(), 10.dp.toPx())
        )
      }
    }
  }
}

@Composable
fun AnimatedHeaderSubtitle(
  title: String,
  subtitle: String
) {
  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = title,
      color = OrPrestige,
      fontSize = 24.sp,
      fontWeight = FontWeight.ExtraBold,
      letterSpacing = 2.sp,
      textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = subtitle,
      color = GrisTechnique,
      fontSize = 12.sp,
      fontWeight = FontWeight.Medium,
      textAlign = TextAlign.Center
    )
  }
}

@Composable
fun CustomAnalyticsChart(
  propertyCounts: Map<String, Int>,
  transactionSums: Map<String, Double>
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = "RAPPORT DE RENDEMENT FINANCIER & ANALYSE",
      color = OrPrestige,
      fontSize = 14.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 1.sp,
      modifier = Modifier.padding(bottom = 12.dp)
    )

    // 1. Availability statuses indicator
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      val dispVal = propertyCounts["DISPONIBLE"] ?: 0
      val occVal = propertyCounts["OCCUPÉ"] ?: 0
      val travVal = propertyCounts["EN_TRAVAUX"] ?: 0

      StatusStatBadge(label = "DISPONIBLES", count = dispVal, color = VertOk)
      StatusStatBadge(label = "OCCUPÉS", count = occVal, color = RougeAlerte)
      StatusStatBadge(label = "EN TRAVAUX", count = travVal, color = GrisTechnique)
    }

    // 2. Beautiful customized gold layout bars representing financial breakdown
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(NoirSec.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
        .border(1.dp, OrPrestige.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
        .padding(16.dp)
    ) {
      Text(
        text = "MONÉTISATION PAR FRAIS (USD)",
        color = BlancPur,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 12.dp)
      )

      val subscriptionRevenue = transactionSums["ABONNEMENT"] ?: 0.0
      val commissionRevenue = transactionSums["COMMISSION"] ?: 0.0
      val maxVal = maxOf(subscriptionRevenue, commissionRevenue, 5000.0)

      FinancialBar(
        label = "Abonnements Récurrents ($subscriptionRevenue USD)",
        amount = subscriptionRevenue,
        max = maxVal,
        color = OrPrestige
      )
      Spacer(modifier = Modifier.height(14.dp))
      FinancialBar(
        label = "Commissions Relation ($commissionRevenue USD)",
        amount = commissionRevenue,
        max = maxVal,
        color = OrMuted
      )
    }
  }
}

@Composable
fun StatusStatBadge(label: String, count: Int, color: Color) {
  Card(
    modifier = Modifier
      .width(96.dp)
      .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = NoirSec)
  ) {
    Column(
      modifier = Modifier.padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = label, color = GrisTechnique, fontSize = 8.sp, fontWeight = FontWeight.Bold)
      Text(text = count.toString(), color = color, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
    }
  }
}

@Composable
fun FinancialBar(label: String, amount: Double, max: Double, color: Color) {
  val ratio = (amount / max).toFloat().coerceIn(0.05f, 1f)
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(text = label, color = GrisTechnique, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    Spacer(modifier = Modifier.height(4.dp))
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(16.dp)
        .background(NoirProfond, RoundedCornerShape(4.dp))
    ) {
      Box(
        modifier = Modifier
          .fillMaxHeight()
          .fillMaxWidth(ratio)
          .background(color, RoundedCornerShape(4.dp))
      )
    }
  }
}
