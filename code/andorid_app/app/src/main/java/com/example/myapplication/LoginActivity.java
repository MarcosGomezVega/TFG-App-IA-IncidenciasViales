package com.example.myapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Actividad de inicio de sesión que permite al usuario iniciar sesión con su correo electrónico y contraseña
 * o mediante cuenta de Google.
 * Aplica el idioma preferido del usuario tras iniciar sesión correctamente.
 *
 * @author Marcos Gomez Vega
 * @version 1.1
 */
public class LoginActivity extends AppCompatActivity {

  private GoogleSignInClient googleSignInClient;
  private FirebaseAuth mAuth;
  private ActivityResultLauncher<Intent> googleSignInLauncher;


  /**
   * Llamado cuando la actividad está comenzando.
   * Inicializa los componentes de la interfaz de usuario y establece los listeners para los botones de inicio de sesión y creación de cuenta.
   *
   * @param savedInstanceState Si la actividad está siendo reinicializada después de haber sido cerrada previamente,
   *                           este Bundle contiene los datos más recientes. De lo contrario, es nulo.
   */

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
      .requestIdToken(getString(R.string.default_web_client_id))
      .requestEmail()
      .build();

    googleSignInLauncher = registerForActivityResult(
      new ActivityResultContracts.StartActivityForResult(),
      result -> {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
          handleGoogleSignInResult(result.getData());
        }
      }
    );

    googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    mAuth = FirebaseAuth.getInstance();

    Button buttonLogin = findViewById(R.id.buttonLogin);
    Button buttonCreateAccount = findViewById(R.id.buttonCreateAccount);
    Button imageButtonGoogle = findViewById(R.id.buttonGoogle);

    buttonLogin.setOnClickListener(v -> pushLoginButton(mAuth));
    buttonCreateAccount.setOnClickListener(this::pushCreateAccountButton);
    imageButtonGoogle.setOnClickListener(v -> pushBtnGoogle());

  }

  /**
   * Inicia el proceso de inicio de sesión con Google.
   * Lanza la actividad proporcionada por GoogleSignInClient para que el usuario seleccione una cuenta.

   */
  private void pushBtnGoogle() {
    Intent intent = googleSignInClient.getSignInIntent();
    googleSignInLauncher.launch(intent);
  }

  /**
   * Procesa el resultado del intento de inicio de sesión con Google.
   * Extrae la cuenta de Google del Intent y procede con la autenticación de Firebase.
   *
   * @param data Intent que contiene los datos de inicio de sesión de Google.
   */
  private void handleGoogleSignInResult(Intent data) {
    Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
    try {
      GoogleSignInAccount account = accountTask.getResult(ApiException.class);
      firebaseAuthWithGoogleAcount(account);
    } catch (Exception e) {
      e.printStackTrace();
      Toast.makeText(this, "Error en el inicio de sesión con Google", Toast.LENGTH_SHORT).show();
    }
  }

  /**
   * Autentica al usuario con Firebase usando las credenciales de Google.
   * Si es un nuevo usuario, lo registra en Firestore. Si ya existe, carga su idioma.
   *
   * @param account Cuenta de Google autenticada.
   */
  private void firebaseAuthWithGoogleAcount(GoogleSignInAccount account) {
    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
    mAuth.signInWithCredential(credential)
      .addOnSuccessListener(authResult -> {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = firebaseUser.getUid();

        if (authResult.getAdditionalUserInfo().isNewUser()) {

          String name = firebaseUser.getDisplayName();
          String email = firebaseUser.getEmail();
          String photoUrl = (firebaseUser.getPhotoUrl() != null) ? firebaseUser.getPhotoUrl().toString() : "";

          String langCode = Locale.getDefault().getLanguage();
          if (!langCode.equals("es")) {
            langCode = "en";
          }

          Map<String, Object> user = new HashMap<>();
          user.put("name", name);
          user.put("email", email);
          user.put("lastLogin", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
          user.put("avatar", photoUrl);
          user.put("language", langCode);

          String finalLangCode = langCode;
          db.collection("users").document(uid).set(user)
            .addOnSuccessListener(aVoid -> {
              setLocal(this, finalLangCode);

              Toast.makeText(this, "Cuenta de Google creada con éxito", Toast.LENGTH_SHORT).show();
              startActivity(new Intent(this, MainActivity.class));
              finish();
            });

        } else {

          loadLanguageAndLaunchMain(firebaseUser);
        }
      })
      .addOnFailureListener(e -> Toast.makeText(this, "Fallo en autenticación con Google", Toast.LENGTH_SHORT).show());
  }

  /**
   * Aplica la configuración regional (idioma) para la actividad.
   *
   * @param activity Actividad actual
   * @param langCode Código del idioma ("es", "en", etc.)
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
   * Verifica si hay un usuario ya autenticado y lo redirige si es necesario, aplicando el idioma guardado.
   */
  @Override
  protected void onStart() {
    super.onStart();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    if (currentUser != null) {
      loadLanguageAndLaunchMain(currentUser);

    }
  }

  /**
   * Maneja el clic del botón de inicio de sesión.
   * Valida el correo electrónico y la contraseña, muestra mensajes de error si es necesario, e inicia sesión si las credenciales son válidas.
   *
   * @param mAuth Autentificación
   */
  private void pushLoginButton(FirebaseAuth mAuth) {
    EditText editTextEmail = findViewById(R.id.emailLogin);
    EditText editTextPassword = findViewById(R.id.passwordLogin);

    String email = editTextEmail.getText().toString().trim();
    String password = editTextPassword.getText().toString().trim();

    if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
      Toast.makeText(LoginActivity.this, getString(R.string.gaps_empty), Toast.LENGTH_SHORT).show();
    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
      Toast.makeText(LoginActivity.this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();

    } else {
      mAuth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(this, task -> {
          if (task.isSuccessful()) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
              loadLanguageAndLaunchMain(user);
            }
          } else {
            Toast.makeText(LoginActivity.this, getString(R.string.email_passwd_dont_match), Toast.LENGTH_SHORT).show();

          }
        });
    }
  }



  /**
   * Maneja el clic del botón de crear cuenta.
   * Navega al usuario hacia la pantalla de creación de cuenta.
   *
   * @param v la vista que fue clicada
   */
  private void pushCreateAccountButton(View v) {
    Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
    startActivity(intent);
  }

  /**
   * Carga el idioma guardado del usuario, actualiza lastLogin,
   * aplica el idioma y lanza MainActivity.
   *
   * @param user Usuario autenticado de Firebase
   */
  private void loadLanguageAndLaunchMain(FirebaseUser user) {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uid = user.getUid();

    String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    db.collection("users").document(uid).update("lastLogin", currentDateTime);

    db.collection("users").document(uid).get()
      .addOnSuccessListener(documentSnapshot -> {
        String lang = "es";
        if (documentSnapshot.exists()) {
          String savedLang = documentSnapshot.getString("language");
          if (savedLang != null && (savedLang.equals("es") || savedLang.equals("en"))) {
            lang = savedLang;
          }
        }

        setLocal(this, lang);
        startActivity(new Intent(this, MainActivity.class));
        finish();
      })
      .addOnFailureListener(e -> {
        setLocal(this, "es");
        startActivity(new Intent(this, MainActivity.class));
        finish();
      });
  }

}
