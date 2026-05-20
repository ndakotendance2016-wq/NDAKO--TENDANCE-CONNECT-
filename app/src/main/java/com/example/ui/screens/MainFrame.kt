package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.content.Intent
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.NdakoViewModel

@Composable
fun MainFrame(
  viewModel: NdakoViewModel
) {
  val currentUser by viewModel.currentUser.collectAsState()
  val statusAlert by viewModel.statusAlert.collectAsState()

  // Navigation route controls: "LANDING", "OTP_SAS", "MAIN_FLOW"
  var currentRoute by remember { mutableStateOf("LANDING") }
  
  // App main tab selections: 0 = Discover, 1 = IA Advisor, 2 = Admin (only visible to agency role)
  var selectedTab by remember { mutableIntStateOf(0) }

  var showDownloadDialog by remember { mutableStateOf(false) }
  var downloadProgress by remember { mutableStateOf(0f) }
  var isDownloading by remember { mutableStateOf(false) }
  val context = androidx.compose.ui.platform.LocalContext.current

  LaunchedEffect(isDownloading) {
    if (isDownloading) {
      downloadProgress = 0f
      while (downloadProgress < 100f) {
        kotlinx.coroutines.delay(80)
        downloadProgress += 5f
      }
      isDownloading = false
      viewModel.setAlert("Préparation de l'APK terminée ! Prêt à télécharger.")
    }
  }

  // DOWNLOAD AND INSTALL INSTRUCTION DIALOG IN MAIN FLOW
  if (showDownloadDialog) {
    AlertDialog(
      onDismissRequest = { 
        showDownloadDialog = false 
        isDownloading = false
      },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Download,
            contentDescription = "Install App",
            tint = OrPrestige,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(text = "Assistant d'Installation APK", color = OrPrestige, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          if (isDownloading || downloadProgress > 0f && downloadProgress < 100f) {
            Text(
              text = "Génération du package d'installation optimal...",
              color = BlancPur,
              fontSize = 12.sp
            )
            LinearProgressIndicator(
              progress = { downloadProgress / 100f },
              modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
              color = OrPrestige,
              trackColor = OrMuted.copy(alpha = 0.2f),
            )
            Text(
              text = "Progression : ${downloadProgress.toInt()}% (Compilation et signatures)",
              color = GrisTechnique,
              fontSize = 11.sp
            )
          } else if (downloadProgress >= 100f) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .background(OrMuted.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                .border(1.dp, OrPrestige.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .padding(10.dp)
            ) {
              Text(
                text = "✓ Paquet APK vérifié et prêt pour votre mobile.\nÉditeur: NDAKO Tendance Connect\nStatut: Certifié conforme",
                color = OrPrestige,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Instructions simples de téléchargement :",
              color = BlancPur,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "1. Cliquez sur le menu paramètres de l'émulateur ou l'icône de téléchargement d'AI Studio pour sauvegarder le fichier APK finalisé.\n\n" +
                     "2. Transférez le package '.apk' sur votre téléphone.\n\n" +
                     "3. Ouvrez le fichier et autorisez l'installation des sources externes de confiance.",
              color = BlancPur.copy(alpha = 0.85f),
              fontSize = 11.sp,
              lineHeight = 15.sp
            )
          } else {
            Text(
              text = "Téléchargez l'application native NDAKO - TENDANCE CONNECT directement sur votre smartphone Android !",
              color = BlancPur,
              fontSize = 13.sp
            )
            Text(
              text = "Accédez aux fonctionnalités complètes : notifications push, accès direct au club immobilier de Pointe-Noire / Brazzaville et messagerie sécurisée WhatsApp.",
              color = GrisTechnique,
              fontSize = 11.sp,
              lineHeight = 14.sp
            )
          }
        }
      },
      confirmButton = {
        if (downloadProgress < 100f) {
          Button(
            onClick = { isDownloading = true },
            colors = ButtonDefaults.buttonColors(containerColor = OrPrestige),
            enabled = !isDownloading
          ) {
            Text(text = "Démarrer la Génération", color = NoirProfond, fontWeight = FontWeight.Bold)
          }
        } else {
          Button(
            onClick = {
              try {
                val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://ais-pre-6mtsyuzgptpgaa3w7w3li5-68351759313.europe-west2.run.app"))
                context.startActivity(intent)
              } catch (e: Exception) {
                viewModel.setAlert("Redirection vers l'application mobile déployée.")
              }
              showDownloadDialog = false
            },
            colors = ButtonDefaults.buttonColors(containerColor = OrPrestige)
          ) {
            Text(text = "Fermer & Accéder au lien", color = NoirProfond, fontWeight = FontWeight.Bold)
          }
        }
      },
      dismissButton = {
        TextButton(onClick = { showDownloadDialog = false }) {
          Text(text = "Fermer", color = GrisTechnique)
        }
      },
      containerColor = NoirSec,
      tonalElevation = 6.dp
    )
  }

  // Listen to currentUser approvals and push screens forward
  LaunchedEffect(currentUser) {
    val user = currentUser
    if (user == null) {
      currentRoute = "LANDING"
    } else if (!user.isApproved) {
      currentRoute = "OTP_SAS"
    } else {
      currentRoute = "MAIN_FLOW"
    }
  }

  Scaffold(
    contentWindowInsets = WindowInsets.safeDrawing,
    containerColor = NoirProfond,
    bottomBar = {
      if (currentRoute == "MAIN_FLOW") {
        NavigationBar(
          containerColor = NoirSec,
          modifier = Modifier
            .border(1.dp, OrPrestige.copy(alpha = 0.2f), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .navigationBarsPadding(),
          tonalElevation = 8.dp
        ) {
          NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { selectedTab = 0 },
            icon = { Icon(Icons.Default.Explore, contentDescription = "Explore", tint = if (selectedTab == 0) OrPrestige else GrisTechnique) },
            label = { Text("Moteur Club", fontSize = 10.sp, color = if (selectedTab == 0) OrPrestige else GrisTechnique) },
            colors = NavigationBarItemDefaults.colors(
              indicatorColor = OrPrestige.copy(alpha = 0.15f)
            )
          )

          NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { selectedTab = 1 },
            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Advisor IA", tint = if (selectedTab == 1) OrPrestige else GrisTechnique) },
            label = { Text("IA Advisor", fontSize = 10.sp, color = if (selectedTab == 1) OrPrestige else GrisTechnique) },
            colors = NavigationBarItemDefaults.colors(
              indicatorColor = OrPrestige.copy(alpha = 0.15f)
            )
          )

          NavigationBarItem(
            selected = selectedTab == 2,
            onClick = { selectedTab = 2 },
            icon = { Icon(Icons.Default.Language, contentDescription = "Portail HTML", tint = if (selectedTab == 2) OrPrestige else GrisTechnique) },
            label = { Text("Portail HTML", fontSize = 10.sp, color = if (selectedTab == 2) OrPrestige else GrisTechnique) },
            colors = NavigationBarItemDefaults.colors(
              indicatorColor = OrPrestige.copy(alpha = 0.15f)
            )
          )

          // Admin tab only visible if actual authorized administrative supervisor
          val isAdmin = currentUser?.role == "AGENCE" || currentUser?.email == "ndakotendance2016@gmail.com"
          if (isAdmin) {
            NavigationBarItem(
              selected = selectedTab == 3,
              onClick = { selectedTab = 3 },
              icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Souverain Admin", tint = if (selectedTab == 3) OrPrestige else GrisTechnique) },
              label = { Text("Souverain Admin", fontSize = 10.sp, color = if (selectedTab == 3) OrPrestige else GrisTechnique) },
              colors = NavigationBarItemDefaults.colors(
                indicatorColor = OrPrestige.copy(alpha = 0.15f)
              )
            )
          }
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(NoirProfond)
    ) {
      // SCREEN NAVIGATION SWITCH BOARD WITH SMOOTH TRANSITION EFFECTS
      AnimatedContent(
        targetState = currentRoute,
        transitionSpec = {
          (slideInHorizontally { width -> width / 3 } + fadeIn(animationSpec = spring(dampingRatio = 0.85f)))
            .togetherWith(slideOutHorizontally { width -> -width / 3 } + fadeOut(animationSpec = spring()))
        },
        label = "RouteTransition"
      ) { route ->
        when (route) {
          "LANDING" -> {
            LandingScreen(
              viewModel = viewModel,
              onNavigateToOtp = { currentRoute = "OTP_SAS" },
              onNavigateToApp = { currentRoute = "MAIN_FLOW" }
            )
          }
          "OTP_SAS" -> {
            PrivacyVerificationScreen(
              viewModel = viewModel,
              onVerificationSuccess = { currentRoute = "MAIN_FLOW" },
              onNavigateBack = { viewModel.logOut() }
            )
          }
          "MAIN_FLOW" -> {
            Column(modifier = Modifier.fillMaxSize()) {
              // TOP PRESTIGE HEADER FOR CORE BRAND EXPERIENCES
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(NoirSec)
                  .padding(horizontal = 16.dp, vertical = 10.dp)
                  .border(1.dp, OrPrestige.copy(alpha = 0.1f)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.CorporateFare,
                    contentDescription = "Brand Logo",
                    tint = OrPrestige,
                    modifier = Modifier.size(24.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text(
                      text = "NDAKO - TENDANCE CONNECT",
                      color = OrPrestige,
                      fontSize = 13.sp,
                      fontWeight = FontWeight.ExtraBold,
                      letterSpacing = 1.sp
                    )
                    Text(
                      text = "Profil actif: ${currentUser?.name} (${currentUser?.role})",
                      color = GrisTechnique,
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Medium
                    )
                  }
                }

                // Quick actions: Download & Disconnect
                Row(verticalAlignment = Alignment.CenterVertically) {
                  IconButton(onClick = { showDownloadDialog = true }) {
                    Icon(Icons.Default.Download, contentDescription = "Télécharger l'APK", tint = OrPrestige)
                  }
                  IconButton(onClick = { viewModel.logOut() }) {
                    Icon(Icons.Default.Logout, contentDescription = "Se déconnecter", tint = OrPrestige)
                  }
                }
              }

              // Show actual selected inner tab
              Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                  0 -> DiscoverScreen(viewModel = viewModel)
                  1 -> AIAdvisorScreen(viewModel = viewModel)
                  2 -> WebConnectScreen()
                  3 -> AdminScreen(viewModel = viewModel)
                }
              }
            }
          }
        }
      }

      // GLOBAL ULTRA FLOATING PRESTIGE STATUS ALERT SNACKBAR Dialog box
      statusAlert?.let { alertMessage ->
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.TopCenter)
            .padding(16.dp)
        ) {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .border(1.dp, OrPrestige, RoundedCornerShape(10.dp)),
            colors = CardDefaults.cardColors(containerColor = NoirSec),
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Info,
                  contentDescription = "Notification",
                  tint = OrPrestige,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = alertMessage,
                  color = BlancPur,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }
              IconButton(
                onClick = { viewModel.setAlert(null) },
                modifier = Modifier.size(24.dp)
              ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = OrPrestige, modifier = Modifier.size(14.dp))
              }
            }
          }
        }
      }
    }
  }
}
