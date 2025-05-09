package com.example.myapplication.ui.Incidents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Incident;
import com.example.myapplication.R;
import com.example.myapplication.database.DBManager;
import com.example.myapplication.databinding.FragmentIncidentsBinding;

import java.util.ArrayList;

public class IncidentsFragment extends Fragment {


    private RecyclerView recyclerViewIncidencia;
    private ArrayList<Incident> incidents;
    private DBManager dbManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_incidents, container, false);

        recyclerViewIncidencia = root.findViewById(R.id.recyclerViewIncidencias);
        dbManager = new DBManager(getContext());

        incidents = new ArrayList<>(dbManager.obtenerIncidencias());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewIncidencia.setLayoutManager(layoutManager);

        NavController navController = NavHostFragment.findNavController(this);

        IncidentAdapter incidentAdapter = new IncidentAdapter(getContext(), incidents, navController);
        recyclerViewIncidencia.setAdapter(incidentAdapter);

        return root;
    }
}
