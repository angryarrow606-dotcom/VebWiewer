package com.MeawCreates.VebWiewer;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
  EditText url;

  @Override protected void onCreate(Bundle b) {
    super.onCreate(b);
    setContentView(R.layout.activity_main);
    if (getSupportActionBar() != null) getSupportActionBar().hide();
    getWindow().setStatusBarColor(Color.parseColor("#061826"));
    getWindow().setNavigationBarColor(Color.parseColor("#010509"));

    int logoId = getResources().getIdentifier("logo", "drawable", getPackageName());
    if (logoId != 0) {
      ImageView logo = findViewById(R.id.logo);
      logo.setImageResource(logoId);
      logo.setVisibility(View.VISIBLE);
    }

    GradientDrawable g = new GradientDrawable();
    g.setColor(0x66000000);
    g.setCornerRadius(80f);
    g.setStroke(2, 0x55FFFFFF);
    findViewById(R.id.bar).setBackground(g);

    url = findViewById(R.id.url);
    findViewById(R.id.go).setOnClickListener(v -> go());
    url.setOnEditorActionListener((v, a, e) -> { go(); return true; });
    findViewById(R.id.settings).setOnClickListener(
      v -> startActivity(new Intent(this, SettingsActivity.class)));
  }

  @Override protected void onResume() {
    super.onResume();
    WaterSound.start(this);
  }

  @Override protected void onPause() {
    super.onPause();
    WaterSound.stop();
  }

  void go() {
    String t = url.getText().toString().trim();
    if (t.isEmpty()) return;
    String u;
    if (t.startsWith("http://") || t.startsWith("https://")) u = t;
    else if (t.contains(" ") || !t.contains("."))
      u = "https://www.google.com/search?q=" + Uri.encode(t);
    else u = "https://" + t;
    Intent i = new Intent(this, BrowserActivity.class);
    i.putExtra("url", u);
    startActivity(i);
  }
}