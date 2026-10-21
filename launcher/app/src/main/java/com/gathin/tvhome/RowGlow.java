package com.gathin.tvhome;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.view.View;

/**
 * The light that sits behind the selected row: a soft warm band that starts at the left edge and
 * fades out before the middle of the screen. It is one small bitmap stretched, never a live blur,
 * so a 1 GB TV can afford it.
 */
final class RowGlow extends View {

    private static final int TEX_W = 256;
    private static final int TEX_H = 64;

    private final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
    private int tint = 0xFFFFFFFF;

    RowGlow(Context ctx) {
        super(ctx);
        setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        rebuild();
    }

    /** The glow takes its colour from the hour: warm at dusk, cool at midday. */
    void setTint(int color) {
        if (color == tint) return;
        tint = color;
        rebuild();
    }

    private void rebuild() {
        Bitmap bmp = Bitmap.createBitmap(TEX_W, TEX_H, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        // Horizontal falloff: strongest at the left, gone by three quarters of the width.
        int r = (tint >> 16) & 0xFF, g = (tint >> 8) & 0xFF, b = tint & 0xFF;
        int a0 = (int) (0.16f * ((tint >>> 24) & 0xFF));
        p.setShader(new LinearGradient(0, 0, TEX_W * 0.62f, 0,
                new int[]{(a0 << 24) | (r << 16) | (g << 8) | b, 0x00FFFFFF},
                new float[]{0f, 1f}, Shader.TileMode.CLAMP));
        c.drawRect(0, 0, TEX_W, TEX_H, p);

        // Vertical falloff: soft top and bottom so the band has no edges.
        p.setShader(null);
        p.setXfermode(new android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.DST_IN));
        p.setShader(new LinearGradient(0, 0, 0, TEX_H,
                new int[]{0x00FFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0x00FFFFFF},
                new float[]{0f, 0.22f, 0.78f, 1f}, Shader.TileMode.CLAMP));
        c.drawRect(0, 0, TEX_W, TEX_H, p);

        paint.setShader(new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
    }
}
