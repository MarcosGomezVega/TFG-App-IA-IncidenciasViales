package com.example.myapplication.manager;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.myapplication.R;

/**
 * Clase utilitaria para aplicar el estado textual y el color correspondiente
 * a un TextView, según un estado dado (como "pendiente", "en proceso", "resuelta").
 */
public class ChangeColorStatusManager {


  private ChangeColorStatusManager() {}

  /**
   * Aplica el estado y su color correspondiente a un TextView.
   *
   * @param context   El contexto para acceder a recursos.
   * @param txtStatus El TextView a modificar.
   * @param state     El estado textual ("pendiente", "en proceso", "resuelta", etc.).
   */
  public static void applyStatus(Context context, TextView txtStatus, String state) {
    if (context == null || txtStatus == null || state == null) return;

    switch (state.toLowerCase()) {
      case "pendiente":
        txtStatus.setText(context.getString(R.string.waiting));
        txtStatus.setTextColor(ContextCompat.getColor(context, R.color.red));
        break;
      case "en proceso":
        txtStatus.setText(context.getString(R.string.in_proces));
        txtStatus.setTextColor(ContextCompat.getColor(context, R.color.yellow));
        break;
      case "resuelta":
        txtStatus.setText(context.getString(R.string.result));
        txtStatus.setTextColor(ContextCompat.getColor(context, R.color.green));
        break;
      default:
        txtStatus.setText(state);
        txtStatus.setTextColor(Color.LTGRAY);
        break;
    }
  }

}
