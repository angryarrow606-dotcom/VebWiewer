package com.MeawCreates.VebWiewer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.view.View;

public class EdgeLoader extends View {
  private final Paint line = new Paint(Paint.ANTI_ALIAS_FLAG);
  private final Paint dot = new Paint(Paint.ANTI_ALIAS_FLAG);
  private final Path ring = new Path();
  private final Path seg = new Path();
  private final PathMeasure pm = new PathMeasure();
  private final float[] pos = new float[2];
  private final float dp;
  private float len, target, shown, alpha;
  private boolean running, finishing;

  private static final float[] W = {14f, 9f, 5f, 2.5f};
  private static final int[] A = {0x22, 0x44, 0x88, 0xFF};

  public EdgeLoader(Context c) {
    super(c);
    dp = c.getResources().getDisplayMetrics().density;
    line.setStyle(Paint.Style.STROKE);
    line.setStrokeCap(Paint.Cap.ROUND);
  }

  void start() {
    if (running) return;
    running = true;
    finishing = false;
    target = 0.1f;
    shown = 0f;
    alpha = 1f;
    invalidate();
  }

  void setProgress(int pct) {
    if (!running) {
      if (pct >= 100) return;
      start();
    }
    target = Math.max(target, pct / 100f);
    if (pct >= 100) target = 1f;
  }

  void finish() {
    if (running) target = 1f;
  }

  @Override protected void onSizeChanged(int w, int h, int ow, int oh) {
    float inset = 3 * dp;
    float rad = 28 * dp;
    RectF r = new RectF(inset, inset, w - inset, h - inset);
    ring.reset();
    ring.addRoundRect(r, rad, rad, Path.Direction.CW);
    pm.setPath(ring, false);
    len = pm.getLength();
  }

  private void drawSeg(Canvas c, float from, float to, float mul) {
    if (to <= from) return;
    seg.reset();
    pm.getSegment(from, to, seg, true);
    for (int i = 0; i < 4; i++) {
      line.setStrokeWidth(W[i] * dp);
      int a = (int) (A[i] * mul * alpha);
      line.setColor((a << 24) | (i == 3 ? 0xE0F7FF : 0x4FC3F7));
      c.drawPath(seg, line);
    }
  }

  @Override protected void onDraw(Canvas c) {
    if ((!running && !finishing) || len == 0) return;

    if (running) {
      if (target < 1f && shown < 0.9f) shown += 0.0012f;
      shown += (target - shown) * 0.06f;
      if (target >= 1f && shown > 0.995f) {
        shown = 1f;
        running = false;
        finishing = true;
        alpha = 1f;
      } else if (target < 1f && shown > 0.96f) {
        shown = 0.96f;
      }
    } else {
      alpha -= 0.03f;
      if (alpha <= 0f) {
        finishing = false;
        return;
      }
    }

    if (finishing) {
      drawSeg(c, 0, len, 1f);
    } else {
      float d = shown * len;
      float tail = len * 0.35f;
      for (int i = 0; i < 4; i++) {
        float a0 = Math.max(0, d - tail * (4 - i) / 4f);
        float a1 = Math.max(0, d - tail * (3 - i) / 4f);
        drawSeg(c, a0, a1, (i + 1) / 4f);
      }
      pm.getPosTan(d, pos, null);
      dot.setColor(0x444FC3F7);
      c.drawCircle(pos[0], pos[1], 12 * dp, dot);
      dot.setColor(0xFFFFFFFF);
      c.drawCircle(pos[0], pos[1], 4 * dp, dot);
    }
    postInvalidateOnAnimation();
  }
}