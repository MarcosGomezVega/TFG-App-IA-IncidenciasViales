package com.example.myapplication.ui.Home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;
import com.example.myapplication.database.DBManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.content.Intent;
import android.provider.MediaStore;
import android.Manifest;
import androidx.core.content.FileProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import android.os.Environment;



public class HomeFragment extends Fragment {


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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        btnTakePhoto = root.findViewById(R.id.btnTakePhoto);
        btnSendIncident = root.findViewById(R.id.btnSendIncident);
        btnViewIncident = root.findViewById(R.id.btnViewIncidences);
        imageView = root.findViewById(R.id.imagePreview);
        imageViewTipoIncidencia = root.findViewById(R.id.textDetectedType);
        imageViewLocalizacion = root.findViewById(R.id.textLocation);

        dbManager = new DBManager(getContext());

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }

        btnTakePhoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            } else {
                openCamera();
            }
        });

        btnSendIncident.setOnClickListener(v -> {
            String tipoIncidencia = imageViewTipoIncidencia.getText().toString();
            String localizacion = imageViewLocalizacion.getText().toString();
            String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            int usuarioId = dbManager.obtenerIdUltimoUsuario();
            String status = "Pendiente";

            boolean estadoInsertado = dbManager.insertarIncidencia(usuarioId, tipoIncidencia, localizacion, currentPhotoPath, fecha, status);
            if (estadoInsertado) {
                Toast.makeText(getContext(), "Incidencia registrada correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Error al registrar el estado", Toast.LENGTH_SHORT).show();
            }

            imageViewTipoIncidencia.setText("");
            imageViewLocalizacion.setText("");
            imageView.setImageResource(0);
            currentBitmap = null;
        });

        btnViewIncident.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(HomeFragment.this);
            navController.navigate(R.id.nav_gallery);
        });

        return root;
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            File photoFile;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(getContext(), Html.fromHtml("<font color='#FF0000'><b>" + getString(R.string.error_creating_image) + "</b></font>"), Toast.LENGTH_SHORT).show();
                return;
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(
                        getContext(),
                        "com.example.myapplication.fileprovider",
                        photoFile
                );
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(getContext(), getString(R.string.camera_permission_fail), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
        imageView.setImageBitmap(bitmap);
        currentBitmap = bitmap;


        imageViewTipoIncidencia.setText("Asfalto roto");

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), location -> {
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

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir != null && !storageDir.exists()) {
            storageDir.mkdirs();
        }
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