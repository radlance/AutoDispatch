package com.github.radlance.autodispatch.util

import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.utils.io.jvm.javaio.toInputStream
import java.io.File
import java.util.UUID

private val mode = System.getenv("MODE") ?: "debug"

val fileUploadDir
    get() = if (mode == "release") {
        File("/uploads")
    } else {
        File("uploaded_files")
    }

private fun saveFilePart(
    part: PartData.FileItem,
    uploadDir: File
): String {
    val extension = File(part.originalFileName ?: "file.jpg").extension
    val fileName = "${UUID.randomUUID()}.$extension"
    val file = File(uploadDir, fileName)

    part.provider().toInputStream().use { input ->
        file.outputStream().buffered().use { output ->
            input.copyTo(output)
        }
    }

    return "/static/$fileName"
}

private suspend fun handleFileUpload(
    call: ApplicationCall,
    uploadDir: File
): List<String> {
    val multipart = call.receiveMultipart()
    val photoUrls = mutableListOf<String>()

    multipart.forEachPart { part ->
        if (part is PartData.FileItem) {
            photoUrls += saveFilePart(part, uploadDir)
        }
        part.dispose()
    }

    return photoUrls
}


private fun deleteUploadedFiles(photoUrls: List<String>, uploadDir: File) {
    photoUrls.forEach { url ->
        val fileName = url.substringAfter("/static/")
        val file = File(uploadDir, fileName)
        if (file.exists()) file.delete()
    }
}

suspend fun ApplicationCall.processImagesUpload(
    uploadDir: File,
    block: suspend (photoUrls: List<String>) -> Unit
) {
    val photoUrls = handleFileUpload(this, uploadDir)

    try {
        block(photoUrls)
        respond(HttpStatusCode.OK)
    } catch (e: Exception) {
        deleteUploadedFiles(photoUrls, uploadDir)
        respond(HttpStatusCode.InternalServerError, e.message ?: "Unknown error")
    }
}

suspend fun ApplicationCall.processSingleImageUpload(
    uploadDir: File,
    fieldName: String = "file",
    block: suspend (imageUrl: String) -> Unit
) {
    val multipart = receiveMultipart()
    var uploadedFileUrl: String? = null

    multipart.forEachPart { part ->
        when (part) {
            is PartData.FileItem -> {
                if (part.name != fieldName) {
                    part.dispose()
                    return@forEachPart
                }

                if (uploadedFileUrl != null) {
                    part.dispose()
                    throw IllegalArgumentException("Only one file is allowed")
                }

                uploadedFileUrl = saveFilePart(part, uploadDir)
            }

            else -> Unit
        }
        part.dispose()
    }

    if (uploadedFileUrl == null) {
        throw IllegalArgumentException("Image file is required")
    }

    try {
        block(uploadedFileUrl)
        respond(HttpStatusCode.OK)
    } catch (e: Exception) {
        deleteUploadedFiles(listOf(uploadedFileUrl), uploadDir)
        throw e
    }
}
