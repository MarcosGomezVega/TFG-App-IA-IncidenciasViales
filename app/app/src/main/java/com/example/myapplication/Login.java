package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import android.util.Log;

import com.example.myapplication.database.DBManager;




public class Login extends AppCompatActivity {

  private DBManager dbManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    dbManager = new DBManager(this);


    Button buttonLogin = findViewById(R.id.buttonLogin);
    Button buttonCreateAccount = findViewById(R.id.buttonCreateAccount);

    buttonLogin.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        push_LoginButton(v);
      }
    });

    buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        push_CreateAccountButton(v);
      }
    });
  }

  @Override
  protected void onStart() {
    super.onStart();

    if (dbManager.hayUsuariosRegistrados()) {
      Intent intent = new Intent(Login.this, MainActivity.class);
      startActivity(intent);
      finish();
    }
  }
  public void push_LoginButton(View v) {
    EditText editText_email = findViewById(R.id.emailLogin);
    EditText editText_password = findViewById(R.id.passwordLogin);

    String email = editText_email.getText().toString();
    String password = editText_password.getText().toString();

    Log.d("LoginActivity", "Email ingresado: " + email);  // Verificar el email ingresado
    Log.d("LoginActivity", "Contraseña ingresada: " + password);  // Verificar la contraseña ingresada

    if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
      Toast.makeText(Login.this, Html.fromHtml("<font color='#FF0000'><b>" + getString(R.string.gaps_empty) + "</b></font>"), Toast.LENGTH_SHORT).show();
    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
      Toast.makeText(Login.this, Html.fromHtml("<font color='#FF0000'><b>" + getString(R.string.invalid_email) + "</b></font>"), Toast.LENGTH_SHORT).show();
    } else {
      boolean validacionUser = dbManager.validarUsuario(email, password);

      if (validacionUser) {
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
        finish();
      } else {
        Toast.makeText(Login.this, Html.fromHtml("<font color='#FF0000'><b>" + getString(R.string.email_passwd_dont_match) + "</b></font>"), Toast.LENGTH_SHORT).show();
      }
    }
  }

  public void push_CreateAccountButton(View v) {
    Intent intent = new Intent(Login.this, CreateAccount.class);
    startActivity(intent);
  }


}
