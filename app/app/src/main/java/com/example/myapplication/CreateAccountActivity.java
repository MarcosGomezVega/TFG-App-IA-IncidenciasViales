package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {

  private Button button_CreateAcount;
  private FirebaseAuth mAuth;
  private FirebaseFirestore db;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_account);

    mAuth = FirebaseAuth.getInstance();
    db = FirebaseFirestore.getInstance();

    button_CreateAcount = findViewById(R.id.btnCreateAccount);

    button_CreateAcount.setOnClickListener(v -> push_CreateAcount(v));
  }

  public void push_CreateAcount(View v) {

    EditText editTextUsr = findViewById(R.id.editTextUsrNombre);
    EditText editTextEmail = findViewById(R.id.editTextEmail);
    EditText passwordField = findViewById(R.id.editTextPassword);
    EditText confirmPasswordField = findViewById(R.id.editTextConfirmPassword);
    CheckBox checkBox = findViewById(R.id.checkBox);

    String user_name = editTextUsr.getText().toString();
    String email = editTextEmail.getText().toString();
    String password = passwordField.getText().toString();
    String confirmPassword = confirmPasswordField.getText().toString();

    if (TextUtils.isEmpty(user_name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
      Toast.makeText(this, Html.fromHtml("<font color='#FF0000'><b>" + getString(R.string.gaps_empty) + "</b></font>"), Toast.LENGTH_SHORT).show();
    } else if (!password.equals(confirmPassword)) {
      Toast.makeText(this, Html.fromHtml("<font color='#FF0000'><b>" + getString(R.string.passwd_dont_match) + "</b></font>"), Toast.LENGTH_SHORT).show();
    } else if (!checkBox.isChecked()) {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setTitle(Html.fromHtml("<font color='#FF0000'>" + getString(R.string.error) + "</font>"));
      builder.setMessage(getString(R.string.accept_conditions));
      builder.setPositiveButton(Html.fromHtml("<font color='#6750A4'>" + getString(R.string.acept) + "</font>"), null);
      builder.show();
    } else {
      Log.d("FIREBASE", "Email actual:" +email);
      Log.d("FIREBASE", "Passwd actual:" +password);
      mAuth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(task -> {
          if (task.isSuccessful()) {
            String uid = mAuth.getCurrentUser().getUid();


            Map<String, Object> user = new HashMap<>();
            user.put("nombre", user_name);
            user.put("email", email);
            user.put("lastLogin", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
            user.put("avatar", "");

            db.collection("users").document(uid).set(user).addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Cuenta creada correctamente", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
              }).addOnFailureListener(e -> {
                Toast.makeText(this, "Error al guardar usuario en Firestore", Toast.LENGTH_SHORT).show();
              });

          } else {
            Toast.makeText(this, "Error al crear cuenta: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
          }
        });
    }
  }
}
