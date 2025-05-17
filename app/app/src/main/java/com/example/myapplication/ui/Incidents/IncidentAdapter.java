package com.example.myapplication.ui.incidents;

import android.content.Context;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
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

    holder.txtTypeIncident.setText(String.valueOf(incidencia.getTipoIncidencia()));
    holder.btnView.setOnClickListener(v -> pushBtnViewIncient(incidencia));
    String estado = incidencia.getStatus().toLowerCase();

    switch (estado) {
      case "pendiente":
        holder.txtStatus.setText(incidencia.getStatus());
        holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.red));
        break;
      case "en proceso":
        holder.txtStatus.setText(incidencia.getStatus());
        holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.yellow));
        break;
      case "resuelta":
        holder.txtStatus.setText(incidencia.getStatus());
        holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.green));
        break;
      default:
        holder.txtStatus.setText(incidencia.getStatus());
        break;
    }


  }

  private void pushBtnViewIncient(Incident incidencia){
    Bundle bundle = new Bundle();
    bundle.putString("incident_id", incidencia.getUid());
    Log.e("FIREBASE", "id_incdent enviada: "+incidencia.getUid());


    navController.navigate(R.id.nav_checkIncident, bundle);
  }

  @Override
  public int getItemCount() {
    return listaIncidencias.size();
  }

  public static class IncidentViewHolder extends RecyclerView.ViewHolder {
    TextView txtTypeIncident;
    TextView txtStatus;
    Button  btnView;

    public IncidentViewHolder(View itemView) {
      super(itemView);
      txtTypeIncident = itemView.findViewById(R.id.txtTypeIncident);
      txtStatus = itemView.findViewById(R.id.txtStatus);
      btnView = itemView.findViewById(R.id.btnView);
    }
  }
}
