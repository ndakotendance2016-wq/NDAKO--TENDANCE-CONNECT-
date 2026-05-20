package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Property
import com.example.ui.theme.*
import com.example.viewmodel.NdakoViewModel
import kotlinx.coroutines.launch

@Composable
fun DiscoverScreen(
  viewModel: NdakoViewModel
) {
  val context = LocalContext.current
  val properties by viewModel.propertiesList.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()
  
  // Search parameters
  var searchQuery by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("Tous") }
  var showOnlyAvailable by remember { mutableStateOf(true) }

  // New Property publishing states
  var showPublishDialog by remember { mutableStateOf(false) }
  var newTitle by remember { mutableStateOf("") }
  var newDescription by remember { mutableStateOf("") }
  var newPrice by remember { mutableStateOf("") }
  var newCategory by remember { mutableStateOf("Location Standard") }
  var newLocation by remember { mutableStateOf("Brazzaville") }

  // Action Popup Dialog for Bank/Notary transfers
  var activePropertyForAction by remember { mutableStateOf<Property?>(null) }
  var bankRequestDialog by remember { mutableStateOf(false) }
  var notaryRequestDialog by remember { mutableStateOf(false) }
  var amountToBorrow by remember { mutableStateOf("") }
  var notaryComments by remember { mutableStateOf("") }

  val categories = listOf("Tous", "Location Standard", "Location Saisonnière", "Location Journalière", "Vente")

  // Filter listings according to query limits and instant-availability block
  val filteredProperties = properties.filter { prop ->
    val locationMatch = prop.location.contains(searchQuery, ignoreCase = true) || 
                        prop.title.contains(searchQuery, ignoreCase = true)
    val categoryMatch = selectedCategory == "Tous" || prop.category == selectedCategory
    val availabilityMatch = !showOnlyAvailable || prop.status == "DISPONIBLE"
    locationMatch && categoryMatch && availabilityMatch
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(NoirProfond)
  ) {
    // Elegant luxury Search and filter bar
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(NoirSec)
        .padding(horizontal = 16.dp, vertical = 12.dp)
        .border(
          width = 1.dp,
          brush = Brush.verticalGradient(listOf(OrPrestige.copy(alpha = 0.3f), Color.Transparent)),
          shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
        )
    ) {
      Text(
        text = "MOTEUR DE SÉLECTION INTÉLLIGENT",
        color = OrPrestige,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 8.dp)
      )

      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Rechercher Brazzaville, Pointe-Noire, Côte Sauvage...", color = GrisTechnique.copy(alpha = 0.6f)) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Rechercher", tint = OrPrestige) },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { searchQuery = "" }) {
              Icon(Icons.Default.Clear, contentDescription = "Clear", tint = OrPrestige)
            }
          }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = OrPrestige,
          unfocusedBorderColor = OrPrestige.copy(alpha = 0.3f),
          focusedTextColor = BlancPur,
          unfocusedTextColor = BlancPur
        ),
        singleLine = true,
        shape = RoundedCornerShape(10.dp)
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Category filter horizontal selector
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        categories.forEach { cat ->
          val isSelected = selectedCategory == cat
          Box(
            modifier = Modifier
              .background(
                brush = Brush.horizontalGradient(
                  if (isSelected) listOf(OrMuted, OrPrestige)
                  else listOf(NoirProfond, NoirProfond)
                ),
                shape = RoundedCornerShape(20.dp)
              )
              .border(
                width = 1.dp,
                color = if (isSelected) Color.Transparent else OrPrestige.copy(alpha = 0.3f),
                shape = RoundedCornerShape(20.dp)
              )
              .clickable { selectedCategory = cat }
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text(
              text = cat,
              color = if (isSelected) NoirProfond else BlancPur,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Realtime Availability Switch to prevent empty/dead searches
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.FilterList,
            contentDescription = "Availability Filter",
            tint = OrPrestige,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Masquer les biens occupés (Temps Réel)",
            color = GrisTechnique,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
          )
        }
        Switch(
          checked = showOnlyAvailable,
          onCheckedChange = { showOnlyAvailable = it },
          colors = SwitchDefaults.colors(
            checkedThumbColor = OrPrestige,
            checkedTrackColor = OrMuted,
            uncheckedThumbColor = GrisTechnique,
            uncheckedTrackColor = NoirProfond
          )
        )
      }
    }

    // MAIN DIRECTORY LISTING OR PLACEHOLDER EXPANSIONS
    if (filteredProperties.isEmpty()) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Icon(
          imageVector = Icons.Default.MapsHomeWork,
          contentDescription = "Aucun bien",
          tint = OrPrestige.copy(alpha = 0.4f),
          modifier = Modifier.size(60.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = "Aucun Bien Disponible",
          color = OrPrestige,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Affinez vos filtres ou basculez le bouton de disponibilité temps réel pour voir les biens occupés ou en travaux.",
          color = GrisTechnique,
          fontSize = 12.sp,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(top = 4.dp)
        )
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        items(filteredProperties) { prop ->
          PropertyCardItem(
            property = prop,
            canModify = viewModel.canModifyProperty(prop),
            currentRole = currentUser?.role ?: "CLIENT",
            onToggleStatus = { next -> viewModel.togglePropertyAvailability(prop, next) },
            onDelete = { viewModel.deleteProperty(prop) },
            onWhatsAppContact = {
              viewModel.trackWhatsAppRedirection(prop) { link ->
                try {
                  val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                  context.startActivity(intent)
                } catch (e: Exception) {
                  viewModel.setAlert("Redirection WhatsApp simulée (Lien: $link)")
                }
              }
            },
            onBankAction = {
              activePropertyForAction = prop
              bankRequestDialog = true
            },
            onNotaryAction = {
              activePropertyForAction = prop
              notaryRequestDialog = true
            }
          )
        }
      }
    }

    // PUBLISH ACTION BUTTON - Visible for owners, agents, and agency administrators
    val userRole = currentUser?.role ?: "CLIENT"
    if (userRole != "CLIENT" && userRole != "BANQUE" && userRole != "NOTAIRE") {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        contentAlignment = Alignment.Center
      ) {
        Button(
          onClick = { showPublishDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = OrPrestige),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Default.AddHome, contentDescription = "Add", tint = NoirProfond)
          Spacer(modifier = Modifier.width(8.dp))
          Text("PUBLIER UN NOUVEAU BIEN", color = NoirProfond, fontWeight = FontWeight.Bold)
        }
      }
    }

    // --- DIALOGS FOR PUBLISHING ---
    if (showPublishDialog) {
      AlertDialog(
        onDismissRequest = { showPublishDialog = false },
        containerColor = NoirSec,
        title = { Text("PUBLIER UN BIEN", color = OrPrestige, fontWeight = FontWeight.Bold) },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
              value = newTitle,
              onValueChange = { newTitle = it },
              label = { Text("Titre de l'annonce", color = GrisTechnique) },
              colors = OutlinedTextFieldDefaults.colors(focusedTextColor = BlancPur, unfocusedTextColor = BlancPur),
              singleLine = true
            )
            OutlinedTextField(
              value = newDescription,
              onValueChange = { newDescription = it },
              label = { Text("Description complète", color = GrisTechnique) },
              colors = OutlinedTextFieldDefaults.colors(focusedTextColor = BlancPur, unfocusedTextColor = BlancPur)
            )
            OutlinedTextField(
              value = newPrice,
              onValueChange = { newPrice = it },
              label = { Text("Prix / Loyer (USD)", color = GrisTechnique) },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              colors = OutlinedTextFieldDefaults.colors(focusedTextColor = BlancPur, unfocusedTextColor = BlancPur),
              singleLine = true
            )
            // Hard selection category
            Text("Catégorie de bien:", color = OrPrestige, fontSize = 12.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              listOf("Location Standard", "Location Saisonnière", "Vente").forEach { scat ->
                FilterChip(
                  selected = newCategory == scat,
                  onClick = { newCategory = scat },
                  label = { Text(scat, fontSize = 9.sp) },
                  colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = OrPrestige,
                    selectedLabelColor = NoirProfond,
                    labelColor = BlancPur
                  )
                )
              }
            }
            OutlinedTextField(
              value = newLocation,
              onValueChange = { newLocation = it },
              placeholder = { Text("ex. Pointe-Noire - Côte Sauvage", color = GrisTechnique) },
              label = { Text("Localisation et Ville", color = GrisTechnique) },
              colors = OutlinedTextFieldDefaults.colors(focusedTextColor = BlancPur, unfocusedTextColor = BlancPur),
              singleLine = true
            )
          }
        },
        confirmButton = {
          Button(
            onClick = {
              val parsedPrice = newPrice.toDoubleOrNull() ?: 0.0
              if (newTitle.isNotBlank() && parsedPrice > 0.0) {
                viewModel.addNewProperty(newTitle, newDescription, parsedPrice, newCategory, newLocation)
                showPublishDialog = false
                newTitle = ""
                newDescription = ""
                newPrice = ""
              } else {
                viewModel.setAlert("Champs requis invalides.")
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = OrPrestige)
          ) {
            Text("Enregistrer", color = NoirProfond)
          }
        },
        dismissButton = {
          TextButton(onClick = { showPublishDialog = false }) {
            Text("Annuler", color = OrPrestige)
          }
        }
      )
    }

    // --- BANK LOAN CREATION REQUEST ---
    if (bankRequestDialog && activePropertyForAction != null) {
      val prop = activePropertyForAction!!
      AlertDialog(
        onDismissRequest = { bankRequestDialog = false },
        containerColor = NoirSec,
        title = { Text("DEMANDE DE FINANCEMENT (CONGO SG)", color = OrPrestige, fontWeight = FontWeight.Bold) },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Vous sollicitez un emprunt immobilier pour :", color = GrisTechnique, fontSize = 11.sp)
            Text(prop.title, color = BlancPur, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("Prix global: ${prop.price} USD. Indiquez le montant de l'apport ou de l'emprunt sollicité :", color = GrisTechnique, fontSize = 11.sp)
            
            OutlinedTextField(
              value = amountToBorrow,
              onValueChange = { amountToBorrow = it },
              label = { Text("Montant réclamé (USD / FCFA equivalent)", color = GrisTechnique) },
              colors = OutlinedTextFieldDefaults.colors(focusedTextColor = BlancPur, unfocusedTextColor = BlancPur),
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              singleLine = true
            )
          }
        },
        confirmButton = {
          Button(
            onClick = {
              val amt = amountToBorrow.toDoubleOrNull() ?: 0.0
              if (amt > 0) {
                viewModel.submitToBank(prop, amt, "banque1@ndako.com")
                bankRequestDialog = false
                amountToBorrow = ""
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = OrPrestige)
          ) {
            Text("Transmettre le Dossier", color = NoirProfond)
          }
        },
        dismissButton = {
          TextButton(onClick = { bankRequestDialog = false }) {
            Text("Fermer", color = OrPrestige)
          }
        }
      )
    }

    // --- NOTARY VALIDATION REQUEST ---
    if (notaryRequestDialog && activePropertyForAction != null) {
      val prop = activePropertyForAction!!
      AlertDialog(
        onDismissRequest = { notaryRequestDialog = false },
        containerColor = NoirSec,
        title = { Text("ÉTUDE NOTARIALE - ACTE DE VENTE", color = OrPrestige, fontWeight = FontWeight.Bold) },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Soumettre le bien à l'étude juridique notariale de Me. Antoine Makosso pour rédaction d'acte de transfert.", color = GrisTechnique, fontSize = 11.sp)
            Text(prop.title, color = BlancPur, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            
            OutlinedTextField(
              value = notaryComments,
              onValueChange = { notaryComments = it },
              label = { Text("Instructions juridiques particulières", color = GrisTechnique) },
              colors = OutlinedTextFieldDefaults.colors(focusedTextColor = BlancPur, unfocusedTextColor = BlancPur),
              singleLine = false
            )
          }
        },
        confirmButton = {
          Button(
            onClick = {
              viewModel.submitToNotary(prop, notaryComments, "notaire1@ndako.com")
              notaryRequestDialog = false
              notaryComments = ""
            },
            colors = ButtonDefaults.buttonColors(containerColor = OrPrestige)
          ) {
            Text("Soumettre l'Acte", color = NoirProfond)
          }
        },
        dismissButton = {
          TextButton(onClick = { notaryRequestDialog = false }) {
            Text("Retour", color = OrPrestige)
          }
        }
      )
    }
  }
}

@Composable
fun PropertyCardItem(
  property: Property,
  canModify: Boolean,
  currentRole: String,
  onToggleStatus: (String) -> Unit,
  onDelete: () -> Unit,
  onWhatsAppContact: () -> Unit,
  onBankAction: () -> Unit,
  onNotaryAction: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .border(
        width = 1.dp,
        color = if (property.status == "DISPONIBLE") OrPrestige.copy(alpha = 0.2f) else RougeAlerte.copy(alpha = 0.25f),
        shape = RoundedCornerShape(24.dp)
      ),
    colors = CardDefaults.cardColors(containerColor = NoirSec),
    shape = RoundedCornerShape(24.dp)
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      // CARD IMAGE RECONSTRUCTION
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Gradient architectural image thumbnail
        Box(
          modifier = Modifier
            .size(60.dp)
            .background(
              brush = Brush.verticalGradient(listOf(Color(0xFF2A2A2A), NoirProfond)),
              shape = RoundedCornerShape(16.dp)
            )
            .border(1.dp, OrPrestige.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = when (property.category) {
              "Vente" -> "🏰"
              "Location Saisonnière" -> "🏖️"
              "Location Journalière" -> "⛺"
              else -> "🏢"
            },
            fontSize = 24.sp
          )
        }

        Column(modifier = Modifier.weight(1f)) {
          // CATEGORY AND LOCATION BADGE
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
              modifier = Modifier
                .background(OrPrestige.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(text = property.category, color = OrPrestige, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            }

            Box(
              modifier = Modifier
                .background(NoirProfond, RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(text = property.location, color = BlancPur, fontSize = 8.sp, fontWeight = FontWeight.Normal)
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = property.title,
            color = BlancPur,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
        }

        // PRICE OR LEASE TAG
        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "${property.price.toInt()} USD",
            color = OrPrestige,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = if (property.category == "Vente") "Frais uniques" else "Par mois",
            color = GrisTechnique,
            fontSize = 8.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = property.description,
        color = BlancPur.copy(alpha = 0.9f),
        fontSize = 11.sp,
        lineHeight = 15.sp,
        maxLines = 3
      )

      Spacer(modifier = Modifier.height(8.dp))
      
      // WhatsApp Click and owner stats
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "Publié par : ${property.ownerName}",
          color = GrisTechnique,
          fontSize = 9.sp,
          fontWeight = FontWeight.SemiBold
        )
        Text(
          text = "Intermédiation : ${property.whatsAppClicks} Clics",
          color = OrPrestige,
          fontSize = 9.sp
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // STATUS ALERTER LAYOUT
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(NoirProfond, RoundedCornerShape(8.dp))
          .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(
                when (property.status) {
                  "DISPONIBLE" -> VertOk
                  "OCCUPÉ" -> RougeAlerte
                  else -> GrisTechnique
                }
              )
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "STATUT : [${property.status}]",
            color = when (property.status) {
              "DISPONIBLE" -> VertOk
              "OCCUPÉ" -> RougeAlerte
              else -> GrisTechnique
            },
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
          )
        }

        // Available actions based on roles
        if (canModify) {
          Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(
              onClick = { onToggleStatus("DISPONIBLE") },
              modifier = Modifier.size(28.dp)
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = "Disponible", tint = VertOk, modifier = Modifier.size(18.dp))
            }
            IconButton(
              onClick = { onToggleStatus("OCCUPÉ") },
              modifier = Modifier.size(28.dp)
            ) {
              Icon(Icons.Default.RemoveCircle, contentDescription = "Occupé", tint = RougeAlerte, modifier = Modifier.size(18.dp))
            }
            IconButton(
              onClick = { onToggleStatus("EN_TRAVAUX") },
              modifier = Modifier.size(28.dp)
            ) {
              Icon(Icons.Default.Build, contentDescription = "Travaux", tint = GrisTechnique, modifier = Modifier.size(18.dp))
            }
            IconButton(
              onClick = onDelete,
              modifier = Modifier.size(28.dp)
            ) {
              Icon(Icons.Default.DeleteForever, contentDescription = "Supprimer", tint = RougeAlerte, modifier = Modifier.size(18.dp))
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // INTERACTIVE ACTION PANEL
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        if (property.status == "DISPONIBLE") {
          // CONTACT WHATSAPP TUNNELED REDIRECTION
          Button(
            onClick = onWhatsAppContact,
            colors = ButtonDefaults.buttonColors(containerColor = VertOk),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1.5f)
          ) {
            Icon(Icons.Default.ContactPhone, contentDescription = "WhatsApp", tint = BlancPur, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("RESERVER VIA WHATSAPP", color = BlancPur, fontSize = 9.sp, fontWeight = FontWeight.Bold)
          }

          // Auxiliary Actions (Simulated Bank Loan submissions and Notary Deed request)
          IconButton(
            onClick = onBankAction,
            modifier = Modifier
              .background(NoirProfond, RoundedCornerShape(8.dp))
              .border(1.dp, OrPrestige.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
              .size(40.dp)
          ) {
            Icon(Icons.Default.AccountBalance, contentDescription = "Bank", tint = OrPrestige)
          }

          IconButton(
            onClick = onNotaryAction,
            modifier = Modifier
              .background(NoirProfond, RoundedCornerShape(8.dp))
              .border(1.dp, OrPrestige.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
              .size(40.dp)
          ) {
            Icon(Icons.Default.Gavel, contentDescription = "Notary", tint = OrPrestige)
          }
        } else {
          // LOCK NOTIFICATION BLOCK IF MARKED AS OCCUPIED [Disponibilité instantanée blocking client actions]
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(RougeAlerte.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
              .border(1.dp, RougeAlerte.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
              .padding(10.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Lock, contentDescription = "Locked", tint = RougeAlerte, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "INDISPONIBLE : CE BIEN EST OCCUPÉ OU EN TRAVAUX",
                color = RougeAlerte,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
              )
            }
          }
        }
      }
    }
  }
}
