# Base de Datos Firebase y NoSQL

El proyecto utiliza **Firebase Firestore** como base de datos principal, estructurada en colecciones documentales desnormalizadas para optimizar la velocidad de lectura.
Es una base de datos libre y fácil de utilizar para todos los usuarios.

### Colecciones Principales
* **`usuarios`**: Almacena perfil, avatares y arrays con los IDs de los usuarios agregados como amigos.
* **`ligas`**: Almacena metadatos del torneo. Contiene arrays relacionales (`idsMiembros`, `idsAgentesLibres`) y diccionarios anidados (`nombresEquipos`) para gestionar la pertenencia de usuarios a los equipos.
* **`partidos`**: Entidades ligadas a una liga mediante el campo `idLiga`.
* **`partidosSueltos`**: Documentos independientes para gestionar pachangas con arrays de inscritos.
* **`chatsPrivados` / `mensajes` / `solicitudes`**: Nodos encargados del sistema social P2P.
<img width="1464" height="742" alt="image" src="https://github.com/user-attachments/assets/adabc662-7043-46a0-befe-d65c67cc49c0" />

### Sincronización en Tiempo Real
La plataforma hace uso de `addSnapshotListener` de Firestore. Esto permite que los módulos de **Chat** y **Listados de Ligas** escuchen cambios bidireccionalmente sin necesidad de que el usuario recargue la pantalla. 

### Firebase Authentication
Gestiona los usuarios que inicien sesión en la aplicación. Permite cambiar o restablecer contraseñasy visualizar la información como los id´s de los usuarios o su fecha de creación.
https://github.com/juanma-ge/Rivalry/blob/f693d15172850ffbc01457ae97f8308885cca164/app/src/main/java/com/example/rivalry/presentation/auth/AuthViewModel.kt#L88-L121

### Storage
En este proyecto, de momento, trabaja las imágenes multimedia en local. Así que no trabaja con Firebase Storage. Se utiliza el selector nativo de Android (`PickVisualMediaRequest`) para obtener la URI de la imagen elegida.
https://github.com/juanma-ge/Rivalry/blob/f693d15172850ffbc01457ae97f8308885cca164/app/src/main/java/com/example/rivalry/presentation/auth/home/PerfilViewModel.kt#L52-L76
