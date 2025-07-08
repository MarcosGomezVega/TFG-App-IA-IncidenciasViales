import os
import numpy as np
import cv2
import xml.etree.ElementTree as ET

import tensorflow as tf
from tensorflow.keras.applications import MobileNetV2
from tensorflow.keras.applications.mobilenet_v2 import preprocess_input
from tensorflow.keras.layers import Dense
from tensorflow.keras.models import Model
from tensorflow.keras.optimizers import Adam
import matplotlib.pyplot as plt
from sklearn.model_selection import train_test_split

# === RUTAS ===
cracked_dir = r'C:\Users\marco\AndroidStudioProjects\TFG\TFG-App-IA-IncidenciasViales\ModeloIA\datasets\cracks\Pavements\Cracked'
holes_dir = r'C:\Users\marco\AndroidStudioProjects\TFG\TFG-App-IA-IncidenciasViales\ModeloIA\datasets\holes2\images'
annotations_dir = r'C:\Users\marco\AndroidStudioProjects\TFG\TFG-App-IA-IncidenciasViales\ModeloIA\datasets\holes2\annotations'
postesCaidos_dir = r'C:\Users\marco\AndroidStudioProjects\TFG\TFG-App-IA-IncidenciasViales\ModeloIA\datasets\PostesCaidos\train'

# === MAPA DE CLASES ===
label_map = {
    'minor_pothole': 1,
    'medium_pothole': 2,
    'major_pothole': 3
}

# === DATOS: GRIETAS (Clase 0) Y POSTES (Clase 4) ===
image_paths = []
labels = []

for f in os.listdir(cracked_dir):
    if f.lower().endswith(('.jpg', '.jpeg', '.png')):
        image_paths.append(os.path.join(cracked_dir, f))
        labels.append(0)  # Grieta

for f in os.listdir(postesCaidos_dir):
    if f.lower().endswith(('.jpg', '.jpeg', '.png')):
        image_paths.append(os.path.join(postesCaidos_dir, f))
        labels.append(4)  # Poste caído

# === DATOS: BACHES ===
for file in os.listdir(annotations_dir):
    if file.endswith('.xml'):
        tree = ET.parse(os.path.join(annotations_dir, file))
        root = tree.getroot()
        filename = root.find('filename').text
        image_path = os.path.join(holes_dir, filename)

        classes = [obj.find('name').text for obj in root.findall('object')]
        if not classes:
            continue

        severities = list(filter(lambda c: c in label_map, classes))
        if not severities:
            continue

        selected_class = max(severities, key=lambda c: label_map[c])

        if os.path.exists(image_path):
            image_paths.append(image_path)
            labels.append(label_map[selected_class])

print(f"Total imágenes cargadas: {len(image_paths)}")

def cargar_y_preprocesar_cv2(file_path):
    img = cv2.imread(file_path)
    if img is None:
        raise ValueError(f"No se pudo cargar la imagen: {file_path}")
    img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)

    # Ecualización del histograma en YUV
    img_yuv = cv2.cvtColor(img, cv2.COLOR_RGB2YUV)
    img_yuv[:, :, 0] = cv2.equalizeHist(img_yuv[:, :, 0])
    img_eq = cv2.cvtColor(img_yuv, cv2.COLOR_YUV2RGB)

    # Resize
    img_resized = cv2.resize(img_eq, (224, 224))

    # Preprocesamiento para MobileNetV2
    img_preprocessed = preprocess_input(img_resized.astype(np.float32))
    return img_preprocessed


# === PREPROCESAMIENTO DE DATOS ===
data = np.empty((len(image_paths), 224, 224, 3), dtype=np.float32)
labels_np = np.array(labels)

for i, file_path in enumerate(image_paths):
    data[i] = cargar_y_preprocesar_cv2(file_path)

# === DIVISIÓN TRAIN / VALIDACIÓN ===
X_train, X_val, y_train, y_val = train_test_split(data, labels_np, test_size=0.2, stratify=labels_np)

# === MODELO ===
base_model = MobileNetV2(weights='imagenet', include_top=False, pooling='avg')
x = base_model.output
output = Dense(5, activation='softmax')(x)  # 5 clases (0–4)

model = Model(inputs=base_model.input, outputs=output)

for layer in base_model.layers:
    layer.trainable = False

model.compile(optimizer=Adam(),
              loss='sparse_categorical_crossentropy',
              metrics=['accuracy'])

model.fit(X_train, y_train, validation_data=(X_val, y_val), epochs=5, batch_size=32, verbose=2)


# === EXPORTAR COMO SAVEDMODEL COMPATIBLE CON TFLITE ===
model.export('modelo_entrenado')

# === CONVERTIR A TFLITE ===
converter = tf.lite.TFLiteConverter.from_saved_model('modelo_entrenado')
tflite_model = converter.convert()

# === GUARDAR MODELO .tflite ===
with open('modelo_entrenado.tflite', 'wb') as f:
    f.write(tflite_model)

print("✅ Modelo convertido y guardado como modelo_entrenado.tflite")

