# Justificación de Resultados de Aprendizaje (RAs) y Criterios de Evaluación

Este documento detalla de manera exhaustiva cómo el proyecto **Rivalry** cumple con todos y cada uno de los criterios de evaluación exigidos en las rúbricas de los distintos módulos de 2º de DAM, desglosando cada ítem evaluable y aportando evidencias directas en el código fuente.

---

## 1. Desarrollo de Interfaces (DI)

* **1.1. Distribución coherente y estética:** La interfaz cuenta con una distribución adecuada de los componentes, siendo intuitiva y teniendo en cuenta la cantidad de acciones en los diferentes menús.
https://github.com/juanma-ge/Rivalry/blob/9a9bdd9b98d3136dc8a67869247594de57a877df/app/src/main/java/com/example/rivalry/presentation/auth/home/PantallaHome.kt#L55-L114
* **1.2. Adaptabilidad a todos los tamaños:** Se incluyen los componentes adecuados para poder adaptar la interfaz a todo tipo de tamaños de pantalla. Se han evitado medidas fijas en favor de pesos dinámicos.
https://github.com/juanma-ge/Rivalry/blob/9a9bdd9b98d3136dc8a67869247594de57a877df/app/src/main/java/com/example/rivalry/presentation/auth/home/PantallaCrearLiga.kt#L92-L105
* **1.3. Pruebas de usabilidad, integración y rendimiento:** Se han realizado pruebas de integración y rendimiento, además de pruebas de usabilidad con potenciales usuarios para pulir la experiencia final.
[Testing y Calidad](testing.md)
* **1.4. Documentación y difusión:** Existen un manual de instalación, un manual de usuario y un medio de difusión adecuados y cuidados.
[Manual de Usuario](manual-usuario.md) / [Instalación y Despliegue](instalacion.md).
* **1.5. Guía de diseño del SO:** Se han tenido en cuenta todos y cada uno de los elementos presentes en la guía de diseño del sistema operativo objetivo (Material Design 3), incluyendo paletas dinámicas.
https://github.com/juanma-ge/Rivalry/blob/9a9bdd9b98d3136dc8a67869247594de57a877df/app/src/main/java/com/example/rivalry/ui/theme/Theme.kt#L19-L69
https://github.com/juanma-ge/Rivalry/blob/9a9bdd9b98d3136dc8a67869247594de57a877df/app/src/main/java/com/example/rivalry/presentation/auth/home/TemaViewModel.kt#L7-L14
---

## 2. Programación Multimedia y Dispositivos Móviles (PMDM)

* **2.1. Código estructurado y robusto (Común):** Código bien estructurado, legible, robusto, adaptable y fácil de mantener.
https://github.com/juanma-ge/Rivalry/blob/9a9bdd9b98d3136dc8a67869247594de57a877df/app/src/main/java/com/example/rivalry/presentation/auth/AuthViewModel.kt#L12-L121
https://github.com/juanma-ge/Rivalry/blob/9a9bdd9b98d3136dc8a67869247594de57a877df/app/src/test/java/com/example/rivalry/ExampleUnitTest.kt#L8-L49
* **2.2. Documentación y pruebas (Común):** Contiene manual de instalación y ayuda. La aplicación funciona perfectamente en su totalidad, superando las pruebas correspondientes.
    * 🔗 **[PON AQUÍ UN PERMALINK AL CÓDIGO]**: Enlaza al `README.md` principal.
[README principal](../README.md)
* **2.3. Arquitectura y ciclo de vida:** Buenas prácticas de arquitectura con separación por capas (MVVM), uso de *data binding* (StateFlow) y control del ciclo de vida (nivel muy alto).
https://github.com/juanma-ge/Rivalry/blob/9a9bdd9b98d3136dc8a67869247594de57a877df/app/src/main/java/com/example/rivalry/presentation/auth/home/LigaViewModel.kt#L83-L89
https://github.com/juanma-ge/Rivalry/blob/9a9bdd9b98d3136dc8a67869247594de57a877df/app/src/main/java/com/example/rivalry/presentation/auth/home/LigaViewModel.kt#L24-L46
* **2.4. Ventanas, menús y controles:** Se han utilizado clases para modelar ventanas, menús, alertas y controles, con una usabilidad y control de datos adecuados.
https://github.com/juanma-ge/Rivalry/blob/9a9bdd9b98d3136dc8a67869247594de57a877df/app/src/main/java/com/example/rivalry/presentation/auth/home/PantallaDetalleLiga.kt#L324-L346
* **2.5. Grafo de Navegación:** Se ha generado un grafo de navegación adecuado, incluyendo el botón atrás y paso de parámetros por navegación sin deficiencias.
https://github.com/juanma-ge/Rivalry/blob/9a9bdd9b98d3136dc8a67869247594de57a877df/app/src/main/java/com/example/rivalry/navigation/RivalryNavigation.kt#L28-L100
* **2.6. Uso de librerías de integración:** Se han integrado diversas librerías multimedia, acceso a servicios y bases de datos con un nivel muy alto.
https://github.com/juanma-ge/Rivalry/blob/9a9bdd9b98d3136dc8a67869247594de57a877df/app/src/main/java/com/example/rivalry/presentation/auth/home/SeccionPerfil.kt#L132-L143
---

## 3. Acceso a Datos (ADA)

* **3.1. Gestión en ficheros:** Se utilizan ficheros para leer y escribir información y se han valorado las ventajas y los inconvenientes de las distintas formas de acceso (optando por almacenamiento local de Android para imágenes temporales).
https://github.com/juanma-ge/Rivalry/blob/9a9bdd9b98d3136dc8a67869247594de57a877df/app/src/main/java/com/example/rivalry/presentation/auth/home/PerfilViewModel.kt#L53-L77
* **3.2. Gestión en Bases de Datos:** Se utiliza una base de datos para almacenar información y su gestión se ha realizado correctamente (Firebase Firestore).
[Firebase](firebase.md)
* **3.3. Mapeo Objeto-Relacional (ORM/ODM):** Se ha realizado un mapeo objeto-relacional correcto que aprovecha sus características, serializando JSON a objetos Kotlin automáticamente.
https://github.com/juanma-ge/Rivalry/blob/9a9bdd9b98d3136dc8a67869247594de57a877df/app/src/main/java/com/example/rivalry/data/repository/EquipoRepositoryImpl.kt#L24-L34
---

## 4. Sistemas de Gestión Empresarial (SGE)

* **4.1. Documentación técnica:** Se generan documentación técnica y los diagramas apropiados, representando todos los módulos de los que dispone el sistema.
[Arquitecturea del proyecto](arquitectura.md)
* **4.2. Verificación de configuraciones:** Se verifican las configuraciones del sistema operativo y del gestor de datos garantizando totalmente la funcionalidad.
https://github.com/juanma-ge/Rivalry/blob/9a9bdd9b98d3136dc8a67869247594de57a877df/app/src/main/java/com/example/rivalry/data/repository/LigaRepositoryImpl.kt#L9-L33
* **4.3. Herramientas de consulta:** Se utilizan herramientas y lenguajes de consulta acorde a las especificaciones del sistema, permitiendo un acceso avanzado a datos mediante filtros o transacciones atómicas.
https://github.com/juanma-ge/Rivalry/blob/9a9bdd9b98d3136dc8a67869247594de57a877df/app/src/main/java/com/example/rivalry/presentation/auth/home/LigaViewModel.kt#L150-L176
* **4.4. Manipulación y exportación de datos:** Se generan mecanismos que permiten la manipulación de la información y la exportación de ella (creación de actas e informes).
https://github.com/juanma-ge/Rivalry/blob/9a9bdd9b98d3136dc8a67869247594de57a877df/app/src/main/java/com/example/rivalry/presentation/auth/home/GeneradorPDF.kt#L27-L109
* **4.5. Autoría y registro de incidencias:** Se generan mecanismos efectivos en todas las acciones para verificar autorías con FIrebase y comprobar el correcto funcionamiento, documentando incidencias. Aún así le muestro parte de código funcional como ejemplo.
https://github.com/juanma-ge/Rivalry/blob/9a9bdd9b98d3136dc8a67869247594de57a877df/app/src/main/java/com/example/rivalry/data/repository/AuthRepositoryImpl.kt#L8-L36
---

## 5. Horas de Libre Configuración (HLC)

* **5.1. Construcción y nomenclatura:** Se generan estructuras de control, variables y métodos que cumplen con las recomendaciones de buena construcción de software, facilitando su mantenimiento.
https://github.com/juanma-ge/Rivalry/blob/9a9bdd9b98d3136dc8a67869247594de57a877df/app/src/main/java/com/example/rivalry/presentation/auth/home/PartidoSueltoViewModel.kt#L12-L120
* **5.2. Operaciones de Entrada/Salida (E/S):** Hace un uso adecuado de las operaciones de entrada y salida de información, utilizando procedimientos específicos del lenguaje y librerías solventes.
https://github.com/juanma-ge/Rivalry/blob/9a9bdd9b98d3136dc8a67869247594de57a877df/app/src/main/java/com/example/rivalry/presentation/auth/chat/ChatViewModel.kt#L22-L40
* **5.3. Programación Orientada a Objetos (POO):** Se hace uso de elementos de la POO (clases, objetos, interfaces, herencia, polimorfismo) de forma acorde y bien desplegada.
https://github.com/juanma-ge/Rivalry/blob/9a9bdd9b98d3136dc8a67869247594de57a877df/app/src/main/java/com/example/rivalry/data/repository/UsuarioRepositoryImpl.kt#L9-L28
https://github.com/juanma-ge/Rivalry/blob/9a9bdd9b98d3136dc8a67869247594de57a877df/app/src/main/java/com/example/rivalry/domain/repository/UsuarioRepository.kt#L5-L10
* **5.4. Framework e interfaces:** La aplicación está bien construida en cuanto a interfaces gráficas y se observa una integración impecable con el código y el estado de la aplicación.
https://github.com/juanma-ge/Rivalry/blob/9a9bdd9b98d3136dc8a67869247594de57a877df/app/src/main/java/com/example/rivalry/presentation/auth/home/PantallaHome.kt#L36-L48
