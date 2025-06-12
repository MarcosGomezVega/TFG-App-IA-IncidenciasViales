package com.example.myapplication.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.myapplication.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public final class LogoutManager {

  private LogoutManager() {
    throw new UnsupportedOperationException("Utility class should not be instantiated");
  }

  /**
   * Cierra la sesión del usuario actual y redirige a la pantalla de login.
   *
   * @param context  Un contexto válido (por ejemplo, getContext() desde un Fragment).
   * @param activity La actividad actual desde donde se llama (para poder llamar a finish()).
   */
  public static void logout(Context context, Activity activity) {
    FirebaseAuth.getInstance().signOut();

    Intent intent = new Intent(context, LoginActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    context.startActivity(intent);

    activity.finish();
  }
}
