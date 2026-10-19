package com.chessvision.ai

import android.graphics.Bitmap

class FenBuilder {

    /**
     * Tahta bitmap'ini 8x8'e böler ve TFLite modeliyle her kareyi sınıflandırır.
     * MODEL GEREKLİ: app/src/main/assets/chess_pieces.tflite
     * Model yoksa boş string döner — FEN üretilemez (test gerekir).
     */
    fun boardToFen(boardBmp: Bitmap, turn: Char = 'w'): String {
        val modelPath = "chess_pieces.tflite"
        return try {
            classifyWithModel(boardBmp, modelPath, turn)
        } catch (e: Exception) {
            ""
        }
    }

    private fun classifyWithModel(bmp: Bitmap, modelPath: String, turn: Char): String {
        // TFLite modeli assets'ten yüklenip 64 kare sınıflandırılır.
        // Model entegrasyonu ayrı adım — şu an boş dönüyor.
        // Model eklendiğinde:
        //   Interpreter(loadModelFile(modelPath))
        //   her kare için input tensor doldur, output index -> piece char
        //   satır satır FEN stringine yaz
        return ""
    }
}
