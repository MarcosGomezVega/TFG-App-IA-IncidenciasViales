import tensorflow as tf
from tensorflow.keras.applications import MobileNetV2
from tensorflow.keras.preprocessing import image
from tensorflow.keras.applications.mobilenet_v2 import preprocess_input, decode_predictions
import numpy as np
from imageio import imread
from PIL import Image

# Load the MobileNetV2 model pre-trained on ImageNet
model = MobileNetV2(weights='imagenet')
# Load and preprocess the image
data = np.empty((1, 224, 224, 3))
# Load the image using imageio
img_path = r'.\IMG\gato.jpg' 
#read the image using imageio
img= imread(img_path)
# resize the image to 224x224 using PIL
img_resized = np.array(Image.fromarray(img).resize((224, 224)))
# Convert the image to a numpy array and preprocess it
data[0] = img_resized
#preprocess the image for MobileNetV2
data = preprocess_input(data)
# Make predictions
predictions = model.predict(data)
# Get the predicted class index with the highest probability
output_neuron = np.argmax(predictions[0])
# select the highest probability class
top_prediction = decode_predictions(predictions, top=1)[0][0]
# Get the class name, description, and score
name, desc, score = top_prediction
# Print the results
print('- {} ({:.2f}%)'.format(desc, 100 * score))