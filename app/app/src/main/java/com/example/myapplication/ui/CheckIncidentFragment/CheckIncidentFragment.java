package com.example.myapplication.ui.CheckIncidentFragment;

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

import com.example.myapplication.Incident;
import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;

public class CheckIncidentFragment extends Fragment {

  private TextView txtType_incident;
  private ImageView imgView;
  private TextView txtLocalitation;
  private TextView txtDate;
  private TextView txtStatus;
  private String imageUrl;
  private String id_incident;
  private FirebaseFirestore db;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
                           ViewGroup container, Bundle savedInstanceState) {

    View root = inflater.inflate(R.layout.fragment_checkincidents, container, false);

    txtType_incident = root.findViewById(R.id.txt_TypeIncident);
    imgView = root.findViewById(R.id.imagePreview);
    txtLocalitation = root.findViewById(R.id.textLocation);
    txtDate = root.findViewById(R.id.textDate);
    txtStatus = root.findViewById(R.id.textStatus);

    Bundle args = getArguments();
    if (args != null) {
      id_incident = args.getString("incident_id");
    }

    db = FirebaseFirestore.getInstance();
    db.collection("incidencias").document(id_incident).get()
      .addOnSuccessListener(documentSnapshot -> {
        if (documentSnapshot.exists()) {
          Incident incident = documentSnapshot.toObject(Incident.class);

          if (incident != null) {

            txtType_incident.setText(incident.getTipoIncidencia());
            Log.d("FIREBASE", "Tipo de Incidencia: " + incident.getTipoIncidencia() );
            imageUrl = incident.getFoto();

            File imgFile = new File(imageUrl);
            if (imgFile.exists()) {
              Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
              imgView.setImageBitmap(bitmap);
            }


            txtLocalitation.setText(incident.getLocalizacion());
            txtDate.setText(incident.getFecha());

            String estado = incident.getStatus();
            switch (estado.toLowerCase()) {
              case "pendiente":
                txtStatus.setText(getString(R.string.waiting));
                txtStatus.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red));
                txtStatus.setTextColor(Color.BLACK);
                break;
              case "en proceso":
                txtStatus.setText(getString(R.string.in_proces));
                txtStatus.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.yellow));
                txtStatus.setTextColor(Color.BLACK);
                break;
              case "resuelta":
                txtStatus.setText(getString(R.string.result));
                txtStatus.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green));
                txtStatus.setTextColor(Color.BLACK);
                break;
              default:
                txtStatus.setText(estado);
                txtStatus.setBackgroundColor(Color.LTGRAY);
                txtStatus.setTextColor(Color.BLACK);
                break;
            }
          }
        } else {
          Log.e("FIREBASE", "El documento no existe");
        }
      })
      .addOnFailureListener(e -> Log.e("FIREBASE", "Error al obtener el documento", e));

    return root;
  }
}
