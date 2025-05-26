package com.example.myapplication;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.io.File;
import java.io.InputStream;

/**
 * Actividad principal de la aplicación.
 * Gestiona la navegación mediante un Navigation Drawer y muestra
 * la información del usuario autenticado en el header del menú lateral.
 * <p>
 * Se integra con Firebase Authentication para gestionar la sesión del usuario
 * y con Firestore para obtener datos adicionales del usuario.
 */
public class MainActivity extends AppCompatActivity {
  /**
   * Configuración para la barra de aplicación (AppBar) y navegación.
   */
  private AppBarConfiguration mAppBarConfiguration;
  /**
   * Binding para acceder a las vistas definidas en activity_main.xml.
   */
  private ActivityMainBinding binding;

  /**
   * Método llamado al crear la actividad.
   * Inicializa el layout, la barra de herramientas, el Navigation Drawer y
   * configura los listeners para la navegación y cierre de sesión.
   *
   * @param savedInstanceState Estado previo de la actividad, si existe.
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    setSupportActionBar(binding.appBarMain.toolbar);

    DrawerLayout drawer = binding.drawerLayout;
    NavigationView navigationView = binding.navView;

    mAppBarConfiguration = new AppBarConfiguration.Builder(
      R.id.nav_home, R.id.nav_Incidents, R.id.nav_config, R.id.nav_share, R.id.nav_info)
      .setOpenableLayout(drawer)
      .build();
    NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
    NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
    NavigationUI.setupWithNavController(navigationView, navController);

    navigationView.setNavigationItemSelectedListener(item -> {
      if (item.getItemId() == R.id.nav_logout) {
        logoutUser();
        return true;
      } else if (item.getItemId() == R.id.nav_share) {
        showShareDialog();
        return true;
      } else {
        boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
        if (handled) {
          binding.drawerLayout.closeDrawers();
        }
        return handled;
      }
    });

    drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
      @Override
      public void onDrawerOpened(View drawerView) {
        updateNavHeader();
      }

      @Override
      public void onDrawerClosed(View drawerView) {
        // Noncompliant - method is empty
      }

      @Override
      public void onDrawerSlide(View drawerView, float slideOffset) {
        // Noncompliant - method is empty
      }

      @Override
      public void onDrawerStateChanged(int newState) {
        // Noncompliant - method is empty
      }
    });
  }

  /**
   * Cierra la sesión del usuario actual y redirige a la pantalla de login.
   */
  private void logoutUser() {
    FirebaseAuth.getInstance().signOut();

    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
    startActivity(intent);
    finish();
  }

  private void showShareDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
    builder.setTitle(getString(R.string.dialog_share_title));

    View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_share, null);
    builder.setView(dialogView);

    AlertDialog dialog = builder.create();
    dialog.show();

    TextView urlTextView = dialogView.findViewById(R.id.url_github);
    ImageButton btnWhatsapp = dialogView.findViewById(R.id.btn_whatsapp);
    ImageButton btnEmail = dialogView.findViewById(R.id.btn_email);
    ImageButton btnCopy = dialogView.findViewById(R.id.btn_copy);

    String url = urlTextView.getText().toString();

    btnWhatsapp.setOnClickListener(v -> {
      Intent intent = new Intent(Intent.ACTION_SEND);
      intent.setType("text/plain");
      intent.putExtra(Intent.EXTRA_TEXT, url);
      intent.setPackage("com.whatsapp");

      try {
        startActivity(intent);
      } catch (ActivityNotFoundException e) {
        Toast.makeText(this, getString(R.string.dialog_share_WhatsApp_error), Toast.LENGTH_SHORT).show();
      }
    });

    btnEmail.setOnClickListener(v -> {
      Intent intent = new Intent(Intent.ACTION_SENDTO);
      intent.setData(Uri.parse("mailto:"));
      intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.dialog_share_check_app));
      intent.putExtra(Intent.EXTRA_TEXT, url);

      try {
        startActivity(intent);
      } catch (ActivityNotFoundException e) {
        Toast.makeText(this, getString(R.string.dialog_share_No_App_error), Toast.LENGTH_SHORT).show();
      }
    });

    btnCopy.setOnClickListener(v -> {
      ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
      ClipData clip = ClipData.newPlainText(getString(R.string.dialog_share_github_link), url);
      clipboard.setPrimaryClip(clip);
      Toast.makeText(this, getString(R.string.dialog_share_copy_link), Toast.LENGTH_SHORT).show();
    });
  }


  /**
   * Maneja la acción del botón "Up" en la barra de navegación.
   * Navega hacia arriba en la pila de navegación usando NavController.
   *
   * @return true si la navegación "Up" fue manejada correctamente, false si se delega al comportamiento padre.
   */
  @Override
  public boolean onSupportNavigateUp() {
    NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
    return NavigationUI.navigateUp(navController, mAppBarConfiguration)
      || super.onSupportNavigateUp();
  }


  /**
   * Actualiza la información del header del Navigation Drawer con los datos del usuario actual:
   * nombre, correo y avatar.
   * Consulta los datos en Firestore y actualiza la UI.
   */
  private void updateNavHeader() {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    if (user != null) {
      View headerView = binding.navView.getHeaderView(0);

      TextView nameTextView = headerView.findViewById(R.id.nav_header_name);
      TextView emailTextView = headerView.findViewById(R.id.nav_header_email);
      ImageView imageView = headerView.findViewById(R.id.imageView);

      FirebaseFirestore db = FirebaseFirestore.getInstance();

      String userId = user.getUid();

      db.collection("users").document(userId).get()
        .addOnSuccessListener(documentSnapshot -> {
          if (documentSnapshot.exists()) {
            String name = documentSnapshot.getString("name");
            String email = documentSnapshot.getString("email");
            String imageUrl = documentSnapshot.getString("avatar");

            if (imageUrl != null && !imageUrl.isEmpty()) {
              Glide.with(headerView.getContext())
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                  @Override
                  public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    Bitmap squareBitmap = cropBitMapToSquare(resource);
                    imageView.setImageBitmap(squareBitmap);
                  }

                  @Override
                  public void onLoadCleared(@Nullable Drawable placeholder) {
                    imageView.setImageResource(R.drawable.ic_launcher_foreground);
                  }
                });
            }

            nameTextView.setText(name);
            emailTextView.setText(email);
          }
        })
        .addOnFailureListener(e -> Log.e("FIREBASE", "Error al obtener usuario", e));
    }
  }

  /**
   * Recorta un {@link Bitmap} original para obtener un bitmap cuadrado centrado.
   * La imagen resultante tendrá el lado igual al menor de los lados (ancho o alto).
   *
   * @param bitmap El {@link Bitmap} original que se desea recortar.
   * @return Un nuevo {@link Bitmap} cuadrado centrado recortado y redimensionado.
   */
  private Bitmap cropBitMapToSquare(Bitmap bitmap) {
    if (bitmap == null) return null;

    int width = bitmap.getWidth();
    int height = bitmap.getHeight();

    int newEdge = Math.min(width, height);

    int x = (width - newEdge) / 2;
    int y = (height - newEdge) / 2;

    Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, x, y, newEdge, newEdge);

    int targetSize = 200;

    return Bitmap.createScaledBitmap(croppedBitmap, targetSize, targetSize, true);
  }

}
