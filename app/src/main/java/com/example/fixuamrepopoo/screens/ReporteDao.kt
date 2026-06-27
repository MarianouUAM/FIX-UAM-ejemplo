package com.example.fixuamrepopoo.screens

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReporteDao {

    // Insertar un reporte nuevo
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarReporte(reporte: Reporte)

    // Actualizar un reporte existente (ej. cambiar su estado)
    @Update
    suspend fun actualizarReporte(reporte: Reporte)

    // Borrar un reporte
    @Delete
    suspend fun eliminarReporte(reporte: Reporte)

    // Traer todos los reportes en tiempo real
    @Query("SELECT * FROM reportes ORDER BY id DESC")
    fun obtenerTodosLosReportes(): Flow<List<Reporte>>

    // Traer reportes solo de un docente (por si lo ocupamos luego)
    @Query("SELECT * FROM reportes WHERE docenteUid = :uid ORDER BY id DESC")
    fun obtenerReportesPorDocente(uid: String): Flow<List<Reporte>>
}