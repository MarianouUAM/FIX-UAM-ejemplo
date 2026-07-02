package com.example.fixuamrepopoo.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.firestore.FirebaseFirestore // <-- IMPORTANTE: Librería de Firebase

import com.example.fixuamrepopoo.screens.*
import com.example.fixuamrepopoo.ui.theme.ConfiguracionTema
import com.example.fixuamrepopoo.ui.theme.LocalConfiguracionTema

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation() {
    val contexto = LocalContext.current

    // --- CONEXIÓN DIRECTA A FIREBASE ---
    val db = FirebaseFirestore.getInstance()
    var reportes by remember { mutableStateOf(emptyList<Reporte>()) }

    // Escuchador en tiempo real: Cualquier cambio en la nube se refleja al instante
    DisposableEffect(Unit) {
        val listener = db.collection("reportes")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    val listaFirebase = snapshot.documents.mapNotNull { doc ->
                        val reporte = doc.toObject(Reporte::class.java)
                        reporte?.apply { firestoreId = doc.id } // Guardamos el ID único del documento
                    }
                    reportes = listaFirebase
                }
            }
        onDispose { listener.remove() } // Se limpia para no gastar memoria
    }
    // -----------------------------------

    var pantallaActual by remember { mutableStateOf("login") }
    var rolSeleccionado by remember { mutableStateOf("") }
    var modoOscuro by remember { mutableStateOf(false) }

    var usuarioActual by remember { mutableStateOf("") }
    var usuarioActualUid by remember { mutableStateOf("") }

    var reporteTemporal by remember { mutableStateOf<Reporte?>(null) }
    var reporteSeleccionadoId by remember { mutableStateOf<Int?>(null) }

    fun irPantallaPrincipalPorRol(rol: String) {
        pantallaActual = when (rol) {
            "docente" -> "inicio_docente"
            "colaborador" -> "colaborador"
            "admin" -> "dashboard"
            else -> "login"
        }
    }

    fun cerrarSesionCompleta() {
        usuarioActual = ""
        usuarioActualUid = ""
        rolSeleccionado = ""
        reporteTemporal = null
        reporteSeleccionadoId = null
        pantallaActual = "login"
    }

    CompositionLocalProvider(
        LocalConfiguracionTema provides ConfiguracionTema(
            modoOscuro = modoOscuro,
            cambiarModo = { modoOscuro = !modoOscuro }
        )
    ) {
        AnimatedContent(
            targetState = pantallaActual,
            transitionSpec = {
                slideInHorizontally(
                    animationSpec = tween(350),
                    initialOffsetX = { fullWidth -> fullWidth }
                ) + fadeIn(animationSpec = tween(350)) togetherWith slideOutHorizontally(
                    animationSpec = tween(350),
                    targetOffsetX = { fullWidth -> -fullWidth }
                ) + fadeOut(animationSpec = tween(350))
            },
            label = "TransicionPantallas"
        ) { pantalla ->

            when (pantalla) {
                "login" -> LoginScreen(
                    seleccionarRol = { rol ->
                        rolSeleccionado = rol
                        pantallaActual = "credenciales"
                    }
                )

                "credenciales" -> CredencialesScreen(
                    rol = rolSeleccionado,
                    volver = { pantallaActual = "login" },
                    ingresar = { correo, contrasena, mostrarError ->
                        usuarioActual = "Docente Prueba"
                        usuarioActualUid = "uid_simulado_123"
                        irPantallaPrincipalPorRol(rolSeleccionado)
                    },
                    crearCuenta = { pantallaActual = "registro" }
                )

                "registro" -> RegistroScreen(
                    rol = rolSeleccionado,
                    volver = { pantallaActual = "credenciales" },
                    registrarUsuario = { nuevoUsuario, mostrarError ->
                        usuarioActual = nuevoUsuario.nombre
                        usuarioActualUid = "uid_simulado_nuevo_123"
                        rolSeleccionado = nuevoUsuario.rol
                        irPantallaPrincipalPorRol(nuevoUsuario.rol)
                    }
                )

                "dashboard" -> DashboardScreen(
                    rol = rolSeleccionado,
                    reportes = reportes,
                    usuarioNombre = usuarioActual,
                    onCerrarSesion = { cerrarSesionCompleta() },
                    onNavigateToInicio = { pantallaActual = "dashboard" },
                    eliminarReporte = { id ->
                        val reporte = reportes.find { it.id == id }
                        if (reporte != null && reporte.firestoreId.isNotEmpty()) {
                            db.collection("reportes").document(reporte.firestoreId).delete()
                            ImagenStorage.eliminarImagenSiEsInterna(contexto, reporte.fotoUri)
                        }
                    }
                    // NOTA: Si en tu DashboardScreen.kt viejo tenías el parámetro "reporteDao",
                    // tenés que quitárselo de allá también para que compile sin errores.
                )

                "inicio_docente" -> InicioDocenteScreen(
                    irNuevoReporte = { pantallaActual = "nuevo_reporte" },
                    irMisReportes = { pantallaActual = "mis_reportes" },
                    irPerfil = { pantallaActual = "perfil_docente" }
                )

                "nuevo_reporte" -> NuevoReporteScreen(
                    volver = { pantallaActual = "inicio_docente" },
                    continuar = { reporte ->
                        reporteTemporal = reporte.copy(
                            docente = usuarioActual,
                            docenteUid = usuarioActualUid,
                            estado = "Pendiente",
                            atendidoPor = "",
                            atendidoPorUid = ""
                        )
                        pantallaActual = "detalle_reporte"
                    }
                )

                "detalle_reporte" -> DetalleReporteScreen(
                    reporte = reporteTemporal,
                    volver = { pantallaActual = "nuevo_reporte" },
                    enviarReporte = {
                        reporteTemporal?.let { reporte ->
                            // --- GUARDAR EN FIREBASE ---
                            db.collection("reportes")
                                .add(reporte)
                                .addOnSuccessListener {
                                    reporteTemporal = null
                                    pantallaActual = "confirmacion"
                                }
                        }
                    }
                )

                "confirmacion" -> ConfirmacionScreen(
                    irInicio = { pantallaActual = "inicio_docente" },
                    irMisReportes = { pantallaActual = "mis_reportes" }
                )

                "mis_reportes" -> MisReportesScreen(
                    reportes = reportes.filter { it.docenteUid == usuarioActualUid },
                    volver = { pantallaActual = "inicio_docente" },
                    irDetalleEstado = { id ->
                        reporteSeleccionadoId = id
                        pantallaActual = "detalle_estado"
                    }
                )

                "detalle_estado" -> DetalleEstadoScreen(
                    reporte = reportes.find { it.id == reporteSeleccionadoId },
                    volver = { pantallaActual = "mis_reportes" },
                    cancelarReporte = { id ->
                        val reporte = reportes.find { it.id == id }
                        if (reporte != null && reporte.firestoreId.isNotEmpty()) {
                            db.collection("reportes").document(reporte.firestoreId).delete()
                                .addOnSuccessListener { pantallaActual = "mis_reportes" }
                            ImagenStorage.eliminarImagenSiEsInterna(contexto, reporte.fotoUri)
                        } else {
                            pantallaActual = "mis_reportes"
                        }
                    }
                )

                "perfil_docente" -> PerfilDocenteScreen(
                    volver = { pantallaActual = "inicio_docente" },
                    cerrarSesion = { cerrarSesionCompleta() }
                )

                "colaborador" -> ColaboradorScreen(
                    reportes = reportes,
                    colaboradorNombre = usuarioActual.ifBlank { "Soporte UAM" },
                    colaboradorUid = usuarioActualUid,
                    tomarReporte = { id ->
                        val reporte = reportes.find { it.id == id }
                        if (reporte != null && reporte.firestoreId.isNotEmpty()) {
                            // --- ACTUALIZAR EN FIREBASE ---
                            db.collection("reportes").document(reporte.firestoreId)
                                .update(
                                    "estado", "En proceso",
                                    "atendidoPor", usuarioActual.ifBlank { "Soporte UAM" },
                                    "atendidoPorUid", usuarioActualUid
                                )
                        }
                    },
                    resolverReporte = { id ->
                        val reporte = reportes.find { it.id == id }
                        if (reporte != null && reporte.firestoreId.isNotEmpty()) {
                            // --- RESOLVER EN FIREBASE ---
                            db.collection("reportes").document(reporte.firestoreId)
                                .update("estado", "Resuelto")
                        }
                    },
                    cerrarSesion = { cerrarSesionCompleta() }
                )
            }
        }
    }
}