package com.example.myapplication;

  import android.os.Bundle;
  import android.widget.TextView;
  import androidx.appcompat.app.AppCompatActivity;

public class ActionBar extends AppCompatActivity {

  protected void setCustomActionBar(String titleText) {
    androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayShowCustomEnabled(true);
      actionBar.setDisplayShowTitleEnabled(false);
      actionBar.setCustomView(R.layout.custom_action_bar);
      TextView title = findViewById(R.id.action_bar_title);
      if (title != null) {
        title.setText(titleText);
      }
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
}
