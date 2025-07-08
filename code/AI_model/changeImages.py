import os
import cv2
from PIL import Image, ImageEnhance
import numpy as np

# Ruta de entrada y salida
input_folder = r'C:\Users\marco\AndroidStudioProjects\TFG\TFG-App-IA-IncidenciasViales\ModeloIA\datasets\PostesCaidos'



def augment_image(image_path, output_folder, base_name):
    img = Image.open(image_path)

    count = 0

    # 1. Imagen original redimensionada
    img_resized = img.resize((int(img.width * 0.5), int(img.height * 0.5)))
    img_resized.save(os.path.join(output_folder, f"{base_name}_resize.jpg"))
    count += 1

    # 2. Imagen rotada
    img_rotated = img.rotate(90)
    img_rotated.save(os.path.join(output_folder, f"{base_name}_rotated.jpg"))
    count += 1

    # 3. Imagen volteada horizontalmente
    img_flipped = img.transpose(Image.FLIP_LEFT_RIGHT)
    img_flipped.save(os.path.join(output_folder, f"{base_name}_flipped.jpg"))
    count += 1

    # 4. Imagen con brillo aumentado
    enhancer = ImageEnhance.Brightness(img)
    img_brighter = enhancer.enhance(1.5)  # 50% más brillante
    img_brighter.save(os.path.join(output_folder, f"{base_name}_bright.jpg"))
    count += 1

    # 5. Imagen con brillo reducido
    img_darker = enhancer.enhance(0.5)  # 50% más oscura
    img_darker.save(os.path.join(output_folder, f"{base_name}_dark.jpg"))
    count += 1

    return count

# Iterar sobre las imágenes
total_augmented = 0
for filename in os.listdir(input_folder):
    if filename.lower().endswith(('.png', '.jpg', '.jpeg')):
        image_path = os.path.join(input_folder, filename)
        base_name = os.path.splitext(filename)[0]
        total_augmented += augment_image(image_path, input_folder, base_name)

print(f"✅ Total de imágenes aumentadas: {total_augmented}")
print(f"📁 Carpeta de salida: {input_folder}")
