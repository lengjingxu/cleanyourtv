package com.gathin.tvhome;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * The list of looks, shown the same way from both places that can change one: the Settings menu on Home
 * and the Appearance row in Launcher settings. Each row carries a swatch of the theme's own paper, its
 * name, what it does, and a tick on the one in use, so the choice is visible instead of a blind toggle.
 *
 * <p>The rows are drawn in {@code chrome}, the look of the screen they sit on: Home passes its own
 * theme, while Launcher settings is a fixed dark screen and passes Night for legible type.
 */
final class ThemePicker {

    private ThemePicker() {}

    static void show(final Activity host, final Theme current, final Theme chrome, final Runnable onPicked) {
        final String[] ids = {Theme.NIGHT_ID, Theme.CREASE_ID};
        final String[] names = {host.getString(R.string.theme_night), host.getString(R.string.theme_crease)};
        final String[] hints = {host.getString(R.string.theme_night_hint), host.getString(R.string.theme_crease_hint)};
        final float density = host.getResources().getDisplayMetrics().density;

        LinearLayout list = new LinearLayout(host);
        list.setOrientation(LinearLayout.VERTICAL);
        int pad = Math.round(18 * density);
        list.setPadding(pad, Math.round(8 * density), pad, Math.round(14 * density));

        final AlertDialog dialog = new AlertDialog.Builder(host)
                .setTitle(R.string.appearance_title)
                .create();
        dialog.setView(list);

        for (int i = 0; i < ids.length; i++) {
            final String id = ids[i];
            list.addView(row(host, density, chrome, Theme.of(id), names[i], hints[i],
                    id.equals(current.id), () -> {
                        host.getSharedPreferences(Config.SETTINGS_NAME, Activity.MODE_PRIVATE).edit()
                                .putString("theme", id).apply();
                        dialog.dismiss();
                        onPicked.run();
                    }));
        }
        dialog.show();
    }

    private static View row(Activity host, float density, Theme chrome, Theme t, String name, String hint,
                            boolean selected, Runnable onPick) {
        LinearLayout row = new LinearLayout(host);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        int p = Math.round(14 * density);
        row.setPadding(p, p, p, p);
        row.setFocusable(true);
        row.setClickable(true);

        GradientDrawable focused = new GradientDrawable();
        focused.setColor((chrome.ink & 0x00FFFFFF) | 0x1A000000);
        focused.setCornerRadius(12 * density);
        GradientDrawable plain = new GradientDrawable();
        plain.setColor(0x00000000);
        plain.setCornerRadius(12 * density);
        StateListDrawable bg = new StateListDrawable();
        bg.addState(new int[]{android.R.attr.state_focused}, focused);
        bg.addState(new int[]{}, plain);
        row.setBackground(bg);

        // The swatch is the theme in miniature: its paper, a hairline, and its own accent.
        View swatch = new View(host);
        GradientDrawable sw = new GradientDrawable();
        sw.setShape(GradientDrawable.RECTANGLE);
        sw.setColor(t.paper);
        sw.setStroke(Math.max(1, Math.round(density)), t.showMarker ? t.accent : chrome.hairline);
        sw.setCornerRadius(10 * density);
        swatch.setBackground(sw);
        LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(
                Math.round(44 * density), Math.round(44 * density));
        slp.setMarginEnd(Math.round(16 * density));
        row.addView(swatch, slp);

        LinearLayout text = new LinearLayout(host);
        text.setOrientation(LinearLayout.VERTICAL);
        TextView title = new TextView(host);
        title.setText(name);
        title.setTextSize(18);
        title.setTextColor(chrome.ink);
        TextView sub = new TextView(host);
        sub.setText(hint);
        sub.setTextSize(12);
        sub.setTextColor(chrome.muted);
        text.addView(title);
        text.addView(sub);
        row.addView(text, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        if (selected) {
            TextView tick = new TextView(host);
            tick.setText("\u2713");
            tick.setTextSize(20);
            tick.setTextColor(chrome.accent);
            row.addView(tick);
        }
        row.setOnClickListener(v -> onPick.run());
        return row;
    }
}
