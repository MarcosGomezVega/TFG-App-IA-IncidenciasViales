# TFG-App-IA-IncidenciasViales

TFG-App-IA-IncidenciasViales es un proyecto enfocado en desarrollar una aplicación móvil que utiliza inteligencia artificial para detectar desperfectos en infraestructuras viales a través de fotos. La aplicación permite geolocalizar los problemas detectados y enviar la información a las autoridades correspondientes para facilitar su reparación.

---

## Tabla de Contenidos

1. [Requisitos](#requisitos)  
2. [Cómo construir este proyecto](#cómo-construir-este-proyecto)  
3. [Estructura del proyecto](#estructura-del-proyecto)  
4. [Referencia de comandos](#referencia-de-comandos)  
5. [Problemas conocidos](#problemas-conocidos)  

---

## Requisitos

Para desarrollar y ejecutar este proyecto, asegúrate de tener instalados los siguientes requisitos:

- **Android Studio**
- **Java Development Kit (JDK)**
- **Gradle**
- **Dispositivo Android o Emulador**:
---

## Cómo construir este proyecto

Sigue estos pasos para configurar y ejecutar el proyecto correctamente:

1. **Clonar el repositorio**  
   Clona este repositorio en tu máquina local utilizando el siguiente comando:  
   ```bash
   git clone https://github.com/MarcosGomezVega/TFG-App-IA-IncidenciasViales.git
   cd TFG-App-IA-IncidenciasViales
   ```

2. **Abrir el proyecto en Android Studio**  
   - Abre Android Studio.  
   - Selecciona la opción "Open an Existing Project".  
   - Navega hasta la carpeta del proyecto y selecciónala.

3. **Sincronizar dependencias**  
   Una vez abierto el proyecto, Android Studio detectará el archivo `build.gradle`. Sincroniza las dependencias haciendo clic en "Sync Now" en la barra superior.

4. **Configurar un dispositivo de prueba**  
   - Conecta un dispositivo Android físico con la depuración USB habilitada, o configura un emulador en Android Studio.  
   - Asegúrate de que el dispositivo esté correctamente detectado.

5. **Compilar y ejecutar**  
   - Haz clic en el botón "Run" (o presiona `Shift + F10`) para compilar y ejecutar la aplicación en el dispositivo configurado.

6. **Probar la aplicación**  
   - Una vez instalada, prueba las funcionalidades principales, como la detección de desperfectos y la geolocalización.

---

## Estructura del proyecto

```text
# TFG-App-IA-IncidenciasViales
├── CHANGELOG.md
├── README.md
├── app/
│   ├── app
│   └── src
│       ├── androidTest
│       ├── main
│       │   ├── java
│       │   │   └── com
│       │   │       └── example
│       │   │           └── incidenciasviales
│       │   └── res
│       │       ├── drawable
│       │       ├── layout
│       │       ├── mipmap-anydpi
│       │       ├── mipmap-hdpi
│       │       ├── mipmap-mdpi
│       │       ├── mipmap-xhdpi
│       │       ├── mipmap-xxhdpi
│       │       ├── mipmap-xxxhdpi
│       │       ├── values
│       │       ├── values-night
│       │       └── xml
│       └── test
└── gradle
└── docs/
    ├── research
    └──
```

### Descripción de carpetas importantes:

- **`app/src/main/java/com/example/incidenciasviales/`**: Contiene el código fuente principal de la aplicación, donde se implementa la lógica de negocio y las funcionalidades principales.
- **`app/src/main/res/layout/`**: Contiene los archivos XML que definen las vistas y pantallas de la aplicación, especificando cómo se verá la interfaz de usuario.
- **`app/src/main/res/drawable/`**: Almacena recursos gráficos como imágenes e íconos que se utilizan en la interfaz.
- **`app/src/main/res/values/`**: Contiene recursos reutilizables como cadenas de texto, colores y estilos, que facilitan la personalización y consistencia visual.
- **`app/src/main/res/mipmap-*`**: Contiene los íconos de la aplicación en diferentes resoluciones para adaptarse a distintos dispositivos.
- **`app/src/androidTest/`** y **`app/src/test/`**: Incluyen pruebas instrumentadas y pruebas unitarias para garantizar el correcto funcionamiento de la aplicación.
- **`docs/`**: Almacena documentación adicional, como investigaciones, guías o diagramas relacionados con el proyecto.

---

## Referencia de comandos

A continuación, se listan algunos comandos útiles para trabajar con este proyecto:

- **Clonar el repositorio**:  
   ```bash
   git clone https://github.com/MarcosGomezVega/TFG-App-IA-IncidenciasViales.git
   cd TFG-App-IA-IncidenciasViales
   ```

- **Ejecutar pruebas unitarias**:  
   ```bash
   ./gradlew test
   ```

- **Ejecutar pruebas instrumentadas**:  
   ```bash
   ./gradlew connectedAndroidTest
   ```

- **Compilar y ejecutar la aplicación**:  
   ```bash
   ./gradlew assembleDebug
   ```

---

## Problemas conocidos

NA

