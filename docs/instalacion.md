# 🚀 Instalación y Despliegue

Este documento detalla los procedimientos necesarios para ejecutar **Rivalry**, tanto si se desea evaluar la aplicación final instalada en un dispositivo físico, como si se pretende clonar el código fuente para revisar su estructura en un entorno de desarrollo.

---
### 1. Instalación para Evaluadores y Usuarios Finales (Sideloading)

Para la fase de evaluación del proyecto, la aplicación se distribuye de forma directa mediante el empaquetado universal en formato **.APK (Android Package Kit)**, evitando los tiempos de revisión de las tiendas de aplicaciones oficiales y facilitando el acceso inmediato.

Puedes descargar el ejecutable directamente pulsando en el siguiente botón:

[![Descargar APK](https://img.shields.io/badge/ANDROID-DESCARGAR_APK-2ECC71?style=for-the-badge&logo=android&logoColor=white)](https://github.com/juanma-ge/Rivalry/releases/latest)

* **Paso 1:** Pulse el botón superior o navegue a la pestaña de **Releases** en este repositorio de GitHub para descargar el archivo `app-release.apk` en su dispositivo móvil.
* **Paso 2:** Al intentar abrir el archivo, el sistema operativo le solicitará permisos para **"Instalar aplicaciones de orígenes desconocidos"**. Habilite esta opción de forma temporal por seguridad.
* **Paso 3:** Finalice la instalación y abra la aplicación.
    * *Nota: La aplicación ya cuenta con la conexión preconfigurada a la base de datos de producción en la nube, por lo que no es necesario realizar ninguna configuración de servidores.*
---

### 2. Despliegue para Desarrolladores (Entorno de Código)

Para los miembros del tribunal o desarrolladores que deseen inspeccionar, compilar y ejecutar el código fuente directamente desde el IDE, el flujo de trabajo es el siguiente:

* **Paso 1: Clonación del repositorio**
  Abra una terminal o utilice la herramienta de control de versiones integrada en su IDE para clonar el proyecto en su máquina local:
```bash
  git clone [https://github.com/juanma-ge/Rivalry.git](https://github.com/juanma-ge/Rivalry.git)