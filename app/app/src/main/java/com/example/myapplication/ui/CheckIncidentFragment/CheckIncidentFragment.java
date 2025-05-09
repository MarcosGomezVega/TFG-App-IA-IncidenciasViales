package com.example.myapplication.ui.CheckIncidentFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;



import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.database.DBManager;
import com.example.myapplication.IncidentStatus;

import java.io.File;

public class CheckIncidentFragment extends Fragment {


    private ImageView imgView;
    private TextView txtLocalitation;
    private TextView txtDate;
    private TextView txtStatus;
    private DBManager dbManager;
    private IncidentStatus incidentSatatus;
    private String imageUrl;
    private int id_incident;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_checkincidents, container, false);

        imgView = root.findViewById(R.id.imagePreview);
        txtLocalitation = root.findViewById(R.id.textLocation);
        txtDate = root.findViewById(R.id.textDate);
        txtStatus = root.findViewById(R.id.textStatus);

        //Pasar ID de la inicidencia
        dbManager = new DBManager(getContext());

        Bundle args = getArguments();
        if (args != null) {
            id_incident = args.getInt("ID_incident", -1);
        }
        else {
            Log.e("CheckIncidentFragment", "No se recibió ningún argumento");
        }

        incidentSatatus = dbManager.obtenerIncidencias(id_incident);

        getActivity().setTitle(incidentSatatus.getIncident_type());

        imageUrl = incidentSatatus.getPhoto();
        File imgFile = new File(imageUrl);
        if (imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imgView.setImageBitmap(bitmap);
        }
        txtLocalitation.setText(incidentSatatus.getLocalitation());
        txtDate.setText(incidentSatatus.getDate());

        String estado = incidentSatatus.getStatus();
        txtStatus.setText(estado);

        switch (estado.toLowerCase()) {
            case "Pendiente":
                txtStatus.setBackgroundColor(Color.parseColor("#FFCDD2"));
                txtStatus.setTextColor(Color.BLACK);
                break;
            case "En proceso":
                txtStatus.setBackgroundColor(Color.parseColor("#FFF9C4"));
                txtStatus.setTextColor(Color.BLACK);
                break;
            case "Resuelta":
                txtStatus.setBackgroundColor(Color.parseColor("#C8E6C9"));
                txtStatus.setTextColor(Color.BLACK);
                break;
            default:
                txtStatus.setBackgroundColor(Color.LTGRAY);
                break;
        }

        return root;
    }
}