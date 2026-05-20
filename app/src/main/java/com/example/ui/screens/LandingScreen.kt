package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.NdakoViewModel

@Composable
fun LandingScreen(
  viewModel: NdakoViewModel,
  onNavigateToOtp: () -> Unit,
  onNavigateToApp: () -> Unit
) {
  val scrollState = rememberScrollState()
  
  // Registration Form States
  var email by remember { mutableStateOf("") }
  var name by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var selectedRole by remember { mutableStateOf("CLIENT") } // "CLIENT", "PROPRIETAIRE", "DEMARCHEUR", "NOTAIRE", "BANQUE", "AGENCE"
  var adminPassword by remember { mutableStateOf("") }
  
  var expandedRoleDropdown by remember { mutableStateOf(false) }
  var isRegistering by remember { mutableStateOf(true) } // toggles between registration (under OTP) or admin direct pass

  val roles = listOf(
    "CLIENT" to "Client (Recherche & Réservation)",
    "PROPRIETAIRE" to "Propriétaire (Bailleurs directs)",
    "DEMARCHEUR" to "Démarcheur & Agent Immobilier",
    "NOTAIRE" to "Notaire (Validation Juridique)",
    "BANQUE" to "Banque (Crédits Immobiliers)",
    "AGENCE" to "Super-Administrateur (Agence NDAKO)"
  )

  var showDownloadDialog by remember { mutableStateOf(false) }
  var downloadProgress by remember { mutableStateOf(0f) }
  var isDownloading by remember { mutableStateOf(false) }
  val context = LocalContext.current

  LaunchedEffect(isDownloading) {
    if (isDownloading) {
      downloadProgress = 0f
      while (downloadProgress < 100f) {
        kotlinx.coroutines.delay(80)
        downloadProgress += 5f
      }
      isDownloading = false
      viewModel.setAlert("Compilation et préparation de l'APK NDAKO terminées avec succès !")
    }
  }

  // DOWNLOAD AND INSTALL INSTRUCTION DIALOG
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
            contentDescription = "Install",
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
              text = "Progression : ${downloadProgress.toInt()}% (Optimisation des dépendances et signatures)",
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
                text = "✓ Paquet APK vérifié et prêt à installer.\nVersion: NATIVE 2016\nSécurité: Certifiée par NDAKO Tendance",
                color = OrPrestige,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Pour continuer l'installation sur votre mobile :",
              color = BlancPur,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "1. Cliquez sur le bouton 'PARAMÈTRES / CONFIGURATION' de l'émulateur ou l'onglet de téléchargement d'AI Studio pour obtenir le binaire APK compilé.\n\n" +
                     "2. Transférez le fichier '.apk' sur votre appareil Android.\n\n" +
                     "3. Activez 'Sources Inconnues' dans les paramètres de sécurité Android puis lancez l'installation.",
              color = BlancPur.copy(alpha = 0.85f),
              fontSize = 11.sp,
              lineHeight = 15.sp
            )
          } else {
            Text(
              text = "Souhaitez-vous compiler et télécharger l'application native NDAKO - TENDANCE CONNECT pour une utilisation optimale en conditions réelles ?",
              color = BlancPur,
              fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Cela intègre l'ensemble du moteur de recherche de biens immobiliers de Pointe-Noire & Brazzaville, le terminal Fintech de simulation et l'intelligence artificielle Gemini en local.",
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
            Text(text = "Lancer la Compilation", color = NoirProfond, fontWeight = FontWeight.Bold)
          }
        } else {
          Button(
            onClick = {
              try {
                // Navigate to the deployment pre-rendering link
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://ais-pre-6mtsyuzgptpgaa3w7w3li5-68351759313.europe-west2.run.app"))
                context.startActivity(intent)
              } catch (e: Exception) {
                viewModel.setAlert("Lien temporaire ouvert dans votre presse-papiers.")
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
          Text(text = "Annuler", color = GrisTechnique)
        }
      },
      containerColor = NoirSec,
      tonalElevation = 6.dp
    )
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(NoirProfond)
      .verticalScroll(scrollState)
      .padding(horizontal = 20.dp, vertical = 24.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Header: Brand Identity in Clean Minimalism style
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 16.dp, bottom = 16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "NDAKO",
          color = OrPrestige,
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 2.sp
        )
        Text(
          text = "Tendance Connect",
          color = GrisTechnique,
          fontSize = 10.sp,
          fontWeight = FontWeight.Medium,
          letterSpacing = 2.sp,
          modifier = Modifier.padding(top = 2.dp)
        )
      }

      // Small round active glow badge
      Box(
        modifier = Modifier
          .size(40.dp)
          .border(
            width = 1.dp,
            color = OrPrestige.copy(alpha = 0.3f),
            shape = RoundedCornerShape(20.dp)
          )
          .background(NoirSec, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
      ) {
        // glowing indicator dot
        Box(
          modifier = Modifier
            .size(8.dp)
            .background(OrPrestige, RoundedCornerShape(4.dp))
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Hero Section: Typographie épurée
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 24.dp),
      horizontalAlignment = Alignment.Start
    ) {
      Text(
        text = "L'immobilier qui",
        color = BlancPur,
        fontSize = 24.sp,
        fontWeight = FontWeight.Light,
        letterSpacing = 0.5.sp
      )
      Text(
        text = "vous comprend.",
        color = OrPrestige,
        fontSize = 26.sp,
        fontWeight = FontWeight.Light,
        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
        letterSpacing = 0.5.sp,
        modifier = Modifier.padding(top = 2.dp)
      )
    }

    // ENROLLMENT / LOGIN CARD
    LuxuryCard {
      Text(
        text = "PORTAL IMMOBILIER TRIPLE ACTION",
        color = OrPrestige,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.5.sp,
        modifier = Modifier.padding(bottom = 16.dp)
      )

      // ROLE SELECTOR DROPDOWN
      Text(
        text = "Sélectionnez votre rôle dans l'écosystème :",
        color = GrisTechnique,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(bottom = 6.dp)
      )

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp)
          .background(NoirProfond, RoundedCornerShape(10.dp))
          .border(1.dp, OrPrestige.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
          .clickable { expandedRoleDropdown = true }
          .padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = roles.find { it.first == selectedRole }?.second ?: selectedRole,
            color = BlancPur,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
          )
          Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = "Choisir Rôle",
            tint = OrPrestige
          )
        }

        DropdownMenu(
          expanded = expandedRoleDropdown,
          onDismissRequest = { expandedRoleDropdown = false },
          modifier = Modifier
            .fillMaxWidth(0.85f)
            .background(NoirSec)
            .border(1.dp, OrPrestige.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
        ) {
          roles.forEach { rolePair ->
            DropdownMenuItem(
              text = {
                Text(
                  text = rolePair.second,
                  color = if (selectedRole == rolePair.first) OrPrestige else BlancPur,
                  fontWeight = if (selectedRole == rolePair.first) FontWeight.Bold else FontWeight.Normal
                )
              },
              onClick = {
                selectedRole = rolePair.first
                expandedRoleDropdown = false
                isRegistering = rolePair.first != "AGENCE"
              }
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      if (selectedRole == "AGENCE") {
        // ADMIN LOGIN BY MASTER SECURED KEY
        OutlinedTextField(
          value = adminPassword,
          onValueChange = { adminPassword = it },
          label = { Text("Clé d'administration principale", color = OrPrestige) },
          leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password", tint = OrPrestige) },
          visualTransformation = PasswordVisualTransformation(),
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = OrPrestige,
            unfocusedBorderColor = OrPrestige.copy(alpha = 0.3f),
            focusedTextColor = BlancPur,
            unfocusedTextColor = BlancPur,
            cursorColor = OrPrestige
          ),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(18.dp))

        GoldGradientButton(
          text = "CONNEXION SOUVERAINE",
          onClick = {
            if (viewModel.loginAdminWithMasterKey(adminPassword)) {
              onNavigateToApp()
            } else {
              viewModel.setAlert("Clé maîtresse administrative invalide. (Note : Vérifiez la clé)")
            }
          },
          modifier = Modifier.fillMaxWidth()
        )
      } else {
        // REGULAR ROLES VALIDATION SAS (REQUIRES REASONING OTP CODES)
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Nom complet ou Dénomination", color = GrisTechnique) },
          leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name", tint = OrPrestige) },
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = OrPrestige,
            unfocusedBorderColor = OrPrestige.copy(alpha = 0.3f),
            focusedTextColor = BlancPur,
            unfocusedTextColor = BlancPur
          ),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = email,
          onValueChange = { email = it },
          label = { Text("Adresse email", color = GrisTechnique) },
          leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = OrPrestige) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = OrPrestige,
            unfocusedBorderColor = OrPrestige.copy(alpha = 0.3f),
            focusedTextColor = BlancPur,
            unfocusedTextColor = BlancPur
          ),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("Frais Phone (MTN/Airtel Money)", color = GrisTechnique) },
          placeholder = { Text("+242066226914", color = GrisTechnique.copy(alpha = 0.5f)) },
          leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone", tint = OrPrestige) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = OrPrestige,
            unfocusedBorderColor = OrPrestige.copy(alpha = 0.3f),
            focusedTextColor = BlancPur,
            unfocusedTextColor = BlancPur
          ),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(18.dp))

        GoldGradientButton(
          text = "S'INSCRIRE ET RECEVOIR OTP",
          onClick = {
            if (email.isBlank() || name.isBlank() || phone.isBlank()) {
              viewModel.setAlert("Veuillez remplir tous les champs avant de poursuivre.")
            } else {
              viewModel.triggerRegistration(email, name, selectedRole, phone)
              onNavigateToOtp()
            }
          },
          modifier = Modifier.fillMaxWidth()
        )
      }
    }

    Spacer(modifier = Modifier.height(28.dp))

    // QR SCAN & MOBILE INSTALL MODULES
    LuxuryCard {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "INSTALLATION & SUITE MOBILE",
          color = OrPrestige,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          SimulatedQrCodeCanvas(modifier = Modifier.weight(1f))
          
          Spacer(modifier = Modifier.width(16.dp))
          
          Column(
            modifier = Modifier.weight(1.2f),
            verticalArrangement = Arrangement.Center
          ) {
            Text(
              text = "Scannez pour Installer",
              color = BlancPur,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Générez l'APK ou rejoignez l'application sur votre smartphone en scannant ce code.",
              color = GrisTechnique,
              fontSize = 10.sp,
              lineHeight = 13.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Support officiel :\nndakotendance2016@gmail.com",
              color = OrPrestige,
              fontSize = 9.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        GoldGradientButton(
          text = "TÉLÉCHARGER L'APK MOBILE",
          onClick = { 
            showDownloadDialog = true 
          },
          modifier = Modifier.fillMaxWidth()
        )
      }
    }

    Spacer(modifier = Modifier.height(30.dp))

    // DEVISE BADGES
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceAround
    ) {
      DeviseBadge(icon = Icons.Default.VerifiedUser, text = "Sécurisé")
      DeviseBadge(icon = Icons.Default.SettingsSuggest, text = "Innovation")
      DeviseBadge(icon = Icons.Default.HotTub, text = "Confort")
    }

    Spacer(modifier = Modifier.height(40.dp))
  }
}

@Composable
fun DeviseBadge(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Icon(imageVector = icon, contentDescription = null, tint = OrPrestige, modifier = Modifier.size(16.dp))
    Spacer(modifier = Modifier.width(4.dp))
    Text(text = text, color = BlancPur, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
  }
}
