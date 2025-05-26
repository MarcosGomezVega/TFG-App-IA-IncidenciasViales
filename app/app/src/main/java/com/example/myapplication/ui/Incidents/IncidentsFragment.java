package com.example.myapplication.ui.incidents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Incident;
import com.example.myapplication.R;
import com.google.android.material.badge.BadgeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

/**
 * Fragmento encargado de mostrar la lista de incidencias del usuario actualmente autenticado.
 * Carga las incidencias desde Firestore filtradas por usuario, las ordena por fecha descendente
 * y las muestra en un RecyclerView.
 */
public class IncidentsFragment extends Fragment {

  private ArrayList<Incident> incidents = new ArrayList<>();
  private IncidentAdapter incidentAdapter;
  private boolean sortAscType = true;
  private boolean sortAscStatus = true;
  private Button btnStatus;
  private Button btnTypeIncident;

  /**
   * Método llamado para crear la vista del fragmento.
   * Infla el layout, configura el RecyclerView y obtiene las incidencias desde Firestore.
   * Una vez obtenidas, ordena las incidencias por fecha descendente (más recientes primero)
   * y las muestra usando un adaptador.
   *
   * @param inflater           Inflater para inflar el layout del fragmento
   * @param container          Contenedor padre de la vista
   * @param savedInstanceState Estado previo guardado
   * @return La vista raíz inflada del fragmento
   */
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
                           ViewGroup container, Bundle savedInstanceState) {

    View root = inflater.inflate(R.layout.fragment_incidents, container, false);

    RecyclerView recyclerViewIncidencia = root.findViewById(R.id.recyclerViewIncidencias);

    btnTypeIncident = root.findViewById(R.id.btnTypeIncident);
    btnStatus = root.findViewById(R.id.btnstatus);

    btnTypeIncident.setOnClickListener(v -> sortByType());
    btnStatus.setOnClickListener(v -> sortByStatus());

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    if (user != null) {
      FirebaseFirestore db = FirebaseFirestore.getInstance();
      db.collection("incidents")
        .whereEqualTo("user_id", user.getUid())
        .get()
        .addOnCompleteListener(task -> {
          if (task.isSuccessful()) {
            QuerySnapshot querySnapshot = task.getResult();
            if (querySnapshot != null) {

              incidents.clear();

              for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                Incident incident = document.toObject(Incident.class);
                incidents.add(incident);
              }
              shortIncidents(incidents);
              LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
              recyclerViewIncidencia.setLayoutManager(layoutManager);

              NavController navController = NavHostFragment.findNavController(this);
              incidentAdapter = new IncidentAdapter(getContext(), incidents, navController);
              recyclerViewIncidencia.setAdapter(incidentAdapter);
            }
          }
        });
    }
    return root;
  }
  /**
   * Ordena la lista de incidencias en memoria por la fecha en orden descendente (más reciente primero).
   * La fecha se interpreta a partir de un String con formato "yyyy-MM-dd HH:mm:ss".
   *
   * @param incidents Lista de incidencias a ordenar
   */
  private void shortIncidents( ArrayList<Incident> incidents){
    Collections.sort(incidents, (i1, i2) -> {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
      try {
        Date d1 = sdf.parse(i1.getDate());
        Date d2 = sdf.parse(i2.getDate());
        return d2.compareTo(d1);
      } catch (ParseException e) {
        e.printStackTrace();
        return 0;
      }
    });
  }
  private void sortByType() {
    Collections.sort(incidents, (i1, i2) -> {
      if (sortAscType) {
        return i1.getIncidentType().compareToIgnoreCase(i2.getIncidentType());
      } else {
        return i2.getIncidentType().compareToIgnoreCase(i1.getIncidentType());
      }
    });
    sortAscType = !sortAscType;
    incidentAdapter.notifyDataSetChanged();

    String arrow = sortAscType ? "↓":"↑";
    btnTypeIncident.setText(getString(R.string.type_incident) + arrow);
  }

  private void sortByStatus() {
    Collections.sort(incidents, (i1, i2) -> {
      if (sortAscStatus) {
        return i1.getStatus().compareToIgnoreCase(i2.getStatus());
      } else {
        return i2.getStatus().compareToIgnoreCase(i1.getStatus());
      }
    });
    sortAscStatus = !sortAscStatus;
    incidentAdapter.notifyDataSetChanged();

    String arrow = sortAscType ?  "↓":"↑";
    btnStatus.setText(getString(R.string.status_incident_list) + arrow);
  }

}
