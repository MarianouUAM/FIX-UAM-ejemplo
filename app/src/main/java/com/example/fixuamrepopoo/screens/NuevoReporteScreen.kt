package com.example.fixuamrepopoo.screens

import androidx.compose.ui.platform.LocalContext
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fixuamrepopoo.ui.theme.ColorGrisTexto
import com.example.fixuamrepopoo.ui.theme.ColorPrincipal
import com.example.fixuamrepopoo.ui.theme.ColorRojo
import com.example.fixuamrepopoo.ui.theme.ColorTexto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

@Composable
fun NuevoReporteScreen(
    volver: () -> Unit,
    continuar: (Reporte) -> Unit
) {
    var tipoSeleccionado by remember { mutableStateOf("Internet / Wi-Fi") }
    var aula by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fotoUri by remember { mutableStateOf("") }
    var fotoBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var error by remember { mutableStateOf("") }
    val contexto = LocalContext.current

    val selectorFotoGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            fotoUri = uri.toString()
            fotoBitmap = null
            error = ""
        }
    }

    val tomarFotoCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            fotoBitmap = bitmap
            fotoUri = ""
            error = ""
        }
    }
    fun abrirCamaraSeguro() {
        try {
            tomarFotoCamara.launch(null)
        } catch (e: Exception) {
            error = "No se pudo abrir la cámara."
        }
    }

    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { permisoConcedido ->
        if (permisoConcedido) {
            abrirCamaraSeguro()
        } else {
            error = "Se necesita permiso de cámara para tomar la foto."
        }
    }

    val tipos = listOf(
        "Computadora",
        "Proyector",
        "Pantalla interactiva",
        "Internet / Wi-Fi",
        "Cable HDMI",
        "Accesorios",
        "Otro"
    )

    FondoPrincipal {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(22.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(26.dp))

            Text(
                text = "< Volver",
                color = ColorPrincipal,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    volver()
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Nuevo reporte",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = ColorTexto
            )

            Text(
                text = "Seleccioná el tipo de problema encontrado en el aula",
                color = ColorGrisTexto
            )

            Spacer(modifier = Modifier.height(18.dp))

            tipos.forEach { tipo ->
                OpcionProblema(
                    texto = tipo,
                    seleccionado = tipo == tipoSeleccionado,
                    onClick = {
                        tipoSeleccionado = tipo
                        error = ""
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = aula,
                onValueChange = {
                    aula = it
                    error = ""
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Salón o aula donde está la falla")
                },
                placeholder = {
                    Text("Ej: A201, B105, C302")
                },
                singleLine = true,
                shape = RoundedCornerShape(18.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = descripcion,
                onValueChange = {
                    descripcion = it
                    error = ""
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                label = {
                    Text("Describe el problema")
                },
                placeholder = {
                    Text("Ej: El proyector no enciende.")
                },
                shape = RoundedCornerShape(18.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            TarjetaBlanca(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Foto del problema",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorTexto
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = when {
                        fotoBitmap != null -> "Foto tomada con la cámara."
                        fotoUri.isNotBlank() -> "Foto seleccionada desde la galería."
                        else -> "Podés tomar una foto o elegir una imagen como evidencia."
                    },
                    color = if (fotoBitmap != null || fotoUri.isNotBlank()) {
                        ColorPrincipal
                    } else {
                        ColorGrisTexto
                    }
                )

                val bitmapVistaPrevia = when {
                    fotoBitmap != null -> fotoBitmap
                    fotoUri.isNotBlank() -> ImagenStorage.cargarBitmap(contexto, fotoUri)
                    else -> null
                }

                if (bitmapVistaPrevia != null) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Image(
                        bitmap = bitmapVistaPrevia.asImageBitmap(),
                        contentDescription = "Foto del problema",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                BotonPrincipal(
                    texto = "Tomar foto",
                    onClick = {
                        val permiso = ContextCompat.checkSelfPermission(
                            contexto,
                            Manifest.permission.CAMERA
                        )

                        if (permiso == PackageManager.PERMISSION_GRANTED) {
                            abrirCamaraSeguro()
                        } else {
                            permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                BotonOscuro(
                    texto = "Elegir de galería",
                    onClick = {
                        selectorFotoGaleria.launch("image/*")
                    }
                )
            }

            if (error.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = error,
                    color = ColorRojo,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            BotonPrincipal(
                texto = "Continuar",
                onClick = {
                    error = when {
                        aula.isBlank() -> "Ingresá el salón o aula donde está la falla"
                        descripcion.isBlank() -> "Describí el problema encontrado"
                        descripcion.length < 8 -> "La descripción debe ser un poco más clara"
                        else -> ""
                    }

                    if (error.isEmpty()) {
                        val fechaActual = SimpleDateFormat(
                            "dd/MM/yyyy HH:mm",
                            Locale.getDefault()
                        ).format(Date())

                        val rutaFotoGuardada = when
                        {
                            fotoBitmap != null -> ImagenStorage.guardarBitmap(contexto, fotoBitmap!!)
                            fotoUri.isNotBlank() -> ImagenStorage.copiarImagenDesdeUri(contexto, fotoUri)
                            else -> ""
                        }
                        val nuevoReporte = Reporte(
                            id = (System.currentTimeMillis() % 1000000).toInt(),
                            tipo = tipoSeleccionado,
                            aula = aula.trim(),
                            descripcion = descripcion.trim(),
                            fecha = fechaActual,
                            prioridad = "Media",
                            estado = "Pendiente",
                            atendidoPor = "",
                            atendidoPorUid = "",
                            fotoUri = rutaFotoGuardada,
                        )
                        continuar(nuevoReporte)
                    }
                }
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun OpcionProblema(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(
            width = if (seleccionado) 2.dp else 1.dp,
            color = if (seleccionado) ColorPrincipal else Color(0xFFE0E0E0)
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = texto,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                color = ColorTexto,
                fontSize = 17.sp
            )

            RadioButton(
                selected = seleccionado,
                onClick = onClick
            )
        }
    }
}