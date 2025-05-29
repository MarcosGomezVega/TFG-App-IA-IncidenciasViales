package com.example.myapplication.ui.map;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.Incident;
import com.example.myapplication.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragmento que muestra un mapa con marcadores representando incidentes del usuario.
 * Utiliza OpenStreetMap (osmdroid) para renderizar el mapa y Firebase Firestore para obtener datos.
 */
public class MapFragment extends Fragment {
  /**
   * Vista del mapa donde se colocan los marcadores
   */
  private MapView map;

  /**
   * Inflar la vista del fragmento con el layout correspondiente.
   *
   * @param inflater           Inflater para inflar vistas XML.
   * @param container          Contenedor padre.
   * @param savedInstanceState Estado previo guardado.
   * @return Vista inflada para el fragmento.
   */
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_map, container, false);
  }

  /**
   * Método llamado después de crear la vista. Aquí se configura el mapa, se carga la ubicación inicial
   * y se cargan los incidentes del usuario para mostrar marcadores en el mapa.
   *
   * @param view               Vista creada.
   * @param savedInstanceState Estado previo guardado.
   */
  @Override
  public void onViewCreated(@NonNull View view,
                            @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    SharedPreferences prefs = requireContext().getSharedPreferences("my_app_preferences", Context.MODE_PRIVATE);
    Configuration.getInstance().load(requireContext(), prefs);

    map = view.findViewById(R.id.map_fragment);
    map.setTileSource(TileSourceFactory.MAPNIK);
    map.setMultiTouchControls(true);
    map.getController().setZoom(6.0);
    firstLocalitation();
    loadUserIncidentsAndAddMarkers();
  }


  /**
   * Carga los incidentes asociados al usuario actual desde Firebase Firestore
   * y añade marcadores en el mapa para cada incidencia.
   */
  private void loadUserIncidentsAndAddMarkers() {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    if (user == null) return;

    FirebaseFirestore.getInstance()
      .collection("incidents")
      .whereEqualTo("user_id", user.getUid())
      .get()
      .addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
          List<Incident> incidents = new ArrayList<>();
          QuerySnapshot querySnapshot = task.getResult();
          if (querySnapshot != null) {
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
              Incident incident = document.toObject(Incident.class);
              incidents.add(incident);
            }
          }
          addMarkers(incidents);
        }
      });
  }

  /**
   * Añade marcadores al mapa para cada incidente recibido.
   * Configura posición, título, icono, y listener de click para cada marcador.
   *
   * @param incidents Lista de incidentes para mostrar en el mapa.
   */
  private void addMarkers(List<Incident> incidents) {
    for (Incident inc : incidents) {
      double[] coords = parseLatLon(inc.getLocalitation());
      if (coords.length == 2) {
        Marker marker = new Marker(map);
        marker.setPosition(new GeoPoint(coords[0], coords[1]));
        marker.setTitle(inc.getIncidentType());
        marker.setIcon(getMarkerIconByStatus(inc.getStatus()));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setRelatedObject(inc);

        marker.setOnMarkerClickListener((m, mapView) -> {
          Bundle bundle = new Bundle();
          Incident incident = (Incident) m.getRelatedObject();
          bundle.putString("incident_id", incident.getUid());

          NavController navController = NavHostFragment.findNavController(this);
          navController.navigate(R.id.nav_checkIncident, bundle);

          return true;
        });
        map.getOverlays().add(marker);
      }
    }
    map.invalidate();
  }

  /**
   * Parsea una cadena de texto con formato "lat: <valor>, lon: <valor>"
   * y devuelve un array con la latitud y longitud como double.
   *
   * @param localitation Cadena con la ubicación en formato esperado.
   * @return Array de dos posiciones: [latitud, longitud], o null si falla el parseo.
   */
  private double[] parseLatLon(String localitation) {

    try {
      String[] parts = localitation.split(",");
      String latPart = parts[0].trim();
      String lonPart = parts[1].trim();

      double lat = Double.parseDouble(latPart.split(":")[1].trim());
      double lon = Double.parseDouble(lonPart.split(":")[1].trim());

      return new double[]{lat, lon};
    } catch (Exception e) {
      e.printStackTrace();
      return new double[0];
    }

  }

  /**
   * Obtiene la ubicación inicial del usuario usando FusedLocationProviderClient.
   * Si no se dispone de permiso o ubicación, centra el mapa en una ubicación por defecto.
   */
  private void firstLocalitation() {
    FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      fusedLocationClient.getLastLocation()
        .addOnSuccessListener(location -> {
          if (location != null) {
            map.getController().setZoom(15.0);
            map.getController().setCenter(new GeoPoint(location.getLatitude(), location.getLongitude()));
          } else {
            map.getController().setZoom(10.0);
            map.getController().setCenter(new GeoPoint(40.4168, -3.7038));
          }
        });
    } else {
      map.getController().setZoom(10.0);
      map.getController().setCenter(new GeoPoint(40.4168, -3.7038));
    }
  }

  /**
   * Devuelve un icono Drawable para el marcador según el estado del incidente.
   *
   * @param status Estado del incidente (ej. "pendiente", "en proceso", "resuelta").
   * @return Drawable con el icono correspondiente.
   */
  private Drawable getMarkerIconByStatus(String status) {
    switch (status.toLowerCase()) {
      case "pendiente":
        return ContextCompat.getDrawable(requireContext(), R.drawable.warming_map);
      case "en proceso":
        return ContextCompat.getDrawable(requireContext(), R.drawable.warming_yelow);
      case "resuelta":
        return ContextCompat.getDrawable(requireContext(), R.drawable.warming_green);
      default:
        return ContextCompat.getDrawable(requireContext(), R.drawable.warning);
    }
  }
}
