package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
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


public class MainActivity extends AppCompatActivity {

  private AppBarConfiguration mAppBarConfiguration;
  private ActivityMainBinding binding;


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

    navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_logout) {
          logoutUser();
          return true;
        } else {
          boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
          if (handled) {
            DrawerLayout drawer = binding.drawerLayout;
            drawer.closeDrawers();
          }
          return handled;
        }
      }
    });

    drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
      @Override
      public void onDrawerOpened(View drawerView) {
        actualizarNavHeader();
      }
      @Override
      public void onDrawerClosed(View drawerView) {}

      @Override
      public void onDrawerSlide(View drawerView, float slideOffset) {}

      @Override
      public void onDrawerStateChanged(int newState) {}
    });
  }

  private void logoutUser() {
    FirebaseAuth.getInstance().signOut();

    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
    startActivity(intent);
    finish();
  }


  @Override
  public boolean onSupportNavigateUp() {
    NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
    return NavigationUI.navigateUp(navController, mAppBarConfiguration)
      || super.onSupportNavigateUp();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.toolbar_undo, menu);
    return true;
  }

  private void actualizarNavHeader() {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    if (user != null) {
      View headerView = binding.navView.getHeaderView(0);

      TextView nombreTextView = headerView.findViewById(R.id.nav_header_nombre);
      TextView emailTextView = headerView.findViewById(R.id.nav_header_email);

      ImageView avatarImageView = headerView.findViewById(R.id.imageView);

      FirebaseFirestore db = FirebaseFirestore.getInstance();

      String userId = user.getUid();
      Log.d("FIREBASE", "UserId: " + userId );


      db.collection("users").document(userId).get()
        .addOnSuccessListener(documentSnapshot -> {
          if (documentSnapshot.exists()) {
            String nombre = documentSnapshot.getString("nombre");
            Log.d("FIREBASE", "Nombre: " + documentSnapshot.getString("nombre"));
            String email = documentSnapshot.getString("email");
            Log.d("FIREBASE", "Email: " + documentSnapshot.getString("email"));
            String avatarUrl = documentSnapshot.getString("avatar");

            nombreTextView.setText(nombre);
            emailTextView.setText(email);
          }
        })
        .addOnFailureListener(e -> {
          Log.e("Firebase", "Error al obtener los datos del usuario", e);
        });
    } else {
      Log.e("Firebase", "No hay usuario autenticado");
    }
  }
}
