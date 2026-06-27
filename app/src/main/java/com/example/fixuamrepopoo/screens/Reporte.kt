package com.example.fixuamrepopoo.screens

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "reportes")
data class Reporte(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val firestoreId: String = "",
    val docenteUid: String = "",
    val docente: String = "",
    val tipo: String = "",
    val aula: String = "",
    val descripcion: String = "",
    val fecha: String = "",
    val prioridad: String = "Media",
    val estado: String = "Pendiente",
    val atendidoPor: String = "",
    val atendidoPorUid: String = "",
    val fotoUri: String = ""
) {
    // Al ponerlo aquí adentro, Room lo ignora felizmente
    // y Kotlin ya no lo exige para construir el objeto.
    @Ignore
    var fotoBitmap: Bitmap? = null
}