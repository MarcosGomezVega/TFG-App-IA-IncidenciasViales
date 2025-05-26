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

  private static final String HTML_RED_BOLD_OPEN = "<font color='#FF0000'><b>";
  private static final String HTML_BOLD_CLOSE = "</b></font>";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_account);

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Button buttonCreateAcount = findViewById(R.id.btnCreateAccount);

    buttonCreateAcount.setOnClickListener(v -> pushCreateAcount(mAuth, db));
  }

  public void pushCreateAcount(FirebaseAuth mAuth, FirebaseFirestore db) {

    EditText editTextUsr = findViewById(R.id.editTextUsrNombre);
    EditText editTextEmail = findViewById(R.id.editTextEmail);
    EditText passwordField = findViewById(R.id.editTextPassword);
    EditText confirmPasswordField = findViewById(R.id.editTextConfirmPassword);
    CheckBox checkBox = findViewById(R.id.checkBox);

    String userName = editTextUsr.getText().toString();
    String email = editTextEmail.getText().toString();
    String password = passwordField.getText().toString();
    String confirmPassword = confirmPasswordField.getText().toString();

    if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
      Toast.makeText(this, Html.fromHtml(HTML_RED_BOLD_OPEN + getString(R.string.gaps_empty) + HTML_BOLD_CLOSE, Html.FROM_HTML_MODE_LEGACY), Toast.LENGTH_SHORT).show();
    } else if (!password.equals(confirmPassword)) {
      Toast.makeText(this, Html.fromHtml(HTML_RED_BOLD_OPEN + getString(R.string.passwd_dont_match) + HTML_BOLD_CLOSE, Html.FROM_HTML_MODE_LEGACY), Toast.LENGTH_SHORT).show();
    } else if (!checkBox.isChecked()) {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setTitle(Html.fromHtml(HTML_RED_BOLD_OPEN + getString(R.string.error) + "</font>", Html.FROM_HTML_MODE_LEGACY));
      builder.setMessage(getString(R.string.accept_conditions));
      builder.setPositiveButton(Html.fromHtml("<font color='#6750A4'>" + getString(R.string.acept) + "</font>", Html.FROM_HTML_MODE_LEGACY), null);
      builder.show();
    } else {
      mAuth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(task -> {
          if (task.isSuccessful()) {
            String uid = mAuth.getCurrentUser().getUid();

            Map<String, Object> user = new HashMap<>();
            user.put("name", userName);
            user.put("email", email);
            user.put("lastLogin", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
            user.put("avatar", "");

            db.collection("users").document(uid).set(user).addOnSuccessListener(aVoid -> {
              Toast.makeText(this, getString(R.string.acount_create_well), Toast.LENGTH_SHORT).show();
              startActivity(new Intent(this, LoginActivity.class));
              finish();
            }).addOnFailureListener(e ->
              Toast.makeText(this, Html.fromHtml(HTML_RED_BOLD_OPEN + getString(R.string.error_saving_user) + HTML_BOLD_CLOSE, Html.FROM_HTML_MODE_LEGACY), Toast.LENGTH_SHORT).show()
            );

          } else {
            Toast.makeText(this, Html.fromHtml(HTML_RED_BOLD_OPEN + getString(R.string.error_create_Acount) + task.getException().getMessage() + HTML_BOLD_CLOSE, Html.FROM_HTML_MODE_LEGACY), Toast.LENGTH_SHORT).show();
          }
        });
    }
  }
}
