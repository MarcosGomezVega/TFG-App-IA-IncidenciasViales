package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {

  private static final int REQUEST_IMAGE_CAPTURE = 1;
  private Uri photoUri;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Inicializar el ImageButton
    ImageButton btnTakePhoto = findViewById(R.id.btnTakePhoto);

    // Configurar el listener del botón para abrir la cámara
    btnTakePhoto.setOnClickListener(v -> {
      openCamera();
    });
  }

  // Método para abrir la cámara
  private void openCamera() {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    // Verificar que hay una aplicación de cámara para manejar el intent
    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
      // Crear un archivo temporal para almacenar la foto
      try {
        photoUri = createImageUri(); // Debes crear una URI de almacenamiento
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);  // Enviar la URI para guardar la foto
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
      } catch (Exception e) {
        e.printStackTrace();
        Toast.makeText(this, "Error al acceder a la cámara", Toast.LENGTH_SHORT).show();
      }
    } else {
      Toast.makeText(this, "No hay aplicación de cámara disponible", Toast.LENGTH_SHORT).show();
    }
  }

  // Método para crear una URI temporal para almacenar la imagen
  private Uri createImageUri() {
    // Crea un archivo temporal para la foto y devuelve la URI
    // Aquí usarías un FileProvider o la API correspondiente en Android 11+ (Scoped Storage)
    // Este es solo un ejemplo simplificado, asegúrate de manejar correctamente el almacenamiento
    return Uri.fromFile(new File(getExternalFilesDir(null), "photo.jpg"));
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
      // La foto fue tomada exitosamente, ahora puedes hacer algo con la imagen
      // Por ejemplo, mostrarla en una ImageView
      // imageView.setImageURI(photoUri);  // Si tienes una ImageView para mostrar la foto
    }
  }
}
