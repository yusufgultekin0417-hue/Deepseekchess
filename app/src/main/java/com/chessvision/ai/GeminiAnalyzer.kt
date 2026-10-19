package com.chessvision.ai

import com.google.ai.client.generativeai.GenerativeModel

class GeminiAnalyzer {
    private val model by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    suspend fun explain(fen: String, bestMove: String): String {
        if (BuildConfig.GEMINI_API_KEY.isBlank()) {
            return "Gemini API anahtari tanimli degil."
        }
        val prompt = """
            Satranc pozisyonu (FEN): $fen
            Stockfish'in onerdigi en iyi hamle: $bestMove
            Bu hamleyi Turkce, sade ve oyuncuya yol gosterici sekilde 3 cumlede acikla.
            Rakip tehdidini ve hamlenin amacini belirt.
        """.trimIndent()
        return try {
            model.generateContent(prompt).text ?: "Aciklama alinamadi."
        } catch (e: Exception) {
            "Gemini hatasi: ${e.message}"
        }
    }
}
