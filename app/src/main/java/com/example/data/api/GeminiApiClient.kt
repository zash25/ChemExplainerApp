package com.example.data.api

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.example.data.knowledge.ChemistryKnowledgeBase
import com.example.data.knowledge.QuizQuestion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiApiClient(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val apiKey: String
        get() = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

    /**
     * Explains a chemical reaction or question with RAG context from the curriculum notes.
     */
    suspend fun explainReactionWithRag(
        query: String,
        conversationHistory: List<Pair<String, String>> = emptyList(),
        modelName: String = "gemini-3.8-flash"
    ): String = withContext(Dispatchers.IO) {
        val ragContext = ChemistryKnowledgeBase.searchCurriculum(query)

        val systemInstructionText = """
            You are Dr. Rutherford, an expert, encouraging AI Chemistry Tutor specializing in senior secondary and college chemistry.
            You explain chemical reactions step-by-step with supreme clarity, emphasizing:
            1. Reaction Type (Redox, Addition, Substitution, Esterification, Electrolysis, Neutralization, etc.)
            2. Oxidation Numbers & Electron Transfer (LEO GER / Half-reactions when applicable)
            3. Balanced Chemical Equation (clearly labeled reactants and products)
            4. Real-world applications, observation changes (color shifts, gas bubbles, precipitates), and safety cautions.
            
            Use the following RAG CURRICULUM CONTEXT strictly to align with the student's syllabus:
            $ragContext
            
            Format your answer with clear markdown headings, bullet points, and chemical formulas.
        """.trimIndent()

        // Build Gemini Request Payload
        val contentsArray = JSONArray()

        // Append recent conversation history
        for (turn in conversationHistory.takeLast(6)) {
            val role = if (turn.first == "USER") "user" else "model"
            contentsArray.put(JSONObject().apply {
                put("role", role)
                put("parts", JSONArray().put(JSONObject().put("text", turn.second)))
            })
        }

        // Append current prompt
        contentsArray.put(JSONObject().apply {
            put("role", "user")
            put("parts", JSONArray().put(JSONObject().put("text", query)))
        })

        val requestJson = JSONObject().apply {
            put("contents", contentsArray)
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().put(JSONObject().put("text", systemInstructionText)))
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.4)
                put("maxOutputTokens", 2048)
            })
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Provide a high quality offline synthesized explanation from RAG context
            return@withContext getOfflineExplanation(query, ragContext)
        }

        try {
            val cleanModel = when (modelName) {
                "gemini-3.8-flash", "models/gemini-3.8-flash" -> "gemini-3.8-flash"
                "gemini-3.1-pro-preview" -> "gemini-3.1-pro-preview"
                "gemini-3.1-flash-lite-preview", "gemini-3.1-flash-lite" -> "gemini-3.1-flash-lite-preview"
                "gemini-3.8-live" -> "gemini-3.8-live"
                else -> "gemini-3.8-flash"
            }
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$cleanModel:generateContent?key=$apiKey"
            val body = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.w("GeminiApi", "API call failed with code ${response.code}: $responseBody")
                return@withContext getOfflineExplanation(query, ragContext)
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text")

            if (!text.isNullOrBlank()) {
                text
            } else {
                getOfflineExplanation(query, ragContext)
            }
        } catch (e: Exception) {
            Log.e("GeminiApi", "Exception in explainReactionWithRag", e)
            getOfflineExplanation(query, ragContext)
        }
    }

    /**
     * Real-time conversational Live Voice API interaction using model gemini-3.8-live.
     */
    suspend fun liveVoiceConversation(spokenPrompt: String): String = withContext(Dispatchers.IO) {
        val ragContext = ChemistryKnowledgeBase.searchCurriculum(spokenPrompt)
        val liveSystemInstruction = """
            You are Dr. Rutherford on the Gemini Live Voice channel (gemini-3.8-live).
            Keep your spoken response natural, punchy, conversational, and direct (2-4 sentences).
            Ground your answers in the chemistry curriculum:
            $ragContext
        """.trimIndent()

        val requestJson = JSONObject().apply {
            put("contents", JSONArray().put(JSONObject().apply {
                put("parts", JSONArray().put(JSONObject().put("text", spokenPrompt)))
            }))
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().put(JSONObject().put("text", liveSystemInstruction)))
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.6)
                put("maxOutputTokens", 256)
            })
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "Dr. Rutherford here on Live API! Regarding $spokenPrompt: Remember to balance both atom count and electrical charges. In acidic solutions we add water and H+ ions, whereas in basic solutions we neutralize with hydroxide ions."
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.8-live:generateContent?key=$apiKey"
            val body = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                // Fallback to gemini-3.8-flash if live endpoint is in restricted preview
                return@withContext explainReactionWithRag(spokenPrompt, modelName = "gemini-3.8-flash")
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            val text = candidates?.optJSONObject(0)
                ?.optJSONObject("content")
                ?.optJSONArray("parts")
                ?.optJSONObject(0)
                ?.optString("text") ?: ""

            if (text.isNotBlank()) text else "Great question! Let me break that reaction down for you."
        } catch (e: Exception) {
            explainReactionWithRag(spokenPrompt, modelName = "gemini-3.8-flash")
        }
    }

    /**
     * Generates custom dynamic practice questions using Gemini structured output.
     */
    suspend fun generateCustomQuizQuestions(
        topicPrompt: String,
        count: Int = 3
    ): List<QuizQuestion> = withContext(Dispatchers.IO) {
        val ragContext = ChemistryKnowledgeBase.searchCurriculum(topicPrompt)

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext ChemistryKnowledgeBase.practiceQuestions
                .filter { it.topicId.contains(topicPrompt, ignoreCase = true) || topicPrompt.isBlank() }
                .take(count)
                .ifEmpty { ChemistryKnowledgeBase.practiceQuestions.shuffled().take(count) }
        }

        val prompt = """
            Generate $count multiple-choice chemistry questions on the topic '$topicPrompt' based on this syllabus context:
            $ragContext
            
            Return JSON in this exact structure:
            [
              {
                "question": "string",
                "options": ["A", "B", "C", "D"],
                "correctIndex": 0,
                "explanation": "Detailed step-by-step reasoning"
              }
            ]
        """.trimIndent()

        val requestJson = JSONObject().apply {
            put("contents", JSONArray().put(JSONObject().apply {
                put("parts", JSONArray().put(JSONObject().put("text", prompt)))
            }))
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.3)
            })
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val body = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext ChemistryKnowledgeBase.practiceQuestions.shuffled().take(count)
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            val text = candidates?.optJSONObject(0)
                ?.optJSONObject("content")
                ?.optJSONArray("parts")
                ?.optJSONObject(0)
                ?.optString("text") ?: ""

            val jsonArray = JSONArray(text.trim().removePrefix("```json").removeSuffix("```").trim())
            val result = mutableListOf<QuizQuestion>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val qText = obj.getString("question")
                val optArray = obj.getJSONArray("options")
                val options = mutableListOf<String>()
                for (j in 0 until optArray.length()) {
                    options.add(optArray.getString(j))
                }
                val correctIndex = obj.getInt("correctIndex")
                val explanation = obj.getString("explanation")

                result.add(
                    QuizQuestion(
                        id = "ai_q_${System.currentTimeMillis()}_$i",
                        topicId = topicPrompt,
                        question = qText,
                        options = options,
                        correctIndex = correctIndex.coerceIn(0, options.size - 1),
                        explanation = explanation
                    )
                )
            }
            if (result.isNotEmpty()) result else ChemistryKnowledgeBase.practiceQuestions.shuffled().take(count)
        } catch (e: Exception) {
            Log.e("GeminiApi", "Error parsing generated quiz questions", e)
            ChemistryKnowledgeBase.practiceQuestions.shuffled().take(count)
        }
    }

    private fun getOfflineExplanation(query: String, ragContext: String): String {
        val qLower = query.lowercase()
        return when {
            qLower.contains("mno4") || (qLower.contains("redox") && qLower.contains("balanc")) -> {
                """
                ### 🧪 Step-by-Step Redox Reaction Balancing
                **Reaction:** MnO4⁻ + I⁻ → Mn²⁺ + I₂ (Acidic Solution)
                
                **1. Identify Oxidation States:**
                • Mn in MnO4⁻ has O.N. = +7, reduced to Mn²⁺ (+2). Gain of 5e⁻ (Reduction half).
                • I in I⁻ has O.N. = -1, oxidized to I₂ (0). Loss of 1e⁻ per atom (Oxidation half).
                
                **2. Balance Half-Equations:**
                • **Reduction Half:**
                  MnO4⁻ + 8H⁺ + 5e⁻ → Mn²⁺ + 4H₂O
                • **Oxidation Half:**
                  2I⁻ → I₂ + 2e⁻
                
                **3. Equalize Electron Transfer:**
                • Multiply Reduction half by 2:
                  2MnO4⁻ + 16H⁺ + 10e⁻ → 2Mn²⁺ + 8H₂O
                • Multiply Oxidation half by 5:
                  10I⁻ → 5I₂ + 10e⁻
                
                **4. Overall Balanced Equation:**
                2MnO4⁻(aq) + 10I⁻(aq) + 16H⁺(aq) → 2Mn²⁺(aq) + 5I₂(s) + 8H₂O(l)
                
                **Observation:** The purple permanganate color discharges to pale pink/colorless Mn²⁺, while elemental iodine (I₂) forms a brown solution / blue-black color with starch.
                """.trimIndent()
            }
            qLower.contains("electrolysis") && (qLower.contains("cuso4") || qLower.contains("copper")) -> {
                """
                ### ⚡ Electrolysis of Copper(II) Sulphate (CuSO4)
                
                **Ions present in aqueous solution:**
                • From salt: Cu²⁺, SO4²⁻
                • From water: H⁺, OH⁻
                
                **Case 1: Using Platinum (Inert) Electrodes**
                • **Cathode (-):** Cu²⁺ is discharged in preference to H⁺:
                  Cu²⁺(aq) + 2e⁻ → Cu(s)   (Copper deposits)
                • **Anode (+):** OH⁻ is discharged in preference to SO4²⁻:
                  4OH⁻(aq) → 2H₂O(l) + O₂(g) + 4e⁻   (Oxygen liberated)
                • **Observation:** Blue color fades as Cu²⁺ ions are removed. Solution becomes acidic due to remaining H⁺ and SO4²⁻ (dilute H₂SO4).
                
                **Case 2: Using Copper (Active) Electrodes (Electro-refining)**
                • **Cathode (-):** Pure Cu sheet grows as Cu²⁺ deposits:
                  Cu²⁺(aq) + 2e⁻ → Cu(s)
                • **Anode (+):** Impure Cu block dissolves:
                  Cu(s) → Cu²⁺(aq) + 2e⁻
                • **Observation:** Blue color intensity remains constant because rate of dissolution equals rate of deposition. Impurities settle as anode sludge/slime (containing Ag, Au).
                """.trimIndent()
            }
            qLower.contains("soap") || qLower.contains("detergent") -> {
                """
                ### 🧼 Cleansing Action of Soaps & Detergents
                
                **Structure of a Soap Molecule (e.g. Sodium Stearate):**
                CH3(CH2)16-COO⁻ Na⁺
                
                **1. Two Distinct Functional Parts:**
                • **Hydrophobic Tail:** Long non-polar hydrocarbon chain (C17H35-), repelled by water but dissolves readily in grease and organic oils.
                • **Hydrophilic Head:** Polar ionic carboxylate group (-COO⁻Na⁺), strongly attracted to polar water molecules.
                
                **2. Mechanism (Micelle Formation):**
                • When soap is added to water containing oily stains, the hydrophobic tails embed themselves into the grease droplet.
                • The hydrophilic heads remain pointing outward into the surrounding water.
                • Agitation breaks the grease into spherical colloidal droplets called **micelles**.
                • Due to mutual repulsion between the negatively charged heads on the outside of each micelle, droplets do not coalesce and remain suspended in water, rinsed away with water!
                
                **Soap vs Synthetic Detergent (ABS):**
                • Soaps form insoluble scum (Ca²⁺/Mg²⁺ curd) in hard water.
                • Soapless detergents (-SO3⁻Na⁺) do not form scum in hard water.
                """.trimIndent()
            }
            qLower.contains("faraday") -> {
                """
                ### ⚖️ Faraday's Laws of Electrolysis
                
                **Faraday's First Law:**
                The mass (m) of substance liberated at an electrode is directly proportional to the quantity of electricity (Q = I × t) passed through it:
                m = Z × I × t
                Where:
                • m = mass in grams
                • I = current in amperes
                • t = time in seconds
                • Z = electrochemical equivalent (g/C)
                
                **Faraday's Second Law:**
                When the same quantity of electricity is passed through different electrolytes, the masses deposited are proportional to their chemical equivalents:
                Chemical Equivalent = (Atomic Weight) / (Valency)
                
                **Key Constant:**
                1 Faraday (F) = 96,500 Coulombs = charge of 1 mole of electrons
                • 1F deposits 1 mol Ag⁺ (108 g)
                • 2F deposits 1 mol Cu²⁺ (63.5 g)
                • 3F deposits 1 mol Al³⁺ (27 g)
                """.trimIndent()
            }
            else -> {
                """
                ### 📚 Dr. Rutherford's Chemistry Explanation
                **Topic Query:** "$query"
                
                Based on your senior chemistry curriculum notes:
                
                $ragContext
                
                **Core Principles:**
                • Review the fundamental definitions and electron transfer steps.
                • Ensure conservation of both mass and charge when writing balanced chemical equations.
                • Tap the speaker icon to listen to this concept read aloud!
                """.trimIndent()
            }
        }
    }
}
