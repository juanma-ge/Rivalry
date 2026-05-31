# Justificación de Resultados de Aprendizaje (RAs) y Criterios de Evaluación

Este documento detalla de manera exhaustiva cómo el proyecto **Rivalry** cumple con todos y cada uno de los criterios de evaluación exigidos en las rúbricas de los distintos módulos de 2º de DAM, desglosando cada ítem evaluable y aportando evidencias directas en el código fuente.

---

## 1. Desarrollo de Interfaces (DI)

* **1.1. Distribución coherente y estética:** La interfaz cuenta con una distribución adecuada de los componentes, siendo intuitiva y teniendo en cuenta la cantidad de acciones en los diferentes menús.
    * 🔗 **[PON AQUÍ UN PERMALINK AL CÓDIGO]**: Enlaza a `PantallaHome.kt` (donde se configura el `NavigationBar` y el `Scaffold`).
* **1.2. Adaptabilidad a todos los tamaños:** Se incluyen los componentes adecuados para poder adaptar la interfaz a todo tipo de tamaños de pantalla. Se han evitado medidas fijas en favor de pesos dinámicos.
    * 🔗 **[PON AQUÍ UN PERMALINK AL CÓDIGO]**: Enlaza a `PantallaCrearLiga.kt` (señalando el uso de `Modifier.weight(1f)` y `fillMaxWidth()`).
* **1.3. Pruebas de usabilidad, integración y rendimiento:** Se han realizado pruebas de integración y rendimiento, además de pruebas de usabilidad con potenciales usuarios para pulir la experiencia final.
    * 🔗 **[PON AQUÍ UN PERMALINK AL CÓDIGO]**: Enlaza al archivo `testing.md`.
* **1.4. Documentación y difusión:** Existen un manual de instalación, un manual de usuario y un medio de difusión adecuados y cuidados.
    * 🔗 **[PON AQUÍ UN PERMALINK AL CÓDIGO]**: Enlaza al archivo `manual-usuario.md` y `instalacion.md`.
* **1.5. Guía de diseño del SO:** Se han tenido en cuenta todos y cada uno de los elementos presentes en la guía de diseño del sistema operativo objetivo (Material Design 3), incluyendo paletas dinámicas.
    * 🔗 **[PON AQUÍ UN PERMALINK AL CÓDIGO]**: Enlaza a `Theme.kt` y `TemaViewModel.kt` (manejo de modos Claro/Oscuro).

---

## 2. Programación Multimedia y Dispositivos Móviles (PMDM)

* **2.1. Código estructurado y robusto (Común):** Código bien estructurado, legible, robusto, adaptable y fácil de mantener.
    * 🔗 **[PON AQUÍ UN PERMALINK AL CÓDIGO]**: Enlaza a `LogicaRivalryTest.kt` y `AuthViewModel.kt`.
* **2.2. Documentación y pruebas (Común):** Contiene manual de instalación y ayuda. La aplicación funciona perfectamente en su totalidad, superando las pruebas correspondientes.
    * 🔗 **[PON AQUÍ UN PERMALINK AL CÓDIGO]**: Enlaza al `README.md` principal.
* **2.3. Arquitectura y ciclo de vida:** Buenas prácticas de arquitectura con separación por capas (MVVM), uso de *data binding* (StateFlow) y control del ciclo de vida (nivel muy alto).
    * 🔗 **[PON AQUÍ UN PERMALINK AL CÓDIGO]**: Enlaza a `LigaViewModel.kt` (uso de `viewModelScope.launch` y `StateFlow`).
* **2.4. Ventanas, menús y controles:** Se han utilizado clases para modelar ventanas, menús, alertas y controles, con una usabilidad y control de datos adecuados.
    * 🔗 **[PON AQUÍ UN PERMALINK AL CÓDIGO]**: Enlaza a `PantallaDetalleLiga.kt` (señalando el uso de `AlertDialog` y modales de entrada de datos).
* **2.5. Grafo de Navegación:** Se ha generado un grafo de navegación adecuado, incluyendo el botón atrás y paso de parámetros por navegación sin deficiencias.
    * 🔗 **[PON AQUÍ UN PERMALINK AL CÓDIGO]**: Enlaza a `RivalryNavigation.kt`.
* **2.6. Uso de librerías de integración:** Se han integrado diversas librerías multimedia, acceso a servicios y bases de datos con un nivel muy alto.
    * 🔗 **[PON AQUÍ UN PERMALINK AL CÓDIGO]**: Enlaza a `SeccionPerfil.kt` (uso de la librería Coil para imágenes asíncronas).

---

## 3. Acceso a Datos (ADA)

* **3.1. Gestión en ficheros:** Se utilizan ficheros para leer y escribir información y se han valorado las ventajas y los inconvenientes de las distintas formas de acceso (optando por almacenamiento local de Android para imágenes temporales).
    * 🔗 **[PON AQUÍ UN PERMALINK AL CÓDIGO]**: Enlaza a `PerfilViewModel.kt` (función `subirFotoPerfil` usando `FileOutputStream`).
* **3.2. Gestión en Bases de Datos:** Se utiliza una base de datos para almacenar información y su gestión se ha realizado correctamente (Firebase Firestore).
    * 🔗 **[PON AQUÍ UN PERMALINK AL CÓDIGO]**: Enlaza a `firebase.md` (y opcionalmente añade una captura de la consola de Firestore).
* **3.3. Mapeo Objeto-Relacional (ORM/ODM):** Se ha realizado un mapeo objeto-relacional correcto que aprovecha sus características, serializando JSON a objetos Kotlin automáticamente.
    * 🔗 **[PON AQUÍ UN PERMALINK AL CÓDIGO]**: Enlaza a `EquipoRepositoryImpl.kt` (señalando la instrucción `.toObjects(Equipo::class.java)`).

---

## 4. Sistemas de Gestión Empresarial (SGE)

* **4.1. Documentación técnica:** Se generan documentación técnica y los diagramas apropiados, representando todos los módulos de los que dispone el sistema.
    * 🔗 **[PON AQUÍ UN PERMALINK AL CÓDIGO]**: Enlaza a `arquitectura.md`.
* **4.2. Verificación de configuraciones:** Se verifican las configuraciones del sistema operativo y del gestor de datos garantizando totalmente la funcionalidad.
    * 🔗 **[PON AQUÍ UN PERMALINK AL CÓDIGO]**: Enlaza a la capa de Repositorios (ej. `LigaRepositoryImpl.kt`) donde se instancia Firebase y se controlan los accesos.
* **4.3. Herramientas de consulta:** Se utilizan herramientas y lenguajes de consulta acorde a las especificaciones del sistema, permitiendo un acceso avanzado a datos mediante filtros o transacciones atómicas.
    * 🔗 **[PON AQUÍ UN PERMALINK AL CÓDIGO]**: Enlaza a la función `ficharAgenteLibre` en `LigaViewModel.kt` (bloque `db.runTransaction`).
* **4.4. Manipulación y exportación de datos:** Se generan mecanismos que permiten la manipulación de la información y la exportación de ella (creación de actas e informes).
    * 🔗 **[PON AQUÍ UN PERMALINK AL CÓDIGO]**: Enlaza a `GeneradorPDF.kt` (creación dinámica de documentos PDF).
* **4.5. Autoría y registro de incidencias:** Se generan mecanismos efectivos en todas las acciones para verificar autorías (Auth) y comprobar el correcto funcionamiento, documentando incidencias.
    * 🔗 **[PON AQUÍ UN PERMALINK AL CÓDIGO]**: Enlaza a `AuthRepositoryImpl.kt` (uso de validación de identidad e intercepción de excepciones).

---

## 5. Horas de Libre Configuración (HLC)

* **5.1. Construcción y nomenclatura:** Se generan estructuras de control, variables y métodos que cumplen con las recomendaciones de buena construcción de software, facilitando su mantenimiento.
    * 🔗 **[PON AQUÍ UN PERMALINK AL CÓDIGO]**: Enlaza a `LogicaRivalryTest.kt` o `PartidoSueltoViewModel.kt` (nomenclatura semántica).
* **5.2. Operaciones de Entrada/Salida (E/S):** Hace un uso adecuado de las operaciones de entrada y salida de información, utilizando procedimientos específicos del lenguaje y librerías solventes.
    * 🔗 **[PON AQUÍ UN PERMALINK AL CÓDIGO]**: Enlaza a `ChatViewModel.kt` (uso de `addSnapshotListener` para I/O en tiempo real).
* **5.3. Programación Orientada a Objetos (POO):** Se hace uso de elementos de la POO (clases, objetos, interfaces, herencia, polimorfismo) de forma acorde y bien desplegada.
    * 🔗 **[PON AQUÍ UN PERMALINK AL CÓDIGO]**: Enlaza a `UsuarioRepository.kt` (Interfaz en Dominio) y `UsuarioRepositoryImpl.kt` (Implementación en Datos).
* **5.4. Framework e interfaces:** La aplicación está bien construida en cuanto a interfaces gráficas y se observa una integración impecable con el código y el estado de la aplicación.
    * 🔗 **[PON AQUÍ UN PERMALINK AL CÓDIGO]**: Enlaza a `PantallaHome.kt` (mostrando el uso de `collectAsState()` para inyectar el estado del framework a la UI).