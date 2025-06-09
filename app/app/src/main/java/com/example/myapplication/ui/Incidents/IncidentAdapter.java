package com.example.myapplication.ui.incidents;

import android.content.Context;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Incident;
import com.example.myapplication.R;
import com.example.myapplication.manager.ChangeColorStatusManager;

import java.util.List;
import java.util.Locale;

/**
 * Adaptador personalizado para mostrar una lista de incidentes en un RecyclerView.
 * Permite visualizar el tipo y estado del incidente, y proporciona un botón para ver detalles.
 */
public class IncidentAdapter extends RecyclerView.Adapter<IncidentAdapter.IncidentViewHolder> {
  /**
   * Contexto de la actividad o fragmento.
   */
  private Context context;
  /**
   * Lista de incidentes a mostrar en el RecyclerView.
   */
  private List<Incident> incidentList;

  /**
   * Controlador de navegación para gestionar transiciones de pantalla.
   */
  private NavController navController;

  /**
   * Constructor para el adaptador de incidentes.
   *
   * @param context       El contexto de la actividad o fragmento.
   * @param incidentList  Lista de objetos {@link Incident} que se mostrarán.
   * @param navController Controlador de navegación para gestionar las transiciones entre fragmentos.
   */
  public IncidentAdapter(Context context, List<Incident> incidentList, NavController navController) {
    this.context = context;
    this.incidentList = incidentList;
    this.navController = navController;
  }

  /**
   * Crea nuevas vistas (invocado por el layout manager).
   *
   * @param parent   El ViewGroup en el que se añadirá la nueva vista.
   * @param viewType Tipo de vista del nuevo ítem.
   * @return Una nueva instancia de {@link IncidentViewHolder}.
   */
  @Override
  public IncidentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_incident, parent, false);
    return new IncidentViewHolder(view);
  }

  /**
   * Reemplaza el contenido de una vista (invocado por el layout manager).
   *
   * @param holder   ViewHolder que debe ser actualizado.
   * @param position Posición del elemento dentro del conjunto de datos.
   */
  @Override
  public void onBindViewHolder(IncidentViewHolder holder, int position) {

    Incident incident = incidentList.get(position);
    holder.txtTypeIncident.setText(translateIncidentType(incident.getIncidentType()));


    holder.btnView.setOnClickListener(v -> pushBtnViewIncient(incident));
    String state = incident.getStatus().toLowerCase();

    ChangeColorStatusManager.applyStatus(holder.itemView.getContext(), holder.txtStatus, state);

  }

  /**
   * Traduce el tipo de incidente al inglés si el idioma actual es inglés.
   * Usa las preferencias guardadas o, si no existen, el idioma del sistema.
   *
   * @param type Tipo de incidente en texto.
   * @return Tipo de incidente traducido si corresponde, o el mismo texto si no se necesita traducir.
   */
  private String translateIncidentType(String type) {
    SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);

    String lang = prefs.getString("lang", null); // null si no hay idioma guardado
    Log.d("LANG_DEBUG", "Current language share preferecnces: " + lang);

    if (lang == null) {
      // No hay idioma guardado, tomar el idioma del sistema
      Locale systemLocale = Locale.getDefault();
      lang = systemLocale.getLanguage();
    }

    Log.d("LANG_DEBUG", "Current language sin idioma guardado: " + lang);

    boolean isEnglish = lang.equals("en");

    if (!isEnglish) return type;

    switch (type.toLowerCase()) {
      case "grieta":
        return "Crack";
      case "agujero":
        return "Pothole";
      case "poste caído":
        return "Fallen pole";
      case "sin incidencia":
        return "No incident";
      default:
        return type;
    }
  }


  /**
   * Navega a la vista de detalle del incidente seleccionado.
   *
   * @param incident Incidente que se desea visualizar.
   */
  private void pushBtnViewIncient(Incident incident) {
    Bundle bundle = new Bundle();
    bundle.putString("incident_id", incident.getUid());

    navController.navigate(R.id.nav_checkIncident, bundle);
  }

  /**
   * Devuelve el número total de elementos en la lista.
   *
   * @return Cantidad de incidentes en la lista.
   */
  @Override
  public int getItemCount() {
    return incidentList.size();
  }

  /**
   * Clase interna que representa el ViewHolder para un incidente.
   * Contiene referencias a los elementos visuales del ítem.
   */
  public static class IncidentViewHolder extends RecyclerView.ViewHolder {
    TextView txtTypeIncident;
    TextView txtStatus;
    Button btnView;

    /**
     * Constructor del ViewHolder que inicializa las vistas del ítem.
     *
     * @param itemView Vista correspondiente al ítem de la lista.
     */
    public IncidentViewHolder(View itemView) {
      super(itemView);
      txtTypeIncident = itemView.findViewById(R.id.txtTypeIncident);
      txtStatus = itemView.findViewById(R.id.txtStatus);
      btnView = itemView.findViewById(R.id.btnView);
    }
  }
}
