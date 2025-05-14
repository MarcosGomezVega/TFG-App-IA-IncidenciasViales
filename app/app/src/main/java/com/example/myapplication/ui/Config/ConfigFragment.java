package com.example.myapplication.ui.Config;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
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

import com.example.myapplication.LoginActivity;
import com.example.myapplication.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ConfigFragment extends Fragment {

  private static final int REQUEST_IMAGE_CAPTURE = 1;
  private static final int REQUEST_PICK_IMAGE = 100;
  private Switch switchDarkMode;
  private Switch switchNotification;
  private Spinner spinnerLenguage;
  private Button btnChangeEmail;
  private Button btnChangePasword;
  private Button btnChangeAvatar;
  private Button btnDeleteAcount;
  private Button btnLogOut;
  private Uri photoUri;
  private ImageView imgviewAvatar;
  private Bitmap currentBitmap;
  private Uri selectedImageUri = null;
  private String currentPhotoPath = null;



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
    btnChangeAvatar.setOnClickListener(v -> showChangeAvatar());
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
    EditText edtNewEmail = view.findViewById(R.id.edtNewEmail);
    EditText edtConfirmNewEmail = view.findViewById(R.id.edtConfirmNewEmail);
    EditText edtPassword = view.findViewById(R.id.edtPassword);

    builder.setPositiveButton(getString(R.string.change), (dialog, which) -> {
      pushBtnChangeEmail(edtCurrentEmail, edtNewEmail, edtConfirmNewEmail, edtPassword);
    });
    builder.setNegativeButton(getString(R.string.button_cancel), (dialog, which) -> dialog.dismiss());
    builder.create().show();
  }
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

    if (user == null || user.getEmail() == null) {
      Toast.makeText(getContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
      return;
    }

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
            Log.d("FIREBASE", "Email update failed: " + updateTask.getException());
            Toast.makeText(getContext(), "Error al enviar verificación", Toast.LENGTH_SHORT).show();
          }
        });
      } else {
        Log.d("FIREBASE", "Reauthentication failed: " + task.getException());
        Toast.makeText(getContext(), "Reautenticación fallida", Toast.LENGTH_SHORT).show();
      }
    });

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
      pushBtnChangePassword(edtEmail, edtCurrentPassword, edtNewPassword, edtConfirmNewPassword);
    });

    builder.setNegativeButton(getString(R.string.button_cancel), null);

    AlertDialog dialog = builder.create();
    dialog.setCancelable(false);
    dialog.show();
  }
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


  private void showDeleteAccountDialog() {
    new AlertDialog.Builder(requireContext())
      .setTitle(getString(R.string.dialog_delete_account_title))
      .setMessage(getString(R.string.dialog_delete_account_message))
      .setPositiveButton(getString(R.string.dialog_delete_account_yes), (dialog, which) -> {
        pushBtnDeleteAccount();
      })
      .setNegativeButton(getString(R.string.dialog_delete_account_no), null)
      .show();
  }

  private void pushBtnDeleteAccount() {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    if (user != null) {
      FirebaseFirestore db = FirebaseFirestore.getInstance();
      db.collection("users").document(user.getUid())
        .delete()
        .addOnSuccessListener(aVoid -> {
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
            });
        })
        .addOnFailureListener(e -> {
          Toast.makeText(getContext(), getString(R.string.toast_account_delete_failed), Toast.LENGTH_SHORT).show();
        });
    }
  }

  private void showChangeAvatar() {
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
    builder.setTitle("Cambiar Avatar");

    View view = getLayoutInflater().inflate(R.layout.dialog_change_avatar, null);
    builder.setView(view);

    imgviewAvatar = view.findViewById(R.id.imageviewAvatar);
    ImageButton btnGalery = view.findViewById(R.id.btnGallery);
    ImageButton btnCamera = view.findViewById(R.id.btnCamera);

    btnGalery.setOnClickListener(v -> pickImageFromGallery());
    btnCamera.setOnClickListener(v -> openCamera());


    builder.setPositiveButton(getString(R.string.change), (dialog, which) -> {btnChangeAvatar(); });
    builder.setNegativeButton(getString(R.string.button_cancel), (dialog, which) -> dialog.dismiss());
    builder.create().show();
  }

  private void btnChangeAvatar(){

  }
  private void pickImageFromGallery() {
    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    intent.setType("image/*");
    pickImageLauncher.launch(intent);
  }
  private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
    galery(result);
  });
  private void galery(ActivityResult result) {
    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
      Uri imageUri = result.getData().getData();
      if (imageUri != null) {
        imgviewAvatar.setImageURI(imageUri);
        selectedImageUri = imageUri;
      }
    }
  }
  private final ActivityResultLauncher<Intent> takePhotoLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
    photo(result);
  });
  private void photo(ActivityResult result) {
    if (result.getResultCode() == Activity.RESULT_OK) {
      Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
      if (bitmap != null) {
        imgviewAvatar.setImageBitmap(bitmap);
        selectedImageUri = photoUri;
      } else {
        Toast.makeText(getContext(), "No se pudo cargar la imagen", Toast.LENGTH_SHORT).show();
      }
    }
  }
  private void openCamera() {
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
      File photoFile;
      try {
        photoFile = createImageFile();
      } catch (IOException ex) {
        Toast.makeText(getContext(), "Error al crear archivo de imagen", Toast.LENGTH_SHORT).show();
        return;
      }
      if (photoFile != null) {
        photoUri = FileProvider.getUriForFile(
          getContext(),
          "com.example.myapplication.fileprovider",
          photoFile
        );
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        takePhotoLauncher.launch(intent); // <--- nuevo launcher
      }
    }
  }
  private File createImageFile() throws IOException {
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    String imageFileName = "JPEG_" + timeStamp + "_";

    File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    if (storageDir != null && !storageDir.exists()) {
      storageDir.mkdirs();
    }
    File image = File.createTempFile(
      imageFileName,
      ".jpg",
      storageDir
    );

    currentPhotoPath = image.getAbsolutePath();
    Log.d("RutaImagen", "Ruta de la imagen: " + currentPhotoPath);
    return image;
  }
  private void logout() {
    FirebaseAuth.getInstance().signOut();

    Intent intent = new Intent(getContext(), LoginActivity.class);
    startActivity(intent);
    requireActivity().finish();
  }

}

