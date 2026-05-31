package com.example.rivalry.presentation.auth.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rivalry.domain.model.Deporte
import com.example.rivalry.domain.model.Liga
import com.example.rivalry.presentation.auth.model.MiembroUI
import com.example.rivalry.domain.model.Partido
import com.example.rivalry.presentation.auth.model.AgenteLibreUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class LigaViewModel : ViewModel() {

    data class NoticiaLiga(val id: String = "", val texto: String = "", val fecha: Long = 0)

    private val _noticiasLiga = MutableStateFlow<List<NoticiaLiga>>(emptyList())
    val noticiasLiga: StateFlow<List<NoticiaLiga>> = _noticiasLiga.asStateFlow()

    private val _misLigas = MutableStateFlow<List<Liga>>(emptyList())
    val misLigas: StateFlow<List<Liga>> = _misLigas

    private val _ligasExplorar = MutableStateFlow<List<Liga>>(emptyList())
    val ligasExplorar: StateFlow<List<Liga>> = _ligasExplorar

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando

    private val _ligaSeleccionada = MutableStateFlow<Liga?>(null)
    val ligaSeleccionada: StateFlow<Liga?> = _ligaSeleccionada.asStateFlow()

    private val _miembrosLiga = MutableStateFlow<List<MiembroUI>>(emptyList())
    val miembrosLiga: StateFlow<List<MiembroUI>> = _miembrosLiga.asStateFlow()

    private val _agentesLibres = MutableStateFlow<List<AgenteLibreUI>>(emptyList())
    val agentesLibres: StateFlow<List<AgenteLibreUI>> = _agentesLibres.asStateFlow()

    private val _partidosLiga = MutableStateFlow<List<Partido>>(emptyList())
    val partidosLiga: StateFlow<List<Partido>> = _partidosLiga.asStateFlow()

    init {
        cargarLigas()
    }

    private fun cargarLigas() {
        _cargando.value = true
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            _cargando.value = false
            return
        }

        FirebaseFirestore.getInstance().collection("ligas")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    _cargando.value = false
                    return@addSnapshotListener
                }

                val todasLasLigas = snapshot.documents.mapNotNull { documento ->
                    val liga = documento.toObject(Liga::class.java)
                    liga?.copy(id = documento.id)
                }

                _misLigas.value = todasLasLigas.filter { liga ->
                    liga.idsMiembros.contains(userId) || liga.idsAgentesLibres.contains(userId)
                }

                _ligasExplorar.value = todasLasLigas.filter { liga ->
                    (!liga.esPrivada || liga.esPublica) && !liga.idsMiembros.contains(userId) && !liga.idsAgentesLibres.contains(userId)
                }
                _cargando.value = false
            }
    }

    fun crearLigaEnFirebase(nombre: String, deporte: Deporte, maxParticipantes: Int, esPublica: Boolean, provincia: String, ciudad: String, onExito: () -> Unit) {
        viewModelScope.launch {
            _cargando.value = true
            val creadorId = FirebaseAuth.getInstance().currentUser?.uid
            if (creadorId != null) {
                val codigoGenerado = if (!esPublica) "RIV-${UUID.randomUUID().toString().take(4).uppercase()}" else ""

                val nuevaLiga = Liga(
                    id = "",
                    nombre = nombre,
                    deporte = deporte.name,
                    creadorId = creadorId,
                    maxParticipantes = maxParticipantes,
                    esPublica = esPublica,
                    esPrivada = !esPublica,
                    codigoInvitacion = codigoGenerado,
                    idsMiembros = listOf(creadorId),
                    provincia = provincia,
                    ciudad = ciudad
                )

                FirebaseFirestore.getInstance().collection("ligas").add(nuevaLiga).addOnSuccessListener { documento ->
                    FirebaseFirestore.getInstance().collection("ligas").document(documento.id).update("id", documento.id)
                    _cargando.value = false
                    onExito()
                }.addOnFailureListener { _cargando.value = false }
            }
        }
    }

    fun cargarDetalleLiga(ligaId: String) {
        _cargando.value = true
        FirebaseFirestore.getInstance().collection("ligas").document(ligaId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) {
                    _cargando.value = false
                    return@addSnapshotListener
                }
                val liga = snapshot.toObject(Liga::class.java)
                _ligaSeleccionada.value = liga?.copy(id = snapshot.id)
                _cargando.value = false
            }
    }

    fun cargarNoticias(ligaId: String) {
        FirebaseFirestore.getInstance().collection("ligas").document(ligaId).collection("noticias")
            .orderBy("fecha", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val lista = snapshot?.documents?.mapNotNull { doc ->
                    NoticiaLiga(id = doc.id, texto = doc.getString("texto") ?: "", fecha = doc.getLong("fecha") ?: 0L)
                } ?: emptyList()
                _noticiasLiga.value = lista
            }
    }

    fun limpiarLigaSeleccionada() {
        _ligaSeleccionada.value = null
        _miembrosLiga.value = emptyList()
    }

    fun unirseALiga(ligaId: String, nombreEquipo: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val updates = mapOf("idsMiembros" to FieldValue.arrayUnion(userId), "nombresEquipos.$userId" to nombreEquipo)
        FirebaseFirestore.getInstance().collection("ligas").document(ligaId).update(updates)
    }

    fun ficharAgenteLibre(ligaId: String, agente: AgenteLibreUI, nombreEquipo: String) {
        val db = FirebaseFirestore.getInstance()
        val miId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val ligaRef = db.collection("ligas").document(ligaId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(ligaRef)
            val idsMiembros = snapshot.get("idsMiembros") as? MutableList<String> ?: mutableListOf()

            if (!idsMiembros.contains(agente.id)) {
                idsMiembros.add(agente.id)
            }

            transaction.update(ligaRef, "idsMiembros", idsMiembros)
            transaction.update(ligaRef, "asignacionesEquipos.${agente.id}", miId)
            transaction.update(ligaRef, "idsAgentesLibres", FieldValue.arrayRemove(agente.id))

            val nuevaNoticiaRef = ligaRef.collection("noticias").document()
            val noticiaData = mapOf(
                "texto" to "🤝 El equipo $nombreEquipo ha fichado al jugador ${agente.nombre}.",
                "fecha" to System.currentTimeMillis()
            )
            transaction.set(nuevaNoticiaRef, noticiaData)

        }.addOnSuccessListener {
            cargarDetalleLiga(ligaId)
        }
    }

    fun cargarMiembros(ligaId: String, ids: List<String>, creadorId: String, nombresEquipos: Map<String, String>) {
        if (ids.isEmpty()) {
            _miembrosLiga.value = emptyList()
            return
        }

        FirebaseFirestore.getInstance().collection("ligas").document(ligaId).get()
            .addOnSuccessListener { ligaDoc ->
                val asignacionesEquipos = ligaDoc.get("asignacionesEquipos") as? Map<String, String> ?: emptyMap()

                FirebaseFirestore.getInstance().collection("usuarios")
                    .whereIn(com.google.firebase.firestore.FieldPath.documentId(), ids).get()
                    .addOnSuccessListener { snapshot ->
                        val lista = snapshot.documents.mapNotNull { doc ->
                            val nombre = doc.getString("apodo") ?: doc.getString("nombreUsuario") ?: doc.getString("nombre") ?: "Jugador"
                            val esAdmin = doc.id == creadorId

                            val nombreEquipo = if (nombresEquipos.containsKey(doc.id)) {
                                nombresEquipos[doc.id] ?: "Sin equipo"
                            } else {
                                val idDeSuCapitan = asignacionesEquipos[doc.id]
                                nombresEquipos[idDeSuCapitan] ?: "Agente Libre / Sin Equipo"
                            }
                            MiembroUI(doc.id, nombre, esAdmin, nombreEquipo)
                        }
                        _miembrosLiga.value = lista
                    }
            }
    }

    fun salirDeLiga(ligaId: String, onVolver: () -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val ligaActual = _ligaSeleccionada.value ?: return
        val db = FirebaseFirestore.getInstance()
        val ligaRef = db.collection("ligas").document(ligaId)

        if (ligaActual.creadorId == userId) {
            val miembrosRestantes = ligaActual.idsMiembros.filter { it != userId }
            if (miembrosRestantes.isEmpty()) {
                ligaRef.delete().addOnSuccessListener { onVolver() }
            } else {
                val nuevoAdminId = miembrosRestantes.first()
                val actualizaciones = mapOf("idsMiembros" to FieldValue.arrayRemove(userId), "creadorId" to nuevoAdminId)
                ligaRef.update(actualizaciones).addOnSuccessListener { onVolver() }
            }
        } else {
            ligaRef.update("idsMiembros", FieldValue.arrayRemove(userId)).addOnSuccessListener { onVolver() }
        }
    }

    fun cargarAgentesLibres(ids: List<String>) {
        if (ids.isEmpty()) {
            _agentesLibres.value = emptyList()
            return
        }
        FirebaseFirestore.getInstance().collection("usuarios")
            .whereIn(com.google.firebase.firestore.FieldPath.documentId(), ids).get()
            .addOnSuccessListener { snapshot ->
                val lista = snapshot.documents.mapNotNull { doc ->
                    val id = doc.id
                    val nombre = doc.getString("apodo") ?: doc.getString("nombre") ?: "Jugador"
                    val email = doc.getString("email") ?: doc.getString("correo") ?: "correo@ejemplo.com"
                    AgenteLibreUI(id, nombre, email)
                }
                _agentesLibres.value = lista
            }
    }

    fun apuntarseComoAgenteLibre(ligaId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("ligas").document(ligaId).update("idsAgentesLibres", FieldValue.arrayUnion(userId))
    }

    fun salirDeAgentesLibres(ligaId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("ligas").document(ligaId).update("idsAgentesLibres", FieldValue.arrayRemove(userId))
    }

    fun crearPartido(partido: Partido) {
        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("partidos").document()
        ref.set(partido.copy(id = ref.id)).addOnSuccessListener { cargarPartidosLiga(partido.idLiga) }
    }

    fun cargarPartidosLiga(ligaId: String) {
        FirebaseFirestore.getInstance().collection("partidos").whereEqualTo("idLiga", ligaId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val lista = snapshot?.documents?.mapNotNull { it.toObject(Partido::class.java) } ?: emptyList()
                _partidosLiga.value = lista.sortedBy { it.jornada }
            }
    }

    fun finalizarPartido(partidoId: String, golesL: Int, golesV: Int, goleadoresMapa: Map<String, Int>) {
        val db = FirebaseFirestore.getInstance()
        db.collection("partidos").document(partidoId).update(
            mapOf("golesLocal" to golesL, "golesVisitante" to golesV, "estado" to "FINALIZADO", "goleadores" to goleadoresMapa)
        ).addOnSuccessListener { println("Resultado guardado") }
    }

    fun calcularPuntos(equipoId: String, partidos: List<Partido>): Int {
        var puntos = 0
        partidos.filter { it.estado == "FINALIZADO" }.forEach { p ->
            when {
                p.idLocal == equipoId && p.golesLocal > p.golesVisitante -> puntos += 3
                p.idVisitante == equipoId && p.golesVisitante > p.golesLocal -> puntos += 3
                (p.idLocal == equipoId || p.idVisitante == equipoId) && p.golesLocal == p.golesVisitante -> puntos += 1
            }
        }
        return puntos
    }

    fun generarCalendario(ligaId: String, miembros: List<MiembroUI>) {
        if (miembros.size < 2) return
        val equipos = miembros.toMutableList()
        if (equipos.size % 2 != 0) equipos.add(MiembroUI("DESCANSA", "Descansa", false, "Descansa"))

        val numRondas = equipos.size - 1
        val mitad = equipos.size / 2
        val partidosNuevos = mutableListOf<Partido>()

        for (ronda in 0 until numRondas) {
            for (i in 0 until mitad) {
                val local = equipos[i]
                val visitante = equipos[equipos.size - 1 - i]
                if (local.id != "DESCANSA" && visitante.id != "DESCANSA") {
                    partidosNuevos.add(Partido(idLiga = ligaId, jornada = ronda + 1, idLocal = local.id, idVisitante = visitante.id, nombreLocal = local.nombreEquipo, nombreVisitante = visitante.nombreEquipo, estado = "PENDIENTE"))
                }
            }
            val ultimo = equipos.removeAt(equipos.size - 1)
            equipos.add(1, ultimo)
        }

        val db = FirebaseFirestore.getInstance()
        val batch = db.batch()
        partidosNuevos.forEach { partido ->
            val ref = db.collection("partidos").document()
            batch.set(ref, partido.copy(id = ref.id))
        }
        val ligaRef = db.collection("ligas").document(ligaId)
        batch.update(ligaRef, "estado", "EN_JUEGO")

        batch.commit().addOnSuccessListener {
            cargarPartidosLiga(ligaId)
        }
    }

    fun cambiarNombreEquipo(ligaId: String, equipoId: String, nuevoNombre: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("ligas").document(ligaId).update("nombresEquipos.$equipoId", nuevoNombre).addOnSuccessListener {
            db.collection("partidos").whereEqualTo("idLiga", ligaId).get().addOnSuccessListener { snapshot ->
                val batch = db.batch()
                var partidosModificados = 0
                for (doc in snapshot.documents) {
                    if (doc.getString("idLocal") == equipoId) { batch.update(doc.reference, "nombreLocal", nuevoNombre); partidosModificados++ }
                    if (doc.getString("idVisitante") == equipoId) { batch.update(doc.reference, "nombreVisitante", nuevoNombre); partidosModificados++ }
                }
                if (partidosModificados > 0) {
                    batch.commit().addOnSuccessListener { cargarPartidosLiga(ligaId) }
                }
            }
        }
    }

    fun subirLogoLiga(context: android.content.Context, ligaId: String, uri: Uri) {
        val db = FirebaseFirestore.getInstance()

        try {
            val nombreArchivo = "liga_${ligaId}.jpg"
            val archivoLocal = java.io.File(context.filesDir, nombreArchivo)

            val inputStream = context.contentResolver.openInputStream(uri)
            val outputStream = java.io.FileOutputStream(archivoLocal)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            val rutaAbsoluta = archivoLocal.absolutePath
            val datosLogo = mapOf("fotoUrl" to rutaAbsoluta)

            db.collection("ligas").document(ligaId)
                .set(datosLogo, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener {
                    cargarDetalleLiga(ligaId)
                }

        } catch (e: Exception) {
            println("Error en fichero local de liga: ${e.message}")
        }
    }

    fun unirseConCodigo(codigo: String, nombreEquipo: String, onResultado: (Boolean, String) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("ligas").whereEqualTo("codigoInvitacion", codigo).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    onResultado(false, "No se ha encontrado ninguna liga con ese código.")
                } else {
                    val ligaDoc = snapshot.documents.first()
                    val ligaId = ligaDoc.id
                    val miembrosActuales = ligaDoc.get("idsMiembros") as? List<String> ?: emptyList()
                    val agentesActuales = ligaDoc.get("idsAgentesLibres") as? List<String> ?: emptyList()

                    if (miembrosActuales.contains(userId) || agentesActuales.contains(userId)) {
                        onResultado(false, "Ya estás dentro de esta liga.")
                    } else {
                        if (nombreEquipo.isBlank()) {
                            db.collection("ligas").document(ligaId).update("idsAgentesLibres", FieldValue.arrayUnion(userId))
                                .addOnSuccessListener {
                                    cargarLigas()
                                    onResultado(true, "¡Te has unido a la liga como Agente Libre!")
                                }
                        } else {
                            val updates = mapOf(
                                "idsMiembros" to FieldValue.arrayUnion(userId),
                                "nombresEquipos.$userId" to nombreEquipo
                            )
                            db.collection("ligas").document(ligaId).update(updates)
                                .addOnSuccessListener {
                                    cargarLigas()
                                    onResultado(true, "¡Te has unido a la liga correctamente!")
                                }
                        }
                    }
                }
            }
            .addOnFailureListener {
                onResultado(false, "Error de conexión al buscar el código.")
            }
    }
}