package com.example.myapplication.ui.config.manager;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.FileProvider;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AvatarManager {

  private final Context context;
  private final LayoutInflater inflater;
  private String currentPhotoPath;
  private ImageView imgviewAvatar;
  private AlertDialog dialog;
  private final ActivityResultLauncher<Uri> takePictureLauncher;
  private final ActivityResultLauncher<String> pickImageLauncher;

  public AvatarManager(Context context,LayoutInflater inflater,ActivityResultLauncher<Uri> takePictureLauncher,ActivityResultLauncher<String> pickImageLauncher) {
    this.context = context;
    this.inflater = inflater;
    this.takePictureLauncher = takePictureLauncher;
    this.pickImageLauncher = pickImageLauncher;
  }

  /**
   * Muestra un diálogo personalizado para que el usuario pueda cambiar su avatar,
   * con opciones para seleccionar una imagen desde la galería o tomar una foto.
   */
  public void showChangeAvatarDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
    builder.setTitle("Cambiar Avatar");

    View view = inflater.inflate(R.layout.dialog_change_avatar, null);
    imgviewAvatar = view.findViewById(R.id.imageviewAvatar);
    Button btnGalery = view.findViewById(R.id.btnGallery);
    Button btnCamera = view.findViewById(R.id.btnCamera);

    btnGalery.setOnClickListener(v -> pickImageFromGallery());
    btnCamera.setOnClickListener(v -> openCamera());

    builder.setPositiveButton(context.getString(R.string.change), (dialog, which) -> uploadAvatar());
    builder.setNegativeButton(context.getString(R.string.button_cancel), (dialog, which) -> dialog.dismiss());

    dialog = builder.create();
    dialog.setView(view);
    dialog.show();
  }

  /**
   * Actualiza la imagen de avatar del usuario en Firestore usando la ruta
   * actual almacenada en `currentPhotoPath`.
   */
  private void uploadAvatar() {
    if (currentPhotoPath == null) {
      Toast.makeText(context, "No se ha seleccionado una imagen.", Toast.LENGTH_SHORT).show();
      return;
    }

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
          .addOnSuccessListener(aVoid -> Toast.makeText(context, "Avatar actualizado", Toast.LENGTH_SHORT).show());

      })
    ).addOnFailureListener(e ->
      Toast.makeText(context, context.getString(R.string.error_upload_photo), Toast.LENGTH_SHORT).show()
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
        Uri photoURI = FileProvider.getUriForFile(context, "com.example.myapplication.fileprovider", photoFile);
        currentPhotoPath = photoFile.getAbsolutePath();
        takePictureLauncher.launch(photoURI);
      }
    } catch (IOException ex) {
      Toast.makeText(context, Html.fromHtml("<font color='#FF0000'><b>" + context.getString(R.string.error_creating_image) + "</b></font>", Html.FROM_HTML_MODE_LEGACY), Toast.LENGTH_SHORT).show();
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
    File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    if (storageDir != null && !storageDir.exists()) {
      storageDir.mkdirs();
    }
    File image = File.createTempFile(imageFileName, ".jpg", storageDir);
    currentPhotoPath = image.getAbsolutePath();
    return image;
  }

  public void handleCameraResult(boolean success) {
    if (success) {
      Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
      if (bitmap != null && imgviewAvatar != null) {
        imgviewAvatar.setImageBitmap(bitmap);
      }
    }
  }

  public void handleGalleryResult(Uri uri) {
    if (uri != null && imgviewAvatar != null) {
      imgviewAvatar.setImageURI(uri);
      File localFile = copyContentUriToLocal(uri);
      if (localFile != null) {
        currentPhotoPath = localFile.getAbsolutePath();
      }
    }
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
    File localFile = new File(context.getFilesDir(), "avatar.jpg");
    try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
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

}
