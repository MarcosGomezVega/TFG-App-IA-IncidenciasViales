from tensorflow.keras.models import load_model
from tensorflow.keras.applications.mobilenet_v2 import preprocess_input
import numpy as np
import cv2
import matplotlib.pyplot as plt

# Cargar modelo entrenado
model = load_model("modelo_incidentes.h5")

# Mapeo de clases
etiquetas = ['Grieta', 'Agujero leve', 'Agujero medio', 'Agujero grave', 'Poste caído']

def predecir_imagen(file_path):
    # Cargar y preprocesar imagen
    img = cv2.imread(file_path)
    if img is None:
        print(f"No se pudo cargar la imagen: {file_path}")
        return
    img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)

    # Ecualización del histograma en YUV
    img_yuv = cv2.cvtColor(img, cv2.COLOR_RGB2YUV)
    img_yuv[:, :, 0] = cv2.equalizeHist(img_yuv[:, :, 0])
    img_eq = cv2.cvtColor(img_yuv, cv2.COLOR_YUV2RGB)

    img_resized = cv2.resize(img_eq, (224, 224))
    img_preprocessed = preprocess_input(img_resized.astype(np.float32))
    img_batch = np.expand_dims(img_preprocessed, axis=0)

    # Predicción
    pred = model.predict(img_batch)
    clase = np.argmax(pred)
    confianza = pred[0][clase] * 100

    print(f"Predicción: {etiquetas[clase]} ({confianza:.2f}%)")

    # Mostrar imagen con etiqueta
    plt.imshow(img)
    plt.title(f"{etiquetas[clase]} ({confianza:.1f}%)")
    plt.axis('off')
    plt.show()

# Ejecutar predicción sobre una imagen de prueba
predecir_imagen(r'C:\Users\marco\AndroidStudioProjects\TFG\TFG-App-IA-IncidenciasViales\ModeloIA\IMG\posteCaido.jpg')
