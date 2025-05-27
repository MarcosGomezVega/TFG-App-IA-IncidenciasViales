package com.example.myapplication.ui.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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


public class MapFragment extends Fragment {
  private MapView map;

  public static class Incidencia {
    String titulo;
    double lat;
    double lon;

    public Incidencia(String titulo, double lat, double lon) {
      this.titulo = titulo;
      this.lat = lat;
      this.lon = lon;
    }
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_map, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view,
                            @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    Configuration.getInstance().load(
      requireContext(),
      PreferenceManager.getDefaultSharedPreferences(requireContext())
    );


    map = view.findViewById(R.id.map_fragment);
    map.setTileSource(TileSourceFactory.MAPNIK);
    map.setMultiTouchControls(true);

    map.getController().setZoom(6.0);

    firstLocalitation();

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    if (user != null) {
      FirebaseFirestore db = FirebaseFirestore.getInstance();
      db.collection("incidents")
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

            for (Incident inc : incidents) {
              double[] coords = parseLatLon(inc.getLocalitation());
              if (coords != null) {
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
        });
    }
  }

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
      return null;
    }

  }

  private void firstLocalitation(){
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
