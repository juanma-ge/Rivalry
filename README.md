# Rivalry - Red Social y Gestión de Ligas Deportivas

Rivalry es una aplicación nativa Android desarrollada con Kotlin y Jetpack Compose como proyecto final de 2º DAM.  
La aplicación está orientada a la gestión de ligas deportivas amateur y a la interacción social entre jugadores, permitiendo organizar competiciones, partidos casuales y sistemas de comunicación en tiempo real.

Pretende incentivar a las personas, especialmente jóvenes, a participar en ligas deportivas, especialmente de fútbol, brindando facilidades y una óptima gestión de ligas y partidos. Todo concentrado en una misma aplicación.

La plataforma combina funcionalidades de red social, gestión competitiva y mensajería instantánea usando Firebase como infraestructura backend.

---

# Contenido del Repositorio

Para facilitar la revisión y evaluación del proyecto, la documentación se encuentra separada en distintos apartados:

## [JUSTIFICACIÓN DE RAs](justificacion-ras.md)
Relación directa entre las funcionalidades desarrolladas en la app y los RAs exigidos en los módulos de 2º DAM (DI, PMDM, ADA, SGE, HLC).

---

## [MANUAL DE USUARIO](manual-usuario.md)
Guía visual sobre cómo utilizar la aplicación, crear ligas, participar en pachangas, gestionar fichajes, utilizar el sistema social integrado, etc.
Para facilitar al ususario su uso.

---

## [ARQUITECTURA Y DISEÑO](arquitectura.md)
Documentación técnica sobre la arquitectura MVVM, organización por capas, flujo de datos y estructura general del proyecto.

---

## [BASE DE DATOS FIREBASE](firebase.md)
Explicación del modelo NoSQL en Firestore, colecciones utilizadas, sincronización en tiempo real y persistencia de datos.

---

## [FUNCIONALIDADES PRINCIPALES](funcionalidades.md)
Descripción de las funcionalidades principales:
- Gestión de ligas
- Calendario Round Robin
- Mercado de agentes libres
- Chat en tiempo real
- Pachangas
- Sistema de amistades
- Exportación PDF

---

## [TESTING Y CONTROL DE CALIDAD](testing.md)
Pruebas unitarias realizadas con JUnit, validaciones implementadas y estrategias de control de errores.

---

## [INSTALACIÓN Y DESPLIEGUE](instalacion.md)
Pasos para ejecutar el proyecto en Android Studio, instalación mediante APK y configuración del entorno.

---

# Tecnologías Utilizadas
- Kotlin
- Jetpack Compose
- Firebase Authentication
- Firebase Firestore
- Kotlin Coroutines & StateFlow
- Material Design 3
- MVVM Architecture
- JUnit

---
