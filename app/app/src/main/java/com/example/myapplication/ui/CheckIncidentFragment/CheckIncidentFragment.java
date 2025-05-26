package com.example.myapplication.ui.checkincidentfragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Incident;
import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
/**
 * Fragmento que muestra los detalles de una incidencia específica.
 * Obtiene la incidencia desde Firestore usando su ID, y muestra
 * tipo de incidencia, foto, localización, fecha y estado con color.
 */
public class CheckIncidentFragment extends Fragment {


  /** ID de la incidencia a consultar */
  private String idIncident;

  /** TextView que muestra el estado de la incidencia */
  private TextView txtStatus;

  /**
   * Crea la vista del fragmento, infla el layout, obtiene la incidencia
   * desde Firestore y actualiza la UI con los datos correspondientes.
   *
   * @param inflater           Inflater para inflar el layout
   * @param container          Contenedor padre de la vista
   * @param savedInstanceState Estado previo guardado
   * @return Vista raíz inflada del fragmento
   */
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
                           ViewGroup container, Bundle savedInstanceState) {

    View root = inflater.inflate(R.layout.fragment_checkincidents, container, false);

    TextView txtTypeIncident = root.findViewById(R.id.txt_TypeIncident);
    ImageView imgView = root.findViewById(R.id.imagePreview);
    TextView txtLocalitation = root.findViewById(R.id.textLocation);
    TextView txtDate = root.findViewById(R.id.textDate);
    txtStatus = root.findViewById(R.id.textStatus);

    Bundle args = getArguments();
    if (args != null) {
      idIncident = args.getString("incident_id");
    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    db.collection("incidents").document(idIncident).get()
      .addOnSuccessListener(documentSnapshot -> {
        if (documentSnapshot.exists()) {
          Incident incident = documentSnapshot.toObject(Incident.class);

          if (incident != null) {
            txtTypeIncident.setText(incident.getIncidentType());

            String imageUrl = incident.getPhoto();

            if (imageUrl != null && !imageUrl.isEmpty()) {
              Glide.with(requireContext())
                .load(imageUrl)
                .into(imgView);
            }

            txtLocalitation.setText(incident.getLocalitation());
            txtDate.setText(incident.getDate());

            String estado = incident.getStatus();
            changeColorStatus(estado);
          }
        }
      })
      .addOnFailureListener(e -> Log.e("FIREBASE", "Error al obtener el documento", e));

    return root;
  }


  /**
   * Cambia el texto y color del estado mostrado según el estado de la incidencia.
   * - "pendiente": texto en rojo
   * - "en proceso": texto en amarillo
   * - "resuelta": texto en verde
   * - Otros: texto en gris claro
   *
   * @param state Estado de la incidencia (String)
   */
  private void changeColorStatus(String state) {
    switch (state.toLowerCase()) {
      case "pendiente":
        txtStatus.setText(getString(R.string.waiting));
        txtStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        break;
      case "en proceso":
        txtStatus.setText(getString(R.string.in_proces));
        txtStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.yellow));
        break;
      case "resuelta":
        txtStatus.setText(getString(R.string.result));
        txtStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
        break;
      default:
        txtStatus.setText(state);
        txtStatus.setTextColor(Color.LTGRAY);
        break;
    }
  }
}
