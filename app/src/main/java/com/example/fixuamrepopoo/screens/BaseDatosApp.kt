package com.example.fixuamrepopoo.screens

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Reporte::class], version = 1, exportSchema = false)
abstract class BaseDatosApp : RoomDatabase() {

    abstract fun reporteDao(): ReporteDao

    companion object {
        @Volatile
        private var INSTANCE: BaseDatosApp? = null

        fun obtenerBaseDatos(context: Context): BaseDatosApp {
            return INSTANCE ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    BaseDatosApp::class.java,
                    "fixuam_bd" // Nombre del archivo de la base de datos en el cel
                ).build()
                INSTANCE = instancia
                instancia
            }
        }
    }
}