import tensorflow as tf

# Cargar modelo H5
converter = tf.lite.TFLiteConverter.from_saved_model('modelo_entrenado')


tflite_model = converter.convert()

# Guardar modelo TFLite
with open("modelo_incidentes.tflite", "wb") as f:
    f.write(tflite_model)

