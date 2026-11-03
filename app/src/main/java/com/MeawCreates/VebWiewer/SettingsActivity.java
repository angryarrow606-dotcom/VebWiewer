package com.MeawCreates.VebWiewer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
  @Override protected void onCreate(Bundle b) {
    super.onCreate(b);
    setTitle("Settings");

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);

    Switch sound = new Switch(this);
    sound.setText("Water sound on home screen");
    sound.setPadding(32, 32, 32, 16);
    sound.setChecked(WaterSound.enabled(this));
    sound.setOnCheckedChangeListener((x, on) -> WaterSound.setEnabled(this, on));

    Switch ads = new Switch(this);
    ads.setText("Ad blocker and popup blocker");
    ads.setPadding(32, 16, 32, 32);
    ads.setChecked(getSharedPreferences("s", 0).getBoolean("adblock", true));
    ads.setOnCheckedChangeListener((x, on) ->
        getSharedPreferences("s", 0).edit().putBoolean("adblock", on).apply());

    Button clear = new Button(this);
    clear.setText("Clear history");
    ListView list = new ListView(this);

    root.addView(sound);
    root.addView(ads);
    root.addView(clear);
    root.addView(list, new LinearLayout.LayoutParams(-1, 0, 1f));
    setContentView(root);

    ArrayAdapter<String> ad = new ArrayAdapter<>(this,
        android.R.layout.simple_list_item_1, History.get(this));
    list.setAdapter(ad);

    list.setOnItemClickListener((p, v, i, id) -> {
      Intent in = new Intent(this, BrowserActivity.class);
      in.putExtra("url", ad.getItem(i));
      startActivity(in);
    });

    clear.setOnClickListener(v -> {
      History.clear(this);
      ad.clear();
    });
  }
}