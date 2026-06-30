package com.example.fixuamrepopoo.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

/**
 * Maneja las imágenes de los reportes.
 *
 * Room no guarda Bitmaps directamente. Por eso la imagen se copia/guarda como
 * archivo dentro del almacenamiento interno de la app y en Room solo se guarda
 * la ruta del archivo en el campo fotoUri del Reporte.
 */
object ImagenStorage {

    private const val CARPETA_REPORTES = "imagenes_reportes"

    fun guardarBitmap(context: Context, bitmap: Bitmap): String {
        val archivo = crearArchivoImagen(context)

        FileOutputStream(archivo).use { salida ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, salida)
        }

        return archivo.absolutePath
    }

    fun copiarImagenDesdeUri(context: Context, uriTexto: String): String {
        if (uriTexto.isBlank()) return ""

        return try {
            val uri = Uri.parse(uriTexto)
            val archivo = crearArchivoImagen(context)

            context.contentResolver.openInputStream(uri)?.use { entrada ->
                FileOutputStream(archivo).use { salida ->
                    entrada.copyTo(salida)
                }
            } ?: return uriTexto

            archivo.absolutePath
        } catch (e: Exception) {
            uriTexto
        }
    }

    fun cargarBitmap(context: Context, ruta: String): Bitmap? {
        if (ruta.isBlank()) return null

        return try {
            when {
                ruta.startsWith("content://") || ruta.startsWith("file://") -> {
                    context.contentResolver.openInputStream(Uri.parse(ruta))?.use { entrada ->
                        BitmapFactory.decodeStream(entrada)
                    }
                }

                else -> BitmapFactory.decodeFile(ruta)
            }
        } catch (e: Exception) {
            null
        }
    }

    fun eliminarImagenSiEsInterna(context: Context, ruta: String) {
        if (ruta.isBlank()) return

        try {
            val carpetaInterna = File(context.filesDir, CARPETA_REPORTES).absolutePath
            val archivo = File(ruta)

            if (archivo.absolutePath.startsWith(carpetaInterna) && archivo.exists()) {
                archivo.delete()
            }
        } catch (_: Exception) {
            // Si no se puede borrar la imagen, no detenemos el flujo principal.
        }
    }

    private fun crearArchivoImagen(context: Context):  File {
        val directorio = File(context.filesDir, CARPETA_REPORTES)

        if (!directorio.exists()) {
            directorio.mkdirs()
        }

        return File(
            directorio,
            "reporte_${System.currentTimeMillis()}.jpg"
        )
    }
}