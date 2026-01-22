package com.perflyst.twire.misc

import android.content.Context
import android.util.Log
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * A Timber Tree that logs errors to a file for debugging purposes.
 * Only use in debug builds!
 *
 * Log file location: /storage/emulated/0/Android/data/com.perflyst.twire/files/twire_debug.log
 */
class FileLoggingTree(context: Context) : Timber.Tree() {

    private val logFile: File
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
    private val maxLogSize = 5 * 1024 * 1024 // 5 MB max log size

    init {
        val logDir = context.getExternalFilesDir(null)
        logFile = File(logDir, "twire_debug.log")

        // Rotate log if too large
        if (logFile.exists() && logFile.length() > maxLogSize) {
            val backupFile = File(logDir, "twire_debug.log.old")
            backupFile.delete()
            logFile.renameTo(backupFile)
        }

        // Write startup marker
        writeToFile("I", "FileLoggingTree", "=== App started ===")
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // Only log warnings and errors to file
        if (priority < Log.WARN) return

        val priorityStr = when (priority) {
            Log.WARN -> "W"
            Log.ERROR -> "E"
            Log.ASSERT -> "A"
            else -> "?"
        }

        writeToFile(priorityStr, tag ?: "Unknown", message)

        // Also log stack trace for exceptions
        if (t != null) {
            writeThrowable(t)
        }
    }

    private fun writeToFile(priority: String, tag: String, message: String) {
        try {
            synchronized(logFile) {
                FileWriter(logFile, true).use { writer ->
                    val timestamp = dateFormat.format(Date())
                    writer.appendLine("$timestamp $priority/$tag: $message")
                }
            }
        } catch (e: Exception) {
            // Don't crash if logging fails
            Log.e("FileLoggingTree", "Failed to write to log file", e)
        }
    }

    private fun writeThrowable(t: Throwable) {
        try {
            synchronized(logFile) {
                FileWriter(logFile, true).use { writer ->
                    PrintWriter(writer).use { pw ->
                        t.printStackTrace(pw)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("FileLoggingTree", "Failed to write throwable to log file", e)
        }
    }

    companion object {
        /**
         * Get the log file path for display to users.
         */
        fun getLogFilePath(context: Context): String {
            val logDir = context.getExternalFilesDir(null)
            return File(logDir, "twire_debug.log").absolutePath
        }
    }
}