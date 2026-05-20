package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {
  val allUsers: Flow<List<UserAccount>> = appDao.getAllUserAccounts()
  val allProperties: Flow<List<Property>> = appDao.getAllProperties()
  val allTransactions: Flow<List<BusinessTransaction>> = appDao.getAllTransactions()

  suspend fun getUserAccountByEmail(email: String): UserAccount? {
    return appDao.getUserAccountByEmail(email)
  }

  suspend fun insertUserAccount(user: UserAccount) {
    appDao.insertUserAccount(user)
  }

  suspend fun updateUserAccount(user: UserAccount) {
    appDao.updateUserAccount(user)
  }

  suspend fun deleteUserAccount(user: UserAccount) {
    appDao.deleteUserAccount(user)
  }

  suspend fun insertProperty(property: Property) {
    appDao.insertProperty(property)
  }

  suspend fun updateProperty(property: Property) {
    appDao.updateProperty(property)
  }

  suspend fun deleteProperty(property: Property) {
    appDao.deleteProperty(property)
  }

  suspend fun deletePropertyById(id: Int) {
    appDao.deletePropertyById(id)
  }

  suspend fun insertTransaction(tx: BusinessTransaction) {
    appDao.insertTransaction(tx)
  }

  suspend fun updateTransaction(tx: BusinessTransaction) {
    appDao.updateTransaction(tx)
  }

  suspend fun prePopulateIfEmpty() {
    // We populate default simulation accounts and high-fidelty properties
    val existingUsers = appDao.getUserAccountByEmail("ndakotendance2016@gmail.com")
    if (existingUsers == null) {
      // Create admin accounts
      appDao.insertUserAccount(
        UserAccount(
          email = "ndakotendance2016@gmail.com",
          name = "NDAKO Tendance Admin",
          role = "AGENCE",
          phone = "+242066226914",
          isApproved = true,
          subscriptionPaid = true
        )
      )
      
      // Post standard demo actors to enable full testing right away!
      appDao.insertUserAccount(
        UserAccount(
          email = "proprietaire1@ndako.com",
          name = "Serge Mvoula",
          role = "PROPRIETAIRE",
          phone = "+242066551000",
          isApproved = true,
          subscriptionPaid = true
        )
      )
      
      appDao.insertUserAccount(
        UserAccount(
          email = "demarcheur1@ndako.com",
          name = "Grace Okemba",
          role = "DEMARCHEUR",
          phone = "+242044819000",
          isApproved = true,
          subscriptionPaid = true
        )
      )

      appDao.insertUserAccount(
        UserAccount(
          email = "notaire1@ndako.com",
          name = "Me. Antoine Makosso",
          role = "NOTAIRE",
          phone = "+242055621000",
          isApproved = true,
          subscriptionPaid = true
        )
      )

      appDao.insertUserAccount(
        UserAccount(
          email = "banque1@ndako.com",
          name = "Congo Banque SG",
          role = "BANQUE",
          phone = "+242022830000",
          isApproved = true,
          subscriptionPaid = true
        )
      )

      // Post sample properties
      appDao.insertProperty(
        Property(
          title = "Villa Prestige Océan",
          description = "Villa moderne haut standing avec piscine à débordement, salon majestueux et vue panoramique imprenable sur l'océan Atlantique.",
          price = 150000.0,
          category = "Location Saisonnière",
          location = "Pointe-Noire - Côte Sauvage",
          status = "DISPONIBLE",
          ownerEmail = "proprietaire1@ndako.com",
          ownerName = "Serge Mvoula",
          ownerPhone = "+242066551000",
          imageResName = "house_1"
        )
      )

      appDao.insertProperty(
        Property(
          title = "Penthouse Ndako d'Or",
          description = "Penthouse situé au coeur du centre-ville, design raffiné noir profond et or prestige avec domotique complète et sécurité 24h/24.",
          price = 2500.0,
          category = "Location Journalière",
          location = "Brazzaville - Centre-Ville",
          status = "DISPONIBLE",
          ownerEmail = "ndakotendance2016@gmail.com",
          ownerName = "Agence NDAKO Tendance",
          ownerPhone = "+242066226914",
          imageResName = "house_2"
        )
      )

      appDao.insertProperty(
        Property(
          title = "Résidence Familiale Confort",
          description = "Duplex spacieux comprenant 4 chambres, jardin arboré, cuisine moderne équipée et garage fermé pour deux véhicules.",
          price = 850000.0,
          category = "Location Standard",
          location = "Brazzaville - Plateau des Quinze ans",
          status = "DISPONIBLE",
          ownerEmail = "demarcheur1@ndako.com",
          ownerName = "Grace Okemba",
          ownerPhone = "+242044819000",
          imageResName = "house_3"
        )
      )

      appDao.insertProperty(
        Property(
          title = "Appartement Lumineux Mpila",
          description = "Appartement de type F3 meublé proche commerces avec parking privé et réserve d'eau autonome installée.",
          price = 450.0,
          category = "Location Standard",
          location = "Brazzaville - Mpila",
          status = "OCCUPÉ",
          ownerEmail = "proprietaire1@ndako.com",
          ownerName = "Serge Mvoula",
          ownerPhone = "+242066551000",
          imageResName = "house_4"
        )
      )
      
      // Let's add some initial transactions for demo analytics
      appDao.insertTransaction(
        BusinessTransaction(
          type = "ABONNEMENT",
          requesterEmail = "proprietaire1@ndako.com",
          targetEmail = "ndakotendance2016@gmail.com",
          propertyId = 0,
          propertyTitle = "Frais d'adhésion mensuels",
          amount = 15000.0,
          status = "VALIDE",
          comments = "Abonnement Elite Propriétaire Payé par Mobile Money",
          paymentMethod = "MTN MoMo"
        )
      )

      appDao.insertTransaction(
        BusinessTransaction(
          type = "COMMISSION",
          requesterEmail = "demarcheur1@ndako.com",
          targetEmail = "ndakotendance2016@gmail.com",
          propertyId = 3,
          propertyTitle = "Résidence Familiale Confort",
          amount = 85000.0,
          status = "VALIDE",
          comments = "Commission 10% validée sur location",
          paymentMethod = "Airtel Money"
        )
      )

      appDao.insertTransaction(
        BusinessTransaction(
          type = "DOSSIER_BANCAIRE",
          requesterEmail = "proprietaire1@ndako.com",
          targetEmail = "banque1@ndako.com",
          propertyId = 1,
          propertyTitle = "Villa Prestige Océan",
          amount = 120000000.0, // CFA francs
          status = "EN_ATTENTE",
          comments = "Demande de prêt immobilier acquéreur.",
          paymentMethod = "Virement Bancaire"
        )
      )
    }
  }
}
