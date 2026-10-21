package com.gathin.tvhome;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/**
 * The wash behind the selected row.
 *
 * <p>Full width, and deliberately not scaled with the type: a gradient that grew with the row would
 * have its transparent end pushed past the screen edge, and the selection would read as one pale
 * block instead of light falling away to the right. It therefore stops at 46% of the width, which
 * is still on screen after the row has grown.
 */
final class RowWash extends View {

    // The row is drawn from the left edge and may be scaled by the selection, so the gradient has
    // to be finished well inside the screen: at 1.5x the visible part is the left two thirds, and
    // a falloff that only ended at the far edge would look like a flat block.
    private static final float[] STOPS = {0f, 0.10f, 0.42f};

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int tint = 0xFFFFFFFF;

    RowWash(Context ctx) {
        this(ctx, null);
    }

    /** Inflated from item_app.xml, so the AttributeSet constructor is the one that actually runs. */
    RowWash(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
    }

    /** The wash takes the hour's colour, so the selection feels lit rather than painted. */
    void setTint(int color) {
        if (color == tint) return;
        tint = color;
        rebuild();
    }

    private void rebuild() {
        int w = getWidth();
        if (w <= 0) return;
        int r = (tint >> 16) & 0xFF, g = (tint >> 8) & 0xFF, b = tint & 0xFF;
        int[] colors = {
                (0x44 << 24) | (r << 16) | (g << 8) | b,
                (0x1E << 24) | (r << 16) | (g << 8) | b,
                0x00FFFFFF
        };
        paint.setShader(new LinearGradient(0, 0, w, 0, colors, STOPS, Shader.TileMode.CLAMP));
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        rebuild();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
    }
}
