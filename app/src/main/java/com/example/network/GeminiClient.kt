package com.example.network

import android.util.Log
import com.example.BuildConfig
import com.example.data.Property
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiClient {
  private val mediaType = "application/json; charset=utf-8".toMediaType()

  private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

  /**
   * Generates feedback tailored for NDAKO TENDANCE.
   */
  suspend fun askAdvisor(
    prompt: String,
    properties: List<Property>
  ): String = withContext(Dispatchers.IO) {
    val apiKey = BuildConfig.GEMINI_API_KEY
    if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
      return@withContext "Clé API Gemini non configurée dans le panneau de Secrets d'AI Studio."
    }

    // Build standard properties catalog context to inject as knowledge
    val catalogBuilder = StringBuilder()
    catalogBuilder.append("Catalogue des biens immobiliers réels de NDAKO TENDANCE (disponibilité en temps réel) :\n")
    for (prop in properties) {
      catalogBuilder.append("- ID: ${prop.id} | Titre: ${prop.title} | Type: ${prop.category} | Prix: ${prop.price} USD | Statut actuel: ${prop.status} | Ville: ${prop.location} | Publié par: ${prop.ownerName}\n")
    }

    val systemInstruction = """
      Vous êtes l'IA Advisor, le conseiller virtuel officiel haut de gamme de l'agence immobilière de prestige NDAKO TENDANCE au Congo (Pointe-Noire et Brazzaville).
      Votre ton est professionnel, élégant, courtois et plein d'expertise locale.
      Utilisez ces consignes strictes :
      1. Répondez de manière structurée et polie en français.
      2. Guidez les clients exclusivement vers les biens marqués comme DISPONIBLE dans le catalogue. Si le statut d'un bien est OCCUPÉ ou EN_TRAVAUX, mentionnez poliment qu'il est actuellement indisponible et proposez d'autres solutions.
      3. Aidez à estimer la valeur des loyers et des ventes selon les tendances du marché au Congo (Brazzaville, Pointe-Noire) et à l'international.
      4. Si la requête contient des mots suspects (par exemple: 'contourner l'agence', 'paiement de main à main', 'sans commission', 'fraude'), alertez immédiatement par une mention spéciale: '[ALERTE FRAUDE SUSPECTÉE]'.
      5. La devise de l'agence est : "Sécurisé – Innovation – Confort". La promesse commerciale est : "L'immobilier qui vous comprend."
    """.trimIndent()

    val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

    try {
      // Build JSON request using JSONObject
      val requestJson = JSONObject()
      
      // Contents block
      val contentsArray = JSONArray()
      val contentObj = JSONObject()
      val partsArray = JSONArray()
      
      // First, add the user text which includes context catalog + user question
      val textPart = JSONObject()
      textPart.put("text", "CONTEXTE DE L'AGENCE :\n$catalogBuilder\n\nQUESTION DE L'UTILISATEUR :\n$prompt")
      partsArray.put(textPart)
      contentObj.put("parts", partsArray)
      contentsArray.put(contentObj)
      requestJson.put("contents", contentsArray)

      // System instruction block
      val sysInstructObj = JSONObject()
      val sysPartsArray = JSONArray()
      val sysTextPart = JSONObject()
      sysTextPart.put("text", systemInstruction)
      sysPartsArray.put(sysTextPart)
      sysInstructObj.put("parts", sysPartsArray)
      requestJson.put("systemInstruction", sysInstructObj)

      val requestBody = requestJson.toString().toRequestBody(mediaType)

      val request = Request.Builder()
        .url(endpoint)
        .post(requestBody)
        .build()

      okHttpClient.newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
          return@withContext "Erreur lors de l'interrogation de l'IA (code: ${response.code})"
        }
        val responseBodyString = response.body?.string() ?: return@withContext "Réponse vide de l'IA"
        
        val jsonResponse = JSONObject(responseBodyString)
        val candidates = jsonResponse.optJSONArray("candidates")
        if (candidates != null && candidates.length() > 0) {
          val candidate = candidates.getJSONObject(0)
          val content = candidate.optJSONObject("content")
          val parts = content?.optJSONArray("parts")
          if (parts != null && parts.length() > 0) {
            return@withContext parts.getJSONObject(0).optString("text", "Aucun texte généré.")
          }
        }
        "Désolé, je n'ai pas pu analyser la demande immobilière."
      }
    } catch (e: Exception) {
      Log.e("GeminiClient", "API error", e)
      "Erreur d'IA: ${e.localizedMessage ?: "Vérifiez votre connexion internet."}"
    }
  }
}
