from tensorflow.keras.layers import Input, Dense, Dropout, TimeDistributed, RepeatVector
from tensorflow.keras.models import Model
from tensorflow.keras.applications import EfficientNetB2
from tensorflow.keras.applications.efficientnet import preprocess_input
from tensorflow.keras.optimizers import Adam
from tensorflow.keras.utils import Sequence
from tensorflow.keras.callbacks import EarlyStopping
from sklearn.model_selection import train_test_split
from sklearn.utils import resample
import numpy as np
import pandas as pd
import os
import cv2
import xml.etree.ElementTree as ET
from PIL import Image
import math
import tensorflow as tf

# --- Funciones de carga de datasets (sin cambios) ---

def load_rdd2022_dataset(root_folder):
    data = []
    for country_1 in os.listdir(root_folder):
        path_1 = os.path.join(root_folder, country_1)
        if not os.path.isdir(path_1): continue
        for country_2 in os.listdir(path_1):
            path_2 = os.path.join(path_1, country_2)
            if not os.path.isdir(path_2): continue
            train_path = os.path.join(path_2, 'train')
            images_path = os.path.join(train_path, 'images')
            annotations_path = os.path.join(train_path, 'annotations', 'xmls')
            if not os.path.exists(images_path) or not os.path.exists(annotations_path): continue
            for xml_file in os.listdir(annotations_path):
                if not xml_file.endswith('.xml'): continue
                xml_path = os.path.join(annotations_path, xml_file)
                try:
                    tree = ET.parse(xml_path)
                    root = tree.getroot()
                    filename = root.find('filename').text
                    img_path = os.path.join(images_path, filename)
                    for obj in root.findall('object'):
                        label = obj.find('name').text.strip()
                        bbox = obj.find('bndbox')
                        xmin = int(float(bbox.find('xmin').text))
                        ymin = int(float(bbox.find('ymin').text))
                        xmax = int(float(bbox.find('xmax').text))
                        ymax = int(float(bbox.find('ymax').text))
                        data.append([img_path, xmin, ymin, xmax, ymax, label])
                except Exception as e:
                    print(f'❌ Error en {xml_path}: {e}')
    df = pd.DataFrame(data, columns=['image_path', 'xmin', 'ymin', 'xmax', 'ymax', 'label'])
    print(f"✅ RDD2022 dataset cargado: {len(df)} muestras")
    return df

def load_humans_dataset(root_folder):
    data = []
    for split in ['train', 'val']:
        images_path = os.path.join(root_folder, 'images', split)
        labels_path = os.path.join(root_folder, 'labels', split)
        if not os.path.exists(images_path) or not os.path.exists(labels_path): continue
        for label_file in os.listdir(labels_path):
            if not label_file.endswith('.txt'): continue
            label_path = os.path.join(labels_path, label_file)
            img_filename = os.path.splitext(label_file)[0] + '.jpg'
            img_path = os.path.join(images_path, img_filename)
            if not os.path.exists(img_path):
                img_filename = os.path.splitext(label_file)[0] + '.png'
                img_path = os.path.join(images_path, img_filename)
                if not os.path.exists(img_path):
                    print(f"⚠️ Imagen no encontrada para {label_path}")
                    continue
            with Image.open(img_path) as img:
                width, height = img.size
            with open(label_path, 'r') as f:
                for line in f:
                    parts = line.strip().split()
                    if len(parts) != 5: continue
                    class_id = parts[0]
                    x_center_rel = float(parts[1])
                    y_center_rel = float(parts[2])
                    width_rel = float(parts[3])
                    height_rel = float(parts[4])
                    xmin = int((x_center_rel - width_rel / 2) * width)
                    ymin = int((y_center_rel - height_rel / 2) * height)
                    xmax = int((x_center_rel + width_rel / 2) * width)
                    ymax = int((y_center_rel + height_rel / 2) * height)
                    data.append([img_path, xmin, ymin, xmax, ymax, class_id])
    df = pd.DataFrame(data, columns=['image_path', 'xmin', 'ymin', 'xmax', 'ymax', 'label'])
    print(f"✅ Humans dataset cargado: {len(df)} muestras")
    return df

def load_voc2012_dataset(root_folder):
    images_path = os.path.join(root_folder, 'JPEGImages')
    annotations_path = os.path.join(root_folder, 'Annotations')
    data = []
    for xml_file in os.listdir(annotations_path):
        if not xml_file.endswith('.xml'): continue
        xml_path = os.path.join(annotations_path, xml_file)
        try:
            tree = ET.parse(xml_path)
            root = tree.getroot()
            filename = root.find('filename').text
            img_path = os.path.join(images_path, filename)
            for obj in root.findall('object'):
                label = obj.find('name').text.strip().lower()
                if label in ['person', 'human']:
                    class_id = 0
                else:
                    class_id = 1
                bbox = obj.find('bndbox')
                xmin = int(float(bbox.find('xmin').text))
                ymin = int(float(bbox.find('ymin').text))
                xmax = int(float(bbox.find('xmax').text))
                ymax = int(float(bbox.find('ymax').text))
                data.append([img_path, xmin, ymin, xmax, ymax, class_id])
        except Exception as e:
            print(f"❌ Error en {xml_path}: {e}")
    df = pd.DataFrame(data, columns=['image_path', 'xmin', 'ymin', 'xmax', 'ymax', 'label'])
    print(f"✅ VOC2012 dataset cargado: {len(df)} muestras")
    print(df['label'].value_counts())
    return df

def unify_labels(df, person_labels=set(['person', 'human', 0])):
    def map_label(label):
        if label in person_labels or str(label).lower() in person_labels:
            return 0
        return label
    df['label'] = df['label'].apply(map_label)
    return df

# --- DataGenerator para detección con múltiples cajas ---

class MultiBoxDataGenerator(Sequence):
    def __init__(self, df, batch_size=4, img_size=(224, 224), shuffle=True, class_to_idx=None, max_boxes=10):
        self.df = df
        self.batch_size = batch_size
        self.img_size = img_size
        self.shuffle = shuffle
        self.max_boxes = max_boxes
        self.class_to_idx = class_to_idx or {name: idx for idx, name in enumerate(sorted(self.df['label'].unique()))}
        self.groups = df.groupby('image_path')
        self.image_paths = list(self.groups.groups.keys())
        self.on_epoch_end()

    def __len__(self):
        return math.ceil(len(self.image_paths) / self.batch_size)

    def on_epoch_end(self):
        self.indices = np.arange(len(self.image_paths))
        if self.shuffle:
            np.random.shuffle(self.indices)

    def __getitem__(self, index):
        batch_indices = self.indices[index * self.batch_size:(index + 1) * self.batch_size]
        batch_image_paths = [self.image_paths[i] for i in batch_indices]
        images = []
        batch_bboxes = []
        batch_labels = []
        for img_path in batch_image_paths:
            img = cv2.imread(img_path)
            if img is None:
                print(f"⚠️ Imagen no encontrada: {img_path}")
                images.append(np.zeros((self.img_size[0], self.img_size[1], 3), dtype=np.float32))
                batch_bboxes.append(np.zeros((self.max_boxes, 4), dtype=np.float32))
                batch_labels.append(np.full((self.max_boxes,), -1, dtype=np.int32))
                continue
            original_h, original_w = img.shape[:2]
            img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
            img_resized = cv2.resize(img, self.img_size)
            img_resized = preprocess_input(img_resized.astype(np.float32))

            annotations = self.groups.get_group(img_path)
            boxes = []
            labels = []
            for _, row in annotations.iterrows():
                x_min = row['xmin'] / original_w
                y_min = row['ymin'] / original_h
                x_max = row['xmax'] / original_w
                y_max = row['ymax'] / original_h
                boxes.append([x_min, y_min, x_max, y_max])
                labels.append(self.class_to_idx[row['label']])

            # Padding o recorte para max_boxes
            if len(boxes) > self.max_boxes:
                boxes = boxes[:self.max_boxes]
                labels = labels[:self.max_boxes]
            else:
                while len(boxes) < self.max_boxes:
                    boxes.append([0, 0, 0, 0])
                    labels.append(-1)  # etiqueta ignorada

            images.append(img_resized)
            batch_bboxes.append(boxes)
            batch_labels.append(labels)

        return np.array(images), {
            'bbox': np.array(batch_bboxes, dtype=np.float32),
            'class': np.array(batch_labels, dtype=np.int32)
        }

# --- Funciones de pérdida ---

def smooth_l1_loss(y_true, y_pred):
    diff = tf.abs(y_true - y_pred)
    less_than_one = tf.cast(tf.less(diff, 1.0), tf.float32)
    loss = less_than_one * 0.5 * diff ** 2 + (1 - less_than_one) * (diff - 0.5)
    return tf.reduce_mean(loss)

def masked_sparse_categorical_crossentropy(y_true, y_pred):
    mask = tf.not_equal(y_true, -1)
    y_true_masked = tf.boolean_mask(y_true, mask)
    y_pred_masked = tf.boolean_mask(y_pred, mask)
    loss = tf.keras.losses.sparse_categorical_crossentropy(y_true_masked, y_pred_masked)
    return tf.reduce_mean(loss)

def masked_accuracy(y_true, y_pred):
    mask = tf.not_equal(y_true, -1)
    y_true_masked = tf.boolean_mask(y_true, mask)
    y_pred_masked = tf.boolean_mask(y_pred, mask)
    y_pred_labels = tf.argmax(y_pred_masked, axis=-1)
    correct = tf.equal(tf.cast(y_true_masked, tf.int32), tf.cast(y_pred_labels, tf.int32))
    return tf.reduce_mean(tf.cast(correct, tf.float32))

# --- Paths a datasets (ajusta según tu entorno) ---

rdd_root = r'C:\Users\marco\AndroidStudioProjects\TFG\TFG-App-IA-IncidenciasViales\code\AI_model\datasets\Segmentation\RDD2022'
humans_root = r'C:\Users\marco\AndroidStudioProjects\TFG\TFG-App-IA-IncidenciasViales\code\AI_model\datasets\Segmentation\humans'
voc_root = r'C:\Users\marco\AndroidStudioProjects\TFG\TFG-App-IA-IncidenciasViales\code\AI_model\datasets\Segmentation\VOCdevkit\VOC2012'

print("⏳ Cargando datasets...")
df_rdd = load_rdd2022_dataset(rdd_root)
df_humans = load_humans_dataset(humans_root)
df_voc = load_voc2012_dataset(voc_root)

df_humans = unify_labels(df_humans, person_labels=set([0]))
df_voc = unify_labels(df_voc, person_labels=set(['person', 'human', 0]))

df = pd.concat([df_rdd, df_humans, df_voc], ignore_index=True)

# Filtrar clases no deseadas
clases_a_eliminar = ['block crack', 'd0w0', 'D11']
df = df[~df['label'].astype(str).str.lower().isin([c.lower() for c in clases_a_eliminar])].reset_index(drop=True)

# Asegurar etiquetas tipo string para el mapeo correcto
df['label'] = df['label'].astype(str)
df['label'] = df['label'].replace({'0': '0'})  # mantener coherencia tipo str

# Balancear el dataset
dfs = []
n_samples = 15000
for label in df['label'].unique():
    subset = df[df['label'] == label]
    if len(subset) > n_samples:
        subset = resample(subset, replace=False, n_samples=n_samples, random_state=42)
    dfs.append(subset)
df_balanced = pd.concat(dfs).sample(frac=1, random_state=42).reset_index(drop=True)

print("✅ Dataset final filtrado y balanceado:")
print(df_balanced['label'].value_counts())

# Crear diccionario clase->índice
unique_labels = sorted(df_balanced['label'].unique())
class_to_idx = {label: idx for idx, label in enumerate(unique_labels)}

print("📌 Diccionario único de clases a índices:")
print(class_to_idx)

df_train, df_val = train_test_split(df_balanced, test_size=0.2, random_state=42)
print(f"Muestras entrenamiento: {len(df_train)}")
print(f"Muestras validación: {len(df_val)}")

train_gen = MultiBoxDataGenerator(df_train, batch_size=4, img_size=(224, 224), class_to_idx=class_to_idx, max_boxes=5)
val_gen = MultiBoxDataGenerator(df_val, batch_size=4, img_size=(224, 224), class_to_idx=class_to_idx, max_boxes=5)

# --- Modelo EfficientNetB2 para detección ---

max_boxes = 5
num_classes = len(class_to_idx)

input_tensor = Input(shape=(224, 224, 3))
base_model = EfficientNetB2(include_top=False, weights='imagenet', input_tensor=input_tensor, pooling='avg')

# Congelar todas capas menos últimas 20
for layer in base_model.layers:
    layer.trainable = False
for layer in base_model.layers[-20:]:
    layer.trainable = True

x = base_model.output
x = Dropout(0.3)(x)
x = RepeatVector(max_boxes)(x)  # repetir vector para cada bbox

bbox_output = TimeDistributed(Dense(4), name='bbox')(x)  # coordenadas normalizadas
class_output = TimeDistributed(Dense(num_classes, activation='softmax'), name='class')(x)

model = Model(inputs=input_tensor, outputs=[bbox_output, class_output])

model.compile(
    optimizer=Adam(1e-4),
    loss={
        'bbox': smooth_l1_loss,
        'class': masked_sparse_categorical_crossentropy
    },
    metrics={
        'class': masked_accuracy
    }
)

# --- Entrenamiento ---

early_stop = EarlyStopping(monitor='val_loss', patience=5, restore_best_weights=True)

history = model.fit(
    train_gen,
    validation_data=val_gen,
    epochs=30,
    callbacks=[early_stop],
    verbose=2
)

print("✅ Entrenamiento finalizado")
