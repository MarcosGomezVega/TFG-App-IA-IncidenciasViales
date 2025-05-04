package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.example.myapplication.database.DBManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Main activity that allows the user to capture a photo using the device camera.
 * The photo is saved in external storage and displayed in an ImageView.
 *
 * @author Marcos Gomez Vega
 * @version 1.0
 */
public class MainActivity extends ActionBar {

  private static final int REQUEST_IMAGE_CAPTURE = 1;
  private static final int REQUEST_CAMERA_PERMISSION = 100;
  private static final int REQUEST_LOCATION_PERMISSION = 101;
  private Button btnTakePhoto;
  private Button btnSendIncident;
  private Button btnViewIncident;
  private ImageView imageView;
  private TextView imageViewTipoIncidencia;
  private TextView imageViewLocalizacion;
  private String currentPhotoPath;
  private Bitmap currentBitmap;
  private DBManager dbManager;
  private FusedLocationProviderClient fusedLocationClient;

  /**
   * Called when the activity is first created.
   * Initializes the UI and sets the button click listener.
   *
   * @param savedInstanceState the saved instance state bundle
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    setCustomActionBar(getString(R.string.title_activity_main));

    btnTakePhoto = findViewById(R.id.btnTakePhoto);
    btnSendIncident = findViewById(R.id.btnSendIncident);
    btnViewIncident = findViewById(R.id.btnViewIncidences);
    imageView = findViewById(R.id.imagePreview);
    imageViewTipoIncidencia = findViewById(R.id.textDetectedType);
    imageViewLocalizacion = findViewById(R.id.textLocation);

    dbManager = new DBManager(this);

    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
      != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this,
        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
        REQUEST_LOCATION_PERMISSION);
    }

    btnTakePhoto.setOnClickListener(v -> {
      if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
      } else {
        openCamera();
      }
    });

    btnSendIncident.setOnClickListener(v -> {
      String tipoIncidencia = imageViewTipoIncidencia.getText().toString();
      String localizacion = imageViewLocalizacion.getText().toString();
      String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
      int usuarioId = dbManager.obtenerIdUsuario("marcosgomezvegaportillo@gmail.com");
      String status = "pendiente";

      boolean estadoInsertado = dbManager.insertarIncidencia(usuarioId, tipoIncidencia, localizacion,currentPhotoPath,fecha,status );
      if (estadoInsertado) {
        Toast.makeText(this, "Incidencia registrada correctamente", Toast.LENGTH_SHORT).show();
      } else {
        Toast.makeText(this, "Error al registrar el estado", Toast.LENGTH_SHORT).show();
      }

      imageViewTipoIncidencia.setText("");
      imageViewLocalizacion.setText("");
      imageView.setImageResource(0);
      currentBitmap = null;
    });


    btnViewIncident.setOnClickListener(v -> {
      Intent intent = new Intent(MainActivity.this, IncidentActivity.class);
      startActivity(intent);
    });
  }

  /**
   * Opens the camera app to capture an image and saves the image to a file.
   * Uses FileProvider to securely share the file URI with the camera app.
   */
  private void openCamera() {
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if (intent.resolveActivity(getPackageManager()) != null) {
      File photoFile;
      try {
        photoFile = createImageFile();
      } catch (IOException ex) {
        Toast.makeText(this, Html.fromHtml("<font color='#FF0000'><b>" + getString(R.string.error_creating_image) + "</b></font>"), Toast.LENGTH_SHORT).show();
        return;
      }

      if (photoFile != null) {
        Uri photoURI = FileProvider.getUriForFile(
          this,
          "com.example.myapplication.fileprovider",
          photoFile
        );
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
      }
    }
  }

  /**
   * Callback for the result from requesting permissions.
   * If permission is granted, opens the camera.
   *
   * @param requestCode  the request code passed in requestPermissions
   * @param permissions  the requested permissions
   * @param grantResults the grant results for the corresponding permissions
   */
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == REQUEST_CAMERA_PERMISSION) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        openCamera();
      } else {
        Toast.makeText(this, getString(R.string.camera_permission_fail), Toast.LENGTH_SHORT).show();
      }
    }
  }

  /**
   * Receives the result from the camera activity.
   * If the result is OK, decodes the image file and sets it to the ImageView.
   *
   * @param requestCode the integer request code originally supplied
   * @param resultCode  the integer result code returned by the child activity
   * @param data        an Intent which can return result data to the caller
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
    imageView.setImageBitmap(bitmap);
    currentBitmap = bitmap;

    // Valor de prueba
    imageViewTipoIncidencia.setText("Asfalto roto");

    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
        if (location != null) {
          double lat = location.getLatitude();
          double lon = location.getLongitude();
          String coords = "Lat: " + lat + ", Lon: " + lon;
          imageViewLocalizacion.setText(coords);
        } else {
          imageViewLocalizacion.setText(getString(R.string.location_not_enabled));
        }
      }).addOnFailureListener(e -> {
        imageViewLocalizacion.setText(getString(R.string.error_having_location));
      });
    } else {
      imageViewLocalizacion.setText(getString(R.string.permission_locattion_not_enable));
    }
  }


  /**
   * Creates a temporary image file in the app's private external storage.
   * The path to the file is saved in currentPhotoPath.
   *
   * @return the created image file
   * @throws IOException if the file could not be created
   */
  private File createImageFile() throws IOException {
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    String imageFileName = "JPEG_" + timeStamp + "_";
    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    File image = File.createTempFile(
      imageFileName,
      ".jpg",
      storageDir
    );

    currentPhotoPath = image.getAbsolutePath();
    Log.d("RutaImagen", "Ruta de la imagen: " + currentPhotoPath);
    return image;
  }

}
