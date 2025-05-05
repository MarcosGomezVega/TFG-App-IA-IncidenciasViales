package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ActionBar extends AppCompatActivity {
  protected ImageButton backButton;
  protected ImageButton menuButton;

  protected void setCustomActionBar(String titleText) {
    androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayShowCustomEnabled(true);
      actionBar.setDisplayShowTitleEnabled(false);

      LayoutInflater inflater = LayoutInflater.from(this);
      View customView = inflater.inflate(R.layout.custom_action_bar, null);
      actionBar.setCustomView(customView);

      TextView title = customView.findViewById(R.id.action_bar_title);
      backButton = customView.findViewById(R.id.button_back);
      menuButton = customView.findViewById(R.id.button_menu);

      if (title != null) {
        title.setText(titleText);
      }

      if (backButton != null) {
        backButton.setOnClickListener(v -> {
          onBackPressed();
        });
      }

      if (menuButton != null) {
        menuButton.setOnClickListener(v -> {

        });
      }
    }
  }
}
