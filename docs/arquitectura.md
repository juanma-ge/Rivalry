# Arquitectura y Diseño

El proyecto **Rivalry** ha sido estructurado siguiendo el patrón arquitectónico **MVVM (Model-View-ViewModel)**, recomendado oficialmente por Google para aplicaciones Android nativas. Esta decisión garantiza un código robusto, altamente testeable, escalable y fácil de mantener a lo largo del tiempo.

### 1. Capa de Dominio (Domain)
Esta capa es el corazón de la aplicación y contiene las reglas de negocio.
* **Modelos (`domain/model`):** Clases de datos (`Data Classes`) que representan las entidades fundamentales de la app, tales como `Liga.kt`, `Partido.kt`, o `Usuario.kt`.
* **Interfaces (`domain/repository`):** Contratos lógicos que definen las operaciones que el sistema puede realizar, por ejemplo, `AuthRepository.kt`. Esto permite aplicar el principio de Inversión de Dependencias (SOLID) y facilita el uso intensivo de la Programación Orientada a Objetos.

### 2. Capa de Datos (Data)
Su responsabilidad exclusiva es la comunicación con fuentes de información externas, en este caso, Firebase.
* **Repositorios (`data/repository`):** Clases concretas que implementan las interfaces del dominio (ej. `LigaRepositoryImpl.kt`). Estas clases manejan las operaciones de entrada/salida y mapean los diccionarios JSON provenientes de Firestore directamente a los objetos de Kotlin definidos en la capa de dominio.

### 3. Capa de Presentación (Presentation)
Gestiona la interfaz gráfica y la retención del estado visual del usuario.
* **ViewModels:** Clases diseñadas para aguantar los cambios de configuración del dispositivo (como la rotación de pantalla). Utilizan el entorno de rutinas `viewModelScope.launch` para ejecutar operaciones de E/S de forma asíncrona, exponiendo los resultados a la vista mediante flujos de datos inmutables (`StateFlow`).
* **UI (Jetpack Compose):** Funciones `@Composable` totalmente declarativas. La interfaz gráfica se conecta fluidamente con el código subyacente, reaccionando de manera automática a las emisiones del `StateFlow` y redibujando únicamente los componentes necesarios en la pantalla.
