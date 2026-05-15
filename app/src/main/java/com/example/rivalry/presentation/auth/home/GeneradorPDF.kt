package com.example.rivalry.presentation.auth.home

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.rivalry.domain.model.Partido
import com.example.rivalry.domain.model.MiembroUI
import java.io.File
import java.io.FileOutputStream

// Clase interna para no depender de otros archivos al calcular
private data class EquipoPDF(
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

object GeneradorPDF {

    fun generarYCompartir(context: Context, nombreLiga: String, miembros: List<MiembroUI>, partidos: List<Partido>) {

        // 1. CÁLCULO DE LA CLASIFICACIÓN
        val mapaEquipos = mutableMapOf<String, EquipoPDF>()
        miembros.forEach { mapaEquipos[it.nombreEquipo] = EquipoPDF(nombre = it.nombreEquipo) }

        partidos.filter { it.estado == "FINALIZADO" }.forEach { p ->
            val local = mapaEquipos[p.nombreLocal]
            val visitante = mapaEquipos[p.nombreVisitante]

            if (local != null && visitante != null) {
                local.jugados++; visitante.jugados++
                local.golesFavor += p.golesLocal; local.golesContra += p.golesVisitante
                visitante.golesFavor += p.golesVisitante; visitante.golesContra += p.golesLocal

                when {
                    p.golesLocal > p.golesVisitante -> { local.puntos += 3; local.victorias++; visitante.derrotas++ }
                    p.golesLocal < p.golesVisitante -> { visitante.puntos += 3; visitante.victorias++; local.derrotas++ }
                    else -> { local.puntos += 1; visitante.puntos += 1; local.empates++; visitante.empates++ }
                }
            }
        }

        val tablaOrdenada = mapaEquipos.values.sortedWith(
            compareByDescending<EquipoPDF> { it.puntos }
                .thenByDescending { it.diferenciaGoles }
                .thenByDescending { it.golesFavor }
        )

        // 2. CREACIÓN DEL DOCUMENTO PDF
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()

        // Título y Cabecera
        paint.textSize = 24f
        paint.isFakeBoldText = true
        canvas.drawText("Clasificación: $nombreLiga", 50f, 50f, paint)

        paint.textSize = 12f
        paint.isFakeBoldText = false
        canvas.drawText("Rivalry App - Informe Oficial", 50f, 80f, paint)

        // Encabezados tabla
        var y = 140f
        paint.isFakeBoldText = true
        canvas.drawText("POS", 50f, y, paint)
        canvas.drawText("EQUIPO", 100f, y, paint)
        canvas.drawText("PTS", 380f, y, paint)
        canvas.drawText("PJ", 430f, y, paint)
        canvas.drawText("DG", 480f, y, paint)

        paint.strokeWidth = 1f
        canvas.drawLine(50f, y + 10f, 530f, y + 10f, paint)

        // Filas de equipos
        y += 40f
        paint.isFakeBoldText = false
        tablaOrdenada.forEachIndexed { index, equipo ->
            canvas.drawText("${index + 1}", 50f, y, paint)
            canvas.drawText(equipo.nombre, 100f, y, paint)
            canvas.drawText("${equipo.puntos}", 380f, y, paint)
            canvas.drawText("${equipo.jugados}", 430f, y, paint)
            canvas.drawText("${equipo.diferenciaGoles}", 480f, y, paint)
            y += 30f
        }

        pdfDocument.finishPage(page)

        // 3. GUARDAR Y ENVIAR
        try {
            val file = File(context.cacheDir, "Clasificacion_Rivalry.pdf")
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Enviar Clasificación"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}