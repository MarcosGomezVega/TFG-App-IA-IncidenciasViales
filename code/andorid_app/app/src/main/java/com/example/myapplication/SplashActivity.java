package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.databinding.ActivitySplashBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

  private ActivitySplashBinding binding;
  private FirebaseAuth mAuth;
  private FirebaseFirestore db;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivitySplashBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    if (getSupportActionBar() != null) {
      getSupportActionBar().hide();
    }

    // Cargar el GIF con Glide
    Glide.with(this)
      .asGif()
      .load(R.drawable.spinnercharging)
      .into(binding.loadingGif);

    mAuth = FirebaseAuth.getInstance();
    db = FirebaseFirestore.getInstance();

    FirebaseUser user = mAuth.getCurrentUser();

    if (user != null) {
      db.collection("users")
        .document(user.getUid())
        .get()
        .addOnSuccessListener(documentSnapshot -> {
          if (documentSnapshot.exists()) {
            String lang = documentSnapshot.getString("language");

            if (lang != null && (lang.equals("es") || lang.equals("en"))) {
              setLocal(this, lang);
            }
          }
          goToMain();
        })
        .addOnFailureListener(e -> goToMain());
    } else {
      String deviceLang = Locale.getDefault().getLanguage();
      if (!deviceLang.equals("es") && !deviceLang.equals("en")) {
        deviceLang = "es";
      }
      setLocal(this, deviceLang);
      goToLogin();
    }
  }

  public void setLocal(Activity activity, String langCode) {
    Locale locale = new Locale(langCode);
    Locale.setDefault(locale);

    Resources resources = activity.getResources();
    Configuration configuration = resources.getConfiguration();
    configuration.setLocale(locale);
    resources.updateConfiguration(configuration, resources.getDisplayMetrics());
  }

  private void goToLogin() {
    startActivity(new Intent(this, LoginActivity.class));
    finish();
  }

  private void goToMain() {
    startActivity(new Intent(this, MainActivity.class));
    finish();
  }
}
