# Modelos IA para Detección de Incidencias en la Vía Pública

Este documento recoge un análisis comparativo de distintos modelos de inteligencia artificial orientados a la clasificación de imágenes, evaluando su rendimiento en aplicaciones móviles mediante TensorFlow Lite. 


---

## Tabla de Contenidos

- [Objetivo](#objetivo)
- [Metodologia](#metodología)
- [Modelos Evaluados](#modelos-evaluados)
    - [MobileNetV2](#mobilenetv2)
    - [Efficientnet_v2](#efficientnet_v2)
    - [InceptionV3](#inceptionv3)
    - [ResNet-50](#resnet-50)
- [Características Comunes](#características-comunes)
- [Tabla Comparativa](#tabla-comparativa)
- [Conclusión y Modelo Seleccionado](#conclusión-y-modelo-seleccionado)

---

##  Objetivo
El **objetivo** es determinar el modelo más adecuado para mi app Android que detecte incidencias en la vía pública, como baches, basura, señales caídas o daños estructurales.

### **Meta**
Identificar el mejor modelo IA de clasificacion de images para la integración eficiente en aplicaciones móviles, tomando en cuenta la facilidad de uso, soporte, y capacidades.

---

## Metodología 

- **Fuente consultadas** Revisiar los modelos de IA en [Kaggle](https://www.kaggle.com/)

- **Enfoque en:** 
    - Tipo de modelo
    - Compatibildiad con Andorid 
    - Tamaño del modelo
    - Reentrenamiento
    - Velocidad de analisis 
    - Soporte de la comunidad y documentación disponible  

--- 

## Modelos Evaluados

### [MobileNetV2](https://www.kaggle.com/models/google/mobilenet-v2)

Modelo de detección de objetos basado en SSD entrenado en Open Images V4 con ImageNet MobileNet V2 preentrenado como extractor de características de imagen.
- Las entradas del modelo es una imagen de tres canales de tamaño variable pero el modelo NO es compatible Dosificación
- Las salida es un diccionario que contiene:
    - Un tensor de forma que contiene Coordenadas del cuadro delimitador.
    - Un tensor de forma que contiene nombres de clase de detección.
    - Un tensor de forma que contiene Nombres de clases de detección legibles por humano.
    - Un tensor de forma con clase índices.
    - Un tensor de forma que contiene puntuaciones de detección.
- El modelo su puede reentrenar, pero ya viene entrenado con umagesnes de clasificación.

### [Efficientnet_v2](https://www.kaggle.com/models/google/efficientnet-v2)

EfficientNets V2 son una familia de modelos de clasificación de imágenes, que logran precisión de última generación, pero siendo un orden de magnitud más pequeño y más rápido que los modelos anteriores.

- La entrada es una imagen y se espera que tenga valores de color en el rango [0,1], siguiendo las convenciones comunes de entrada de imagen. El tamaño esperado de las imágenes de entrada es x = 299 x 299 píxeles de forma predeterminada, pero son posibles otros tamaños de entrada (dentro de los límites).
- La salida es un lote de vectores logits. Los índices en los logits son las clases = 1001 de la clasificación del entrenamiento original. 
- El modelo se puede reentrenar ya que los pesos se obtuvieron mediante ILSVRC-2012-CLS conjunto de datos para la clasificación de imágenes ("Imagenet") con preprocesamiento AutoAugment.

### [InceptionV3](https://www.kaggle.com/models/google/inception-v3/tensorFlow2/classification/2?tfhub-redirect=true)

Inception V3 es una arquitectura de red neuronal para la clasificación de imágenes. Este módulo TF-Hub utiliza la implementación TF-Slim de . El módulo contiene una instancia entrenada de la red, empaquetada para realizar la clasificación de imágenes con la que se entrenó la red.

- La entrada es una imagen y se espera que tenga valores de color en el rango [0,1], siguiendo las convenciones comunes de entrada de imagen. El tamaño esperado de las imágenes de entrada es x = 299 x 299 píxeles de forma predeterminada, pero son posibles otros tamaños de entrada (dentro de los límites).
- La salida es un lote de vectores logits. Los índices en los logits son las clases = 1001 de la clasificación de la formación original. 
- El modelo se puede reenetrenar, los pesos se ovtuvieron originalmente mediante entrenamiento en el ILSVRC-2012-CLS conjunto de datos para la clasificación de imágenes. 

### [ResNet-50](https://www.kaggle.com/models/tensorflow/resnet-50)
ResNet es una familia de arquitecturas de red para la clasificación de imágenes. Este modelo de TF Hub utiliza la implementación de ResNet con 50 capas de tensorflow/models/official/legacy/image_classification. El modelo contiene una instancia entrenada de la red, empaquetada para realizar la clasificación de imágenes con la que se entrenó la red.

- La entrada es una imagen que tenga valores de color en el rango [0,1], siguiendo las convenciones comunes de entrada de imagen. Para este modelo, el tamaño de las imágenes de entrada se fija en x = 224 x 224 píxeles.
- La salida es un lote de vectores logits. Los índices en los logits son las clases = 1001 de la clasificación del entrenamiento original.
- El modelo se puede reentrenar y los pesos de este modelo se han obtenido mediante entrenamiento en el ILSVRC-2012-CLS conjunto de datos para la clasificación de imágenes 

---

## Características Comunes

Los modelos analizados presentan una serie de características clave que los hacen adecuados para tareas de clasificación de imágenes en dispositivos móviles. A continuación se resumen los principales puntos en común:

- **Compatibilidad con TensorFlow Lite**: Todos los modelos pueden ser convertidos al formato .tflite, lo que permite ejecutarlos de forma eficiente en dispositivos Android con recursos limitados.

- **Reentrenamiento posible**: Permiten ser ajustados con datasets personalizados, lo cual es importante para adaptar el modelo a la detección de incidencias específicas en la vía pública.

- **Entrenados con ImageNet**: Todos tienen un entrenamiento previo sobre el conjunto de datos ImageNet, lo que hace que tengan una base sólida de reconocimiento visual general.

- **Facilidad de integración con herramientas de entrenamiento**: Son compatibles con frameworks como TensorFlow Model Maker, Keras o TF-Slim, lo que facilita su implementación y ajuste.

- **Interfaz de entrada/salida estandarizada**: Utilizan entradas de imagen en formatos comunes y devuelven salidas tipo logits o vectores de predicción.

- **Disponibilidad pública y documentación**: Están disponibles en plataformas como TensorFlow Hub o Kaggle Models, con soporte comunitario y documentación detallada para su uso.

---

## Tabla Comparativa

| Modelo             | Precisión     | Tamaño del Modelo | Velocidad de Inferencia | Reentrenamiento | Ideal para Móvil |
|--------------------|---------------|-------------------|--------------------------|-------------------|------------------|
| **MobileNetV2**    | Media-Alta    | Bajo              | Muy Alta ⚡              | ✅ Sí             | ✅ Sí            |
| **EfficientNet V2**| Alta          | Medio             | Alta ⚡                  | ✅ Sí             | ✅ Sí            |
| **InceptionV3**    | Muy Alta      | Alto              | Baja 🐢                 | ✅ Sí             | ❌ No            |
| **ResNet-50**      | Muy Alta      | Alto              | Baja 🐢                 | ✅ Sí             | ❌ No            |


---

## Conclusión y Modelo Seleccionado

Tras el análisis comparativo realizado, he decidido trabajar con MobileNetV2 como modelo base para la detección de incidencias en la vía pública. Esta elección se basa en varios factores clave:

- Tiene una relación muy equilibrada entre precisión, velocidad y tamaño, lo que la hace ideal para su uso en dispositivos móviles.

- Es reentrenable fácilmente con datasets personalizados, lo que permite adaptarla a los distintos tipos de incidencias que se quieren detectar.

- Es uno de los modelos más utilizados y descargados en plataformas como Kaggle, lo que implica una gran cantidad de ejemplos, tutoriales y recursos disponibles para su implementación.

- Su velocidad de inferencia es muy alta, lo cual es esencial para el funcionamiento en tiempo real dentro de la app.

- Cuenta con amplia compatibilidad con TensorFlow Lite, facilitando su integración directa en Android Studio.

---
