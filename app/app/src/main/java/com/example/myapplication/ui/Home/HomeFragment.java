package com.example.myapplication.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.Html;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.myapplication.R;
import com.example.myapplication.TFLiteModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.os.Environment;
import android.provider.MediaStore;
import android.Manifest;

import androidx.core.content.FileProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import org.tensorflow.lite.Interpreter;

public class HomeFragment extends Fragment {

  private ImageView imageView;
  private TextView imageViewTipoIncidencia;
  private TextView imageViewLocalizacion;
  private String currentPhotoPath;

  // Launcher para tomar foto con Uri
  private ActivityResultLauncher<Uri> takePictureLauncher;

  // Launcher para pedir permiso cámara
  private ActivityResultLauncher<String> requestCameraPermissionLauncher;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
                           ViewGroup container, Bundle savedInstanceState) {

    View root = inflater.inflate(R.layout.fragment_home, container, false);

    Button btnTakePhoto = root.findViewById(R.id.btnTakePhoto);
    Button btnSendIncident = root.findViewById(R.id.btnSendIncident);
    Button btnViewIncident = root.findViewById(R.id.btnViewIncidences);
    imageView = root.findViewById(R.id.imagePreview);
    imageViewTipoIncidencia = root.findViewById(R.id.textDetectedType);
    imageViewLocalizacion = root.findViewById(R.id.textLocation);

    // Registrar launcher para tomar foto
    takePictureLauncher = registerForActivityResult(
      new ActivityResultContracts.TakePicture(),
      result -> {
        if (Boolean.TRUE.equals(result)) {
          Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
          if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            predecirTipoIncidencia(bitmap);
            obtenerUltimaUbicacion();
          }
        } else {
          Toast.makeText(getContext(), "No se tomó la foto", Toast.LENGTH_SHORT).show();
        }
      }
    );

    // Registrar launcher para permiso cámara
    requestCameraPermissionLauncher = registerForActivityResult(
      new ActivityResultContracts.RequestPermission(),
      isGranted -> {
        if (Boolean.TRUE.equals(isGranted)) {
          openCamera();
        } else {
          Toast.makeText(getContext(), getString(R.string.camera_permission_fail), Toast.LENGTH_SHORT).show();
        }
      }
    );

    // Registrar launcher para permiso ubicación COMO VARIABLE LOCAL
    ActivityResultLauncher<String> requestLocationPermissionLauncher = registerForActivityResult(
      new ActivityResultContracts.RequestPermission(),
      isGranted -> {
        if (Boolean.FALSE.equals(isGranted)) {
          Toast.makeText(getContext(), getString(R.string.permission_locattion_not_enable), Toast.LENGTH_SHORT).show();
        }
      }
    );

    // Pedir permiso ubicación si no está concedido
    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
      != PackageManager.PERMISSION_GRANTED) {
      requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    btnTakePhoto.setOnClickListener(v -> pushBtnTakePhoto());

    btnSendIncident.setOnClickListener(v -> pushBtnSendIncident());

    btnViewIncident.setOnClickListener(v -> pushBtnViewIncent());

    return root;
  }

  private void pushBtnTakePhoto() {
    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
      != PackageManager.PERMISSION_GRANTED) {
      requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
    } else {
      openCamera();
    }
  }

  private void pushBtnViewIncent() {
    NavController navController = NavHostFragment.findNavController(HomeFragment.this);
    navController.navigate(R.id.nav_Incidents);
  }

  private void pushBtnSendIncident() {
    String tipoIncidencia = imageViewTipoIncidencia.getText().toString();
    String localizacion = imageViewLocalizacion.getText().toString();
    String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    String status = "Pendiente";

    if (tipoIncidencia.isEmpty() || localizacion.isEmpty() || fecha.isEmpty() || currentPhotoPath == null || currentPhotoPath.isEmpty() || status.isEmpty()) {
      Toast.makeText(getContext(), "Todos los campos deben ser completos", Toast.LENGTH_SHORT).show();
      return;
    }
    saveIncidentToFirestore(tipoIncidencia, localizacion, currentPhotoPath, fecha, status);

    imageViewTipoIncidencia.setText("");
    imageViewLocalizacion.setText("");
    imageView.setImageResource(0);
    currentPhotoPath = null;
  }

  private void openCamera() {
    try {
      File photoFile = createImageFile();
      if (photoFile != null) {
        Uri photoURI = FileProvider.getUriForFile(
          requireContext(),
          "com.example.myapplication.fileprovider",
          photoFile
        );
        currentPhotoPath = photoFile.getAbsolutePath();
        takePictureLauncher.launch(photoURI);
      }
    } catch (IOException ex) {
      Toast.makeText(getContext(),
        Html.fromHtml("<font color='#FF0000'><b>" + getString(R.string.error_creating_image) + "</b></font>", Html.FROM_HTML_MODE_LEGACY),
        Toast.LENGTH_SHORT).show();
    }
  }

  private void obtenerUltimaUbicacion() {
    FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), location -> {
        if (location != null) {
          double lat = location.getLatitude();
          double lon = location.getLongitude();
          String coords = "Lat: " + lat + ", Lon: " + lon;
          imageViewLocalizacion.setText(coords);
        } else {
          imageViewLocalizacion.setText(getString(R.string.location_not_enabled));
        }
      }).addOnFailureListener(e ->
        imageViewLocalizacion.setText(getString(R.string.error_having_location))
      );
    } else {
      imageViewLocalizacion.setText(getString(R.string.permission_locattion_not_enable));
    }
  }

  private void predecirTipoIncidencia(Bitmap bitmap) {
    try {
      TFLiteModel tfliteModel = new TFLiteModel(getActivity().getAssets());
      Interpreter interpreter = tfliteModel.getInterpreter();

      Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
      float[][][][] input = preprocessBitmap(scaledBitmap);

      float[][] output = new float[1][5];
      interpreter.run(input, output);

      int clase = argMax(output[0]);
      String[] clases = {"Grieta", "Bache leve", "Bache medio", "Bache grave", "Poste caído"};
      String tipoIncidencia = clases[clase];

      imageViewTipoIncidencia.setText(tipoIncidencia);

    } catch (Exception e) {
      e.printStackTrace();
      imageViewTipoIncidencia.setText("Error al predecir");
    }
  }

  private float[][][][] preprocessBitmap(Bitmap bitmap) {
    float[][][][] input = new float[1][224][224][3];
    for (int y = 0; y < 224; y++) {
      for (int x = 0; x < 224; x++) {
        int px = bitmap.getPixel(x, y);
        input[0][y][x][0] = ((px >> 16 & 0xFF) / 127.5f) - 1.0f;
        input[0][y][x][1] = ((px >> 8 & 0xFF) / 127.5f) - 1.0f;
        input[0][y][x][2] = ((px & 0xFF) / 127.5f) - 1.0f;
      }
    }
    return input;
  }

  private int argMax(float[] array) {
    int maxIndex = 0;
    float max = array[0];
    for (int i = 1; i < array.length; i++) {
      if (array[i] > max) {
        max = array[i];
        maxIndex = i;
      }
    }
    return maxIndex;
  }

  private File createImageFile() throws IOException {
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    String imageFileName = "JPEG_" + timeStamp + "_";

    File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    if (storageDir != null && !storageDir.exists()) {
      storageDir.mkdirs();
    }
    File image = File.createTempFile(
      imageFileName,
      ".jpg",
      storageDir
    );

    currentPhotoPath = image.getAbsolutePath();
    return image;
  }

  private void saveIncidentToFirestore(String tipoIncidencia, String localizacion, String imageUrl, String fecha, String status) {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    if (user != null) {
      FirebaseFirestore db = FirebaseFirestore.getInstance();

      Map<String, Object> incidentData = new HashMap<>();
      incidentData.put("usuario_id", user.getUid());
      incidentData.put("tipo_incidencia", tipoIncidencia);
      incidentData.put("localizacion", localizacion);
      incidentData.put("foto", imageUrl);
      incidentData.put("fecha", fecha);
      incidentData.put("status", status);

      db.collection("incidencias")
        .add(incidentData)
        .addOnSuccessListener(documentReference -> {
          String incidentId = documentReference.getId();

          incidentData.put("uid", incidentId);

          documentReference.update("uid", incidentId)
            .addOnSuccessListener(aVoid ->
              Toast.makeText(getContext(), "Incidencia registrada correctamente con UID", Toast.LENGTH_SHORT).show()
            )
            .addOnFailureListener(e ->
              Toast.makeText(getContext(), "Error al actualizar el UID en la incidencia", Toast.LENGTH_SHORT).show()
            );
        })
        .addOnFailureListener(e ->
          Toast.makeText(getContext(), "Error al registrar incidencia", Toast.LENGTH_SHORT).show()
        );
    }
  }
}
