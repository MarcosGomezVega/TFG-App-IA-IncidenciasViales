import os
import shutil

# Carpeta origen general
base_dir = r"C:\Users\marco\AndroidStudioProjects\TFG\TFG-App-IA-IncidenciasViales\ModeloIA\datasets\Dollar street trial"

# Carpeta destino final (todas las imágenes sin subcarpetas)
destino = r"C:\Users\marco\AndroidStudioProjects\TFG\TFG-App-IA-IncidenciasViales\ModeloIA\datasets\SInIncidencia"
os.makedirs(destino, exist_ok=True)

# Extensiones de imagen válidas
extensiones = ('.jpg', '.jpeg', '.png', '.bmp', '.gif')

# Recorremos todas las subcarpetas en la carpeta base
for nombre_carpeta in os.listdir(base_dir):
    ruta_carpeta = os.path.join(base_dir, nombre_carpeta)
    if os.path.isdir(ruta_carpeta):
        carpeta_img = os.path.join(ruta_carpeta, "img")
        if os.path.isdir(carpeta_img):
            for archivo in os.listdir(carpeta_img):
                if archivo.lower().endswith(extensiones):
                    ruta_origen = os.path.join(carpeta_img, archivo)
                    # Prefijo con el nombre de la carpeta para evitar colisiones
                    nuevo_nombre = f"{nombre_carpeta}_{archivo}"
                    ruta_destino = os.path.join(destino, nuevo_nombre)
                    shutil.copy2(ruta_origen, ruta_destino)

print("¡Todas las imágenes se han copiado correctamente a la carpeta 'SInIncidencia'!")