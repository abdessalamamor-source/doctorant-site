package com.example.data

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Locale

object SecurityUtils {

    private const val MAX_PDF_BYTES = 10L * 1024L * 1024L // 10 Mo max

    fun hashPassword(password: String): String {
        val saltBytes = ByteArray(16)
        SecureRandom().nextBytes(saltBytes)
        val salt = saltBytes.joinToString("") { "%02x".format(it) }
        val hash = sha256("$salt:$password")
        return "$salt:$hash"
    }

    fun verifyPassword(password: String, storedHash: String): Boolean {
        val parts = storedHash.split(":")
        if (parts.size != 2) return false
        val salt = parts[0]
        val expectedHash = parts[1]
        val actualHash = sha256("$salt:$password")
        return MessageDigest.isEqual(
            expectedHash.toByteArray(Charsets.UTF_8),
            actualHash.toByteArray(Charsets.UTF_8)
        )
    }

    private fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    data class StoredPdfResult(
        val fileName: String,
        val fileSizeLabel: String,
        val localFilePath: String
    )

    fun copyAndValidatePdf(
        context: Context,
        uri: Uri,
        applicationId: String,
        docTypeKey: String
    ): Result<StoredPdfResult> {
        return try {
            val resolver = context.contentResolver
            val mimeType = resolver.getType(uri)
            var displayName = "document.pdf"
            var fileSize = -1L

            resolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIdx = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (nameIdx >= 0) {
                        displayName = cursor.getString(nameIdx) ?: "document.pdf"
                    }
                    if (sizeIdx >= 0 && !cursor.isNull(sizeIdx)) {
                        fileSize = cursor.getLong(sizeIdx)
                    }
                }
            }

            if (fileSize > MAX_PDF_BYTES) {
                return Result.failure(Exception("Le fichier dépasse la taille maximale autorisée de 10 Mo."))
            }

            val isPdfMime = mimeType?.equals("application/pdf", ignoreCase = true) == true
            val isPdfExtension = displayName.lowercase(Locale.ROOT).endsWith(".pdf")
            if (!isPdfMime && !isPdfExtension) {
                return Result.failure(Exception("Format invalide. Seuls les fichiers PDF (.pdf) sont acceptés."))
            }

            val docsDir = File(context.filesDir, "candidature_docs")
            if (!docsDir.exists()) {
                docsDir.mkdirs()
            }

            val safeName = displayName.replace(Regex("[^a-zA-Z0-9._-]"), "_")
            val targetFile = File(docsDir, "${applicationId}_${docTypeKey}_${System.currentTimeMillis()}.pdf")

            resolver.openInputStream(uri)?.use { input ->
                val header = ByteArray(5)
                val readHeader = input.read(header)
                if (readHeader < 4 || String(header, 0, 4, Charsets.US_ASCII) != "%PDF") {
                    return Result.failure(Exception("Le fichier sélectionné n'est pas un document PDF valide."))
                }

                FileOutputStream(targetFile).use { output ->
                    output.write(header, 0, readHeader)
                    val buffer = ByteArray(8192)
                    var totalBytes = readHeader.toLong()
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        totalBytes += bytesRead
                        if (totalBytes > MAX_PDF_BYTES) {
                            targetFile.delete()
                            return Result.failure(Exception("Le fichier dépasse la taille maximale autorisée de 10 Mo."))
                        }
                        output.write(buffer, 0, bytesRead)
                    }
                    fileSize = totalBytes
                }
            } ?: return Result.failure(Exception("Impossible de lire le fichier sélectionné."))

            val formattedSize = formatFileSize(fileSize)
            Result.success(
                StoredPdfResult(
                    fileName = safeName,
                    fileSizeLabel = formattedSize,
                    localFilePath = targetFile.absolutePath
                )
            )
        } catch (e: Exception) {
            Result.failure(Exception("Erreur lors de l'importation du document : ${e.localizedMessage}"))
        }
    }

    fun formatFileSize(bytes: Long): String {
        if (bytes <= 0) return "0 Ko"
        val kb = bytes / 1024.0
        return if (kb < 1024.0) {
            String.format(Locale.FRANCE, "%.0f Ko", kb)
        } else {
            String.format(Locale.FRANCE, "%.2f Mo", kb / 1024.0)
        }
    }
}
