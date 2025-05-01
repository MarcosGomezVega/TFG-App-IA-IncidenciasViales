package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;

public class CreateAccount extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_account);

    Button button_CreateAcount = findViewById(R.id.btnCreateAccount);

    button_CreateAcount.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        push_CreateAcount(v);
      }
    });
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
      Toast.makeText(CreateAccount.this, Html.fromHtml("<font color='#FF0000'><b> " + getString(R.string.gaps_empty) + " </b></font>"), Toast.LENGTH_SHORT).show();
    } else if (!password.equals(confirmPassword)) {
      Toast.makeText(CreateAccount.this, Html.fromHtml("<font color='#FF0000'><b>" + getString(R.string.passwd_dont_match) + "</b></font>"), Toast.LENGTH_SHORT).show();
    } else if (!checkBox.isChecked()) {
      AlertDialog.Builder builder = new AlertDialog.Builder(CreateAccount.this);
      builder.setTitle(Html.fromHtml("<font color='#FF0000'>" + getString(R.string.error) + "</font>"));
      builder.setMessage(getString(R.string.accept_conditions));
      builder.setPositiveButton(Html.fromHtml("<font color='#6750A4'>" + getString(R.string.acept) + "</font>"), null);
      builder.show();
    } else {
      Intent intent = new Intent(CreateAccount.this, Login.class);
      startActivity(intent);
    }
  }
}
