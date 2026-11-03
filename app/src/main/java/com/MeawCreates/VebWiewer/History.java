package com.MeawCreates.VebWiewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;

public class History {
  static SharedPreferences p(Context c) {
    return c.getSharedPreferences("h", 0);
  }

  static List<String> get(Context c) {
    List<String> l = new ArrayList<>();
    for (String x : p(c).getString("l", "").split("\n"))
      if (!x.isEmpty()) l.add(x);
    return l;
  }

  static void add(Context c, String u) {
    List<String> l = get(c);
    l.remove(u);
    l.add(0, u);
    if (l.size() > 100) l = new ArrayList<>(l.subList(0, 100));
    p(c).edit().putString("l", TextUtils.join("\n", l)).apply();
  }

  static void clear(Context c) {
    p(c).edit().clear().apply();
  }
}