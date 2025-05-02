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

import com.example.myapplication.database.DBManager;

/**
 * Activity that handles user account creation.
 * It validates user input (username, email, password) and stores the data in the database if valid.
 * Shows error messages if there are any validation issues or if the user fails to accept terms and conditions.
 *
 * @author Marcos Gómez Vega
 * @version 1.0
 */
public class CreateAccountActivity extends AppCompatActivity {

  private DBManager dbManager;
  private Button button_CreateAcount;

  /**
   * Called when the activity is starting.
   * Initializes UI components and sets the listener for the "Create Account" button.
   *
   * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
   *                           this Bundle contains the most recent data. Otherwise, it is null.
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_account);

    dbManager = new DBManager(this);

    button_CreateAcount = findViewById(R.id.btnCreateAccount);

    button_CreateAcount.setOnClickListener(v -> push_CreateAcount(v));
  }

  /**
   * Handles the "Create Account" button click.
   * It validates the user input and attempts to insert the new account in the database.
   * Displays relevant error messages in case of invalid input or missing acceptance of terms.
   *
   * @param v the view that was clicked (the "Create Account" button).
   */
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
      Toast.makeText(CreateAccountActivity.this, Html.fromHtml("<font color='#FF0000'><b> " + getString(R.string.gaps_empty) + " </b></font>"), Toast.LENGTH_SHORT).show();
    }
    else if (!password.equals(confirmPassword)) {
      Toast.makeText(CreateAccountActivity.this, Html.fromHtml("<font color='#FF0000'><b>" + getString(R.string.passwd_dont_match) + "</b></font>"), Toast.LENGTH_SHORT).show();
    }
    else if (!checkBox.isChecked()) {
      AlertDialog.Builder builder = new AlertDialog.Builder(CreateAccountActivity.this);
      builder.setTitle(Html.fromHtml("<font color='#FF0000'>" + getString(R.string.error) + "</font>"));
      builder.setMessage(getString(R.string.accept_conditions));
      builder.setPositiveButton(Html.fromHtml("<font color='#6750A4'>" + getString(R.string.acept) + "</font>"), null);
      builder.show();
    }
    else {
      boolean insertado = dbManager.insertarUsuario(user_name, email, password);

      if (insertado) {
        Toast.makeText(CreateAccountActivity.this, Html.fromHtml("<font color='#00C853'><b>"+getString(R.string.acounbt_create_well)+"</b></font>"), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
      }
      else {
        Toast.makeText(CreateAccountActivity.this, Html.fromHtml("<font color='#FF0000'><b>Error: "+getString(R.string.email_is_login)+"</b></font>"), Toast.LENGTH_SHORT).show();
      }
    }
  }
}
