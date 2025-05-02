package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class IncidentActivity extends AppCompatActivity {

  private RecyclerView recyclerViewIncidencia;
  private ArrayList<Incident> incidents;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_incident);

    recyclerViewIncidencia = findViewById(R.id.recyclerViewIncidencias);

    incidents = new ArrayList<>();
    incidents.add(new Incident("Asfalto roto", -1)); // Ejemplo de incidente

    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    recyclerViewIncidencia.setLayoutManager(layoutManager);

    IncidentAdapter incidentAdapter = new IncidentAdapter(this,incidents);
    recyclerViewIncidencia.setAdapter(incidentAdapter);
  }
}
