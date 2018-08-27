package me.jlurena.ritscheduler.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import me.jlurena.ritscheduler.R;
import me.jlurena.ritscheduler.models.Settings;

/**
 * A singleton class that manages the Settings.
 */
public class SettingsManager {

    private static SettingsManager settingsManager;
    private Settings settings;

    public static SettingsManager getInstance(Context context) {
        if (settingsManager == null) {
            settingsManager = new SettingsManager();
            settingsManager.settings = new Settings();
            settingsManager.updateSettings(context);
        }
        return settingsManager;
    }

    public Settings getSettings() {
        return settings;
    }

    public void updateSettings(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean autoLimitTime = prefs.getBoolean(context.getString(R.string.auto_limit_time_key), false);
        int numVisibleDays = prefs.getInt(context.getString(R.string.num_visible_days_key), 3);
        String timeRange = prefs.getString(context.getString(R.string.hour_range_key), "0-1380");
        int firstVisibleDay = Integer.parseInt(prefs.getString(context.getString(R.string.first_visible_day_key), "0"));
        this.settings.setAutoLimitTime(autoLimitTime)
                .setNumberOfVisibleDays(numVisibleDays)
                .setTimeRange(timeRange)
                .setFirstVisibleDay(firstVisibleDay);
    }

}
