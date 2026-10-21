package com.gathin.tvhome;

import android.graphics.Color;

/**
 * The two looks the home screen can wear.
 *
 * <p>{@link #NIGHT} is the original: the picture fills the screen, type is white, the selection is a
 * wash of light. {@link #CREASE} is the CREASE scheme from the design reference — white acrylic,
 * black acrylic, scattered light, and red used only as a hairline.
 */
final class Theme {

    static final String NIGHT_ID = "night";
    static final String CREASE_ID = "crease";

    static final Theme NIGHT = new Theme(
            NIGHT_ID,
            /* paper */    0xFF000000,
            /* ink */      0xFFFFFFFF,
            /* inkRest */  0x52FFFFFF,
            /* index */    0x8AFFFFFF,
            /* indexRest */0x33FFFFFF,
            /* muted */    0xA6FFFFFF,
            /* faint */    0x8CFFFFFF,
            /* hairline */ 0x1FFFFFFF,
            /* wash */     0xFFFFFFFF,   // tint of the selection wash
            /* accent */   0xFFFFFFFF,   // marker: none in this theme, hidden per showMarker
            /* showMarker*/false,
            /* veil */      0);

    static final Theme CREASE = new Theme(
            CREASE_ID,
            /* paper */    0xFFFFFFFF,
            /* ink */      0xFF0A0A0C,
            /* inkRest */  0x8C0A0A0C,   // black held back: the row is present but quiet
            /* index */    0xFF0A0A0C,
            /* indexRest */0x590A0A0C,
            /* muted */    0xFF5C5A58,
            /* faint */    0xFF5C5A58,
            /* hairline */ 0xFFE8E8E8,
            /* wash */     0xFFF2F2F2,   // the selection is acrylic, not light
            /* accent */   0xFFD31218,   // the one red in the room
            /* showMarker*/true,
            /* veil */      176);   // white acrylic: the picture is still read, held back

    final String id;
    final int paper;
    final int ink;
    final int inkRest;
    final int index;
    final int indexRest;
    final int muted;
    final int faint;
    final int hairline;
    final int wash;
    final int accent;
    /** CREASE marks the selection with a red hairline; NIGHT lights it instead. */
    final boolean showMarker;
    /** How strongly the paper veils the picture, 0-255. CREASE keeps the photograph under acrylic. */
    final int veil;

    private Theme(String id, int paper, int ink, int inkRest, int index, int indexRest, int muted,
                  int faint, int hairline, int wash, int accent, boolean showMarker, int veil) {
        this.id = id;
        this.paper = paper;
        this.ink = ink;
        this.inkRest = inkRest;
        this.index = index;
        this.indexRest = indexRest;
        this.muted = muted;
        this.faint = faint;
        this.hairline = hairline;
        this.wash = wash;
        this.accent = accent;
        this.showMarker = showMarker;
        this.veil = veil;
    }

    static Theme of(String id) {
        return CREASE_ID.equals(id) ? CREASE : NIGHT;
    }

    /** Blends toward the paper, for a row that should sit further back without changing hue. */
    int fade(int color, float amount) {
        int a = Color.alpha(color);
        int blended = (int) (a * (1f - amount));
        return (blended << 24) | (color & 0x00FFFFFF);
    }
}
