package com.example.fixuamrepopoo.screens

import android.graphics.Bitmap
import com.google.firebase.firestore.Exclude

data class Reporte(
    val id: Int = 0,
    var firestoreId: String = "", // Cambiado a var para que Firebase le asigne el suyo
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
    // Firebase ignorará esto gracias al @get:Exclude
    @get:Exclude
    var fotoBitmap: Bitmap? = null
}