# Funcionalidades y Lógica de Negocio

El proyecto **Rivalry** es un ecosistema completo que abarca desde la gestión deportiva hasta la interacción social. A continuación, se detallan todas las funcionalidades desarrolladas y la lógica técnica que las sustenta.

---

## 1. Autenticación y Gestión de Perfiles

El acceso al sistema está protegido y personalizado para cada usuario.

- **Registro y Login Seguro:** Gestión de credenciales mediante Firebase Authentication, garantizando que las contraseñas se almacenen cifradas y validando la semántica de los correos electrónicos.  
https://github.com/juanma-ge/Rivalry/blob/76cebde6ec5eeb9c03243685a78a90d584a992f4/app/src/main/java/com/example/rivalry/presentation/auth/AuthViewModel.kt#L37-L61
https://github.com/juanma-ge/Rivalry/blob/76cebde6ec5eeb9c03243685a78a90d584a992f4/app/src/main/java/com/example/rivalry/presentation/auth/AuthViewModel.kt#L63-L86

- **Perfiles Personalizados:** Los usuarios pueden definir su identidad deportiva (Apodo, Posición, Dorsal y Biografía). Además, pueden seleccionar una imagen de avatar desde su galería, la cual se procesa y persiste localmente para ahorrar cuota de red.  
https://github.com/juanma-ge/Rivalry/blob/76cebde6ec5eeb9c03243685a78a90d584a992f4/app/src/main/java/com/example/rivalry/presentation/auth/home/PerfilViewModel.kt#L38-L50
https://github.com/juanma-ge/Rivalry/blob/76cebde6ec5eeb9c03243685a78a90d584a992f4/app/src/main/java/com/example/rivalry/presentation/auth/home/PerfilViewModel.kt#L52-L76

---

## 2. Ecosistema de Competiciones (Ligas)

El núcleo de la aplicación es la creación y administración de ligas amateur.

- **Ligas Públicas y Privadas:** Los usuarios pueden crear competiciones definiendo deporte, ubicación y límite de plazas. Si una liga es "Privada", el sistema genera automáticamente un código hash único que el administrador debe compartir con los participantes autorizados.

- **Buscador Integrado:** Funcionalidad de filtrado de texto (ignorando mayúsculas y minúsculas) que permite buscar ligas por nombre, provincia o ciudad.  
https://github.com/juanma-ge/Rivalry/blob/76cebde6ec5eeb9c03243685a78a90d584a992f4/app/src/main/java/com/example/rivalry/presentation/auth/PantallaHome.kt#L203-L234

---

## 3. Generación de Calendarios (Round-Robin)

El sistema genera automáticamente el calendario de competición.

El algoritmo empareja a todos los equipos inscritos en formato todos contra todos.  
Si el número de equipos es impar, se añade un equipo fantasma llamado **"DESCANSA"** para equilibrar las jornadas.
https://github.com/juanma-ge/Rivalry/blob/76cebde6ec5eeb9c03243685a78a90d584a992f4/app/src/main/java/com/example/rivalry/presentation/auth/home/LigaViewModel.kt#L290-L323

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
https://github.com/juanma-ge/Rivalry/blob/76cebde6ec5eeb9c03243685a78a90d584a992f4/app/src/main/java/com/example/rivalry/presentation/auth/home/PestaniaClasificacion.kt#L75-L118

---

## 5. Mercado de Agentes Libres (Fichajes Atómicos)

Sistema de fichajes para jugadores sin equipo.

Se utiliza `db.runTransaction` para evitar problemas de concurrencia (dos fichajes simultáneos sobre el mismo jugador).  
La operación es atómica: elimina al jugador del mercado y lo asigna al equipo del capitán.

🔗 Ver en código: `LigaViewModel.kt` (función `ficharAgenteLibre`)  
`../app/src/main/java/com/example/rivalry/presentation/auth/home/LigaViewModel.kt`
https://github.com/juanma-ge/Rivalry/blob/76cebde6ec5eeb9c03243685a78a90d584a992f4/app/src/main/java/com/example/rivalry/presentation/auth/home/LigaViewModel.kt#L149-L176

---

## 6. Partidos Sueltos (Pachangas)

Permite organizar partidos sin liga formal.

- Control de plazas máximas
- Bloqueo automático cuando `jugadores == maxJugadores`
- Gestión en tiempo real desde la UI
https://github.com/juanma-ge/Rivalry/blob/76cebde6ec5eeb9c03243685a78a90d584a992f4/app/src/main/java/com/example/rivalry/presentation/auth/components/TarjetaPartido.kt#L14-L66

---

## 7. Sistema Social: Amigos y Códigos Únicos

Cada usuario tiene un código único generado desde su ID (ej: `RIV-A8F2`).

Permite:
- Enviar solicitudes de amistad
- Estado PENDIENTE / ACEPTADO / RECHAZADO
https://github.com/juanma-ge/Rivalry/blob/76cebde6ec5eeb9c03243685a78a90d584a992f4/app/src/main/java/com/example/rivalry/presentation/auth/home/SocialViewModel.kt#L95-L138

---

## 8. Chat Integrado y Salas P2P

Sistema de mensajería en tiempo real con Firebase.

- Chat de liga (grupo)
- Chat privado P2P

Los chats privados se generan con un ID único basado en los IDs de usuario ordenados alfabéticamente.
https://github.com/juanma-ge/Rivalry/blob/76cebde6ec5eeb9c03243685a78a90d584a992f4/app/src/main/java/com/example/rivalry/presentation/auth/home/SocialViewModel.kt#L168-L190

---

## 9. Motor de Exportación a PDF

Genera documentos PDF con la clasificación de la liga.

- Uso de `PdfDocument`
- Renderizado en memoria
- Exportación mediante `FileProvider`
- Compartición externa (WhatsApp, correo, etc.)

https://github.com/juanma-ge/Rivalry/blob/76cebde6ec5eeb9c03243685a78a90d584a992f4/app/src/main/java/com/example/rivalry/presentation/auth/home/GeneradorPDF.kt#L29-L49
