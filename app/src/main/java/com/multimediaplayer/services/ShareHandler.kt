package com.multimediaplayer.services

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.documentfile.provider.DocumentFile
import com.multimediaplayer.data.repository.MediaRepository
import com.multimediaplayer.models.MediaItem
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ShareHandler(
    private val context: Context,
    private val mediaRepository: MediaRepository
) {
    suspend fun handleSharedContent(uri: Uri): Boolean {
        return try {
            val mimeType = context.contentResolver.getType(uri) ?: return false
            
            val mediaType = when {
                mimeType.startsWith("audio/") -> "audio"
                mimeType.startsWith("video/") -> "video"
                mimeType.startsWith("image/") -> "image"
                else -> return false
            }
            
            // Get file name
            val fileName = getFileName(uri) ?: return false
            
            // Copy file to internal storage
            val destinationFile = copyFileToInternalStorage(uri, fileName, mediaType)
            
            if (destinationFile != null) {
                // Create MediaItem and add to repository
                val mediaItem = MediaItem(
                    title = fileName,
                    path = destinationFile.absolutePath,
                    type = mediaType,
                    dateAdded = System.currentTimeMillis()
                )
                
                mediaRepository.addToCollection(mediaItem)
                return true
            }
            
            false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    private fun getFileName(uri: Uri): String? {
        return when (uri.scheme) {
            "content" -> {
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0) {
                        cursor.moveToFirst()
                        return cursor.getString(nameIndex)
                    }
                }
                null
            }
            "file" -> File(uri.path ?: "").name
            else -> null
        }
    }
    
    private fun copyFileToInternalStorage(uri: Uri, fileName: String, mediaType: String): File? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            
            if (inputStream != null) {
                // Create appropriate directory based on media type
                val directory = when (mediaType) {
                    "audio" -> File(context.filesDir, "shared_audio")
                    "video" -> File(context.filesDir, "shared_video")
                    "image" -> File(context.filesDir, "shared_image")
                    else -> File(context.filesDir, "shared_files")
                }
                
                if (!directory.exists()) {
                    directory.mkdirs()
                }
                
                val outputFile = File(directory, fileName)
                
                FileOutputStream(outputFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                
                inputStream.close()
                
                outputFile
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}