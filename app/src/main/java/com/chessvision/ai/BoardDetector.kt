package com.chessvision.ai

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

class BoardDetector {

    fun detectBoard(bmp: Bitmap): Bitmap? {
        val src = Mat()
        Utils.bitmapToMat(bmp, src)
        val gray = Mat()
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_RGBA2GRAY)
        val edges = Mat()
        Imgproc.Canny(gray, edges, 50.0, 150.0)

        val contours = ArrayList<MatOfPoint>()
        Imgproc.findContours(edges, contours, Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

        var best: MatOfPoint? = null
        var bestArea = 0.0
        for (c in contours) {
            val area = Imgproc.contourArea(c)
            if (area > bestArea) {
                bestArea = area
                best = c
            }
        }
        if (best == null || bestArea < 10000) return null

        val rect = Imgproc.boundingRect(best)
        val cropped = Mat(src, rect)
        val out = Bitmap.createBitmap(cropped.cols(), cropped.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(cropped, out)
        src.release(); gray.release(); edges.release()
        return out
    }
}
