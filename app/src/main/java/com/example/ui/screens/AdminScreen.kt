package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BusinessTransaction
import com.example.data.UserAccount
import com.example.ui.theme.*
import com.example.viewmodel.NdakoViewModel

@Composable
fun AdminScreen(
  viewModel: NdakoViewModel
) {
  val users by viewModel.usersList.collectAsState()
  val transactions by viewModel.transactionsList.collectAsState()
  val properties by viewModel.propertiesList.collectAsState()

  val scrollState = rememberScrollState()
  var selectedTab by remember { mutableIntStateOf(0) } // 0 = Comptes, 1 = Flux Financiers & Workflow, 2 = Rapport & Dashboard

  // Calculations for bar charts
  val propertyCounts = remember(properties) {
    val counts = mutableMapOf("DISPONIBLE" to 0, "OCCUPÉ" to 0, "EN_TRAVAUX" to 0)
    properties.forEach {
      counts[it.status] = (counts[it.status] ?: 0) + 1
    }
    counts
  }

  val transactionTotals = remember(transactions) {
    val sums = mutableMapOf("ABONNEMENT" to 0.0, "COMMISSION" to 0.0)
    transactions.forEach {
      if (it.status == "VALIDE") {
        sums[it.type] = (sums[it.type] ?: 0.0) + it.amount
      }
    }
    sums
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(NoirProfond)
  ) {
    // Elegant luxury tab selector
    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = NoirSec,
      contentColor = OrPrestige,
      indicator = { tabPositions ->
        TabRowDefaults.SecondaryIndicator(
          Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
          color = OrPrestige
        )
      }
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = { Text("ÉCOSYSTÈME COMPTES", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = { Text("FLUX & VALUATION", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
      )
      Tab(
        selected = selectedTab == 2,
        onClick = { selectedTab = 2 },
        text = { Text("RAPPORT RENDEMENT", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    // RENDER SCREENS ACCORDING TO SELECTED OPTION
    when (selectedTab) {
      0 -> {
        // --- USERS COMPTES MANAGEMENT PANEL ---
        LazyColumn(
          modifier = Modifier.weight(1f).fillMaxWidth(),
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          item {
            Card(
              modifier = Modifier.fillMaxWidth().border(1.dp, OrPrestige.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
              colors = CardDefaults.cardColors(containerColor = NoirSec)
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Text(
                  text = "POUVOIRS SOUVERAINS DE L'AGENCE",
                  color = OrPrestige,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "L'administration dispose d'un contrôle de coupure immédiate : suspension instantanée pour non-paiement, radiation de démarcheur, ou validation d'accès.",
                  color = GrisTechnique,
                  fontSize = 10.sp,
                  lineHeight = 13.sp
                )
              }
            }
          }

          items(users) { usr ->
            UserManagementCard(
              user = usr,
              onToggleSuspend = { viewModel.modifyUserSuspendedStatus(usr, !usr.isSuspended) },
              onTogglePaid = { viewModel.modifyUserPaidStatus(usr, !usr.subscriptionPaid) },
              onPurge = { viewModel.purgeUserAccount(usr) }
            )
          }
        }
      }

      1 -> {
        // --- TRANSACTIONS METRICS AND WORKFLOW VERIFICATIONS ---
        LazyColumn(
          modifier = Modifier.weight(1f).fillMaxWidth(),
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          item {
            Text(
              text = "HISTORIQUE FINTECH (COMPTE AGENCE)",
              color = OrPrestige,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(bottom = 6.dp)
            )
            Text(
              text = "Frais connectés en République du Congo (MTN, Airtel) et portail Stripe.",
              color = GrisTechnique,
              fontSize = 11.sp,
              modifier = Modifier.padding(bottom = 12.dp)
            )
          }

          items(transactions) { tx ->
            TransactionLedgerItem(
              tx = tx,
              onRespondToWorkflow = { approve, comment ->
                viewModel.respondToWorkflowTransaction(tx, approve, comment)
              }
            )
          }
        }
      }

      2 -> {
        // --- DATA VISUALIZATION DASHBOARD ---
        Column(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          LuxuryCard {
            CustomAnalyticsChart(
              propertyCounts = propertyCounts,
              transactionSums = transactionTotals
            )
          }

          LuxuryCard {
            Text(
              text = "CONTRÔLE DES DIRECTS WHATSAPP",
              color = OrPrestige,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Le tunneling de mise en relation a enregistré les connexions des clients avant d'autoriser la redirection vers MTN (+242066226914) ou Airtel (+242044783082).",
              color = BlancPur,
              fontSize = 11.sp,
              lineHeight = 15.sp
            )
            
            Spacer(modifier = Modifier.height(14.dp))
            
            // Render beautiful mini indicator of total redirections and estimation indices
            val whatsappTxCount = transactions.count { it.type == "MISE_EN_RELATION" }
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(NoirProfond, RoundedCornerShape(8.dp))
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(text = "Rapports d'intermédiations", color = GrisTechnique, fontSize = 10.sp)
                Text(text = "$whatsappTxCount Redirections Trackées", color = OrPrestige, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
              }
              Icon(Icons.Default.TrendingUp, contentDescription = "Growth", tint = VertOk)
            }
          }
        }
      }
    }
  }
}

@Composable
fun UserManagementCard(
  user: UserAccount,
  onToggleSuspend: () -> Unit,
  onTogglePaid: () -> Unit,
  onPurge: () -> Unit
) {
  // Prevent admin from deleting themselves
  val isAgencyAdmin = user.email == "ndakotendance2016@gmail.com"

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .border(
        width = 1.dp,
        color = if (user.isSuspended) RougeAlerte.copy(alpha = 0.7f) else OrPrestige.copy(alpha = 0.15f),
        shape = RoundedCornerShape(12.dp)
      ),
    colors = CardDefaults.cardColors(containerColor = NoirSec)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text(text = user.name, color = BlancPur, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          Text(text = "Rôle : ${user.role} | Email : ${user.email}", color = GrisTechnique, fontSize = 10.sp)
          Text(text = "Phone : ${user.phone}", color = GrisTechnique, fontSize = 10.sp)
        }

        Box(
          modifier = Modifier
            .background(
              if (user.isSuspended) RougeAlerte.copy(alpha = 0.15f)
              else if (user.isApproved) VertOk.copy(alpha = 0.15f)
              else OrPrestige.copy(alpha = 0.15f),
              RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = if (user.isSuspended) "SUSPENDU" else if (user.isApproved) "CHARTE OK" else "OTP PENDING",
            color = if (user.isSuspended) RougeAlerte else if (user.isApproved) VertOk else OrPrestige,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        if (!isAgencyAdmin) {
          // SUSPEND / ACTIVATE BUTTON
          Button(
            onClick = onToggleSuspend,
            colors = ButtonDefaults.buttonColors(
              containerColor = if (user.isSuspended) VertOk else RougeAlerte
            ),
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 8.dp),
            modifier = Modifier.weight(1f).height(32.dp)
          ) {
            Text(
              text = if (user.isSuspended) "Réactiver" else "Suspendre",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = NoirProfond
            )
          }

          // SUBSCRIPTION LOCK OUT TOGGLE (For Owners or Agents)
          Button(
            onClick = onTogglePaid,
            colors = ButtonDefaults.buttonColors(containerColor = OrMuted),
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 8.dp),
            modifier = Modifier.weight(1f).height(32.dp)
          ) {
            Text(
              text = if (user.subscriptionPaid) "Bloquer Cotise" else "Valider Cotise",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = BlancPur
            )
          }

          // DESTRUCTIVE PURGE EXCLUSIVELY FOR SUPER ADMIN
          IconButton(
            onClick = onPurge,
            modifier = Modifier
              .background(NoirProfond, RoundedCornerShape(6.dp))
              .size(32.dp)
          ) {
            Icon(Icons.Default.DeleteForever, contentDescription = "Purge", tint = RougeAlerte, modifier = Modifier.size(16.dp))
          }
        } else {
          Text(
            text = "Ce compte représente le Super-Admin souverain de NDAKO TENDANCE.",
            color = OrPrestige,
            fontSize = 9.sp,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }
  }
}

@Composable
fun TransactionLedgerItem(
  tx: BusinessTransaction,
  onRespondToWorkflow: (Boolean, String) -> Unit
) {
  var responseText by remember { mutableStateOf("") }
  var showRespondView by remember { mutableStateOf(false) }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .border(
        width = 1.dp,
        color = when (tx.type) {
          "COMMISSION" -> OrPrestige.copy(alpha = 0.3f)
          "ABONNEMENT" -> OrMuted.copy(alpha = 0.3f)
          "DOSSIER_BANCAIRE" -> Color.Cyan.copy(alpha = 0.3f)
          else -> Color.Magenta.copy(alpha = 0.3f)
        },
        shape = RoundedCornerShape(12.dp)
      ),
    colors = CardDefaults.cardColors(containerColor = NoirSec)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column {
          Text(
            text = "[${tx.type}]",
            color = when (tx.type) {
              "COMMISSION" -> OrPrestige
              "ABONNEMENT" -> OrMuted
              "DOSSIER_BANCAIRE" -> Color.Cyan
              else -> Color.Magenta
            },
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
          Text(text = tx.propertyTitle, color = BlancPur, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          Text(text = "Réf : ${tx.requesterEmail}", color = GrisTechnique, fontSize = 9.sp)
        }

        Column(horizontalAlignment = Alignment.End) {
          if (tx.amount > 0) {
            Text(text = "${tx.amount.toInt()} USD", color = BlancPur, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }
          Box(
            modifier = Modifier
              .background(
                when (tx.status) {
                  "VALIDE" -> VertOk.copy(alpha = 0.15f)
                  "EN_ATTENTE" -> OrPrestige.copy(alpha = 0.15f)
                  else -> RougeAlerte.copy(alpha = 0.15f)
                },
                RoundedCornerShape(4.dp)
              )
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = tx.status,
              color = when (tx.status) {
                "VALIDE" -> VertOk
                "EN_ATTENTE" -> OrPrestige
                else -> RougeAlerte
              },
              fontSize = 8.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = tx.comments,
        color = GrisTechnique,
        fontSize = 11.sp,
        lineHeight = 14.sp
      )

      if (tx.paymentMethod.isNotBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Passerelle de facturation : ${tx.paymentMethod}",
          color = OrPrestige,
          fontSize = 9.sp,
          fontWeight = FontWeight.SemiBold
        )
      }

      // Action workflow panel for banks & notary files or admin commission verifications
      if (tx.status == "EN_ATTENTE") {
        Spacer(modifier = Modifier.height(10.dp))
        
        if (!showRespondView) {
          Button(
            onClick = { showRespondView = true },
            colors = ButtonDefaults.buttonColors(containerColor = OrPrestige),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.fillMaxWidth().height(32.dp)
          ) {
            Text("Traiter ce dossier", color = NoirProfond, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }
        } else {
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedTextField(
              value = responseText,
              onValueChange = { responseText = it },
              placeholder = { Text("Commentaire de validation ou décision...", color = GrisTechnique, fontSize = 10.sp) },
              modifier = Modifier.fillMaxWidth().height(56.dp),
              colors = OutlinedTextFieldDefaults.colors(focusedTextColor = BlancPur, unfocusedTextColor = BlancPur)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Button(
                onClick = {
                  onRespondToWorkflow(true, responseText.ifBlank { "Validé par l'autorité compétente." })
                  showRespondView = false
                },
                colors = ButtonDefaults.buttonColors(containerColor = VertOk),
                modifier = Modifier.weight(1f).height(32.dp)
              ) {
                Text("Approuver", color = NoirProfond, fontSize = 9.sp, fontWeight = FontWeight.Bold)
              }

              Button(
                onClick = {
                  onRespondToWorkflow(false, responseText.ifBlank { "Rejeté après étude de dossier." })
                  showRespondView = false
                },
                colors = ButtonDefaults.buttonColors(containerColor = RougeAlerte),
                modifier = Modifier.weight(1f).height(32.dp)
              ) {
                Text("Rejeter", color = BlancPur, fontSize = 9.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
  }
}
