package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Login activity that allows the user to log with his email, and his password
 *
 * @author Marcos Gomez Vega
 * @version 1.0
 */
public class LoginActivity extends AppCompatActivity{

  private  Button buttonLogin;
  private  Button buttonCreateAccount;
  private FirebaseAuth mAuth;


  /**
   * Called when the activity is starting.
   * Initializes UI components and sets click listeners for login and create account buttons.
   *
   * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
   *                           this Bundle contains the most recent data. Otherwise, it is null.
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    mAuth = FirebaseAuth.getInstance();

    buttonLogin = findViewById(R.id.buttonLogin);
    buttonCreateAccount = findViewById(R.id.buttonCreateAccount);

    buttonLogin.setOnClickListener(v -> push_LoginButton(v));
    buttonCreateAccount.setOnClickListener(v -> push_CreateAccountButton(v));
  }

  /**
   * Called after onCreate() or after the activity has been stopped and is restarting.
   * Checks if there are already registered users; if so, redirects to the main activity.
   */
  @Override
  protected void onStart() {
    super.onStart();

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    Log.d("FIREBASE","El usairo activo es: " + currentUser);
    if (currentUser != null) {
      startActivity(new Intent(LoginActivity.this, MainActivity.class));
      finish();
    }
  }

  /**
   * Handles the login button click.
   * Validates the email and password, shows error messages if needed, and logs in if credentials are valid.
   *
   * @param v the view that was clicked
   */
  public void push_LoginButton(View v) {
    EditText editText_email = findViewById(R.id.emailLogin);
    EditText editText_password = findViewById(R.id.passwordLogin);

    String email = editText_email.getText().toString().trim();
    String password = editText_password.getText().toString().trim();

    if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
      Toast.makeText(LoginActivity.this, Html.fromHtml("<font color='#FF0000'><b>" + getString(R.string.gaps_empty) + "</b></font>"), Toast.LENGTH_SHORT).show();
    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
      Toast.makeText(LoginActivity.this, Html.fromHtml("<font color='#FF0000'><b>" + getString(R.string.invalid_email) + "</b></font>"), Toast.LENGTH_SHORT).show();
    } else {
      mAuth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(this, task -> {
          if (task.isSuccessful()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
          } else {
            Toast.makeText(LoginActivity.this, Html.fromHtml("<font color='#FF0000'><b>" + getString(R.string.email_passwd_dont_match) + "</b></font>"), Toast.LENGTH_SHORT).show();
          }
        });
    }
  }


  /**
   * Handles the create account button click.
   * Navigates the user to the account creation screen.
   *
   * @param v the view that was clicked
   */
  public void push_CreateAccountButton(View v) {
    Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
    startActivity(intent);
  }

}
