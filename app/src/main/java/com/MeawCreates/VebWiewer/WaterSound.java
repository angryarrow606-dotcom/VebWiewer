package com.MeawCreates.VebWiewer;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import java.util.Random;

public class WaterSound {
  static AudioTrack track;

  static boolean enabled(Context c) {
    return c.getSharedPreferences("s", 0).getBoolean("sound", true);
  }

  static void setEnabled(Context c, boolean on) {
    c.getSharedPreferences("s", 0).edit().putBoolean("sound", on).apply();
  }

  static void start(Context c) {
    if (track != null || !enabled(c)) return;
    try {
      final int rate = 22050;
      final int n = rate * 8;
      final int f = rate / 2;
      float[] g = new float[n + f];
      Random r = new Random(7);
      float a = 0, b = 0, peak = 0.0001f;

      for (int i = 0; i < g.length; i++) {
        float w = r.nextFloat() * 2f - 1f;
        a += 0.015f * (w - a);
        b += 0.12f * (w - b);
        float m1 = 0.65f + 0.35f * (float) Math.sin(2 * Math.PI * 3 * i / n);
        float m2 = 0.5f + 0.5f * (float) Math.sin(2 * Math.PI * 7 * i / n + 1);
        g[i] = a * 8f * m1 + (b - a) * 1.5f * m2;
        peak = Math.max(peak, Math.abs(g[i]));
      }

      short[] out = new short[n];
      float scale = 0.8f * 32767f / peak;
      for (int i = 0; i < n; i++) {
        float v = g[i];
        if (i < f) {
          float t = i / (float) f;
          v = g[i] * t + g[n + i] * (1f - t);
        }
        out[i] = (short) (v * scale);
      }

      track = new AudioTrack(AudioManager.STREAM_MUSIC, rate,
          AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
          n * 2, AudioTrack.MODE_STATIC);
      track.write(out, 0, n);
      track.setLoopPoints(0, n, -1);
      track.setVolume(0.4f);
      track.play();
    } catch (Exception e) {
      track = null;
    }
  }

  static void stop() {
    if (track != null) {
      try { track.stop(); } catch (Exception e) {}
      track.release();
      track = null;
    }
  }
}