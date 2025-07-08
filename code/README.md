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
TFG-App-IA-IncidenciasViales/
├── CHANGELOG.md
├── README.md
├── code/
│   ├── android_app/
│   │   ├── gradle/
│   │   └── app/
│   │       └── src/
│   │           ├── androidTest/
│   │           ├── main/
│   │           │   ├── java/
│   │           │   │   └── com/
│   │           │   │       └── example/
│   │           │   │           └── incidenciasviales/
│   │           │   ├── res/
│   │           │   │   ├── drawable/
│   │           │   │   ├── layout/
│   │           │   │   ├── mipmap-anydpi/
│   │           │   │   ├── mipmap-hdpi/
│   │           │   │   ├── mipmap-mdpi/
│   │           │   │   ├── mipmap-xhdpi/
│   │           │   │   ├── mipmap-xxhdpi/
│   │           │   │   ├── mipmap-xxxhdpi/
│   │           │   │   ├── values/
│   │           │   │   ├── values-night/
│   │           │   │   └── xml/
│   │           │   └── assets/
│   │           │       ├── model_entrenado.tflite
│   │           │       └── labels.txt
│   │           └── test/
│   ├── AI_model/
│   ├── Firebase_funtions/
│   └── web_service/
└── docs/
    ├── research/
    └── TFG_memory/
        

```

### Descripción de carpetas importantes:


- **`code/android_app/app/src/main/java/com/example/`**: Contiene el código fuente principal de la aplicación, donde se implementa la lógica de negocio y las funcionalidades principales.
- **`code/android_app/app/src/main/res/layout/`**: Archivos XML que definen las vistas y pantallas de la aplicación, especificando cómo se verá la interfaz de usuario.
- **`code/android_app/app/src/main/res/drawable/`**: Recursos gráficos como imágenes e íconos utilizados en la interfaz de usuario.
- **`code/android_app/app/src/main/res/values/`**: Cadenas de texto, colores y estilos reutilizables para mantener una interfaz coherente.
- **`code/android_app/app/src/main/res/mipmap-*/`**: Íconos de la aplicación en múltiples resoluciones para garantizar compatibilidad con distintos dispositivos Android.
- **`code/android_app/app/src/androidTest/`** y **`code/android_app/app/src/test/`**: Incluyen pruebas instrumentadas y pruebas unitarias para verificar el correcto funcionamiento de la aplicación.
- **`docs/`**: Almacena documentación técnica y académica del proyecto, como investigaciones, diagramas y la memoria del TFG.
- **`code/AI_model/`**: Contiene los scripts y recursos necesarios para entrenar y convertir el modelo de inteligencia artificial a formato TensorFlow Lite.
- **`code/Firebase_funtions/`**: Código para implementar funciones en la nube que gestionan el envío de notificaciones cuando cambia el estado de una incidencia.
- **`code/web_service/`**: Contiene la lógica del sitio web desde el cual se pueden visualizar y actualizar las incidencias reportadas.


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

