package com.example.fixuamrepopoo.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fixuamrepopoo.ui.theme.ColorGrisTexto
import com.example.fixuamrepopoo.ui.theme.ColorNaranja
import com.example.fixuamrepopoo.ui.theme.ColorPrincipal
import com.example.fixuamrepopoo.ui.theme.ColorRojo
import com.example.fixuamrepopoo.ui.theme.ColorTexto
import com.example.fixuamrepopoo.ui.theme.ColorVerde

data class EstadisticasDashboard(
    val totalReportes: Int,
    val pendientes: Int,
    val enProceso: Int,
    val resueltos: Int
)

data class ItemEstadoDashboard(
    val nombre: String,
    val valor: Int,
    val color: Color
)

data class ItemTipoDashboard(
    val nombre: String,
    val valor: Int,
    val color: Color
)

@Composable
fun DashboardScreen(
    rol: String,
    reportes: List<Reporte>,
    usuarioNombre: String,
    onCerrarSesion: () -> Unit,
    onNavigateToInicio: () -> Unit,
    eliminarReporte: (Int) -> Unit = {}
) {
    var filtroAula by remember { mutableStateOf("") }
    var filtroTipo by remember { mutableStateOf("") }

    // Esta lista cambia automáticamente si escribís en las cajitas de texto
    val reportesFiltrados = reportes.filter {
        (filtroAula.isBlank() || it.aula.contains(filtroAula, ignoreCase = true)) &&
                (filtroTipo.isBlank() || it.tipo.contains(filtroTipo, ignoreCase = true))
    }
    var pestanaActual by remember { mutableStateOf("resumen") }

    val reportesVisibles = reportes.sortedByDescending { it.id }

    val estadisticas = EstadisticasDashboard(
        totalReportes = reportesVisibles.size,
        pendientes = reportesVisibles.count { it.estado == "Pendiente" },
        enProceso = reportesVisibles.count { it.estado == "En proceso" },
        resueltos = reportesVisibles.count { it.estado == "Resuelto" }
    )

    val activos = estadisticas.pendientes + estadisticas.enProceso

    val tasaResolucion = if (estadisticas.totalReportes == 0) {
        0.0
    } else {
        (estadisticas.resueltos.toDouble() / estadisticas.totalReportes.toDouble()) * 100
    }

    val estados = listOf(
        ItemEstadoDashboard("Pendientes", estadisticas.pendientes, ColorRojo),
        ItemEstadoDashboard("En proceso", estadisticas.enProceso, ColorNaranja),
        ItemEstadoDashboard("Resueltos", estadisticas.resueltos, ColorVerde)
    )

    val coloresTipos = listOf(
        ColorPrincipal,
        Color(0xFF5C6BC0),
        Color(0xFFFFA726),
        Color(0xFF26A69A),
        Color(0xFFE57373)
    )

    val tiposProblema = reportesVisibles
        .groupingBy { it.tipo }
        .eachCount()
        .entries
        .sortedByDescending { it.value }
        .take(5)
        .mapIndexed { index, item ->
            ItemTipoDashboard(
                nombre = item.key,
                valor = item.value,
                color = coloresTipos[index % coloresTipos.size]
            )
        }

    val reportesPendientes = reportesVisibles.filter { it.estado == "Pendiente" }
    val reportesEnProceso = reportesVisibles.filter { it.estado == "En proceso" }
    val reportesResueltos = reportesVisibles.filter { it.estado == "Resuelto" }
    val reportesRecientes = reportesVisibles.take(6)

    FondoPrincipal {
        // Estados para filtros
        var filtroAula by remember { mutableStateOf("") }
        var filtroTipo by remember { mutableStateOf("") }

// Lógica de filtrado
        val reportesFiltrados = reportes.filter {
            (filtroAula.isBlank() || it.aula.contains(filtroAula, ignoreCase = true)) &&
                    (filtroTipo.isBlank() || it.tipo == filtroTipo)
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 18.dp, bottom = 22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                EncabezadoDashboard(
                    usuarioNombre = usuarioNombre.ifBlank { "Administrador" },
                    activos = activos,
                    tasaResolucion = tasaResolucion
                )
            }
            
            item {
                TabsDashboard(
                    pestanaActual = pestanaActual,
                    cambiarPestana = { pestanaActual = it }
                )
            }

            when (pestanaActual) {
                "resumen" -> {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            TarjetaIndicador(
                                titulo = "Total",
                                valor = estadisticas.totalReportes.toString(),
                                detalle = "reportes",
                                color = ColorPrincipal,
                                modifier = Modifier.weight(1f)
                            )

                            TarjetaIndicador(
                                titulo = "Activos",
                                valor = activos.toString(),
                                detalle = "en atención",
                                color = ColorNaranja,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            TarjetaIndicador(
                                titulo = "Pendientes",
                                valor = estadisticas.pendientes.toString(),
                                detalle = "sin asignar",
                                color = ColorRojo,
                                modifier = Modifier.weight(1f)
                            )

                            TarjetaIndicador(
                                titulo = "Resueltos",
                                valor = estadisticas.resueltos.toString(),
                                detalle = "finalizados",
                                color = ColorVerde,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        GraficoEstados(
                            total = estadisticas.totalReportes,
                            estados = estados
                        )
                    }

                    item {
                        GraficoTiposProblema(datos = tiposProblema)
                    }

                    item {
                        Text(
                            text = "Actividad reciente",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorTexto
                        )
                    }

                    if (reportesRecientes.isEmpty()) {
                        item {
                            MensajeDashboardVacio(
                                titulo = "Sin actividad todavía",
                                descripcion = "Cuando se registren reportes desde Firestore, aparecerán aquí en tiempo real."
                            )
                        }
                    } else {
                        items(
                            items = reportesRecientes,
                            key = { it.firestoreId.ifBlank { "local-${it.id}" } }
                        ) { reporte ->
                            TarjetaReporteReciente(reporte = reporte)
                        }
                    }
                }

                "pendientes" -> {
                    if (reportesPendientes.isEmpty()) {
                        item {
                            MensajeDashboardVacio(
                                titulo = "Todo al día",
                                descripcion = "No hay reportes pendientes por revisar."
                            )
                        }
                    } else {
                        items(
                            items = reportesPendientes,
                            key = { it.firestoreId.ifBlank { "local-${it.id}" } }
                        ) { reporte ->
                            TarjetaReporteAdminDashboard(
                                reporte = reporte,
                                textoBoton = "Eliminar reporte",
                                colorBoton = ColorRojo,
                                botonActivo = true,
                                onClickBoton = {
                                    eliminarReporte(reporte.id)
                                }
                            )
                        }
                    }
                }

                "historial" -> {
                    val historial = reportesEnProceso + reportesResueltos

                    if (historial.isEmpty()) {
                        item {
                            MensajeDashboardVacio(
                                titulo = "Historial vacío",
                                descripcion = "Todavía no hay reportes en proceso ni reportes resueltos."
                            )
                        }
                    } else {
                        items(
                            items = historial,
                            key = { it.firestoreId.ifBlank { "local-${it.id}" } }
                        ) { reporte ->
                            TarjetaReporteAdminDashboard(
                                reporte = reporte,
                                textoBoton = if (reporte.estado == "Resuelto") {
                                    "Reporte finalizado"
                                } else {
                                    "Reporte en proceso"
                                },
                                colorBoton = if (reporte.estado == "Resuelto") {
                                    ColorVerde
                                } else {
                                    ColorNaranja
                                },
                                botonActivo = false,
                                onClickBoton = {}
                            )
                        }
                    }
                }

                "personal" -> {
                    item {
                        TarjetaPersonalDashboard(
                            nombre = "Carlos López",
                            rol = "Colaborador técnico",
                            area = "Dirección Tecnológica"
                        )
                    }

                    item {
                        TarjetaPersonalDashboard(
                            nombre = "Ana Martínez",
                            rol = "Soporte de aulas",
                            area = "Tecnología educativa"
                        )
                    }

                    item {
                        TarjetaPersonalDashboard(
                            nombre = usuarioNombre.ifBlank { "Administrador" },
                            rol = "Administrador",
                            area = "Gestión de incidencias"
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(6.dp))

                BotonOscuro(
                    texto = "Cerrar sesión",
                    onClick = onCerrarSesion
                )
            }
        }
    }
}

@Composable
fun EncabezadoDashboard(
    usuarioNombre: String,
    activos: Int,
    tasaResolucion: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF123B40)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(ColorPrincipal, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = inicialesUsuario(usuarioNombre),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Panel administrativo",
                        color = Color.White,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = usuarioNombre,
                        color = Color(0xFFC9E6E8),
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MiniDatoHeader(
                    titulo = "Activos",
                    valor = activos.toString(),
                    modifier = Modifier.weight(1f)
                )

                MiniDatoHeader(
                    titulo = "Cierre",
                    valor = String.format("%.0f", tasaResolucion) + "%",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun MiniDatoHeader(
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.12f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = titulo,
                color = Color(0xFFC9E6E8),
                fontSize = 13.sp
            )

            Text(
                text = valor,
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TabsDashboard(
    pestanaActual: String,
    cambiarPestana: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BotonTabDashboard(
                texto = "Resumen",
                seleccionado = pestanaActual == "resumen",
                modifier = Modifier.weight(1f),
                onClick = { cambiarPestana("resumen") }
            )

            BotonTabDashboard(
                texto = "Pendientes",
                seleccionado = pestanaActual == "pendientes",
                modifier = Modifier.weight(1f),
                onClick = { cambiarPestana("pendientes") }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BotonTabDashboard(
                texto = "Historial",
                seleccionado = pestanaActual == "historial",
                modifier = Modifier.weight(1f),
                onClick = { cambiarPestana("historial") }
            )

            BotonTabDashboard(
                texto = "Personal",
                seleccionado = pestanaActual == "personal",
                modifier = Modifier.weight(1f),
                onClick = { cambiarPestana("personal") }
            )
        }
    }
}

@Composable
fun BotonTabDashboard(
    texto: String,
    seleccionado: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(54.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionado) ColorPrincipal else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = texto,
                color = if (seleccionado) Color.White else ColorTexto,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun TarjetaIndicador(
    titulo: String,
    valor: String,
    detalle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(128.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(color, CircleShape)
            )

            Column {
                Text(
                    text = valor,
                    color = ColorTexto,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = titulo,
                    color = ColorTexto,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = detalle,
                    color = ColorGrisTexto,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun GraficoEstados(
    total: Int,
    estados: List<ItemEstadoDashboard>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Distribución por estado",
                color = ColorTexto,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Resumen visual del flujo de atención",
                color = ColorGrisTexto,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(145.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(
                        modifier = Modifier.size(145.dp)
                    ) {
                        val stroke = Stroke(
                            width = 22.dp.toPx(),
                            cap = StrokeCap.Round
                        )

                        if (total == 0) {
                            drawArc(
                                color = Color(0xFFE7EEF0),
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = stroke
                            )
                        } else {
                            var startAngle = -90f

                            estados.forEach { item ->
                                val sweep = (item.valor.toFloat() / total.toFloat()) * 360f

                                if (item.valor > 0) {
                                    drawArc(
                                        color = item.color,
                                        startAngle = startAngle,
                                        sweepAngle = sweep,
                                        useCenter = false,
                                        style = stroke
                                    )
                                }

                                startAngle += sweep
                            }
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = total.toString(),
                            color = ColorTexto,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "total",
                            color = ColorGrisTexto,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(22.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    estados.forEach { item ->
                        FilaLeyendaEstado(item = item)
                    }
                }
            }
        }
    }
}

@Composable
fun FilaLeyendaEstado(
    item: ItemEstadoDashboard
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(item.color, CircleShape)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = item.nombre,
            modifier = Modifier.weight(1f),
            color = ColorTexto,
            fontSize = 14.sp
        )

        Text(
            text = item.valor.toString(),
            color = ColorTexto,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun GraficoTiposProblema(
    datos: List<ItemTipoDashboard>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Problemas más reportados",
                color = ColorTexto,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Áreas con mayor carga de soporte",
                color = ColorGrisTexto,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (datos.isEmpty()) {
                Text(
                    text = "Aún no hay datos suficientes para generar el gráfico.",
                    color = ColorGrisTexto,
                    fontSize = 15.sp
                )
            } else {
                val maximo = datos.maxOf { it.valor }

                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    datos.forEach { item ->
                        BarraTipoProblema(
                            item = item,
                            maximo = maximo
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BarraTipoProblema(
    item: ItemTipoDashboard,
    maximo: Int
) {
    val porcentaje = if (maximo == 0) {
        0f
    } else {
        item.valor.toFloat() / maximo.toFloat()
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.nombre,
                modifier = Modifier.weight(1f),
                color = ColorTexto,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = item.valor.toString(),
                color = ColorGrisTexto,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(11.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFEAF1F2))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(porcentaje.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .background(item.color)
            )
        }
    }
}

@Composable
fun TarjetaReporteReciente(
    reporte: Reporte
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = reporte.tipo,
                        color = ColorTexto,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "${reporte.aula} · ${reporte.docente}",
                        color = ColorGrisTexto,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                EstadoChipDashboard(estado = reporte.estado)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = reporte.descripcion,
                color = ColorGrisTexto,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = reporte.fecha,
                color = ColorPrincipal,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            if (reporte.atendidoPor.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Atendido por: ${reporte.atendidoPor}",
                    color = ColorPrincipal,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun TarjetaReporteAdminDashboard(
    reporte: Reporte,
    textoBoton: String,
    colorBoton: Color,
    botonActivo: Boolean,
    onClickBoton: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "RPT-${reporte.id}",
                        color = ColorPrincipal,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = reporte.tipo,
                        color = ColorTexto,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                EstadoChipDashboard(estado = reporte.estado)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Docente: ${reporte.docente}",
                color = ColorGrisTexto,
                fontSize = 15.sp
            )

            Text(
                text = "Aula: ${reporte.aula}",
                color = ColorGrisTexto,
                fontSize = 15.sp
            )

            Text(
                text = "Fecha: ${reporte.fecha}",
                color = ColorGrisTexto,
                fontSize = 15.sp
            )

            if (reporte.atendidoPor.isNotBlank()) {
                Text(
                    text = "Atendido por: ${reporte.atendidoPor}",
                    color = ColorPrincipal,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = reporte.descripcion,
                color = ColorTexto,
                fontSize = 15.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (botonActivo) {
                            colorBoton
                        } else {
                            colorBoton.copy(alpha = 0.16f)
                        }
                    )
                    .clickable(enabled = botonActivo) {
                        onClickBoton()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = textoBoton,
                    color = if (botonActivo) Color.White else colorBoton,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun TarjetaPersonalDashboard(
    nombre: String,
    rol: String,
    area: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(ColorPrincipal.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = inicialesUsuario(nombre),
                    color = ColorPrincipal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = nombre,
                    color = ColorTexto,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = rol,
                    color = ColorPrincipal,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = area,
                    color = ColorGrisTexto,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun EstadoChipDashboard(
    estado: String
) {
    val color = when (estado) {
        "Pendiente" -> ColorRojo
        "En proceso" -> ColorNaranja
        "Resuelto" -> ColorVerde
        else -> ColorPrincipal
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(40.dp))
            .background(color.copy(alpha = 0.13f))
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = estado,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MensajeDashboardVacio(
    titulo: String,
    descripcion: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = titulo,
                color = ColorTexto,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = descripcion,
                color = ColorGrisTexto,
                fontSize = 15.sp
            )
        }
    }
}

fun inicialesUsuario(nombre: String): String {
    val partes = nombre.trim().split(" ").filter { it.isNotBlank() }

    return when {
        partes.size >= 2 -> "${partes[0].first()}${partes[1].first()}".uppercase()
        partes.size == 1 -> partes[0].take(2).uppercase()
        else -> "AD"
    }
}