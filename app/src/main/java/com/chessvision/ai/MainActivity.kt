package com.chessvision.ai

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.opencv.android.OpenCVLoader

class MainActivity : AppCompatActivity() {
    private val REQ_CAPTURE = 2001
    private lateinit var status: TextView
    private lateinit var result: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        status = findViewById(R.id.statusText)
        result = findViewById(R.id.resultText)

        if (!OpenCVLoader.initDebug()) {
            status.text = "OpenCV yuklenemedi"
        }

        findViewById<Button>(R.id.startBtn).setOnClickListener {
            val mgr = getSystemService(MediaProjectionManager::class.java)
            startActivityForResult(mgr.createScreenCaptureIntent(), REQ_CAPTURE)
        }

        findViewById<Button>(R.id.stopBtn).setOnClickListener {
            stopService(Intent(this, ScreenCaptureService::class.java))
            status.text = "Takip durduruldu"
        }

        findViewById<Button>(R.id.analyzeBtn).setOnClickListener {
            analyze()
        }
    }

    override fun onActivityResult(req: Int, code: Int, data: Intent?) {
        super.onActivityResult(req, code, data)
        if (req == REQ_CAPTURE && code == Activity.RESULT_OK && data != null) {
            val i = Intent(this, ScreenCaptureService::class.java)
                .putExtra("resultCode", code)
                .putExtra("data", data)
            startForegroundService(i)
            status.text = "Takip basladi"
        }
    }

    private fun analyze() {
        lifecycleScope.launch {
            result.text = "Analiz ediliyor..."
            val frame = ScreenCaptureService.latestFrame
            if (frame == null) {
                result.text = "Ekran goruntusu yok. Takibi baslat."
                return@launch
            }

            val boardBmp = withContext(Dispatchers.Default) {
                BoardDetector().detectBoard(frame)
            }
            if (boardBmp == null) {
                result.text = "Tahta bulunamadi."
                return@launch
            }

            val fen = withContext(Dispatchers.Default) {
                FenBuilder().boardToFen(boardBmp)
            }
            if (fen.isEmpty()) {
                result.text = "FEN uretilemedi. Tas tanima modeli eksik."
                return@launch
            }

            val sf = StockfishEngine(this@MainActivity)
            sf.init()
            val best = sf.bestMove(fen)
            val explanation = GeminiAnalyzer().explain(fen, best)
            result.text = "FEN: $fen\nEn iyi hamle: $best\n\n$explanation"
            sf.quit()
        }
    }
}
