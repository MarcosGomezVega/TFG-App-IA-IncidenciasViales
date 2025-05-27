package com.example.myapplication.ui.config;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.myapplication.LocaleHelper;
import com.example.myapplication.LoginActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ConfigFragment extends Fragment {

  private Switch switchDarkMode;
  private Switch switchNotification;
  private Spinner spinnerLenguage;
  private ImageView imgviewAvatar;
  private AlertDialog dialog;

  /**
   * Ruta del archivo de la imagen capturada
   */
  private String currentPhotoPath;

  /**
   * Lanzador para tomar una foto
   */
  private ActivityResultLauncher<Uri> takePictureLauncher;

  /**
   * Lanzador para pedir permiso de la cámara
   */
  private ActivityResultLauncher<String> requestCameraPermissionLauncher;
  private ActivityResultLauncher<String> pickImageLauncher;
  private boolean userIsInteracting = false;

  /**
   * Inicializa el fragmento y configura los elementos de la interfaz,
   * así como los listeners para los botones y componentes interactivos.
   *
   * @param inflater           Inflater para crear la vista.
   * @param container          Contenedor padre de la vista.
   * @param savedInstanceState Estado previo guardado del fragmento.
   * @return La vista creada para el fragmento.
   */
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    View root = inflater.inflate(R.layout.fragment_config, container, false);

    switchDarkMode = root.findViewById(R.id.switchDarkMode);
    switchNotification = root.findViewById(R.id.switchNotifications);
    spinnerLenguage = root.findViewById(R.id.spinnerLanguage);
    Button btnChangeEmail = root.findViewById(R.id.btnChangeEmail);
    Button btnChangePasword = root.findViewById(R.id.btnChangePassword);
    Button btnChangeAvatar = root.findViewById(R.id.btnChangeAvatar);
    Button btnDeleteAcount = root.findViewById(R.id.btnDeleteAccount);
    Button btnLogOut = root.findViewById(R.id.btnLogout);

    setupActivityLauncher();
    setupSpinner();
    setupDarkModeSwitch();
    setUpNotificationModeSwitch();

    btnChangeEmail.setOnClickListener(v -> showChangeEmailDialog());
    btnChangePasword.setOnClickListener(v -> showChangePasswordDialog());
    btnChangeAvatar.setOnClickListener(v -> showChangeAvatar());
    btnDeleteAcount.setOnClickListener(v -> showDeleteAccountDialog());
    btnLogOut.setOnClickListener(v -> logout());

    return root;
  }

  /**
   * Configura el adaptador y comportamiento del spinner de selección de idioma.
   * Asigna el idioma actual y define la acción al cambiar la selección.
   */

  private void setupSpinner() {
    String[] languages  = {getString(R.string.lenguage_chose), getString(R.string.spanish), getString(R.string.english)};
    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, languages );
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinnerLenguage.setAdapter(adapter);

    SharedPreferences prefs = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
    String savedLang = prefs.getString("lang", Locale.getDefault().getLanguage());


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

        if (selectedLang.equals(getString(R.string.spanish))) {
          prefs.edit().putString("lang", "es").apply();
          setLocal(requireActivity(), "es");
          requireActivity().finish();
          startActivity(new Intent(getContext(), MainActivity.class));
        } else if (selectedLang.equals(getString(R.string.english))) {
          prefs.edit().putString("lang", "en").apply();
          setLocal(requireActivity(), "en");
          requireActivity().finish();
          startActivity(new Intent(getContext(), MainActivity.class));
        }

      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });

  }
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

  /**
   * Configura el interruptor para activar o desactivar las notificaciones,
   * mostrando mensajes acorde al estado.
   */
  private void setUpNotificationModeSwitch() {
    switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
      if (isChecked) {
        Toast.makeText(getContext(), getString(R.string.notification_activated), Toast.LENGTH_SHORT).show();
      } else {
        Toast.makeText(getContext(), getString(R.string.notification_disable), Toast.LENGTH_SHORT).show();
      }
    });
  }

  /**
   * Muestra un diálogo personalizado para que el usuario pueda cambiar su correo electrónico.
   * Incluye validaciones básicas y la acción para cambiar el correo.
   */
  private void showChangeEmailDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
    builder.setTitle(getString(R.string.change_email));

    View view = getLayoutInflater().inflate(R.layout.dialog_change_email, null);
    builder.setView(view);

    EditText edtCurrentEmail = view.findViewById(R.id.edtCurrentEmail);
    EditText edtNewEmail = view.findViewById(R.id.edtNewEmail);
    EditText edtConfirmNewEmail = view.findViewById(R.id.edtConfirmNewEmail);
    EditText edtPassword = view.findViewById(R.id.edtPassword);

    builder.setPositiveButton(getString(R.string.change), (dialog, which) -> pushBtnChangeEmail(edtCurrentEmail, edtNewEmail, edtConfirmNewEmail, edtPassword));
    builder.setNegativeButton(getString(R.string.button_cancel), (dialog, which) -> dialog.dismiss());
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
      Toast.makeText(getContext(), getString(R.string.toast_all_fields_required), Toast.LENGTH_SHORT).show();
      return;
    }

    if (!newEmail.equals(confirmNewEmail)) {
      Toast.makeText(getContext(), getString(R.string.toast_emails_not_match), Toast.LENGTH_SHORT).show();
      return;
    }

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    if (!user.getEmail().equals(currentEmail)) {
      Toast.makeText(getContext(), getString(R.string.toast_current_email_not_match), Toast.LENGTH_SHORT).show();
      return;
    }

    AuthCredential credential = EmailAuthProvider.getCredential(currentEmail, password);

    user.reauthenticate(credential).addOnCompleteListener(task -> {
      if (task.isSuccessful()) {
        user.verifyBeforeUpdateEmail(newEmail).addOnCompleteListener(updateTask -> {
          if (updateTask.isSuccessful()) {
            Toast.makeText(getContext(), "Verifica el nuevo correo para completar el cambio, Se te habrá enviado un correo al nuevo correo electronico", Toast.LENGTH_LONG).show();
          } else {
            Toast.makeText(getContext(), "Error al enviar verificación", Toast.LENGTH_SHORT).show();
          }
        });
      } else {
        Toast.makeText(getContext(), "Reautenticación fallida", Toast.LENGTH_SHORT).show();
      }
    });

  }

  /**
   * Muestra un diálogo personalizado para que el usuario pueda cambiar su contraseña.
   * Incluye los campos necesarios y botones para confirmar o cancelar.
   */
  private void showChangePasswordDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
    builder.setTitle(getString(R.string.change_password));

    View view = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
    builder.setView(view);

    EditText edtEmail = view.findViewById(R.id.edtEmail);
    EditText edtCurrentPassword = view.findViewById(R.id.edtCurrentPassword);
    EditText edtNewPassword = view.findViewById(R.id.edtNewPassword);
    EditText edtConfirmNewPassword = view.findViewById(R.id.edtConfirmNewPassword);

    builder.setPositiveButton(getString(R.string.change), (dialog, which) -> pushBtnChangePassword(edtEmail, edtCurrentPassword, edtNewPassword, edtConfirmNewPassword));
    builder.setNegativeButton(getString(R.string.button_cancel), null);

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
      Toast.makeText(getContext(), getString(R.string.toast_all_fields_required), Toast.LENGTH_SHORT).show();
      return;
    }

    if (!newPassword.equals(confirmNewPassword)) {
      Toast.makeText(getContext(), getString(R.string.toast_passwords_not_match), Toast.LENGTH_SHORT).show();
      return;
    }

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    if (user != null) {
      AuthCredential credential = EmailAuthProvider.getCredential(email, currentPassword);
      user.reauthenticate(credential).addOnCompleteListener(task -> {
        if (task.isSuccessful()) {

          user.updatePassword(newPassword).addOnCompleteListener(updateTask -> {
            if (updateTask.isSuccessful()) {
              Toast.makeText(getContext(), getString(R.string.toast_password_updated_success), Toast.LENGTH_SHORT).show();
            } else {
              Toast.makeText(getContext(), getString(R.string.toast_password_update_failed), Toast.LENGTH_SHORT).show();
            }
          });
        } else {
          Toast.makeText(getContext(), getString(R.string.toast_error_reauthenticating), Toast.LENGTH_SHORT).show();
        }
      });
    }
  }

  /**
   * Muestra un diálogo de confirmación para que el usuario pueda eliminar su cuenta.
   */
  private void showDeleteAccountDialog() {
    new AlertDialog.Builder(requireContext())
      .setTitle(getString(R.string.dialog_delete_account_title))
      .setMessage(getString(R.string.dialog_delete_account_message))
      .setPositiveButton(getString(R.string.dialog_delete_account_yes), (dialog, which) -> pushBtnDeleteAccount())
      .setNegativeButton(getString(R.string.dialog_delete_account_no), null)
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
                Toast.makeText(getContext(), getString(R.string.toast_account_deleted), Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE);
                sharedPreferences.edit().clear().apply();
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                startActivity(intent);
                requireActivity().finish();
              } else {
                Toast.makeText(getContext(), getString(R.string.toast_account_delete_failed), Toast.LENGTH_SHORT).show();
              }
            })
        )
        .addOnFailureListener(e -> Toast.makeText(getContext(), getString(R.string.toast_account_delete_failed), Toast.LENGTH_SHORT).show());
    }
  }

  /**
   * Muestra un diálogo personalizado para que el usuario pueda cambiar su avatar,
   * con opciones para seleccionar una imagen desde la galería o tomar una foto.
   */
  private void showChangeAvatar() {
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
    builder.setTitle("Cambiar Avatar");

    View view = getLayoutInflater().inflate(R.layout.dialog_change_avatar, null);
    imgviewAvatar = view.findViewById(R.id.imageviewAvatar);
    Button btnGalery = view.findViewById(R.id.btnGallery);
    Button btnCamera = view.findViewById(R.id.btnCamera);

    btnGalery.setOnClickListener(v -> pickImageFromGallery());
    btnCamera.setOnClickListener(v -> openCamera());

    builder.setPositiveButton(getString(R.string.change), (dialog, which) -> btnChangeAvatar());
    builder.setNegativeButton(getString(R.string.button_cancel), (dialog, which) -> dialog.dismiss());

    dialog = builder.create();
    dialog.setView(view);
    dialog.show();
  }

  /**
   * Actualiza la imagen de avatar del usuario en Firestore usando la ruta
   * actual almacenada en `currentPhotoPath`.
   */
  private void btnChangeAvatar() {
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Uri fileUri = Uri.fromFile(new File(currentPhotoPath));
    String fileName = "images/avatar/" + fileUri.getLastPathSegment();

    StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(fileName);
    UploadTask uploadTask = storageRef.putFile(fileUri);

    uploadTask.addOnSuccessListener(taskSnapshot ->
      storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
        String imageUrl = uri.toString();
        Map<String, Object> update = new HashMap<>();
        update.put("avatar", imageUrl);

        db.collection("users").document(uid)
          .update(update)
          .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Avatar actualizado", Toast.LENGTH_SHORT).show());


      })
    ).addOnFailureListener(e ->
      Toast.makeText(getContext(), getString(R.string.error_upload_photo), Toast.LENGTH_SHORT).show()
    );


  }


  /**
   * Lanza el selector de imágenes para que el usuario elija una imagen desde la galería.
   */
  private void pickImageFromGallery() {
    pickImageLauncher.launch("image/*");
  }

  /**
   * Abre la cámara del dispositivo para tomar una fotografía y guarda temporalmente la imagen capturada.
   */
  private void openCamera() {
    try {
      File photoFile = createImageFile();
      if (photoFile != null) {
        Uri photoURI = FileProvider.getUriForFile(
          requireContext(),
          "com.example.myapplication.fileprovider",
          photoFile
        );
        currentPhotoPath = photoFile.getAbsolutePath();
        takePictureLauncher.launch(photoURI);
      }
    } catch (IOException ex) {
      Toast.makeText(getContext(),
        Html.fromHtml("<font color='#FF0000'><b>" + getString(R.string.error_creating_image) + "</b></font>", Html.FROM_HTML_MODE_LEGACY),
        Toast.LENGTH_SHORT).show();
    }
  }

  /**
   * Crea un archivo temporal para almacenar una imagen capturada por la cámara.
   *
   * @return Archivo creado para la imagen.
   * @throws IOException Si ocurre un error al crear el archivo.
   */
  private File createImageFile() throws IOException {
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    String imageFileName = "JPEG_" + timeStamp + "_";

    File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    if (storageDir != null && !storageDir.exists()) {
      storageDir.mkdirs();
    }
    File image = File.createTempFile(
      imageFileName,
      ".jpg",
      storageDir
    );

    currentPhotoPath = image.getAbsolutePath();
    return image;
  }

  /**
   * Inicializa los lanzadores de actividades para tomar fotos, pedir permisos
   * y seleccionar imágenes de la galería, configurando sus callbacks.
   */
  private void setupActivityLauncher() {
    takePictureLauncher = registerForActivityResult(
      new ActivityResultContracts.TakePicture(),
      result -> {
        if (Boolean.TRUE.equals(result)) {
          Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
          if (bitmap != null) {
            imgviewAvatar.setImageBitmap(bitmap);
          }
        }
      }
    );

    requestCameraPermissionLauncher = registerForActivityResult(
      new ActivityResultContracts.RequestPermission(),
      isGranted -> {
        if (Boolean.TRUE.equals(isGranted)) {
          openCamera();
        } else {
          Toast.makeText(getContext(), getString(R.string.camera_permission_fail), Toast.LENGTH_SHORT).show();
        }
      }
    );

    pickImageLauncher = registerForActivityResult(
      new ActivityResultContracts.GetContent(),
      uri -> {
        if (uri != null) {
          imgviewAvatar.setImageURI(uri);
          File localfile = copyContentUriToLocal(uri);
          if (localfile != null) {
            currentPhotoPath = localfile.getAbsolutePath();
          }
        }
      }
    );
  }


  /**
   * Copia el contenido apuntado por un {@link Uri} a un archivo local en el almacenamiento
   * privado de la aplicación y devuelve dicho archivo.
   *
   * @param uri El {@link Uri} de la fuente de datos a copiar.
   * @return Un {@link File} que representa el archivo local creado con el contenido copiado,
   * o {@code null} si ocurre algún error durante la operación.
   */
  private File copyContentUriToLocal(Uri uri) {
    File localFile = new File(requireContext().getFilesDir(), "avatar.jpg");
    try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
         FileOutputStream outputStream = new FileOutputStream(localFile)) {

      byte[] buffer = new byte[4096];
      int bytesRead;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, bytesRead);
      }
      outputStream.flush();
      return localFile;

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Cierra la sesión del usuario actual y redirige a la pantalla de login.
   */
  private void logout() {
    FirebaseAuth.getInstance().signOut();

    Intent intent = new Intent(getContext(), LoginActivity.class);
    startActivity(intent);
    requireActivity().finish();
  }

}

