package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Bundle;
import android.util.Log;

import com.example.myapplication.database.DBManager;

import java.util.ArrayList;

public class IncidentActivity extends ActionBar {

  private RecyclerView recyclerViewIncidencia;
  private ArrayList<Incident> incidents;
  private DBManager dbManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_incident);

    setCustomActionBar(getString(R.string.title_activity_listIncident));


    recyclerViewIncidencia = findViewById(R.id.recyclerViewIncidencias);
    dbManager = new DBManager(this);

    incidents = new ArrayList<>(dbManager.obtenerIncidencias());

    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    recyclerViewIncidencia.setLayoutManager(layoutManager);
    IncidentAdapter incidentAdapter = new IncidentAdapter(this, incidents);
    recyclerViewIncidencia.setAdapter(incidentAdapter);
  }
}
