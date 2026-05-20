package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DomainVerification
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.NdakoViewModel

@Composable
fun PrivacyVerificationScreen(
  viewModel: NdakoViewModel,
  onVerificationSuccess: () -> Unit,
  onNavigateBack: () -> Unit
) {
  val scrollState = rememberScrollState()
  val currentUser by viewModel.currentUser.collectAsState()
  val activeOtp by viewModel.activeSimulatedOtp.collectAsState()
  
  var otpInput by remember { mutableStateOf("") }
  var acceptedCharter by remember { mutableStateOf(false) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(NoirProfond)
      .verticalScroll(scrollState)
      .padding(horizontal = 20.dp, vertical = 24.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Spacer(modifier = Modifier.height(30.dp))

    Icon(
      imageVector = Icons.Default.DomainVerification,
      contentDescription = "Charter Lock",
      tint = OrPrestige,
      modifier = Modifier.size(60.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = "SAS D'INSCRIPTION SÉCURISÉ",
      color = OrPrestige,
      fontSize = 20.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 2.sp,
      textAlign = TextAlign.Center
    )

    Text(
      text = "Validation de la Charte & Code d'Activation",
      color = GrisTechnique,
      fontSize = 13.sp,
      textAlign = TextAlign.Center,
      modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
    )

    // CHARTER RULES INFORMATION DISPLAY
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, OrPrestige.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
      colors = CardDefaults.cardColors(containerColor = NoirSec)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(bottom = 8.dp)
        ) {
          Icon(Icons.Default.Info, contentDescription = "Rules", tint = OrPrestige, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "Règles d'Utilisation NDAKO TENDANCE", color = OrPrestige, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        
        Text(
          text = "1. Souveraineté de l'Agence:\nL'agence prélève un abonnement forfaitaire ou une commission fixe de 10% sur chaque réservation ou transaction immobilière trackée.\n\n" +
                 "2. Traçabilité des Relations:\nTous les contacts WhatsApp sont historisés par l'API pour éviter les fraudes et le contournement direct.\n\n" +
                 "3. Fiabilité des Disponibilités:\nLes propriétaires s'engagent à actualiser quotidiennement l'état de leurs biens en un clic. Tout manquement entraîne une suspension immédiate.",
          color = BlancPur,
          fontSize = 11.sp,
          lineHeight = 15.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // SIMULATED SYSTEM SMS BANNER TO ELIMINATE TESTER DEAD-ENDS
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, OrPrestige.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
      colors = CardDefaults.cardColors(containerColor = OrMuted.copy(alpha = 0.1f))
    ) {
      Column(
        modifier = Modifier.padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "[SIMULATION SMS DE L'AGENCE]",
          color = OrPrestige,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Un code d'activation OTP unique a été simulé pour votre numéro (${currentUser?.phone}) :",
          color = BlancPur,
          fontSize = 11.sp,
          textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
          modifier = Modifier
            .background(NoirSec, RoundedCornerShape(6.dp))
            .border(1.dp, OrPrestige.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
          Text(
            text = activeOtp,
            color = OrPrestige,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 2.sp
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "(Ou saisissez le code générique de test : 2016)",
          color = GrisTechnique,
          fontSize = 9.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // INPUT VALIDATION FORM
    LuxuryCard {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Checkbox(
          checked = acceptedCharter,
          onCheckedChange = { acceptedCharter = it },
          colors = CheckboxDefaults.colors(
            checkedColor = OrPrestige,
            uncheckedColor = GrisTechnique,
            checkmarkColor = NoirProfond
          )
        )
        Text(
          text = "J'accepte sans réserve les conditions techniques d'adhésion et de commissions de l'agence.",
          color = BlancPur,
          fontSize = 11.sp,
          lineHeight = 14.sp
        )
      }

      Spacer(modifier = Modifier.height(18.dp))

      OutlinedTextField(
        value = otpInput,
        onValueChange = { otpInput = it },
        label = { Text("Entrez le code OTP reçu", color = GrisTechnique) },
        placeholder = { Text("ex. 4032", color = GrisTechnique.copy(alpha = 0.4f)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = OrPrestige,
          unfocusedBorderColor = OrPrestige.copy(alpha = 0.3f),
          focusedTextColor = BlancPur,
          unfocusedTextColor = BlancPur
        ),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(20.dp))

      GoldGradientButton(
        text = "VALIDER MON INSCRIPTION",
        enabled = acceptedCharter && otpInput.length >= 4,
        onClick = {
          if (viewModel.verifyOtpAndTerms(otpInput)) {
            onVerificationSuccess()
          } else {
            viewModel.setAlert("Code OTP d'activation invalide. Veuillez réessayer.")
          }
        },
        modifier = Modifier.fillMaxWidth()
      )
      
      Spacer(modifier = Modifier.height(12.dp))
      
      TextButton(
        onClick = onNavigateBack,
        modifier = Modifier.align(Alignment.CenterHorizontally)
      ) {
        Text(text = "Retourner à l'accueil", color = OrPrestige, fontSize = 12.sp)
      }
    }

    Spacer(modifier = Modifier.height(40.dp))
  }
}
