package com.example.myapplication.ui.config.manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.myapplication.LoginActivity;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeleteAccountManager {

  private final Context context;
  private final Activity activity;

  public DeleteAccountManager(Context context, Activity activity) {
    this.context = context;
    this.activity = activity;
  }

  /**
   * Muestra un diálogo de confirmación para que el usuario pueda eliminar su cuenta.
   */
  public void showDeleteAccountDialog() {
    new AlertDialog.Builder(context)
      .setTitle(context.getString(R.string.dialog_delete_account_title))
      .setMessage(context.getString(R.string.dialog_delete_account_message))
      .setPositiveButton(context.getString(R.string.dialog_delete_account_yes), (dialog, which) -> pushBtnDeleteAccount())
      .setNegativeButton(context.getString(R.string.dialog_delete_account_no), null)
      .show();
  }

  /**
   * Gestiona la eliminación de la cuenta del usuario actual,
   * borrando primero su documento en Firestore y luego la cuenta de autenticación.
   */
  private void pushBtnDeleteAccount() {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    if (user != null) {
      FirebaseFirestore db = FirebaseFirestore.getInstance();
      db.collection("users").document(user.getUid())
        .delete()
        .addOnSuccessListener(aVoid ->
          user.delete()
            .addOnCompleteListener(task -> {
              if (task.isSuccessful()) {
                Toast.makeText(context, context.getString(R.string.toast_account_deleted), Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE);
                sharedPreferences.edit().clear().apply();
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
                activity.finish();
              } else {
                Toast.makeText(context, context.getString(R.string.toast_account_delete_failed), Toast.LENGTH_SHORT).show();
              }
            })
        )
        .addOnFailureListener(e -> Toast.makeText(context, context.getString(R.string.toast_account_delete_failed), Toast.LENGTH_SHORT).show());
    }
  }

}
