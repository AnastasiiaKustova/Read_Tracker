package com.example.readtracker.android.data.local

import android.content.Context
import android.net.Uri
import com.example.readtracker.android.di.ApplicationScope
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@ApplicationScope
class CoverStorage @Inject constructor(
    private val context: Context
) {
    fun saveCoverToInternalStorage(sourceUri: Uri?, bookId: String): Uri? {
        if (sourceUri == null) return null
        return try {
            val coversDir = File(context.filesDir, "book_covers")
            if (!coversDir.exists()) {
                coversDir.mkdirs()
            }

            val targetFile = File(coversDir, "cover_$bookId.jpg")

            context.contentResolver.openInputStream(sourceUri)?.use { inputStream ->
                FileOutputStream(targetFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            Uri.fromFile(targetFile)
        } catch (e: Exception) {
            android.util.Log.e("APP_DEBUG", "Ошибка при копировании обложки в память приложения: ", e)
            null
        }
    }
}