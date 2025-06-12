package com.example.myapplication;

import org.tensorflow.lite.Interpreter;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class TFLiteModel {

  private Interpreter interpreter;

  public TFLiteModel(AssetManager assetManager) throws IOException {
    MappedByteBuffer model = loadModelFile(assetManager, "modelo_entrenado.tflite");
    interpreter = new Interpreter(model);
  }

  private MappedByteBuffer loadModelFile(AssetManager assetManager, String modelPath) throws IOException {
    try (AssetFileDescriptor fileDescriptor = assetManager.openFd(modelPath);
         FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
         FileChannel fileChannel = inputStream.getChannel()) {

      long startOffset = fileDescriptor.getStartOffset();
      long declaredLength = fileDescriptor.getDeclaredLength();
      return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
  }

  public Interpreter getInterpreter() {
    return interpreter;
  }
}
