package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.NdakoViewModel

@Composable
fun AIAdvisorScreen(
  viewModel: NdakoViewModel
) {
  val chatMessages by viewModel.chatMessages.collectAsState()
  val isChatLoading by viewModel.isChatLoading.collectAsState()
  var inputQuery by remember { mutableStateOf("") }

  val suggestions = listOf(
    "Quels biens sont disponibles à Brazzaville ?",
    "Estimer le loyer d'un duplex F4 à Pointe-Noire",
    "Règles contre la tentative de contourner l'agence"
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(NoirProfond)
  ) {
    // Top luxury branding bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(NoirSec)
        .padding(horizontal = 16.dp, vertical = 14.dp)
        .border(
          width = 1.dp,
          color = OrPrestige.copy(alpha = 0.2f),
          shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
        ),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = "AI Advisor",
          tint = OrPrestige,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text(text = "CONSEILLER IA PRESTIGE", color = OrPrestige, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          Text(text = "Analyse, Estimation et Sécurité Marché Congo", color = GrisTechnique, fontSize = 9.sp)
        }
      }

      IconButton(
        onClick = { viewModel.clearAdvisorHistory() },
        modifier = Modifier.size(36.dp)
      ) {
        Icon(Icons.Default.DeleteSweep, contentDescription = "Effacer l'historique", tint = RougeAlerte)
      }
    }

    // CHAT SCREEN VIEWER
    LazyColumn(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth(),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      reverseLayout = false
    ) {
      items(chatMessages) { chat ->
        val isUser = chat.second
        val text = chat.first
        val isFraudAlert = text.contains("[ALERTE FRAUDE", ignoreCase = true)

        Box(
          modifier = Modifier.fillMaxWidth(),
          contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
        ) {
          Card(
            modifier = Modifier
              .fillMaxWidth(0.85f)
              .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                  if (isFraudAlert) listOf(RougeAlerte, Color.Transparent)
                  else if (isUser) listOf(OrPrestige.copy(alpha = 0.3f), Color.Transparent)
                  else listOf(GrisTechnique.copy(alpha = 0.2f), Color.Transparent)
                ),
                shape = RoundedCornerShape(
                  topStart = 20.dp,
                  topEnd = 20.dp,
                  bottomStart = if (isUser) 20.dp else 4.dp,
                  bottomEnd = if (isUser) 4.dp else 20.dp
                )
              ),
            shape = RoundedCornerShape(
              topStart = 20.dp,
              topEnd = 20.dp,
              bottomStart = if (isUser) 20.dp else 4.dp,
              bottomEnd = if (isUser) 4.dp else 20.dp
            ),
            colors = CardDefaults.cardColors(
              containerColor = if (isFraudAlert) RougeAlerte.copy(alpha = 0.15f)
              else if (isUser) NoirSec 
              else NoirSec.copy(alpha = 0.7f)
            )
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text(
                text = if (isFraudAlert) "⚠️ ALERTE DE SÉCURITÉ CONTOURLEMENT"
                       else if (isUser) "Vous" 
                       else "IA Advisor - NDAKO TENDANCE",
                color = if (isFraudAlert) RougeAlerte 
                        else if (isUser) OrPrestige 
                        else OrPrestige.copy(alpha = 0.8f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
              )
              Text(
                text = text,
                color = if (isFraudAlert) RougeAlerte else BlancPur,
                fontSize = 12.sp,
                lineHeight = 16.sp
              )
            }
          }
        }
      }

      if (isChatLoading) {
        item {
          Box(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            contentAlignment = Alignment.CenterStart
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              CircularProgressIndicator(modifier = Modifier.size(16.dp), color = OrPrestige, strokeWidth = 2.dp)
              Spacer(modifier = Modifier.width(8.dp))
              Text(text = "Estimation en cours par l'Advisor...", color = GrisTechnique, fontSize = 11.sp)
            }
          }
        }
      }
    }

    // INTERACTIVE SUGGESTIONS CHIPS
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
      Text(text = "Recommandations rapides :", color = GrisTechnique, fontSize = 10.sp, fontWeight = FontWeight.Bold)
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        suggestions.forEach { sug ->
          Box(
            modifier = Modifier
              .background(NoirSec, RoundedCornerShape(14.dp))
              .border(1.dp, OrPrestige.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
              .clickable { viewModel.askAdvisorAssistant(sug) }
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(text = sug, color = BlancPur, fontSize = 9.sp, fontWeight = FontWeight.Medium)
          }
        }
      }
    }

    // ENTER TEXT CHAT BOX
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedTextField(
        value = inputQuery,
        onValueChange = { inputQuery = it },
        placeholder = { Text("Posez votre question...", color = GrisTechnique.copy(alpha = 0.5f)) },
        modifier = Modifier.weight(1f),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = OrPrestige,
          unfocusedBorderColor = OrPrestige.copy(alpha = 0.3f),
          focusedTextColor = BlancPur,
          unfocusedTextColor = BlancPur
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
        keyboardActions = KeyboardActions(onSend = {
          if (inputQuery.isNotBlank()) {
            viewModel.askAdvisorAssistant(inputQuery)
            inputQuery = ""
          }
        }),
        singleLine = true,
        shape = RoundedCornerShape(10.dp)
      )

      Spacer(modifier = Modifier.width(10.dp))

      IconButton(
        onClick = {
          if (inputQuery.isNotBlank()) {
            viewModel.askAdvisorAssistant(inputQuery)
            inputQuery = ""
          }
        },
        enabled = inputQuery.isNotBlank() && !isChatLoading,
        modifier = Modifier
          .background(
            if (inputQuery.isNotBlank()) OrPrestige else OrPrestige.copy(alpha = 0.3f),
            RoundedCornerShape(10.dp)
          )
          .size(48.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Send,
          contentDescription = "Send",
          tint = NoirProfond
        )
      }
    }
  }
}
