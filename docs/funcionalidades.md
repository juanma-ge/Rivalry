# Funcionalidades y Lógica de Negocio

El proyecto **Rivalry** es un ecosistema completo que abarca desde la gestión deportiva hasta la interacción social. A continuación, se detallan todas las funcionalidades desarrolladas y la lógica técnica que las sustenta.

---

## 1. Autenticación y Gestión de Perfiles

El acceso al sistema está protegido y personalizado para cada usuario.

- **Registro y Login Seguro:** Gestión de credenciales mediante Firebase Authentication, garantizando que las contraseñas se almacenen cifradas y validando la semántica de los correos electrónicos.  
  🔗 Ver en código: `AuthViewModel.kt` (Lógica de Login/Registro)  
  `../app/src/main/java/com/example/rivalry/presentation/auth/AuthViewModel.kt`

- **Perfiles Personalizados:** Los usuarios pueden definir su identidad deportiva (Apodo, Posición, Dorsal y Biografía). Además, pueden seleccionar una imagen de avatar desde su galería, la cual se procesa y persiste localmente para ahorrar cuota de red.  
  🔗 Ver en código: `PerfilViewModel.kt` (Persistencia de imagen local)  
  `../app/src/main/java/com/example/rivalry/presentation/auth/home/PerfilViewModel.kt`

---

## 2. Ecosistema de Competiciones (Ligas)

El núcleo de la aplicación es la creación y administración de ligas amateur.

- **Ligas Públicas y Privadas:** Los usuarios pueden crear competiciones definiendo deporte, ubicación y límite de plazas. Si una liga es "Privada", el sistema genera automáticamente un código hash único que el administrador debe compartir con los participantes autorizados.

- **Buscador Integrado:** Funcionalidad de filtrado de texto (ignorando mayúsculas y minúsculas) que permite buscar ligas por nombre, provincia o ciudad.  
  🔗 Ver en código: `PantallaHome.kt` (Lógica de filtrado de ligas)  
  `../app/src/main/java/com/example/rivalry/presentation/auth/PantallaHome.kt`

---

## 3. Generación de Calendarios (Round-Robin)

El sistema genera automáticamente el calendario de competición.

El algoritmo empareja a todos los equipos inscritos en formato todos contra todos.  
Si el número de equipos es impar, se añade un equipo fantasma llamado **"DESCANSA"** para equilibrar las jornadas.

🔗 Ver en código: `LigaViewModel.kt` (función `generarCalendario`)  
`../app/src/main/java/com/example/rivalry/presentation/auth/home/LigaViewModel.kt`

---

## 4. Sistema de Clasificación Dinámico

Los resultados actualizan la clasificación en tiempo real.

- 3 puntos por victoria
- 1 punto por empate
- 0 puntos por derrota

Ordenación:
1. Puntos
2. Diferencia de goles (DG)
3. Goles a favor (GF)

🔗 Ver en código: `PestaniaClasificacion.kt` (algoritmo de ordenación)  
`../app/src/main/java/com/example/rivalry/presentation/auth/home/PestaniaClasificacion.kt`

---

## 5. Mercado de Agentes Libres (Fichajes Atómicos)

Sistema de fichajes para jugadores sin equipo.

Se utiliza `db.runTransaction` para evitar problemas de concurrencia (dos fichajes simultáneos sobre el mismo jugador).  
La operación es atómica: elimina al jugador del mercado y lo asigna al equipo del capitán.

🔗 Ver en código: `LigaViewModel.kt` (función `ficharAgenteLibre`)  
`../app/src/main/java/com/example/rivalry/presentation/auth/home/LigaViewModel.kt`

---

## 6. Partidos Sueltos (Pachangas)

Permite organizar partidos sin liga formal.

- Control de plazas máximas
- Bloqueo automático cuando `jugadores == maxJugadores`
- Gestión en tiempo real desde la UI

🔗 Ver en código: `TarjetaPartido.kt` (control de aforo)  
`../app/src/main/java/com/example/rivalry/presentation/auth/components/TarjetaPartido.kt`

---

## 7. Sistema Social: Amigos y Códigos Únicos

Cada usuario tiene un código único generado desde su ID (ej: `RIV-A8F2`).

Permite:
- Enviar solicitudes de amistad
- Estado PENDIENTE / ACEPTADO / RECHAZADO

🔗 Ver en código: `SocialViewModel.kt` (lógica de amistad)  
`../app/src/main/java/com/example/rivalry/presentation/auth/home/SocialViewModel.kt`

---

## 8. Chat Integrado y Salas P2P

Sistema de mensajería en tiempo real con Firebase.

- Chat de liga (grupo)
- Chat privado P2P

Los chats privados se generan con un ID único basado en los IDs de usuario ordenados alfabéticamente.

🔗 Ver en código: `SocialViewModel.kt` (chat privado)  
`../app/src/main/java/com/example/rivalry/presentation/auth/home/SocialViewModel.kt`

---

## 9. Motor de Exportación a PDF

Genera documentos PDF con la clasificación de la liga.

- Uso de `PdfDocument` (Canvas Android)
- Renderizado en memoria
- Exportación mediante `FileProvider`
- Compartición externa (WhatsApp, correo, etc.)

🔗 Ver en código: `GeneradorPDF.kt`  
`../app/src/main/java/com/example/rivalry/presentation/auth/home/GeneradorPDF.kt`