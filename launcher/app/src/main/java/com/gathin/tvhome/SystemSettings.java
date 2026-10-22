package com.gathin.tvhome;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * An intent for the TV's own settings.
 *
 * <p>Most firmware answers the standard {@code android.settings.*} actions. Some do not: TCL's
 * China-market firmware ships {@code com.tcl.settings} but resolves nothing for SETTINGS or
 * WIFI_SETTINGS (checked on a 鹤7 85", Android 11), and China-market MIUI TV does the same with its
 * own settings app. There the vendor's entry activity is used instead, so the Settings and Wi-Fi
 * pills keep working; on firmware with a standard handler nothing changes.
 */
final class SystemSettings {

    /** Vendor settings apps, in the order they are tried, with their settings entry activity. */
    private static final String[][] VENDOR_APPS = {
            {"com.tcl.settings", "com.tcl.settings.ui.MainActivity"},
            {"com.xiaomi.mitv.settings", "com.xiaomi.mitv.settings.entry.MainActivity"},
    };

    private SystemSettings() {}

    static Intent intent(Context context, String standardAction) {
        Intent standard = new Intent(standardAction);
        if (standard.resolveActivity(context.getPackageManager()) != null) return standard;
        for (String[] app : VENDOR_APPS) {
            Intent vendor = new Intent().setComponent(new ComponentName(app[0], app[1]));
            if (vendor.resolveActivity(context.getPackageManager()) != null) return vendor;
        }
        return standard;   // nothing found: the caller shows the can't-open message
    }
}
