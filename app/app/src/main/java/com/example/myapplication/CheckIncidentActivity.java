package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import com.example.myapplication.database.DBManager;

import java.io.File;
import java.util.Arrays;

public class CheckIncidentActivity extends AppCompatActivity {

  private TextView txtTypeIncident;
  private ImageView imgView;
  private TextView txtLocalication;
  private TextView txtDate;
  private TextView txtStatus;
  private DBManager dbManager;
  private IncidentStatus incidentSatatus;

  private String imageUrl;

  int id_incident;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ckeck_incident);
    Log.d("AdapterStatusssss", "Activity Loaded");

    txtTypeIncident = findViewById(R.id.txtTypeIncident);
    imgView = findViewById(R.id.imagePreview);
    txtLocalication = findViewById(R.id.textLocation);
    txtDate = findViewById(R.id.textDate);
    txtStatus = findViewById(R.id.textStatus);

    dbManager = new DBManager(CheckIncidentActivity.this);
    id_incident = getIntent().getIntExtra("ID_incident", -1);

    incidentSatatus=dbManager.obtenerIncidencias(id_incident);

    txtTypeIncident.setText(incidentSatatus.getIncident_type());

    imageUrl = incidentSatatus.getPhoto();
    File imgFile = new File(imageUrl);
    if (imgFile.exists()) {
      Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
      imgView.setImageBitmap(bitmap);
    }

    txtLocalication.setText(incidentSatatus.getLocalitation());
    txtDate.setText(incidentSatatus.getDate());
    String estado = incidentSatatus.getStatus();
    txtStatus.setText(estado);


    switch (estado.toLowerCase()) {
      case "pendiente":
        txtStatus.setBackgroundColor(Color.parseColor("#FFCDD2"));
        break;
      case "en proceso":
        txtStatus.setBackgroundColor(Color.parseColor("#FFF9C4"));
        break;
      case "resuelta":
        txtStatus.setBackgroundColor(Color.parseColor("#C8E6C9"));
        break;
      default:
        txtStatus.setBackgroundColor(Color.LTGRAY);
        break;
    }
  }
}
