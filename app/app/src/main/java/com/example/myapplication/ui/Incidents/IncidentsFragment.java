package com.example.myapplication.ui.incidents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Incident;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class IncidentsFragment extends Fragment {

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
                           ViewGroup container, Bundle savedInstanceState) {

    View root = inflater.inflate(R.layout.fragment_incidents, container, false);

    RecyclerView recyclerViewIncidencia = root.findViewById(R.id.recyclerViewIncidencias);
    ArrayList<Incident> incidents = new ArrayList<>();

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    if (user != null) {
      FirebaseFirestore db = FirebaseFirestore.getInstance();
      db.collection("incidencias")
              .whereEqualTo("usuario_id", user.getUid())
              .get()
              .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                  QuerySnapshot querySnapshot = task.getResult();
                  if (querySnapshot != null) {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                      Incident incident = document.toObject(Incident.class);
                      incidents.add(incident);
                    }
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                    recyclerViewIncidencia.setLayoutManager(layoutManager);

                    NavController navController = NavHostFragment.findNavController(this);
                    IncidentAdapter incidentAdapter = new IncidentAdapter(getContext(), incidents, navController);
                    recyclerViewIncidencia.setAdapter(incidentAdapter);
                  }
                } else {
                  Toast.makeText(getContext(), "Error al cargar las incidencias", Toast.LENGTH_SHORT).show();
                }
              });
    }

    return root;
  }
}
