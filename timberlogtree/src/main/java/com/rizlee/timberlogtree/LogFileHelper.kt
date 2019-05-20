package com.rizlee.timberlogtree

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

private const val fileName = "log.txt"

private fun Any?.notNull(): Boolean {
    return this != null
}

object LogFileHelper {

    private var file: File? = null

    @Throws(SecurityException::class, IOException::class)
    fun saveToFile(data: String, context: Context) {
        if (!file.notNull()) {
            file = File(context.filesDir, fileName)

            if (!file!!.exists()) {
                file!!.createNewFile()
            }
        }

        FileOutputStream(file, file!!.length() / 1024 / 1024 <= 10).also {
            it.write((data + System.getProperty("line.separator")!!).toByteArray())
            it.close()
        }
    }

    fun getLogFile(): File? =
            if (file.notNull() && file!!.exists()) file!! else null
}