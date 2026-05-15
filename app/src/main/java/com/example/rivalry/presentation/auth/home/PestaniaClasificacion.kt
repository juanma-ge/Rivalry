package com.example.rivalry.presentation.auth.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rivalry.domain.model.Partido
import com.example.rivalry.domain.model.MiembroUI

data class EquipoClasificacion(
    val nombre: String,
    var puntos: Int = 0,
    var jugados: Int = 0,
    var victorias: Int = 0,
    var empates: Int = 0,
    var derrotas: Int = 0,
    var golesFavor: Int = 0,
    var golesContra: Int = 0
) {
    val diferenciaGoles: Int get() = golesFavor - golesContra
}

@Composable
fun PestaniaClasificacion(miembros: List<MiembroUI>, partidos: List<Partido>) {
    val tablaOrdenada = remember(miembros, partidos) {
        val mapaEquipos = mutableMapOf<String, EquipoClasificacion>()

        miembros.forEach { miembro ->
            mapaEquipos[miembro.nombreEquipo] = EquipoClasificacion(nombre = miembro.nombreEquipo)
        }

        partidos.filter { it.estado == "FINALIZADO" }.forEach { p ->
            val local = mapaEquipos[p.nombreLocal]
            val visitante = mapaEquipos[p.nombreVisitante]

            if (local != null && visitante != null) {
                local.jugados++
                visitante.jugados++
                local.golesFavor += p.golesLocal
                local.golesContra += p.golesVisitante
                visitante.golesFavor += p.golesVisitante
                visitante.golesContra += p.golesLocal

                when {
                    p.golesLocal > p.golesVisitante -> {
                        local.puntos += 3; local.victorias++; visitante.derrotas++
                    }
                    p.golesLocal < p.golesVisitante -> {
                        visitante.puntos += 3; visitante.victorias++; local.derrotas++
                    }
                    else -> {
                        local.puntos += 1; visitante.puntos += 1; local.empates++; visitante.empates++
                    }
                }
            }
        }

        mapaEquipos.values.sortedWith(
            compareByDescending<EquipoClasificacion> { it.puntos }
                .thenByDescending { it.diferenciaGoles }
                .thenByDescending { it.golesFavor }
                .thenBy { it.nombre }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().background(Color.LightGray).padding(vertical = 8.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("#", modifier = Modifier.width(20.dp), fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text("Equipo", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text("PTS", modifier = Modifier.width(28.dp), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, fontSize = 11.sp)
            Text("PJ", modifier = Modifier.width(22.dp), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, fontSize = 11.sp)
            Text("V", modifier = Modifier.width(20.dp), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, fontSize = 11.sp)
            Text("E", modifier = Modifier.width(20.dp), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, fontSize = 11.sp)
            Text("D", modifier = Modifier.width(20.dp), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, fontSize = 11.sp)
            Text("GF", modifier = Modifier.width(24.dp), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, fontSize = 11.sp)
            Text("GC", modifier = Modifier.width(24.dp), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, fontSize = 11.sp)
            Text("DG", modifier = Modifier.width(28.dp), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, fontSize = 11.sp)
        }
        HorizontalDivider()

        LazyColumn {
            itemsIndexed(tablaOrdenada) { index, equipo ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${index + 1}", modifier = Modifier.width(20.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(equipo.nombre, modifier = Modifier.weight(1f), fontSize = 13.sp, maxLines = 1)
                    Text("${equipo.puntos}", modifier = Modifier.width(28.dp), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center, fontSize = 13.sp)
                    Text("${equipo.jugados}", modifier = Modifier.width(22.dp), textAlign = TextAlign.Center, fontSize = 12.sp)
                    Text("${equipo.victorias}", modifier = Modifier.width(20.dp), textAlign = TextAlign.Center, fontSize = 12.sp)
                    Text("${equipo.empates}", modifier = Modifier.width(20.dp), textAlign = TextAlign.Center, fontSize = 12.sp)
                    Text("${equipo.derrotas}", modifier = Modifier.width(20.dp), textAlign = TextAlign.Center, fontSize = 12.sp)
                    Text("${equipo.golesFavor}", modifier = Modifier.width(24.dp), textAlign = TextAlign.Center, fontSize = 12.sp)
                    Text("${equipo.golesContra}", modifier = Modifier.width(24.dp), textAlign = TextAlign.Center, fontSize = 12.sp)
                    Text(
                        text = if (equipo.diferenciaGoles > 0) "+${equipo.diferenciaGoles}" else "${equipo.diferenciaGoles}",
                        modifier = Modifier.width(28.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color = if (equipo.diferenciaGoles > 0) Color(0xFF4CAF50) else if (equipo.diferenciaGoles < 0) Color.Red else Color.Black
                    )
                }
                HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
            }
        }
    }
}