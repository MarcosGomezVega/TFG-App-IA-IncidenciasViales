package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.view.View;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.internal.GoogleSignInOptionsExtensionParcelable;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Actividad de inicio de sesión que permite al usuario iniciar sesión con su correo electrónico y contraseña.
 *
 * @author Marcos Gomez Vega
 * @version 1.0
 */
public class LoginActivity extends AppCompatActivity {

  private static final String HTML_RED_BOLD_OPEN = "<font color='#FF0000'><b>";
  private static final String HTML_BOLD_CLOSE = "</b></font>";
  private static final int RC_SIG_IN = 100;
  private GoogleSignInClient googleSignInClient;
  private FirebaseAuth mAuth;


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

    googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);


    mAuth = FirebaseAuth.getInstance();

    Button buttonLogin = findViewById(R.id.buttonLogin);
    Button buttonCreateAccount = findViewById(R.id.buttonCreateAccount);
    ImageButton imageButtonFacebook = findViewById(R.id.buttonFacebook);
    ImageButton imageButtonGoogle = findViewById(R.id.buttonGoogle);


    buttonLogin.setOnClickListener(v -> pushLoginButton(v, mAuth));
    buttonCreateAccount.setOnClickListener(this::pushCreateAccountButton);
    imageButtonGoogle.setOnClickListener(v ->  pushBtnGoogle());
    imageButtonFacebook.setOnClickListener(v -> pushLoginFacebook());


  }

  private void pushBtnGoogle(){
    Intent intent = googleSignInClient.getSignInIntent();
    startActivityForResult(intent, RC_SIG_IN);
  }
  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == RC_SIG_IN) {
      Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
      try {
        GoogleSignInAccount account = accountTask.getResult(ApiException.class);
        firebaseAuthWithGoogleAcount(account);
      } catch (Exception e) {

      }
    }
  }

  private void firebaseAuthWithGoogleAcount(GoogleSignInAccount account) {

    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
    mAuth.signInWithCredential(credential)
      .addOnSuccessListener(authResult -> {

        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (authResult.getAdditionalUserInfo().isNewUser()) {

          String uid = firebaseUser.getUid();
          String name = firebaseUser.getDisplayName();
          String email = firebaseUser.getEmail();
          String photoUrl = (firebaseUser.getPhotoUrl() != null) ? firebaseUser.getPhotoUrl().toString() : "";

          Map<String, Object> user = new HashMap<>();
          user.put("name", name);
          user.put("email", email);
          user.put("lastLogin", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
          user.put("avatar", photoUrl);

          FirebaseFirestore db = FirebaseFirestore.getInstance();

          db.collection("users").document(uid).set(user)
            .addOnSuccessListener(aVoid -> {
              Toast.makeText(this, "Cuenta de Google creada con éxito", Toast.LENGTH_SHORT).show();
              startActivity(new Intent(this, MainActivity.class));
              finish();
            });

        } else {
          startActivity(new Intent(this, MainActivity.class));
          finish();
        }

      })
      .addOnFailureListener(e -> {
        Toast.makeText(this, Html.fromHtml(HTML_RED_BOLD_OPEN + "Fallo en autenticación con Google" + HTML_BOLD_CLOSE, Html.FROM_HTML_MODE_LEGACY), Toast.LENGTH_SHORT).show();
      });
  }

  /**
   * Llamado después de onCreate() o después de que la actividad haya sido detenida y se esté reiniciando.
   * Verifica si ya hay usuarios registrados; si es así, redirige a la actividad principal.
   */
  @Override
  protected void onStart() {
    super.onStart();

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    if (currentUser != null) {
      startActivity(new Intent(LoginActivity.this, MainActivity.class));
      finish();
    }
  }

  /**
   * Maneja el clic del botón de inicio de sesión.
   * Valida el correo electrónico y la contraseña, muestra mensajes de error si es necesario, e inicia sesión si las credenciales son válidas.
   *
   * @param v la vista que fue clicada
   */
  private void pushLoginButton(View v, FirebaseAuth mAuth) {
    EditText editTextEmail = findViewById(R.id.emailLogin);
    EditText editTextPassword = findViewById(R.id.passwordLogin);

    String email = editTextEmail.getText().toString().trim();
    String password = editTextPassword.getText().toString().trim();

    if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
      Toast.makeText(LoginActivity.this, Html.fromHtml(HTML_RED_BOLD_OPEN + getString(R.string.gaps_empty) + HTML_BOLD_CLOSE, Html.FROM_HTML_MODE_LEGACY), Toast.LENGTH_SHORT).show();
    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
      Toast.makeText(LoginActivity.this, Html.fromHtml(HTML_RED_BOLD_OPEN + getString(R.string.invalid_email) + HTML_BOLD_CLOSE, Html.FROM_HTML_MODE_LEGACY), Toast.LENGTH_SHORT).show();
    } else {
      mAuth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(this, task -> {
          if (task.isSuccessful()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
          } else {
            Toast.makeText(LoginActivity.this, Html.fromHtml(HTML_RED_BOLD_OPEN + getString(R.string.email_passwd_dont_match) + HTML_BOLD_CLOSE, Html.FROM_HTML_MODE_LEGACY), Toast.LENGTH_SHORT).show();
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


  private void pushLoginFacebook() {

  }


}
