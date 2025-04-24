# Aplicaciones móviles existentes para reportar problemas en infraestructuras públicas

## Tabla de Contenidos

1. [Objetivo](#objetivo)  
2. [Metodología](#metodología)  
3. [Aplicaciones Analizadas](#aplicaciones-analizadas)  
   - [MunicipioInteligente](#1-municipiointeligente)
   - [Waze](#2-waze)
   - [TizAPP](#3-tizapp)
   - [FixMyStreet](#4-fixmystreet)  
   - [SeeClickFix](#5-seeclickfix)  
4. [Comparación](#comparativa-de-aplicaciones-para-reporte-de-incidencias-urbanas)
5. [Patrones UX Identificados](#patrones-ux-identificados)  
6. [Vacíos y Oportunidades](#vacíos-y-oportunidades)  
7. [Notas](#notas)

---

## Objetivo

Explorar aplicaciones móviles existentes que permiten a los usuarios reportar problemas en infraestructuras públicas, como carreteras o carriles bici, mediante fotos.

**Meta:** Comprender las soluciones actuales, características clave, limitaciones y patrones de experiencia de usuario (UX).

---

## Metodología

- **Tiendas de aplicaciones consultadas:** Google Play, Apple App Store  
- **Palabras clave utilizadas:** "reportar incidencias en la vía", "reportar baches", "reportar problemas públicos", etc.  
- **Enfoque en:**  
  - Flujo de usuario  
  - Subida de fotos  
  - Geoetiquetado  
  - Uso de IA para clasificación de incidencias
  - Mecanismos de retroalimentación

---

## Aplicaciones Analizadas

### 1. [MunicipioInteligente](https://municipiointeligente.es/incidencias_urbanas.html)
- **Plataforma:** Web 
- **Características principales:**
  - Reporte de incidencias urbanas mediante formulario
  - Posibilidad de adjuntar fotos
  - Visualización en mapa
  - Gestión y seguimiento por parte del ayuntamiento
- **Ventajas:**
  - Sistema orientado a municipios españoles
  - Integración directa con servicios municipales
  - Interfaz simple y accesible desde navegador
- **Desventajas:**
  - No tiene aplicación móvil nativa
  - Requiere acceso manual para subir fotos desde dispositivos móviles
  - Sin funcionalidades avanzadas como IA o geolocalización automática

### 2. [Waze](https://www.waze.com/es-419/live-map/)
- **Plataforma:** Android, iOS
- **Características principales:**
  - App para viajar
  - Reporte de incidencias en tiempo real 
  - Visualización de incidencias reportadas por otros usuarios
  - Visualización en el mapa con ubicación precisa
  - Categorización de la incidencia
- **Ventajas:**
  - Gran comunidad activa de usuarios que reportan incidencias constantemente
  - Actualizaciones en tiempo real de la situación del tráfico
  - Interfaz de usuario intuitiva y fácil de usar
  - Funciones de navegación integradas con el reporte de incidencias
- **Desventajas:**
  - Dependencia de la comunidad para mantener la precisión de la información
  - No tiene un sistema de validación oficial para las incidencias
  - La calidad de los reportes puede ser inconsistente debido a la naturaleza abierta de la app
  - No se puede subir fotos de las incidencias
  - No usa IA para detectar las incidencias

### 3. [TizAPP](https://tizapp.io/)
- **Plataforma:** Android, iOS
- **Características principales:**
  - Reporte de incidencias urbanas con fotos
  - Mapa interactivo con geolocalización precisa
  - Sistema de categorización de incidencias 
  - Notificación y seguimiento del estado de la incidencia
- **Ventajas:**
  - Interfaz amigable y fácil de usar para los ciudadanos
  - Comunicación directa con las autoridades locales
  - Soporte para que los usuarios puedan seguir el progreso de sus reportes
- **Desventajas:**
  - Limitada en algunos países en cuanto a cobertura de servicios y autoridades locales
  - No tiene IA para el análisis automático de fotos

### 4. [FixMyStreet](https://www.fixmystreet.com/)
- **Plataforma:** Android / iOS / Web
- **Características principales:**
  - Subida de fotos del problema
  - Detección de ubicación mediante GPS
  - Categorización de la incidencia
  - Visibilidad pública de los reportes
- **Ventajas:**
  - Interfaz limpia e intuitiva
  - Seguimiento en tiempo real
  - Es de codigo abierto, su [github](https://github.com/mysociety/fixmystreet)
- **Desventajas:**
  - Soporte de ubicación limitado en algunas regiones
  - Sin notificaciones push para actualizaciones
  - El usuario tiene qe poner que tipo de incidencia

### 5. [SeeClickFix](https://seeclickfix.com/)
- **Plataforma:** Android / iOS / Web
- **Características principales:**
  - Reporte con fotos
  - Mapas interactivos
  - Comunicación con autoridades locales
- **Ventajas:**
  - Fuerte integración con gobiernos locales
  - Funciones de participación comunitaria
- **Desventajas:**
  - Requiere cuenta para reportar
  - Poco uso fuera de EE.UU.
  - No usa IA para detectar la incidencia
---
## Comparativa de Aplicaciones para Reporte de Incidencias Urbanas

La siguiente tabla resume las principales características de varias aplicaciones orientadas a reportar problemas en infraestructuras públicas. Se comparan aspectos clave como el uso de fotos, geolocalización, inteligencia artificial (IA), y la plataforma de uso. Esta comparativa permite identificar fortalezas, debilidades y oportunidades de mejora en el ecosistema actual.

| App                 | Plataforma         | Fotos | Geolocalización | IA | Web/Móvil   | Ventaja clave                              |
|---------------------|--------------------|--------|--------------------|------|----------------|--------------------------------------------|
| MunicipioInteligente | Web                | ✅      | ❌                  | ❌   | Web            | Integración con ayuntamientos              |
| Waze                | Android, iOS       | ❌      | ✅                  | ❌   | Móvil          | Comunidad activa, tráfico en tiempo real   |
| TizAPP              | Android, iOS       | ✅      | ✅                  | ❌   | Móvil          | Seguimiento del reporte                    |
| FixMyStreet         | Android, iOS, Web  | ✅      | ✅                  | ❌   | Web y Móvil    | Código abierto, visibilidad pública        |
| SeeClickFix         | Android, iOS, Web  | ✅      | ✅                  | ❌   | Web y Móvil    | Integración con gobiernos locales          |

---

## Patrones UX Identificados

- **Flujo de reporte centrado en la foto**  
  Las aplicaciones analizadas se centran en permitir que los usuarios suban fotos de manera rápida y fácil para ilustrar el problema, lo cual facilita una identificación más clara del reporte. Esta es una característica fundamental en la mayoría de las apps analizadas.

- **Uso de pines en el mapa para una ubicación precisa**  
  Utilizan mapas interactivos con pines que los usuarios pueden colocar fácilmente para definir la ubicación del problema, lo que mejora la precisión de los reportes.

- **Categorías predefinidas para agilizar el reporte**  
  Muchas aplicaciones permiten seleccionar una categoría específica para el tipo de problema, lo que reduce el tiempo de ingreso de datos y mejora la organización.

- **Indicadores visuales del estado**  
  Las aplicaciones utilizan indicadores visuales para mostrar el estado de cada incidencia, lo que proporciona transparencia y mantiene al usuario informado sobre el progreso de su reporte.

---

## Vacíos y Oportunidades

- **Falta de análisis automatizado de imágenes con IA**  
  Integrar IA permitiría clasificar automáticamente las incidencias a partir de fotos, evitando que el usuario seleccione el tipo de problema. Esto agiliza el proceso, mejora la precisión y abre la posibilidad de admitir videos para análisis en tiempo real.
  
- **Poca personalización en las categorías de incidencias**  
  La mayoría de las apps utilizan categorías predefinidas limitadas para clasificar los problemas reportados. Se podría ofrecer una mayor personalización, permitiendo a los usuarios crear nuevas categorías o subcategorías según el tipo de incidencia.

- **Escasa interacción en tiempo real**  
  Muchas apps no permiten una comunicación fluida entre los usuarios y las autoridades locales. Un sistema de chat en tiempo real o una función de comentarios podría mejorar la comunicación y el seguimiento de los reportes.

---

## Notas

- **Accesibilidad:** Muchas aplicaciones no se centran en la accesibilidad para usuarios con discapacidades. Un área de mejora podría ser la implementación de opciones de accesibilidad como texto a voz o un modo de alto contraste para personas con dificultades visuales.

- **Tendencias en IA y Machine Learning:** La aplicación de técnicas de machine learning en la identificación y clasificación de problemas mediante imágenes y reportes podría ser clave para mejorar la precisión y la eficiencia de la plataforma.

- **Expansión internacional:** Algunas de las aplicaciones populares como [SeeClickFix](#5-seeclickfix) y [FixMyStreet](#4-fixmystreet) tienen un uso limitado fuera de los EE. UU. Existe una oportunidad de adaptar estas plataformas para ser más globales, añadiendo soporte multilingüe y adaptando el sistema a las necesidades locales.

