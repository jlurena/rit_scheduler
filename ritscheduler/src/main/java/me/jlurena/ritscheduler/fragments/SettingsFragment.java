package me.jlurena.ritscheduler.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v14.preference.PreferenceDialogFragment;
import android.support.v14.preference.PreferenceFragment;
import android.support.v7.preference.Preference;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.nightonke.boommenu.Util;

import me.jlurena.ritscheduler.R;
import me.jlurena.ritscheduler.models.Settings;
import me.jlurena.ritscheduler.widgets.NumberPreference;
import me.jlurena.ritscheduler.widgets.TimePreference;

/**
 * Settings fragment used to create settings_preference View.
 */
public class SettingsFragment extends PreferenceFragment {
    public static final String TAG = "PREFERENCE_FRAGMENT";

    public static Settings updateSettings(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean autoLimitTime = prefs.getBoolean(context.getString(R.string.auto_limit_time_key), false);
        int numVisibleDays = prefs.getInt(context.getString(R.string.num_visible_days_key), 3);
        String timeRange = prefs.getString(context.getString(R.string.hour_range_key), "0-1380");
        int firstVisibleDay = Integer.parseInt(prefs.getString(context.getString(R.string.first_visible_day_key), "0"));

        return Settings.getInstance()
                .setAutoLimitTime(autoLimitTime)
                .setNumberOfVisibleDays(numVisibleDays)
                .setTimeRange(timeRange)
                .setFirstVisibleDay(firstVisibleDay);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        updateSettings(getActivity());
        prefs.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> updateSettings(getActivity()));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
            layoutParams.height = Util.dp2px(400);
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.CENTER;
            view.setLayoutParams(layoutParams);
        }
        return view;
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {

        PreferenceDialogFragment dialogFragment = null;
        String key = preference.getKey();
        if (preference instanceof TimePreference) {
            dialogFragment = TimePreference.TimePreferenceDialogFragment.newInstance(key);
        } else if (preference instanceof NumberPreference) {
            dialogFragment = NumberPreference.NumberPreferenceDialogFragment.newInstance(preference.getKey());
        }

        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(getFragmentManager(), TAG);
        } else {
            super.onDisplayPreferenceDialog(preference);
        }

    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        String key = preference.getKey();

        if (key.equals(getString(R.string.about_key))) {
            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.about_title)
                    .setView(R.layout.preference_about_dialog)
                    .setNeutralButton(R.string.close, (d, which) -> d.dismiss()).show();
            TextView tv = dialog.findViewById(R.id.developer_name);
            tv.setMovementMethod(LinkMovementMethod.getInstance());
            tv = dialog.findViewById(R.id.github_url);
            tv.setMovementMethod(LinkMovementMethod.getInstance());
            tv = dialog.findViewById(R.id.app_version);
            String version;
            try {
                version = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                version = "Latest";
            }
            tv.setText(version);
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }
}
