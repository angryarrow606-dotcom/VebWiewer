package com.MeawCreates.VebWiewer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class WaterView extends View {
  private final Paint bg = new Paint();
  private final Paint wave = new Paint(Paint.ANTI_ALIAS_FLAG);
  private final Path path = new Path();
  private final long start = System.currentTimeMillis();
  private static final int[] COLORS = {
    0x33134E6F, 0x330E3A5A, 0x44092A44, 0x55061B2E, 0x66030F1A
  };

  public WaterView(Context c, AttributeSet a) {
    super(c, a);
  }

  @Override protected void onSizeChanged(int w, int h, int ow, int oh) {
    bg.setShader(new LinearGradient(0, 0, 0, h, 0xFF061826, 0xFF010509,
        Shader.TileMode.CLAMP));
  }

  @Override protected void onDraw(Canvas c) {
    int w = getWidth();
    int h = getHeight();
    c.drawRect(0, 0, w, h, bg);
    float t = (System.currentTimeMillis() - start) / 1000f;

    for (int i = 0; i < COLORS.length; i++) {
      float base = h * (0.25f + 0.16f * i);
      float amp = 30f + 10f * i;
      float speed = (0.4f + 0.15f * i) * (i % 2 == 0 ? 1f : -1f);
      float freq = (float) (2 * Math.PI * (1.2f + 0.5f * i) / w);

      path.reset();
      path.moveTo(0, h);
      for (int x = 0; x <= w + 16; x += 16) {
        float y = base
            + amp * (float) Math.sin(x * freq + t * speed)
            + amp * 0.4f * (float) Math.sin(x * freq * 2.3f + t * speed * 1.7f);
        path.lineTo(x, y);
      }
      path.lineTo(w + 16, h);
      path.close();

      wave.setColor(COLORS[i]);
      c.drawPath(path, wave);
    }
    postInvalidateOnAnimation();
  }
}