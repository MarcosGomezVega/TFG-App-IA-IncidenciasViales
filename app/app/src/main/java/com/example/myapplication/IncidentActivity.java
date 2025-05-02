package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Bundle;
import android.util.Log;

import com.example.myapplication.database.DBManager;

import java.util.ArrayList;

public class IncidentActivity extends AppCompatActivity {

  private RecyclerView recyclerViewIncidencia;
  private ArrayList<Incident> incidents;
  private DBManager dbManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_incident);
    Log.d("IncidentActivity", "onCreate started");

    recyclerViewIncidencia = findViewById(R.id.recyclerViewIncidencias);
    dbManager = new DBManager(this);

    try {
      incidents = new ArrayList<>(dbManager.obtenerIncidencias());
      Log.d("IncidentActivity", "Incidencias cargadas");
    } catch (Exception e) {
      Log.e("IncidentActivity", "Error al cargar incidencias", e);
    }

    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    recyclerViewIncidencia.setLayoutManager(layoutManager);
    IncidentAdapter incidentAdapter = new IncidentAdapter(this, incidents);
    recyclerViewIncidencia.setAdapter(incidentAdapter);
  }
}
