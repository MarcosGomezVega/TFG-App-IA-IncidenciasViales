package com.example.myapplication;

 import androidx.appcompat.app.AppCompatActivity;

 import android.content.Intent;
 import android.os.Bundle;
 import android.text.Html;
 import android.text.TextUtils;
 import android.widget.Button;
 import android.widget.EditText;
 import android.widget.ImageButton;
 import android.widget.Toast;
 import android.view.View;



 public class Login extends AppCompatActivity {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     setContentView(R.layout.activity_login);

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

   public void push_LoginButton(View v) {
       EditText editText_email = findViewById(R.id.emailLogin);
       EditText editText_password = findViewById(R.id.passwordLogin);

       String email = editText_email.getText().toString();
       String password = editText_password.getText().toString();

       if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
           Toast.makeText(Login.this, Html.fromHtml("<font color='#FF0000'><b>" + getString(R.string.gaps_empty) + "</b></font>"), Toast.LENGTH_SHORT).show();
       } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
           Toast.makeText(Login.this, Html.fromHtml("<font color='#FF0000'><b>" + getString(R.string.invalid_email) + "</b></font>"), Toast.LENGTH_SHORT).show();
       } else {
           Intent intent = new Intent(Login.this, MainActivity.class);
           startActivity(intent);
       }
   }

   public void push_CreateAccountButton(View v) {
       Intent intent = new Intent(Login.this,CreateAccount.class);
       startActivity(intent);
   }
 }
