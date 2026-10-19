package com.chessvision.ai

import android.content.Context
import kotlinx.coroutines.*
import java.io.*

class StockfishEngine(private val ctx: Context) {
    private var process: Process? = null
    private var writer: BufferedWriter? = null
    private var reader: BufferedReader? = null

    suspend fun init() = withContext(Dispatchers.IO) {
        val bin = File(ctx.filesDir, "stockfish")
        if (!bin.exists()) {
            ctx.assets.open("stockfish").use { input ->
                bin.outputStream().use { input.copyTo(it) }
            }
            bin.setExecutable(true)
        }
        process = ProcessBuilder(bin.absolutePath).redirectErrorStream(true).start()
        writer = BufferedWriter(OutputStreamWriter(process!!.outputStream))
        reader = BufferedReader(InputStreamReader(process!!.inputStream))
        send("uci")
        waitFor("uciok")
    }

    suspend fun bestMove(fen: String, depth: Int = 18): String = withContext(Dispatchers.IO) {
        send("position fen $fen")
        send("go depth $depth")
        val line = waitFor("bestmove")
        if (line.isBlank()) return@withContext ""
        line.split(" ").getOrElse(1) { "" }
    }

    private fun send(cmd: String) {
        writer?.write("$cmd\n")
        writer?.flush()
    }

    private fun waitFor(token: String): String {
        var line: String
        while (true) {
            line = reader?.readLine() ?: break
            if (line.startsWith(token)) return line
        }
        return ""
    }

    fun quit() {
        send("quit")
        process?.destroy()
        process = null
    }
}
