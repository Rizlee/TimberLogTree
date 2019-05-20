package com.rizlee.timberlogtree

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Build
import androidx.core.content.FileProvider
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class LogTree(private val appContext: Context) : Timber.DebugTree() {

    companion object {

        private val logListToShare = mutableListOf<String>()

        private fun getFormatLogs(): String = logListToShare.joinToString("\n")

        fun shareTxtFileLogs(context: Context) {
            LogFileHelper.getLogFile()?.let {
                context.startActivity(Intent.createChooser(Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, "com.rizlee.timberlogtree.provider", it))
                    type = "text/*"
                }, "Share logs").addFlags(FLAG_ACTIVITY_NEW_TASK))
            }
        }

        fun shareTextLogs(context: Context) {
            context.startActivity(Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getFormatLogs())
                type = "text/plain"
            }, "Share logs").addFlags(FLAG_ACTIVITY_NEW_TASK))
        }

        private val longFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US)
        private val shortFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
        private val onlyDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)

        init {
            logListToShare.apply {
                add("Device: ${Build.MANUFACTURER} ${Build.DEVICE}")
                add("Android sdk version: ${Build.VERSION.SDK_INT}")
                add("Date: ${longFormat.format(Date())}")
            }
        }
    }

    private fun writeLogInFile(log: String) =
            try {
                LogFileHelper.saveToFile(onlyDateFormat.format(Date()) + " " + log, appContext)
            } catch (e: Exception) {
            }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        "${shortFormat.format(Date())} $tag: $message".apply {
            logListToShare.add(this)
            writeLogInFile(this)
        }
        super.log(priority, tag, message, t)
    }
}