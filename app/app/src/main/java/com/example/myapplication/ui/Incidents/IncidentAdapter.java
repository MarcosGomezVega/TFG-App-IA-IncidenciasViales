package com.example.myapplication.ui.Incidents;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Incident;
import com.example.myapplication.R;

import java.util.List;

public class IncidentAdapter extends RecyclerView.Adapter<IncidentAdapter.IncidentViewHolder> {
  private Context context;
  private List<Incident> listaIncidencias;

  private NavController navController;


  public IncidentAdapter(Context context, List<Incident> listaIncidencias, NavController navController) {
    this.context = context;
    this.listaIncidencias = listaIncidencias;
    this.navController = navController;
  }

  @Override
  public IncidentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_incident, parent, false);
    return new IncidentViewHolder(view);
  }

  @Override
  public void onBindViewHolder(IncidentViewHolder holder, int position) {

    Incident incidencia = listaIncidencias.get(position);

    holder.txtId.setText(String.valueOf(incidencia.getId()));
    holder.txtTipo.setText(incidencia.getTipo());
    holder.btnVer.setOnClickListener(v -> {
      if (navController != null) {
        Bundle bundle = new Bundle();
        bundle.putInt("ID_incident", incidencia.getId());
        navController.navigate(R.id.nav_slideshow,bundle);
      } else {
        Log.e("IncidentAdapter", "NavController es null");
      }
    });
  }

  @Override
  public int getItemCount() {
    return listaIncidencias.size();
  }

  public static class IncidentViewHolder extends RecyclerView.ViewHolder {
    TextView txtTipo;
    Button btnVer;
    TextView txtId;

    public IncidentViewHolder(View itemView) {
      super(itemView);
      txtId = itemView.findViewById(R.id.txtId);
      txtTipo = itemView.findViewById(R.id.txtTipo);
      btnVer = itemView.findViewById(R.id.btnVer);
    }
  }
}
