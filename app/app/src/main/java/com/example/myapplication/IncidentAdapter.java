package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IncidentAdapter extends RecyclerView.Adapter<IncidentAdapter.IncidentViewHolder> {
  private Context context;
  private List<Incident> listaIncidencias;

  // Constructor
  public IncidentAdapter(Context context, List<Incident> listaIncidencias) {
    this.context = context;
    this.listaIncidencias = listaIncidencias;
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

        if (context == null) {
          Log.e("AdapterStatusssss", "Contexto es null");
        } else {
          Log.d("AdapterStatusssss", "Contexto es válido: " + context.getClass().getSimpleName());
        }
        Intent intent = new Intent(context, CheckIncidentActivity.class);
        intent.putExtra("ID_incident", incidencia.getId());
        context.startActivity(intent);
      }
    );
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
