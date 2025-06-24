package com.example.myapplication.ui.map;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
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
import org.osmdroid.events.MapAdapter;
import org.osmdroid.events.ZoomEvent;
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
  private MapView map;
  private static final String TAG = "MapFragment";
  private FirebaseUser currentUser;

  /**
   * Vista del fragmento inflada.
   */
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_map, container, false);
  }

  /**
   * Configura mapa, ubicación inicial y carga marcadores según zoom.
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

    currentUser = FirebaseAuth.getInstance().getCurrentUser();

    firstLocalitation();

    map.addMapListener(new MapAdapter() {
      @Override
      public boolean onZoom(final ZoomEvent event) {
        double currentZoom = map.getZoomLevelDouble();
        updateMarkersAccordingToZoom(currentZoom);
        return true;
      }
    });

    updateMarkersAccordingToZoom(map.getZoomLevelDouble());
  }

  /**
   * Decide qué marcadores cargar según el nivel de zoom.
   * @param zoom Nivel de zoom actual del mapa.
   */
  private void updateMarkersAccordingToZoom(double zoom) {
    map.getOverlays().clear();

    if (currentUser == null) {
      Log.d(TAG, "Usuario no autenticado, no se cargan marcadores.");
      return;
    }
    if (zoom >= 15.0) {
      loadUserIncidentsAndAddMarkers();
      loadOtherUsersIncidentsAndAddMarkers();
    } else {
      loadUserIncidentsAndAddMarkers();
    }
    map.invalidate();
  }

  /**
   * Carga las incidencias del usuario actual y añade marcadores al mapa.
   */
  private void loadUserIncidentsAndAddMarkers() {
    if (currentUser == null) return;

    FirebaseFirestore.getInstance()
      .collection("incidents")
      .whereEqualTo("user_id", currentUser.getUid())
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
          addMarkers(incidents, true);
        }
      });
  }

  /**
   * Carga las incidencias de otros usuarios y añade marcadores con sus avatares.
   */
  private void loadOtherUsersIncidentsAndAddMarkers() {
    if (currentUser == null) return;

    FirebaseFirestore.getInstance()
      .collection("incidents")
      .whereNotEqualTo("user_id", currentUser.getUid())
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
          addMarkers(incidents, false);
        }
      });
  }

  /**
   * Añade marcadores al mapa para cada incidente.
   * @param incidents Lista de incidentes.
   * @param isCurrentUser Si los incidentes son del usuario actual.
   */
  private void addMarkers(List<Incident> incidents, boolean isCurrentUser) {
    for (Incident inc : incidents) {
      double[] coords = parseLatLon(inc.getLocalitation());
      if (coords.length == 2) {
        GeoPoint point = new GeoPoint(coords[0], coords[1]);

        if (isCurrentUser) {
          Marker marker = new Marker(map);
          marker.setPosition(point);
          marker.setTitle(inc.getIncidentType());
          marker.setIcon(getMarkerIconByStatus(inc.getStatus()));
          marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
          marker.setRelatedObject(inc);

          marker.setOnMarkerClickListener((m, mapView) -> {
            Bundle bundle = new Bundle();
            bundle.putString("incident_id", inc.getUid());
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.nav_checkIncident, bundle);
            return true;
          });

          map.getOverlays().add(marker);
        } else {
          FirebaseFirestore.getInstance().collection("users")
            .document(inc.getUserId())
            .get()
            .addOnSuccessListener(userDoc -> {
              if (userDoc.exists()) {
                String avatarUrl = userDoc.getString("avatar");
                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                  setAvatarMarker(avatarUrl, point, inc);
                }
              }
            });
        }
      }
    }
    map.invalidate();
  }

  /**
   * Descarga y aplica el avatar circular como icono del marcador.
   */
  private void setAvatarMarker(String avatarUrl, GeoPoint position, Incident incident) {
    Glide.with(requireContext())
      .asBitmap()
      .load(avatarUrl)
      .circleCrop()
      .into(new CustomTarget<Bitmap>(100, 100) {
        @Override
        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
          Marker marker = new Marker(map);
          marker.setPosition(position);
          marker.setIcon(new BitmapDrawable(getResources(), resource));
          marker.setTitle(incident.getIncidentType());
          marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
          marker.setRelatedObject(incident);

          marker.setOnMarkerClickListener((m, mapView) -> {
            Bundle bundle = new Bundle();
            bundle.putString("incident_id", incident.getUid());
            NavController navController = NavHostFragment.findNavController(MapFragment.this);
            navController.navigate(R.id.nav_checkIncident, bundle);
            return true;
          });

          map.getOverlays().add(marker);
          map.invalidate();
        }

        @Override
        public void onLoadCleared(@Nullable Drawable placeholder) {}
      });
  }

  /**
   * Parsea una cadena "lat: <val>, lon: <val>" y devuelve un array [lat, lon].
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
   * Centra el mapa en la ubicación inicial del usuario si se permiten permisos.
   */
  private void firstLocalitation() {
    FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
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
   * Devuelve un icono diferente según el estado del incidente.
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
