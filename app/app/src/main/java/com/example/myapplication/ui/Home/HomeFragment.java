package com.example.myapplication.ui.home;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.Environment;
import android.Manifest;

import androidx.core.content.FileProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import org.tensorflow.lite.Interpreter;

/**
 * Fragmento principal de la aplicación que permite:
 * - Tomar una foto de una incidencia vial.
 * - Clasificar la imagen con un modelo de IA (TensorFlow Lite).
 * - Permitir la selección manual del tipo de incidencia si la predicción es poco confiable.
 * - Obtener la ubicación actual del dispositivo.
 * - Enviar la incidencia a Firebase Firestore.
 * - Navegar a la vista de incidencias registradas.
 */
public class HomeFragment extends Fragment {

  /**
   * Vista para mostrar la imagen capturada
   */
  private ImageView imageView;

  /**
   * Texto para mostrar el tipo de incidencia detectada
   */
  private TextView imageViewIncidentType;

  /**
   * Texto para mostrar la ubicación detectada
   */
  private TextView imageViewLocalizacion;

  /**
   * Ruta del archivo de la imagen capturada
   */
  private String currentPhotoPath;

  /**
   * Selector para escoger manualmente el tipo de incidencia
   */
  private Spinner spinnerIncidentType;

  /**
   * Lanzador para tomar una foto
   */
  private ActivityResultLauncher<Uri> takePictureLauncher;

  /**
   * Lanzador para pedir permiso de la cámara
   */
  private ActivityResultLauncher<String> requestCameraPermissionLauncher;

  /**
   * Porcentaje de confianza de la predicción del modelo
   */
  private int incidentPercentage;

  /**
   * Tipo de incidencia seleccionado o detectado
   */
  private String incidentType;

  /**
   * Crea la vista del fragmento e inicializa los elementos de la interfaz y los listeners de botones.
   *
   * @param inflater           El objeto LayoutInflater para inflar vistas en el fragmento.
   * @param container          El contenedor donde se inflará la vista.
   * @param savedInstanceState Datos del estado previamente guardado, si existen.
   * @return La vista raíz del fragmento inflada.
   */
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
                           ViewGroup container, Bundle savedInstanceState) {

    View root = inflater.inflate(R.layout.fragment_home, container, false);

    requestPermissionsAtStartup();

    Button btnTakePhoto = root.findViewById(R.id.btnTakePhoto);
    Button btnSendIncident = root.findViewById(R.id.btnSendIncident);
    Button btnViewIncident = root.findViewById(R.id.btnViewIncidences);
    imageView = root.findViewById(R.id.imagePreview);
    imageViewIncidentType = root.findViewById(R.id.textDetectedType);
    imageViewLocalizacion = root.findViewById(R.id.textLocation);
    spinnerIncidentType = root.findViewById(R.id.spinnerIncidentType);

    String[] clases = {"Selecione una incidencia ...", "Grieta", "Agujero", "Poste caído", "Sin incidencia"};

    setupSpinner(clases);
    setupActivityLauncher(clases);

    btnTakePhoto.setOnClickListener(v -> pushBtnTakePhoto());
    btnSendIncident.setOnClickListener(v -> pushBtnSendIncident());
    btnViewIncident.setOnClickListener(v -> pushBtnViewIncent());

    return root;
  }

  /**
   * Configura el Spinner que permite seleccionar manualmente un tipo de incidencia.
   *
   * @param clases Array de strings que representan las clases de incidencias posibles.
   */
  private void setupSpinner(String[] clases) {


    ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(),
      android.R.layout.simple_spinner_dropdown_item, clases) {
      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = new TextView(getContext());
        view.setText("");
        return view;
      }
    };
    spinnerIncidentType.setAdapter(adapter);
    spinnerIncidentType.setVisibility(View.GONE);


  }

  /**
   * Inicializa los lanzadores de actividades y permisos para tomar fotos y obtener la localización.
   *
   * @param clases Array de strings con los tipos de incidencias para mostrar en caso de baja confianza.
   */
  private void setupActivityLauncher(String[] clases) {
    takePictureLauncher = registerForActivityResult(
      new ActivityResultContracts.TakePicture(),
      result -> {
        if (Boolean.TRUE.equals(result)) {
          Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
          if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            incidentPercentage = predictIncidentType(bitmap);
            if (incidentPercentage < 95) {
              spinner(clases);
            }
            getLastLocation();
          }
        }
      }
    );
  }

  private void requestPermissionsAtStartup() {
    ActivityResultLauncher<String[]> multiplePermissionLauncher =
      registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
        Boolean cameraGranted = result.getOrDefault(Manifest.permission.CAMERA, false);
        Boolean locationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);

        if (Boolean.FALSE.equals(cameraGranted)) {
          Toast.makeText(getContext(), getString(R.string.camera_permission_fail), Toast.LENGTH_SHORT).show();
        }

        if (Boolean.FALSE.equals(locationGranted)) {
          Toast.makeText(getContext(), getString(R.string.permission_locattion_not_enable), Toast.LENGTH_SHORT).show();
        }
      });

    multiplePermissionLauncher.launch(new String[]{
      Manifest.permission.CAMERA,
      Manifest.permission.ACCESS_FINE_LOCATION
    });
  }


  /**
   * Muestra el Spinner para seleccionar manualmente un tipo de incidencia cuando la predicción no tiene suficiente confianza.
   *
   * @param clases Array de strings con los tipos de incidencias.
   */
  private void spinner(String[] clases) {
    spinnerIncidentType.setVisibility(View.VISIBLE);
    spinnerIncidentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

      boolean first = true;

      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (first) {
          first = false;
          return;
        }
        String select = clases[position];
        incidentPercentage = 100;
        imageViewIncidentType.setText(select);
        incidentType = select;
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
        // N/A
      }
    });
  }

  /**
   * Maneja el evento de pulsar el botón de tomar foto. Solicita permiso si es necesario y abre la cámara.
   */
  private void pushBtnTakePhoto() {
    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
      == PackageManager.PERMISSION_GRANTED) {
      openCamera();
    } else {
      Toast.makeText(getContext(), getString(R.string.camera_permission_fail), Toast.LENGTH_SHORT).show();
    }
  }

  /**
   * Navega hacia el fragmento de visualización de incidencias registradas.
   */
  private void pushBtnViewIncent() {
    NavController navController = NavHostFragment.findNavController(HomeFragment.this);
    navController.navigate(R.id.nav_Incidents);
  }

  /**
   * Envía la información de la incidencia a Firestore si todos los campos requeridos están completos.
   * También limpia la vista para el siguiente registro.
   */
  private void pushBtnSendIncident() {
    String localitation = imageViewLocalizacion.getText().toString();
    String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    String status = "Pendiente";

    if (incidentType == null || incidentType.isEmpty() || localitation.isEmpty() || currentPhotoPath == null || currentPhotoPath.isEmpty()) {
      Toast.makeText(getContext(), getString(R.string.gaps_empty), Toast.LENGTH_SHORT).show();
      return;
    }

    /**
     *
     *
     if (incidentType.equals("Sin incidencia")) {
     Toast.makeText(getContext(), getString(R.string.error_send_null_incident), Toast.LENGTH_SHORT).show();
     return;
     }

     enviarCorreo(localitation, date, incidentType);
     */
    uploadImageToFirebaseStorage(incidentType, localitation, date, status, incidentPercentage);

    spinnerIncidentType.setVisibility(View.GONE);

    imageViewIncidentType.setText("");
    imageViewIncidentType.setGravity(Gravity.CENTER);
    imageViewLocalizacion.setText("");
    imageView.setImageResource(0);
    currentPhotoPath = null;
  }
/**
 private void enviarCorreo(String localitation, String date, String incidentType) {

 String correoDestino = "marcosgomezvegaportillo@gmail.com";
 String asunto = "Incidencia Reportada: " + incidentType;
 String mensaje = "Detalles de la incidencia:\n" +
 "Tipo: " + incidentType + "\n" +
 "Localización: " + localitation + "\n" +
 "Fecha: " + date + "\n";

 Intent intent = new Intent(Intent.ACTION_SENDTO);
 intent.setData(Uri.parse("mailto:" + Uri.encode(correoDestino))); // ← añade destinatario en URI
 intent.putExtra(Intent.EXTRA_SUBJECT, asunto);
 intent.putExtra(Intent.EXTRA_TEXT, mensaje);

 try {
 startActivity(intent);
 } catch (ActivityNotFoundException e) {
 Toast.makeText(getContext(), "No hay app de correo instalada", Toast.LENGTH_SHORT).show();
 }
 }
 */

  /**
   * Abre la cámara del dispositivo para tomar una fotografía y guarda temporalmente la imagen capturada.
   */
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

  /**
   * Obtiene la última ubicación conocida del dispositivo y la muestra en la interfaz.
   */
  private void getLastLocation() {
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

  /**
   * Predice el tipo de incidencia utilizando un modelo TensorFlow Lite y muestra el resultado con el porcentaje de confianza.
   *
   * @param bitmap Imagen capturada para analizar.
   * @return Porcentaje de confianza de la predicción.
   */
  private int predictIncidentType(Bitmap bitmap) {
    int confidencePercentage = 0;

    try {
      TFLiteModel tfliteModel = new TFLiteModel(getActivity().getAssets());
      Interpreter interpreter = tfliteModel.getInterpreter();

      Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
      float[][][][] input = preprocessBitmap(scaledBitmap);

      float[][] output = new float[1][4];
      interpreter.run(input, output);

      int type = argMax(output[0]);
      float confidence = output[0][type];
      confidencePercentage = (int) (confidence * 100);

      String[] types = {"Grieta", "Agujero", "Poste caído", "Sin incidencia"};
      incidentType = types[type];

      String result = incidentType + " " + confidencePercentage + "%";
      imageViewIncidentType.setText(result);

      return confidencePercentage;

    } catch (Exception e) {
      e.printStackTrace();
      imageViewIncidentType.setText(getString(R.string.prediction_error));
    }

    return confidencePercentage;
  }

  /**
   * Preprocesa el bitmap para adaptarlo al formato requerido por el modelo TensorFlow Lite.
   *
   * @param bitmap Imagen escalada a 224x224 píxeles.
   * @return Arreglo 4D con los valores normalizados del bitmap.
   */
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

  /**
   * Devuelve el índice del valor máximo dentro de un arreglo de floats.
   *
   * @param array Arreglo de floats.
   * @return Índice del valor máximo.
   */
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

  /**
   * Crea un archivo temporal para almacenar una imagen capturada por la cámara.
   *
   * @return Archivo creado para la imagen.
   * @throws IOException Si ocurre un error al crear el archivo.
   */
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

  /**
   * Guarda la información de una incidencia en Firestore, incluyendo usuario, tipo, localización, foto, fecha y estado.
   *
   * @param incidentType       Tipo de incidencia detectado o seleccionado.
   * @param localitation       Coordenadas de la ubicación donde se detectó la incidencia.
   * @param imageUrl           Ruta local de la imagen capturada.
   * @param date               Fecha y hora del registro.
   * @param status             Estado inicial de la incidencia.
   * @param incidentPercentage Porcentaje de error (confianza) de la predicción automática.
   */
  private void saveIncidentToFirestore(String incidentType, String localitation, String imageUrl, String date, String status, int incidentPercentage) {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    if (user != null) {
      FirebaseFirestore db = FirebaseFirestore.getInstance();

      Map<String, Object> incidentData = new HashMap<>();
      incidentData.put("user_id", user.getUid());
      incidentData.put("incident_type", incidentType);
      incidentData.put("localitation", localitation);
      incidentData.put("photo", imageUrl);
      incidentData.put("date", date);
      incidentData.put("status", status);
      incidentData.put("error_percentage", incidentPercentage);

      db.collection("incidents")
        .add(incidentData)
        .addOnSuccessListener(documentReference -> {
          String incidentId = documentReference.getId();

          incidentData.put("uid", incidentId);

          documentReference.update("uid", incidentId)
            .addOnSuccessListener(aVoid ->
              Toast.makeText(getContext(), getString(R.string.incident_send_well), Toast.LENGTH_SHORT).show()
            )
            .addOnFailureListener(e ->
              Toast.makeText(getContext(), getString(R.string.error_updating_UID_incident), Toast.LENGTH_SHORT).show()
            );
        })
        .addOnFailureListener(e ->
          Toast.makeText(getContext(), getString(R.string.incident_send_bad), Toast.LENGTH_SHORT).show()
        );
    }
  }

  private void uploadImageToFirebaseStorage(String incidentType, String localitation, String date, String status, int incidentPercentage) {
    Uri fileUri = Uri.fromFile(new File(currentPhotoPath));
    String fileName = "images/incidents/" + fileUri.getLastPathSegment();

    StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(fileName);
    UploadTask uploadTask = storageRef.putFile(fileUri);

    uploadTask.addOnSuccessListener(taskSnapshot ->
      storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
        String imageUrl = uri.toString();
        saveIncidentToFirestore(incidentType, localitation, imageUrl, date, status, incidentPercentage);
      })
    ).addOnFailureListener(e ->
      Toast.makeText(getContext(), getString(R.string.error_upload_photo), Toast.LENGTH_SHORT).show()
    );
  }
}
