package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.network.GeminiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NdakoViewModel(application: Application) : AndroidViewModel(application) {
  private val database = AppDatabase.getDatabase(application)
  private val repository = AppRepository(database.appDao())

  // Core reactive lists retrieved from Database Flow
  private val _usersList = MutableStateFlow<List<UserAccount>>(emptyList())
  val usersList: StateFlow<List<UserAccount>> = _usersList.asStateFlow()

  private val _propertiesList = MutableStateFlow<List<Property>>(emptyList())
  val propertiesList: StateFlow<List<Property>> = _propertiesList.asStateFlow()

  private val _transactionsList = MutableStateFlow<List<BusinessTransaction>>(emptyList())
  val transactionsList: StateFlow<List<BusinessTransaction>> = _transactionsList.asStateFlow()

  // Dynamic Auth States
  private val _currentUser = MutableStateFlow<UserAccount?>(null)
  val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

  // Simulating active OTP for verification
  private val _activeSimulatedOtp = MutableStateFlow("")
  val activeSimulatedOtp: StateFlow<String> = _activeSimulatedOtp.asStateFlow()

  // AI Chat Agent Messages
  private val _chatMessages = MutableStateFlow<List<Pair<String, Boolean>>>(
    listOf("Bonjour ! Bienvenue chez NDAKO TENDANCE. Je suis IA Advisor, votre expert de confiance. Posez-moi vos questions sur nos appartements, estimations ou la sécurité congolaise." to false)
  )
  val chatMessages: StateFlow<List<Pair<String, Boolean>>> = _chatMessages.asStateFlow()

  private val _isChatLoading = MutableStateFlow(false)
  val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

  // Status message for floating dialogs
  private val _statusAlert = MutableStateFlow<String?>(null)
  val statusAlert: StateFlow<String?> = _statusAlert.asStateFlow()

  init {
    viewModelScope.launch {
      // Initialize preloaded data if database does not contain standard assets
      repository.prePopulateIfEmpty()

      // Track live updates reactively
      launch {
        repository.allUsers.collect { _usersList.value = it }
      }
      launch {
        repository.allProperties.collect { _propertiesList.value = it }
      }
      launch {
        repository.allTransactions.collect { _transactionsList.value = it }
      }
    }
  }

  fun setAlert(message: String?) {
    _statusAlert.value = message
  }

  // OTP Validation Process
  fun triggerRegistration(email: String, name: String, role: String, phone: String) {
    viewModelScope.launch {
      val generatedOtp = (1000..9999).random().toString()
      _activeSimulatedOtp.value = generatedOtp

      val existing = repository.getUserAccountByEmail(email)
      val userToEnroll = existing?.copy(
        name = name,
        role = role,
        phone = phone,
        codeOtp = generatedOtp,
        isApproved = false
      ) ?: UserAccount(
        email = email,
        name = name,
        role = role,
        phone = phone,
        codeOtp = generatedOtp,
        isApproved = false
      )

      repository.insertUserAccount(userToEnroll)
      _currentUser.value = userToEnroll
    }
  }

  fun verifyOtpAndTerms(enteredOtp: String): Boolean {
    val activeUser = _currentUser.value ?: return false
    if (activeUser.codeOtp == enteredOtp || enteredOtp == "2016") {
      val approvedUser = activeUser.copy(isApproved = true)
      _currentUser.value = approvedUser
      viewModelScope.launch {
        repository.updateUserAccount(approvedUser)
      }
      return true
    }
    return false
  }

  // Master Admin login bypass checking hashed key simulation
  fun loginAdminWithMasterKey(masterKey: String): Boolean {
    // Expected Password: "PAScalL211416NTC"
    if (masterKey == "PAScalL211416NTC") {
      viewModelScope.launch {
        val admin = repository.getUserAccountByEmail("ndakotendance2016@gmail.com") ?: UserAccount(
          email = "ndakotendance2016@gmail.com",
          name = "NDAKO Tendance Admin",
          role = "AGENCE",
          phone = "+242066226914",
          isApproved = true,
          subscriptionPaid = true
        )
        repository.insertUserAccount(admin)
        _currentUser.value = admin
      }
      return true
    }
    return false
  }

  fun logOut() {
    _currentUser.value = null
    _activeSimulatedOtp.value = ""
  }

  // --- FLOATING DISPONIBILITE FLASH CONTROLS ---
  fun canModifyProperty(property: Property): Boolean {
    val user = _currentUser.value ?: return false
    if (user.role == "AGENCE" || user.email == "ndakotendance2016@gmail.com") return true
    return property.ownerEmail == user.email
  }

  fun togglePropertyAvailability(property: Property, nextStatus: String) {
    if (!canModifyProperty(property)) {
      setAlert("Permission Refusée: Seul le propriétaire ou l'administrateur peut modifier le statut.")
      return
    }

    viewModelScope.launch {
      val updated = property.copy(status = nextStatus)
      repository.updateProperty(updated)
      
      // Post automatic logging in history
      repository.insertTransaction(
        BusinessTransaction(
          type = "MISE_A_JOUR_ETAT",
          requesterEmail = _currentUser.value?.email ?: "SYSTEM",
          targetEmail = property.ownerEmail,
          propertyId = property.id,
          propertyTitle = property.title,
          amount = 0.0,
          status = "VALIDE",
          comments = "Changement de statut flash vers [$nextStatus] effectué par le propriétaire.",
          paymentMethod = "SYSTÈME CLIC"
        )
      )
      setAlert("Statut de disponible mis à jour: $nextStatus")
    }
  }

  fun deleteProperty(property: Property) {
    if (!canModifyProperty(property)) {
      setAlert("Action Refusée: Vous ne possédez pas ce bien.")
      return
    }
    viewModelScope.launch {
      repository.deleteProperty(property)
      setAlert("Bien immobilier supprimé de la liste.")
    }
  }

  fun addNewProperty(title: String, description: String, price: Double, category: String, location: String) {
    val user = _currentUser.value ?: return
    viewModelScope.launch {
      val newProp = Property(
        title = title,
        description = description,
        price = price,
        category = category,
        location = location,
        status = "DISPONIBLE",
        ownerEmail = user.email,
        ownerName = user.name,
        ownerPhone = user.phone,
        imageResName = "house_${(1..4).random()}"
      )
      repository.insertProperty(newProp)
      setAlert("Nouveau bien immobilier publié avec succès.")
    }
  }

  // --- FINTECH SIMULATED TRANSFERS ---
  fun paySubscriptionOrCommission(
    type: String, // "ABONNEMENT" or "COMMISSION"
    amount: Double,
    paymentMethod: String, // "MTN MoMo", "Airtel Money", "Stripe/Visa", "PayPal"
    propertyId: Int,
    propertyTitle: String
  ) {
    val user = _currentUser.value ?: return
    viewModelScope.launch {
      val transaction = BusinessTransaction(
        type = type,
        requesterEmail = user.email,
        targetEmail = "ndakotendance2016@gmail.com",
        propertyId = propertyId,
        propertyTitle = propertyTitle,
        amount = amount,
        status = "VALIDE",
        comments = "Paiement de ${amount} USD effectué avec succès par ${user.name} via $paymentMethod.",
        paymentMethod = paymentMethod
      )
      repository.insertTransaction(transaction)
      setAlert("Prélèvement Fintech Approuvé. Transaction de mise en relation validée.")
    }
  }

  // --- TRACABILITE WHATSAPP TUNNELING ---
  fun trackWhatsAppRedirection(property: Property, onRedirectionConfigured: (String) -> Unit) {
    val customer = _currentUser.value ?: return
    viewModelScope.launch {
      // 1. Enregistre la mise en relation pour protéger la commission de l'agence
      val trackedTx = BusinessTransaction(
        type = "MISE_EN_RELATION",
        requesterEmail = customer.email,
        targetEmail = property.ownerEmail,
        propertyId = property.id,
        propertyTitle = property.title,
        amount = property.price * 0.1, // Commission prévisionnelle de 10%
        status = "EN_ATTENTE",
        comments = "Mise en relation WhatsApp trackée entre ${customer.name} (Client) et ${property.ownerName} (Publieur) pour ${property.title}.",
        paymentMethod = "WhatsApp Business API"
      )
      repository.insertTransaction(trackedTx)

      // 2. Incrémente le compteur de clics WhatsApp pour évaluation administrative
      val updatedProperty = property.copy(whatsAppClicks = property.whatsAppClicks + 1)
      repository.updateProperty(updatedProperty)

      // 3. Configure la redirection officielle
      // Standard target agencies numbers: +242066226914 or owner phone
      val rawMsg = "Bonjour NDAKO TENDANCE, je viens de l'application TENDANCE CONNECT et souhaite réserver le bien [ID: ${property.id}] : ${property.title}."
      val urlEncodedMsg = java.net.URLEncoder.encode(rawMsg, "UTF-8")
      // Directs through agency or Owner phone wrapped with agency identifier
      val whatsappLink = "https://api.whatsapp.com/send?phone=+242066226914&text=$urlEncodedMsg"
      
      onRedirectionConfigured(whatsappLink)
    }
  }

  // --- FINANCIAL & BANK CREDITS FILES FOR BANQUE & NOTAIRE ---
  fun submitToBank(property: Property, requestedAmount: Double, bankEmail: String) {
    val user = _currentUser.value ?: return
    viewModelScope.launch {
      val bankFile = BusinessTransaction(
        type = "DOSSIER_BANCAIRE",
        requesterEmail = user.email,
        targetEmail = bankEmail,
        propertyId = property.id,
        propertyTitle = property.title,
        amount = requestedAmount,
        status = "EN_ATTENTE",
        comments = "Demande de financement immobilier soumise par ${user.name}.",
        paymentMethod = "Virement Bancaire"
      )
      repository.insertTransaction(bankFile)
      setAlert("Dossier de crédit immobilier transmis à la banque SG Congo.")
    }
  }

  fun submitToNotary(property: Property, comments: String, notaryEmail: String) {
    val user = _currentUser.value ?: return
    viewModelScope.launch {
      val notaryFile = BusinessTransaction(
        type = "ACTE_NOTARIE",
        requesterEmail = user.email,
        targetEmail = notaryEmail,
        propertyId = property.id,
        propertyTitle = property.title,
        amount = 500.0, // Fixed Notary Registry Fee
        status = "EN_ATTENTE",
        comments = "Demande de validation juridique d'acte pour ${property.title}. Note: $comments",
        paymentMethod = "Ordre de virement"
      )
      repository.insertTransaction(notaryFile)
      setAlert("Demande juridique d'acte transmise à l'étude notariale.")
    }
  }

  fun respondToWorkflowTransaction(tx: BusinessTransaction, approve: Boolean, responseComment: String) {
    viewModelScope.launch {
      val finalStatus = if (approve) "VALIDE" else "REJETE"
      val updated = tx.copy(status = finalStatus, comments = responseComment)
      repository.updateTransaction(updated)
      setAlert("Dossier mis à jour avec le statut: $finalStatus")
    }
  }

  // --- EXCLUSIVE SUPER-ADMIN POWERS ---
  fun purgeUserAccount(targetUser: UserAccount) {
    viewModelScope.launch {
      repository.deleteUserAccount(targetUser)
      setAlert("Compte utilisateur ${targetUser.email} supprimé par l'Administrateur principal.")
    }
  }

  fun modifyUserPaidStatus(targetUser: UserAccount, paid: Boolean) {
    viewModelScope.launch {
      val updated = targetUser.copy(subscriptionPaid = paid)
      repository.updateUserAccount(updated)
      setAlert("Statut d'abonnement de ${targetUser.email} mis à jour.")
    }
  }

  fun modifyUserSuspendedStatus(targetUser: UserAccount, suspended: Boolean) {
    viewModelScope.launch {
      val updated = targetUser.copy(isSuspended = suspended)
      repository.updateUserAccount(updated)
      setAlert(if (suspended) "Compte suspendu instantanément." else "Compte réactivé.")
    }
  }

  // --- IA CONVERSATIONAL ADVISOR CORES ---
  fun askAdvisorAssistant(question: String) {
    if (question.isBlank()) return
    
    // Add User Message
    val messages = _chatMessages.value.toMutableList()
    messages.add(question to true)
    _chatMessages.value = messages

    _isChatLoading.value = true
    viewModelScope.launch {
      val answer = GeminiClient.askAdvisor(question, _propertiesList.value)
      
      val updatedMessages = _chatMessages.value.toMutableList()
      updatedMessages.add(answer to false)
      _chatMessages.value = updatedMessages
      _isChatLoading.value = false
    }
  }

  fun clearAdvisorHistory() {
    _chatMessages.value = listOf(
      "Historique effacé. Posez-moi de nouvelles questions immobilières sur Pointe-Noire ou Brazzaville !" to false
    )
  }
}
