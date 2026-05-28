# Rivalry - Red Social y Gestión de Ligas Deportivas

Rivalry es una aplicación nativa Android desarrollada con Kotlin y Jetpack Compose como proyecto final de 2º DAM.  
La aplicación está orientada a la gestión de ligas deportivas amateur y a la interacción social entre jugadores, permitiendo organizar competiciones, partidos casuales y sistemas de comunicación en tiempo real.

La plataforma combina funcionalidades de red social, gestión competitiva y mensajería instantánea usando Firebase como infraestructura backend.

---

# Contenido del Repositorio

Para facilitar la revisión y evaluación del proyecto, la documentación se encuentra separada en distintos apartados:

## [JUSTIFICACIÓN DE RAs](docs/justificacion-ras.md)
Relación directa entre las funcionalidades desarrolladas en la app y los RAs exigidos en los módulos de 2º DAM (DI, PMDM, ADA, SGE, HLC).

---

## [MANUAL DE USUARIO](docs/manual-usuario.md)
Guía visual sobre cómo utilizar la aplicación, crear ligas, participar en pachangas, gestionar fichajes, utilizar el sistema social integrado, etc.
Para facilitar al ususario su uso.

---

## [ARQUITECTURA Y DISEÑO](docs/arquitectura.md)
Documentación técnica sobre la arquitectura MVVM, organización por capas, flujo de datos y estructura general del proyecto.

---

## [BASE DE DATOS FIREBASE](docs/firebase.md)
Explicación del modelo NoSQL en Firestore, colecciones utilizadas, sincronización en tiempo real y persistencia de datos.

---

## [FUNCIONALIDADES PRINCIPALES](docs/funcionalidades.md)
Descripción de las funcionalidades principales:
- Gestión de ligas
- Calendario Round Robin
- Mercado de agentes libres
- Chat en tiempo real
- Pachangas
- Sistema de amistades
- Exportación PDF

---

## [TESTING Y CONTROL DE CALIDAD](docs/testing.md)
Pruebas unitarias realizadas con JUnit, validaciones implementadas y estrategias de control de errores.

---

## [INSTALACIÓN Y DESPLIEGUE](docs/instalacion.md)
Pasos para ejecutar el proyecto en Android Studio, instalación mediante APK y configuración del entorno.

---

# Tecnologías Utilizadas
- Kotlin
- Jetpack Compose
- Firebase Authentication
- Firebase Firestore
- Firebase Storage
- Kotlin Coroutines & StateFlow
- Material Design 3
- MVVM Architecture
- JUnit

---
