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
import androidx.compose.runtime.collectAsState

import com.example.fixuamrepopoo.screens.*
import com.example.fixuamrepopoo.ui.theme.ConfiguracionTema
import com.example.fixuamrepopoo.ui.theme.LocalConfiguracionTema
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation() {

    // --- CONFIGURACIÓN DE ROOM ---
    val api = remember { RetrofitCliente.api }
    val contexto = LocalContext.current
    val baseDatos = remember { BaseDatosApp.obtenerBaseDatos(contexto) }
    val reporteDao = baseDatos.reporteDao()
    val scope = rememberCoroutineScope() // Para ejecutar acciones en 2do plano sin congelar la app

    // Escuchamos la base de datos en tiempo real.
    // Si hay un cambio en SQL, la lista 'reportes' se actualiza sola.
    val reportes by reporteDao.obtenerTodosLosReportes().collectAsState(initial = emptyList())
    // -----------------------------

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
                        // Sigue siendo simulado por ahora (luego conectaremos esto a la API)
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
                        if (reporte != null) {
                            scope.launch { reporteDao.eliminarReporte(reporte) }
                        }
                    }
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
                            scope.launch {
                                try {
                                    // 1. Intentamos enviar al servidor de Python (si está prendido)
                                    RetrofitCliente.api.crearReporte(reporte)

                                    // 2. Si el servidor respondió bien, guardamos en Room
                                    reporteDao.insertarReporte(reporte)

                                    reporteTemporal = null
                                    pantallaActual = "confirmacion"
                                } catch (e: Exception) {
                                    // 3. Si el servidor está apagado o falla, guardamos en Room localmente
                                    // para que el trabajo del estudiante no se pierda.
                                    reporteDao.insertarReporte(reporte)
                                    reporteTemporal = null
                                    pantallaActual = "confirmacion"
                                }
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
                        if (reporte != null) {
                            // BORRAR REAL EN ROOM
                            scope.launch {
                                reporteDao.eliminarReporte(reporte)
                                pantallaActual = "mis_reportes"
                            }
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
                        if (reporte != null) {
                            // ACTUALIZAR REAL EN ROOM
                            scope.launch {
                                reporteDao.actualizarReporte(
                                    reporte.copy(
                                        estado = "En proceso",
                                        atendidoPor = usuarioActual.ifBlank { "Soporte UAM" },
                                        atendidoPorUid = usuarioActualUid
                                    )
                                )
                            }
                        }
                    },
                    resolverReporte = { id ->
                        val reporte = reportes.find { it.id == id }
                        if (reporte != null) {
                            // ACTUALIZAR REAL EN ROOM
                            scope.launch {
                                reporteDao.actualizarReporte(reporte.copy(estado = "Resuelto"))
                            }
                        }
                    },
                    cerrarSesion = { cerrarSesionCompleta() }
                )
            }
        }
    }
}