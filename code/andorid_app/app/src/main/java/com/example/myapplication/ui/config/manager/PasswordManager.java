package com.example.myapplication.ui.config.manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PasswordManager {

  private final Context context;
  private final LayoutInflater inflater;

  public PasswordManager(Context context, LayoutInflater inflater) {
    this.context = context;
    this.inflater = inflater;
  }


  /**
   * Muestra un diálogo personalizado para que el usuario pueda cambiar su contraseña.
   * Incluye los campos necesarios y botones para confirmar o cancelar.
   */
  public void showChangePasswordDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
    builder.setTitle(context.getString(R.string.change_password));

    View view = inflater.inflate(R.layout.dialog_change_password, null);
    builder.setView(view);

    EditText edtEmail = view.findViewById(R.id.edtEmail);
    EditText edtCurrentPassword = view.findViewById(R.id.edtCurrentPassword);
    EditText edtNewPassword = view.findViewById(R.id.edtNewPassword);
    EditText edtConfirmNewPassword = view.findViewById(R.id.edtConfirmNewPassword);

    builder.setPositiveButton(context.getString(R.string.change), (dialog, which) -> pushBtnChangePassword(edtEmail, edtCurrentPassword, edtNewPassword, edtConfirmNewPassword));
    builder.setNegativeButton(context.getString(R.string.button_cancel), null);

    AlertDialog dialog = builder.create();
    dialog.setCancelable(false);
    dialog.show();
  }

  /**
   * Gestiona la lógica para cambiar la contraseña del usuario,
   * realizando validaciones y autenticación antes de actualizar.
   *
   * @param edtEmail              Campo con el correo electrónico.
   * @param edtCurrentPassword    Campo con la contraseña actual.
   * @param edtNewPassword        Campo con la nueva contraseña.
   * @param edtConfirmNewPassword Campo para confirmar la nueva contraseña.
   */
  private void pushBtnChangePassword(EditText edtEmail, EditText edtCurrentPassword, EditText edtNewPassword, EditText edtConfirmNewPassword) {
    String email = edtEmail.getText().toString().trim();
    String currentPassword = edtCurrentPassword.getText().toString().trim();
    String newPassword = edtNewPassword.getText().toString().trim();
    String confirmNewPassword = edtConfirmNewPassword.getText().toString().trim();

    if (email.isEmpty() || currentPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
      Toast.makeText(context, context.getString(R.string.toast_all_fields_required), Toast.LENGTH_SHORT).show();
      return;
    }

    if (!newPassword.equals(confirmNewPassword)) {
      Toast.makeText(context, context.getString(R.string.toast_passwords_not_match), Toast.LENGTH_SHORT).show();
      return;
    }

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    if (user != null) {
      AuthCredential credential = EmailAuthProvider.getCredential(email, currentPassword);
      user.reauthenticate(credential).addOnCompleteListener(task -> {
        if (task.isSuccessful()) {

          user.updatePassword(newPassword).addOnCompleteListener(updateTask -> {
            if (updateTask.isSuccessful()) {
              Toast.makeText(context, context.getString(R.string.toast_password_updated_success), Toast.LENGTH_SHORT).show();
            } else {
              Toast.makeText(context, context.getString(R.string.toast_password_update_failed), Toast.LENGTH_SHORT).show();
            }
          });
        } else {
          Toast.makeText(context, context.getString(R.string.toast_error_reauthenticating), Toast.LENGTH_SHORT).show();
        }
      });
    }
  }
}
