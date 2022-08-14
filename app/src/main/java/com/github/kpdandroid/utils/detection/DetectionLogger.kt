package com.github.kpdandroid.utils.detection

import android.util.Log
import java.io.Closeable
import java.io.IOException
import java.io.OutputStream

private const val TAG = "DetectionLogger"

class DetectionLogger(logFile: OutputStream) : Closeable {
    private val writer = logFile.bufferedWriter()

    fun log(detectorTitle: String?, width: Int, height: Int, keypointsNum: Int, timeMs: Long) {
        Log.d(TAG, "Writing log: $detectorTitle ${width}x$height $keypointsNum $timeMs")
        try {
            writer.write("$detectorTitle ${width}x$height $keypointsNum $timeMs")
            writer.newLine()
        } catch (e: IOException) {
            Log.e(TAG, "Failed to write a log.", e)
        }
    }

    fun save() {
        Log.i(TAG, "Saving log file.")

        try {
            writer.flush()
        } catch (e: IOException) {
            Log.e(TAG, "Failed to flush log writer.", e)
        }
    }

    override fun close() {
        Log.i(TAG, "Closing log file.")

        try {
            writer.close()
        } catch (e: IOException) {
            Log.e(TAG, "Failed to close log writer.", e)
        }
    }
}
