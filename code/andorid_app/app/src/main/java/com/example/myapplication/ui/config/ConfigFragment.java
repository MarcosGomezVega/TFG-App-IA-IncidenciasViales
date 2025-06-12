package com.example.myapplication.ui.config;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;


import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.manager.LogoutManager;
import com.example.myapplication.ui.config.manager.AvatarManager;
import com.example.myapplication.ui.config.manager.DeleteAccountManager;
import com.example.myapplication.ui.config.manager.EmailManager;
import com.example.myapplication.ui.config.manager.PasswordManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Locale;

/**
 * Fragmento de configuración que permite al usuario modificar opciones de la aplicación como:
 * - Cambiar idioma
 * - Activar/desactivar modo oscuro
 * - Activar/desactivar notificaciones
 * - Cambiar email, contraseña y avatar
 * - Eliminar cuenta
 * - Cerrar sesión
 * <p>
 * Este fragmento actúa como panel de control de la configuración personal del usuario.
 */
public class ConfigFragment extends Fragment {
  /**
   * Interruptor para activar o desactivar el modo oscuro.
   */
  private Switch switchDarkMode;
  /**
   * Interruptor para activar o desactivar las notificaciones.
   */
  private Switch switchNotification;
  /**
   * Selector de idioma para la aplicación.
   */
  private Spinner spinnerLenguage;

  /**
   * Lanzador de actividad para capturar una foto con la cámara.
   */
  private ActivityResultLauncher<Uri> takePictureLauncher;
  /**
   * Lanzador de actividad para seleccionar una imagen desde la galería.
   */
  private ActivityResultLauncher<String> pickImageLauncher;

  /**
   * Gestor responsable de las operaciones relacionadas con el avatar del usuario.
   */
  private AvatarManager avatarManager;

  /**
   * Inicializa el fragmento y configura los elementos de la interfaz,
   * así como los listeners para los botones y componentes interactivos.
   *
   * @param inflater           Inflater para crear la vista.
   * @param container          Contenedor padre de la vista.
   * @param savedInstanceState Estado previo guardado del fragmento.
   * @return La vista creada para el fragmento.
   */
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    View root = inflater.inflate(R.layout.fragment_config, container, false);

    switchDarkMode = root.findViewById(R.id.switchDarkMode);
    switchNotification = root.findViewById(R.id.switchNotifications);
    spinnerLenguage = root.findViewById(R.id.spinnerLanguage);
    Button btnChangeEmail = root.findViewById(R.id.btnChangeEmail);
    Button btnChangePasword = root.findViewById(R.id.btnChangePassword);
    Button btnChangeAvatar = root.findViewById(R.id.btnChangeAvatar);
    Button btnDeleteAcount = root.findViewById(R.id.btnDeleteAccount);
    Button btnLogOut = root.findViewById(R.id.btnLogout);

    loadNotificationPreference();

    setupActivityLaunchers();
    setupSpinner();
    setupDarkModeSwitch();
    setUpNotificationModeSwitch();


    btnChangeEmail.setOnClickListener(v -> pushBtnChangeEmail());
    btnChangePasword.setOnClickListener(v -> pushBtnChangePassword());
    btnChangeAvatar.setOnClickListener(v -> pushBtnChangeAvatar());
    btnDeleteAcount.setOnClickListener(v -> pushDeleteAcount());
    btnLogOut.setOnClickListener(v -> LogoutManager.logout(requireContext(), requireActivity()));

    return root;
  }

  /**
   * Configura el adaptador y comportamiento del spinner de selección de idioma.
   * Asigna el idioma actual y define la acción al cambiar la selección.
   */
  private void setupSpinner() {
    String[] languages = {
      getString(R.string.lenguage_chose),
      getString(R.string.spanish),
      getString(R.string.english)
    };

    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, languages);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinnerLenguage.setAdapter(adapter);

    spinnerLenguage.setSelection(0);

    spinnerLenguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      boolean isFirstTime = true;

      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (isFirstTime) {
          isFirstTime = false;
          return;
        }

        String selectedLang = parent.getItemAtPosition(position).toString();
        String langCode = "";

        if (selectedLang.equals(getString(R.string.spanish))) {
          langCode = "es";
        } else if (selectedLang.equals(getString(R.string.english))) {
          langCode = "en";
        }

        if (!langCode.isEmpty()) {
          setLocal(requireActivity(), langCode);

          FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
          if (currentUser != null) {
            FirebaseFirestore.getInstance()
              .collection("users")
              .document(currentUser.getUid())
              .update("language", langCode);
          }

          requireActivity().finish();
          startActivity(new Intent(getContext(), MainActivity.class));
        }
      }
      @Override
      public void onNothingSelected(AdapterView<?> parent) {
        // default implementation ignored
      }
    });

  }

  /**
   * Cambia el idioma de la aplicación y actualiza la configuración local del dispositivo.
   *
   * @param activity La actividad desde la cual se aplica la configuración.
   * @param langCode Código del idioma (por ejemplo, "en" o "es").
   */
  public void setLocal(Activity activity, String langCode) {
    Locale locale = new Locale(langCode);
    Locale.setDefault(locale);

    Resources resources = activity.getResources();
    Configuration configuration = resources.getConfiguration();
    configuration.setLocale(locale);
    resources.updateConfiguration(configuration, resources.getDisplayMetrics());
  }

  /**
   * Configura el interruptor para activar o desactivar el modo oscuro,
   * aplicando el tema correspondiente y mostrando mensajes.
   */
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

  private void loadNotificationPreference() {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    if (user == null) {
      return;
    }

    FirebaseMessaging.getInstance().getToken()
      .addOnSuccessListener(token -> {
        if (token == null || token.isEmpty()) {
          return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("device_tokens")
          .document(token)
          .get()
          .addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
              Boolean isActivated = documentSnapshot.getBoolean("notification_activated");
              if (isActivated != null) {
                switchNotification.setChecked(isActivated);
              }
            }
          });
      });

  }

  /**
   * Configura el interruptor para activar o desactivar las notificaciones,
   * mostrando mensajes acorde al estado y actualizando Firestore.
   */
  private void setUpNotificationModeSwitch() {
    switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
      Context context = getContext();
      if (context == null) {
        return;
      }
      updateNotificationPreference(isChecked);
    });
  }

  /**
   * Actualiza la preferencia de notificaciones en Firestore
   * para el token actual del dispositivo.
   */
  private void updateNotificationPreference(boolean isEnabled) {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    if (user == null) {
      return;
    }

    FirebaseMessaging.getInstance().getToken()
      .addOnSuccessListener(token -> {
        if (token == null || token.isEmpty()) {
          return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("device_tokens")
          .document(token)
          .update("notification_activated", isEnabled)
          .addOnSuccessListener(aVoid -> Log.d("TOKEN", "Preferencia actualizada exitosamente en Firestore"))
          .addOnFailureListener(e -> Log.e("TOKEN", "Error al actualizar preferencia en Firestore: " + e.getMessage()));
      })
      .addOnFailureListener(e -> Log.e("TOKEN", "Error al obtener token del dispositivo: " + e.getMessage()));
  }

  /**
   * Abre el diálogo para cambiar el correo electrónico del usuario.
   */
  private void pushBtnChangeEmail() {
    EmailManager emailManager = new EmailManager(requireContext(), getLayoutInflater());
    emailManager.showChangeEmailDialog();
  }

  /**
   * Abre el diálogo para cambiar la contraseña del usuario.
   */
  private void pushBtnChangePassword() {
    PasswordManager passwordManager = new PasswordManager(requireContext(), getLayoutInflater());
    passwordManager.showChangePasswordDialog();
  }

  /**
   * Abre el diálogo para cambiar el avatar del usuario, permitiendo elegir entre cámara o galería.
   */
  private void pushBtnChangeAvatar() {
    avatarManager = new AvatarManager(requireContext(), getLayoutInflater(), takePictureLauncher, pickImageLauncher);
    avatarManager.showChangeAvatarDialog();

  }

  /**
   * Muestra el diálogo de confirmación para eliminar la cuenta del usuario actual.
   */
  private void pushDeleteAcount() {
    DeleteAccountManager deleteAcountManager = new DeleteAccountManager(requireContext(), requireActivity());
    deleteAcountManager.showDeleteAccountDialog();
  }

  /**
   * Inicializa los lanzadores de actividades para tomar fotos, pedir permisos
   * y seleccionar imágenes de la galería, configurando sus callbacks.
   */
  private void setupActivityLaunchers() {
    takePictureLauncher = registerForActivityResult(
      new ActivityResultContracts.TakePicture(),
      result -> avatarManager.handleCameraResult(Boolean.TRUE.equals(result))
    );

    pickImageLauncher = registerForActivityResult(
      new ActivityResultContracts.GetContent(),
      uri -> avatarManager.handleGalleryResult(uri)
    );
  }

}

