package com.example.fixuamrepopoo.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.fixuamrepopoo.ui.theme.ColorGrisTexto
import com.example.fixuamrepopoo.ui.theme.ColorPrincipal
import com.example.fixuamrepopoo.ui.theme.ColorTexto

@Composable
fun DetalleReporteScreen(
    reporte: Reporte?,
    volver: () -> Unit,
    enviarReporte: () -> Unit
) {
    FondoPrincipal {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(22.dp)
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
                text = "Detalle del reporte",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = ColorTexto
            )

            Text(
                text = "Revisa la información antes de enviarla",
                color = ColorGrisTexto
            )

            Spacer(modifier = Modifier.height(22.dp))

            TarjetaBlanca(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Tipo de problema",
                    fontWeight = FontWeight.Bold,
                    color = ColorTexto
                )

                Text(
                    text = reporte?.tipo ?: "Sin tipo seleccionado",
                    color = ColorPrincipal
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Salón o aula",
                    fontWeight = FontWeight.Bold,
                    color = ColorTexto
                )

                Text(
                    text = reporte?.aula ?: "Sin aula registrada",
                    color = ColorGrisTexto
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Descripción",
                    fontWeight = FontWeight.Bold,
                    color = ColorTexto
                )

                Text(
                    text = reporte?.descripcion ?: "Sin descripción registrada",
                    color = ColorGrisTexto
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Foto adjunta",
                    fontWeight = FontWeight.Bold,
                    color = ColorTexto
                )

                Text(
                    text = if (
                        reporte?.fotoUri?.isNotBlank() == true ||
                        reporte?.fotoBitmap != null
                    ) {
                        "Sí, el reporte incluye una foto del problema."
                    } else {
                        "No se adjuntó foto."
                    },
                    color = if (
                        reporte?.fotoUri?.isNotBlank() == true ||
                        reporte?.fotoBitmap != null
                    ) {
                        ColorPrincipal
                    } else {
                        ColorGrisTexto
                    }
                )

                // Acá está corregido, un solo bloque limpio y validando nulos correctamente
                reporte?.fotoBitmap?.let { bitmapSeguro ->
                    Image(
                        bitmap = bitmapSeguro.asImageBitmap(),
                        contentDescription = "Foto tomada del problema",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Prioridad",
                    fontWeight = FontWeight.Bold,
                    color = ColorTexto
                )

                Text(
                    text = reporte?.prioridad ?: "Media",
                    color = ColorGrisTexto
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Estado inicial",
                    fontWeight = FontWeight.Bold,
                    color = ColorTexto
                )

                Text(
                    text = reporte?.estado ?: "Pendiente",
                    color = ColorGrisTexto
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            BotonPrincipal(
                texto = "Enviar reporte",
                onClick = {
                    enviarReporte()
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            BotonOscuro(
                texto = "Cancelar",
                onClick = {
                    volver()
                }
            )
        }
    }
}