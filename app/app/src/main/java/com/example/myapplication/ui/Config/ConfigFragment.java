package com.example.myapplication.ui.Config;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.myapplication.LoginActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.Usuario;
import com.example.myapplication.database.DBManager;

import java.util.Locale;

public class ConfigFragment extends Fragment {

  private Switch switchDarkMode;
  private Switch switchNotification;
  private Spinner spinnerLenguage;

  private Button btnChangeEmail;
  private Button btnChangePasword;
  private Button btnChangeAvatar;
  private Button btnDeleteAcount;
  private Button btnLogOut;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    View root = inflater.inflate(R.layout.fragment_config, container, false);

    switchDarkMode = root.findViewById(R.id.switchDarkMode);
    switchNotification = root.findViewById(R.id.switchNotifications);
    spinnerLenguage = root.findViewById(R.id.spinnerLanguage);
    btnChangeEmail = root.findViewById(R.id.btnChangeEmail);
    btnChangePasword = root.findViewById(R.id.btnChangePassword);
    btnChangeAvatar = root.findViewById(R.id.btnChangeAvatar);
    btnDeleteAcount = root.findViewById(R.id.btnDeleteAccount);
    btnLogOut = root.findViewById(R.id.btnLogout);

    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
      getContext(),
      R.array.languages_array,
      android.R.layout.simple_spinner_item
    );
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinnerLenguage.setAdapter(adapter);

    Locale currentLocale = Locale.getDefault();
    String languageCode = currentLocale.getLanguage();

    if (languageCode.equals("es")) {
      spinnerLenguage.setSelection(0);
    } else {
      spinnerLenguage.setSelection(1);
    }


    setupspinner();
    setupDarkModeSwitch();
    setUpLenguageModeSwitch();

    btnChangeEmail.setOnClickListener(v -> showChangeEmailDialog());
    btnChangePasword.setOnClickListener(v -> showChangePasswordDialog());

    btnDeleteAcount.setOnClickListener(v -> showDeleteAccountDialog());
    btnLogOut.setOnClickListener(v -> logout());
    return root;
  }

  private void setupspinner() {
    spinnerLenguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String language = parent.getItemAtPosition(position).toString();
        if (language.equals(R.string.spanish)) {
          Toast.makeText(getContext(), getString(R.string.configuration_lenguage_spanish), Toast.LENGTH_SHORT).show();
        } else if (language.equals(getString(R.string.english)) || language.equals(R.string.english)) {
          Toast.makeText(getContext(), R.string.configuration_lenguage_english, Toast.LENGTH_SHORT).show();
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });
  }

  private void setupDarkModeSwitch() {
    int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
    switchDarkMode.setChecked(currentNightMode == Configuration.UI_MODE_NIGHT_YES);

    switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
      if (isChecked) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        Toast.makeText(getContext(), getString(R.string.dark_mode_activated), Toast.LENGTH_SHORT).show();
      } else {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Toast.makeText(getContext(), getString(R.string.light_mode_activated), Toast.LENGTH_SHORT).show();
      }
    });
  }

  private void setUpLenguageModeSwitch() {
    switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
      if (isChecked) {
        Toast.makeText(getContext(), getString(R.string.notification_activated), Toast.LENGTH_SHORT).show();
      } else {
        Toast.makeText(getContext(), getString(R.string.notification_disable), Toast.LENGTH_SHORT).show();
      }
    });
  }

  private void showChangeEmailDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
    builder.setTitle(getString(R.string.change_email));

    View view = getLayoutInflater().inflate(R.layout.dialog_change_email, null);
    builder.setView(view);

    EditText edtCurrentEmail = view.findViewById(R.id.edtCurrentEmail);
    EditText edtUsername = view.findViewById(R.id.edtUsername);
    EditText edtNewEmail = view.findViewById(R.id.edtNewEmail);
    EditText edtConfirmNewEmail = view.findViewById(R.id.edtConfirmNewEmail);
    EditText edtPassword = view.findViewById(R.id.edtPassword);

    builder.setPositiveButton(getString(R.string.change), (dialog, which) -> {
      String currentEmail = edtCurrentEmail.getText().toString().trim();
      String username = edtUsername.getText().toString().trim();
      String newEmail = edtNewEmail.getText().toString().trim();
      String confirmNewEmail = edtConfirmNewEmail.getText().toString().trim();
      String password = edtPassword.getText().toString().trim();

      if (currentEmail.isEmpty() || username.isEmpty() || newEmail.isEmpty() || confirmNewEmail.isEmpty() || password.isEmpty()) {
        Toast.makeText(getContext(), getString(R.string.toast_all_fields_required), Toast.LENGTH_SHORT).show();
        return;
      }

      if (!newEmail.equals(confirmNewEmail)) {
        Toast.makeText(getContext(), getString(R.string.toast_emails_not_match), Toast.LENGTH_SHORT).show();
        return;
      }

      DBManager dbManager = new DBManager(requireContext());
      Usuario usuario = dbManager.obtenerUsuarioPorCorreo(currentEmail);

      if (usuario == null) {
        Toast.makeText(getContext(), getString(R.string.toast_current_email_not_found), Toast.LENGTH_SHORT).show();
        return;
      }

      SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE);
      int sessionUserId = sharedPreferences.getInt("user_id", -1);

      if (usuario.getId() != sessionUserId) {
        Toast.makeText(getContext(), getString(R.string.toast_email_not_user), Toast.LENGTH_SHORT).show();
        return;
      }

      if (!usuario.getNombre().equals(username) || !usuario.getPassword().equals(password)) {
        Toast.makeText(getContext(), getString(R.string.toast_username_or_password_wrong), Toast.LENGTH_SHORT).show();
        return;
      }

      boolean actualizado = dbManager.actualizarCorreo(currentEmail, newEmail);
      if (actualizado) {
        Toast.makeText(getContext(), getString(R.string.toast_email_updated_success), Toast.LENGTH_SHORT).show();
      } else {
        Toast.makeText(getContext(), getString(R.string.toast_email_update_failed), Toast.LENGTH_SHORT).show();
      }

    });
    builder.setNegativeButton(getString(R.string.button_cancel), (dialog, which) -> dialog.dismiss());
    builder.create().show();
  }

  private void showChangePasswordDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
    builder.setTitle(getString(R.string.change_password));

    View view = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
    builder.setView(view);

    EditText edtEmail = view.findViewById(R.id.edtEmail);
    EditText edtCurrentPassword = view.findViewById(R.id.edtCurrentPassword);
    EditText edtNewPassword = view.findViewById(R.id.edtNewPassword);
    EditText edtConfirmNewPassword = view.findViewById(R.id.edtConfirmNewPassword);

    builder.setPositiveButton(getString(R.string.change), (dialog, which) -> {
      String email = edtEmail.getText().toString().trim();
      String currentPassword = edtCurrentPassword.getText().toString().trim();
      String newPassword = edtNewPassword.getText().toString().trim();
      String confirmNewPassword = edtConfirmNewPassword.getText().toString().trim();

      if (email.isEmpty() || currentPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
        Toast.makeText(getContext(), getString(R.string.toast_all_fields_required), Toast.LENGTH_SHORT).show();
        return;
      }

      if (!newPassword.equals(confirmNewPassword)) {
        Toast.makeText(getContext(), getString(R.string.toast_passwords_not_match), Toast.LENGTH_SHORT).show();
        return;
      }

      DBManager dbManager = new DBManager(requireContext());
      Usuario usuario = dbManager.obtenerUsuarioPorCorreo(email);

      if (usuario == null) {
        Toast.makeText(getContext(), getString(R.string.toast_user_not_found), Toast.LENGTH_SHORT).show();
        return;
      }

      SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE);
      int sessionUserId = sharedPreferences.getInt("user_id", -1);

      if (usuario.getId() != sessionUserId) {
        Toast.makeText(getContext(), getString(R.string.toast_email_not_user), Toast.LENGTH_SHORT).show();
        return;
      }

      if (!usuario.getPassword().equals(currentPassword)) {
        Toast.makeText(getContext(), getString(R.string.toast_wrong_current_password), Toast.LENGTH_SHORT).show();
        return;
      }

      boolean actualizado = dbManager.actualizarPassword(email, newPassword);
      if (actualizado) {
        Toast.makeText(getContext(), getString(R.string.toast_password_updated_success), Toast.LENGTH_SHORT).show();
      } else {
        Toast.makeText(getContext(), getString(R.string.toast_password_update_failed), Toast.LENGTH_SHORT).show();
      }

    });
    builder.setNegativeButton(getString(R.string.button_cancel), (dialog, which) -> dialog.dismiss());
    builder.create().show();
  }

  private void showDeleteAccountDialog() {
    new AlertDialog.Builder(requireContext())
      .setTitle(getString(R.string.dialog_delete_account_title))
      .setMessage(getString(R.string.dialog_delete_account_message))
      .setPositiveButton(getString(R.string.dialog_delete_account_yes), (dialog, which) -> {
        DBManager dbManager = new DBManager(requireContext());
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        int sessionUserId = sharedPreferences.getInt("user_id", -1);

        if (dbManager.deleteUser(sessionUserId)) {
          Toast.makeText(getContext(), getString(R.string.toast_account_deleted), Toast.LENGTH_SHORT).show();
          sharedPreferences.edit().clear().apply();
          Intent intent = new Intent(requireContext(), LoginActivity.class);
          startActivity(intent);
          requireActivity().finish();
        } else {
          Toast.makeText(getContext(), getString(R.string.toast_account_delete_failed), Toast.LENGTH_SHORT).show();
        }
      })
      .setNegativeButton(getString(R.string.dialog_delete_account_no), null)
      .show();
  }

  private void logout() {
    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putBoolean("is_logged_in", false);
    editor.remove("user_id");
    editor.apply();

    Intent intent = new Intent(requireContext(), LoginActivity.class);
    startActivity(intent);
    requireActivity().finish();
  }


}

