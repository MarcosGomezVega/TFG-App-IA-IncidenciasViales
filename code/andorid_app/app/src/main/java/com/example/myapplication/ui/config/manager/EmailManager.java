package com.example.myapplication.ui.config.manager;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailManager {
  private final Context context;
  private final LayoutInflater inflater;

  public EmailManager(Context context, LayoutInflater inflater) {
    this.context = context;
    this.inflater = inflater;
  }

  /**
   * Muestra un diálogo personalizado para que el usuario pueda cambiar su correo electrónico.
   * Incluye validaciones básicas y la acción para cambiar el correo.
   */
  public void showChangeEmailDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
    builder.setTitle(context.getString(R.string.change_email));

    View view = inflater.inflate(R.layout.dialog_change_email, null);
    builder.setView(view);

    EditText edtCurrentEmail = view.findViewById(R.id.edtCurrentEmail);
    EditText edtNewEmail = view.findViewById(R.id.edtNewEmail);
    EditText edtConfirmNewEmail = view.findViewById(R.id.edtConfirmNewEmail);
    EditText edtPassword = view.findViewById(R.id.edtPassword);

    builder.setPositiveButton(context.getString(R.string.change), (dialog, which) ->
      pushBtnChangeEmail(edtCurrentEmail, edtNewEmail, edtConfirmNewEmail, edtPassword));
    builder.setNegativeButton(context.getString(R.string.button_cancel), (dialog, which) -> dialog.dismiss());
    builder.create().show();
  }
  /**
   * Gestiona la lógica para cambiar el correo electrónico del usuario,
   * realizando validaciones y autenticación antes de actualizar.
   *
   * @param edtCurrentEmail    Campo con el correo actual.
   * @param edtNewEmail        Campo con el nuevo correo.
   * @param edtConfirmNewEmail Campo para confirmar el nuevo correo.
   * @param edtPassword        Campo para la contraseña actual.
   */
  private void pushBtnChangeEmail(EditText edtCurrentEmail, EditText edtNewEmail, EditText edtConfirmNewEmail, EditText edtPassword) {
    String currentEmail = edtCurrentEmail.getText().toString().trim();
    String newEmail = edtNewEmail.getText().toString().trim();
    String confirmNewEmail = edtConfirmNewEmail.getText().toString().trim();
    String password = edtPassword.getText().toString().trim();

    if (currentEmail.isEmpty() || newEmail.isEmpty() || confirmNewEmail.isEmpty() || password.isEmpty()) {
      Toast.makeText(context, context.getString(R.string.toast_all_fields_required), Toast.LENGTH_SHORT).show();
      return;
    }

    if (!newEmail.equals(confirmNewEmail)) {
      Toast.makeText(context, context.getString(R.string.toast_emails_not_match), Toast.LENGTH_SHORT).show();
      return;
    }

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    if (user == null || !user.getEmail().equals(currentEmail)) {
      Toast.makeText(context, context.getString(R.string.toast_current_email_not_match), Toast.LENGTH_SHORT).show();
      return;
    }

    AuthCredential credential = EmailAuthProvider.getCredential(currentEmail, password);

    user.reauthenticate(credential).addOnCompleteListener(task -> {
      if (task.isSuccessful()) {
        user.verifyBeforeUpdateEmail(newEmail).addOnCompleteListener(updateTask -> {
          if (updateTask.isSuccessful()) {
            Toast.makeText(context, "Verifica el nuevo correo para completar el cambio, se habrá enviado un correo al nuevo email.", Toast.LENGTH_LONG).show();
          } else {
            Toast.makeText(context, "Error al enviar verificación", Toast.LENGTH_SHORT).show();
          }
        });
      } else {
        Toast.makeText(context, "Reautenticación fallida", Toast.LENGTH_SHORT).show();
      }
    });
  }
}
