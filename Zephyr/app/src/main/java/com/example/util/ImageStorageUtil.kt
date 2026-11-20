package com.example.util

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageStorageUtil {
    private const val TAG = "ImageStorageUtil"

    /**
     * Copies an image from a content URI (e.g. Android Photo Picker) into the app's
     * persistent internal storage directory. This ensures the image file persists
     * across app restarts, process deaths, and reboots without permission expiry.
     */
    fun persistImageToInternalStorage(context: Context, sourceUri: Uri): String {
        return try {
            val photosDir = File(context.filesDir, "dealership_photos").apply {
                if (!exists()) mkdirs()
            }
            val fileName = "car_photo_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.jpg"
            val destFile = File(photosDir, fileName)

            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            } ?: return sourceUri.toString()

            Log.d(TAG, "Successfully copied photo to permanent storage: ${destFile.absolutePath}")
            destFile.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Failed to copy photo to internal storage, falling back to uri string", e)
            sourceUri.toString()
        }
    }
}
